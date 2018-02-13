package engine.kmm1;

import java.util.Collections;

import cpu.CPUModel;
import cpu.Core;
import engine.QueryMatcher;
import engine.ReplicaManager;
import engine.Shard;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import query.Query;

public class ShardServer extends engine.ShardServer {

	public ShardServer(ReplicaManager replicaManager, Shard shard, CPUModel cpuModel, int id) {
		super(replicaManager, shard, cpuModel, id);
	}

	@Override
	protected QueryMatcher newQueryMatcherInstance(Core core) {
		// TODO Auto-generated method stub
		return new engine.kmm1.QueryMatcher(this, core);
	}
	


	public void receiveQuery(Query query) {

		IntList leastLoadedMatcherList = new IntArrayList(matcher.length);
		int load = Integer.MAX_VALUE;
		for (int i = 0; i < matcher.length; i++) {
			
			int mLoad = ((engine.kmm1.QueryMatcher)matcher[i]).getLoad();
			if (mLoad <= load) {
				
				if (mLoad < load) {
					load = mLoad;
					leastLoadedMatcherList.clear();
				}
				leastLoadedMatcherList.add(i);
			}			
		}
		
		if (leastLoadedMatcherList.size() > 1) {

			Collections.shuffle(leastLoadedMatcherList);
		}
		
		matcher[leastLoadedMatcherList.getInt(0)].receiveRequest(query, null, this.getSimulator());		
	}

	public int getLoad() {
		
		int load = 0;
		for (QueryMatcher qm : matcher)
			load += ((engine.kmm1.QueryMatcher) qm).getLoad();
		return load;
		
	}

	Query stealQuery() {
	
		IntList mostLoadedMatcherList = new IntArrayList(matcher.length);
		int load = Integer.MIN_VALUE;
		for (int i = 0; i < matcher.length; i++) {
			
			int mLoad = ((engine.kmm1.QueryMatcher)matcher[i]).getLoad();
			if (mLoad > 1 && mLoad >= load) {
				
				if (mLoad > load) {
					load = mLoad;
					mostLoadedMatcherList.clear();
				}
				mostLoadedMatcherList.add(i);
			}			
		}
		
		if (!mostLoadedMatcherList.isEmpty()) {
			
			if (mostLoadedMatcherList.size() > 1) {
				
				Collections.shuffle(mostLoadedMatcherList);
			}
			
			return ((engine.kmm1.QueryMatcher)matcher[mostLoadedMatcherList.getInt(0)]).dequeueQuery();
			
		} else {
			
			return null;
		}
	}

}
