package br.usp.each.inss.executor;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private static List<MethodCallPair> listAllMethodCallPair = new ArrayList<MethodCallPair>();
	private static Stack<Integer> stackMethod = new Stack<Integer>();
	private static Stack<String> stackClass = new Stack<String>();
	private static Stack<Long> stackInvoke = new Stack<Long>();
	private final String SENTINELLA_CLASS = "0.TestCase.Sentinella";
	
	private static Map<Long,List<MethodCallPair>> mapMethodCallPair = new HashMap<Long,List<MethodCallPair>>();
	private static Map<Long,Stack<Integer>> mapStackMethod = new HashMap<Long,Stack<Integer>>();
	private static Map<Long,Stack<String>> mapStackClass = new HashMap<Long,Stack<String>>();
	private static Map<Long,Stack<Long>> mapStackInvoke = new HashMap<Long,Stack<Long>>();
	
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
			
			if(!mapStackInvoke.containsKey(thread.getId()))
			{
				stackMethod = new Stack<Integer>();
				stackClass = new Stack<String>();
				stackInvoke = new Stack<Long>();
			
			}
			else{
				stackMethod = mapStackMethod.remove(thread.getId());
				stackClass = mapStackClass.remove(thread.getId());
				stackInvoke = mapStackInvoke.remove(thread.getId());
			}
			stackMethod.push(method);
			stackClass.push(clazz);
			stackInvoke.push(invoke);
			mapStackMethod.put(thread.getId(), stackMethod);
			mapStackClass.put(thread.getId(), stackClass);
			mapStackInvoke.put(thread.getId(), stackInvoke);
			
		} else {
			while(stack.getFirst().getId() > invoke)
				stack.removeFirst();
			p = stack.getFirst();
		}

		if(nodeId == -1){
			int curMtdId = 0;
			String curClass = ""; 
			
			if(!mapStackInvoke.isEmpty() && mapStackInvoke.containsKey(thread.getId()) && mapStackInvoke.get(thread.getId()).contains(invoke)){
				long stInvoke = mapStackInvoke.get(thread.getId()).lastElement();
				while(invoke < stInvoke)
				{
					stackMethod = mapStackMethod.remove(thread.getId());
					stackClass = mapStackClass.remove(thread.getId());
					stackInvoke = mapStackInvoke.remove(thread.getId());
					
					if(!mapMethodCallPair.isEmpty() && mapMethodCallPair.containsKey(thread.getId())){
						listMethodCallPair = mapMethodCallPair.remove(thread.getId());
					}
					else{
						listMethodCallPair = new ArrayList<MethodCallPair>();
					}
					
					if(!stackInvoke.empty())
					{
						curMtdId = stackMethod.pop();
						curClass = stackClass.pop();
						stackInvoke.pop();
					
						mapStackMethod.put(thread.getId(), stackMethod);
						mapStackClass.put(thread.getId(), stackClass);
						mapStackInvoke.put(thread.getId(), stackInvoke);
						if(!stackInvoke.empty()) {
							MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
							if(!isInList(metPair,thread.getId())){
								System.out.println("exists: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
								System.out.println("PAIR: "+metPair.toString());
								listMethodCallPair.add(metPair);
							}
						}
						else{
							MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
							if(!isInList(metPair, thread.getId())){
								listMethodCallPair.add(metPair);
							}
						}
						mapMethodCallPair.put(thread.getId(), listMethodCallPair);
					}
					stInvoke = mapStackInvoke.get(thread.getId()).lastElement();
				}
				
				stackMethod = mapStackMethod.remove(thread.getId());
				stackClass = mapStackClass.remove(thread.getId());
				stackInvoke = mapStackInvoke.remove(thread.getId());
				
				if(!mapMethodCallPair.isEmpty() && mapMethodCallPair.containsKey(thread.getId())){
					listMethodCallPair = mapMethodCallPair.remove(thread.getId());
				}
				else{
					listMethodCallPair = new ArrayList<MethodCallPair>();
				}
				
				if(!stackInvoke.empty())
				{
					curMtdId = stackMethod.pop();
					curClass = stackClass.pop();
					stackInvoke.pop();
					
					mapStackMethod.put(thread.getId(), stackMethod);
					mapStackClass.put(thread.getId(), stackClass);
					mapStackInvoke.put(thread.getId(), stackInvoke);
					
					if(!stackInvoke.empty()) {
						MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
						if(!isInList(metPair,thread.getId())){
							listMethodCallPair.add(metPair);
						}
					}
					else{
						MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
						if(!isInList(metPair, thread.getId())){
							listMethodCallPair.add(metPair);
						}
					}
					mapMethodCallPair.put(thread.getId(), listMethodCallPair);
				}
			}
		}
	}

	@Override
	public void exportRequirements(String outputFile) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		
		Set<Long> setThreadId = mapStackInvoke.keySet();
		Iterator<Long> itThreadId = setThreadId.iterator();
		while(itThreadId.hasNext())
		{
			Long threadId = itThreadId.next();
			checkStack(threadId);
			listMethodCallPair = mapMethodCallPair.get(threadId);
			listAllMethodCallPair.addAll(listMethodCallPair);
		}
		
		methodCallRequirements.putAll(removeDuplicates());
		out.writeObject(methodCallRequirements);
		out.close();
		fileOut.close();
	}
	
	@Override
	public void exportTestInformation(String outputFile, String message) throws IOException {
		BufferedWriter fl = new BufferedWriter(new FileWriter(outputFile));
		fl.write(message);
		fl.close();
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

	
	private boolean isInList(MethodCallPair metPair, long threadId)
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
		Set<Long> setThreadId = mapMethodCallPair.keySet();
		Iterator<Long> itThreadId = setThreadId.iterator();

		while(itThreadId.hasNext())
		{
			listMethodCallPair = mapMethodCallPair.get(itThreadId.next());
			
			for(MethodCallPair method : listMethodCallPair)
			{
				System.out.println("Thread: " + itThreadId + ", " + method.getIdMethodCaller() + "," + method.getClassCaller() + "," + method.getIdMethodCalled() + "," + method.getClassCalled());
			}
		}
	}

	public void checkStack()
	{
		
		Set<Long> setThreadId = mapMethodCallPair.keySet();
		Iterator<Long> itThreadId = setThreadId.iterator();

		while(itThreadId.hasNext())
		{
			Long threadId = itThreadId.next();
			
			stackMethod = mapStackMethod.remove(threadId);
			stackClass = mapStackClass.remove(threadId);
			listMethodCallPair = mapMethodCallPair.remove(threadId);
			
			while(!stackMethod.empty())
			{
				int curMtdId = stackMethod.pop();
				String curClass = stackClass.pop();

				if(!stackMethod.empty()) {
					MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
					if(!isInList(metPair)){
						listMethodCallPair.add(metPair);
					}
				}
				else{
					MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
					if(!isInList(metPair)){
						listMethodCallPair.add(metPair);
					}
				}
			}
			mapStackMethod.put(threadId, stackMethod);
			mapStackClass.put(threadId, stackClass);
			mapMethodCallPair.put(threadId, listMethodCallPair);
		}
	}
	
	public void checkStack(Long threadId)
	{
		stackMethod = mapStackMethod.get(threadId);
		stackClass = mapStackClass.get(threadId);
		if(mapMethodCallPair.containsKey(threadId)){
			listMethodCallPair = mapMethodCallPair.remove(threadId);
		}
		else{
			listMethodCallPair = new ArrayList<MethodCallPair>();
		}
		while(!stackMethod.empty())
		{
			int curMtdId = stackMethod.pop();
			String curClass = stackClass.pop();

			if(!stackMethod.empty()) {
				MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
				if(!isInList(metPair, threadId)){
					listMethodCallPair.add(metPair);
				}
			}
			else{
				MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
				if(!isInList(metPair,threadId)){
					listMethodCallPair.add(metPair);
				}
			}
		}
		if(listMethodCallPair.size() > 0){
			mapMethodCallPair.put(threadId, listMethodCallPair);
		}
	}
	
	public void reset()
	{
		methodCallRequirements = new MethodCallPairRequirements();
		mapMethodCallPair = new HashMap<Long,List<MethodCallPair>>();
		mapStackMethod = new HashMap<Long,Stack<Integer>>();
		mapStackClass = new HashMap<Long,Stack<String>>();		
		mapStackInvoke = new HashMap<Long,Stack<Long>>();		
		listMethodCallPair = new ArrayList<MethodCallPair>();
		stackMethod = new Stack<Integer>();
		stackClass = new Stack<String>();
		stackInvoke = new Stack<Long>();
	}
	
	private List<MethodCallPair> removeDuplicates(){
		List<MethodCallPair> newListMCP = new ArrayList<MethodCallPair>();
		Iterator<MethodCallPair> it = listAllMethodCallPair.iterator();  
		while(it.hasNext())
		{
			MethodCallPair mcp = (MethodCallPair) it.next();
			if(!isInListAll(mcp,newListMCP))
			{
				newListMCP.add(mcp);
			}
		}
		return newListMCP;
	}
	
	private boolean isInListAll(MethodCallPair metPair, List<MethodCallPair> list)
	{
		for(MethodCallPair mcp : list)
		{
			if(mcp.getClassCaller().contains(metPair.getClassCaller()) && mcp.getClassCalled().contains(metPair.getClassCalled()) &&
					mcp.getIdMethodCaller() == metPair.getIdMethodCaller() && mcp.getIdMethodCalled() == metPair.getIdMethodCalled()){
				return true;
			}
		}
		return false;
	}

}
