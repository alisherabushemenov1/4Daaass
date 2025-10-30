package utils;

public class Metrics {
    public long startTimeNs;
    public long endTimeNs;

    // counters
    public long dfsVisits = 0;
    public long dfsEdges = 0;
    public long kahnPush = 0;
    public long kahnPop = 0;
    public long relaxations = 0;

    public void startTimer(){ startTimeNs = System.nanoTime(); }
    public void stopTimer(){ endTimeNs = System.nanoTime(); }
    public double elapsedMs(){ return (endTimeNs - startTimeNs) / 1_000_000.0; }
}
