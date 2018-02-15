package cpu.impl;

import cpu.CPU;
import cpu.Core;

public class Intel_i7_4770K_Core extends Core {

	protected int currentFrequency;

	Intel_i7_4770K_Core(CPU cpu) {
		super(cpu);
	}

	@Override
	public int getFrequency() {
		
		return ((Intel_i7_4770K)cpu).getCurrentMaxFrequency();
	}
	
	public void setFrequency(int frequency, long timeMicroseconds) {

		int prevFrequency = currentFrequency;
		
		if (cpu.hasFrequency(frequency)) {
			
			currentFrequency = frequency;
						
		} else {
			
			currentFrequency = cpu.getMaxFrequency();
			
		}
		
		if (prevFrequency != currentFrequency)
			((Intel_i7_4770K)cpu).update(timeMicroseconds);
		
	}

}
