package engine;

import cpu.CPU;
import cpu.CPUModel;
import cpu.Core;
import eu.nicecode.simulator.Simulator;

public abstract class ShardServer {

	protected Shard shard;
	protected ReplicaManager replicaManager;
	protected QueryMatcher[] matcher;
	protected CPU cpu;
	private int id;
	
	public ShardServer(ReplicaManager replicaManager, Shard shard, CPUModel cpuModel, int id) {

		this.shard = shard;
		this.replicaManager = replicaManager;
		
		this.cpu = cpuModel.getNewInstance();
		matcher = new QueryMatcher[cpuModel.getNumCores()];
		for (int i = 0; i < cpuModel.getNumCores(); i++)
			matcher[i] = newQueryMatcherInstance(cpu.getCore(i));
		this.id = id;
	}
	
	protected abstract QueryMatcher newQueryMatcherInstance(Core core);

	public void receiveResults(long uid, long completionTime) {
		
		replicaManager.receiveResults(uid, completionTime);
	}

	public Shard getShard() {

		return shard;
	}
	
	public Simulator getSimulator() {

		return replicaManager.getSimulator();
	}

	public ReplicaManager getReplicaManager() {
		
		return replicaManager;
	}

	public int getId() {
	
		return id;
	}

	public abstract int getLoad();
	
}
