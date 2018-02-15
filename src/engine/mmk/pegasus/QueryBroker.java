package engine.mmk.pegasus;

import java.util.concurrent.TimeUnit;

import com.google.common.math.Quantiles;

import cpu.CPUBuilder;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public class QueryBroker extends engine.mmk.QueryBroker {

	protected LongList mv95thtileKeys;
	protected LongList mv95thtileValues;	

	protected final long mv95thtileWindow;
	protected Time slo;
	protected Time waitUntil;
	
	
	public QueryBroker(Simulator simulator, CPUBuilder cpuBuilder, Time slo, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuBuilder, numOfReplicas, shards);
		mv95thtileKeys = new LongArrayList();
		mv95thtileValues = new LongArrayList();
		mv95thtileWindow = TimeUnit.SECONDS.toMicros(30);
		this.slo = slo;
		waitUntil = new Time(0, TimeUnit.MINUTES);		
	}

	public void receiveResults(long uid, long completionTime) {

		long now = simulator.now().getTimeMicroseconds();
		
		mv95thtileKeys.add(now);
		mv95thtileValues.add(completionTime);
		int cnt = 0;
		for (int i = 0; i < mv95thtileKeys.size(); i++) {
			
			if (mv95thtileKeys.getLong(i) < now - mv95thtileWindow) {
				
				cnt++;
				
			} else {
				
				break;
			}
				
		}
		if (cnt > 0) {
			
			for (int i = 0; i < cnt; i++) {
				
				mv95thtileKeys.removeLong(0);
				mv95thtileValues.removeLong(0);
				
			}
		}
		
		//rule engine
		if (simulator.now().compareTo(waitUntil) >= 0) {

			double mv95thtileDouble = Quantiles.percentiles().index(95).compute(mv95thtileValues);
//			double mvAvgDouble = percent;
//			if (!mvAvgValues.isEmpty()) {
//				for (long c : mvAvgValues)
//					mvAvgDouble += c;
//				mvAvgDouble /= mvAvgValues.size();
//			}

			if (mv95thtileDouble > slo.getTimeMicroseconds()) {

				waitUntil = new Time(simulator.now().getTimeMicroseconds() + TimeUnit.MINUTES.toMicros(1),
						TimeUnit.MICROSECONDS);
				
				setMaxCPUPower();
				

			} else if (completionTime > 1.35 * slo.getTimeMicroseconds()) {
				
				setMaxCPUPower();
				
			} else if (completionTime > slo.getTimeMicroseconds()) {
				
				changeCPUPower(1.07);
				
			} else if (completionTime <= slo.getTimeMicroseconds() && completionTime >= 0.85 * slo.getTimeMicroseconds()) {
				
				//do nothing
				
			} else if (completionTime < 0.85 * slo.getTimeMicroseconds()) {
				
				changeCPUPower(0.99);
				
			} else if (completionTime < 0.60 * slo.getTimeMicroseconds()) {
				
				changeCPUPower(0.97);
			}
		}

		super.receiveResults(uid, completionTime);
	}

	private void changeCPUPower(double d) {
		
		for (IndexReplica r : replicas) 
			((engine.mmk.pegasus.IndexReplica)r).changeCPUPower(d, simulator.now().getTimeMicroseconds());		
	}

	protected void setMaxCPUPower() {
		for (IndexReplica r : replicas) 
			((engine.mmk.pegasus.IndexReplica)r).setMaxCPUPower(simulator.now().getTimeMicroseconds());
	}
	
	@Override
	protected IndexReplica newReplicaManagerInstance(CPUBuilder cpuBuilder, int id, Shard... shards) {
		
		return new engine.mmk.pegasus.IndexReplica(this, cpuBuilder, id, shards);
	}
}
