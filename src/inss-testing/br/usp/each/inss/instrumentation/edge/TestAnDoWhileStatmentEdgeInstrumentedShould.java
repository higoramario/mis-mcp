package br.usp.each.inss.instrumentation.edge;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.DoWhileCFG;
import br.usp.each.opal.requirement.Edge;

public class TestAnDoWhileStatmentEdgeInstrumentedShould  extends TestStatmentEdgeInstrumented {
	
	public TestAnDoWhileStatmentEdgeInstrumentedShould() {
		super(new DoWhileCFG());
	}

	@Test
	public void haveAllEdgesCoveredWhenWhileIsTrue() {
		traverse(0, 1, 0, 1, 2);
		for (Edge edge : requirements()) {
			Assert.assertTrue(edge.isCovered());
		}
	}
	
	@Test
	public void haveAEdgeFromOneToZeroNotCoveredWhenWhileIsNeverTrue() {
		traverse(0, 1, 2);
		Assert.assertTrue(Edge.find(0, 1, requirements()).isCovered());
		Assert.assertTrue(Edge.find(1, 2, requirements()).isCovered());
		Assert.assertFalse(Edge.find(1, 0, requirements()).isCovered());
	}

}
