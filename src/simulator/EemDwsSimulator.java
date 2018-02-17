package simulator;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.zip.GZIPOutputStream;

import engine.QueryBroker;
import eu.nicecode.simulator.Event;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.exception.TimeException;
import query.MSNQuerySource;
import util.Clock;

public class EemDwsSimulator extends Simulator {

	private MSNQuerySource source;
	private QueryBroker broker;
	private int nonClockCnt; //TODO: is this still used?
	private PrintWriter pw;

	public EemDwsSimulator(String output) throws FileNotFoundException, IOException {
		
		GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(output));	
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(gzip)));
		
	}
	
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
		pw.close();
	}

	
	public void schedule(Event e) {
		
		if (!(e instanceof Clock)) nonClockCnt++;
		events.enqueue(e);
	}

	public void setBroker(QueryBroker broker) {

		this.broker = broker;
	}

	public void println(String string) {

		pw.println(string);
	}
}