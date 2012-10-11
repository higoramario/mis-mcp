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

public class BitwiseDuaCoverage16 implements Instrumentator {
	
	@Override
	public void instrument(Program p, Requirement[] requirement) {
		Dua[] duas = (Dua[]) requirement;
		
		BitSet alive = new BitSet(duas.length);
		BitSet sleepy = new BitSet(duas.length);
		BitSet covered = new BitSet(duas.length);
		
		BitSet temp = new BitSet(duas.length);

		for (int i = 0; i < p.size(); i++) {
			ExecutionEntry e = p.getExecutionEntry(i);
			ProgramBlock b = e.getProgramBlock();
			
			BitSets bitSets = new BitSets(duas, b);
			
			// [0,X,X]
			if (bitSets.potcov.isEmpty()) {
				// [0,0,X]
				if (bitSets.sleepy.isEmpty()) {
					// [0,0,0,X]
					if (bitSets.disabled.isEmpty()) {
						if (bitSets.born.isEmpty()) 
						   // [0,0,0,0]
						   e.addLast(new FastDuaCoverageProbeType00(sleepy));
						else
						   // [0,0,0,1]
						   e.addLast(new FastDuaCoverageProbeType01(alive, sleepy, bitSets));
					} // [0,0,1,X]
					else {
						if (bitSets.born.isEmpty()) 
						    // [0,0,1,0]
						    e.addLast(new FastDuaCoverageProbeType10(alive, sleepy, bitSets));
						else
						    // [0,0,1,1]
						    e.addLast(new FastDuaCoverageProbeType11(alive, sleepy, bitSets));
					}
				} 
				// [0,1,X]
				else {
					// [0,1,0,X]
					if (bitSets.disabled.isEmpty()) {
						if (bitSets.born.isEmpty()) 
						   // [0,1,0,0]
						   e.addLast(new FastDuaCoverageProbeType20(sleepy, bitSets));
						else
						   // [0,1,0,1]
						   e.addLast(new FastDuaCoverageProbeType21(alive, sleepy, bitSets));
					} // [0,1,1,X]
					else {
						if (bitSets.born.isEmpty()) 
						   // [0,1,1,0]
						   e.addLast(new FastDuaCoverageProbeType30(alive, sleepy, bitSets));
						else
						   // [0,1,1,1]
						   e.addLast(new FastDuaCoverageProbeType31(alive, sleepy, bitSets));
					}
				}
			}
			// [1,X,X]
			else {
				// [1,0,X]
				if (bitSets.sleepy.isEmpty()) {
					// [1,0,0]
					if (bitSets.disabled.isEmpty()) {
						if (bitSets.born.isEmpty()) 
						   // [1,0,0,0]
						   e.addLast(new FastDuaCoverageProbeType40(alive, sleepy, covered, bitSets, temp));
						else
						   // [1,0,0,1]
						   e.addLast(new FastDuaCoverageProbeType41(alive, sleepy, covered, bitSets, temp));
					}
					// [1,0,1]
					else {
						if (bitSets.born.isEmpty()) 
						   // [1,0,1,0]
						   e.addLast(new FastDuaCoverageProbeType50(alive, sleepy, covered, bitSets, temp));
						else
						   // [1,0,1,1]
						   e.addLast(new FastDuaCoverageProbeType51(alive, sleepy, covered, bitSets, temp));				
					} 
				}
				// [1,1,X]
				else {
					// [1,1,0]
					if (bitSets.disabled.isEmpty()) {
						if (bitSets.born.isEmpty()) 
						   // [1,1,0,0]
						   e.addLast(new FastDuaCoverageProbeType60(alive, sleepy, covered, bitSets, temp));
						else
						   // [1,1,0,1]
						   e.addLast(new FastDuaCoverageProbeType61(alive, sleepy, covered, bitSets, temp));				
					} 
					// [1,1,1]
					else {
						if (bitSets.born.isEmpty()) 
						   // [1,1,1,0]
						   e.addLast(new FastDuaCoverageProbeType70(alive, sleepy, covered, bitSets, temp));
						else
						   // [1,1,1,1]
						   e.addLast(new FastDuaCoverageProbeType71(alive, sleepy, covered, bitSets, temp));	
					}			
				}
			}

			if(p.isExit(b.getId())) {
				e.addLast(new UpdateCoverageProbe(duas, covered));
			}
		}
	}
	
