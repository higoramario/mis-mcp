package br.usp.each.opal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GraphMap<K> extends AbstractGraph<K> implements Graph<K> {

	private final Map<K, Set<K>> map = new LinkedHashMap<K, Set<K>>();
	
	public GraphMap() {
	}
	
	public GraphMap(Collection<K> nodes) {
		for (K k : nodes) {
			add(k);
		}
	}
	
	@Override
	public boolean add(K k) {
		if (!map.containsKey(k)) {
			map.put(k, new HashSet<K>());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean adjacent(K from, K to) {
		Set<K> set = map.get(from);
		if(set != null) {
			return set.contains(to);
		}
		return false;
	}

	@Override
	public Set<K> neighbors(K k) {
		return map.get(k);
	}

	@Override
	public boolean addEdge(K from, K to) {
		Set<K> set = map.get(from);
		if(set != null && map.containsKey(to)) {
			return set.add(to);
		}
		return false;
	}

	@Override
	public boolean removeEdge(K from, K to) {
		Set<K> set = map.get(from);
		if(set != null) {
			return set.remove(to);
		}
		return false;
	}
	
	@Override
	public int size() {
		return map.size();
	}
				
	public GraphMap<K> inverse() {
		GraphMap<K> g = new GraphMap<K>(map.keySet());
		for(K from : this) {
			for(K to : map.get(from)) {
				g.map.get(to).add(from);
			}
		}
		return g;
	}
	
	@Override
	public Iterator<K> iterator() {
		return map.keySet().iterator();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
		
}
