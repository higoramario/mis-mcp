package br.usp.each.inss.bytecode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
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

import br.jabuti.graph.CFG;
import br.jabuti.graph.CFGNode;
import br.jabuti.graph.CFGSuperNode;
import br.jabuti.instrumenter.ASMInstrumenter;
import br.usp.each.commons.io.Files;
import ch.qos.logback.classic.Level;

public class Instrumenter {
	
	private static final Logger logger = LoggerFactory.getLogger(Instrumenter.class);
	private static final Options options;
	private static final HelpFormatter formatter;
	static {
		Option help = new Option( "help", "print this message" );
		Option debug = new Option( "verbose", "print all log messages" );
		@SuppressWarnings("static-access")
		Option path = OptionBuilder.withArgName("path")
								   .withLongOpt("instrument")
								   .hasArg()
								   .withDescription("path where files to instrument are located " +
								   		"( may be a .class file, .jar file or a directory whith classes)")
								   	.isRequired()
								   .create("i");
		@SuppressWarnings("static-access")
		Option dest = OptionBuilder.withArgName("dir")
								   .withLongOpt("dest")
		  						   .hasArg()
		  						   .withDescription("destination path to put instrumente files")
		  						   .isRequired()
		  						   .create("d");
		options = new Options();
		options.addOption(help);
		options.addOption(debug);
		options.addOption(path);
		options.addOption(dest);
		formatter = new HelpFormatter();
	}
	private final File outputDir;
	
	public Instrumenter(File outputDir) {
		this.outputDir = outputDir;
	}
	