	@Override
	public Program copy(Program p, long id) {
		
		BitSet alive = new BitSet();
		BitSet sleepy = new BitSet();
		
		BitSet temp = new BitSet();
		
		Program copy = p.cleanCopyWithId(id);
		for (int i = 0; i < p.size(); i++) {
			List<Probe> probes = p.getExecutionEntry(i).probes();
			ExecutionEntry e = copy.getExecutionEntry(i);
			for (Probe probe : probes) {
				if (probe instanceof FastDuaCoverageProbeType00) {
					FastDuaCoverageProbeType00 newProbe = new FastDuaCoverageProbeType00(sleepy);
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType01) {
					FastDuaCoverageProbeType01 fastProbe = (FastDuaCoverageProbeType01) probe;
					FastDuaCoverageProbeType01 newProbe = new FastDuaCoverageProbeType01(alive, sleepy);
					newProbe.born = fastProbe.born;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType10) {
					FastDuaCoverageProbeType10 fastProbe = (FastDuaCoverageProbeType10) probe;
					FastDuaCoverageProbeType10 newProbe = new FastDuaCoverageProbeType10(alive, sleepy);
					newProbe.disabled = fastProbe.disabled;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType11) {
					FastDuaCoverageProbeType11 fastProbe = (FastDuaCoverageProbeType11) probe;
					FastDuaCoverageProbeType11 newProbe = new FastDuaCoverageProbeType11(alive, sleepy);
					newProbe.born = fastProbe.born;
					newProbe.disabled = fastProbe.disabled;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType20) {
					FastDuaCoverageProbeType20 fastProbe = (FastDuaCoverageProbeType20) probe;
					FastDuaCoverageProbeType20 newProbe = new FastDuaCoverageProbeType20(sleepy);
					newProbe.sleepy = fastProbe.sleepy;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType21) {
					FastDuaCoverageProbeType21 fastProbe = (FastDuaCoverageProbeType21) probe;
					FastDuaCoverageProbeType21 newProbe = new FastDuaCoverageProbeType21(alive, sleepy);
					newProbe.born = fastProbe.born;
					newProbe.sleepy = fastProbe.sleepy;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType30) {
					FastDuaCoverageProbeType30 fastProbe = (FastDuaCoverageProbeType30) probe;
					FastDuaCoverageProbeType30 newProbe = new FastDuaCoverageProbeType30(alive, sleepy);
					newProbe.disabled = fastProbe.disabled;
					newProbe.sleepy = fastProbe.sleepy;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType31) {
					FastDuaCoverageProbeType31 fastProbe = (FastDuaCoverageProbeType31) probe;
					FastDuaCoverageProbeType31 newProbe = new FastDuaCoverageProbeType31(alive, sleepy);
					newProbe.born = fastProbe.born;
					newProbe.disabled = fastProbe.disabled;
					newProbe.sleepy = fastProbe.sleepy;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType40) {
					FastDuaCoverageProbeType40 fastProbe = (FastDuaCoverageProbeType40) probe;
					FastDuaCoverageProbeType40 newProbe = new FastDuaCoverageProbeType40(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType41) {
					FastDuaCoverageProbeType41 fastProbe = (FastDuaCoverageProbeType41) probe;
					FastDuaCoverageProbeType41 newProbe = new FastDuaCoverageProbeType41(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.born = fastProbe.born;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType50) {
					FastDuaCoverageProbeType50 fastProbe = (FastDuaCoverageProbeType50) probe;
					FastDuaCoverageProbeType50 newProbe = new FastDuaCoverageProbeType50(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.disabled = fastProbe.disabled;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType51) {
					FastDuaCoverageProbeType51 fastProbe = (FastDuaCoverageProbeType51) probe;
					FastDuaCoverageProbeType51 newProbe = new FastDuaCoverageProbeType51(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.born = fastProbe.born;
					newProbe.disabled = fastProbe.disabled;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType60) {
					FastDuaCoverageProbeType60 fastProbe = (FastDuaCoverageProbeType60) probe;
					FastDuaCoverageProbeType60 newProbe = new FastDuaCoverageProbeType60(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.sleepy = fastProbe.sleepy;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType61) {
					FastDuaCoverageProbeType61 fastProbe = (FastDuaCoverageProbeType61) probe;
					FastDuaCoverageProbeType61 newProbe = new FastDuaCoverageProbeType61(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.born = fastProbe.born;
					newProbe.sleepy = fastProbe.sleepy;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType70) {
					FastDuaCoverageProbeType70 fastProbe = (FastDuaCoverageProbeType70) probe;
					FastDuaCoverageProbeType70 newProbe = new FastDuaCoverageProbeType70(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.disabled = fastProbe.disabled;
					newProbe.sleepy = fastProbe.sleepy;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else if (probe instanceof FastDuaCoverageProbeType71) {
					FastDuaCoverageProbeType71 fastProbe = (FastDuaCoverageProbeType71) probe;
					FastDuaCoverageProbeType71 newProbe = new FastDuaCoverageProbeType71(alive, sleepy, fastProbe.coveredDuas);
					newProbe.potcov = fastProbe.potcov;
					newProbe.born = fastProbe.born;
					newProbe.disabled = fastProbe.disabled;
					newProbe.sleepy = fastProbe.sleepy;
					newProbe.temp = temp;
					e.addLast(newProbe);
				} else {
					e.addLast(probe);
				}
			}
		}
		return copy;
	}

	private static class FastDuaCoverageProbeType00 implements Probe {
		
			
		private BitSet sleepyDuas;
		
		/**
		 * gen = 0 and sleepy = 0 and (kill == 0 and born == 0) [0,0,0,0] 
		 */
		public FastDuaCoverageProbeType00(BitSet sleepyDuas) {
			this.sleepyDuas = sleepyDuas;
		}

		@Override
		public void execute() {
			sleepyDuas.clear();
		}

	}
	private static class FastDuaCoverageProbeType01 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		
		private BitSet born;
		
		public FastDuaCoverageProbeType01(BitSet aliveDuas, BitSet sleepyDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
		}
		
		/**
		 * gen = 0 and sleepy = 0 and (kill == 0 and born != 0) [0,0,0] 
		 */
		public FastDuaCoverageProbeType01(BitSet aliveDuas, BitSet sleepyDuas, BitSets bitSets) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			
			born = bitSets.born;
		}

		@Override
		public void execute() {
			aliveDuas.or(born);
			sleepyDuas.clear();
		}

	}
	private static class FastDuaCoverageProbeType10 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		
		private BitSet disabled;
		
		public FastDuaCoverageProbeType10(BitSet aliveDuas, BitSet sleepyDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
		}

		/**
		 * gen = 0 and sleepy = 0 and (kill != 0 and born = 0) [0,0,1,0]  
		 */
		public FastDuaCoverageProbeType10(BitSet aliveDuas, BitSet sleepyDuas, BitSets bitSets) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			
			disabled = bitSets.disabled;
		}

		@Override
		public void execute() {
			aliveDuas.andNot(disabled);
			sleepyDuas.clear();
		}

	}
	private static class FastDuaCoverageProbeType11 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		
		private BitSet born;
		private BitSet disabled;
		
		public FastDuaCoverageProbeType11(BitSet aliveDuas, BitSet sleepyDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
		}

		/**
		 * gen = 0 and sleepy = 0 and (kill != 0 and born != 0) [0,0,1,1]  
		 */
		public FastDuaCoverageProbeType11(BitSet aliveDuas, BitSet sleepyDuas, BitSets bitSets) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			
			born = bitSets.born;
			disabled = bitSets.disabled;
		}

		@Override
		public void execute() {
			aliveDuas.andNot(disabled);
			aliveDuas.or(born);
			sleepyDuas.clear();
		}

	}
	private static class FastDuaCoverageProbeType20 implements Probe {
		
		private BitSet sleepyDuas;
		
		private BitSet sleepy;
		
		public FastDuaCoverageProbeType20(BitSet sleepyDuas) {
			this.sleepyDuas = sleepyDuas;
		}

		/**
		 * gen = 0 and sleepy != 0  and (kill = 0  and born = 0) [0,1,0,0] 
		 */
		public FastDuaCoverageProbeType20(BitSet sleepyDuas, BitSets bitSets) {
			this.sleepyDuas = sleepyDuas;
			
			sleepy = bitSets.sleepy;
		}

		@Override
		public void execute() {
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}	
	private static class FastDuaCoverageProbeType21 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		
		private BitSet sleepy;
		private BitSet born;
		
		public FastDuaCoverageProbeType21(BitSet aliveDuas, BitSet sleepyDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
		}

		/**
		 * gen = 0 and sleepy != 0  and (kill = 0  and born != 0) [0,1,0,1] 
		 */
		public FastDuaCoverageProbeType21(BitSet aliveDuas, BitSet sleepyDuas, BitSets bitSets) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			
			sleepy = bitSets.sleepy;
			born = bitSets.born;
		}

		@Override
		public void execute() {
			aliveDuas.or(born);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}
	private static class FastDuaCoverageProbeType30 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		
		private BitSet disabled;
		private BitSet sleepy;
		
		public FastDuaCoverageProbeType30(BitSet aliveDuas, BitSet sleepyDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
		}

		/**
		 * gen = 0 and sleepy != 0 and (kill != 0 and born = 0) [0,1,1,0]
		 */
		public FastDuaCoverageProbeType30(BitSet aliveDuas, BitSet sleepyDuas, BitSets bitSets) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			
			disabled = bitSets.disabled;
			sleepy = bitSets.sleepy;
		}

		@Override
		public void execute() {
			aliveDuas.andNot(disabled);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}
	private static class FastDuaCoverageProbeType31 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		
		private BitSet born;
		private BitSet disabled;
		private BitSet sleepy;
		
		public FastDuaCoverageProbeType31(BitSet aliveDuas, BitSet sleepyDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
		}

		/**
		 * gen = 0 and sleepy != 0 and (kill != 0 and born != 0) [0,1,1,1]
		 */
		public FastDuaCoverageProbeType31(BitSet aliveDuas, BitSet sleepyDuas, BitSets bitSets) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			
			born = bitSets.born;
			disabled = bitSets.disabled;
			sleepy = bitSets.sleepy;
		}

