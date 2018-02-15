package cpu;

public interface CPUBuilder {

	public CPU newInstance(String id);
	public int[] getFrequencies();
}
