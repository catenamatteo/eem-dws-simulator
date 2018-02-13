package engine.kmm1.pesos;

import cpu.CPUModel;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;

public class Broker extends engine.kmm1.Broker {

	protected Time timeBudget;
	protected QueryEfficiencyPredictors qep;

	public Broker(Simulator simulator, CPUModel cpuModel, Time timeBudget, int numOfReplicas, Shard... shards) {
		super(simulator, cpuModel, numOfReplicas, shards);
		qep = new QueryEfficiencyPredictors(cpuModel.getFrequencies());		
		this.timeBudget = timeBudget;
	}

		
	public Time getTimeBudget() {
		return timeBudget;
	}
	
	@Override
	protected IndexReplica newReplicaManagerInstance(CPUModel cpuModel, Shard... shards) {
		
		return new engine.kmm1.pesos.IndexReplica(this, cpuModel, shards);
	}
	
	public QueryEfficiencyPredictors getQueryEfficiencyPredictors() {

		return qep;
			
	}
	
	

}
