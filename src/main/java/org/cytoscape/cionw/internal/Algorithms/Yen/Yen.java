package org.cytoscape.cionw.internal.Algorithms.Yen;

import org.cytoscape.cionw.internal.Algorithms.DataStructures.NodeEdgePair;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Path;
import org.cytoscape.cionw.internal.Algorithms.Dijkstra.Dijkstra;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by a. Zeiser on 10.11.2016.
 * <p>
 * Class for the k-shortest Path algorithm from Yen
 */
public class Yen {
    private List<Path> a; // list of all shortest Paths
    private List<Path> b; // list of all possible candidates for a
    private CyNetwork network;
    private CyNode startNode;
    private CyNode endNode;
    private int k;

    /**
     * Constructor for Yen Class
     *
     * @param cyNetwork current Network (Graph)
     * @param startNode starting Node
     * @param endNode   finish Node
     * @param k         number of searched Paths
     */
    public Yen(CyNetwork cyNetwork, CyNode startNode, CyNode endNode, int k) {
        this.network = cyNetwork;
        this.startNode = startNode;
        this.endNode = endNode;
        this.k = k;
        a = new ArrayList<>();
        b = new ArrayList<>();
    }

    /**
     * Found k-shortest Paths
     * @param maxPathLength maximum Path Length of each Path
     * @return List of Paths
     */
    public List<Path> findKshortestPaths(int maxPathLength) {
        a.clear();
        b.clear();
        // get first shortest path as starting path
        Dijkstra dijkstra = new Dijkstra(network, startNode, endNode);
        Path shortestPath = dijkstra.findShortestPath(null, null);
        if (shortestPath != null) {
            if (shortestPath.size() >= maxPathLength) {
                JOptionPane.showMessageDialog(null, "Found Path is to long. Set a higher path length! (at least " + (shortestPath.size() + 1) + "!");
            } else {
                a.add(shortestPath);
            }
        }
        // find all k Paths
        for (int count = 1; count < k; count++) {
            // iterate through every Node in one Path
            for (int i = 0; (a.size() >= count) && (i < a.get(count - 1).size()); i++) {
                // get last founded path in a
                Path lastPath = a.get(count - 1);
                // create copy of the root Path (R)
                Path rootPath = new Path(new ArrayList<>(lastPath.subList(0, i + 1)), network);
                // iterate through every Path in a and check if a Path contains the current root path (Node 0 .. Node i) as a subsequent
                // if so: add the Edge between Node i and Node i+1 from the path to ignoredEdges. This is equivalent to removing the edge or setting
                // its weight to INF
                List<CyEdge> ignoredEdges = new ArrayList<>();
                for (Path path : a) {
                    if ((path.size() > i + 1) && (rootPath.equals(path.subList(0, i + 1)))) {
                        ignoredEdges.add(path.get(i + 1).getEdge());
                    }
                }
                // ignore all Nodes, which are part of the current root path, but not the last one (its the current start node for Dijkstra)
                // this suppress Dijkstra to find paths which goes back to a visited Node
                List<CyNode> ignoredNodes = new ArrayList<>();
                for (NodeEdgePair aRootPath : rootPath) {
                    ignoredNodes.add(aRootPath.getNode());
                }
                // find new shortest Path with Dijkstra from Node i to endNode and store it as Spur (S)
                Dijkstra dj = new Dijkstra(network, lastPath.get(i).getNode(), endNode);
                Path spurPath = dj.findShortestPath(ignoredEdges, ignoredNodes);
                // if Dijkstra founded a Path (the Spur S), then concatenate Root and Spur to a new Path
                // ad this Path if its not already in b
                if (spurPath != null && !spurPath.isEmpty()) {
                    rootPath.addAll(spurPath.subList(1, spurPath.size()));
                    if (!b.contains(rootPath)) {
                        b.add(rootPath);
                    }
                }
            }
            // stop, if there are no new candidates for a
            if (b.isEmpty()) {
                break;
            }
            // get the shortest Path (smallest weight) from the candidates (b) and add it to a
            b.sort((Comparator.comparingDouble(Path::getWeight)));
            Path newPath = b.get(0);
            if (!a.contains(newPath)) {
                a.add(b.get(0));
                b.remove(newPath);
            } else {
                b.remove(newPath);
            }
        }
        // return a, it contains all founded paths
        return a;
    }
}
