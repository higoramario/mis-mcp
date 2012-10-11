package br.usp.each.inss.executor;

import java.io.FileOutputStream;
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

public class SimulatorMethodCallPairThread implements Simulator {

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
	
	public SimulatorMethodCallPairThread(DefUseCache cache, Instrumentator instrumentator, RequirementDetermination determination) {
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
				//System.out.println("new stackMethod: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
			}
			else{
				stackMethod = mapStackMethod.remove(thread.getId());
				stackClass = mapStackClass.remove(thread.getId());
				stackInvoke = mapStackInvoke.remove(thread.getId());
				//System.out.println("stackMethod exists: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId() + ", stackMethod size:" + stackMethod.size());
			}
			stackMethod.push(method);
			stackClass.push(clazz);
			stackInvoke.push(invoke);
			mapStackMethod.put(thread.getId(), stackMethod);
			mapStackClass.put(thread.getId(), stackClass);
			mapStackInvoke.put(thread.getId(), stackInvoke);
			
		} /*else {
			while(stack.getFirst().getId() > invoke)
				stack.removeFirst();
			p = stack.getFirst();
		}*/

		if(nodeId == -1){
			int curMtdId = 0;
			String curClass = ""; 
			
			if(!mapStackInvoke.isEmpty() && mapStackInvoke.containsKey(thread.getId()) && mapStackInvoke.get(thread.getId()).contains(invoke)){
				long stInvoke = mapStackInvoke.get(thread.getId()).lastElement();
				while(invoke < stInvoke)
				{
					if(invoke < mapStackInvoke.get(thread.getId()).lastElement()){
						//System.out.println("INVOKE BIGGER THAN TOP STACK!!!!!");
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
						//System.out.println("Stack size before: " + stackInvoke.size());
						curMtdId = stackMethod.pop();
						curClass = stackClass.pop();
						stackInvoke.pop();
						//System.out.println("RETIRE FROM STACK: " + curMtdId + "," + curClass);
						mapStackMethod.put(thread.getId(), stackMethod);
						mapStackClass.put(thread.getId(), stackClass);
						mapStackInvoke.put(thread.getId(), stackInvoke);
						//System.out.println("Stack size after: " + stackInvoke.size());
						//create the pair
						if(!stackInvoke.empty()) {
							MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
							//System.out.println("broke thread:" + thread.getId() + ", method:" + method);
							if(!isInList(metPair,thread.getId())){
								System.out.println("exists: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
								System.out.println("PAIR: "+metPair.toString());
								listMethodCallPair.add(metPair);
							}
						}
						else{
							//System.out.println("Method does not form a pair (called by test) - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
							MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
							//System.out.println("broke (not empty) thread:" + thread.getId() + ", method:" + method);
							if(!isInList(metPair, thread.getId())){
								//System.out.println("new: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
								//System.out.println("PAIR: "+metPair.toString());
								listMethodCallPair.add(metPair);
							}
						}
						mapMethodCallPair.put(thread.getId(), listMethodCallPair);
					}
					stInvoke = mapStackInvoke.get(thread.getId()).lastElement();
				}//while
				
				//correct order
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
					//System.out.println("Stack size before: " + stackInvoke.size());
					curMtdId = stackMethod.pop();
					curClass = stackClass.pop();
					stackInvoke.pop();
					if(invoke == -9223372036854775627L && stackInvoke.lastElement() == -9223372036854775630L){
						//System.out.println("Method "+stackMethod.lastElement()+" is dying in stack!!");
					}
					//System.out.println("RETIRE FROM STACK: " + curMtdId + "," + curClass);
					mapStackMethod.put(thread.getId(), stackMethod);
					mapStackClass.put(thread.getId(), stackClass);
					mapStackInvoke.put(thread.getId(), stackInvoke);
					//System.out.println("Stack size after: " + stackInvoke.size());
					//create the pair
					if(!stackInvoke.empty()) {
						MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
						//System.out.println("broke thread:" + thread.getId() + ", method:" + method);
						if(!isInList(metPair,thread.getId())){
							//System.out.println("exists: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
							//System.out.println("PAIR: "+metPair.toString());
							listMethodCallPair.add(metPair);
						}
					}
					else{
						//System.out.println("Method does not form a pair (called by test) - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
						MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
						//System.out.println("broke (not empty) thread:" + thread.getId() + ", method:" + method);
						if(!isInList(metPair, thread.getId())){
							//System.out.println("new: insert in list - class: " + clazz + ", method: " + method + ", thread: " + thread.getId());
							//System.out.println("PAIR: "+metPair.toString());
							listMethodCallPair.add(metPair);
						}
					}
					mapMethodCallPair.put(thread.getId(), listMethodCallPair);
				}//if(!stackInvoke.empty())
				
			}
			
		}//node == -1
	}

	@Override
	public void exportRequirements(String outputFile) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		
		Set<Long> setThreadId = mapStackInvoke.keySet();
		Iterator<Long> itThreadId = setThreadId.iterator();
		//System.out.println();
		//printMethodCallPairs();
		while(itThreadId.hasNext())
		{
			Long threadId = itThreadId.next();
			//System.out.println("Checking stack... "+threadId);
			checkStack(threadId);
			//printMethodCallPairs();
			listMethodCallPair = mapMethodCallPair.get(threadId);
			//System.out.println("exporting MethodCallPairs, size: " + listMethodCallPair.size()+", thread: " + threadId);
			listAllMethodCallPair.addAll(listMethodCallPair);
		}
		//System.out.println("LISTALLMCP before size:" + listAllMethodCallPair.size());
		
		methodCallRequirements.putAll(removeDuplicates());
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

	
	private boolean isInList(MethodCallPair metPair, long threadId)
	{
		//listMethodCallPair = mapMethodCallPair.get(threadId);
		//if(listMethodCallPair != null)
		for(MethodCallPair mcp : listMethodCallPair)
		{
			if(mcp.getClassCaller().contains(metPair.getClassCaller()) && mcp.getClassCalled().contains(metPair.getClassCalled()) &&
					mcp.getIdMethodCaller() == metPair.getIdMethodCaller() && mcp.getIdMethodCalled() == metPair.getIdMethodCalled()){
				//System.out.println("isInList: class: " + mcp.getClassCaller() + ", method: " + mcp.getIdMethodCaller() + ", thread: " + threadId);
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
					//System.out.println("Saving call methods in list after exception");
					//System.out.println("Broke-Pair("+stackMethod.lastElement()+","+stackClass.lastElement()+","+curMtdId+","+curClass);
					MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
					if(!isInList(metPair)){
						listMethodCallPair.add(metPair);
					}
				}
				else{
					//System.out.println("Method does not form a pair (called by test");
					MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
					if(!isInList(metPair)){
						listMethodCallPair.add(metPair);
					}
				}

				//System.out.println("Exit in methodId: " + curMtdId + ", class: " + curClass);
				//System.out.println("List size: " + listMethodCallPair.size());
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
				//System.out.println("Saving call methods in list after exception");
				//System.out.println("Broke-Pair("+stackMethod.lastElement()+","+stackClass.lastElement()+","+curMtdId+","+curClass);
				MethodCallPair metPair = new MethodCallPair(stackMethod.lastElement(),curMtdId,stackClass.lastElement(),curClass);
				if(!isInList(metPair, threadId)){
					listMethodCallPair.add(metPair);
				}
			}
			else{
				//System.out.println("Method does not form a pair (called by test");
				MethodCallPair metPair = new MethodCallPair(0,curMtdId,SENTINELLA_CLASS,curClass);
				if(!isInList(metPair,threadId)){
					listMethodCallPair.add(metPair);
				}
			}

			//System.out.println("Exit in methodId: " + curMtdId + ", class: " + curClass);
			//System.out.println("List size: " + listMethodCallPair.size());
		}
		//mapStackMethod.put(threadId, stackMethod);
		//mapStackClass.put(threadId, stackClass);
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
		//System.out.println("LISTALLMCP after size:" + newListMCP.size());
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
