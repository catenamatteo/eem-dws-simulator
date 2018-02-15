package cpu;

public abstract class Core {

	protected CPU cpu;
	protected boolean busy;

	protected Core(CPU cpu) {

		this.cpu = cpu;
		busy = false;
	}
	
//	public int getCPUCurrentMaxFrequency() {
//		
//		return cpu.getCurrentMaxFrequency();
//	}
	
	public abstract int getFrequency();
//	public int getFrequency() {
//		
//		return currentFrequency;
//	}
	
	public void setMaxFrequency(long timeMicroseconds) {

		setFrequency(cpu.getMaxFrequency(), timeMicroseconds);
	}

	public void busy(long timeMicroseconds) {

		if (!busy) {
			busy = true;
			cpu.update(timeMicroseconds);
		}
		
	}

	public void free(long timeMicroseconds) {
		
		if (busy) {
			busy = false;
			cpu.update(timeMicroseconds);
		}
	}
	
	public boolean isBusy() {
		
		return busy;
	}

	public void setMinFrequency(long timeMicroseconds) {

		setFrequency(cpu.getMinFrequency(), timeMicroseconds);
	}

	public abstract void setFrequency(int frequency, long timeMicroseconds);
//	public void setFrequency(int frequency, long timeMicroseconds) {
//
//		int prevFrequency = currentFrequency;
//		
//		if (cpu.hasFrequency(frequency)) {
//			
//			currentFrequency = frequency;
//						
//		} else {
//			
//			currentFrequency = cpu.getMaxFrequency();
//			
//		}
//		
//		if (prevFrequency != currentFrequency)
//			cpu.update(timeMicroseconds);
//		
//	}

	public CPU getCpu() {
		
		return cpu;
	}
}
