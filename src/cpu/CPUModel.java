package cpu;

import java.util.Arrays;

public class CPUModel {

	protected String name;
	protected int numCores;
	protected int[] frequencies;
	protected double[] powers;
	
	public CPUModel(String name, int numCores, int[] frequencies, double[] powers) {
		super();
		this.name = name;
		this.numCores = numCores;
		this.frequencies = frequencies;
		this.powers = powers;		
	}
	
	public CPU getNewInstance() {
		
		return new CPU(this);
		
	}

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
	
	public int getFrequency(double power) {
		
		int idx = Arrays.binarySearch(powers, power);
		if (idx >= 0) {
			
			return frequencies[idx];
			
		} else {
			
			idx = Math.max(0, - idx - 2);
			return frequencies[idx];
		}			
	}

	public double getPower(int frequency) {
		
		int idx = Arrays.binarySearch(frequencies, frequency);
		return powers[idx];	
	}

	public boolean hasFrequency(int frequency) {
		
		return Arrays.binarySearch(frequencies, frequency) >= 0;
	}
}
