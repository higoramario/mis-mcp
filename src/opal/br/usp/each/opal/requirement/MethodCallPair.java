package br.usp.each.opal.requirement;

public class MethodCallPair extends Requirement {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3181120451102751789L;
	private int idMethodCaller;
	private int idMethodCalled;
	private String classCaller;
	private String classCalled;

	public MethodCallPair(int idCaller, int idCalled, String classCaller, String classCalled) {
		this.idMethodCaller = idCaller;
		this.idMethodCalled = idCalled;
		this.classCaller = classCaller;
		this.classCalled = classCalled;
	}

	public int getIdMethodCaller() {
		return idMethodCaller;
	}

	public int getIdMethodCalled() {
		return idMethodCalled;
	}

	public String getClassCaller() {
		return classCaller;
	}

	public String getClassCalled() {
		return classCalled;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('(');
		buffer.append(idMethodCaller);
		buffer.append(',');
		buffer.append(classCaller);
		buffer.append(',');
		buffer.append(idMethodCalled);
		buffer.append(',');
		buffer.append(classCalled);
		buffer.append(',');
		buffer.append(isCovered());
		buffer.append(')');
		return buffer.toString();
	}

}
