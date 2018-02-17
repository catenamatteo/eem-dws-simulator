package engine;

import cpu.CPU;
import cpu.CPUBuilder;
import cpu.Core;
import eu.nicecode.simulator.Simulator;

public abstract class ShardServer {

	protected Shard shard;
	protected IndexReplica replicaManager;
	protected QueryMatcher[] matcher;
	protected CPU cpu;
	protected int id;
	
	public ShardServer(IndexReplica replicaManager, Shard shard, CPUBuilder cpuBuilder, int id) {

		this.shard = shard;
		this.replicaManager = replicaManager;
		
		this.cpu = cpuBuilder.newInstance(this);
		matcher = new QueryMatcher[cpu.getNumCores()];
		for (int i = 0; i < cpu.getNumCores(); i++)
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

	public IndexReplica getIndexReplica() {
		
		return replicaManager;
	}

	public int getId() {
	
		return id;
	}

	public abstract int getLoad();

	public void shutdown(long timeMicroseconds) {

		cpu.shutdown(timeMicroseconds);
		
	}
	
}
