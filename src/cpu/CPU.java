package cpu;

public class CPU {

	/*
	 * TODO:
	 * some of these fields and methods are very PEGASUS specific and maybe shouldn't be here 
	 * but in a dedicated class or subclass
	 */
	protected CPUModel cpuModel;
	protected Core[] cores;

	protected String status;
	protected long statusChangeTime;
	protected int activeCores;
	protected int currentMaxFrequency;

	protected double mpp_at_cmf; //maximum possible power at the current maximum frequency, i.e., if all the cores were active at the current max freq
	
	private StringBuilder sb;

	CPU(CPUModel cpuModel) {

		this.cpuModel = cpuModel;
		this.statusChangeTime = 0;
		int minFreq = cpuModel.getMinFrequency();
		this.status = "idle";
		this.mpp_at_cmf = cpuModel.getPower(minFreq);
		this.activeCores = 0;
		this.currentMaxFrequency = minFreq;
		
		this.cores = new Core[cpuModel.numCores];
		for (int i = 0; i < cpuModel.numCores; i++) 
			cores[i] = new Core(this);
		
		this.sb = new StringBuilder();
	}

	void update(long timeMicroseconds) {
		
		sb.setLength(0);
		//get number of active cores and current max frequency
		int activeCores = 0;
		int currentMaxFrequency = cpuModel.getMinFrequency();
		for (Core c : cores) {
			
			if (c.isBusy()) {
				
				activeCores++;
				currentMaxFrequency = Math.max(currentMaxFrequency, c.getFrequency());
				sb.append(c.getFrequency());
				sb.append(" ");
			}
		}
		
		if (activeCores == 0)
			sb.append("idle");
		else
			sb.setLength(sb.length()-1); //remove one extra white char
		
		String potentialNewStatus = sb.toString();
		
		if (!(activeCores == this.activeCores && currentMaxFrequency == this.currentMaxFrequency)) { //something actually changed
						
			sb.setLength(0);
			sb.append(this);
			sb.append(" ");
			sb.append(status);
			sb.append(" ");
			sb.append(statusChangeTime);
			sb.append(" ");
			sb.append(timeMicroseconds);
			System.out.println(sb.toString());

			//new status
			this.status = potentialNewStatus;
			this.statusChangeTime = timeMicroseconds;
			this.activeCores = activeCores;
			this.currentMaxFrequency = currentMaxFrequency;
			this.mpp_at_cmf = cpuModel.getPower(currentMaxFrequency);
			
		}
	}

	public CPUModel getCPUModel() {
		
		return cpuModel;
	}

	public Core getCore(int i) {

		return cores[i];
	}

	private void setFrequency(int frequency, long timeMicroseconds) {
		
		boolean update = false;
		for (Core c : cores) {
			update |= c.currentFrequency != frequency;
			c.currentFrequency = frequency;
		}
		if (update) update(timeMicroseconds);
	}
	
	public void setMaxPower(long timeMicroseconds) {

		setFrequency(cpuModel.getMaxFrequency(), timeMicroseconds);
	}

	public void changePower(double d, long timeMicroseconds) {

		int frequency = cpuModel.getFrequency(mpp_at_cmf * d);
		setFrequency(frequency, timeMicroseconds);
	}
}
