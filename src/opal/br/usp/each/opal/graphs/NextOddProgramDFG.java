package br.usp.each.opal.graphs;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;

public final class NextOddProgramDFG extends DFGraph {

	public NextOddProgramDFG() {
		super("nextOdd", 0);
		
		// Set up graph
		ProgramBlock block0 = new ProgramBlock(0);
		ProgramBlock block6 = new ProgramBlock(6);
		ProgramBlock block9 = new ProgramBlock(9);
		
		add(block0);
		add(block6);
		add(block9);
		
		addEdge(block0, block6);
		addEdge(block0, block9);
		addEdge(block6, block9);
		
		// Set up vars
		addVar("this", 0);
		addVar("x", 1);
		
		// Block 0 set var x (Parameter) and this.
		block0.def(0);
		block0.def(1);
		block0.puse(1); // If on Block 0
		
		// x = x + 1
		block6.cuse(1);
		block6.def(1);
		
		// x = x + 1
		block9.cuse(1);
		block9.def(1);
	}

}
