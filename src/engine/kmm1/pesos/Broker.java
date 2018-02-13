package engine.kmm1.pesos;

import cpu.CPUModel;
import engine.ReplicaManager;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public class Broker extends engine.kmm1.Broker {

	protected Time timeBudget;
	protected Object2ObjectMap<Shard, QueryEfficiencyPredictors> qepMap;

	public Broker(Simulator simulator, CPUModel cpuModel, Time timeBudget, int numOfReplicas, Shard... shards) {
		super(simulator, cpuModel, numOfReplicas, shards);
		
		qepMap = new Object2ObjectArrayMap<>(shards.length);
		for (Shard s : shards) {
			
			QueryEfficiencyPredictors qep = new QueryEfficiencyPredictors((cpu.regression.CPUModel)cpuModel);
			qepMap.put(s, qep);
			
		}
		
		this.timeBudget = timeBudget;
	}

		
	public Time getTimeBudget() {
		return timeBudget;
	}
	
	@Override
	protected ReplicaManager newReplicaManagerInstance(CPUModel cpuModel, Shard... shards) {
		
		return new engine.kmm1.pesos.ReplicaManager(this, cpuModel, shards);
	}
	
	public QueryEfficiencyPredictors getQueryEfficiencyPredictors(Shard shard) {

		return qepMap.get(shard);
			
	}
	
	

}
