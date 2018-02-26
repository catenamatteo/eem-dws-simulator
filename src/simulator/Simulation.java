package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cpu.CPUBuilder;
import cpu.impl.Intel_i7_4770K_Builder;
import engine.QueryBroker;
import engine.Shard;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import query.MSNQuerySource;

public class Simulation {
	

	public static void main(String args[]) throws IOException {

		String method = args[0];		
		String queryFilePath = args[1];
		int numOfReplicas = Integer.parseInt(args[2]);
		int simulationDurationInHours = Integer.parseInt(args[3]);
		String outputFilePath = args[4];

		EemDwsSimulator simulator = new EemDwsSimulator(outputFilePath, simulationDurationInHours);
		
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
