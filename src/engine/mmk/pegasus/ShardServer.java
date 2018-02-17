package engine.mmk.pegasus;

import cpu.CPUBuilder;
import cpu.RAPL;
import engine.IndexReplica;
import engine.Shard;

public class ShardServer extends engine.mmk.ShardServer {

	public ShardServer(IndexReplica replicaManager, Shard shard, CPUBuilder cpuModel, int id) {
		super(replicaManager, shard, cpuModel, id);
		// TODO Auto-generated constructor stub
	}

	public void setMaxCPUPowerCap(long timeMicroseconds) {

		((RAPL)cpu).setMaxPowerCap(timeMicroseconds);
	}

	public void multiplyCPUPowerCapBy(double d, long timeMicroseconds) {

		((RAPL)cpu).setPowerCap(d * ((RAPL)cpu).getPowerCap(), timeMicroseconds);
	}

}
