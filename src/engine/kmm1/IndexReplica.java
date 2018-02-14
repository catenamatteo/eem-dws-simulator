package engine.kmm1;

import cpu.CPUModel;
import engine.QueryBroker;
import engine.Shard;
import engine.ShardServer;
import query.Query;

public class IndexReplica extends engine.IndexReplica {

	public IndexReplica(QueryBroker broker, CPUModel cpuModel, int id, Shard[] shards) {
		super(broker, cpuModel, id, shards);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ShardServer newShardServerInstance(Shard shard, CPUModel cpuModel, int id) {
		
		return new engine.kmm1.ShardServer(this, shard, cpuModel, id);
	}

	public void receiveQuery(Query query) {

		for (ShardServer s : servers) {
			
			engine.kmm1.ShardServer s1 = (engine.kmm1.ShardServer) s;
			s1.receiveQuery(query);
			
		}
		
	}

	public int getLoad() {
		
		int load = 0;
		for (ShardServer s : servers)
			load += ((engine.kmm1.ShardServer)s).getLoad();
		return load;
	}
}
