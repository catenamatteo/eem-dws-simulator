package cpu.impl;

import cpu.CPUModel;

public class Intel_i7_4770K extends CPUModel {

	public Intel_i7_4770K() {

		super("Intel i7-4770K", 4,
				new int[] { 800000, 1000000, 1200000, 1400000, 1600000, 1800000, 2000000, 2100000, 2300000, 2500000,
						2700000, 2900000, 3100000, 3300000, 3500000 },
				new double[] { 4.6, 5.7, 8.7, 10.2, 11.5, 13.0, 14.7, 15.6, 17.6, 20.5, 23.0, 25.4, 28.3, 31.0, 34.2 });

	}

}
