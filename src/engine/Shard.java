package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import eu.nicecode.simulator.Time;
import it.unimi.dsi.fastutil.longs.Long2LongArrayMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

class QueryTemplate {
	
	int numberOfTerms;
	int predictedProcessingCost;
	int predictedProcessingCostRMSE;
	Long2LongMap serviceTimes;
	
	QueryTemplate() {

		serviceTimes = new Long2LongArrayMap();
		
	}
	
}

public class Shard {

	protected Long2ObjectMap<QueryTemplate> templates;
	
	public Shard(String filenamePPC, String filenameTimes, int... frequencies) throws IOException {
		
		templates = new Long2ObjectOpenHashMap<>();
		
		BufferedReader br1 = new BufferedReader(new FileReader(filenamePPC));
		String line1 = null;
		while ((line1 = br1.readLine()) != null) {
			String fields[] = line1.split(" ");
			long qid = Long.parseLong(fields[0]);
			int numberOfTerms = Integer.parseInt(fields[1]);
			int predictedProcessingCost = Integer.parseInt(fields[2]);
			int predictedProcessingCostRMSE = Integer.parseInt(fields[3]);
			QueryTemplate qt = new QueryTemplate();
			qt.numberOfTerms = numberOfTerms;
			qt.predictedProcessingCost = predictedProcessingCost;
			qt.predictedProcessingCostRMSE = predictedProcessingCostRMSE;
			templates.put(qid, qt);
		}
		br1.close();
		
		BufferedReader br2 = new BufferedReader(new FileReader(filenameTimes)); 
		String line2 = null;
		while ((line2 = br2.readLine()) != null) {
			String fields[] = line2.split(" ");
			long qid = Long.parseLong(fields[0]);
			QueryTemplate qt = templates.get(qid);
			for (int i = 1; i < fields.length; i++) {
				Float t = Float.parseFloat(fields[i]) * 1000; //ms to micros
				qt.serviceTimes.put(frequencies[i-1], t.longValue());
			}
		}
		br2.close();
		
	}
	
	public Time getServiceTime(long qid, int frequency) {
		
		return new Time(templates.get(qid).serviceTimes.get(frequency), TimeUnit.MICROSECONDS);
	}

	public int getPredictedProcessingCost(long qid) {
		
		return templates.get(qid).predictedProcessingCost;
	}

	public int getPredictedProcessingCostRMSE(long qid) {
		
		return templates.get(qid).predictedProcessingCostRMSE;
	}
	
	public int getNumberOfTerms(long qid) {
		
		return templates.get(qid).numberOfTerms;
	}
}
