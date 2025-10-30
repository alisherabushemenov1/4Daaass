import com.google.gson.*;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPaths;
import utils.Graph;
import utils.Metrics;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * SmartCity Assignment 4
 * Main program to load all datasets, run SCC, TopoSort, DAG Shortest & Longest Paths,
 * and export metrics to report/results.csv
 */
public class Main {
    private static final String DATA_DIR = "data";
    private static final String REPORT_FILE = "report/results.csv";

    public static void main(String[] args) {
        System.out.println("=== SMART CITY REPORT GENERATOR ===");
        List<ResultRow> results = new ArrayList<>();
        try {
            Files.createDirectories(Paths.get("report"));
            Gson gson = new Gson();
            File folder = new File(DATA_DIR);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null) {
                System.err.println("No JSON files found in /data/");
                return;
            }

            for (File file : files) {
                System.out.println("\\n--- Processing: " + file.getName() + " ---");
                try (Reader reader = new FileReader(file)) {
                    JsonObject obj = gson.fromJson(reader, JsonObject.class);
                    boolean directed = obj.get("directed").getAsBoolean();
                    int n = obj.get("n").getAsInt();
                    int source = obj.get("source").getAsInt();
                    JsonArray edges = obj.getAsJsonArray("edges");

                    Graph g = new Graph(n, directed);
                    for (JsonElement el : edges) {
                        JsonObject e = el.getAsJsonObject();
                        g.addEdge(e.get("u").getAsInt(), e.get("v").getAsInt(), e.get("w").getAsDouble());
                    }
                    g.source = source;

                    Metrics mScc = new Metrics();
                    TarjanSCC tarjan = new TarjanSCC(g, mScc);
                    var comps = tarjan.run();
                    var condensation = TarjanSCC.buildCondensation(g, comps);

                    Metrics mTopo = new Metrics();
                    TopologicalSort topo = new TopologicalSort(condensation.dag, mTopo);
                    var topoOrder = topo.kahn();

                    Metrics mSp = new Metrics();
                    DAGShortestPaths sp = new DAGShortestPaths(condensation.dag, mSp);
                    var shortest = sp.shortestFrom(0);
                    var longest = sp.longestFrom(0);

                    double maxLongest = Arrays.stream(longest.dist).max().orElse(Double.NaN);
                    double minShortest = Arrays.stream(shortest.dist)
                            .filter(d -> !Double.isInfinite(d))
                            .min().orElse(Double.NaN);

                    results.add(new ResultRow(
                            file.getName(),
                            n,
                            g.edges().size(),
                            comps.size(),
                            mScc.dfsVisits,
                            mScc.dfsEdges,
                            mTopo.kahnPush,
                            mTopo.kahnPop,
                            mSp.relaxations,
                            mScc.elapsedMs(),
                            mTopo.elapsedMs(),
                            mSp.elapsedMs(),
                            minShortest,
                            maxLongest
                    ));

                    System.out.printf("SCC: %d comps, time=%.3fms | Topo=%.3fms | DAG-SP=%.3fms\\n",
                            comps.size(), mScc.elapsedMs(), mTopo.elapsedMs(), mSp.elapsedMs());

                } catch (Exception ex) {
                    System.err.println("Error reading " + file.getName() + ": " + ex.getMessage());
                }
            }

            writeCSV(results);
            System.out.println("\\n✅ Results saved to " + REPORT_FILE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeCSV(List<ResultRow> rows) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(REPORT_FILE))) {
            pw.println("Dataset,n,Edges,SCCs,DFS_Visits,DFS_Edges,Kahn_Push,Kahn_Pop,Relaxations,SCC_Time(ms),Topo_Time(ms),DAGSP_Time(ms),ShortestDist,LongestDist");
            for (ResultRow r : rows) {
                pw.printf("%s,%d,%d,%d,%d,%d,%d,%d,%d,%.3f,%.3f,%.3f,%.2f,%.2f%n",
                        r.name, r.n, r.edges, r.sccCount, r.dfsVisits, r.dfsEdges, r.kahnPush, r.kahnPop,
                        r.relaxations, r.sccTime, r.topoTime, r.dagspTime, r.shortestDist, r.longestDist);
            }
        }
    }

    private static class ResultRow {
        String name;
        int n, edges, sccCount;
        long dfsVisits, dfsEdges, kahnPush, kahnPop, relaxations;
        double sccTime, topoTime, dagspTime, shortestDist, longestDist;

        public ResultRow(String name, int n, int edges, int sccCount,
                         long dfsVisits, long dfsEdges, long kahnPush, long kahnPop, long relaxations,
                         double sccTime, double topoTime, double dagspTime,
                         double shortestDist, double longestDist) {
            this.name = name;
            this.n = n;
            this.edges = edges;
            this.sccCount = sccCount;
            this.dfsVisits = dfsVisits;
            this.dfsEdges = dfsEdges;
            this.kahnPush = kahnPush;
            this.kahnPop = kahnPop;
            this.relaxations = relaxations;
            this.sccTime = sccTime;
            this.topoTime = topoTime;
            this.dagspTime = dagspTime;
            this.shortestDist = shortestDist;
            this.longestDist = longestDist;
        }
    }
}
