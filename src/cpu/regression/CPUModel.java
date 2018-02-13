package cpu.regression;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

class Regressor {

	private double a,b,c;

	public Regressor(double a, double b, double c) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public double regress(int x) {
		
		return (a * x) + b + c;
		
	}	
}

public class CPUModel extends cpu.CPUModel {

	protected Int2ObjectMap<Int2ObjectMap<Regressor>> regressorsMap;
	
	public CPUModel(String name, int numCores, String regressionFile, int[] frequencies, double[] powers) {
		super(name, numCores, frequencies, powers);

		Properties p = new Properties();
		try {			
			p.load(new FileReader(regressionFile));
			int queryClasses = Integer.parseInt(p.getProperty("query.classes"));
			
			regressorsMap = new Int2ObjectArrayMap<>(queryClasses);
			
			for (int i = 1; i<=queryClasses; i++) {			
				
				regressorsMap.put(i, new Int2ObjectArrayMap<>(frequencies.length));
				
				for (int f : frequencies) {
					
					double a = Double.parseDouble(p.getProperty(f+"."+i+".alpha"));
					double b = Double.parseDouble(p.getProperty(f+"."+i+".beta"));
					double c = Double.parseDouble(p.getProperty(f+"."+i+".rmse"));
					
					regressorsMap.get(i).put(f, new Regressor(a, b, c));
				}			
			}				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public double regress(int numOfTerms, int postings, int frequency) {
		
		Regressor regressor = regressorsMap.get(numOfTerms).get(frequency);
		if (regressor != null) {
			
			return regressor.regress(postings);
			
		} else {
			
			return Double.MAX_VALUE;
		}		
	}
}
