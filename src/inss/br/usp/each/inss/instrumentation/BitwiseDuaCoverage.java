package br.usp.each.inss.instrumentation;

import java.util.BitSet;
import java.util.List;

import br.usp.each.inss.Program;
import br.usp.each.inss.Program.ExecutionEntry;
import br.usp.each.inss.instrumentation.probe.Probe;
import br.usp.each.opal.dataflow.ProgramBlock;
import br.usp.each.opal.requirement.Dua;
import br.usp.each.opal.requirement.Requirement;
import br.usp.each.opal.requirement.Use.Type;

public class BitwiseDuaCoverage implements Instrumentator {

	@Override
	public void instrument(Program p, Requirement[] requirement) {
		Dua[] duas = (Dua[]) requirement;
		
		BitSet alive = new BitSet(duas.length);
		BitSet sleepy = new BitSet(duas.length);
		BitSet covered = new BitSet(duas.length);
		
		for (int i = 0; i < p.size(); i++) {
			ExecutionEntry e = p.getExecutionEntry(i);
			ProgramBlock b = e.getProgramBlock();
			e.addLast(new FastDuaCoverageProbe(b, duas, alive, sleepy, covered));
			if(p.isExit(b.getId())) {
				e.addLast(new UpdateCoverageProbe(duas, covered));
			}
		}
	}
	
	@Override
	public Program copy(Program p, long id) {
		
		BitSet alive = new BitSet();
		BitSet sleepy = new BitSet();
		
		Program copy = p.cleanCopyWithId(id);
		for (int i = 0; i < p.size(); i++) {
			List<Probe> probes = p.getExecutionEntry(i).probes();
			ExecutionEntry e = copy.getExecutionEntry(i);
			for (Probe probe : probes) {
				if (probe instanceof FastDuaCoverageProbe) {
					FastDuaCoverageProbe fastProbe = (FastDuaCoverageProbe) probe;
					FastDuaCoverageProbe newProbe = new FastDuaCoverageProbe(alive, sleepy, fastProbe.coveredDuas);
					newProbe.duas = fastProbe.duas;
					newProbe.potcov = fastProbe.potcov;
					newProbe.born = fastProbe.born;
					newProbe.disabled = fastProbe.disabled;
					newProbe.sleepy = fastProbe.sleepy;
					e.addLast(newProbe);
				} else {
					e.addLast(probe);
				}
			}
		}
		return copy;
	}

	private static class FastDuaCoverageProbe implements Probe {
		
		private Dua[] duas;
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet potcov;
		private BitSet born;
		private BitSet disabled;
		private BitSet sleepy;
		
		public FastDuaCoverageProbe(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		public FastDuaCoverageProbe(ProgramBlock b, Dua[] duas,
				BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			
			this.duas = duas;
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			potcov = new BitSet(duas.length);
			born = new BitSet(duas.length);
			disabled = new BitSet(duas.length);
			sleepy = new BitSet(duas.length);

			for (Dua dua : duas) {
				if (dua.getUse().getUseNode() == b.getId())
					potcov.set(dua.getId());

				if (dua.getDef() == b.getId()) {
					born.set(dua.getId());
				}

				if (dua.getDef() != b.getId() && b.isDef(dua.getVariable()))
					disabled.set(dua.getId());
				
				if (dua.getUse().getType() == Type.P_USE) {
					int origin = dua.getUse().PUse().getOriginNode();
					if (origin != b.getId()) {
						sleepy.set(dua.getId());
					}
				}
			}
		}

		@Override
		public void execute() {
			BitSet temp = new BitSet(duas.length);
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			aliveDuas.andNot(disabled);
			aliveDuas.or(born);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}
	
	private static class UpdateCoverageProbe implements Probe {

		private Dua[] duas;
		private BitSet covered;

		public UpdateCoverageProbe(Dua[] duas, BitSet covered) {
			this.duas = duas;
			this.covered = covered;
		}

		@Override
		public void execute() {
			for (Dua dua : duas) {
				if(covered.get(dua.getId()))
					dua.cover();
			}
		}
		
	}

}
