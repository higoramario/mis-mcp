package br.usp.each.inss.instrumentation.dua;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.opal.graphs.MaxProgramDFG;
import br.usp.each.opal.requirement.Dua;

public class TestMaxProgramDuaInstrumented 
	extends TestStatmentDuaInstrumented {
	
	public TestMaxProgramDuaInstrumented(Instrumentator instrumentator) {
		super(instrumentator, new MaxProgramDFG());
	}

	@Test
	public void test1() {
		// Example input {1}
		traverse(0, 9, 32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertTrue(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertFalse(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test
	public void test2() {
		// Example input {1, 2}
		traverse(0, 9, 15, 22, 26, 9, 32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertFalse(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertTrue(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test
	public void test3() {
		// Example input {2, 1}
		traverse(0, 9, 15, 26, 9, 32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertTrue(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertTrue(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test
	public void test4() {
		// Example input {1, 2, 3}
		traverse(0, 9, 15, 22, 26, 9, 15, 22, 26, 9,32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertFalse(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertTrue(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test
	public void test5() {
		// Example input {1, 3, 2}
		traverse(0, 9, 15, 22, 26, 9, 15, 26, 9, 32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertFalse(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertTrue(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test
	public void test6() {
		// Example input {2, 1, 3}
		traverse(0, 9, 15, 26, 9, 15, 22, 26, 9, 32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertFalse(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertTrue(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertTrue(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test(enabled = false) // Same path test 5
	public void test7() {
		// Example input {2, 3, 1}
		traverse(0, 9, 15, 22, 26, 9, 15, 26, 9, 32);
	}

	@Test
	public void test8() {
		// Example input {3, 1, 2}
		traverse(0, 9, 15, 26, 9, 15, 26, 9, 32);

		int var;
		var = statment.getVarByName("max").getId();
		Assert.assertTrue(Dua.find(0, 32, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(22, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("i").getId();
		Assert.assertTrue(Dua.find(0, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 22, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(26, 15, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(26, 26, var, requirements()).isCovered());

		var = statment.getVarByName("array").getId();
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 32, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 9, 15, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());

		var = statment.getVarByName("array[]").getId();
		Assert.assertFalse(Dua.find(0, 22, var, requirements()).isCovered());
		Assert.assertTrue(Dua.find(0, 15, 26, var, requirements()).isCovered());
		Assert.assertFalse(Dua.find(0, 15, 22, var, requirements()).isCovered());
	}

	@Test(enabled = false) // Same path test 8
	public void test9() {
		// Example input {3, 2, 1}
		traverse(0, 9, 15, 26, 9, 15, 26, 9, 32);
	}

}
