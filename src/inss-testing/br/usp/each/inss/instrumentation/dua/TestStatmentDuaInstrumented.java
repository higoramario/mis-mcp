package br.usp.each.inss.instrumentation.dua;

import org.testng.Assert;
import org.testng.annotations.Test;

import br.usp.each.inss.instrumentation.Instrumentator;
import br.usp.each.inss.instrumentation.TestStatment;
import br.usp.each.opal.dataflow.DFGraph;
import br.usp.each.opal.requirement.Dua;
import br.usp.each.opal.requirement.DuaDetermination;

public abstract class TestStatmentDuaInstrumented extends TestStatment {
	
	public TestStatmentDuaInstrumented(Instrumentator instrumentator, DFGraph statment) {
		super(instrumentator, new DuaDetermination(), statment);
	}

	@Test
	public void haveAllDuasNotCoveredWhenStart() {
		for (Dua dua : requirements()) {
			Assert.assertFalse(dua.isCovered());
		}
	}
	
	@Override
	public Dua[] requirements() {
		return (Dua[]) super.requirements();
	}

}
