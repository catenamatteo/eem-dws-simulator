package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import cpu.CPUModel;
import engine.Broker;
import engine.Shard;
import eu.nicecode.simulator.Event;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import eu.nicecode.simulator.exception.TimeException;
import query.MSNQuerySource;
import util.Clock;

class GenericSimulator extends Simulator {

	private MSNQuerySource source;
	private int nonClockCnt;

	public void setQuerySource(MSNQuerySource source) {

		this.source = source;
	}

	@Override
	public boolean isDone() {

		return source.isDone() && nonClockCnt <= 0;
	}
	
	public void doAllEvents() {
		
		try  {
			
			while (true) {
				
				Event e = events.dequeue();
				
				if (time.compareTo(e.getTime()) <= 0)	{
					
					time.setTime(e.getTime());
					
				} else {
					
					throw new TimeException("You can't go back in time!");
					
				}
				
				e.execute(this);
				
				if (!(e instanceof Clock)) nonClockCnt--;
			}
			
		} catch (NoSuchElementException nsee)  {
		
			//end
		}
		
	}

	
	public void schedule(Event e) {
		
		if (!(e instanceof Clock)) nonClockCnt++;
		events.enqueue(e);
	}
}

public class Simulation {

	public static MSNQuerySource getQuerySource(String queryFilePath, Simulator simulator)
			throws FileNotFoundException, IOException {

		MSNQuerySource source = new MSNQuerySource(simulator, queryFilePath);
		return source;
	}

	public static void main(String args[]) throws IOException {

		String method = args[0];
		
		String queryFilePath = args[1];

		GenericSimulator simulator = new GenericSimulator();

		int[] frequencies = new int[] { 800000, 1000000, 1200000, 1400000, 1600000, 1800000, 2000000, 2100000, 2300000, 2500000,
				2700000, 2900000, 3100000, 3300000, 3500000 };
		double[] powers = new double[] { 4.6, 5.7, 8.7, 10.2, 11.5, 13.0, 14.7, 15.6, 17.6, 20.5, 23.0, 25.4, 28.3, 31.0, 34.2 };
		CPUModel model = new cpu.regression.CPUModel("Intel", 4,
				"regressors.txt", frequencies, powers);

		Shard shardB = new Shard(args[2], args[3], model.getFrequencies());
		Shard shardA2 = new Shard(args[4], args[5], model.getFrequencies());
		Shard shardA3 = new Shard(args[6], args[7], model.getFrequencies());
		Shard shardA4 = new Shard(args[8], args[9], model.getFrequencies());
		Shard shardA5 = new Shard(args[10], args[11], model.getFrequencies());
		
		int numOfReplicas = Integer.parseInt(args[12]);

		Broker broker = null;
		switch (method.toLowerCase()) {
		case "pesos":
			broker = getPESOSQueryBroker(simulator, model, numOfReplicas, shardB, shardA2, shardA3, shardA4, shardA5);
			break;
		case "pegasus":
			broker = getPEGASUSQueryBroker(simulator, model, numOfReplicas, shardB, shardA2, shardA3, shardA4, shardA5);
			break;
		case "perf":
		default:
			broker = getPERFQueryBroker(simulator, model, numOfReplicas, shardB, shardA2, shardA3, shardA4, shardA5);
			break;
		}

		MSNQuerySource source = getQuerySource(queryFilePath, simulator);
		simulator.setQuerySource(source);

		source.generate(simulator, broker);

		simulator.doAllEvents();

	}

	private static Broker getPERFQueryBroker(GenericSimulator simulator, CPUModel model, int numOfReplicas, Shard... shards) {

		return new engine.mmk.Broker(simulator, model, numOfReplicas, shards);

	}

	private static Broker getPESOSQueryBroker(GenericSimulator simulator, CPUModel model, int numOfReplicas, Shard... shards) {


		return new engine.kmm1.pesos.Broker(simulator, model, new Time(500, TimeUnit.MILLISECONDS), numOfReplicas,
				shards);

	}

	private static Broker getPEGASUSQueryBroker(GenericSimulator simulator, CPUModel model, int numOfReplicas, Shard... shards) {

		return new engine.mmk.pegasus.Broker(simulator, model, new Time(500, TimeUnit.MILLISECONDS), numOfReplicas,
				shards);

	}

}
