package engine.kmm1.pesos;

import cpu.CPUBuilder;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;

public class QueryBroker extends engine.kmm1.QueryBroker {

	protected Time timeBudget;
	protected QueryEfficiencyPredictors qep;

	public QueryBroker(Simulator simulator, CPUBuilder cpuBuilder, Time timeBudget, int numOfReplicas, Shard... shards) {
		super(simulator, cpuBuilder, numOfReplicas, shards);
		qep = new QueryEfficiencyPredictors(cpuBuilder.getFrequencies());		
		this.timeBudget = timeBudget;
	}

		
	public Time getTimeBudget() {
		return timeBudget;
	}
	
	@Override
	protected IndexReplica newReplicaManagerInstance(CPUBuilder cpuBuilder, int id, Shard... shards) {
		
		return new engine.kmm1.pesos.IndexReplica(this, cpuBuilder, id, shards);
	}
	
	public QueryEfficiencyPredictors getQueryEfficiencyPredictors() {

		return qep;
			
	}
	
	

}
