package engine.mmk.pegasus;

import cpu.CPUModel;
import engine.Broker;
import engine.Shard;
import engine.ShardServer;

public class ReplicaManager extends engine.mmk.ReplicaManager {

	public ReplicaManager(Broker broker, CPUModel cpuModel, Shard[] shards) {
		super(broker, cpuModel, shards);
	}

	public void setMaxCPUPower(long timeMicroseconds) {

		for (ShardServer s : servers)
			((engine.mmk.pegasus.ShardServer)s).setMaxCPUPower(timeMicroseconds);
		
	}
	

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUModel cpuModel, int id) {
		
		return new engine.mmk.pegasus.ShardServer(this, shard, cpuModel, id);
	}

	public void changeCPUPower(double d, long timeMicroseconds) {
		
		for (ShardServer s : servers)
			((engine.mmk.pegasus.ShardServer)s).changeCPUPower(d, timeMicroseconds);		
	}

}
