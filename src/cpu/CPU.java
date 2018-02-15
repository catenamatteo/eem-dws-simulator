package cpu;

import java.util.Arrays;

public abstract class CPU {

	/*
	 * TODO:
	 * some of these fields and methods are very PEGASUS specific and maybe shouldn't be here 
	 * but in a dedicated class or subclass
	 */
	
	protected int numCores;
	protected int[] frequencies;
	protected double[] powerCaps;	

	protected Core[] cores;

	protected long statusChangeTime;
	protected int activeCores;
	protected int currentMaxFrequency;
	
	protected StringBuilder sb;	
	protected String id;
	
	protected CPU(String id, int numCores, int[] frequencies, double[] powerCaps) {

		this.numCores = numCores;
		this.frequencies = frequencies;
		this.powerCaps = powerCaps;
		
		
		this.statusChangeTime = 0;
		this.currentMaxFrequency = frequencies[0];
		this.activeCores = 0;
		
		this.cores = new Core[numCores];
		for (int i = 0; i < numCores; i++) 
			cores[i] = getNewCoreInstance(this);
		
		this.sb = new StringBuilder();
		
		this.id = id;
	}

	protected abstract Core getNewCoreInstance(CPU cpu);

	protected abstract void update(long timeMicroseconds);
	
//	void update(long timeMicroseconds) {
//		
//		sb.setLength(0);
//		//get number of active cores and current max frequency
//		int activeCores = 0;
//		int currentMaxFrequency = frequencies[0];
//		for (Core c : cores) {
//			
//			currentMaxFrequency = Math.max(currentMaxFrequency, c.getFrequency());
//			if (c.isBusy()) {				
//				activeCores++;
//				sb.append(c.getFrequency());
//				sb.append(" ");
//			}
//		}
//		
//		if (activeCores == 0)
//			sb.append("idle");
//		else
//			sb.setLength(sb.length()-1); //remove one extra white char
//		
//		String newStatus = sb.toString();
//		
//		sb.setLength(0);
//		sb.append("[cpu@");
//		sb.append(id);
//		sb.append("] ");
//		sb.append(status);
//		sb.append(" ");
//		sb.append(statusChangeTime);
//		sb.append(" ");
//		sb.append(timeMicroseconds);
//		if (statusChangeTime != timeMicroseconds) System.out.println(sb.toString());
//		
//		//new status
//		this.status = newStatus;
//		this.statusChangeTime = timeMicroseconds;
//		this.activeCores = activeCores;
//		this.currentMaxFrequency = currentMaxFrequency;
//		this.currentPowerCap = powerCaps[Arrays.binarySearch(frequencies, currentMaxFrequency)];
//	}

	public Core getCore(int i) {

		return cores[i];
	}

	protected abstract void setFrequency(int frequency, long timeMicroseconds);
//	private void setFrequency(int frequency, long timeMicroseconds) {
//				
//		for (Core c : cores) {
//			
//			c.currentFrequency = frequency;
//		}
//		update(timeMicroseconds);
//	}
	
	public void setMaxPower(long timeMicroseconds) {

		setFrequency(frequencies[frequencies.length-1], timeMicroseconds);
	}

	public void changePower(double powerCap, long timeMicroseconds) {

	
		setFrequency(getFrequency(powerCap), timeMicroseconds);
	}

	public abstract void shutdown(long timeMicroseconds);
//	public void shutdown(long timeMicroseconds) {
//
//		//it basically forces a last print of the stats
//		update(timeMicroseconds);
//		
//	}

	public int getMinFrequency() {
		
		return frequencies[0];
	}

	public int getMaxFrequency() {
		
		return frequencies[frequencies.length-1];
	}

	public int getNumCores() {
		
		return numCores;
	}

	public int[] getFrequencies() {
		
		return frequencies;
	}

	public int getFrequency(double powerCap) {

		int idx = Arrays.binarySearch(powerCaps, powerCap);
		if (idx < 0) idx = Math.max(0, - idx - 2);
	
		return frequencies[idx];
	}

	public double getPowerCap(int frequency) {
		
		int idx = Arrays.binarySearch(frequencies, frequency);
		return powerCaps[idx];		
	}

	public boolean hasFrequency(int frequency) {
		
		return Arrays.binarySearch(frequencies, frequency) >= 0;
	}
}
