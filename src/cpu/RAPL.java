package cpu;

public interface RAPL {

	public double getPowerCap();	
	public void setMaxPowerCap(long timeMicroseconds);
	public void setPowerCap(double powerCap, long timeMicroseconds);	
}
