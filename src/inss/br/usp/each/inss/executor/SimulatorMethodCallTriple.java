package br.usp.each.inss.executor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import br.usp.each.inss.Program;
import br.usp.each.inss.cache.DefUseCache;
import br.usp.each.inss.cache.MethodCallTripleRequirements;
import br.usp.each.inss.cache.Requirements;
import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.requirement.MethodCallTriple;
import br.usp.each.opal.requirement.RequirementDetermination;

public class SimulatorMethodCallTriple implements Simulator {

	private final static Program SENTINELLA = new Program(Long.MIN_VALUE, new DFGraph("Sentinella", 0));
	private final Map<Thread, Deque<Program>> stacks = new HashMap<Thread, Deque<Program>>();
	private final DefUseCache cache;
	private final Instrumentator instrumentator;
	private final RequirementDetermination determination;
	private final Requirements requirements = new Requirements();

	private MethodCallTripleRequirements methodCallRequirements = new MethodCallTripleRequirements();
	private static List<MethodCallTriple> listMethodCallTriple = new ArrayList<MethodCallTriple>();
	private static Stack<Integer> stackMethod = new Stack<Integer>();
	private static Stack<String> stackClass = new Stack<String>();
	private final String SENTINELLA_CLASS = "0.TestCase.Sentinella";


	public SimulatorMethodCallTriple(DefUseCache cache, Instrumentator instrumentator, RequirementDetermination determination) {
		this.cache = cache;
		this.instrumentator = instrumentator;
		this.determination = determination;
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
			if(clazz.contains("org.apache.tools.ant.taskdefs.ExecuteWatchdog"))
			System.out.println("Enter method:" + method + " class: " + clazz);
			stackMethod.push(method);
			stackClass.push(clazz);

		} else {
			while(stack.getFirst().getId() > invoke)
				stack.removeFirst();
			p = stack.getFirst();
		}

		if(nodeId == -1){
			if(clazz.contains("org.apache.tools.ant.taskdefs.ExecuteWatchdog"))
				System.out.println("Exit method:" + method + " class: " + clazz);

			int curMethod = 0;
			String curClass = "";

			if(!stackMethod.empty())
			{
				curMethod = stackMethod.pop();
				curClass = stackClass.pop();
			}

			if(!stackMethod.empty())
			{
				int stackSize = stackMethod.size();
				MethodCallTriple metTriple;
				if(stackSize >= 2)
				{
					metTriple = new MethodCallTriple(stackMethod.get(stackSize-2),stackMethod.lastElement(),curMethod,
													 stackClass.get(stackSize-2),stackClass.lastElement(),curClass);
				}
				else if(stackSize == 1){
					metTriple = new MethodCallTriple(0,stackMethod.lastElement(),curMethod,
													SENTINELLA_CLASS,stackClass.lastElement(),curClass);
				}
				else{
					metTriple = new MethodCallTriple(0,0,curMethod,SENTINELLA_CLASS,SENTINELLA_CLASS,curClass);
				}
				if(!isInList(metTriple)){
					listMethodCallTriple.add(metTriple);
				}
			}
			else{
				MethodCallTriple metTriple = new MethodCallTriple(0,0,curMethod,SENTINELLA_CLASS,SENTINELLA_CLASS,curClass);

				if(!isInList(metTriple)){
					listMethodCallTriple.add(metTriple);
				}
			}
		}

	}


	@Override
	public void exportRequirements(String outputFile) throws IOException {

		FileOutputStream fileOut = new FileOutputStream(outputFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);

		System.out.println("exporting MethodCallTriples, size: " + listMethodCallTriple.size());

		checkStack(outputFile);
		//printMethodCallTriples();
		methodCallRequirements.putAll(listMethodCallTriple);
		out.writeObject(methodCallRequirements);
		out.close();
		fileOut.close();
	}

	@Override
	public Requirements requirements() {
		return requirements;
	}

	public MethodCallTripleRequirements methodCallRequirements() {
		return methodCallRequirements;
	}

	private boolean isInList(MethodCallTriple metTriple)
	{
		for(MethodCallTriple mct : listMethodCallTriple)
		{
			if(mct.getClassCaller().contains(metTriple.getClassCaller()) && mct.getClassCalledN1().contains(metTriple.getClassCalledN1()) &&
					mct.getClassCalledN2().contains(metTriple.getClassCalledN2()) & mct.getIdMethodCaller() == metTriple.getIdMethodCaller() &&
					mct.getIdMethodCalledN1() == metTriple.getIdMethodCalledN1() & mct.getIdMethodCalledN2() == metTriple.getIdMethodCalledN2()){
				//System.out.println("Repeated Pair("+metPair.getIdMethodCaller()+","+metPair.getClassCaller()+","+metPair.getIdMethodCalled()+","+metPair.getClassCalled()+")");
				return true;
			}
		}
		return false;
	}

	public void printMethodCallTriples()
	{
		System.out.println("List of method call triples");

		for(MethodCallTriple method : listMethodCallTriple)
		{
			System.out.println(method.getIdMethodCaller() + "," + method.getClassCaller() + " --> " + method.getIdMethodCalledN1() + "," +
							   method.getClassCalledN1() + " --> " + method.getIdMethodCalledN2() + "," + method.getClassCalledN2());
		}
	}

	public void checkStack(String output)
	{

		if(!stackMethod.empty())
		{
			System.out.println(">>>>>The test method " + output + " does not finished correctly.");
			System.out.println("Saving call methods in list after exception");
		}


		while(!stackMethod.empty())
		{
			int curMethod = stackMethod.pop();
			String curClass = stackClass.pop();

			if(!stackMethod.empty())
			{
				int stackSize = stackMethod.size();
				MethodCallTriple metTriple;

				if(stackSize >= 2)
				{
					System.out.println("Broke-Triple("+stackMethod.get(stackSize-2)+","+stackClass.get(stackSize-2)+","+stackMethod.lastElement()+","+stackClass.lastElement()+","+curMethod+","+curClass);
					metTriple = new MethodCallTriple(stackMethod.get(stackSize-2),stackMethod.lastElement(),curMethod,
													 stackClass.get(stackSize-2),stackClass.lastElement(),curClass);
				}
				else if(stackSize == 1){
					System.out.println("Broke-Triple("+0+","+SENTINELLA_CLASS+","+stackMethod.lastElement()+","+stackClass.lastElement()+","+curMethod+","+curClass);
					metTriple = new MethodCallTriple(0,stackMethod.lastElement(),curMethod,
													SENTINELLA_CLASS,stackClass.lastElement(),curClass);
				}
				else{
					System.out.println("Broke-Triple("+0+","+SENTINELLA_CLASS+","+0+","+SENTINELLA_CLASS+","+curMethod+","+curClass);
					metTriple = new MethodCallTriple(0,0,curMethod,SENTINELLA_CLASS,SENTINELLA_CLASS,curClass);
				}
				if(!isInList(metTriple)){
					listMethodCallTriple.add(metTriple);
				}
			}
			else{
				MethodCallTriple metTriple = new MethodCallTriple(0,0,curMethod,SENTINELLA_CLASS,SENTINELLA_CLASS,curClass);

				if(!isInList(metTriple)){
					listMethodCallTriple.add(metTriple);
				}
			}
			System.out.println("Exit in methodId: " + curMethod + ", class: " + curClass);
			System.out.println("List size: " + listMethodCallTriple.size());
		}
	}


}
