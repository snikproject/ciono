package org.cytoscape.cionw.internal.Utils;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by A. Zeiser on 12.11.2016.
 * <p>
 * Static class for global variables (edge weighs, location of instance tables)
 */
public class GlobalSettings {
    public static HashMap<String, Double> edgeWeights; // Hash Map which store al weights of all available Nodes
    public static String InstanceDirectory;
    public static CyNode startNode;
    public static CyNode endNode;
    public static CyNode intermediateNode;
    public static String OutputDirector;
    public static String maxPathLength;
    public static String maxPaths;

    static {
        InstanceDirectory = System.getProperty("user.dir");
        OutputDirector = System.getProperty("user.dir");
        edgeWeights = new HashMap<>();
        maxPathLength = "20";
        maxPaths = "20";
    }

    /**
     * Make Class static
     */
    private GlobalSettings() {
    }

    /**
     * Set all edge types with weight 1.0 (default), if they are new. Identified by the column "interaction"
     *
     * @param network Input network (graph)
     */
    static public void getAllEdgeTypes(CyNetwork network) {
        List<CyEdge> edges = network.getEdgeList();
        for (CyEdge edge : edges) {
            String type = network.getRow(edge).get("interaction", String.class);
            if (!(edgeWeights.containsKey(type))) {
                edgeWeights.put(type, 1.0);
            }
        }
    }
}
