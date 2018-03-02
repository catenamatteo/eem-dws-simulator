package engine.mmk.pegasus;

import java.util.concurrent.TimeUnit;

import cpu.CPUBuilder;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import util.MovingStatistics;

public class QueryBroker extends engine.mmk.QueryBroker {

	protected MovingStatistics mv95thtile;
	protected long sloMicros;
	protected Time waitUntil;
	
	
	public QueryBroker(Simulator simulator, CPUBuilder cpuBuilder, Time slo, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuBuilder, numOfReplicas, shards);

		sloMicros = slo.getTimeMicroseconds();
		waitUntil = new Time(0, TimeUnit.MINUTES);
		mv95thtile = new MovingStatistics(simulator, 30, TimeUnit.SECONDS);
	}

	public void receiveResults(long uid, long completionTime) {
		
		
		mv95thtile.add(completionTime);
		
		//rule engine
		if (simulator.now().compareTo(waitUntil) >= 0) {

			double mv95thtileMicros = mv95thtile.getPercentile(95);

			if (mv95thtileMicros > sloMicros) {

				waitUntil = new Time(simulator.now().getTimeMicroseconds() + TimeUnit.MINUTES.toMicros(5),
						TimeUnit.MICROSECONDS);
				
				setMaxCPUPowerCap();
				

			} else if (completionTime > 1.35 * sloMicros) {
				
				setMaxCPUPowerCap();
				
			} else if (completionTime > sloMicros) {
				
				multiplyCPUPowerCapBy(1.07);
				
			} else if (completionTime <= sloMicros && completionTime >= 0.85 * sloMicros) {
				
				//do nothing
				
			} else if (completionTime < 0.85 * sloMicros) {
				
				multiplyCPUPowerCapBy(0.99);
				
			} else if (completionTime < 0.60 * sloMicros) {
				
				multiplyCPUPowerCapBy(0.97);
			}
		}

		super.receiveResults(uid, completionTime);
	}

	private void multiplyCPUPowerCapBy(double d) {
		
		for (IndexReplica r : replicas) 
			((engine.mmk.pegasus.IndexReplica)r).multiplyCPUPowerCapBy(d, simulator.now().getTimeMicroseconds());		
	}

	protected void setMaxCPUPowerCap() {
		for (IndexReplica r : replicas) 
			((engine.mmk.pegasus.IndexReplica)r).setMaxCPUPowerCap(simulator.now().getTimeMicroseconds());
	}
	
	@Override
	protected IndexReplica newReplicaManagerInstance(CPUBuilder cpuBuilder, int id, Shard... shards) {
		
		return new engine.mmk.pegasus.IndexReplica(this, cpuBuilder, id, shards);
	}
}