		@Override
		public void execute() {
			aliveDuas.andNot(disabled);
			aliveDuas.or(born);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}
	private static class FastDuaCoverageProbeType40 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		
		public FastDuaCoverageProbeType40(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		/**
		 * gen != 0 and sleepy = 0 and (kill = 0  and born = 0) [1,0,0,0]
		 */
		public FastDuaCoverageProbeType40(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			sleepyDuas.clear();
		}

	}	
	private static class FastDuaCoverageProbeType41 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet born;
		
		public FastDuaCoverageProbeType41(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		/**
		 * gen != 0 and sleepy = 0 and (kill = 0  and born != 0) [1,0,0,1]
		 */
		public FastDuaCoverageProbeType41(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			born = bitSets.born;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			aliveDuas.or(born);
			sleepyDuas.clear();
		}

	}	
	private static class FastDuaCoverageProbeType50 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet disabled;
		
		public FastDuaCoverageProbeType50(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		/**
		 * gen != 0 and sleepy = 0 and (kill != 0  and born = 0) [1,0,1,0]
		 */
		public FastDuaCoverageProbeType50(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			disabled = bitSets.disabled;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			aliveDuas.andNot(disabled);
			sleepyDuas.clear();
		}

	}
	private static class FastDuaCoverageProbeType51 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet born;
		private BitSet disabled;
		
