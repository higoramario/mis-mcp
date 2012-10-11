package br.usp.each.opal.requirement;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.dataflow.DFGraph.Var;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.graphs.NextOddProgramDFG;

public class TestNextOddProgramDuasShould {

	private DFGraph nextOdd;
	private Dua[] duas;
	
	@BeforeTest
	public void init() {
		nextOdd = new NextOddProgramDFG();
		duas = new DuaDetermination().requirement(nextOdd);
	}
	
	@Test
	public void haveFiveDuas() {
		Assert.assertEquals(duas.length, 5);
	}
	
	@Test
	public void haveAnDefinitionAt0AndUseAt6OfVariableX() {
		Var var = nextOdd.getVarByName("x");
		ProgramBlock def = nextOdd.getProgramBlockById(0);
		ProgramBlock use = nextOdd.getProgramBlockById(6);
		Assert.assertNotNull(Dua.find(def.getId(), use.getId(), var.getId(), duas));
	}
	
	@Test
	public void haveAnDefinitionAt6AndUseAt9OfVariableX() {
		Var var = nextOdd.getVarByName("x");
		ProgramBlock def = nextOdd.getProgramBlockById(6);
		ProgramBlock use = nextOdd.getProgramBlockById(9);
		Assert.assertNotNull(Dua.find(def.getId(), use.getId(), var.getId(), duas));
	}
	
	@Test
	public void haveAnDefinitionAt0AndUseAt9OfVariableX() {
		Var var = nextOdd.getVarByName("x");
		ProgramBlock def = nextOdd.getProgramBlockById(0);
		ProgramBlock use = nextOdd.getProgramBlockById(9);
		Assert.assertNotNull(Dua.find(def.getId(), use.getId(), var.getId(), duas));
	}
	
	@Test
	public void haveAnDefinitionAt0AndUseAt0To9OfVariableX() {
		Var var = nextOdd.getVarByName("x");
		ProgramBlock def = nextOdd.getProgramBlockById(0);
		ProgramBlock usea = nextOdd.getProgramBlockById(0);
		ProgramBlock useb = nextOdd.getProgramBlockById(9);
		Assert.assertNotNull(Dua.find(def.getId(), usea.getId(), useb.getId(), var.getId(), duas));
	}
	
	@Test
	public void haveAnDefinitionAt0AndUseAt0To6OfVariableX() {
		Var var = nextOdd.getVarByName("x");
		ProgramBlock def = nextOdd.getProgramBlockById(0);
		ProgramBlock usea = nextOdd.getProgramBlockById(0);
		ProgramBlock useb = nextOdd.getProgramBlockById(6);
		Assert.assertNotNull(Dua.find(def.getId(), usea.getId(), useb.getId(), var.getId(), duas));
	}
	
}
