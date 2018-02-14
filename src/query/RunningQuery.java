package query;

import java.util.concurrent.TimeUnit;

import engine.QueryMatcher;
import eu.nicecode.simulator.Event;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;

public class RunningQuery extends Event {

	private QueryMatcher matcher;
	private Query originalQuery;
		
	private long requiredTime;
	private long executedTime; //this value make sense only relatively to requireTime, which changes with frequency
	private long receivedServiceTime;
	
	private int frequency;
	
	boolean firstQuantum = true;
	
	public RunningQuery(Time time, QueryMatcher processor, Query originalQuery) {

		super(time);

		this.matcher = processor;
		this.originalQuery = originalQuery;
		
	}

	@Override
	public void execute(Simulator simulator) {

		if (firstQuantum) {
			
			this.frequency = matcher.getCore().getFrequency();
			this.executedTime = 0;
			this.requiredTime = originalQuery.getServiceTime(matcher.getShardServer().getShard(), frequency).getTimeMicroseconds();
			firstQuantum = false;
			
//			//this is hardcoded
//			double[] b = {0.00910,0.04730,0.07226,0.08384,0.07519,0.07547,0.06564,0.06465,0.06852,0.05864,0.06245,0.06036,0.05149,0.04154,0.03622};
//
//			matcher.getServer().addConsumedEnergy(b[matcher.getFrequencyIdx()]);
		}
		
		long inc = TimeUnit.MILLISECONDS.toMicros(1);
		executedTime+=inc;
		receivedServiceTime+=inc;
//		matcher.increaseBusyTime(inc);
//		matcher.increaseEnergy(inc);
		time = time.addTime(inc, TimeUnit.MICROSECONDS);
		
		if (executedTime >= requiredTime) {
			
			//conclude
			originalQuery.setReceivedServiceTime(receivedServiceTime);
			QueryCompletion rc = new QueryCompletion(time, matcher, originalQuery);
			simulator.schedule(rc);
			
			
		} else {
		
			if (matcher.getCore().getFrequency() != frequency && !firstQuantum) {
				
				int newFrequency = matcher.getCore().getFrequency();
		
				long newRequiredTime = originalQuery.getServiceTime(matcher.getShardServer().getShard(), frequency).getTimeMicroseconds();
				long newExecutedTime = Math.round((executedTime / ((double) requiredTime)) * newRequiredTime);
				
				frequency = newFrequency;
				requiredTime = newRequiredTime;
				executedTime = newExecutedTime;
			
			}
		
			simulator.schedule(this);
		}
		
		//System.err.println(this.getOriginalQuery().getQid()+" : "+(executedTime / ((double) requiredTime)));
	}

	private double getCompletionRatio() {
		
		return ((double) executedTime) / requiredTime;
	}

	/*
	 * TODO: this is very PESOS specific, maybe it should be somewhere else
	 */
	public int getPredictedProcessingCost() {
		
		return originalQuery.getPredictedProcessingCost(matcher.getShardServer().getShard());
	}

	public Query getOriginalQuery() {
		
		return originalQuery;
	}

	/*
	 * TODO: this is very PESOS specific, maybe it should be somewhere else
	 */
	public int getPredictedProcessingCostRMSE() {
		
		return originalQuery.getPredictedProcessingCostRMSE(matcher.getShardServer().getShard());
	}

	/*
	 * TODO: this is very PESOS specific, maybe it should be somewhere else
	 */
	public int getProcessedPostings() {
		
		return (int) Math.floor((getPredictedProcessingCost() + getPredictedProcessingCostRMSE()) * getCompletionRatio());
		
	}

	public int getNumberOfTerms() {
		
		return originalQuery.getNumberOfTerms(matcher.getShardServer().getShard());
	}
}
