package org.cytoscape.cionw.internal.Algorithms.DataStructures;

import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class for representing a Path.
 * Its a simple extension of a ArrayList. The extras are the path length and the weight of the whole path
 * <p>
 * Created by A. Zeiser on 26.11.2016.
 */
public class Path extends ArrayList<NodeEdgePair> {
    private double weight = 0;
    private CyNetwork network;

    /**
     * Disable Constructor form ArrayList
     *
     * @param initialCapacity
     */
    @SuppressWarnings("JavaDoc")
    private Path(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Disable Constructor from ArrayList
     *
     * @param c
     */
    @SuppressWarnings("JavaDoc")
    private Path(Collection<? extends NodeEdgePair> c) {
        super(c);
    }

    /**
     * Create a new path for a List of NodeEdgePairs and the corresponding CyNetwork
     *
     * @param pairs   List of all NodeEdgePairs for the new Path
     * @param network Current CyNetwork
     */
    public Path(List<NodeEdgePair> pairs, CyNetwork network) {
        this.network = network;
        for (NodeEdgePair pair : pairs) {
            this.add(pair);
        }
    }

    /**
     * Disable Default Constructor
     */
    private Path() {
    }

    /**
     * Create a new empty Path.
     *
     * @param network current CyNetwork
     */
    public Path(CyNetwork network) {
        this.network = network;
    }

    /**
     * Get the length of the Path
     *
     * @return return the length
     */
    public int getLength() {
        return this.size();
    }

    /**
     * Get the weight of the whole Path
     *
     * @return return the weight
     */
    public double getWeight() {
        updateWeight();
        return weight;
    }

    /**
     * Update the weight. Weight is the Sum of all Edge Weights
     */
    private void updateWeight() {
        weight = 0;
        for (NodeEdgePair pair : this) {
            CyEdge edge = pair.getEdge();
            if (edge != null) {
                weight += GlobalSettings.edgeWeights.get(network.getRow(edge).get(CyEdge.INTERACTION, String.class));
            }
        }
    }
}
