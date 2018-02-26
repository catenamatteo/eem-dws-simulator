package util;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import eu.nicecode.simulator.Simulator;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;

public class RollingStatistics {

	protected Simulator simulator;
	protected Long2ObjectSortedMap<DoubleList> sec2vals;
	protected DoubleList vals;
	protected long timeInSec;
	protected long windowInSec;
	protected boolean sorted;
	
	public RollingStatistics(Simulator simulator, long windowInSec) {
		
		this.simulator = simulator;
		sec2vals = new Long2ObjectAVLTreeMap<>();
		vals = new DoubleArrayList();
		this.timeInSec = getTimeInSec();
		this.windowInSec = windowInSec;
		sorted = false;
	}
	
	private long updateTime() {

		long callTimeInSec = getTimeInSec();
		if (callTimeInSec != timeInSec && callTimeInSec - timeInSec > windowInSec) {
			
			sec2vals = new Long2ObjectAVLTreeMap<>(sec2vals.tailMap(callTimeInSec - windowInSec));
			vals.clear();
			for (DoubleList l : sec2vals.values()) vals.addAll(l); //TODO: can we improve this if we keep buckets sorted and then we merge ala mergesort?		
			sorted = false;
		}
		
		return timeInSec = callTimeInSec;
		
	}
	
	public void add(double val) {
		

		long callTimeInSec = updateTime();
		
		if (!sec2vals.containsKey(callTimeInSec))
			sec2vals.put(callTimeInSec, new DoubleArrayList());
		sec2vals.get(callTimeInSec).add(val);
		vals.add(val);
		sorted = false;
		
	}
	
	public double getPercentile(double index) {

		updateTime();		
		
		if (vals.isEmpty()) {
			
			return 0;
			
		} else {
			
			sort();
			
			int idx = ((int) Math.ceil((index / 100.0) * vals.size())) - 1;
			return vals.getDouble(idx);
			
		}	
	}
	
	private void sort() {

		if (!sorted) {
			Collections.sort(vals);
			sorted=true;
		}
		
	}
	
	private long getTimeInSec() {
		
		return TimeUnit.MICROSECONDS.toSeconds(simulator.now().getTimeMicroseconds());
	}
	
	
}
