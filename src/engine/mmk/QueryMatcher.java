package engine.mmk;

import cpu.Core;
import eu.nicecode.queueing.Request;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;
import query.Query;

public class QueryMatcher extends engine.QueryMatcher implements Agent {
		
	public QueryMatcher(ShardServer shardServer, Core core) {
		
		super(shardServer, core);
		//Simulator sim = ((ShardServer) server).getSimulator();
		//start listening
		//sim.schedule(new QueryMatcherClock(sim.now().clone(), this));
	}

	@Override
	public void afterRequestCompletion(Request request, Simulator simulator) {

		Query next = ((ShardServer) server).getNextQuery();
		if (next != null) {
			
			serveQuery(next, simulator);
			
		} else {

			//start listening
			//simulator.schedule(new QueryMatcherClock(simulator.now().clone(), this));
			core.free(simulator.now().getTimeMicroseconds());
		}
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
		// TODO Auto-generated method stub
		
	}

	public boolean isIdle() {

		return !core.isBusy();
	}

}
