package util;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.nicecode.simulator.Simulator;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;

public class RollingStatistics {

	protected Simulator simulator;
	protected Long2ObjectSortedMap<DoubleList> sec2vals;
	protected List<Double> vals;
	protected long timeInSec;
	protected long windowInSec;
	
	public RollingStatistics(Simulator simulator, long windowInSec) {
		
		this.simulator = simulator;
		sec2vals = new Long2ObjectAVLTreeMap<>();
		vals = new LinkedList<>();
		this.timeInSec = getTimeInSec();
		this.windowInSec = windowInSec;
	}
	
	private long updateTime() {

		long callTimeInSec = getTimeInSec();
		if (callTimeInSec != timeInSec && callTimeInSec - timeInSec > windowInSec) {
			
			sec2vals = new Long2ObjectAVLTreeMap<>(sec2vals.tailMap(callTimeInSec - windowInSec));
			vals.clear();
			for (DoubleList l : sec2vals.values()) vals.addAll(l); //TODO: can we improve this if we keep buckets sorted and then we merge ala mergesort?		
			Collections.sort(vals);
		}
		
		return timeInSec = callTimeInSec;
		
	}
	
	public void add(double val) {
		

		long callTimeInSec = updateTime();
		
		if (!sec2vals.containsKey(callTimeInSec))
			sec2vals.put(callTimeInSec, new DoubleArrayList());
		sec2vals.get(callTimeInSec).add(val);
		int idx = Collections.binarySearch(vals, val);
		if (idx < 0) idx = -idx -1;
		vals.add(idx, val);
		
	}
	
	public double getPercentile(double index) {

		updateTime();		
		
		if (vals.isEmpty()) {
			
			return 0;
			
		} else {
			
			int idx = ((int) Math.ceil((index / 100.0) * vals.size())) - 1;
			return vals.get(idx);
			
		}	
	}
	
	private long getTimeInSec() {
		
		return TimeUnit.MICROSECONDS.toSeconds(simulator.now().getTimeMicroseconds());
	}
	
	
}