		public FastDuaCoverageProbeType51(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		/**
		 * gen != 0 and sleepy = 0 and (kill != 0  and born != 0) [1,0,1,1]
		 */
		public FastDuaCoverageProbeType51(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			born = bitSets.born;
			disabled = bitSets.disabled;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			aliveDuas.andNot(disabled);
			aliveDuas.or(born);
			sleepyDuas.clear();
		}

	}	
	private static class FastDuaCoverageProbeType60 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet sleepy;
		
		public FastDuaCoverageProbeType60(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}
		
		/**
		 * gen != 0 and sleepy != 0 and (kill = 0  and born = 0) [1,1,0,0] 
		 */
		public FastDuaCoverageProbeType60(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			sleepy = bitSets.sleepy;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}	
	private static class FastDuaCoverageProbeType61 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet sleepy;
		private BitSet born;
		
		public FastDuaCoverageProbeType61(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}
		
		/**
		 * gen != 0 and sleepy != 0 and (kill = 0  and born != 0) [1,1,0,1] 
		 */
		public FastDuaCoverageProbeType61(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			sleepy = bitSets.sleepy;
			born = bitSets.born;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			aliveDuas.or(born);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}
	private static class FastDuaCoverageProbeType70 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet disabled;
		private BitSet sleepy;
		
