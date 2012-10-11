package br.usp.each.opal.requirement;

public class MethodCallTriple extends Requirement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4605846704660257236L;
	private int idMethodCaller;
	private int idMethodCalledN1;
	private int idMethodCalledN2;
	private String classCaller;
	private String classCalledN1;
	private String classCalledN2;

	public MethodCallTriple(int idCaller, int idCalledN1, int idCalledN2, String classCaller, String classCalledN1, String classCalledN2) {
		this.idMethodCaller = idCaller;
		this.idMethodCalledN1 = idCalledN1;
		this.idMethodCalledN2 = idCalledN2;
		this.classCaller = classCaller;
		this.classCalledN1 = classCalledN1;
		this.classCalledN2 = classCalledN2;
	}
	
	public int getIdMethodCaller() {
		return idMethodCaller;
	}

	public int getIdMethodCalledN1() {
		return idMethodCalledN1;
	}

	public int getIdMethodCalledN2() {
		return idMethodCalledN2;
	}

	public String getClassCaller() {
		return classCaller;
	}

	public String getClassCalledN1() {
		return classCalledN1;
	}

	public String getClassCalledN2() {
		return classCalledN2;
	}

	public void setIdMethodCaller(int idMethodCaller) {
		this.idMethodCaller = idMethodCaller;
	}

	public void setIdMethodCalledN1(int idMethodCalledN1) {
		this.idMethodCalledN1 = idMethodCalledN1;
	}

	public void setIdMethodCalledN2(int idMethodCalledN2) {
		this.idMethodCalledN2 = idMethodCalledN2;
	}

	public void setClassCaller(String classCaller) {
		this.classCaller = classCaller;
	}

	public void setClassCalledN1(String classCalledN1) {
		this.classCalledN1 = classCalledN1;
	}

	public void setClassCalledN2(String classCalledN2) {
		this.classCalledN2 = classCalledN2;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('(');
		buffer.append(idMethodCaller);
		buffer.append(',');
		buffer.append(classCaller);
		buffer.append(',');
		buffer.append(idMethodCalledN1);
		buffer.append(',');
		buffer.append(classCalledN1);
		buffer.append(',');
		buffer.append(idMethodCalledN2);
		buffer.append(',');
		buffer.append(classCalledN2);
		buffer.append(',');
		buffer.append(isCovered());
		buffer.append(')');
		return buffer.toString();
	}

}
