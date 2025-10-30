package graph.topo;

import utils.Graph;
import utils.Metrics;

import java.util.*;

public class TopologicalSort {
    private final Graph g;
    private final Metrics metrics;
    public TopologicalSort(Graph g, Metrics metrics){ this.g = g; this.metrics = metrics; }

    public List<Integer> kahn(){
        metrics.startTimer();
        int n = g.n;
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (Graph.Edge e : g.adj[u]) indeg[e.v]++;
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) { q.add(i); metrics.kahnPush++; }
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()){
            int u = q.removeFirst(); metrics.kahnPop++;
            order.add(u);
            for (Graph.Edge e : g.adj[u]){
                indeg[e.v]--;
                if (indeg[e.v] == 0){ q.add(e.v); metrics.kahnPush++; }
            }
        }
        metrics.stopTimer();
        return order;
    }
}
