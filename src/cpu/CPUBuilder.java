package cpu;

import engine.ShardServer;

public interface CPUBuilder {

	public CPU newInstance(ShardServer server);
	public int[] getFrequencies();
}
