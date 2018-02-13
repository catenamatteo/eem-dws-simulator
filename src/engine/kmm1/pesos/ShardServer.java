package engine.kmm1.pesos;

import cpu.CPUModel;
import cpu.Core;
import engine.QueryMatcher;
import engine.IndexReplica;
import engine.Shard;

public class ShardServer extends engine.kmm1.ShardServer {


	public ShardServer(IndexReplica replicaManager, Shard shard, CPUModel cpuModel, int id) {
		super(replicaManager, shard, cpuModel, id);

	}

	@Override
	protected QueryMatcher newQueryMatcherInstance(Core core) {
		
		return new engine.kmm1.pesos.QueryMatcher(this, core);
	}
	
	

}
