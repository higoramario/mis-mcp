package br.usp.each.inss.instrumentation.node;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.IfCFG;
import br.usp.each.opal.requirement.Node;

public class TestAnIfStatmentNodeInstrumentedShould extends TestStatmentNodeInstrumented {
	
	public TestAnIfStatmentNodeInstrumentedShould() {
		super(new IfCFG());
	}

	@Test
	public void haveAllNodesCoveredWhenIfIsTrue() {
		traverse(0, 1, 2);
		for (Node node : requirements()) {
			Assert.assertTrue(node.isCovered());
		}
	}
	
	@Test
	public void haveNodeOneNotCoveredWhenIfIsFalse() {
		traverse(0, 2);
		Assert.assertTrue(Node.find(0, requirements()).isCovered());
		Assert.assertTrue(Node.find(2, requirements()).isCovered());
		Assert.assertFalse(Node.find(1, requirements()).isCovered());
	}

}
