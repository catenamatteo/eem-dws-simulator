package cpu;

public class Core {

	protected CPU cpu;
	protected int currentFrequency;
	private boolean busy;

	Core(CPU cpu) {

		this.cpu = cpu;
		this.currentFrequency = cpu.getCPUModel().getMinFrequency();
		busy=false;
	}
	
	public int getFrequency() {
		
		return currentFrequency;
	}
	
	public int getActualFrequency() {
		
		return cpu.getCPUModel().getFrequency(cpu.mpp_at_cmf);
	}

	public void setMaxFrequency(long timeMicroseconds) {

		setFrequency(cpu.getCPUModel().getMaxFrequency(), timeMicroseconds);
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

		setFrequency(cpu.getCPUModel().getMinFrequency(), timeMicroseconds);
	}

	public void setFrequency(int frequency, long timeMicroseconds) {

		int prevFrequency = currentFrequency;
		
		if (cpu.getCPUModel().hasFrequency(frequency)) {
			
			currentFrequency = frequency;
						
		} else {
			
			currentFrequency = cpu.getCPUModel().getMaxFrequency();
			
		}
		
		if (prevFrequency != currentFrequency)
			cpu.update(timeMicroseconds);
		
	}

	public CPU getCpu() {
		
		return cpu;
	}

	public void shutdown(long timeMicroseconds) {

		//it basically forces a state change in the cpu to dump a last statistic
		if (isBusy()) {
			
			free(timeMicroseconds);
			
		} else {
			
			busy(timeMicroseconds);
			free(timeMicroseconds);
		}
		
	}
}
