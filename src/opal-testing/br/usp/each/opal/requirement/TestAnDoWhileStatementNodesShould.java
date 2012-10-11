package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.DoWhileCFG;

public class TestAnDoWhileStatementNodesShould {

	private DFGraph doWhile;
	private Node[] nodes;
	
	@BeforeTest
	public void init() {
		doWhile = new DoWhileCFG();
		nodes = new NodeDetermination().requirement(doWhile);
	}
	
	@Test
	public void haveThreeNodes() {
		Assert.assertEquals(nodes.length, 3);
	}
	
	@Test
	public void haveANodeForEachProgramBlockWithSameId() {
		for (ProgramBlock block : doWhile) {
			Assert.assertNotNull(Node.find(block.getId(), nodes));
		}
	}
}
