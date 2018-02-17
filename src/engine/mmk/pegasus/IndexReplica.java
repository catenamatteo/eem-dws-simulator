package engine.mmk.pegasus;

import cpu.CPUBuilder;
import engine.QueryBroker;
import engine.Shard;
import engine.ShardServer;

public class IndexReplica extends engine.mmk.IndexReplica {

	public IndexReplica(QueryBroker broker, CPUBuilder cpuBuilder, int id, Shard... shards) {
		super(broker, cpuBuilder, id, shards);
	}

	public void setMaxCPUPowerCap(long timeMicroseconds) {

		for (ShardServer s : servers)
			((engine.mmk.pegasus.ShardServer)s).setMaxCPUPowerCap(timeMicroseconds);
		
	}
	

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUBuilder cpuBuilder, int id) {
		
		return new engine.mmk.pegasus.ShardServer(this, shard, cpuBuilder, id);
	}

	public void multiplyCPUPowerCapBy(double d, long timeMicroseconds) {
		
		for (ShardServer s : servers)
			((engine.mmk.pegasus.ShardServer)s).multiplyCPUPowerCapBy(d, timeMicroseconds);		
	}

}
