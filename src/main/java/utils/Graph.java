package utils;

import java.util.*;

public class Graph {
    public final int n;
    public final boolean directed;
    public final List<Edge>[] adj;
    public int source = 0; // may be set from JSON

    @SuppressWarnings("unchecked")
    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
    }

    public static class Edge {
        public final int u, v;
        public final double w;
        public Edge(int u, int v, double w) { this.u = u; this.v = v; this.w = w; }
    }

    public void addEdge(int u, int v, double w) {
        adj[u].add(new Edge(u, v, w));
        if (!directed) adj[v].add(new Edge(v, u, w));
    }

    public List<Edge> edges() {
        List<Edge> out = new ArrayList<>();
        for (int i = 0; i < n; i++) out.addAll(adj[i]);
        return out;
    }
}
