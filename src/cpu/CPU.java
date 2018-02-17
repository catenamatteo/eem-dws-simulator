package cpu;

import engine.ShardServer;

public abstract class CPU {

	
	protected int numCores;
		
	protected int[] frequencies;

	protected Core[] cores;

	protected long statusChangeTime;
	
	protected ShardServer server;
	
	protected CPU(ShardServer server, int numCores) {

		this.numCores = numCores;
		
		this.statusChangeTime = 0;
		
		this.cores = new Core[numCores];
		for (int i = 0; i < numCores; i++) 
			cores[i] = getNewCoreInstance(this);
				
		this.server = server;
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

	public abstract void shutdown(long timeMicroseconds);

	public abstract int getMinFrequency();

	public abstract int getMaxFrequency();

	public abstract int[] getFrequencies();
	
	public int getNumCores() {
		
		return numCores;
	}
	
	protected int activeCores() {

		int activeCores = 0;
		for (Core c : cores) {
			
			if (c.isBusy()) {				
				activeCores++;
			}
		}
		
		return activeCores;
	}


}
