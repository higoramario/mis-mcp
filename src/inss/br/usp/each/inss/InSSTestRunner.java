package br.usp.each.inss;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import br.usp.each.inss.bytecode.InSSProbe;
import br.usp.each.inss.executor.SimulatorMethodCallPair;
import br.usp.each.inss.executor.SimulatorMethodCallPair;
import br.usp.each.inss.executor.SimulatorMethodCallPrintStack;
import br.usp.each.inss.executor.SimulatorMethodCallTriple;
import br.usp.each.inss.executor.SimulatorMethodCallTriple;

public class InSSTestRunner  {

	public static void main(String[] args) throws ClassNotFoundException {
		JUnitCore core = new JUnitCore();
		core.addListener(new InSSTestRunListener());
		for (String arg : args) {
			core.run(Class.forName(arg));
		}
	}

	private static class InSSTestRunListener extends RunListener {

		private Set<String> failedMethods = new HashSet<String>();

		@Override
		public void testFailure(Failure failure) throws Exception {
			super.testFailure(failure);

			failedMethods.add(
					failure.getDescription().getMethodName());
		}
		@Override
		public void testFinished(Description description) throws Exception {
			super.testFinished(description);

			boolean failed = failedMethods.contains(description.getMethodName());
			String fail = "S";//HIGOR
			if(failed){
				if(InSSProbe.simulator() instanceof br.usp.each.inss.executor.SimulatorMethodCallPair){//HIGOR
					SimulatorMethodCallPair simMetCallPair = (SimulatorMethodCallPair) InSSProbe.simulator();//HIGOR
					simMetCallPair.methodCallRequirements().setFailed(true);
				}//HIGOR
				else if(InSSProbe.simulator() instanceof br.usp.each.inss.executor.SimulatorMethodCallTriple){
					SimulatorMethodCallTriple simMetCallTriple = (SimulatorMethodCallTriple) InSSProbe.simulator();//HIGOR
					simMetCallTriple.methodCallRequirements().setFailed(true);
				}
				else{//HIGOR
					InSSProbe.simulator().requirements().setFailed(true);
				}//HIGOR
				fail = "F";//HIGOR
			}
			String reqType=setRequirementType();//HIGOR
			InSSProbe.simulator().exportRequirements(description.getClassName() + "." + description.getMethodName()+"_"+fail+"."+reqType);
			
			if(InSSProbe.simulator() instanceof br.usp.each.inss.executor.SimulatorMethodCallPair){//HIGOR
				SimulatorMethodCallPair simMetCallPair = (SimulatorMethodCallPair) InSSProbe.simulator();//HIGOR
				//simMetCallPair.methodCallRequirements().reset();//HIGOR
				simMetCallPair.reset();
			}
			else if(InSSProbe.simulator() instanceof br.usp.each.inss.executor.SimulatorMethodCallTriple){//HIGOR
				SimulatorMethodCallTriple simMetCallTriple = (SimulatorMethodCallTriple) InSSProbe.simulator();//HIGOR
				//simMetCallTriple.methodCallRequirements().reset();//HIGOR
				simMetCallTriple.reset();
			}
			else if(InSSProbe.simulator() instanceof br.usp.each.inss.executor.SimulatorMethodCallPrintStack){//HIGOR
				SimulatorMethodCallPrintStack simMetCallPrintStack = (SimulatorMethodCallPrintStack) InSSProbe.simulator();//HIGOR
				simMetCallPrintStack.reset();//HIGOR
			}
			else{
				InSSProbe.simulator().requirements().reset();
			}
		}

		@Override
		public void testRunFinished(Result result) throws Exception {
			//super.testRunFinished(result);
			Failure failure;
			String fileName,message;
			Iterator<Failure> itFailure = result.getFailures().iterator();
			while(itFailure.hasNext())
			{
				failure = itFailure.next();
				System.out.println(failure.getDescription().getClassName() + "." + failure.getDescription().getMethodName() + "." + setRequirementType()+"-out");
				fileName = failure.getDescription().getClassName() + "." + failure.getDescription().getMethodName() + "." + setRequirementType()+"-out";
				message = failure.getMessage() + "\n" + failure.getDescription() + "\n" + failure.getTrace() + "\n" + failure.getException();
				InSSProbe.simulator().exportTestInformation(fileName,message);
			}
		}

		@Override
		public void testStarted(Description description)
		{}

		//Include a type of extension in name of output coverage files
		public String setRequirementType()
		{
			String simulator = System.getProperty("simulator.strategy");

			if(simulator.contains("br.usp.each.inss.executor.SimulatorMethodCallPair")){
				return "mcp";
			}
			else if(simulator.contains("br.usp.each.inss.executor.SimulatorMethodCallTriple")){
				return "mct";
			}
			else if(simulator.contains("br.usp.each.inss.executor.SimulatorMethodCallPrintStack")){
				return "stack";
			}
			else{
				String strategy = System.getProperty("instrumentation.strategy");
				if(strategy.contains("br.usp.each.inss.instrumentation.AllNodes")){
					return "node";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.AllEdges")){
					return "edge";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.ASSET")){
					return "dua-asset";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.BitwiseDuaCoverage")){
					return "dua-bdc";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.BitwiseDuaCoverage16")){
					return "dua-bdc16";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.BitwiseDuaCoverage8")){
					return "dua-bdc8";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.CorrectedDemandDriven")){
					return "dua";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.CorrectedDemandDrivenCloneModified")){
					return "dua-cddcm";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.EmptyInstrumenter")){
					return "ei";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.MatrixBasedDuaCoverage")){
					return "dua-bdc";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.MatrixBasedDuaCoverageImproved")){
					return "dua-bdc";
				}
				if(strategy.contains("br.usp.each.inss.instrumentation.OptimizedDemandDriven")){
					return "dua-odd";
				}
			}
			return "none";
		}
	}

}
