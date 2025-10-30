package graph.dagsp;

import utils.Graph;
import utils.Metrics;
import graph.topo.TopologicalSort;

import java.util.*;

public class DAGShortestPaths {
    private final Graph dag;
    private final Metrics metrics;
    public DAGShortestPaths(Graph dag, Metrics metrics){ this.dag = dag; this.metrics = metrics; }

    // shortest paths from source (edge weights, can be positive)
    public Result shortestFrom(int source){
        metrics.startTimer();
        TopologicalSort topo = new TopologicalSort(dag, metrics);
        List<Integer> order = topo.kahn();
        double[] dist = new double[dag.n];
        int[] parent = new int[dag.n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0;
        // process in topo order
        Map<Integer,Integer> pos = new HashMap<>();
        for (int i = 0; i < order.size(); i++) pos.put(order.get(i), i);
        // If source is not earlier, still safe because unreachable nodes stay INF
        for (int u : order){
            if (dist[u] == Double.POSITIVE_INFINITY) continue;
            for (Graph.Edge e : dag.adj[u]){
                metrics.relaxations++;
                int v = e.v; double nd = dist[u] + e.w;
                if (nd < dist[v]){ dist[v] = nd; parent[v] = u; }
            }
        }
        metrics.stopTimer();
        return new Result(dist, parent, order);
    }

    // longest path (critical) - works if no positive cycles (DAG)
    public Result longestFrom(int source){
        metrics.startTimer();
        TopologicalSort topo = new TopologicalSort(dag, metrics);
        List<Integer> order = topo.kahn();
        double[] dist = new double[dag.n];
        int[] parent = new int[dag.n];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0;
        for (int u : order){
            if (dist[u] == Double.NEGATIVE_INFINITY) continue;
            for (Graph.Edge e : dag.adj[u]){
                metrics.relaxations++;
                int v = e.v; double nd = dist[u] + e.w;
                if (nd > dist[v]){ dist[v] = nd; parent[v] = u; }
            }
        }
        metrics.stopTimer();
        return new Result(dist, parent, order);
    }

    public static class Result{
        public final double[] dist; public final int[] parent; public final List<Integer> topoOrder;
        public Result(double[] dist, int[] parent, List<Integer> topoOrder){ this.dist = dist; this.parent = parent; this.topoOrder = topoOrder; }

        public List<Integer> reconstructPath(int target){
            if (target < 0 || target >= parent.length) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            int cur = target;
            if (parent[cur] == -1 && Double.isInfinite(dist[cur])) return path; // unreachable
            while (cur != -1){ path.add(cur); cur = parent[cur]; }
            Collections.reverse(path);
            return path;
        }
    }
}
