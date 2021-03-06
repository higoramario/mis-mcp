package br.usp.each.inss.instrumentation.traverse;

import java.util.Iterator;
import java.util.List;

import br.usp.each.inss.instrumentation.probe.AutoremoveProbe;
import br.usp.each.inss.instrumentation.probe.Probe;
import br.usp.each.inss.instrumentation.probe.RemoveConditionProbe;

public class DefaultProbeTraverseStrategy implements ProbeTraverseStrategy {

	@Override
	public void traverse(List<Probe> probes) {
		Iterator<Probe> it = probes.iterator();
		while(it.hasNext()) {
			Probe p = it.next();				
			p.execute();
			if(p instanceof AutoremoveProbe) {
				it.remove();
				continue;
			}					
			else if (p instanceof RemoveConditionProbe) {
				if (((RemoveConditionProbe) p).condition())
					it.remove();
				continue;
			}
		}
	}

}
