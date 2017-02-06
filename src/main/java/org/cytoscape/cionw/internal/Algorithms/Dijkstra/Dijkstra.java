package org.cytoscape.cionw.internal.Algorithms.Dijkstra;

import org.cytoscape.cionw.internal.Algorithms.DataStructures.KeyValuePair;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.NodeEdgePair;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Path;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import java.util.*;

/**
 * Created by A. Zeiser on 09.11.2016.
 * <p>
 * Class for the Dijkstra Shortest Path Algorithm
 */
public class Dijkstra {
    private CyNetwork network;
    private PriorityQueue<KeyValuePair> nodes = new PriorityQueue<>(); // priority queue  with key = node, value = priority
    private CyNode startNode;
    private CyNode endNode;
    private CyNode interNode;
    private HashMap<CyNode, Double> dist = new HashMap<>(); // distance to start node
    private HashMap<CyNode, NodeEdgePair> predecessor = new HashMap<>(); // predecessor with the edge (where it comes from)

    /**
     * Default Constructor
     *
     * @param network   the current Graph
     * @param startNode starting Node
     * @param endNode   finish Node
     */
    public Dijkstra(CyNetwork network, CyNode startNode, CyNode endNode) {
        this.network = network;
        this.startNode = startNode;
        this.endNode = endNode;

        // initialize with startNode
        dist.put(startNode, 0.0);
        predecessor.put(startNode, null);
        nodes.add(new KeyValuePair(startNode, 0.0));
    }

    /**
     * Constructor for Dijkstra with Intermediate nodes
     *
     * @param network   current Graph
     * @param startNode starting Node
     * @param endNode   end Node
     * @param interNode intermediate Node
     */
    public Dijkstra(CyNetwork network, CyNode startNode, CyNode endNode, CyNode interNode) {
        this(network, startNode, endNode);
        this.interNode = interNode;
    }

    /**
     * Get the weight of a Edge form the Hash Map Edge-Weight
     *
     * @param edge CyEdge
     * @return Weight of the Edge
     */
    private double getWeightOfEdge(CyEdge edge) {
        return GlobalSettings.edgeWeights.get(network.getRow(edge).get(CyEdge.INTERACTION, String.class));
    }

    /**
     * Get the cheapest edge form a list, depends from the weight of the edge type
     *
     * @param edges CyEdge
     * @return cheapest CyEdge
     */
    private CyEdge getShortestEdge(List<CyEdge> edges) {
        CyEdge shortestEdge = edges.get(0);
        for (CyEdge edge : edges) {
            double edgeWeight = getWeightOfEdge(edge);
            double currentWeight = getWeightOfEdge(shortestEdge);
            if (edgeWeight < currentWeight) {
                shortestEdge = edge;
            }
        }
        return shortestEdge;
    }

    /**
     * Dijkstra Algorithm to find the shortest path. It will use intermediate Node if one is given from the Dijkstra Object.
     *
     * @param ignoredEdges Edges will be ignored in the network (equivalent to setting edge weight to infinity)
     * @param ignoredNodes Nodes will be ignored in the network (equivalent to delete the nodes)
     * @return List of Path
     */
    public Path findShortestPath(List<CyEdge> ignoredEdges, List<CyNode> ignoredNodes) {
        if (interNode == null) {
            return findSimpleShortestPath(ignoredEdges, ignoredNodes);
        } else {
            return findInterShortestPath();
        }
    }

