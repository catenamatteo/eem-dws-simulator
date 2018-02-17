package cpu.impl;

import java.util.Arrays;

import cpu.CPU;
import cpu.Core;
import cpu.RAPL;
import engine.ShardServer;
import it.unimi.dsi.fastutil.doubles.Double2IntAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2IntSortedMap;
import simulator.EemDwsSimulator;

public class Intel_i7_4770K extends CPU implements RAPL {

	static final Double2IntSortedMap[] powerCap2frequency;
	static final int[] frequencies;
	static final int NUMCORES = 4;

	static {

		powerCap2frequency = new Double2IntSortedMap[NUMCORES];
		for (int i = 0; i < NUMCORES; i++)
			powerCap2frequency[i] = new Double2IntAVLTreeMap();
		powerCap2frequency[0].put(1.2, 800000);
		powerCap2frequency[0].put(1.6, 1000000);
		powerCap2frequency[0].put(2.3, 1200000);
		powerCap2frequency[0].put(3.2, 1400000);
		powerCap2frequency[0].put(3.9, 1600000);
		powerCap2frequency[0].put(4.8, 1800000);
		powerCap2frequency[0].put(5.7, 2000000);
		powerCap2frequency[0].put(5.9, 2100000);
		powerCap2frequency[0].put(6.8, 2300000);
		powerCap2frequency[0].put(7.9, 2500000);
		powerCap2frequency[0].put(8.9, 2700000);
		powerCap2frequency[0].put(9.9, 2900000);
		powerCap2frequency[0].put(11.2, 3100000);
		powerCap2frequency[0].put(12.3, 3300000);
		powerCap2frequency[0].put(14.1, 3500000);
		powerCap2frequency[1].put(10.0, 2100000);
		powerCap2frequency[1].put(11.3, 2300000);
		powerCap2frequency[1].put(2.3, 800000);
		powerCap2frequency[1].put(3.3, 1000000);
		powerCap2frequency[1].put(5.1, 1200000);
		powerCap2frequency[1].put(6.2, 1400000);
		powerCap2frequency[1].put(7.5, 1600000);
		powerCap2frequency[1].put(8.7, 1800000);
		powerCap2frequency[1].put(9.6, 2000000);
		powerCap2frequency[1].put(13.5, 2500000);
		powerCap2frequency[1].put(14.9, 2700000);
		powerCap2frequency[1].put(16.1, 2900000);
		powerCap2frequency[1].put(17.7, 3100000);
		powerCap2frequency[1].put(19.6, 3300000);
		powerCap2frequency[1].put(22.4, 3500000);
		powerCap2frequency[2].put(3.7, 800000);
		powerCap2frequency[2].put(5.2, 1000000);
		powerCap2frequency[2].put(7.9, 1200000);
		powerCap2frequency[2].put(9.0, 1400000);
		powerCap2frequency[2].put(10.6, 1600000);
		powerCap2frequency[2].put(11.9, 1800000);
		powerCap2frequency[2].put(13.5, 2000000);
		powerCap2frequency[2].put(14.2, 2100000);
		powerCap2frequency[2].put(15.9, 2300000);
		powerCap2frequency[2].put(18.9, 2500000);
		powerCap2frequency[2].put(20.7, 2700000);
		powerCap2frequency[2].put(22.8, 2900000);
		powerCap2frequency[2].put(25.5, 3100000);
		powerCap2frequency[2].put(28.2, 3300000);
		powerCap2frequency[2].put(31.2, 3500000);
		powerCap2frequency[3].put(4.6, 800000);
		powerCap2frequency[3].put(5.7, 1000000);
		powerCap2frequency[3].put(8.7, 1200000);
		powerCap2frequency[3].put(10.2, 1400000);
		powerCap2frequency[3].put(11.5, 1600000);
		powerCap2frequency[3].put(13.0, 1800000);
		powerCap2frequency[3].put(14.7, 2000000);
		powerCap2frequency[3].put(15.6, 2100000);
		powerCap2frequency[3].put(17.6, 2300000);
		powerCap2frequency[3].put(20.5, 2500000);
		powerCap2frequency[3].put(23.0, 2700000);
		powerCap2frequency[3].put(25.4, 2900000);
		powerCap2frequency[3].put(28.3, 3100000);
		powerCap2frequency[3].put(31.0, 3300000);
		powerCap2frequency[3].put(34.2, 3500000);

		frequencies = powerCap2frequency[0].values().toIntArray();
		Arrays.sort(frequencies);
	}

