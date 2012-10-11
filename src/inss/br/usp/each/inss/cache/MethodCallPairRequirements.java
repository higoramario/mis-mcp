package br.usp.each.inss.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.usp.each.inss.cache.Requirements.ClassRequirement;
import br.usp.each.opal.requirement.MethodCallPair;

public class MethodCallPairRequirements implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4602081514963627068L;
	private List<MethodCallPair> listMethodCall;
	private boolean failed= false;

	public MethodCallPairRequirements(){
		listMethodCall = new ArrayList<MethodCallPair>();
	}

	public List<MethodCallPair> getRequirements()
	{
		return listMethodCall;
	}

	public void putAll(List<MethodCallPair> methodCall)
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
		listMethodCall = new ArrayList<MethodCallPair>();
	}

}
