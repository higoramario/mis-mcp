package br.usp.each.opal.graphs;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;

public final class NextOddProgramPresentationDFG extends DFGraph {

	public NextOddProgramPresentationDFG() {
		super("nextOddPresentation", 0);
		
		ProgramBlock block0 = new ProgramBlock(0);
		ProgramBlock block1 = new ProgramBlock(1);
		ProgramBlock block2 = new ProgramBlock(2);
		ProgramBlock block3 = new ProgramBlock(3);
		ProgramBlock block4 = new ProgramBlock(4);
		
		add(block0);
		add(block1);
		add(block2);
		add(block3);
		add(block4);
		
		addEdge(block0, block1);
		addEdge(block1, block2);
		addEdge(block2, block3);
		addEdge(block2, block4);
		addEdge(block3, block4);
		
		// Set up vars
		addVar("x", 0);
		addVar("issOdd", 1);
		
		// Block 0 set var x (Parameter)
		block0.def(0);
		
		// Block 1 use var x to determine var isOdd 
		block1.cuse(0);
		block1.def(1); 
		
		// If on var issOdd
		block2.puse(1);
		
		// x = x + 1
		block3.cuse(0);
		block3.def(0);
		
		// x = x + 1
		block4.cuse(0);
		block4.def(0);
	}

}
