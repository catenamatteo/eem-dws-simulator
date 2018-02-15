package engine.kmm1.pesos;

import cpu.CPUBuilder;
import cpu.Core;
import engine.IndexReplica;
import engine.QueryMatcher;
import engine.Shard;

public class ShardServer extends engine.kmm1.ShardServer {


	public ShardServer(IndexReplica replicaManager, Shard shard, CPUBuilder cpuBuilder, int id) {
		super(replicaManager, shard, cpuBuilder, id);

	}

	@Override
	protected QueryMatcher newQueryMatcherInstance(Core core) {
		
		return new engine.kmm1.pesos.QueryMatcher(this, core);
	}
	
	

}
