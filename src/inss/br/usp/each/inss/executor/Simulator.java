package br.usp.each.inss.executor;

import java.io.IOException;

import br.usp.each.inss.cache.Requirements;

public interface Simulator {
	
	void execute(Thread thread, String className, int methodId, long invokeId, int nodeId);

	void exportRequirements(String outputFile) throws IOException;
	
	void exportTestInformation(String outputFile, String message) throws IOException;
	
	Requirements requirements();

}
