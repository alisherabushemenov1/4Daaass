package graph.scc;

import utils.Graph;
import utils.Metrics;

import java.util.*;

public class TarjanSCC {
    private final Graph g;
    private final int n;
    private int time = 0;
    private int[] disc, low;
    private boolean[] inStack;
    private Deque<Integer> stack = new ArrayDeque<>();
    private final List<List<Integer>> components = new ArrayList<>();
    private final Metrics metrics;

    public TarjanSCC(Graph g, Metrics metrics){
        this.g = g; this.n = g.n; this.metrics = metrics;
        disc = new int[n]; Arrays.fill(disc, -1);
        low = new int[n];
        inStack = new boolean[n];
    }

    public List<List<Integer>> run(){
        metrics.startTimer();
        for (int i = 0; i < n; i++) if (disc[i] == -1) dfs(i);
        metrics.stopTimer();
        return components;
    }

    private void dfs(int u){
        disc[u] = low[u] = time++;
        metrics.dfsVisits++;
        stack.push(u); inStack[u] = true;
        for (Graph.Edge e : g.adj[u]){
            metrics.dfsEdges++;
            int v = e.v;
            if (disc[v] == -1){
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]){
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        if (low[u] == disc[u]){
            List<Integer> comp = new ArrayList<>();
            while (true){
                int w = stack.pop(); inStack[w] = false;
                comp.add(w);
                if (w == u) break;
            }
            components.add(comp);
        }
    }

    // build condensation mapping: vertex -> compId and condensed DAG
    public static CondensationResult buildCondensation(Graph g, List<List<Integer>> comps){
        int k = comps.size();
        int[] compId = new int[g.n];
        for (int i = 0; i < k; i++){
            for (int v : comps.get(i)) compId[v] = i;
        }
        Graph dag = new Graph(k, true);
        Set<Long> seen = new HashSet<>();
        for (int u = 0; u < g.n; u++){
            for (Graph.Edge e : g.adj[u]){
                int a = compId[u], b = compId[e.v];
                if (a != b){
                    long key = ((long)a<<32) | (b & 0xffffffffL);
                    if (!seen.contains(key)){
                        dag.addEdge(a,b, e.w);
                        seen.add(key);
                    }
                }
            }
        }
        return new CondensationResult(compId, dag);
    }

    public static class CondensationResult{
        public final int[] compId; public final Graph dag;
        public CondensationResult(int[] compId, Graph dag){ this.compId = compId; this.dag = dag; }
    }
}
