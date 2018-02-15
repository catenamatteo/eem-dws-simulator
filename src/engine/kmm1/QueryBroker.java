package engine.kmm1;

import cpu.CPUBuilder;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;

public class QueryBroker extends engine.QueryBroker {

	public QueryBroker(Simulator simulator, CPUBuilder cpuBuilder, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuBuilder, numOfReplicas, shards);
	}

	@Override
	protected IndexReplica newReplicaManagerInstance(CPUBuilder cpuBuilder, int id, Shard... shards) {
		
		return new engine.kmm1.IndexReplica(this, cpuBuilder, id, shards);
	}
}
