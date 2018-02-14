package engine;

import java.util.Collections;

import cpu.CPUModel;
import eu.nicecode.simulator.Simulator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import query.Query;

public abstract class IndexReplica {

	protected QueryBroker broker;
	protected ShardServer[] servers;

	protected Long2ObjectMap<LongList> times;
	private int id;

	public IndexReplica(QueryBroker broker, CPUModel cpuModel, int id, Shard... shards) {
		
		this.broker = broker;
		this.id = id;
		servers = new ShardServer[shards.length];
		for (int i = 0; i < shards.length; i++) {
			servers[i] = newShardServerInstance(shards[i], cpuModel, i);
		}
		times = new Long2ObjectOpenHashMap<>();
	}
	
	public int getId() {
		
		return id;
		
	}

	protected abstract ShardServer newShardServerInstance(Shard shard, CPUModel cpuModel, int id);
	public abstract void receiveQuery(Query query);
	
	public void receiveResults(long uid, long completionTime) {
		
		LongList timesList = null;
		if (!times.containsKey(uid)) {
			
			timesList = new LongArrayList(servers.length);
			times.put(uid, timesList);			
		}
		times.get(uid).add(completionTime);
		
		if (times.get(uid).size() == servers.length) {
			
			long actualCompletionTime = Collections.max(times.get(uid));
			broker.receiveResults(uid, actualCompletionTime);
			times.remove(uid);
		}		
	}


	public Simulator getSimulator() {
		
		return broker.getSimulator();
	}

	public QueryBroker getBroker() {

		return broker;		
	}

	public abstract int getLoad();

	public void shutdown(long timeMicroseconds) {

		for (ShardServer s : servers) s.shutdown(timeMicroseconds);
	}
}
