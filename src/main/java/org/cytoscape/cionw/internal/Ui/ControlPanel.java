package org.cytoscape.cionw.internal.Ui;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.cionw.internal.Algorithms.DataStructures.Path;
import org.cytoscape.cionw.internal.Algorithms.Dijkstra.Dijkstra;
import org.cytoscape.cionw.internal.Algorithms.Yen.Yen;
import org.cytoscape.cionw.internal.CyActivator;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by A. Zeiser on 21.11.2016.
 * <p>
 * Form for the Control Panel (WEST)
 */
@SuppressWarnings({"WeakerAccess", "Convert2Lambda"})
public class ControlPanel extends JPanel implements CytoPanelComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanel.class);
    public static JTextField textFieldStartNode;
    public static JTextField textFieldEndNode;
    public DefaultTableModel tModel = new DefaultTableModel() {
        // make sec. Column editable (Weights)
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 1) return true;
            if (column == 0) return false;
            return false;
        }
    };
    private JTextField maxPaths;
    private JTextField maxPathLength;
    private JTable table;
    private CyActivator activator;
    private CyNetwork network;
    private PathTable pathTable;
    private boolean firstCall = true; // just a boolean to do some work at initialize

    /**
     * Create a JPanel for WEST Panel with all control UI elements for CIONw
     *
     * @param activator   current CyActivator
     * @param network     current CyNetwork
     */
    public ControlPanel(CyActivator activator, CyNetwork network) {
        this.activator = activator;
        this.network = network;

        tModel.addColumn("EdgeType");
        tModel.addColumn("Weight");
        GlobalSettings.getAllEdgeTypes(network);
        for (String type : GlobalSettings.edgeWeights.keySet()) {
            tModel.addRow(new Object[]{type, GlobalSettings.edgeWeights.get(type)});
        }

        /* **************** GUI **************** */
        JLabel lblMaxim = new JLabel("Maximum Path Number (k-Paths)");

        maxPaths = new JTextField();
        maxPaths.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateValue();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateValue();
            }

            /**
             * updates the Text form maxPaths
             */
            public void updateValue() {
                if (Integer.parseInt(maxPaths.getText()) <= 0) {
                    JOptionPane.showMessageDialog(null, "Error: Please enter number bigger than 0", "Error Massage", JOptionPane.ERROR_MESSAGE);
                } else {
                    GlobalSettings.maxPaths = maxPaths.getText();
                }
            }
        });
        maxPaths.setText(GlobalSettings.maxPaths);
        maxPaths.setColumns(10);

        JLabel lblMaximumPathLength = new JLabel("Maximum Path Length");

        maxPathLength = new JTextField();
        maxPathLength.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateValue();
            }

            public void updateValue() {
                if (Integer.parseInt(maxPathLength.getText()) <= 0) {
                    JOptionPane.showMessageDialog(null, "Error: Please enter number bigger than 0", "Error Massage", JOptionPane.ERROR_MESSAGE);
                } else {
                    GlobalSettings.maxPathLength = maxPathLength.getText();
                }
            }
        });
        maxPathLength.setText(GlobalSettings.maxPathLength);
        maxPathLength.setColumns(10);

        table = new JTable();
        table.setModel(tModel);
        // Listener for changed Values in EdgeWeight Table
        tModel.addTableModelListener(new TableModelListener() {
                                         @Override
                                         public void tableChanged(TableModelEvent e) {
                                             int row = e.getFirstRow();
                                             TableModel model = (TableModel) e.getSource();
                                             String type = (String) model.getValueAt(row, 0);
                                             double weight = Double.parseDouble((table.getValueAt(row, 1)).toString());
                                             GlobalSettings.edgeWeights.put(type, weight);
                                         }
                                     }
        );

        JLabel lblFindPaths = new JLabel("Find Paths:");

        JButton btnDijkstra = new JButton("Dijkstra");
        btnDijkstra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runDijkstra();
                GlobalSettings.intermediateNode = null;
            }
        });
        btnDijkstra.setToolTipText("Finds one of the shortest path via Dijkstra Algorithm");

        JButton btnYen = new JButton("Yen");
        btnYen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runYen();
            }
        });
        btnYen.setToolTipText("Finds k-shortest paths via Yen Algorithm. k can be set in the box \"Maximum Path Number\"");

        JLabel lblSelectedStartNode = new JLabel("Selected start node:");

        textFieldStartNode = new JTextField();
        textFieldStartNode.setColumns(10);

        JLabel lblSelectedEndNode = new JLabel("Selected end node");

        textFieldEndNode = new JTextField();
        textFieldEndNode.setColumns(10);

        JButton btnInstanceDir = new JButton("Change instance location");
        btnInstanceDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectInstanceTablePaths();
            }
        });

        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JButton btnOutputDir = new JButton("Change output location");
        btnOutputDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPathOutputDir();
            }
        });
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(lblMaxim)
                                                        .addComponent(lblMaximumPathLength))
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                                        .addComponent(maxPathLength, 0, 0, Short.MAX_VALUE)
                                                        .addComponent(maxPaths, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                                                .addGap(4))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(lblFindPaths)
                                                .addContainerGap(153, Short.MAX_VALUE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(btnDijkstra)
                                                .addPreferredGap(ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                                                .addComponent(btnYen, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(lblSelectedStartNode)
                                                .addContainerGap(161, Short.MAX_VALUE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addComponent(lblSelectedEndNode)
                                                .addContainerGap(161, Short.MAX_VALUE))
                                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(textFieldEndNode, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                                                        .addComponent(textFieldStartNode, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                                                .addContainerGap())
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                                        .addComponent(btnOutputDir, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnInstanceDir, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addContainerGap(118, Short.MAX_VALUE))))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblMaxim)
                                        .addComponent(maxPaths, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblMaximumPathLength)
                                        .addComponent(maxPathLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
                                .addGap(18)
                                .addComponent(lblFindPaths)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(btnDijkstra)
                                        .addComponent(btnYen))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(lblSelectedStartNode)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(textFieldStartNode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(lblSelectedEndNode)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(textFieldEndNode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(btnInstanceDir)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnOutputDir)
                                .addContainerGap(58, Short.MAX_VALUE))
        );
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane.setViewportView(table);
        setLayout(groupLayout);
        /* ************** GUI END ************* */
    }

    /**
     * perform Yen algorithm
     */
    private void runYen() {
        if (GlobalSettings.startNode != null && GlobalSettings.endNode != null) {
            LOGGER.info("Start Yen Algorithm");
            Yen yen = new Yen(network, GlobalSettings.startNode, GlobalSettings.endNode, Integer.parseInt(maxPaths.getText()));
            java.util.List<Path> paths = yen.findKshortestPaths(Integer.parseInt(maxPathLength.getText()));
            paths.removeIf(p -> p.getLength() > Integer.parseInt(maxPathLength.getText()));
            LOGGER.info("Found " + paths.size() + " Paths");
            showPaths(paths);
            LOGGER.info("End Yen");
        } else {
            JOptionPane.showMessageDialog(null, "No start or end node selected!");
            LOGGER.info("No start or end Node for Yen selected");
        }
    }

    /**
     * perform Dijkstra algorithm
     */
    private void runDijkstra() {
        if (GlobalSettings.startNode != null && GlobalSettings.endNode != null) {
            LOGGER.info("Start Dijkstra");
            Dijkstra dj;
            if (GlobalSettings.intermediateNode != null) {
                dj = new Dijkstra(network, GlobalSettings.startNode, GlobalSettings.endNode, GlobalSettings.intermediateNode);
            } else {
                dj = new Dijkstra(network, GlobalSettings.startNode, GlobalSettings.endNode);
            }

            Path path = dj.findShortestPath(null, null);
            java.util.List<Path> paths = new ArrayList<>();
            paths.add(path);

            int value = Integer.parseInt(maxPathLength.getText());
            if (path.size() <= value) {
                // create new tablePanel (SOUTH)
                showPaths(paths);
            } else {
                // Massage box, founded Path is to long
                JOptionPane.showMessageDialog(null, "Found Path is to long. Set a higher path length! (at least " + (path.size() + 1) + " !");
                LOGGER.info("Founded Path is to long");
            }
            LOGGER.info("End Dijkstra");
        } else {
            JOptionPane.showMessageDialog(null, "No start or end node selected!");
            LOGGER.info("No start or end Node for Yen selected");
        }
    }

    /**
     * Create a new PathTable Panel for the SOUTH Component with all founded paths in a table
     *
     * @param paths Paths which will be listed in Table
     */
    private void showPaths(java.util.List<Path> paths) {
        CyServiceRegistrar serviceRegistrar = activator.getServiceRegistrar();
        CySwingApplication application = activator.getSwingApplication();
        if (firstCall) {
            pathTable = new PathTable(paths, network);
            setInfoLabel(pathTable, paths);
            serviceRegistrar.registerService(pathTable, CytoPanelComponent.class, new Properties());
            CytoPanel cytoPanelSouth = application.getCytoPanel(CytoPanelName.SOUTH);
            cytoPanelSouth.setSelectedIndex(cytoPanelSouth.indexOfComponent(pathTable));
            firstCall = false;
        } else {
            serviceRegistrar.unregisterService(pathTable, CytoPanelComponent.class);
            pathTable = new PathTable(paths, network);
            setInfoLabel(pathTable, paths);
            serviceRegistrar.registerService(pathTable, CytoPanelComponent.class, new Properties());
            CytoPanel cytoPanelSouth = application.getCytoPanel(CytoPanelName.SOUTH);
            cytoPanelSouth.setSelectedIndex(cytoPanelSouth.indexOfComponent(pathTable));
        }
    }

    /**
     * Set the Label of PathTable panel with the Information: path counter, Start Node, End Node
     *
     * @param pathTable pathTable where the Label is.
     */
    private void setInfoLabel(PathTable pathTable, java.util.List<Path> paths) {
        String startNodeName = network.getRow(GlobalSettings.startNode).get(CyNetwork.NAME, String.class);
        String endNodeName = network.getRow(GlobalSettings.endNode).get(CyNetwork.NAME, String.class);
        pathTable.lblFoundedPaths.setText("Found " + paths.size() + " Paths from " + startNodeName + " to " + endNodeName);
    }

    /**
     * Open new File chooser Dialog and set the selected directory to the current directory for the Instance tables
     */
    private void selectInstanceTablePaths() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            GlobalSettings.InstanceDirectory = fileChooser.getSelectedFile().toString();
        }
    }

    /**
     * Open new File chooser Dialog and set the selected directory to the output directory of path exports
     */
    private void selectPathOutputDir() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            GlobalSettings.OutputDirector = fileChooser.getSelectedFile().toString();
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

    @Override
    public String getTitle() {
        return "CIONw Control Panel";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
