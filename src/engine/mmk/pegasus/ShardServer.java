package engine.mmk.pegasus;

import cpu.CPUModel;
import engine.ReplicaManager;
import engine.Shard;

public class ShardServer extends engine.mmk.ShardServer {

	public ShardServer(ReplicaManager replicaManager, Shard shard, CPUModel cpuModel, int id) {
		super(replicaManager, shard, cpuModel, id);
		// TODO Auto-generated constructor stub
	}

	public void setMaxCPUPower(long timeMicroseconds) {

		cpu.setMaxPower(timeMicroseconds);
	}

	public void changeCPUPower(double d, long timeMicroseconds) {

		cpu.changePower(d, timeMicroseconds);
	}

}
