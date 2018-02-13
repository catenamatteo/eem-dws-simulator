package engine.kmm1.pesos;

import cpu.Core;
import engine.Shard;
import engine.ShardServer;
import eu.nicecode.queueing.Request;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import query.Query;
import query.RunningQuery;

public class QueryMatcher extends engine.kmm1.QueryMatcher {

	protected RunningQuery currentRunningQuery;
	

	public QueryMatcher(ShardServer shardServer, Core core) {
		super(shardServer, core);
		core.setMinFrequency(server.getReplicaManager().getBroker().getSimulator().now().getTimeMicroseconds());
	}
	
	@Override
	public void receiveRequest(Request request, Agent from, Simulator simulator) {
			
		if (!core.isBusy() && queue.isEmpty()) {

			serveQuery(((Query)request), simulator);
			
		} else {
			
			queue.offer(request);
			runFrequencyScheduler(simulator.now().getTimeMicroseconds());
		}
	}
	
	
	public void serveQuery(Query query, Simulator simulator) {

		RunningQuery rq = new RunningQuery(simulator.now().clone(), this, query);
		simulator.schedule(rq);
		core.busy(simulator.now().getTimeMicroseconds());
		currentRunningQuery = rq;
		runFrequencyScheduler(simulator.now().getTimeMicroseconds());
	}
		
	
	@Override
	public void afterRequestCompletion(Request request, Simulator simulator) {
		
		currentRunningQuery = null;
		core.setMinFrequency(simulator.now().getTimeMicroseconds());
		super.afterRequestCompletion(request, simulator);
	}

	private void runFrequencyScheduler(long now) {
		
		Time timeBudget = ((engine.kmm1.pesos.Broker)server.getReplicaManager().getBroker()).getTimeBudget();
		
		long volume = 0;
		double maxDensity = 0;
		
		double lateness = getLateness(now); 
		
		Query currentOriginalQuery = currentRunningQuery.getOriginalQuery();
		int pcost = Math.max(0, (currentRunningQuery.getPredictedProcessingCost() + currentRunningQuery.getPredictedProcessingCostRMSE()) - currentRunningQuery.getProcessedPostings()); 		
		volume+=pcost;
		double currentDeadline = currentOriginalQuery.getArrivalTime().getTimeMicroseconds() + timeBudget.getTimeMicroseconds() - lateness;
		if (currentDeadline <= now) {
			
			//maxDensity = Double.POSITIVE_INFINITY;
			core.setMaxFrequency(now);
			return;
		
		} else {
		
			maxDensity = ((double)volume) / (currentDeadline - now);
		}
		
		for (Request r : queue) {
			
			Query q = (Query) r;
			volume += q.getPredictedProcessingCost(server.getShard()) + q.getPredictedProcessingCostRMSE(server.getShard());
			
			double deadline = q.getArrivalTime().getTimeMicroseconds() + timeBudget.getTimeMicroseconds() - lateness;
			double density;
			if (deadline <= now) {
				
				//density = Double.POSITIVE_INFINITY;
				core.setMaxFrequency(now);
				return;
				
			} else {
				
				density = ((double)volume) / (deadline - now);
			}
			
			if (density > maxDensity) 
				maxDensity = density;
		}
		
		double targetTime = Math.min((pcost/maxDensity) / 1000.0,//ms
				timeBudget.getTimeMicroseconds() / 1000.0);
		int frequency = identifyTargetFrequency(currentRunningQuery.getNumberOfTerms(), pcost, targetTime);
		core.setFrequency(frequency, now);		
	}
	
	private int identifyTargetFrequency(int numOfTerms, int postings, double targetTime) {
	
		QueryEfficiencyPredictors qep = ((engine.kmm1.pesos.Broker)server.getReplicaManager().getBroker()).getQueryEfficiencyPredictors();
		
		for (int frequency : core.getCpu().getCPUModel().getFrequencies()) {
			
			//double time = getShardServer().getShard().getServiceTime(query.getQid(), frequency).getTimeMicroseconds();
			double time = qep.regress(numOfTerms, postings, frequency);
			if (time <= targetTime) return frequency;
		}		
		return core.getCpu().getCPUModel().getMaxFrequency();
	}

	protected double getLateness(long now) {

		Time timeBudget = ((engine.kmm1.pesos.Broker)server.getReplicaManager().getBroker()).getTimeBudget();
		
		double lateness = 0;
		int cnt = 0;
		
		long generalBudget = timeBudget.getTimeMicroseconds();
		
		Query sqCurrent = currentRunningQuery.getOriginalQuery();
		int pcost = Math.max(0, (currentRunningQuery.getPredictedProcessingCost() + currentRunningQuery.getPredictedProcessingCostRMSE()) - currentRunningQuery.getProcessedPostings()); 		
		double currentRemaining = predictServiceTimeAtMaxFreq(currentRunningQuery.getNumberOfTerms(), pcost) * 1000.0;
		double currentBudget = Math.max(0, generalBudget - (now - sqCurrent.getArrivalTime().getTimeMicroseconds()));
		
		if (currentRemaining > currentBudget) {
			
			lateness += currentRemaining - currentBudget;
		
		} else {
			
			cnt++;
		}
		
		Shard shard = server.getShard();
		
		for (Request r : queue) {
			
			Query q = (Query) r;
			double remainingTime4q = predictServiceTimeAtMaxFreq(q.getNumberOfTerms(shard), q.getPredictedProcessingCost(shard)+q.getPredictedProcessingCostRMSE(shard)) * 1000;
			double budget = Math.max(0, generalBudget - (now - q.getArrivalTime().getTimeMicroseconds()));
			if (remainingTime4q > budget) {
				
				lateness += remainingTime4q - budget;
			
			} else {
				
				cnt++;
			}
		}
		
		
		return Math.ceil(lateness / cnt);
	}

	private double predictServiceTimeAtMaxFreq(int numOfTerms, int postings) {
		
		//return getShardServer().getShard().getServiceTime(query.getQid(), core.getCpu().getCPUModel().getMaxFrequency()).getTimeMicroseconds();
		QueryEfficiencyPredictors qep = ((engine.kmm1.pesos.Broker)server.getReplicaManager().getBroker()).getQueryEfficiencyPredictors();
		
		int max = core.getCpu().getCPUModel().getMaxFrequency();
		return qep.regress(numOfTerms, postings, max); 
	}

	protected Query dequeueQuery() {
		
		Query rtn = (Query) queue.poll();
		runFrequencyScheduler(server.getSimulator().now().getTimeMicroseconds());
		return rtn;
	}

}
