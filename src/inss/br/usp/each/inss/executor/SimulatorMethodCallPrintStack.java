package br.usp.each.inss.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import br.usp.each.inss.Program;
import br.usp.each.inss.cache.DefUseCache;
import br.usp.each.inss.cache.Requirements;
import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.requirement.RequirementDetermination;

public class SimulatorMethodCallPrintStack implements Simulator {

	private final static Program SENTINELLA = new Program(Long.MIN_VALUE, new DFGraph("Sentinella", 0));
	private final Map<Thread, Deque<Program>> stacks = new HashMap<Thread, Deque<Program>>();
	private final DefUseCache cache;
	private final Instrumentator instrumentator;
	private final RequirementDetermination determination;
	private final Requirements requirements = new Requirements();

	private static Map<Long,List<String>> mapStack = new HashMap<Long,List<String>>();
	private static List<String> stackPrint = new ArrayList<String>();
	private static Map<Long,Integer> mapCountTab = new HashMap<Long,Integer>();
	int countTab = 0;
	
	private static List<Thread> listThread = new ArrayList<Thread>();

	public SimulatorMethodCallPrintStack(DefUseCache cache, Instrumentator instrumentator, RequirementDetermination determination) {
		this.cache = cache;
		this.instrumentator = instrumentator;
		this.determination = determination;
		stackPrint.add("--> Stack of method calls <--");
	}

	@Override
	public void execute(Thread thread, String clazz, int method, long invoke, int nodeId) {

		// Get current thread stack
		Deque<Program> stack = stacks.get(thread);
		if(stack == null) {
			// Thread stack not found... lets create a new one
			stacks.put(thread, stack = new LinkedList<Program>());
			stack.push(SENTINELLA);
		}

		Program p = stack.getFirst();

		// Means that the first node
		if(invoke > p.getId()) {
			//topo chama novo p
			DFGraph graph = cache.get(clazz, method);
			p = new Program(invoke, graph);
			stack.addFirst(p);

/*			if(!listThread.contains(thread))
			{
				listThread.add(thread);
			}
*/			
			
			if(!mapStack.containsKey(thread.getId()))
			{
				stackPrint = new ArrayList<String>();
				countTab = 0;
			}
			else
			{
				stackPrint = mapStack.remove(thread.getId());
				countTab = mapCountTab.remove(thread.getId());
			}
			try {
				stackPrint.add("--> "+tab()+"IN: " + clazz + ", method: " + method + " - " + getMethodName(clazz,method) + ", thread: " + thread.getId() + ", invoke: " + invoke);
			} catch (ClassFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mapStack.put(thread.getId(), stackPrint);
			countTab++;
			mapCountTab.put(thread.getId(), countTab);
		}
		else {
			while(stack.getFirst().getId() > invoke)
				stack.removeFirst();
			p = stack.getFirst();
		}

		if(nodeId == -1){
			countTab = mapCountTab.remove(thread.getId());
			countTab--;
			stackPrint = mapStack.remove(thread.getId());
			try {
				stackPrint.add("<-- "+tab()+"OUT: " + clazz + ", method: " + method + " - " + getMethodName(clazz,method) + ", thread: " + thread.getId() + ", invoke: " + invoke);
			} catch (ClassFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mapStack.put(thread.getId(), stackPrint);
			mapCountTab.put(thread.getId(), countTab);
		}

	}

	@SuppressWarnings("deprecation")
	public void verifyThread() throws InterruptedException
	{
		if(listThread.size() > 1)
		{
			Iterator<Thread> itThread = listThread.iterator();
			if(itThread.hasNext())
			{
				Thread thread = itThread.next();
				if(thread.isAlive())
				{
					thread.join(1000);
				}
			}
		}
	}
	
	@Override
	public void exportRequirements(String outputFile) throws IOException {
		BufferedWriter fl = new BufferedWriter(new FileWriter(outputFile));

		Set<Long> setThreadId = mapStack.keySet();
		Iterator<Long> itThreadId = setThreadId.iterator();

		while(itThreadId.hasNext())
		{
			Long threadId = itThreadId.next();
			stackPrint = mapStack.get(threadId);
			Iterator<String> it = stackPrint.iterator();
			while(it.hasNext())
			{
				fl.write(it.next());
				fl.write("\n");
			}
		}

		fl.close();
	}

	@Override
	public Requirements requirements() {
		return requirements;
	}

	public String tab()
	{
		String tab = "";
		for(int i = 0; i < countTab; i++)
		{
			tab += "\t";
		}
		return tab;
	}

	
	public void reset()
	{
		mapStack = new HashMap<Long,List<String>>();
		stackPrint = new ArrayList<String>();
		stackPrint.add("--> Stack of method calls <--");
		mapCountTab = new HashMap<Long,Integer>();
		countTab = 0;
	}

	public String getMethodName(String clazz, int methodId) throws ClassFormatException, IOException
	{
		String classesDirectory = System.getProperty("user.dir")+"/build/classes";//to ant and xml-security

		File f = new File(classesDirectory, clazz.replace('.', '/') + ".class");
		JavaClass javaClass = new ClassParser(f.getAbsolutePath()).parse();
		javaClass.getSourceFileName();
		Method method = javaClass.getMethods()[methodId];
		String signature = formatSignature(method.getSignature());

		return method.getName()+signature;

	}

	//Adjust the signature of methods
    public String formatSignature(String strSignature)
    {
    	String sigTemp = strSignature;
    	String signature = "";

    	sigTemp = sigTemp.substring(sigTemp.indexOf("(")+1, sigTemp.indexOf(")"));

    	while(!sigTemp.isEmpty())
    	{
    		boolean isArray = false;
        	if(sigTemp.startsWith("["))
    		{
    			isArray = true;
    			sigTemp = sigTemp.substring(1);
    		}
    		if(sigTemp.startsWith("L"))
    		{
    			String strSig = sigTemp.substring(0,sigTemp.indexOf(";"));
    			signature = signature + strSig.substring(strSig.lastIndexOf("/")+1, strSig.length()) + (!isArray ? "," : "[],");
    			sigTemp = sigTemp.substring(sigTemp.indexOf(";")+1);
    		}
    		else
    		{
    			signature = signature + getPrimitiveType(sigTemp.charAt(0)) + (!isArray ? "," : "[],");
    			if(sigTemp.length() > 1){
    				sigTemp = sigTemp.substring(1);
    			}
    			else{
    				sigTemp = "";
    			}
    		}
    	}

    	if(!signature.isEmpty())
    	{
    		signature = signature.substring(0, signature.lastIndexOf(","));
    	}

    	signature = "("+signature+")";

    	return signature;
    }

    public String getPrimitiveType(char cType)
    {
    	if(cType == 'I')
    	{
    		return "int";
    	}
    	if(cType == 'D')
    	{
    		return "double";
    	}
    	if(cType == 'F')
    	{
    		return "float";
    	}
    	if(cType == 'Z')
    	{
    		return "boolean";
    	}
    	if(cType == 'B')
    	{
    		return "byte";
    	}
    	if(cType == 'C')
    	{
    		return "char";
    	}
    	if(cType == 'S')
    	{
    		return "short";
    	}
    	if(cType == 'J')
    	{
    		return "long";
    	}
    	return "";
    }

}
