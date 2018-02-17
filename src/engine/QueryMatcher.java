package engine;

import cpu.Core;
import engine.ShardServer;
import eu.nicecode.queueing.Request;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;
import query.Query;
import query.RunningQuery;

public abstract class QueryMatcher implements Agent {

	protected ShardServer server;
	protected Core core;


	public QueryMatcher(ShardServer shardServer, Core core) {
		
		this.server = shardServer;
		this.core = core;
		core.setMaxFrequency(server.getIndexReplica().getBroker().getSimulator().now().getTimeMicroseconds());
		
	}

	public Core getCore() {
		
		return core;
	}

	public ShardServer getShardServer() {
		
		return server;
	}
	
	public void serveQuery(Query query, Simulator simulator) {

		RunningQuery rq = new RunningQuery(simulator.now().clone(), this, query);
		simulator.schedule(rq);
		core.busy(simulator.now().getTimeMicroseconds());
	}
	
	public void completeRequest(Request request, Simulator simulator) {

		Query sq = (Query) request;
		
		long now = simulator.now().getTimeMicroseconds();
		long arrivalTime = sq.getArrivalTime().getTimeMicroseconds();
		long completionTime = now - arrivalTime;
				
		server.receiveResults(sq.getUid(), completionTime);
		
		afterRequestCompletion(sq, simulator);
	}
}
