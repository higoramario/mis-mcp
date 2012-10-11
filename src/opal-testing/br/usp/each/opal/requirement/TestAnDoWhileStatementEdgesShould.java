package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.DoWhileCFG;

public class TestAnDoWhileStatementEdgesShould {
	
	private DFGraph doWhile;
	private Edge[] edges;

	@BeforeTest
	public void init() {
		doWhile = new DoWhileCFG();
		edges = new EdgeDetermination().requirement(doWhile);
	}
		
	@Test
	public void haveThreeEdges() {
		Assert.assertEquals(edges.length, 3);
	}
		
	@Test
	public void haveAnEdgeFromBlock0ToBlock1() {
		ProgramBlock block0 = doWhile.getProgramBlockById(0);
		ProgramBlock block1 = doWhile.getProgramBlockById(1);
		Assert.assertNotNull(Edge.find(block0.getId(), block1.getId(), edges));
	}

	@Test
	public void haveAnEdgeFromBlock1ToBlock0() {
		ProgramBlock block1 = doWhile.getProgramBlockById(1);
		ProgramBlock block0 = doWhile.getProgramBlockById(0);
		Assert.assertNotNull(Edge.find(block1.getId(), block0.getId(), edges));
	}
	
	@Test
	public void haveAnEdgeFromBlock1ToBlock2() {
		ProgramBlock block1 = doWhile.getProgramBlockById(1);
		ProgramBlock block2 = doWhile.getProgramBlockById(2);
		Assert.assertNotNull(Edge.find(block1.getId(), block2.getId(), edges));
	}

}
