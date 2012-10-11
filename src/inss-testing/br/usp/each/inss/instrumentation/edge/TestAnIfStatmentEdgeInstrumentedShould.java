package br.usp.each.inss.instrumentation.edge;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.IfCFG;
import br.usp.each.opal.requirement.Edge;

public class TestAnIfStatmentEdgeInstrumentedShould extends	TestStatmentEdgeInstrumented {
	
	public TestAnIfStatmentEdgeInstrumentedShould() {
		super(new IfCFG());
	}

	@Test
	public void haveAEdgeFromZeroToTwoNotCoveredWhenIfIsTrue() {
		traverse(0, 1, 2);
		Assert.assertTrue(Edge.find(0, 1, requirements()).isCovered());
		Assert.assertTrue(Edge.find(1, 2, requirements()).isCovered());
		Assert.assertFalse(Edge.find(0, 2, requirements()).isCovered());
	}
	
	@Test
	public void haveAEdgeFromZeroToOneAndFromOneToTwoNotCoveredWhenIfIsFalse() {
		traverse(0, 2);
		Assert.assertTrue(Edge.find(0, 2, requirements()).isCovered());
		Assert.assertFalse(Edge.find(0, 1, requirements()).isCovered());
		Assert.assertFalse(Edge.find(1, 2, requirements()).isCovered());
	}

}
