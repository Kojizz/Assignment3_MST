package mst;

import java.util.*;

public class KruskalAlgorithm {
    public static AlgoResult run(Graph graph) {
        AlgoResult res = new AlgoResult();
        long ops = 0;
        long start = System.nanoTime();

        List<Edge> edges = new ArrayList<>(graph.edges);
        Collections.sort(edges, Comparator.comparingInt(e -> e.weight));
        ops += edges.size();

        UnionFind uf = new UnionFind(graph.nodes);

        for (Edge e : edges) {
            ops++;
            if (uf.find(e.from).equals(uf.find(e.to))) { ops++; continue; }
            if (uf.union(e.from, e.to)) {
                res.mstEdges.add(e);
                res.totalCost += e.weight;
            }
            ops += uf.operations;
            uf.operations = 0;
            if (res.mstEdges.size() == graph.nodes.size() - 1) break;
        }

        res.operations = ops;
        res.executionTimeMs = (System.nanoTime() - start) / 1_000_000.0;
        return res;
    }
}