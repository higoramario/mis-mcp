package br.usp.each.inss.instrumentation.edge;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.IfElseCFG;
import br.usp.each.opal.requirement.Edge;

public class TestAnIfElseStatmentEdgeInstrumentedShould extends	TestStatmentEdgeInstrumented {
	
	public TestAnIfElseStatmentEdgeInstrumentedShould() {
		super(new IfElseCFG());
	}

	@Test
	public void haveEdgeFromZeroToOneAndFromOneToThreeNotCoveredWhenIfIsFalse() {
		traverse(0, 2, 3);
		Assert.assertTrue(Edge.find(0, 2, requirements()).isCovered());
		Assert.assertTrue(Edge.find(2, 3, requirements()).isCovered());
		Assert.assertFalse(Edge.find(0, 1, requirements()).isCovered());
		Assert.assertFalse(Edge.find(1, 3, requirements()).isCovered());
	}

	@Test
	public void haveEdgeFromZeroToTwoAndFromTwoToThreeNotCoveredWhenIfIsTrue() {
		traverse(0, 1, 3);
		Assert.assertFalse(Edge.find(0, 2, requirements()).isCovered());
		Assert.assertFalse(Edge.find(2, 3, requirements()).isCovered());
		Assert.assertTrue(Edge.find(0, 1, requirements()).isCovered());
		Assert.assertTrue(Edge.find(1, 3, requirements()).isCovered());
	}
	
}
