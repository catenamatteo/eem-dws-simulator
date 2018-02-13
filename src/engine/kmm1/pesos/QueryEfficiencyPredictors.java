package engine.kmm1.pesos;

import cpu.regression.CPUModel;

public class QueryEfficiencyPredictors {
	
	private CPUModel cpuModel;
	
	public QueryEfficiencyPredictors(CPUModel cpuModel) {
		
		this.cpuModel = cpuModel;
		
	}
	
	public double regress(int numOfTerms, int postings, int frequency) {
		
		return cpuModel.regress(numOfTerms, postings, frequency);
	}
	
}
