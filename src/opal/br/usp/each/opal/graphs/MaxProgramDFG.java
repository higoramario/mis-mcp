package br.usp.each.opal.graphs;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;

public class MaxProgramDFG extends DFGraph {

	public MaxProgramDFG() {
		super("max", 0);
		
		ProgramBlock block0 = new ProgramBlock(0);
		ProgramBlock block9 = new ProgramBlock(9);
		ProgramBlock block15 = new ProgramBlock(15);
		ProgramBlock block22 = new ProgramBlock(22);
		ProgramBlock block26 = new ProgramBlock(26);
		ProgramBlock block32 = new ProgramBlock(32);
		
		add(block0);
		add(block9);
		add(block15);
		add(block22);
		add(block26);
		add(block32);
		addEdge(block0, block9);
		
		addEdge(block9, block15);
		addEdge(block9, block32);
		addEdge(block15, block22);
		addEdge(block15, block26);
		addEdge(block22, block26);
		addEdge(block26, block9);
		
		// Set up vars
		addVar("this", 0);
		addVar("array", 1);
		addVar("array[]", 2);
		addVar("i", 3);
		addVar("max", 4);
		
		/* 
		 * public int max(int[] array)
		 *     int i = 0
		 *     int max = array[++i]
		 */
		block0.def(0);
		block0.def(1);
		block0.def(2);
		block0.def(3);
		block0.def(4);
		block0.cuse(2);
		
		// while (i < array.length)
		block9.puse(1);
		block9.puse(3);
		
		// if (array[i] > max)
		block15.puse(1);
		block15.puse(2);
		block15.puse(3);
		block15.puse(4);
		
		// max = array[i]
		block22.def(4);
		block22.cuse(1);
		block22.cuse(2);
		block22.cuse(3);
		
		// i = i + 1
		block26.def(3);
		block26.cuse(3);
		
		// return max
		block32.cuse(4);
	}

}