	protected double maxPowerCap;
	protected double minPowerCap;
	protected double powerCap;

	private int currentMaxFrequency;
	private int activeCores;

	Intel_i7_4770K(ShardServer server) {

		super(server, NUMCORES);

		this.maxPowerCap = powerCap2frequency[powerCap2frequency.length - 1].lastDoubleKey();
		this.minPowerCap = powerCap2frequency[powerCap2frequency.length - 1].firstDoubleKey(); // we guarantee that all cores can run, at least at min freq
		this.powerCap = maxPowerCap;

		this.currentMaxFrequency = getMinFrequency();
		this.activeCores = 0;
	}

	protected Core getNewCoreInstance(CPU cpu) {

		return new Intel_i7_4770K_Core(this);
	}

	@Override
	protected void update(long timeMicroseconds) {

		int currentMaxFrequency = getCurrentMaxFrequency();
		int activeCores = activeCores();
		
		if (timeMicroseconds != statusChangeTime) {
			
			String out = String.format("[cpu@%d:%d] %d %d %d %.3f", 
					server.getIndexReplica().getId(), server.getId(),
					this.currentMaxFrequency, this.activeCores,
					this.statusChangeTime, (timeMicroseconds - this.statusChangeTime) / 1000000.0);
			
			((EemDwsSimulator)  server.getSimulator()).println(out);
		}
		
		// new status
		this.statusChangeTime = timeMicroseconds;
		this.activeCores = activeCores;
		this.currentMaxFrequency = currentMaxFrequency;

	}

	@Override
	public void shutdown(long timeMicroseconds) {

		// it basically forces a last print of the stats
		update(timeMicroseconds);

	}

	int getCurrentMaxFrequency() {

		int currentMaxFrequency = getMinFrequency();

		int activeCores = activeCores();
		Double2IntSortedMap map = powerCap2frequency[Math.max(0, activeCores - 1)];
		Double2IntSortedMap headMap = map.headMap(powerCap + 0.00001);
		double ldk = 0.0;
		switch (headMap.size()) {
		case 0:
			ldk = map.firstDoubleKey();
			break;
		case 1:
			ldk = headMap.firstDoubleKey();
			break;
		default:
			ldk = headMap.lastDoubleKey();
			break;
		}
		final int maxPossibleFrequency = map.get(ldk);

		for (Core c : cores) {

			if (c.isBusy())
				currentMaxFrequency = Math.max(currentMaxFrequency,
						Math.min(((Intel_i7_4770K_Core) c).currentFrequency, maxPossibleFrequency));

		}
		return currentMaxFrequency;
	}

	int getFrequency(int frequency) {

		int activeCores = activeCores();
		Double2IntSortedMap map = powerCap2frequency[Math.max(0, activeCores - 1)];
		double ldk = map.headMap(powerCap + 0.00001).lastDoubleKey();
		return Math.min(frequency, map.get(ldk));

	}

	@Override
	public int getMinFrequency() {

		return frequencies[0];
	}

	@Override
	public int getMaxFrequency() {

		return frequencies[frequencies.length - 1];
	}

	@Override
	public int[] getFrequencies() {

		return frequencies;
	}

	@Override
	public double getPowerCap() {

		return powerCap;
	}

	@Override
	public void setMaxPowerCap(long timeMicroseconds) {

		setPowerCap(maxPowerCap, timeMicroseconds);
	}

	@Override
	public void setPowerCap(double powerCap, long timeMicroseconds) {

		double prevPowerCap = this.powerCap;
		if (powerCap < minPowerCap)
			powerCap = minPowerCap;
		else if (powerCap > maxPowerCap)
			powerCap = maxPowerCap;
		this.powerCap = powerCap;
		if (this.powerCap != prevPowerCap)
			update(timeMicroseconds);
	}

}
