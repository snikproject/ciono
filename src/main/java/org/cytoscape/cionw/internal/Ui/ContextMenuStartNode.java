package org.cytoscape.cionw.internal.Ui;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Context Menu Class for selecting the start node
 * <p>
 * Created by A. Zeiser on 22.11.2016.
 */
public class ContextMenuStartNode extends ContextMenu implements CyNodeViewContextMenuFactory, ActionListener {
    private CyNetworkView networkView;
    private View<CyNode> view;

    @Override
    public CyMenuItem createMenuItem(CyNetworkView cyNetworkView, View<CyNode> view) {
        this.networkView = cyNetworkView;
        this.view = view;
        JMenuItem jMenu = new JMenuItem("Select as start Node");
        jMenu.addActionListener(this);
        return new CyMenuItem(jMenu, 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GlobalSettings.startNode = view.getModel();
        // highlight selected Node in current View
        CyNetwork network = networkView.getModel();
        CyTable nodeTable = network.getDefaultNodeTable();
        Long suid = GlobalSettings.startNode.getSUID();
        if (suid != null) {
            nodeTable.getRow(suid).set("selected", true);
            String nodeName = nodeTable.getRow(suid).get("name", String.class);
            if (ControlPanel.textFieldStartNode != null) {
                ControlPanel.textFieldStartNode.setText(nodeName);
            }
        }
        correctNodeHighlighting(networkView);
    }
}
