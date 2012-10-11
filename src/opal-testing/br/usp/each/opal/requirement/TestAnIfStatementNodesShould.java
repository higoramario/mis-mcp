package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.IfCFG;

public class TestAnIfStatementNodesShould {

	private DFGraph ifStatment;
	private Node[] nodes;
	
	@BeforeTest
	public void init() {
		ifStatment = new IfCFG();
		nodes = new NodeDetermination().requirement(ifStatment);
	}
	
	@Test
	public void haveThreeNodes() {
		Assert.assertEquals(nodes.length, 3);
	}
	
	@Test
	public void haveANodeForEachProgramBlockWithSameId() {
		for (ProgramBlock block : ifStatment) {
			Assert.assertNotNull(Node.find(block.getId(), nodes));
		}
	}
}
