package cpu.impl;

import cpu.CPU;
import cpu.Core;

public class Intel_i7_4770K extends CPU {

	static final int[] frequencies = new int[] { 800000, 1000000, 1200000, 1400000, 1600000, 1800000, 2000000, 2100000, 2300000, 2500000,
			2700000, 2900000, 3100000, 3300000, 3500000};
	static final double[] powerCaps = new double[] { 4.6, 5.7, 8.7, 10.2, 11.5, 13.0, 14.7, 15.6, 17.6, 20.5, 23.0, 25.4, 28.3, 31.0, 34.2 };
	
	Intel_i7_4770K(String id) {

		super(id, 4, frequencies, powerCaps);
	}

	protected Core getNewCoreInstance(CPU cpu) {
		
		return new Intel_i7_4770K_Core(this);
	}

	@Override
	protected void update(long timeMicroseconds) {
		
		//get number of active cores
		int activeCores = 0;
		for (Core c : cores) {
			
			if (c.isBusy()) {				
				activeCores++;
			}
		}
		
		if (statusChangeTime != timeMicroseconds) {
			
			sb.setLength(0);
			sb.append("[cpu@");
			sb.append(id);
			sb.append("] ");
			sb.append(this.currentMaxFrequency);
			sb.append(" ");
			sb.append(this.activeCores);
			sb.append(" ");
			sb.append(this.statusChangeTime);
			sb.append(" ");
			sb.append((timeMicroseconds - this.statusChangeTime)/1000000.0); //micros to sec
			
			System.out.println(sb.toString());
		} 
		
		//new status
		this.statusChangeTime = timeMicroseconds;
		this.activeCores = activeCores;
		this.currentMaxFrequency = getCurrentMaxFrequency();
	}

	@Override
	protected void setFrequency(int frequency, long timeMicroseconds) {

		for (Core c : cores) {

			((Intel_i7_4770K_Core)c).currentFrequency = frequency;
		}
		update(timeMicroseconds);
	}

	@Override
	public void shutdown(long timeMicroseconds) {

		//it basically forces a last print of the stats
		update(timeMicroseconds);
		
	}
	
	int getCurrentMaxFrequency() {
		
		int currentMaxFrequency = frequencies[0];
		for (Core c : cores) {
			
			if (c.isBusy()) currentMaxFrequency = Math.max(currentMaxFrequency, ((Intel_i7_4770K_Core)c).currentFrequency);

		}
		return currentMaxFrequency;
	}

}
