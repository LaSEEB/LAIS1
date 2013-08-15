package org.laseeb.LAIS.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for quickly profiling pieces of code. Does not replace a real
 * profiler, but it's quick and efficient. Only to be used when developing LAIS
 * code, otherwise ignore.
 * <p>
 * This class is thread-safe.
 * 
 * @author Nuno Fachada
 */
public class QuickProfiler {
	
	/* We keep cumulative execution times here (for all start-ends for a given key). */
	private static HashMap<String, ArrayList<Long>> map = new HashMap<String, ArrayList<Long>>();
	
	/* This is used for an each unique start and end. */
	private static HashMap<String, Long> auxMap = new HashMap<String, Long>();
	
	/**
	 * Clear current profiling data.
	 */
	public static void clear() {
		map.clear();
		auxMap.clear();
	}
	
	/**
	 * Start profiling a piece of code.
	 * 
	 * @param key Unique identifier for the piece of code under profiling.
	 */
	public static synchronized void start(String key) {
		auxMap.put(key + Thread.currentThread().getId(), System.currentTimeMillis());
	}
	
	/**
	 * End profiling a piece of code.
	 * 
	 * @param key Unique identifier for the piece of code under profiling.
	 */
	public static synchronized void end(String key) {
		Long start = auxMap.get(key + Thread.currentThread().getId());
		if (start == null)
			throw new RuntimeException("Profiling 'end'->'" + key + "' does not have the respective 'start'");
		addDuration(key, System.currentTimeMillis() - start);
	}
	
	/* Add a given duration to the given key. */
	private static void addDuration(String key, Long duration) {
		if (map.containsKey(key)) {
			map.get(key).add(duration);
		} else {
			ArrayList<Long> list = new ArrayList<Long>();
			list.add(duration);
			map.put(key, list);
		}
	}
	
	/**
	 * Returns a string with the cumulative durations of each piece of code identified by
	 * a key.
	 * 
	 * @return A string with the cumulative durations of each piece of code identified by
	 * a key.
	 */
	public static String getDurations() {
		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			long min = Long.MAX_VALUE, max = 0l, total = 0l;
			ArrayList<Long> list = map.get(key);
			for (long value : list) {
				if (value > max)
					max = value;
				if (value < min)
					min = value;
				total += value; 
			}
			sb.append("[" + key + "]: " + total + "(max: " + max + "; min: " + min + "; avg: " + ((double) total)/((double) list.size()) +")\n");
		}
		sb.append("\n");
		return sb.toString();
	}

}
