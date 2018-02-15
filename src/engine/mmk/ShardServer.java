package engine.mmk;

import java.util.Collections;

import cpu.CPUBuilder;
import cpu.Core;
import engine.IndexReplica;
import engine.QueryMatcher;
import engine.Shard;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import query.Query;

public class ShardServer extends engine.ShardServer {

	public ShardServer(IndexReplica replicaManager, Shard shard, CPUBuilder cpuBuilder, int id) {
		super(replicaManager, shard, cpuBuilder, id);
	}

	@Override
	protected QueryMatcher newQueryMatcherInstance(Core core) {
		
		return new engine.mmk.QueryMatcher(this, core);
	}
	
	public Query getNextQuery() {
		
		return ((engine.mmk.IndexReplica)replicaManager).getNextQuery(this.getId());
	}

	boolean hasIdlingResources() {
		
		for (QueryMatcher m : matcher) {
			
			engine.mmk.QueryMatcher m1 = (engine.mmk.QueryMatcher) m;
			if (m1.isIdle()) return true;
			
		}
		return false;		
		
	}

	void receiveQuery(Query query) {
		
		IntList slackingMatcher = new IntArrayList(matcher.length);
		for (int i = 0; i < matcher.length; i++) if (((engine.mmk.QueryMatcher) matcher[i]).isIdle()) slackingMatcher.add(i);
		
		if (slackingMatcher.size() > 1) {
			
			Collections.shuffle(slackingMatcher);
			
		}
		matcher[slackingMatcher.getInt(0)].serveQuery(query, getSimulator());		
	}

	@Override
	public int getLoad() {
		
		int cnt = 0;
		for (QueryMatcher m : matcher) if (!((engine.mmk.QueryMatcher) m).isIdle()) cnt++;
		return cnt;
		
	}

}
