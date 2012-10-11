package br.usp.each.inss.instrumentation.node;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.WhileCFG;
import br.usp.each.opal.requirement.Node;

public class TestAnWhileStatmentNodeInstrumentedShould extends TestStatmentNodeInstrumented {

	public TestAnWhileStatmentNodeInstrumentedShould() {
		super(new WhileCFG());
	}

	@Test
	public void haveAllNodesCoveredWhenWhileIsTrue() {
		traverse(0, 1, 0, 2);
		for (Node node : requirements()) {
			Assert.assertTrue(node.isCovered());
		}
	}

	@Test
	public void haveNodeOneNotCoveredWhenWhileIsNeverTrue() {
		traverse(0, 2);
		Assert.assertTrue(Node.find(0, requirements()).isCovered());
		Assert.assertTrue(Node.find(2, requirements()).isCovered());
		Assert.assertFalse(Node.find(1, requirements()).isCovered());
	}

}
