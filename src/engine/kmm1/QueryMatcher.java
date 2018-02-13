package engine.kmm1;

import java.util.LinkedList;
import java.util.Queue;

import cpu.Core;
import engine.ShardServer;
import eu.nicecode.queueing.Request;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;
import query.Query;

public class QueryMatcher extends engine.QueryMatcher {

	protected Queue<Request> queue;
	
	public QueryMatcher(ShardServer shardServer, Core core) {
		super(shardServer, core);
		this.queue = new LinkedList<>();
	}

	@Override
	public Request nextRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendRequest(Request request, Agent to, Simulator simulator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveRequest(Request request, Agent from, Simulator simulator) {
			
		if (!core.isBusy() && queue.isEmpty()) {

			serveQuery(((Query)request), simulator);
			
		} else {
			
			queue.offer(request);
		}
	}

	@Override
	public void afterRequestCompletion(Request request, Simulator simulator) {

		if (!queue.isEmpty()) {
			
			serveQuery(((Query)queue.poll()), simulator);

		} else {
			
			Query q = ((engine.kmm1.ShardServer) server).stealQuery();
			if (q != null) {
				
				serveQuery(q, simulator);
				
			} else {
				
				core.free(simulator.now().getTimeMicroseconds());
				
			}
		}
	}

	public int getLoad() {
		
		return core.isBusy() ? queue.size() + 1 : queue.size();
	}

	protected Query dequeueQuery() {
		
		return (Query) queue.poll();
	}

}
