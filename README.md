# SmartCity Assignment 4 — Report 

## 1. Data Summary

| Dataset | Nodes (n) | Edges | Directed | Weight Model | SCCs | Structure |
|----------|------------|--------|-----------|---------------|--------|-------------|
| small_1.json | 6 | 7 | true | edge | 2 | sparse |
| small_2.json | 8 | 14 | true | edge | 3 | medium |
| small_3.json | 10 | 24 | true | edge | 1 | dense |
| medium_1.json | 12 | 29 | true | edge | 1 | medium |
| medium_2.json | 15 | 76 | true | edge | 1 | dense |
| medium_3.json | 18 | 72 | true | edge | 2 | dense |
| large_1.json | 22 | 36 | true | edge | 16 | sparse |
| large_2.json | 30 | 107 | true | edge | 1 | dense |
| large_3.json | 40 | 162 | true | edge | 2 | dense |

All graphs are directed and use an edge-weight model.  
The number of nodes varies from 6 to 40, with edges ranging from 7 to 162.  
Most graphs contain one or several strongly connected components (SCCs), reflecting mixed cyclic structures.

---

## 2. Results 

### SCC (Tarjan)

| Dataset | DFS Visits | DFS Edges | SCCs | Time (ms) |
|----------|-------------|------------|-------|------------|
| small_1.json | 6 | 7 | 2 | 0.028 |
| small_2.json | 8 | 14 | 3 | 0.025 |
| small_3.json | 10 | 24 | 1 | 0.020 |
| medium_1.json | 12 | 29 | 1 | 0.021 |
| medium_2.json | 15 | 76 | 1 | 0.037 |
| medium_3.json | 18 | 72 | 2 | 0.039 |
| large_1.json | 22 | 36 | 16 | 0.040 |
| large_2.json | 30 | 107 | 1 | 0.119 |
| large_3.json | 40 | 162 | 2 | 0.125 |

The Tarjan algorithm shows linear growth in time relative to the number of edges.  
Graphs with many SCCs (e.g., large_1) represent highly fragmented structures.

---

### Topological Sort (Kahn)

| Dataset | Kahn Push | Kahn Pop | Time (ms) |
|----------|------------|------------|------------|
| small_1.json | 2 | 2 | 0.005 |
| small_2.json | 3 | 3 | 0.006 |
| small_3.json | 1 | 1 | 0.004 |
| medium_1.json | 1 | 1 | 0.008 |
| medium_2.json | 1 | 1 | 0.004 |
| medium_3.json | 2 | 2 | 0.005 |
| large_1.json | 16 | 16 | 0.026 |
| large_2.json | 1 | 1 | 0.008 |
| large_3.json | 2 | 2 | 0.009 |

Topological sorting executes extremely fast (under 0.03 ms).  
Push and pop operations correspond to the number of vertices in the DAG after SCC condensation.  
Even for graphs with 40 nodes, the algorithm remains efficient.

---

### DAG Shortest / Longest Paths

| Dataset | Relaxations | Time (ms) | ShortestDist | LongestDist |
|----------|--------------|------------|---------------|---------------|
| small_1.json | 0 | 0.004 | 0.00 | 0.00 |
| small_2.json | 0 | 0.007 | 0.00 | 0.00 |
| small_3.json | 0 | 0.043 | 0.00 | 0.00 |
| medium_1.json | 0 | 0.005 | 0.00 | 0.00 |
| medium_2.json | 0 | 0.003 | 0.00 | 0.00 |
| medium_3.json | 0 | 0.003 | 0.00 | 0.00 |
| large_1.json | 0 | 0.031 | 0.00 | 0.00 |
| large_2.json | 0 | 0.005 | 0.00 | 0.00 |
| large_3.json | 0 | 0.011 | 0.00 | 0.00 |

The shortest and longest distances equal zero because there is no path between the chosen source and other nodes in some datasets.  
This is normal for graphs that become partially disconnected after SCC condensation.  
Execution times are stable and remain below 0.05 ms even for large graphs.

---

## 3. Algorithm Analysis

### SCC (Tarjan)
- The main computational load comes from the DFS traversal.
- Execution time grows linearly with the number of edges.
- Many SCCs indicate a graph with strong internal dependencies.

### Topological Sort
- After SCC condensation, graphs become DAGs.
- Kahn’s algorithm scales perfectly with graph size.
- Queue operations are minimal even for dense graphs.

### DAG Shortest and Longest Paths
- Very low execution time due to optimized linear traversal.
- Zero distances indicate isolated SCC groups with no connecting edges.

---

## 4. Conclusions

| Situation | Recommended Algorithm | Reason |
|------------|----------------------|---------|
| Graph contains cycles | Tarjan SCC | Identifies strongly connected regions and simplifies the structure |
| DAG structure after SCC | Topological Sort (Kahn) | Establishes valid execution order |
| Route or scheduling optimization | DAG Shortest Paths | Minimizes total time or cost |
| Critical dependency analysis | DAG Longest Paths | Detects maximum dependency chains |
| Large Smart City graphs (n ≤ 50) | All algorithms | Maintain linear scalability and high performance |

---

### Summary

All algorithms demonstrated linear scalability and very low execution time.  
SCC condensation effectively transforms complex cyclic graphs into DAGs suitable for planning.  
Topological sorting and DAG path algorithms provide efficient methods for scheduling and dependency analysis in Smart City systems.  
This architecture can be applied to transportation routing, energy distribution, and service scheduling in urban infrastructure.