	public static void main(String[] args) {
		long n = System.nanoTime();
		// create the parser
		CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			if(line.hasOption("verbose")) {
				ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.DEBUG);
				logger.debug("---VERBOSE MODE---");
			}
			File file = new File(line.getOptionValue("instrument"));
			File dest = new File(line.getOptionValue("dest"));
			if(!dest.isDirectory()) {
				throw new ParseException("destination may by a valid directory.");
			}
			Instrumenter i = new Instrumenter(dest);
			if(file.isDirectory()) {
				// directory
				List<File> files = Files.listRecursive(file, new FilenameFilter() {			
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".class");
					}
				});
				logger.info("Found {} files .class", files.size());
				for (File f : files) {
					i.instrument(f);
				}
			} else if (file.isFile()) {
				if(file.getName().endsWith(".class")) {
					// .class
					i.instrument(file);
				} else if (file.getName().endsWith(".jar")) {
					// .jar
					try {
						ZipFile jarFile = new JarFile(file);
						Enumeration<? extends ZipEntry> entries = jarFile.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							if (entry.getName().endsWith(".class"))
								i.instrument(jarFile.getInputStream(entry), entry.getName());
						}
					} catch (IOException e) {
						logger.error("Error reading jar file");
					}
					
				} else {
					throw new ParseException("instrument <path> may by a may be a .class file, " +
							".jar file or a valid directory whith some classes.");
				}
			} else {
				throw new ParseException("instrument <path> may by a may be a .class file, "
								+ ".jar file or a valid directory whith some classes.");
			}			
		} catch (ParseException exp) {
			// oops, something went wrong
			logger.info("Failed!  Reason: " + exp.getMessage());
			formatter.printHelp("Instrumenter", options);
		}
		float t = ((float)(System.nanoTime() - n))/600000000;
		logger.info("Total time: {}", t);
	}
	
	public static JavaClass getJavaClass(File f) throws ClassFormatException, IOException {
		return new ClassParser(f.getAbsolutePath()).parse();
	}
	
	public static JavaClass getJavaClass(InputStream is, String filePath) throws ClassFormatException, IOException {
		return new ClassParser(is, filePath).parse();
	}
	
	public void instrument(File file) {
		logger.debug("Trying to instrument file {}", file);
		try {
			JavaClass javaClass = getJavaClass(file);
			if (!isInstrumented(javaClass)) {
				javaClass = instrument(javaClass);
				write(file.getName(), javaClass);
			} else {
				logger.debug("{} has was instrumented.", file);
			}
		} catch (ClassFormatException e) {
			logger.error("File {} is not valid java bytecode class.", file);
		} catch (IOException e) {
			logger.error("Error reading file {}", file);
		}
	}
	
	public void instrument(InputStream is, String filePath) {
		logger.debug("Trying to instrument file {}", filePath);
		try {
			JavaClass javaClass = getJavaClass(is, filePath);
			if (!isInstrumented(javaClass)) {
				javaClass = instrument(javaClass);
				int i  = filePath.lastIndexOf('/');
				if(i > 0)
					write(filePath.substring(i), javaClass);
				else
					write(filePath, javaClass);
			} else {
				logger.debug("{} has was instrumented.", filePath);
			}
		} catch (ClassFormatException e) {
			logger.error("File {} is not valid java bytecode class.", filePath);
		} catch (IOException e) {
			logger.error("Error reading file {}", filePath);
		}
	}
	
	private void write(String fileName, JavaClass javaClass) {
		File dir = new File(outputDir, javaClass.getPackageName().replace('.', '/'));
		dir.mkdirs();
		File file = new File(dir, fileName);
		logger.debug("Trying to save class in {}", file);
		OutputStream out = null;
		try {
			if(file.createNewFile()) {
				out = new FileOutputStream(file);
				out.write(javaClass.getBytes());
			}
		} catch (FileNotFoundException e) {
			logger.error("Exception:{}", e.getMessage());
		} catch (IOException e) {
			logger.error("Exception:{}", e.getMessage());
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error("Exception:{}", e.getMessage());
				}
			}
		}
	}
	
	public JavaClass instrument(JavaClass javaClass) {
		try {
			long n = System.nanoTime();
			JavaClass j = new ClassProber(javaClass).instrument();
			float t = ((float)(System.nanoTime() - n))/600000000;
			logger.debug("{} was instrumented in: {}", javaClass.getClassName(), t);
			return j;
		} catch (ClassInstrumentationException e) {
			logger.debug(e.getMessage());
			return javaClass;
		}
	}
	
	public boolean isInstrumented(JavaClass javaClass) {
        Attribute[] attributes = javaClass.getAttributes();
        for (Attribute attr : attributes) {
			if(attr instanceof Unknown) {
				Unknown unknown = (Unknown) attr;
				byte[] bytes = unknown.getBytes();
				if(Arrays.equals(bytes, ClassProber.DEFAULT_INSTR_ATTRIBUTE.getBytes())) {
        			return true;
				}
			}
		}
        return false;
	}
	
	private static class ClassProber {
		
		public static String DEFAULT_INSTR_ATTRIBUTE = "InSS Default Instrumented";
       
		private ClassGen classGen;				 
        private ConstantPoolGen poolGen;
        
        public ClassProber(JavaClass javaClass) throws ClassInstrumentationException {
        	if(javaClass.isInterface()) {
        		throw new ClassInstrumentationException("Does not instrument interfaces.");
        	}
			classGen = new ClassGen(javaClass);
			poolGen = classGen.getConstantPool();
		}
        
        public JavaClass instrument() {
        	Method[] methods = classGen.getMethods();
        	for (int i = 0; i < methods.length; i++) {
				try {
					Method method = new MethodProber(i).instrument();
					methods[i] = method;
				} catch (MethodInstrumentationException e) {
					logger.debug(e.getMessage());
				}
			}
            int newIndex = poolGen.addUtf8(DEFAULT_INSTR_ATTRIBUTE);
            Attribute atr = new Unknown(newIndex, 
            		DEFAULT_INSTR_ATTRIBUTE.length(),
            		DEFAULT_INSTR_ATTRIBUTE.getBytes(),
            		poolGen.getConstantPool());
            classGen.setConstantPool(poolGen);
            classGen.addAttribute(atr);
            classGen.setMethods(methods);
            return classGen.getJavaClass();
        }
        
    	private class MethodProber {
    		
    		private int methodId;
			private MethodGen methodGen;
			private InstructionList instructionList;
			private ASMInstrumenter asm;
			private CFG cfg;
			
            private InstructionHandle[] ihVec;
            private int[] ihOffset;

			public MethodProber(int id) throws MethodInstrumentationException {
				Method method = classGen.getMethodAt(id);
				if (method.getName().equals("<clinit>")) {
					throw new MethodInstrumentationException("Does not instrument static initializations.");
				} else if (method.isAbstract()) {
					throw new MethodInstrumentationException("Does not instrument abstract methods.");
				}
				methodId = id;
				methodGen = new MethodGen(method, classGen.getClassName(), poolGen);
				instructionList = methodGen.getInstructionList();
				asm = new ASMInstrumenter(methodGen, classGen, poolGen);
				
				ihVec = instructionList.getInstructionHandles();
				ihOffset = instructionList.getInstructionPositions();
				try {
					cfg = new CFG(methodGen, classGen, CFG.NO_CALL_NODE);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			public Method instrument() {
				if(methodGen.getName().equals("<init>")) {
					instrumentConstructor();
				} else {
					instrumentDefault();
				}
				int maxStack = methodGen.getMaxStack();
                methodGen.setMaxStack(maxStack + 5);
				ConstantPoolGen p = methodGen.getConstantPool();
				for (Attribute atr : methodGen.getCodeAttributes()) {
					int i = atr.getNameIndex();
					Constant c = p.getConstant(i);
					String s = ((ConstantUtf8) c).getBytes();
					if (s.equals("LocalVariableTypeTable")) {
						methodGen.removeCodeAttribute(atr);
					}
				}
				return methodGen.getMethod();
			}    		
    		
    		private void instrumentConstructor() {
    			LocalVariableGen localVarGen = methodGen.addLocalVariable("nest", Type.LONG, null, null);
    			int nest = localVarGen.getIndex();
                CFGNode node = null;
                for (int i = 0; i < cfg.size(); i++) {
                    node = (CFGNode) cfg.elementAt(i);
                    if (node instanceof CFGSuperNode)
                        break;
                }
                cfg.findIDFT(false, node);
                InstructionFactory factory = new InstructionFactory(classGen, poolGen);
                InstructionList instructions = new InstructionList();
                instructions.append(factory.createInvoke(
                		InSSProbe.class.getName(),
                		"nest",
                		Type.LONG,
                		Type.NO_ARGS,
                		Constants.INVOKESTATIC ));
                instructions.append(InstructionFactory.createStore(Type.LONG, nest));
                instructions.append(factory.createConstant(classGen.getClassName()));
                instructions.append(factory.createConstant(methodId));
                instructions.append(InstructionFactory.createLoad(Type.LONG, nest));
                instructions.append(factory.createConstant(node.getNumber()));
                instructions.append(factory.createInvoke(
                		InSSProbe.class.getName(),
                		"probe",
                		Type.VOID,
                		new Type[] { Type.STRING, Type.INT, Type.LONG, Type.INT },
                		Constants.INVOKESTATIC ));          
				InstructionHandle ih = InstructionList.findHandle(ihVec, ihOffset, ihOffset.length, node.getEnd());
                asm.insertAfter(ih,instructions);
                for (int i = 0; i < cfg.size(); i++) {
                    node = (CFGNode) cfg.elementAt(i);
                    // if marked means it is before the super
                    if (node.getMark()) {
                        continue;
                    }
                    instructions = new InstructionList();
                    instructions.append(factory.createConstant(classGen.getClassName()));
                    instructions.append(factory.createConstant(methodId));
                    instructions.append(InstructionFactory.createLoad(Type.LONG, nest));
                    instructions.append(factory.createConstant(node.getNumber()));
                    instructions.append(factory.createInvoke(
                    		InSSProbe.class.getName(),
                    		"probe",
                    		Type.VOID,
                    		new Type[] { Type.STRING, Type.INT, Type.LONG, Type.INT },
                    		Constants.INVOKESTATIC ));
					ih = InstructionList.findHandle(ihVec, ihOffset, ihOffset.length, node.getStart());
                    asm.insertBefore(ih, instructions);
                  //methodCallPair
                    if(cfg.isExit(node)) {

                    	ih = InstructionList.findHandle(ihVec, ihOffset, ihOffset.length, node.getEnd());
    					factory = new InstructionFactory(classGen, poolGen);
    					instructions = new InstructionList();
    					instructions.append(factory.createConstant(classGen.getClassName()));
                        instructions.append(factory.createConstant(methodId));
                        instructions.append(InstructionFactory.createLoad(Type.LONG, nest));
                        instructions.append(factory.createConstant(-1));
                        instructions.append(factory.createInvoke(
                        		InSSProbe.class.getName(),
                        		"probe",
                        		Type.VOID,
                        		new Type[] { Type.STRING, Type.INT, Type.LONG, Type.INT },
                        		Constants.INVOKESTATIC ));
                        asm.insertBefore(ih, instructions);
    				}
                    
				}
    		}
    		
    		private void instrumentDefault() {
    			LocalVariableGen localVarGen = methodGen.addLocalVariable("nest", Type.LONG, null, null);
    			int nest = localVarGen.getIndex();
    			for (int i = 0; i < cfg.size(); i++) {
    				CFGNode node = (CFGNode) cfg.elementAt(i);
    				InstructionFactory factory = new InstructionFactory(classGen, poolGen);
    				InstructionList instructions = new InstructionList();
    				InstructionHandle ih = InstructionList.findHandle(ihVec, ihOffset, ihOffset.length, node.getStart());
    				if(cfg.isEntry(node)) {
                        instructions.append(factory.createInvoke(
                        		InSSProbe.class.getName(),
                        		"nest",
                        		Type.LONG,
                        		Type.NO_ARGS,
                        		Constants.INVOKESTATIC ));
                        instructions.append(InstructionFactory.createStore(Type.LONG, nest));
                    	asm.addBefore(ih, instructions);
    				}
    				factory = new InstructionFactory(classGen, poolGen);
    				instructions = new InstructionList();
                    instructions.append(factory.createConstant(classGen.getClassName()));
                    instructions.append(factory.createConstant(methodId));
                    instructions.append(InstructionFactory.createLoad(Type.LONG, nest));
                    instructions.append(factory.createConstant(node.getNumber()));
                    instructions.append(factory.createInvoke(
                    		InSSProbe.class.getName(),
                    		"probe",
                    		Type.VOID,
                    		new Type[] { Type.STRING, Type.INT, Type.LONG, Type.INT },
                    		Constants.INVOKESTATIC ));
                    asm.insertBefore(ih, instructions);
                    //methodCallPair
                    if(cfg.isExit(node)) {

                    	ih = InstructionList.findHandle(ihVec, ihOffset, ihOffset.length, node.getEnd());
    					factory = new InstructionFactory(classGen, poolGen);
    					instructions = new InstructionList();
    					instructions.append(factory.createConstant(classGen.getClassName()));
                        instructions.append(factory.createConstant(methodId));
                        instructions.append(InstructionFactory.createLoad(Type.LONG, nest));
                        instructions.append(factory.createConstant(-1));
                        instructions.append(factory.createInvoke(
                        		InSSProbe.class.getName(),
                        		"probe",
                        		Type.VOID,
                        		new Type[] { Type.STRING, Type.INT, Type.LONG, Type.INT },
                        		Constants.INVOKESTATIC ));
                        asm.insertBefore(ih, instructions);
    				}
                    
				}
    		}
    	}
	}	
}
