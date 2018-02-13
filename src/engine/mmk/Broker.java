package engine.mmk;

import cpu.CPUModel;
import engine.IndexReplica;
import engine.Shard;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;

public class Broker extends engine.QueryBroker implements Agent {
	
	public Broker(Simulator simulator, CPUModel cpuModel, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuModel, numOfReplicas, shards);
	}	
	

	@Override
	protected IndexReplica newReplicaManagerInstance(CPUModel cpuModel, Shard... shards) {
		
		return new engine.mmk.IndexReplica(this, cpuModel, shards);
	}
}