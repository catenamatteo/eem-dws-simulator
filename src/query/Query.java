package query;

import engine.Shard;
import eu.nicecode.queueing.Request;
import eu.nicecode.simulator.Time;

public class Query extends Request {

	private long qid;
	private long uid;	
	private long receivedServiceTime;
	
	public Query(Time arrivalTime, long qid, long uid) {
		
		super(arrivalTime, Time.ZERO);
		this.qid = qid;
		this.uid = uid;
	}

	public long getQid() {
		return qid;
	}
	
	public long getUid() {
		
		return uid;
	}
	
	@Override
	public Time getServiceTime() {
		
		throw new UnsupportedOperationException();
	}
	
	public void setReceivedServiceTime(long executedTime) {
		
		this.receivedServiceTime = executedTime;
	}
	
	public long getReceivedServiceTime() {
		
		return receivedServiceTime;
	}

	public Time getServiceTime(Shard shard, int frequency) {

		return shard.getServiceTime(qid, frequency);
	}

	public int getPredictedProcessingCost(Shard shard) {
		
		return shard.getPredictedProcessingCost(qid);
	}

	public int getPredictedProcessingCostRMSE(Shard shard) {
		
		return shard.getPredictedProcessingCostRMSE(qid);
	}

	public int getNumberOfTerms(Shard shard) {
		
		return shard.getNumberOfTerms(qid);
	}


}
