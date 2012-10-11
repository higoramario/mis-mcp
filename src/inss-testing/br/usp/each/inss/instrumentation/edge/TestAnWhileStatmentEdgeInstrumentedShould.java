package br.usp.each.inss.instrumentation.edge;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.WhileCFG;
import br.usp.each.opal.requirement.Edge;

public class TestAnWhileStatmentEdgeInstrumentedShould extends TestStatmentEdgeInstrumented {
	
	public TestAnWhileStatmentEdgeInstrumentedShould() {
		super(new WhileCFG());
	}

	@Test
	public void haveAllEdgesCoveredWhenWhileIsTrue() {
		traverse(0, 1, 0, 2);
		for (Edge edge : requirements()) {
			Assert.assertTrue(edge.isCovered());
		}
	}

	@Test
	public void haveEdgeFromZeroToOneAndFromOneToZeroNotCoveredWhenWhileIsNeverTrue() {
		traverse(0, 2);
		Assert.assertTrue(Edge.find(0, 2, requirements()).isCovered());
		Assert.assertFalse(Edge.find(0, 1, requirements()).isCovered());
		Assert.assertFalse(Edge.find(1, 0, requirements()).isCovered());
	}

}
