package org.cytoscape.cionw.internal.Ui;

import javafx.util.Pair;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.NodeEdgePair;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Path;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Triple;
import org.cytoscape.cionw.internal.Algorithms.Instances.InstanceJoin;
import org.cytoscape.cionw.internal.Algorithms.Instances.InstanceReader;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.*;
import org.slf4j.Logger;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Class for the Paths Table Panel (at south panel of Cytoscape)
 */
@SuppressWarnings({"WeakerAccess", "unchecked"})
public class PathTable extends JPanel implements CytoPanelComponent {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PathTable.class);
    public JTable pathTable;
    public JLabel lblFoundedPaths;
    public List<Path> paths;
    private CyNetwork network;

    /**
     * Create TablePanel and fill it with all founded Paths
     *
     * @param paths   Founded Paths
     * @param network Current CyNetwork
     */
    @SuppressWarnings({"WeakerAccess", "unchecked"})
    public PathTable(List<Path> paths, CyNetwork network) {
        this.network = network;
        this.paths = paths;

        /* **************** GUI **************** */
        lblFoundedPaths = new JLabel("Found Paths from to ");
        Component horizontalStrut = Box.createHorizontalStrut(20);
        JScrollPane scrollPane = new JScrollPane();
        JButton btnExportSelectedPath = new JButton("export selected path");
        // export Button Action
        //noinspection Convert2Lambda
        btnExportSelectedPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String out = tryToExport();
                if (!Objects.equals(out, "")) {
                    // instance missing
                    JOptionPane.showMessageDialog(null, "Can't Join Path, because one instance table is missing or empty: " + out);
                    LOGGER.info("Join Path not possible, because at least one instance table is missing or empty");
                }
            }
        });
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(lblFoundedPaths)
                                                .addPreferredGap(ComponentPlacement.RELATED, 348, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addGap(51)
                                                .addComponent(btnExportSelectedPath))
                                        .addComponent(horizontalStrut, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(horizontalStrut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(3)
                                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                        .addComponent(lblFoundedPaths)
                                                        .addComponent(btnExportSelectedPath))))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                .addContainerGap())
        );
        /* **************** GUI **************** */
        // fill Table
        if (this.paths != null && !this.paths.isEmpty()) {
            // sort paths by there Length
            this.paths.sort((o1, o2) -> Double.compare(o2.getLength(), o1.getLength()));
            Path firstPath = this.paths.get(0);
            int length = firstPath.size();

            List<String> colHeader = new ArrayList<>();
            colHeader.add("Length");
            colHeader.add("Weight");
            for (int i = 0; i < length; i++) {
                if (i != 0) {
                    colHeader.add("Edge");
                }
                colHeader.add("Node");
            }
            String[] headerArray = colHeader.toArray(new String[0]);

            DefaultTableModel tModel = new DefaultTableModel(null, headerArray);
            // fill TableModel with path data
            for (Path path : this.paths) {
                Vector v = new Vector();
                v.add(path.getLength());
                v.add(path.getWeight());
                for (int i = 0; i < path.size(); i++) {
                    NodeEdgePair pair = path.get(i);
                    if (i != 0) {
                        CyEdge edge = pair.getEdge();
                        String edgeName = network.getRow(edge).get("interaction", String.class);
                        v.add(edgeName);
                    }
                    CyNode node = pair.getNode();
                    String name = network.getRow(node).get(CyNetwork.NAME, String.class);
                    v.add(name);
                }
                tModel.addRow(v);
            }
            // create Table
            pathTable = new JTable(tModel);
            TableRowSorter rowSorter = new TableRowSorter(tModel);
            //noinspection Convert2Lambda
            rowSorter.setComparator(0, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 - o2;
                }

            });
            pathTable.setRowSorter(rowSorter);

            // add listener to highlight selected Path
            //noinspection Convert2Lambda
            pathTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() && pathTable.getSelectedRow() != -1) {
                        highlightPath();
                    }
                }
            });
            pathTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            scrollPane.setViewportView(pathTable);
            setLayout(groupLayout);
        }
    }

    /**
     * Function to select all Nodes and Edges from current selected Path in PathTable
     */
    private void highlightPath() {
        int selectedRows = this.pathTable.getSelectedRow();
        selectedRows = this.pathTable.convertRowIndexToModel(selectedRows);

        Path path = this.paths.get(selectedRows);
        CyTable edgeTable = network.getDefaultEdgeTable();
        CyTable nodeTable = network.getDefaultNodeTable();
        // deselect all other current selected edges
        Collection<CyRow> oldSelectedRows = edgeTable.getMatchingRows("selected", true);
        for (CyRow row : oldSelectedRows) {
            Long suid = row.get(edgeTable.getPrimaryKey().getName(), Long.class);
            if (suid != null) {
                edgeTable.getRow(row.get("SUID", Long.class)).set("selected", false);
            }
        }
        // deselect all other current selected nodes
        Collection<CyRow> oldSelectedNodes = nodeTable.getMatchingRows("selected", true);
        for (CyRow row : oldSelectedNodes) {
            Long suid = row.get(nodeTable.getPrimaryKey().getName(), Long.class);
            if (suid != null) {
                nodeTable.getRow(suid).set("selected", false);
            }
        }
        // for each Node and Edge of current selected path set Attribute "selected" to true
        for (NodeEdgePair pair : path) {
            if (pair.getEdge() != null) {
                edgeTable.getRow(pair.getEdge().getSUID()).set("selected", true);
            }
            nodeTable.getRow(pair.getNode().getSUID()).set("selected", true);
        }
    }

    /**
     * Function to Export selected Paths
     */
    private String tryToExport() {
        // get selected paths
        int[] selectedRows = this.pathTable.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            selectedRows[i] = this.pathTable.convertRowIndexToModel(selectedRows[i]);
        }
        if (selectedRows.length != 0) {
            // iterate over every selected row
            //noinspection LoopStatementThatDoesntLoop
            for (int rowIndex : selectedRows) {
                // get all Triple from relation instance Table (RelationID, ObjectID1, ObjectID2)
                List<List<Triple>> instanceTripleList = new ArrayList<>();
                // iterate over NodeEdgePair and fill List of Pair with Nodes Edge Node Edge ... instance pairs
                Path path = this.paths.get(rowIndex);
                List<Map.Entry<String, String>> compressPath = compressPath(path);
                HashSet<String> nodeNames = new HashSet<>();
                List<String> csvHeader = new ArrayList<>(); // create Header vor CSV

                for (int i = 0; i < compressPath.size(); i++) {
                    Map.Entry pair = compressPath.get(i);
                    if (i > 0) {
                        String edgeName = (String) pair.getValue();
                        String sourceName = compressPath.get(i - 1).getKey();
                        String targetName = (String) pair.getKey();
                        // read CSV and store triples in instanceTripleList
                        String filePath = GlobalSettings.InstanceDirectory + File.separator + edgeName + sourceName + targetName + ".txt";
                        List<Triple> instancesTriple;
                        // check if relation Table exists, if not check if relation table with inverse direction exists
                        File f = new File(filePath);
                        if (f.exists() && !f.isDirectory()) {
                            instancesTriple = InstanceReader.getInstancesTriple(filePath, false);
                        } else {
                            filePath = GlobalSettings.InstanceDirectory + File.separator + edgeName + targetName + sourceName + ".txt";
                            f = new File(filePath);
                            if (f.exists() && !f.isDirectory()) {
                                instancesTriple = InstanceReader.getInstancesTriple(filePath, true);
                            } else {
                                return filePath;
                            }
                        }
                        if (instancesTriple.isEmpty()) {
                            // stop whole export process and show which instance is missing
                            return filePath;
                        }
                        instanceTripleList.add(instancesTriple);
                        String nodeName = (String) pair.getKey();
                        nodeNames.add(nodeName);
                        csvHeader.add(edgeName);
                        csvHeader.add("ID");
                        csvHeader.add(nodeName);
                    } else {
                        String nodeName = (String) pair.getKey();
                        nodeNames.add(nodeName);
                        csvHeader.add("ID");
                        csvHeader.add(nodeName);
                    }
                }

                // join all Instances in instanceTripleList
                InstanceJoin iJ = new InstanceJoin();
                List<List<String>> joinedList = iJ.join(instanceTripleList);
                // get first all pairs of (ID, NodeName) from node instance tables
                HashMap<String, String> nodeIdName = new HashMap<>();
                for (String nodeName : nodeNames) {
                    String filePath = GlobalSettings.InstanceDirectory + File.separator + nodeName + ".txt";
                    for (Pair pair : InstanceReader.getInstancesPair(filePath)) {
                        nodeIdName.put((String) pair.getKey(), (String) pair.getValue());
                    }
                }

                // replace IDs in joinedList with NodeNames and write to File
                try {
                    String startNodeName = network.getRow(GlobalSettings.startNode).get(CyNetwork.NAME, String.class);
                    String endNodeName = network.getRow(GlobalSettings.endNode).get(CyNetwork.NAME, String.class);
                    File file = new File(GlobalSettings.OutputDirector + File.separator + "Path " + startNodeName.replace("/", "") + " to " + endNodeName.replace("/", "") + ".csv");
                    FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                    // create CSV Header
                    String headerLine = "";
                    for (String header : csvHeader) {
                        headerLine += "\"" + header + "\",";
                    }
                    headerLine = headerLine.substring(0, headerLine.length() - 1);
                    bufferedWriter.write(headerLine);
                    bufferedWriter.newLine();

                    for (List<String> entry : joinedList) {
                        String line = "";
                        for (String id : entry) {
                            String name = nodeIdName.get(id);
                            line += "\"" + id + "\",\"" + name + "\",\"\",";
                        }
                        // remove last ","
                        if (line.length() > 3) {
                            line = line.substring(0, line.length() - 4);
                        }
                        bufferedWriter.write(line);
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
        } else {
            JOptionPane.showMessageDialog(null, "No Path selected!");
            LOGGER.info("No Path selected");
            return "";
        }
        return "";
    }

    @SuppressWarnings("Duplicates")
    private List<Map.Entry<String, String>> compressPath(Path path) {
        List<Map.Entry<String, String>> compPath = new ArrayList<>();
        for (int i = 0; i < path.getLength(); i++) {
            CyNode node = path.get(i).getNode();
            String currentNodeName = network.getRow(node).get(CyNetwork.NAME, String.class);
            String subTop = network.getRow(node).get("subTop", String.class);
            // combine current node i with his predecessor (i-1) and his successor (i+1)
            if (subTop.contains("Function")) {
                // only if i is not the start or end of the path
                if ((i != 0) && ((i + 1) < path.getLength())) {
                    // get the Name of i+1 Node and his Predecessor Edge
                    CyNode nextNode = path.get(i + 1).getNode();
                    String nodeName = network.getRow(nextNode).get(CyNetwork.NAME, String.class);
                    CyEdge nextNodeEdge = path.get(i + 1).getEdge();
                    // get edge Name between i and i-1
                    CyEdge edge = path.get(i).getEdge();
                    String edgeName = network.getRow(edge).get(CyEdge.INTERACTION, String.class);
                    String nextNodeEdgeName = network.getRow(nextNodeEdge).get(CyEdge.INTERACTION, String.class);
                    // create new Entry, which will be the new Edge combined from i-1.edge + i + i+1.edge
                    Map.Entry e = new AbstractMap.SimpleEntry(nodeName, edgeName + currentNodeName + nextNodeEdgeName);
                    compPath.add(e);
                    // skip next Node
                    i++;
                } else {
                    CyEdge edge = path.get(i).getEdge();
                    if (edge != null) {
                        String edgeName = network.getRow(edge).get(CyEdge.INTERACTION, String.class);
                        Map.Entry e = new AbstractMap.SimpleEntry(currentNodeName, edgeName);
                        compPath.add(e);
                    } else {
                        Map.Entry e = new AbstractMap.SimpleEntry(currentNodeName, null);
                        compPath.add(e);
                    }
                }
            } else {
                CyEdge edge = path.get(i).getEdge();
                if (edge != null) {
                    String edgeName = network.getRow(edge).get(CyEdge.INTERACTION, String.class);
                    Map.Entry e = new AbstractMap.SimpleEntry(currentNodeName, edgeName);
                    compPath.add(e);
                } else {
                    Map.Entry e = new AbstractMap.SimpleEntry(currentNodeName, null);
                    compPath.add(e);
                }
            }
        }
        return compPath;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.SOUTH;
    }

    @Override
    public String getTitle() {
        return "CIONw Paths";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
