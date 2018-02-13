package cpu;
//package cpu;
//
//public class CPU {
//
//	protected CPUModel cpuModel;
//	protected Core[] cores;
//	protected long statusChangeTime = 0;
//	protected String status = "IDLE";
//	private double power;
//
//	CPU(CPUModel cpuModel) {
//
//		this.cpuModel = cpuModel;
//		cores = new Core[cpuModel.numCores];
//		for (int i = 0; i < cpuModel.numCores; i++) 
//			cores[i] = new Core(this);
//		updatePower();
//		status = buildStatusString();
//	}
//
//	private String buildStatusString() {
//
//		boolean isIdle = true;
//		int cnt = 0;
//		for (Core c : cores) {
//			if (c.isBusy()) {
//				isIdle = false;
//				cnt++;
//			}
//		}
//		
//		String status = null;
//		
//		if (isIdle) {
//			
//			status = Integer.toString(cpuModel.getMinFrequency());
//			
//		}else {
//			
//			String[] freqs = new String[cnt];
//			int idx = 0;
//			for (Core c : cores) {
//				if (c.isBusy()) {
//					freqs[idx++]=Integer.toString(c.getFrequency());
//				}
//			}
//			status = String.join(" ", freqs);			
//		
//		}
//		
//		return status;
//
//	}
//
//	public CPUModel getCPUModel() {
//		
//		return cpuModel;
//	}
//
//	public Core getCore(int i) {
//
//		return cores[i];
//	}
//
//	public void stateChanged(long timeMicroseconds) {
//
//		long time = timeMicroseconds - statusChangeTime;
//		
//		StringBuilder sb = new StringBuilder(this.toString());
//		sb.append(" ");
//		sb.append(status);
//		sb.append(" ");
//		sb.append(time);
//		System.out.println(sb.toString());
//
//		//new status
//		status = buildStatusString();
//		statusChangeTime = timeMicroseconds;
//	}
//
//	public void setMaxPower() {
//
//		int frequency = cpuModel.getMaxFrequency();
//		for (Core c : cores)
//			c.setFrequency(frequency);
//		power = cpuModel.getPower(frequency);
//
//	}
//
//	public void changePower(double d) {
//
//		int frequency = cpuModel.getFrequency(power * d);
//		for (Core c : cores)
//			c.setFrequency(frequency);
//		power = cpuModel.getPower(frequency);
//		
//	}
//
//	public void updatePower() {
//		
//		int max = 0;
//		for (Core c : cores) max = Math.max(max, c.getFrequency());
//		power = cpuModel.freq2power.get(max);
//		
//	}
//
//}
