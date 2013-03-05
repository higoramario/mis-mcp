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
import br.usp.each.inss.cache.MethodCallTripleRequirements;
import br.usp.each.inss.cache.Requirements;
import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.requirement.MethodCallPair;
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
	private static List<MethodCallTriple> listAllMethodCallTriple = new ArrayList<MethodCallTriple>();
	private static Stack<Integer> stackMethod = new Stack<Integer>();
	private static Stack<String> stackClass = new Stack<String>();
	private static Stack<Long> stackInvoke = new Stack<Long>();
	private final String SENTINELLA_CLASS = "0.TestCase.Sentinella";

	private static Map<Long,List<MethodCallTriple>> mapMethodCallTriple = new HashMap<Long,List<MethodCallTriple>>();
	private static Map<Long,Stack<Integer>> mapStackMethod = new HashMap<Long,Stack<Integer>>();
	private static Map<Long,Stack<String>> mapStackClass = new HashMap<Long,Stack<String>>();
	private static Map<Long,Stack<Long>> mapStackInvoke = new HashMap<Long,Stack<Long>>();

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
		
		}
		
		if(nodeId == -1){
			int curMethod = 0;
			String curClass = "";

			if(!mapStackInvoke.isEmpty() && mapStackInvoke.containsKey(thread.getId()) && mapStackInvoke.get(thread.getId()).contains(invoke)){
				long stInvoke = mapStackInvoke.get(thread.getId()).lastElement();
				while(invoke < stInvoke)
				{
					stackMethod = mapStackMethod.remove(thread.getId());
					stackClass = mapStackClass.remove(thread.getId());
					stackInvoke = mapStackInvoke.remove(thread.getId());
					
					if(!mapMethodCallTriple.isEmpty() && mapMethodCallTriple.containsKey(thread.getId())){
						listMethodCallTriple = mapMethodCallTriple.remove(thread.getId());
					}
					else{
						listMethodCallTriple = new ArrayList<MethodCallTriple>();
					}
					
					if(!stackInvoke.empty())
					{
						curMethod = stackMethod.pop();
						curClass = stackClass.pop();
						stackInvoke.pop();
						mapStackMethod.put(thread.getId(), stackMethod);
						mapStackClass.put(thread.getId(), stackClass);
						mapStackInvoke.put(thread.getId(), stackInvoke);
						int stackSize = stackInvoke.size();
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
						
						mapMethodCallTriple.put(thread.getId(), listMethodCallTriple);
					}
					else{
						MethodCallTriple metTriple = new MethodCallTriple(0,0,curMethod,SENTINELLA_CLASS,SENTINELLA_CLASS,curClass);

						if(!isInList(metTriple)){
							listMethodCallTriple.add(metTriple);
						}
					}
					stInvoke = mapStackInvoke.get(thread.getId()).lastElement();
				}
				
				stackMethod = mapStackMethod.remove(thread.getId());
				stackClass = mapStackClass.remove(thread.getId());
				stackInvoke = mapStackInvoke.remove(thread.getId());
				
				if(!mapMethodCallTriple.isEmpty() && mapMethodCallTriple.containsKey(thread.getId())){
					listMethodCallTriple = mapMethodCallTriple.remove(thread.getId());
				}
				else{
					listMethodCallTriple = new ArrayList<MethodCallTriple>();
				}
				
				if(!stackInvoke.empty())
				{
					curMethod = stackMethod.pop();
					curClass = stackClass.pop();
					stackInvoke.pop();
					mapStackMethod.put(thread.getId(), stackMethod);
					mapStackClass.put(thread.getId(), stackClass);
					mapStackInvoke.put(thread.getId(), stackInvoke);
					int stackSize = stackInvoke.size();
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
					
					mapMethodCallTriple.put(thread.getId(), listMethodCallTriple);
				}
				else{
					MethodCallTriple metTriple = new MethodCallTriple(0,0,curMethod,SENTINELLA_CLASS,SENTINELLA_CLASS,curClass);

					if(!isInList(metTriple)){
						listMethodCallTriple.add(metTriple);
					}
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
			listMethodCallTriple = mapMethodCallTriple.get(threadId);
			listAllMethodCallTriple.addAll(listMethodCallTriple);
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
				return true;
			}
		}
		return false;
	}

	public void printMethodCallTriples()
	{
		System.out.println("List of method call triples");
		Set<Long> setThreadId = mapMethodCallTriple.keySet();
		Iterator<Long> itThreadId = setThreadId.iterator();

		while(itThreadId.hasNext())
		{
			listMethodCallTriple = mapMethodCallTriple.get(itThreadId.next());

			for(MethodCallTriple method : listMethodCallTriple)
			{
				System.out.println("Thread: " + itThreadId + ", " + method.toString());
			}
		}
	}

	public void checkStack(Long threadId)
	{
		stackMethod = mapStackMethod.get(threadId);
		stackClass = mapStackClass.get(threadId);
		stackInvoke = mapStackInvoke.get(threadId);
		if(mapMethodCallTriple.containsKey(threadId)){
			listMethodCallTriple = mapMethodCallTriple.remove(threadId);
		}
		else{
			listMethodCallTriple = new ArrayList<MethodCallTriple>();
		}
		
		while(!stackInvoke.empty())
		{
			int curMethod = stackMethod.pop();
			String curClass = stackClass.pop();
			stackInvoke.pop();
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
		
		if(listMethodCallTriple.size() > 0){
			mapMethodCallTriple.put(threadId, listMethodCallTriple);
		}
	}
	
	public void reset()
	{
		methodCallRequirements = new MethodCallTripleRequirements();
		mapMethodCallTriple = new HashMap<Long,List<MethodCallTriple>>();
		mapStackMethod = new HashMap<Long,Stack<Integer>>();
		mapStackClass = new HashMap<Long,Stack<String>>();		
		mapStackInvoke = new HashMap<Long,Stack<Long>>();		
		listMethodCallTriple = new ArrayList<MethodCallTriple>();
		stackMethod = new Stack<Integer>();
		stackClass = new Stack<String>();
		stackInvoke = new Stack<Long>();
	}
	
	private List<MethodCallTriple> removeDuplicates(){
		List<MethodCallTriple> newListMCT = new ArrayList<MethodCallTriple>();
		Iterator<MethodCallTriple> it = listAllMethodCallTriple.iterator();  
		while(it.hasNext())
		{
			MethodCallTriple mct = (MethodCallTriple) it.next();
			if(!isInListAll(mct,newListMCT))
			{
				newListMCT.add(mct);
			}
		}
		return newListMCT;
	}
	
	private boolean isInListAll(MethodCallTriple metTriple, List<MethodCallTriple> list)
	{
		for(MethodCallTriple mct : list)
		{
			if(mct.getClassCaller().contains(metTriple.getClassCaller()) && mct.getClassCalledN1().contains(metTriple.getClassCalledN1()) &&
					mct.getClassCalledN2().contains(metTriple.getClassCalledN2()) & mct.getIdMethodCaller() == metTriple.getIdMethodCaller() &&
					mct.getIdMethodCalledN1() == metTriple.getIdMethodCalledN1() & mct.getIdMethodCalledN2() == metTriple.getIdMethodCalledN2()){
				return true;
			}
		}
		return false;
	}

	public void printMethodCall(long itThreadId)
	{
		System.out.println("List of method call triples");
		int i = stackInvoke.size();
		while(i >= 0)
		{
			System.out.println("STACKS: method:" + stackMethod.indexOf(i) + ", class:" + stackClass.indexOf(i) + ", invoke:"+stackInvoke.indexOf(i));
			i--;
		}
	}

}
