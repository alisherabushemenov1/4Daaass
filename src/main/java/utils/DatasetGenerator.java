package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatasetGenerator {
    // Generates 9 JSON files under ./data/
    public static void main(String[] args) throws IOException{
        new DatasetGenerator().generateAll();
    }

    private void generateAll() throws IOException{
        Random rnd = new Random(123);
        // small: 3 graphs
        genAndWrite(6, true, 0.15, rnd, "data/small_1.json");
        genAndWrite(8, true, 0.25, rnd, "data/small_2.json");
        genAndWrite(10, false, 0.10, rnd, "data/small_3.json");
        // medium
        genAndWrite(12, true, 0.20, rnd, "data/medium_1.json");
        genAndWrite(15, true, 0.35, rnd, "data/medium_2.json");
        genAndWrite(18, false, 0.12, rnd, "data/medium_3.json");
        // large
        genAndWrite(22, true, 0.08, rnd, "data/large_1.json");
        genAndWrite(30, true, 0.12, rnd, "data/large_2.json");
        genAndWrite(40, false, 0.05, rnd, "data/large_3.json");
        System.out.println("Datasets generated in ./data/");
    }

    private void genAndWrite(int n, boolean directed, double density, Random rnd, String path) throws IOException{
        List<int[]> edges = new ArrayList<>();
        int maxEdges = n * (n-1);
        int desired = Math.max(1, (int)(maxEdges * density));
        Set<Long> seen = new HashSet<>();
        while (edges.size() < desired){
            int u = rnd.nextInt(n), v = rnd.nextInt(n);
            if (u == v) continue;
            long key = ((long)u<<32) | (v & 0xffffffffL);
            if (seen.contains(key)) continue;
            seen.add(key);
            edges.add(new int[]{u,v, 1 + rnd.nextInt(10)});
        }
        // ensure at least one SCC in some graphs: add a small cycle sometimes
        if (n >= 4 && rnd.nextDouble() < 0.6){
            int a = rnd.nextInt(n-2), b = a+1, c = a+2;
            edges.add(new int[]{a,b, rnd.nextInt(5)+1});
            edges.add(new int[]{b,c, rnd.nextInt(5)+1});
            edges.add(new int[]{c,a, rnd.nextInt(5)+1});
        }

        // write JSON
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"directed\": %s,\n", directed));
        sb.append(String.format("  \"n\": %d,\n", n));
        sb.append("  \"edges\": [\n");
        for (int i = 0; i < edges.size(); i++){
            int[] e = edges.get(i);
            sb.append(String.format("    {\"u\": %d, \"v\": %d, \"w\": %d}", e[0], e[1], e[2]));
            if (i < edges.size()-1) sb.append(",\n");
            else sb.append("\n");
        }
        sb.append("  ],\n");
        sb.append(String.format("  \"source\": %d,\n", 0));
        sb.append("  \"weight_model\": \"edge\"\n");
        sb.append("}\n");

        try (FileWriter fw = new FileWriter(path)){
            fw.write(sb.toString());
        }
    }
}
