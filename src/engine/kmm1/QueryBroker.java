package engine.kmm1;

import cpu.CPUModel;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Simulator;

public class QueryBroker extends engine.QueryBroker {

	public QueryBroker(Simulator simulator, CPUModel cpuModel, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuModel, numOfReplicas, shards);
	}

	@Override
	protected IndexReplica newReplicaManagerInstance(CPUModel cpuModel, Shard... shards) {
		
		return new engine.kmm1.IndexReplica(this, cpuModel, shards);
	}
}
