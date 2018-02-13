package engine.mmk;

import java.util.LinkedList;
import java.util.Queue;

import cpu.CPUModel;
import engine.Broker;
import engine.Shard;
import engine.ShardServer;
import query.Query;

public class ReplicaManager extends engine.ReplicaManager {

	protected Queue<Query>[] queues;
	
	@SuppressWarnings("unchecked")
	public ReplicaManager(Broker broker, CPUModel cpuModel, Shard[] shards) {
		super(broker, cpuModel, shards);
		queues = new Queue[shards.length];
		for (int i = 0; i < shards.length; i++) {
			queues[i] = new LinkedList<>();
		}
	}

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUModel cpuModel, int id) {
		
		return new engine.mmk.ShardServer(this, shard, cpuModel, id);
	}
	
	public Query getNextQuery(int shardServerId) {
		
		return queues[shardServerId].poll(); 

	}


	public void receiveQuery(Query query) {

		for (int i = 0; i < servers.length; i++) {
			
			engine.mmk.ShardServer s1 = (engine.mmk.ShardServer) servers[i];
			if (s1.hasIdlingResources()) {
				s1.receiveQuery((Query) query);
			} else {
				queues[i].offer(query);
			}			
		}
	}

	boolean hasIdlingResources() {

		for (ShardServer s : servers) {
			
			engine.mmk.ShardServer s1 = (engine.mmk.ShardServer) s;
			if (s1.hasIdlingResources()) return true;
			
		}
		return false;	
		
	}

	@Override
	public int getLoad() {
	
		int cnt = 0;
		for (int i = 0; i < servers.length; i++) {
			
			cnt += queues[i].size();
			cnt += servers[i].getLoad();
		}
		return cnt;
	}


}
