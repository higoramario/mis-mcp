package br.usp.each.inss.executor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import br.usp.each.inss.Program;
import br.usp.each.inss.cache.DefUseCache;
import br.usp.each.inss.cache.Requirements;
import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.requirement.Requirement;
import br.usp.each.opal.requirement.RequirementDetermination;
import br.usp.each.opal.requirement.RequirementDeterminationException;

public class SimpleSimulator implements Simulator {

	private final static Program SENTINELLA =
			new Program(Long.MIN_VALUE, new DFGraph("Sentinella", 0));

	private final Map<Thread, Deque<Program>> stacks = new HashMap<Thread, Deque<Program>>();

	private final Requirements requirements = new Requirements();

	private final DefUseCache cache;

	private final Instrumentator instrumentator;

	private final RequirementDetermination determination;

	public SimpleSimulator(DefUseCache cache, Instrumentator instrumentator, RequirementDetermination determination) {
		this.cache = cache;
		this.instrumentator = instrumentator;
		this.determination = determination;
	}

	@Override
	public void execute(Thread thread, String className, int methodId, long invokeId, int nodeId) {
		if (nodeId == -1)
			return;

		// Get current thread stack
		Deque<Program> stack = stacks.get(thread);
		if(stack == null) {
			// Thread stack not found... lets create a new one
			stacks.put(thread, stack = new LinkedList<Program>());
			stack.push(SENTINELLA);
		}

		Program p = stack.getFirst();

		// Means that the first node
		if(invokeId > p.getId()) {
			DFGraph graph;
			Requirement[] requirement;

			graph = cache.get(className, methodId);
			requirement = requirements.get(className, methodId);
			if (requirement == null) {
				try {
					requirement = determination.requirement(graph);
				} catch (RequirementDeterminationException e) {
					throw new Error(String.format("Error getting requirements for class: %s and method: %d", className, methodId), e);
				}
				requirements.put(className, methodId, requirement);
			}

			p = new Program(invokeId, graph);
			instrumentator.instrument(p, requirement);
			stack.addFirst(p);
		} else {
			while(stack.getFirst().getId() > invokeId)
				stack.removeFirst();
			p = stack.getFirst();
		}

		p.getExecutionEntryById(nodeId).traverse();

		if (p.isExit(nodeId))
			stack.removeFirst();
	}

	@Override
	public void exportRequirements(String outputFile) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(outputFile);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(requirements);
		out.close();
		fileOut.close();
	}

	@Override
	public Requirements requirements() {
		return requirements;
	}

}
