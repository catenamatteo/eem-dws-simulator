package engine.kmm1.pesos;

import cpu.CPUBuilder;
import engine.QueryBroker;
import engine.Shard;
import engine.ShardServer;

public class IndexReplica extends engine.kmm1.IndexReplica {

	public IndexReplica(QueryBroker broker, CPUBuilder cpuBuilder, int id, Shard... shards) {
		super(broker, cpuBuilder, id, shards);
	}

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUBuilder cpuBuilder, int id) {

		return new engine.kmm1.pesos.ShardServer(this, shard, cpuBuilder, id);
	}
	
	

}
