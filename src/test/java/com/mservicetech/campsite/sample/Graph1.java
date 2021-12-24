package com.mservicetech.campsite.sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

public class Graph1 {

    private LinkedList<Integer> arj[];
    HashMap<Integer, TreeSet<Integer>> graph;

    private int V;
    public Graph1(int v) {
        V=v;
        arj=new LinkedList[v];
        graph = new HashMap<>();
        for (int i=0; i<v; i++) {
            arj[i] = new LinkedList<>();
            graph.put(i, new TreeSet<>());
        }
    }

    void addEdge(int v, int w) {
        arj[v].add(w);
        graph.get(v).add(w);
    }

    void DFSUtil(int v, boolean visited[] ) {
         visited[v] = true;
         System.out.print(v + " ");
        ListIterator listIterators = arj[v].listIterator();
        while (listIterators.hasNext()) {
            Integer i = (Integer) listIterators.next();
            if (!visited[i]) {
                DFSUtil(i, visited);
            }
        }

    }

    void DFS(int v)
    {
        // Mark all the vertices as
        // not visited(set as
        // false by default in java)
        boolean visited[] = new boolean[V];

        // Call the recursive helper
        // function to print DFS
        // traversal
        DFSUtil(v, visited);
    }


    public static void main(String[] args) {
        Graph1 g = new Graph1(4);

        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        g.addEdge(2, 3);
        g.addEdge(3, 3);

        g.DFS(0);
    }
}
