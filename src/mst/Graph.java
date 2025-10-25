package mst;

import java.util.*;

public class Graph {
    public List<String> nodes;
    public List<Edge> edges;

    public Graph(List<String> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    // Optional helper: build adjacency list
    public Map<String, List<Edge>> buildAdjacencyList() {
        Map<String, List<Edge>> adj = new HashMap<>();
        for (String n : nodes) adj.put(n, new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.from).add(new Edge(e.from, e.to, e.weight));
            adj.get(e.to).add(new Edge(e.to, e.from, e.weight));
        }
        return adj;
    }
}
