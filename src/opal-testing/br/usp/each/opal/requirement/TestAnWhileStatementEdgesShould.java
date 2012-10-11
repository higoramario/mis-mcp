package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.WhileCFG;

public class TestAnWhileStatementEdgesShould {
	
	private DFGraph whileStatment;
	private Edge[] edges;

	@BeforeTest
	public void init() {
		whileStatment = new WhileCFG();
		edges = new EdgeDetermination().requirement(whileStatment);
	}
	
	@Test
	public void haveThreeEdges() {
		Assert.assertEquals(edges.length, 3);
	}
	
	@Test
	public void haveAnEdgeFromBlock0ToBlock1() {
		ProgramBlock block0 = whileStatment.getProgramBlockById(0);
		ProgramBlock block1 = whileStatment.getProgramBlockById(1);
		Assert.assertNotNull(Edge.find(block0.getId(), block1.getId(), edges));
	}

	@Test
	public void haveAnEdgeFromBlock1ToBlock0() {
		ProgramBlock block1 = whileStatment.getProgramBlockById(1);
		ProgramBlock block0 = whileStatment.getProgramBlockById(0);
		Assert.assertNotNull(Edge.find(block1.getId(), block0.getId(), edges));
	}
	
	@Test
	public void haveAnEdgeFromBlock0ToBlock2() {
		ProgramBlock block0 = whileStatment.getProgramBlockById(0);
		ProgramBlock block2 = whileStatment.getProgramBlockById(2);
		Assert.assertNotNull(Edge.find(block0.getId(), block2.getId(), edges));
	}

}
