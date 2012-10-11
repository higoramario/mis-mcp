package br.usp.each.opal.requirement;

import br.usp.each.opal.requirement.Use.Type;


public class Dua extends Requirement {

	private static final long serialVersionUID = -799794017000522210L;

	/**
	 * Id of Dua
	 */
	private int id;

	/**
	 * # of definition node
	 */
	private int def;

	/**
	 * node/arc Use
	 */
	private Use use;

	/**
	 * # of variable
	 */
	private int variable;
	
	public Dua(int id, int def, Use use, int variable) {
		this.id = id;
		this.def = def;
		this.use = use;
		this.variable = variable;
	}

	public int getId() {
		return id;
	}

	public int getDef() {
		return def;
	}

	public Use getUse() {
		return use;
	}

	public int getVariable() {
		return variable;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		buffer.append(def);
		buffer.append(',');
		buffer.append(use);
		buffer.append(',');
		buffer.append(variable);
		buffer.append(',');
		buffer.append(isCovered());
		buffer.append(']');
		return buffer.toString();
	}
	
	public static Dua find(int def, int use, int var, Dua[] duas) {
		for (Dua dua : duas) {
			if (dua.getUse().getType() == Type.P_USE)
				continue;

			if (dua.getDef() == def
					&& dua.getUse().getUseNode() == use
					&& dua.getVariable() == var) {
				return dua;
			}
		}
		return null;
	}
	
	public static Dua find(int def, int usea, int useb, int var, Dua[] duas) {
		for (Dua dua : duas) {
			if (dua.getUse().getType() == Type.C_USE)
				continue;

			PUse use = dua.getUse().PUse();
			if (dua.getDef() == def
					&& use.getOriginNode() == usea
					&& use.getDestNode() == useb
					&& dua.getVariable() == var) {
				return dua;
			}
		}
		return null;
	}
}
