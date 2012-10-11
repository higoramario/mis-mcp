package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.IfElseCFG;

public class TestAnIfElseStatementNodesShould {

	private DFGraph ifElse;
	private Node[] nodes;
	
	@BeforeTest
	public void init() {
		ifElse = new IfElseCFG();
		nodes = new NodeDetermination().requirement(ifElse);
	}
	
	@Test
	public void haveFourNodes() {
		Assert.assertEquals(nodes.length, 4);
	}
	
	@Test
	public void haveANodeForEachProgramBlockWithSameId() {
		for (ProgramBlock block : ifElse) {
			Assert.assertNotNull(Node.find(block.getId(), nodes));
		}
	}
}
