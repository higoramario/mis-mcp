package br.usp.each.inss.bytecode;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.usp.each.inss.Properties;
import br.usp.each.inss.cache.DefUseCache;
import br.usp.each.inss.dfg.GXLFileDFGLoader;
import br.usp.each.inss.executor.Simulator;

public class InSSProbe {

	private static final Simulator simulator;
	
	private static final Logger logger = LoggerFactory.getLogger(InSSProbe.class);
	
	static {
		try {
			Properties p = Properties.getInstance();
			DefUseCache defUseCache = new DefUseCache(new GXLFileDFGLoader(p.getGXLDirectory()));
			
			simulator = p.getSimulator(
					defUseCache,
					p.getInstrumenter(),
					p.getRequirementDetermination());
			
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		} catch (Exception e) {
			throw new Error("InSS Static init error", e);
		}
	}

	private static long nestlevel = Long.MIN_VALUE;

	public static long nest() {
		if(nestlevel == Long.MAX_VALUE)
			throw new Error("nest is too long.");
		
		synchronized (InSSProbe.class) {
			return ++nestlevel;
		}
	}

	public static void probe(String className, int methodId, long invokeId, int nodeId) {
		synchronized (InSSProbe.class) {
			simulator.execute(Thread.currentThread(), className, methodId, invokeId, nodeId);
		}
	}
	
	public static Simulator simulator() {
		return simulator;
	}
	
	private static class ShutdownHook extends Thread {
		
		@Override
		public void run() {
			logger.info("Invoking shutdown hook");
			String filename = System.getProperty("output.file");
			if(filename != null) {
				try {
					simulator.exportRequirements(filename);
				} catch (IOException e) {
					logger.error("Error exporting requirements", e);
				}
			}
		}
		
	}

}
