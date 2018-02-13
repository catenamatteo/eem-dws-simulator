package query;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import eu.nicecode.queueing.Request;
import eu.nicecode.queueing.RequestSource;
import eu.nicecode.queueing.event.RequestArrival;
import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;

public class MSNQuerySource extends RequestSource {

	private BufferedReader brQueries;
	private boolean isBrQueriesClosed;
	private int uidCnt = 0;
		
	public MSNQuerySource(Simulator simulator, String queriesFilePath) throws FileNotFoundException, IOException {
						
		// open files
		brQueries = new BufferedReader(new InputStreamReader(new FileInputStream(queriesFilePath)));
		
	}

	public void generate(Simulator simulator, Agent to) {

		Request request = nextRequest();

		if (request != null) {

			RequestArrival ra = new RequestArrival(request.getArrivalTime(), this, to, request);
			simulator.schedule(ra);
			
		}
	}

	@Override
	public Request nextRequest() {

		if (isBrQueriesClosed) {
			
			return null;
		}
		
		String queryString = null;
		try {

			queryString = brQueries.readLine();

		} catch (IOException e) {

			//logger.error("Error generating next request", e);
		}

		if (queryString == null) {

			try {
				
				brQueries.close();
				isBrQueriesClosed = true;
			
			} catch (IOException e) {
				
				//logger.error("Error while closing a BufferedReader in {}", QuerySource.class.getSimpleName(), e);
			}
			
			return null;

		} else {

			String fields[] = queryString.split(" ");
			
			
			Time arrivalTime = new Time(Long.parseLong(fields[0]), TimeUnit.MILLISECONDS);
			
			long qid = Long.parseLong(fields[1]);
			long uid = uidCnt++;
			
					
			return new Query(arrivalTime, qid, uid);
			
		}
		
	}
	
	public boolean isDone() {
		
		return isBrQueriesClosed;
	}
}
