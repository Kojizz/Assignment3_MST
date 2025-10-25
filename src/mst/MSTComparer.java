package mst;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class MSTComparer {

    // Represents an edge between two nodes with a given weight
    static class Edge {
        String u, v;
        int w;
        Edge(String u, String v, int w){ this.u = u; this.v = v; this.w = w; }
    }

    // Represents an edge in the MST result for displaying
    static class ResultEdge {
        String from, to;
        int weight;
        ResultEdge(String from, String to, int weight){ this.from = from; this.to = to; this.weight = weight; }
    }

    //Holds the result of running an algorithm (Prim or Kruskal)
    static class AlgoResult {
        public Deque<mst.Edge> mstEdges;
        public int totalCost;
        public long operations;
        public double executionTimeMs;

        // Used internally for output formatting

        List<ResultEdge> mst_edges = new ArrayList<>();
        int total_cost = 0;
        long operations_count = 0;
        double execution_time_ms = 0.0;
    }

    // Disjoint Set (Union-Find) structure used in Kruskal’s algorithm
    static class UnionFind {
        Map<String, String> parent = new HashMap<>(); // parent map
        Map<String, Integer> rank = new HashMap<>();  // rank (tree depth)
        long ops; // count of operations

        // Initialize each node as its own parent
        UnionFind(Collection<String> nodes){
            for(String n: nodes){ parent.put(n,n); rank.put(n,0); }
            ops = 0;
        }

        // find the root (representative) of a node

        String find(String x){
            ops++;
            if(!parent.get(x).equals(x)){
                parent.put(x, find(parent.get(x))); // path compression
                ops++;
            }
            return parent.get(x);
        }

        // Union two sets if they are different
        boolean union(String a, String b){
            ops++;
            String ra = find(a);
            String rb = find(b);
            if(ra.equals(rb)) return false; // already connected

            // Attach the smaller tree to the larger one
            if(rank.get(ra) < rank.get(rb)) parent.put(ra, rb);
            else if(rank.get(ra) > rank.get(rb)) parent.put(rb, ra);
            else { parent.put(rb, ra); rank.put(ra, rank.get(ra)+1); ops++; }
            return true;
        }
    }

    //implementation of Prim’s MST algorithm
    static AlgoResult primMST(List<String> nodes, List<Edge> edges){
        AlgoResult res = new AlgoResult();
        long ops = 0;
        long start = System.nanoTime(); // start time measurement

        // Build adjacency list
        Map<String, List<Edge>> adj = new HashMap<>();
        for(String n: nodes) adj.put(n, new ArrayList<>());
        for(Edge e: edges){
            adj.get(e.u).add(new Edge(e.u, e.v, e.w));
            adj.get(e.v).add(new Edge(e.v, e.u, e.w));
            ops += 2;
        }

        Set<String> visited = new HashSet<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.w));

        // Start from the first vertex
        String startNode = nodes.get(0);
        visited.add(startNode);

        //add all edges from the starting node
        for(Edge e: adj.get(startNode)){ pq.add(e); ops++; }

        // continue until MST contains all vertices

        while(!pq.isEmpty() && visited.size() < nodes.size()){
            Edge smallest = pq.poll(); ops++;

            //Skip edges that lead to already visited nodes
            if(visited.contains(smallest.v)) { ops++; continue; }

            visited.add(smallest.v); ops++;
            res.mst_edges.add(new ResultEdge(smallest.u, smallest.v, smallest.w));
            res.total_cost += smallest.w; ops++;

            // add new edges from the newly added vertex
            for(Edge e: adj.get(smallest.v)){
                if(!visited.contains(e.v)){
                    pq.add(e); ops++;
                }
                ops++;
            }
        }

        // Calculate total time and operations
        long end = System.nanoTime();
        res.operations_count = ops;
        res.execution_time_ms = (end - start) / 1_000_000.0;
        return res;
    }

    // Implementation of Kruskal’s MST algorithm
    static AlgoResult kruskalMST(List<String> nodes, List<Edge> edges){
        AlgoResult res = new AlgoResult();
        long ops = 0;
        long start = System.nanoTime();

        // Sort all edges by their weight
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingInt(e -> e.w));
        ops += sorted.size();

        UnionFind uf = new UnionFind(nodes);
        ops += uf.ops;

        // iterate through sorted edges and connect disjoint sets
        for(Edge e: sorted){
            ops++;
            if(uf.find(e.u).equals(uf.find(e.v))){ ops++; continue; } // skip if cycle
            boolean merged = uf.union(e.u, e.v);
            ops += uf.ops; uf.ops = 0;
            if(merged){
                res.mst_edges.add(new ResultEdge(e.u, e.v, e.w));
                res.total_cost += e.w; ops++;
            }
            if(res.mst_edges.size() == nodes.size() - 1) break; // MST complete
        }

        long end = System.nanoTime();
        res.operations_count = ops;
        res.execution_time_ms = (end - start) / 1_000_000.0;
        return res;
    }

    public static void main(String[] args) throws Exception {
        String inputPath;

        // check if user provided a JSON file path as argument
        if (args.length > 0) {
            inputPath = args[0];
            System.out.println("Reading input from: " + inputPath);
        } else {
            // Default input file name
            inputPath = "input.json";
            System.out.println("No argument provided, using default file: " + inputPath);
        }

        // read the JSON file content
        String jsonText = new String(Files.readAllBytes(Paths.get(inputPath)));

        // Regular expression to extract graph data from JSON manually

        Pattern graphPattern = Pattern.compile("\\{\\s*\"id\":\\s*(\\d+).*?\\[([^\\]]+)\\].*?\\[([^\\]]+)\\]", Pattern.DOTALL);
        Matcher m = graphPattern.matcher(jsonText);

        // process each graph found in the JSON
        while(m.find()){
            int id = Integer.parseInt(m.group(1));

            // Extract vertex names
            List<String> nodes = new ArrayList<>();
            Matcher nodeMatcher = Pattern.compile("\"([A-Za-z0-9]+)\"").matcher(m.group(2));
            while(nodeMatcher.find()) nodes.add(nodeMatcher.group(1));

            // Extract edges

            List<Edge> edges = new ArrayList<>();
            Matcher edgeMatcher = Pattern.compile("\\{\"from\":\\s*\"(\\w+)\",\\s*\"to\":\\s*\"(\\w+)\",\\s*\"weight\":\\s*(\\d+)\\}").matcher(m.group(3));
            while(edgeMatcher.find()){
                edges.add(new Edge(edgeMatcher.group(1), edgeMatcher.group(2), Integer.parseInt(edgeMatcher.group(3))));
            }

            // Print graph summary
            System.out.println("\nGraph ID: " + id);
            System.out.println("Vertices: " + nodes.size() + ", Edges: " + edges.size());

            // run both algorithms
            AlgoResult prim = primMST(nodes, edges);
            AlgoResult kruskal = kruskalMST(nodes, edges);

            // Print Prim’s result
            System.out.println("\nPrim's Algorithm:");
            for(ResultEdge e: prim.mst_edges)
                System.out.println("  " + e.from + " - " + e.to + " (" + e.weight + ")");
            System.out.printf("Total Cost: %d, Ops: %d, Time: %.2f ms\n",
                    prim.total_cost, prim.operations_count, prim.execution_time_ms);

            // print Kruskal’s result

            System.out.println("\nKruskal's Algorithm:");
            for(ResultEdge e: kruskal.mst_edges)
                System.out.println("  " + e.from + " - " + e.to + " (" + e.weight + ")");
            System.out.printf("Total Cost: %d, Ops: %d, Time: %.2f ms\n",
                    kruskal.total_cost, kruskal.operations_count, kruskal.execution_time_ms);

            System.out.println("\n-----------------------------------");
        }
    }
}
