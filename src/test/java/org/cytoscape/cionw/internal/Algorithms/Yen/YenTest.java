package org.cytoscape.cionw.internal.Algorithms.Yen;

import org.cytoscape.cionw.internal.Algorithms.DataStructures.Path;
import org.cytoscape.cionw.internal.Algorithms.Dijkstra.Dijkstra;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Yen Algorithm
 * <p>
 * Created by A. Zeiser on 26.11.2016.
 */
public class YenTest {
    private CyNetwork network;
    private List<CyNode> nodes;
    private List<CyEdge> edges;

    @Before
    @SuppressWarnings("WeakerAccess")
    public void createTestNetwork() {
        NetworkTestSupport nts = new NetworkTestSupport();
        this.network = nts.getNetwork();
        // create 5 Nodes
        this.nodes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            nodes.add(network.addNode());
        }
        // connect Nodes
        this.edges = new ArrayList<>();
        // connection between node 0 and node 4 through all other nodes
        for (int i = 0; i < 4; i++) {
            edges.add(network.addEdge(nodes.get(i), nodes.get(i + 1), true));
        }
        // add some extra edges between Node1 and Node 2
        edges.add(network.addEdge(nodes.get(1), nodes.get(2), true));
        edges.add(network.addEdge(nodes.get(1), nodes.get(2), true));
        // add one extra edge between Node 2 and Node 3
        edges.add(network.addEdge(nodes.get(2), nodes.get(3), true));
        // get and set all EdgeWeights for Dijkstra
        GlobalSettings.getAllEdgeTypes(network);
    }

    @Test
    public void findKshortestPaths() throws Exception {
        createTestNetwork();
        // create Yen
        Yen yen = new Yen(network, nodes.get(0), nodes.get(4), 20);
        List<Path> foundedPaths = yen.findKshortestPaths(100);
        // create reference Path
        List<Path> expectedPath = new ArrayList<>();

        // find all 6 path via Dijkstra
        Dijkstra dj = new Dijkstra(network, nodes.get(0), nodes.get(4));
        // 1. Path
        List<CyEdge> ignoredEdges = new ArrayList<>();
        ignoredEdges.add(edges.get(4));
        ignoredEdges.add(edges.get(6));
        ignoredEdges.add(edges.get(5));
        expectedPath.add(dj.findShortestPath(ignoredEdges, null));
        // 2. Path
        ignoredEdges.clear();
        ignoredEdges.add(edges.get(4));
        ignoredEdges.add(edges.get(5));
        ignoredEdges.add(edges.get(2));
        expectedPath.add(dj.findShortestPath(ignoredEdges, null));
        // 3. Path
        ignoredEdges.clear();
        ignoredEdges.add(edges.get(1));
        ignoredEdges.add(edges.get(4));
        ignoredEdges.add(edges.get(6));
        expectedPath.add(dj.findShortestPath(ignoredEdges, null));
        // 4. Path
        ignoredEdges.clear();
        ignoredEdges.add(edges.get(1));
        ignoredEdges.add(edges.get(4));
        ignoredEdges.add(edges.get(2));
        expectedPath.add(dj.findShortestPath(ignoredEdges, null));
        // 5. Path
        ignoredEdges.clear();
        ignoredEdges.add(edges.get(4));
        ignoredEdges.add(edges.get(5));
        ignoredEdges.add(edges.get(6));
        expectedPath.add(dj.findShortestPath(ignoredEdges, null));
        // 6. Path
        ignoredEdges.clear();
        ignoredEdges.add(edges.get(4));
        ignoredEdges.add(edges.get(5));
        ignoredEdges.add(edges.get(2));
        expectedPath.add(dj.findShortestPath(ignoredEdges, null));

        // compare number of founded Paths
        if (foundedPaths.size() != expectedPath.size()) {
            Assert.fail("Found Paths and expected Paths not same size");
        }

        // Compare if founded path are the expected paths
        expectedPath.removeIf(foundedPaths::contains);
        if (!expectedPath.isEmpty()) {
            Assert.fail("Found not the right Paths.");
        }

    }

}