		public FastDuaCoverageProbeType70(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		/**
		 * gen != 0 and sleepy != 0 and  (kill != 0  and born = 0) [1,1,1,0]
		 */
		public FastDuaCoverageProbeType70(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			disabled = bitSets.disabled;
			sleepy = bitSets.sleepy;
		}

		@Override
		public void execute() {
			temp.clear();
			temp.or(aliveDuas);
			temp.andNot(sleepyDuas);
			temp.and(potcov);
			coveredDuas.or(temp);
			aliveDuas.andNot(disabled);
			sleepyDuas.clear();
			sleepyDuas.or(sleepy);
		}

	}
	private static class FastDuaCoverageProbeType71 implements Probe {
		
		private BitSet aliveDuas;
		private BitSet sleepyDuas;
		private BitSet coveredDuas;
		
		private BitSet temp;
		
		private BitSet potcov;
		private BitSet born;
		private BitSet disabled;
		private BitSet sleepy;
		
		public FastDuaCoverageProbeType71(BitSet aliveDuas, BitSet sleepyDuas, BitSet coveredDuas) {
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
		}

		/**
		 * gen != 0 and sleepy != 0 and  (kill != 0  and born != 0) [1,1,1,1]
		 */
		public FastDuaCoverageProbeType71(BitSet aliveDuas, BitSet sleepyDuas,
				BitSet coveredDuas, BitSets bitSets, BitSet temp) {
			
			this.aliveDuas = aliveDuas;
			this.sleepyDuas = sleepyDuas;
			this.coveredDuas = coveredDuas;
			
			this.temp = temp;
			
			potcov = bitSets.potcov;
			born = bitSets.born;
			disabled = bitSets.disabled;
			sleepy = bitSets.sleepy;
		}

		@Override
		public void execute() {
			temp.clear();
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
	
	private static class BitSets {
		
		private BitSet potcov;
		private BitSet born;
		private BitSet disabled;
		private BitSet sleepy;

		public BitSets(Dua[] duas, ProgramBlock b) {
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
		
	}

}
