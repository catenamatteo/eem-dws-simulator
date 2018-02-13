package engine.kmm1;

import cpu.CPUModel;
import engine.ReplicaManager;
import engine.Shard;
import eu.nicecode.simulator.Simulator;

public class Broker extends engine.Broker {

	public Broker(Simulator simulator, CPUModel cpuModel, int numOfReplicas, Shard[] shards) {
		super(simulator, cpuModel, numOfReplicas, shards);
	}

	@Override
	protected ReplicaManager newReplicaManagerInstance(CPUModel cpuModel, Shard... shards) {
		
		return new engine.kmm1.ReplicaManager(this, cpuModel, shards);
	}
}
