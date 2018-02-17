package cpu.impl;

import cpu.CPU;
import cpu.CPUBuilder;
import engine.ShardServer;

public class Intel_i7_4770K_Builder implements CPUBuilder {

	@Override
	public CPU newInstance(ShardServer server) {
		
		return new Intel_i7_4770K(server);
	}

	@Override
	public int[] getFrequencies() {
		
		return Intel_i7_4770K.frequencies;
	}

}
