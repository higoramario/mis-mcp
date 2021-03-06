package br.usp.each.j2gxl.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.usp.each.j2gxl.JavaClass2GXL;
import br.usp.each.j2gxl.information.Information;

/**
 * Receives command line arguments to generate DefUse informations 
 * 
 * @author Felipe Albuquerque
 */
public class DefUse {
	
	private static final Logger logger = LoggerFactory.getLogger(DefUse.class);
	private static final Options options;
	private static final HelpFormatter formatter;
	static {
		@SuppressWarnings("static-access")
		Option path = OptionBuilder.withArgName("path")
								   .withLongOpt("file")
								   .hasArg()
								   .withDescription("path where files to read are located " +
								   		"( may be a .class file, .jar file, .zip file or a directory whith classes)")
								   	.isRequired()
								   .create("f");
		@SuppressWarnings("static-access")
		Option dest = OptionBuilder.withArgName("dir")
								   .withLongOpt("dest")
		  						   .hasArg()
		  						   .withDescription("specifies the directory where the GXL files will be created")
		  						   .isRequired()
		  						   .create("d");
		@SuppressWarnings("static-access")
		Option simple = OptionBuilder.withLongOpt("simple")
		  						   	 .withDescription("creates graphs without call nodes")
		  						   	 .create("s");
		@SuppressWarnings("static-access")
		Option complete = OptionBuilder.withLongOpt("complete")
		  						   	   .withDescription("creates graphs with call nodes")
		  						   	   .create("c");
		options = new Options();
		options.addOption(path);
		options.addOption(dest);
		options.addOption(simple);
		options.addOption(complete);
		formatter = new HelpFormatter();
	}

	/**
	 * Main method. Receives the arguments from the command line, extracts DefUse 
	 * informations from a file and generates GXL files 
	 *  
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		long n = System.nanoTime();
		
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse(options, args);
					
			if (line.hasOption("simple") && line.hasOption("complete")) {
				throw new ParseException("Duplicate graph type definition");
			}
			
			Information defUseType = Information.SIMPLIFIED_DEF_USE;
			
			if (line.hasOption("complete")) {
				defUseType = Information.COMPLETE_DEF_USE;
			}
			
			String dest = line.getOptionValue("dest");
			String file = line.getOptionValue("file");
			
			try {
				new JavaClass2GXL(file)
						.addInformation(defUseType, dest)
						.generateFiles();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			
		} catch (ParseException exp) {
			logger.warn(exp.getMessage());
			formatter.printHelp("AllUses", options);
		}
		
		logger.info("Total time: %f", ((float)(System.nanoTime() - n)) / 600000000);
	}
	
}
