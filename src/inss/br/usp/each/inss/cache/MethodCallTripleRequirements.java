package br.usp.each.inss.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.usp.each.inss.cache.Requirements.ClassRequirement;
import br.usp.each.opal.requirement.MethodCallTriple;

public class MethodCallTripleRequirements implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4192608840142614866L;
	private List<MethodCallTriple> listMethodCall;
	private boolean failed= false;

	public MethodCallTripleRequirements(){
		listMethodCall = new ArrayList<MethodCallTriple>();
	}

	public List<MethodCallTriple> getRequirements()
	{
		return listMethodCall;
	}

	public void putAll(List<MethodCallTriple> methodCall)
	{
		listMethodCall.addAll(methodCall);
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public boolean isFailed() {
		return failed;
	}
	
	public void reset() {
		failed = false;
		listMethodCall = new ArrayList<MethodCallTriple>();
	}

}
