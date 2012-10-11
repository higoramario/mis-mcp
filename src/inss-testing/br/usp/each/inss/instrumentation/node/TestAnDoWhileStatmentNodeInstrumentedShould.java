package br.usp.each.inss.instrumentation.node;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.opal.graphs.DoWhileCFG;
import br.usp.each.opal.requirement.Node;

public class TestAnDoWhileStatmentNodeInstrumentedShould extends TestStatmentNodeInstrumented {
	
	public TestAnDoWhileStatmentNodeInstrumentedShould() {
		super(new DoWhileCFG());
	}

	@Test
	public void haveAllNodesCoveredWhenWhileIsTrue() {
		traverse(0, 1, 0, 1, 2);
		for (Node node : requirements()) {
			Assert.assertTrue(node.isCovered());
		}
	}
	
	@Test
	public void haveAllNodesCoveredWhenWhileIsNeverTrue() {
		traverse(0, 1, 2);
		for (Node node : requirements()) {
			Assert.assertTrue(node.isCovered());
		}
	}

}
