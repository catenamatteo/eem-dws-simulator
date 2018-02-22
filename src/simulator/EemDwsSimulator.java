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
	private double[] sec2energyConsumption;

	public EemDwsSimulator(String output, int simulationDurationInHours) throws FileNotFoundException, IOException {
		
		GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(output));	
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(gzip)));
		sec2energyConsumption = new double[(simulationDurationInHours * 60 * 60) + 1]; //+1, just to be sure;
		
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
		printEnergy();
		pw.close();
	}

	
	private void printEnergy() {
		
		for (int sec = 0; sec < sec2energyConsumption.length; sec++){
			
			println(String.format("[energy] %d %.3f", sec, sec2energyConsumption[sec]));
		}
		
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
	
	public void updateEnergyConsumption(double power, long statusChangeTimeInMicros, double seconds) {

		long start = statusChangeTimeInMicros;
        int start_sec = (int) (start / 1_000_000); //start in seconds
        int end_sec = (int) ((start + (seconds * 1_000_000)) / 1_000_000); //end in seconds
        double energy_fraction = (power * seconds) / Math.max(1, end_sec - start_sec);
        for (int b = start_sec; b <= end_sec; b++) sec2energyConsumption[b] += energy_fraction;
	}
}