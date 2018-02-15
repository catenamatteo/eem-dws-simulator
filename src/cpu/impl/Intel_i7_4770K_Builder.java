package cpu.impl;

import cpu.CPU;
import cpu.CPUBuilder;

public class Intel_i7_4770K_Builder implements CPUBuilder {

	@Override
	public CPU newInstance(String id) {
		
		return new Intel_i7_4770K(id);
	}

	@Override
	public int[] getFrequencies() {
		
		return Intel_i7_4770K.frequencies;
	}

}
