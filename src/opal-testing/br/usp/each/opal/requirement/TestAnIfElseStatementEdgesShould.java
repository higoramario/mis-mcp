package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.IfElseCFG;

public class TestAnIfElseStatementEdgesShould {
	
	private DFGraph ifElse;
	private Edge[] edges;

	@BeforeTest
	public void init() {
		ifElse = new IfElseCFG();
		edges = new EdgeDetermination().requirement(ifElse);
	}
	
	@Test
	public void haveFourEdges() {
		Assert.assertEquals(edges.length, 4);
	}
		
	@Test
	public void haveAnEdgeFromBlock0ToBlock1() {
		ProgramBlock block0 = ifElse.getProgramBlockById(0);
		ProgramBlock block1 = ifElse.getProgramBlockById(1);
		Assert.assertNotNull(Edge.find(block0.getId(), block1.getId(), edges));
	}
	
	@Test
	public void haveAnEdgeFromBlock0ToBlock2() {
		ProgramBlock block0 = ifElse.getProgramBlockById(0);
		ProgramBlock block2 = ifElse.getProgramBlockById(2);
		Assert.assertNotNull(Edge.find(block0.getId(), block2.getId(), edges));
	}
	
	@Test
	public void haveAnEdgeFromBlock1ToBlock3() {
		ProgramBlock block1 = ifElse.getProgramBlockById(1);
		ProgramBlock block3 = ifElse.getProgramBlockById(3);
		Assert.assertNotNull(Edge.find(block1.getId(), block3.getId(), edges));
	}
	
	@Test
	public void haveAnEdgeFromBlock2ToBlock3() {
		ProgramBlock block2 = ifElse.getProgramBlockById(2);
		ProgramBlock block3 = ifElse.getProgramBlockById(3);
		Assert.assertNotNull(Edge.find(block2.getId(), block3.getId(), edges));
	}

}