    /**
     * Dijkstra Algorithm to find the shortest Path WITH intermediate Nodes
     *
     * @return List of Path
     */
    private Path findInterShortestPath() {
        Dijkstra dj = new Dijkstra(network, startNode, interNode);
        Path rootPath = dj.findShortestPath(null, null);
        List<CyEdge> ignoredEdgesFromRoot = new ArrayList<>();
        List<CyNode> ignoredNodesFromRoot = new ArrayList<>();
        for (int i = 0; i < rootPath.size() - 1; i++) {
            NodeEdgePair pair = rootPath.get(i);
            if (pair.getEdge() != null) {
                ignoredEdgesFromRoot.add(pair.getEdge());
            }
            ignoredNodesFromRoot.add(pair.getNode());
        }
        Dijkstra dj2 = new Dijkstra(network, interNode, endNode);
        Path spurPath = dj2.findShortestPath(ignoredEdgesFromRoot, ignoredNodesFromRoot);

        if ((!rootPath.isEmpty()) && (!spurPath.isEmpty())) {
            rootPath.addAll(spurPath.subList(1, spurPath.size()));
            return rootPath;
        } else return null;
    }

    /**
     * Dijkstra Algorithm to find the shortest path (without intermediate Nodes)
     *
     * @param ignoredEdges Edges will be ignored in the network (equivalent to setting edge weight to infinity)
     * @param ignoredNodes Nodes will be ignored in the network (equivalent to delete the nodes)
     * @return List of Path
     */
    private Path findSimpleShortestPath(List<CyEdge> ignoredEdges, List<CyNode> ignoredNodes) {
        Path path = new Path(this.network);

        // Calculate distance to all (reachable) nodes
        while (!nodes.isEmpty()) {
            CyNode currentNode = nodes.poll().getNode();
            // endNode found, stop while loop
            if (currentNode == endNode) {
                break;
            }
            // iterate over all reachable neighbors
            List<CyNode> neighbors = network.getNeighborList(currentNode, CyEdge.Type.ANY);
            if (neighbors != null) {
                for (CyNode neighbor : neighbors) {
                    List<CyEdge> connectingEdges = network.getConnectingEdgeList(currentNode, neighbor, CyEdge.Type.ANY);
                    // remove ignoredEdge form connectingEdges (equivalent to setting ignoredEdge Weight to infinity)
                    if (ignoredEdges != null && !ignoredEdges.isEmpty() && !connectingEdges.isEmpty()) {
                        connectingEdges.removeAll(ignoredEdges);
                    }
                    // remove edges from connecting Edges, which goes to a ignored Node
                    if (ignoredNodes != null && !ignoredNodes.isEmpty() && !connectingEdges.isEmpty()) {
                        // delete current Edge
                        connectingEdges.removeIf(edge -> ignoredNodes.contains(edge.getTarget()));
                    }
                    if (!connectingEdges.isEmpty()) {
                        CyEdge shortestEdge = getShortestEdge(connectingEdges);
                        double distance = getWeightOfEdge(shortestEdge);
                        double newDistance = distance + dist.get(currentNode);
                        if (!dist.containsKey(neighbor) || (newDistance < dist.get(neighbor))) {
                            dist.put(neighbor, newDistance);
                            predecessor.put(neighbor, new NodeEdgePair(currentNode, shortestEdge));
                            nodes.add(new KeyValuePair(neighbor, newDistance));
                        }
                    }
                }
            }
        }

        if (predecessor.containsKey(endNode) && predecessor.get(endNode) != null) {
            NodeEdgePair currentNodeEdgePair = new NodeEdgePair(endNode, predecessor.get(endNode).getEdge());
            path.add(currentNodeEdgePair);
            while (predecessor.get(currentNodeEdgePair.getNode()) != null &&
                    currentNodeEdgePair.getEdge() != null) {
                CyNode nextNode = predecessor.get(currentNodeEdgePair.getNode()).getNode();
                if (predecessor.get(nextNode) != null) {
                    CyEdge nextEdge = predecessor.get(nextNode).getEdge();
                    currentNodeEdgePair = new NodeEdgePair(nextNode, nextEdge);
                } else {
                    currentNodeEdgePair = new NodeEdgePair(nextNode, null);
                }
                path.add(currentNodeEdgePair);
            }
        }
        Collections.reverse(path);
        return path;
    }
}
