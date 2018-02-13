package engine;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import cpu.CPUModel;
import eu.nicecode.queueing.Request;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import query.Query;

public abstract class QueryBroker implements Agent {
	
	protected IndexReplica[] replicas;
	protected Long2LongMap arrivalTimes;
	protected Simulator simulator;
	
	public QueryBroker(Simulator simulator, CPUModel cpuModel, int numOfReplicas, Shard... shards) {

		this.simulator = simulator;
		this.arrivalTimes = new Long2LongOpenHashMap();
		replicas = new IndexReplica[numOfReplicas];
		for (int i = 0; i < numOfReplicas; i++)
			replicas[i] = newReplicaManagerInstance(cpuModel, shards);
			
		
	}
	
	@Override
	public Request nextRequest() {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendRequest(Request request, Agent to, Simulator simulator) {

		throw new UnsupportedOperationException();
		
	}

	@Override
	public void receiveRequest(Request request, Agent from, Simulator simulator) {
		
		arrivalTimes.put(((Query) request).getUid(), request.getArrivalTime().getTimeMicroseconds());		

		
		IntList leastLoadedReplicaList = new IntArrayList(replicas.length);
		int load = Integer.MAX_VALUE;
		for (int i = 0; i < replicas.length; i++) {
			
			int mLoad = replicas[i].getLoad();
			if (mLoad <= load) {
				
				if (mLoad < load) {
					load = mLoad;
					leastLoadedReplicaList.clear();
				}
				leastLoadedReplicaList.add(i);
			}			
		}
		
		if (leastLoadedReplicaList.size() > 1) {

			Collections.shuffle(leastLoadedReplicaList);
		}
		
		replicas[leastLoadedReplicaList.getInt(0)].receiveQuery((Query) request);
	}

	@Override
	public void completeRequest(Request request, Simulator simulator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterRequestCompletion(Request request, Simulator simulator) {
		// TODO Auto-generated method stub

	}
	
	protected abstract IndexReplica newReplicaManagerInstance(CPUModel cpuModel, Shard... shards);

	public void receiveResults(long uid, long completionTime) {

		long arrivalTime = arrivalTimes.remove(uid); //get and remove;
		System.out.printf("[broker]\t%d\t%.3f\n", TimeUnit.MICROSECONDS.toSeconds(arrivalTime), completionTime/1e3);
	}

	public Simulator getSimulator() {
	
		return simulator;
	}

	public void shutdown(long timeMicroseconds) {

		for (IndexReplica r : replicas) r.shutdown(timeMicroseconds);
	}	
}
