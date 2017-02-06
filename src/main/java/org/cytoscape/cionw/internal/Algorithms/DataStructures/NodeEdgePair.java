package org.cytoscape.cionw.internal.Algorithms.DataStructures;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * Created by A. Zeiser on 09.11.2016.
 * <p>
 * Class to store a Pair of Node and a Edge. Needed to restore the route of a path
 */
public class NodeEdgePair {
    private CyNode node;
    private CyEdge edge;

    /**
     * Default Constructor
     *
     * @param node CyNode
     * @param edge CyEdge
     */
    public NodeEdgePair(CyNode node, CyEdge edge) {
        this.node = node;
        this.edge = edge;
    }

    /**
     * Get the Node of the Node Edge Pair
     *
     * @return CyNode
     */
    public CyNode getNode() {
        return this.node;
    }

    /**
     * Get the Edge of the Node Edge Pair
     *
     * @return CyEdge
     */
    public CyEdge getEdge() {
        return this.edge;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeEdgePair) {
            boolean edges = false;
            boolean nodes = false;
            NodeEdgePair other = (NodeEdgePair) obj;
            if (this.getEdge() == null && other.getEdge() == null) {
                edges = true;
            }
            if (this.getEdge() != null && other.getEdge() != null) {
                edges = this.getEdge().equals(other.getEdge());
            }
            if (this.getNode() == null && other.getNode() == null) {
                nodes = true;
            }
            if (this.getNode() != null && other.getNode() != null) {
                nodes = this.getNode().equals(other.getNode());
            }
            return (edges && nodes);
        }
        return false;
    }
}
