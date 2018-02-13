package engine.kmm1.pesos;

import cpu.CPUModel;
import engine.QueryBroker;
import engine.Shard;
import engine.ShardServer;

public class IndexReplica extends engine.kmm1.IndexReplica {

	public IndexReplica(QueryBroker broker, CPUModel cpuModel, Shard... shards) {
		super(broker, cpuModel, shards);
	}

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUModel cpuModel, int id) {

		return new engine.kmm1.pesos.ShardServer(this, shard, cpuModel, id);
	}
	
	

}
