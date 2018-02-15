package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import cpu.CPUBuilder;
import cpu.impl.Intel_i7_4770K_Builder;
import engine.QueryBroker;
import engine.Shard;
import eu.nicecode.simulator.Event;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import eu.nicecode.simulator.exception.TimeException;
import query.MSNQuerySource;
import util.Clock;

class EemDwsSimulator extends Simulator {

	private MSNQuerySource source;
	private QueryBroker broker;
	private int nonClockCnt; //TODO: is this still used?

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
		
		broker.shutdown(now().getTimeMicroseconds());
	}

	
	public void schedule(Event e) {
		
		if (!(e instanceof Clock)) nonClockCnt++;
		events.enqueue(e);
	}

	public void setBroker(QueryBroker broker) {

		this.broker = broker;
	}
}

public class Simulation {
	

	public static void main(String args[]) throws IOException {

		String method = args[0];		
		String queryFilePath = args[1];
		int numOfReplicas = Integer.parseInt(args[2]);

		EemDwsSimulator simulator = new EemDwsSimulator();
		
		CPUBuilder cpuBuilder = new Intel_i7_4770K_Builder();

		Shard shardB = new Shard("resources/cw09b.ef.pp", "resources/cw09b.ef.time", cpuBuilder.getFrequencies());
		Shard shardA2 = new Shard("resources/cw09a2.ef.pp", "resources/cw09a2.ef.time", cpuBuilder.getFrequencies());
		Shard shardA3 = new Shard("resources/cw09a3.ef.pp", "resources/cw09a3.ef.time", cpuBuilder.getFrequencies());
		Shard shardA4 = new Shard("resources/cw09a4.ef.pp", "resources/cw09a4.ef.time", cpuBuilder.getFrequencies());
		Shard shardA5 = new Shard("resources/cw09a4.ef.pp", "resources/cw09a5.ef.time", cpuBuilder.getFrequencies());
		
		QueryBroker broker = null;
		switch (method.toLowerCase()) {
		case "pesos":
			broker = getPESOSQueryBroker(simulator, cpuBuilder, numOfReplicas, shardB, shardA2, shardA3, shardA4, shardA5);
			break;
		case "pegasus":
			broker = getPEGASUSQueryBroker(simulator, cpuBuilder, numOfReplicas, shardB, shardA2, shardA3, shardA4, shardA5);
			break;
		case "perf":
		default:
			broker = getPERFQueryBroker(simulator, cpuBuilder, numOfReplicas, shardB, shardA2, shardA3, shardA4, shardA5);
			break;
		}

		MSNQuerySource source = getQuerySource(queryFilePath, simulator);
		simulator.setQuerySource(source);
		simulator.setBroker(broker);

		source.generate(simulator, broker);

		simulator.doAllEvents();

	}
	
	public static MSNQuerySource getQuerySource(String queryFilePath, Simulator simulator)
			throws FileNotFoundException, IOException {

		return new MSNQuerySource(simulator, queryFilePath);
	}


	private static QueryBroker getPERFQueryBroker(EemDwsSimulator simulator, CPUBuilder builder, int numOfReplicas, Shard... shards) {

		return new engine.kmm1.QueryBroker(simulator, builder, numOfReplicas, shards);

	}

	private static QueryBroker getPESOSQueryBroker(EemDwsSimulator simulator, CPUBuilder builder, int numOfReplicas, Shard... shards) {


		return new engine.kmm1.pesos.QueryBroker(simulator, builder, new Time(500, TimeUnit.MILLISECONDS), numOfReplicas,
				shards);

	}

	private static QueryBroker getPEGASUSQueryBroker(EemDwsSimulator simulator, CPUBuilder builder, int numOfReplicas, Shard... shards) {

		return new engine.mmk.pegasus.QueryBroker(simulator, builder, new Time(500, TimeUnit.MILLISECONDS), numOfReplicas,
				shards);

	}

}
