package engine.mmk;

import cpu.CPUBuilder;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;

public class QueryBroker extends engine.QueryBroker implements Agent {
	
	public QueryBroker(Simulator simulator, CPUBuilder cpuBuilder, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuBuilder, numOfReplicas, shards);
	}	
	

	@Override
	protected IndexReplica newReplicaManagerInstance(CPUBuilder cpuBuilder, int id, Shard... shards) {
		
		return new engine.mmk.IndexReplica(this, cpuBuilder, id, shards);
	}
}
