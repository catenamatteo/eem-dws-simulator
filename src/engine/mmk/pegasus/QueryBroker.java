package engine.mmk.pegasus;

import java.util.concurrent.TimeUnit;

import com.google.common.math.Quantiles;
import com.google.common.math.Quantiles.ScaleAndIndex;

import cpu.CPUBuilder;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import it.unimi.dsi.fastutil.longs.Long2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleSortedMap;

public class QueryBroker extends engine.mmk.QueryBroker {

	protected Long2DoubleSortedMap mv95thtile;
//	protected LongList mv95thtileKeys;
//	protected DoubleList mv95thtileValues;	
	protected ScaleAndIndex index;
	protected final long mv95thtileWindow;
	protected Time slo;
	protected Time waitUntil;
	
	
	public QueryBroker(Simulator simulator, CPUBuilder cpuBuilder, Time slo, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuBuilder, numOfReplicas, shards);
//		mv95thtileKeys = new LongArrayList();
//		mv95thtileValues = new DoubleArrayList();
		mv95thtileWindow = TimeUnit.SECONDS.toMicros(30);
		this.slo = slo;
		waitUntil = new Time(0, TimeUnit.MINUTES);
		mv95thtile = new Long2DoubleRBTreeMap();
		index = Quantiles.percentiles().index(95);
	}

	public void receiveResults(long uid, long completionTime) {

		long now = simulator.now().getTimeMicroseconds();
		
		mv95thtile.put(now, completionTime);
		Long2DoubleSortedMap tailMap = mv95thtile.tailMap(now - mv95thtileWindow);
		if (mv95thtile.size() != tailMap.size())
			mv95thtile = new Long2DoubleRBTreeMap(tailMap);
		
//		mv95thtileKeys.add(now);
//		mv95thtileValues.add(completionTime);
//		int cnt = 0;
//		for (int i = 0; i < mv95thtileKeys.size(); i++) {
//			
//			if (mv95thtileKeys.getLong(i) < now - mv95thtileWindow) {
//				
//				cnt++;
//				
//			} else {
//				
//				break;
//			}
//				
//		}
//		if (cnt > 0) {
//			
//			for (int i = 0; i < cnt; i++) {
//				
//				mv95thtileKeys.removeLong(0);
//				mv95thtileValues.removeDouble(0);
//				
//			}
//		}
		
		//rule engine
		if (simulator.now().compareTo(waitUntil) >= 0) {

			
			
			double mv95thtileDouble = index.computeInPlace(mv95thtile.values().toDoubleArray());
//			double mvAvgDouble = percent;
//			if (!mvAvgValues.isEmpty()) {
//				for (long c : mvAvgValues)
//					mvAvgDouble += c;
//				mvAvgDouble /= mvAvgValues.size();
//			}

			if (mv95thtileDouble > slo.getTimeMicroseconds()) {

				waitUntil = new Time(simulator.now().getTimeMicroseconds() + TimeUnit.MINUTES.toMicros(5),
						TimeUnit.MICROSECONDS);
				
				setMaxCPUPowerCap();
				

			} else if (completionTime > 1.35 * slo.getTimeMicroseconds()) {
				
				setMaxCPUPowerCap();
				
			} else if (completionTime > slo.getTimeMicroseconds()) {
				
				multiplyCPUPowerBy(1.07);
				
			} else if (completionTime <= slo.getTimeMicroseconds() && completionTime >= 0.85 * slo.getTimeMicroseconds()) {
				
				//do nothing
				
			} else if (completionTime < 0.85 * slo.getTimeMicroseconds()) {
				
				multiplyCPUPowerBy(0.99);
				
			} else if (completionTime < 0.60 * slo.getTimeMicroseconds()) {
				
				multiplyCPUPowerBy(0.97);
			}
		}

		super.receiveResults(uid, completionTime);
	}

	private void multiplyCPUPowerBy(double d) {
		
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
