package engine.mmk;

import cpu.CPUModel;
import engine.ReplicaManager;
import engine.Shard;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;

public class Broker extends engine.Broker implements Agent {
	
	public Broker(Simulator simulator, CPUModel cpuModel, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuModel, numOfReplicas, shards);
	}	
	

	@Override
	protected ReplicaManager newReplicaManagerInstance(CPUModel cpuModel, Shard... shards) {
		
		return new engine.mmk.ReplicaManager(this, cpuModel, shards);
	}
}
