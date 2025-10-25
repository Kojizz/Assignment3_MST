package mst;

import java.util.*;

public class UnionFind {
    private final Map<String, String> parent = new HashMap<>();
    private final Map<String, Integer> rank = new HashMap<>();
    public long operations = 0;

    public UnionFind(List<String> nodes) {
        for (String node : nodes) {
            parent.put(node, node);
            rank.put(node, 0);
        }
    }

    public String find(String x) {
        operations++;
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));
            operations++;
        }
        return parent.get(x);
    }

    public boolean union(String a, String b) {
        operations++;
        String rootA = find(a);
        String rootB = find(b);
        if (rootA.equals(rootB)) return false;
        if (rank.get(rootA) < rank.get(rootB)) parent.put(rootA, rootB);
        else if (rank.get(rootA) > rank.get(rootB)) parent.put(rootB, rootA);
        else {
            parent.put(rootB, rootA);
            rank.put(rootA, rank.get(rootA) + 1);
            operations++;
        }
        return true;
    }
}