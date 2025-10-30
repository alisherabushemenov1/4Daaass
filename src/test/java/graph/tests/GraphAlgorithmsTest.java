package graph.tests;

import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPaths;
import utils.Graph;
import utils.Metrics;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmartCity Assignment 4
 * - SCC (Tarjan)
 * - Topological Sort (Kahn)
 * - DAG Shortest and Longest Paths
 */
public class GraphAlgorithmsTest {

    @Test
    public void testTarjanSimpleCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        Metrics m = new Metrics();
        TarjanSCC scc = new TarjanSCC(g, m);
        List<List<Integer>> comps = scc.run();

        assertEquals(1, comps.size(), "All vertices should form one SCC");
        assertEquals(3, comps.get(0).size(), "Component size should be 3");
    }

    @Test
    public void testTopologicalSortSimpleDAG() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 1);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort(g, m);
        List<Integer> order = topo.kahn();

        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testDAGShortestPath() {
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 4);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 7);
        g.addEdge(2, 4, 3);
        g.addEdge(3, 4, 1);

        Metrics m = new Metrics();
        DAGShortestPaths sp = new DAGShortestPaths(g, m);
        DAGShortestPaths.Result res = sp.shortestFrom(0);

        assertEquals(0.0, res.dist[0], 1e-6);
        assertEquals(3.0, res.dist[2], 1e-6);
        assertEquals(6.0, res.dist[4], 1e-6);
    }

    @Test
    public void testDAGLongestPath() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 4);

        Metrics m = new Metrics();
        DAGShortestPaths sp = new DAGShortestPaths(g, m);
        DAGShortestPaths.Result res = sp.longestFrom(0);

        assertEquals(9.0, res.dist[3], 1e-6);
        List<Integer> path = res.reconstructPath(3);
        assertEquals(Arrays.asList(0, 1, 2, 3), path);
    }
}
