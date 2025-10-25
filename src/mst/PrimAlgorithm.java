package mst;

import java.util.*;

public class PrimAlgorithm {
    public static MSTComparer.AlgoResult run(Graph graph) {
        MSTComparer.AlgoResult res = new MSTComparer.AlgoResult();
        long ops = 0;
        long start = System.nanoTime();

        Map<String, List<Edge>> adj = graph.buildAdjacencyList();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        String startNode = graph.nodes.get(0);
        visited.add(startNode);
        pq.addAll(adj.get(startNode));
        ops += adj.get(startNode).size();

        while (!pq.isEmpty() && visited.size() < graph.nodes.size()) {
            Edge smallest = pq.poll(); ops++;
            if (visited.contains(smallest.to)) { ops++; continue; }
            visited.add(smallest.to); ops++;
            res.mstEdges.add(smallest);
            res.totalCost += smallest.weight;
            for (Edge e : adj.get(smallest.to)) {
                if (!visited.contains(e.to)) pq.add(e);
                ops++;
            }
        }

        res.operations = ops;
        res.executionTimeMs = (System.nanoTime() - start) / 1_000_000.0;
        return res;
    }
}