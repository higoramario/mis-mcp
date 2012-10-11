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
import br.usp.each.inss.cache.MethodCallPairRequirements;
import br.usp.each.inss.cache.Requirements;
import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.requirement.MethodCallPair;
import br.usp.each.opal.requirement.RequirementDetermination;

public class SimulatorMethodCallPair implements Simulator {

	private final static Program SENTINELLA = new Program(Long.MIN_VALUE, new DFGraph("Sentinella", 0));
	private final Map<Thread, Deque<Program>> stacks = new HashMap<Thread, Deque<Program>>();
	private final DefUseCache cache;
	private final Instrumentator instrumentator;
	private final RequirementDetermination determination;
	private final Requirements requirements = new Requirements();

	private MethodCallPairRequirements methodCallRequirements = new MethodCallPairRequirements();
	private static List<MethodCallPair> listMethodCallPair = new ArrayList<MethodCallPair>();
	private static Stack<Integer> stackMethod = new Stack<Integer>();
	private static Stack<String> stackClass = new Stack<String>();

	public SimulatorMethodCallPair(DefUseCache cache, Instrumentator instrumentator, RequirementDetermination determination) {
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

			stackMethod.push(method);
			stackClass.push(clazz);

		} else {
			while(stack.getFirst().getId() > invoke)
				stack.removeFirst();
			p = stack.getFirst();
		}

		if(nodeId == -1){
			int curMtdId = 0;
			String curClass = "";

			if(!stackMethod.empty())
			{
				curMtdId = stackMethod.pop();
				curClass = stackClass.pop();
			}

			if(!stackMethod.empty()) {
				MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
				if(!isInList(metPair)){
					listMethodCallPair.add(metPair);
				}
			}
			else{
				System.out.println("Method does not form a pair (called by test");
				MethodCallPair metPair = new MethodCallPair(0,curMtdId,"0.TestCase.Sentinella",curClass);
				if(!isInList(metPair)){
					listMethodCallPair.add(metPair);
				}
			}

		}

	}

	@Override
	public void exportRequirements(String outputFile) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);

		System.out.println("exporting MethodCallPairs, size: " + listMethodCallPair.size());
		checkStack();
		//printMethodCallPairs();
		methodCallRequirements.putAll(listMethodCallPair);
		out.writeObject(methodCallRequirements);
		out.close();
		fileOut.close();
	}

	@Override
	public Requirements requirements() {
		return requirements;
	}

	public MethodCallPairRequirements methodCallRequirements() {
		return methodCallRequirements;
	}

	private boolean isInList(MethodCallPair metPair)
	{
		for(MethodCallPair mcp : listMethodCallPair)
		{
			if(mcp.getClassCaller().contains(metPair.getClassCaller()) && mcp.getClassCalled().contains(metPair.getClassCalled()) &&
					mcp.getIdMethodCaller() == metPair.getIdMethodCaller() && mcp.getIdMethodCalled() == metPair.getIdMethodCalled()){
				return true;
			}
		}
		return false;
	}

	public void printMethodCallPairs()
	{
		System.out.println("List of method call pairs");

		for(MethodCallPair method : listMethodCallPair)
		{
			System.out.println(method.getIdMethodCaller() + "," + method.getClassCaller() + "," + method.getIdMethodCalled() + "," + method.getClassCalled());
		}
	}

	public void checkStack()
	{
		while(!stackMethod.empty())
		{
			int curMtdId = stackMethod.pop();
			String curClass = stackClass.pop();

			if(!stackMethod.empty()) {
				System.out.println("Saving call methods in list after exception");
				System.out.println("Broke-Pair("+stackMethod.lastElement()+","+stackClass.lastElement()+","+curMtdId+","+curClass);
				MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
				if(!isInList(metPair)){
					listMethodCallPair.add(metPair);
				}
			}
			else{
				System.out.println("Method does not form a pair (called by test");
				MethodCallPair metPair = new MethodCallPair(0,curMtdId,"0.TestCase.Sentinella",curClass);
				if(!isInList(metPair)){
					listMethodCallPair.add(metPair);
				}
			}

			System.out.println("Exit in methodId: " + curMtdId + ", class: " + curClass);
			System.out.println("List size: " + listMethodCallPair.size());
		}
	}

}
