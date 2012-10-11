package br.usp.each.inss.instrumentation.dua;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.graphs.NextOddProgramDFG;
import br.usp.each.opal.requirement.Dua;

public class TestNextOddProgramDuaInstrumented
	extends TestStatmentDuaInstrumented {

	public TestNextOddProgramDuaInstrumented(Instrumentator instrumentator) {
		super(instrumentator, new NextOddProgramDFG());
	}

	@Test
	public void testIsOdd() {
		traverse(0, 6, 9);
		int var = statment.getVarByName("x").getId();
		// True
		Assert.assertTrue(Dua.find(0, 6, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(6, 9, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 0, 6, var, requirements()).isCovered());
		// False
		Assert.assertFalse(Dua.find(0, 0, 9, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, var, requirements()).isCovered());
	}

	@Test
	public void testNotOdd() {
		traverse( 0, 9);
		int var = statment.getVarByName("x").getId();
		// False
		Assert.assertFalse(Dua.find(0, 6, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(6, 9, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 0, 6, var, requirements()).isCovered());
		// True
		Assert.assertTrue(Dua.find(0, 0, 9, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, var, requirements()).isCovered());
	}

}
