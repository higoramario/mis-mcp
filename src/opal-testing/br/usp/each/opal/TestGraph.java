package br.usp.each.opal;

import java.util.Arrays;
import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestGraph {

	private Graph<Integer> graph;

	@BeforeClass
	public void before() {
		graph = new GraphMap<Integer>();
	}

	@Test
	public void testAddNode() {
		// True
		Assert.assertTrue(graph.add(0));
		Assert.assertTrue(graph.add(1));
		Assert.assertTrue(graph.add(2));
		Assert.assertTrue(graph.add(3));
		Assert.assertTrue(graph.add(4));
		Assert.assertTrue(graph.add(5));
		// False (Duplicated)
		Assert.assertFalse(graph.add(0));
		Assert.assertFalse(graph.add(1));
		Assert.assertFalse(graph.add(2));
		Assert.assertFalse(graph.add(3));
		Assert.assertFalse(graph.add(4));
		Assert.assertFalse(graph.add(5));
		
		Assert.assertEquals(graph.size(), 6);
	}

	@Test(dependsOnMethods = "testAddNode")
	public void testAddEdge() {
		// True
		Assert.assertTrue(graph.addEdge(0, 1));
		Assert.assertTrue(graph.addEdge(1, 2));
		Assert.assertTrue(graph.addEdge(1, 3));
		Assert.assertTrue(graph.addEdge(2, 3));
		Assert.assertTrue(graph.addEdge(3, 2));
		// False (Duplicated)
		Assert.assertFalse(graph.addEdge(0, 1));
		Assert.assertFalse(graph.addEdge(1, 2));
		Assert.assertFalse(graph.addEdge(1, 3));
		Assert.assertFalse(graph.addEdge(2, 3));
		Assert.assertFalse(graph.addEdge(3, 2));
		// False (one node does not exists)
		Assert.assertFalse(graph.addEdge(640, 1));
		// False (another node does not exists)
		Assert.assertFalse(graph.addEdge(1, 617));
		// False (node does not exists)
		Assert.assertFalse(graph.addEdge(374, 853));

		// Adjacent tests
		Assert.assertTrue(graph.adjacent(0, 1));
		Assert.assertTrue(graph.adjacent(1, 2));
		Assert.assertTrue(graph.adjacent(1, 3));
		Assert.assertTrue(graph.adjacent(2, 3));
		Assert.assertTrue(graph.adjacent(3, 2));
		// False (one node does not exists)
		Assert.assertFalse(graph.adjacent(640, 1));
		// False (another node does not exists)
		Assert.assertFalse(graph.adjacent(1, 617));
		// False (node does not exists)
		Assert.assertFalse(graph.adjacent(374, 853));
		// False (no edge)
		Assert.assertFalse(graph.adjacent(4, 5));
	}

	@Test(dependsOnMethods = "testAddEdge")
	public void testRemoveEdge() {
		// True
		Assert.assertTrue(graph.removeEdge(2, 3));
		// False (one node does not exists)
		Assert.assertFalse(graph.removeEdge(640, 1));
		// False (another node does not exists)
		Assert.assertFalse(graph.removeEdge(1, 617));
		// False (node does not exists)
		Assert.assertFalse(graph.removeEdge(374, 853));
		// False (no edge)
		Assert.assertFalse(graph.removeEdge(2, 3));
		Assert.assertFalse(graph.removeEdge(4, 5));
	}

	@Test(dependsOnMethods = "testRemoveEdge")
	public void testNeighbors() {
		Assert.assertEquals(graph.neighbors(0), Arrays.asList(1));
		Assert.assertEquals(graph.neighbors(1), Arrays.asList(2, 3));
		Assert.assertEquals(graph.neighbors(2), Collections.EMPTY_SET);
		Assert.assertEquals(graph.neighbors(3), Arrays.asList(2));
		Assert.assertEquals(graph.neighbors(4), Collections.EMPTY_SET);
		Assert.assertEquals(graph.neighbors(5), Collections.EMPTY_SET);
		Assert.assertNull(graph.neighbors(7263));
	}
	
	@Test(dependsOnMethods = "testRemoveEdge")
	public void testInverse() {
		Graph<Integer> inverse = graph.inverse();
		Assert.assertEquals(inverse.size(), graph.size());
		Assert.assertTrue(inverse.adjacent(1, 0));
		Assert.assertTrue(inverse.adjacent(2, 1));
		Assert.assertTrue(inverse.adjacent(3, 1));
		Assert.assertTrue(inverse.adjacent(2, 3));		
	}

	@Test(dependsOnMethods = "testAddNode")
	public void testIterable() {
		Integer i = 0;
		for (Integer node : graph) {
			Assert.assertEquals(i, node);
			i++;
		}
	}

}
