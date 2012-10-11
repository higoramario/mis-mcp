package br.usp.each.inss;

import java.io.File;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.usp.each.inss.cache.DefUseCache;
import br.usp.each.inss.executor.CloneSimulator;
import br.usp.each.inss.executor.Simulator;
import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.inss.instrumentation.traverse.DefaultProbeTraverseStrategy;
import br.usp.each.inss.instrumentation.traverse.ProbeTraverseStrategy;
import br.usp.each.opal.requirement.RequirementDetermination;

public final class Properties {
	
	private static final Logger logger = LoggerFactory.getLogger(Properties.class);

	private static final Properties instance = new Properties();
	
	public static Properties getInstance() {
		return instance;
	}
	
	private final File workingDirectory = new File(System.getProperty("user.dir"));

	private File gxlDirectory = workingDirectory;
	
	private Class<Instrumentator> instrumenterClass;

	private Class<Simulator> simulatorClass;
	
	private Class<RequirementDetermination> requirementDeterminationClass;
	
	// Private Constructor, Singleton pattern
	private Properties() {
		defineGXLDirectory();
		defineProbeTraverseStrategy();
		defineInstrumenter();
		defineSimulator();
		defineRequirementDetermination();
	}

	private void defineGXLDirectory() {
		String dirname = System.getProperty("gxl.dir");
		
		// Check if is defined. If NOT use working directory
		if (dirname == null) {
			logger.info("gxl.dir not set, using working dir: '{}'", workingDirectory);
			return;
		}
		
		// Check if directory defined exists. If NOT then stop execution
		gxlDirectory = new File(dirname);
		if (!gxlDirectory.exists()) {
			throw new Error(String.format("gxl.dir '%s' does not exists", gxlDirectory));
		}

		logger.info("gxl.dir: '{}'", gxlDirectory);
	}

	private void defineProbeTraverseStrategy() {
		String clazz = System.getProperty("traverse.strategy");

		// Check if is defined. If NOT use default
		if (clazz == null) {
			logger.info("traverse.strategy not set, using {}",
					DefaultProbeTraverseStrategy.class);
			return;
		}
		
		// Try to defined probe traverse strategy. If ERROR then stop execution
		try {
			Program.setTraverseStrategy(
					(ProbeTraverseStrategy) Class.forName(clazz).newInstance());
		} catch (Exception e) {
			throw new Error("Error defining traverse.strategy", e);
		}

		logger.info("traverse.strategy: {}", clazz);
	}
	

	@SuppressWarnings("unchecked")
	private void defineInstrumenter() {
		String clazz = System.getProperty("instrumentation.strategy");
		
		// Check if is defined. If NOT then stop execution
		if(clazz == null) {
			throw new Error("instrumentation.strategy not set");
		} 
		
		// Try to define instrumentation strategy. If ERROR then stop execution
		try {
			instrumenterClass = (Class<Instrumentator>) Class.forName(clazz);
		} catch (Exception e) {
			throw new Error("Error defining instrumentation.strategy", e);
		}
		
		logger.info("instrumentation.strategy: {}", clazz);
	}
	
	@SuppressWarnings("unchecked")
	private void defineSimulator() {
		String clazz = System.getProperty("simulator.strategy");
		
		if(clazz == null) {
			clazz = CloneSimulator.class.getName();
		} 
		
		// Try to define simulator strategy. If ERROR then stop execution
		try {
			simulatorClass = (Class<Simulator>) Class.forName(clazz);
		} catch (Exception e) {
			throw new Error("Error defining simulator.strategy", e);
		}
		
		logger.info("simulator.strategy: {}", clazz);
	}
	
	@SuppressWarnings("unchecked")
	private void defineRequirementDetermination() {
		String clazz = System.getProperty("requirement.determination");
		
		// Check if is defined. If NOT then stop execution
		if(clazz == null) {
			throw new Error("requirement.determination not set");
		}
		
		// Try to define requirement determination. If ERROR then stop execution
		try {
			requirementDeterminationClass = (Class<RequirementDetermination>) Class.forName(clazz);
		} catch (Exception e) {
			throw new Error("Error defining requirement.determination", e);
		}
			
		logger.info("requirement.determination: {}", clazz);
	}
	
	public Instrumentator getInstrumenter() {
		Instrumentator instrumenter = null;
		try {
			instrumenter = instrumenterClass.newInstance();
		} catch (Exception e) {
			throw new Error("Error getting Instrumenter instance", e);
		}
		return instrumenter;
	}
	
	public Simulator getSimulator(DefUseCache c, Instrumentator i, RequirementDetermination d) {
		Simulator simulator = null;
		try {
			Constructor<Simulator> constructor = simulatorClass
					.getConstructor(DefUseCache.class, Instrumentator.class, RequirementDetermination.class);
		simulator = constructor.newInstance(c,i,d);
		} catch (Exception e) {
			throw new Error("Error getting Simulator instance", e);
		}
		return simulator;
	}
	
	public RequirementDetermination getRequirementDetermination() {
		RequirementDetermination instance = null;
		try {
			Constructor<RequirementDetermination> constructor = requirementDeterminationClass.getConstructor(File.class);
			instance = constructor.newInstance(gxlDirectory);
		} catch (NoSuchMethodException e) {
			logger.debug("RequirementDetermination constructor not recive a File argument");
			try {
				logger.debug("Calling RequirementDetermination default constructor");
				instance = requirementDeterminationClass.newInstance();
			} catch (Exception ee) {
				throw new Error("Error getting RequirementDetermination instance", e);
			}
		} catch (Exception e) {
			throw new Error("Error getting RequirementDetermination instance", e);
		}
		return instance;
	}
	
	public File getGXLDirectory() {
		return gxlDirectory;
	}

}
