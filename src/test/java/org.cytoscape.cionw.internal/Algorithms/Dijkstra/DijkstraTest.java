package org.cytoscape.cionw.internal.Algorithms.Dijkstra;

import org.cytoscape.cionw.internal.Algorithms.DataStructures.NodeEdgePair;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Path;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Class for Dijkstra Algorithm
 * <p>
 * Created by A. Zeiser on 23.11.2016.
 */
public class DijkstraTest {
    private CyNetwork network;
    private List<CyNode> nodes;
    private List<CyEdge> edges;

    @Before
    public void createTestNetwork() {
        NetworkTestSupport nts = new NetworkTestSupport();
        this.network = nts.getNetwork();
        // create 5 Nodes
        this.nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            this.nodes.add(network.addNode());
        }

        // connect Nodes
        this.edges = new ArrayList<>();
        // connection between node 0 and node 4 through all other nodes
        for (int i = 0; i < 4; i++) {
            this.edges.add(network.addEdge(nodes.get(i), nodes.get(i + 1), true));
        }
        // direct connection between node 0 and 4
        this.edges.add(network.addEdge(nodes.get(0), nodes.get(4), true));


        // create two different EdgeTypes
        CyTable defaultEdgeTable = network.getDefaultEdgeTable();
        for (int i = 0; i < 5; i++) {
            (defaultEdgeTable.getRow(edges.get(i).getSUID())).set("interaction", "uses");
        }
        defaultEdgeTable.getRow(edges.get(3).getSUID()).set("interaction", "updates");

        // get all EdgeTypes weighted
        GlobalSettings.getAllEdgeTypes(network);
        GlobalSettings.edgeWeights.put("uses", 3.0);
        GlobalSettings.edgeWeights.put("updates", 100.0);
    }

    @Test
    public void findShortestPath() throws Exception {
        createTestNetwork();

        try {
            // create Dijkstra and find shortest Path
            Dijkstra dj = new Dijkstra(this.network, this.nodes.get(0), this.nodes.get(4));
            Path shortestPath = dj.findShortestPath(null, null);

            // expected Path
            Path expectedPath = new Path(network);
            expectedPath.add(new NodeEdgePair(nodes.get(0), null));
            expectedPath.add(new NodeEdgePair(nodes.get(4), edges.get(4)));

            Object[] shortestPathArray = shortestPath.toArray();
            Object[] expectedPathToArray = expectedPath.toArray();
            Assert.assertArrayEquals("Dijkstra found not the right shortest Path", expectedPathToArray, shortestPathArray);
        } catch (Exception e) {
            Assert.fail("Error while create Dijkstra or findShortestPath");
        }

        try {
            // create second Dijkstra with intermediate Node
            Dijkstra dj = new Dijkstra(this.network, this.nodes.get(0), this.nodes.get(4), this.nodes.get(3));
            Path shortestPath = dj.findShortestPath(null, null);
            Path expectedPath = new Path(network);
            expectedPath.add(new NodeEdgePair(nodes.get(0), null));
            expectedPath.add(new NodeEdgePair(nodes.get(1), edges.get(0)));
            expectedPath.add(new NodeEdgePair(nodes.get(2), edges.get(1)));
            expectedPath.add(new NodeEdgePair(nodes.get(3), edges.get(2)));
            expectedPath.add(new NodeEdgePair(nodes.get(4), edges.get(3)));

            Object[] shortestPathArray = shortestPath.toArray();
            Object[] expectedPathToArray = expectedPath.toArray();
            Assert.assertArrayEquals("Dijkstra found not the right shortest Path", expectedPathToArray, shortestPathArray);
        } catch (Exception e) {
            Assert.fail("Error while create Dijkstra or findShortestPath");
        }

    }


}