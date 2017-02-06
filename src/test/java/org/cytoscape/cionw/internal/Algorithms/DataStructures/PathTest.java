package org.cytoscape.cionw.internal.Algorithms.DataStructures;

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
 * Test Class for Path
 * <p>
 * Created by A. Zeiser on 22.12.2016.
 */
public class PathTest {
    private CyNetwork network;
    private List<CyNode> nodes;
    private List<CyEdge> edges;
    private Path testPath;

    @Before
    public void createPaths() {
        // Create whole new Network
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
        // create Test Path
        List<NodeEdgePair> pairs = new ArrayList<>();
        pairs.add(new NodeEdgePair(nodes.get(1), edges.get(1)));
        pairs.add(new NodeEdgePair(nodes.get(2), edges.get(2)));
        testPath = new Path(pairs, network);

    }

    @Test
    public void createPath() throws Exception {
        List<NodeEdgePair> pairs = new ArrayList<>();
        pairs.add(new NodeEdgePair(nodes.get(1), edges.get(1)));
        pairs.add(new NodeEdgePair(nodes.get(2), edges.get(2)));
        try {
            Path path = new Path(pairs, network);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void getLength() throws Exception {
        Assert.assertEquals(testPath.getLength(), 2);
    }

    @Test
    public void getWeight() throws Exception {
        Assert.assertEquals(testPath.getWeight(), 2.0, 0.0001);
    }

    @Test
    public void joinPaths() throws Exception {
        List<NodeEdgePair> pairs2 = new ArrayList<>();
        pairs2.add(new NodeEdgePair(nodes.get(2), edges.get(2)));
        pairs2.add(new NodeEdgePair(nodes.get(3), edges.get(3)));
        Path path2 = new Path(pairs2, network);

        path2.addAll(testPath);
        Assert.assertEquals(path2.getLength(), 4);
        Assert.assertEquals(path2.getWeight(), 4.0, 0.0001);
    }
}