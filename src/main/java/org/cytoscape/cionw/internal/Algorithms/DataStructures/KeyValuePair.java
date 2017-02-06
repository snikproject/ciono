package org.cytoscape.cionw.internal.Algorithms.DataStructures;

import org.cytoscape.model.CyNode;

/**
 * Created by A. Zeiser on 09.11.2016.
 * <p>
 * Class to Store a Pair of CyNode and a priority.
 */
public class KeyValuePair implements Comparable<KeyValuePair> {
    private CyNode node;
    private double prior;

    /**
     * Default Constructor
     *
     * @param node  CyNode
     * @param prior Double
     */
    public KeyValuePair(CyNode node, double prior) {
        this.node = node;
        this.prior = prior;
    }

    /**
     * Get the Node of the Pair
     *
     * @return CyNode
     */
    public CyNode getNode() {
        return this.node;
    }

    /**
     * Get the Priority of the Node
     *
     * @return Priority
     */
    private double getPriority() {
        return this.prior;
    }

    @Override
    public int compareTo(KeyValuePair o) {
        return Double.compare(this.getPriority(), o.getPriority());
    }
}
