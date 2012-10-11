package br.usp.each.opal.graphs;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;

public final class IfCFG extends DFGraph {
	
	public IfCFG() {
		super("if", 0);
		ProgramBlock b0, b1, b2;
		
		add(b0 = new ProgramBlock(0));
		add(b1 = new ProgramBlock(1));
		add(b2 = new ProgramBlock(2));
		
		addEdge(b0, b1);
		addEdge(b1, b2);
		addEdge(b0, b2);
	}

}
