package engine.kmm1.pesos;

import cpu.CPUModel;
import engine.Broker;
import engine.Shard;
import engine.ShardServer;

public class ReplicaManager extends engine.kmm1.ReplicaManager {

	public ReplicaManager(Broker broker, CPUModel cpuModel, Shard... shards) {
		super(broker, cpuModel, shards);
	}

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUModel cpuModel, int id) {

		return new engine.kmm1.pesos.ShardServer(this, shard, cpuModel, id);
	}
	
	

}
