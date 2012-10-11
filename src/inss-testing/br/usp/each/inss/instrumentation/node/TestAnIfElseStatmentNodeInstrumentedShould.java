package br.usp.each.inss.instrumentation.node;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.IfElseCFG;
import br.usp.each.opal.requirement.Node;

public class TestAnIfElseStatmentNodeInstrumentedShould extends TestStatmentNodeInstrumented {
	
	public TestAnIfElseStatmentNodeInstrumentedShould() {
		super(new IfElseCFG());
	}
	
	@Test
	public void haveNodeOneNotCoveredWhenIfIsFalse() {
		traverse(0, 2, 3);
		Assert.assertTrue(Node.find(0, requirements()).isCovered());
		Assert.assertTrue(Node.find(2, requirements()).isCovered());
		Assert.assertTrue(Node.find(3, requirements()).isCovered());
		Assert.assertFalse(Node.find(1, requirements()).isCovered());
	}

	@Test
	public void haveNodeTwoNotCoveredWhenIfIsTrue() {
		traverse(0, 1, 3);
		Assert.assertTrue(Node.find(0, requirements()).isCovered());
		Assert.assertTrue(Node.find(1, requirements()).isCovered());
		Assert.assertTrue(Node.find(3, requirements()).isCovered());
		Assert.assertFalse(Node.find(2, requirements()).isCovered());
	}

}
