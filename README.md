MST Comparer — Prim’s vs Kruskal’s Algorithm
Overview

This project compares two classic algorithms for finding the Minimum Spanning Tree (MST) of a weighted undirected graph:

Prim’s Algorithm

Kruskal’s Algorithm

Both algorithms are implemented in Java and tested using graphs provided in a JSON input file.
The program measures:

Total cost of the MST

Number of operations performed

Execution time in milliseconds

This allows performance comparison between Prim’s and Kruskal’s approaches.

Features

- Reads graph data (nodes and edges) from a JSON file
- Implements both Prim’s and Kruskal’s MST algorithms
- Counts the total number of operations for complexity comparison
- Measures execution time for each algorithm
- Displays MST edges, total cost, and performance metrics

Prim’s Algorithm:

- Starts from any vertex and grows the MST by always adding the smallest edge that connects a new vertex to the existing tree.

- Uses a priority queue (min-heap) to select the minimum edge efficiently.

- Works best for dense graphs.

Kruskal’s Algorithm:

- Sorts all edges by weight and adds them one by one while avoiding cycles.

- Uses a Union-Find (Disjoint Set) data structure to detect cycles efficiently.

- Works well for sparse graphs.

Project Structure
MSTComparer.java
│
├── Edge                Represents a graph edge (u, v, weight)
├── ResultEdge          Represents an MST edge for output
├── AlgoResult          Stores result metrics (cost, ops, time)
├── UnionFind           Used by Kruskal’s Algorithm
├── primMST()           Implements Prim’s Algorithm
├── kruskalMST()        Implements Kruskal’s Algorithm
└── main()              Reads input, runs algorithms, prints results

Input Format

The program expects a JSON file (default: input.json) containing graph data like this:

[
  {
    "id": 1,
    "nodes": ["A", "B", "C", "D"],
    "edges": [
      {"from": "A", "to": "B", "weight": 4},
      {"from": "A", "to": "C", "weight": 2},
      {"from": "B", "to": "C", "weight": 1},
      {"from": "B", "to": "D", "weight": 5},
      {"from": "C", "to": "D", "weight": 3}
    ]
  }
]


Run MSTComparer

Run from Terminal
javac mst/MSTComparer.java
java mst.MSTComparer input.json


If no argument is provided, it will automatically use the default file input.json.

Example Output
Reading input from: input.json

Graph ID: 1
Vertices: 4, Edges: 5

Prim's Algorithm:
  A - C (2)
  C - B (1)
  C - D (3)
Total Cost: 6, Ops: 47, Time: 0.05 ms

Kruskal's Algorithm:
  B - C (1)
  A - C (2)
  C - D (3)
Total Cost: 6, Ops: 52, Time: 0.03 ms

Performance Metrics:
Algorithm	Total Cost	Operations	Execution Time (ms)
Prim’s	6	47	0.05
Kruskal’s	6	52	0.03

Both produce the same MST cost, but operation count and time may vary depending on graph structure.

Key learnings

- Prim’s is efficient for dense graphs because it grows locally.

- Kruskal’s is better for sparse graphs due to sorting and disjoint set merging.

- Using operation counts and timing shows how algorithmic complexity behaves in practice.

- Parsing JSON manually with regex helps avoid extra libraries and keeps code lightweight.

By:Rasul Ahmetov
