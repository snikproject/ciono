package org.cytoscape.cionw.internal.Ui;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Context Menu Class for selecting the intermediate node
 * <p>
 * Created by A. Zeiser on 22.11.2016.
 */
public class ContextMenuIntermediateNode extends ContextMenu implements CyNodeViewContextMenuFactory, ActionListener {
    private CyNetworkView networkView;
    private View<CyNode> view;

    @Override
    public void actionPerformed(ActionEvent e) {
        GlobalSettings.intermediateNode = view.getModel();
        correctNodeHighlighting(networkView);
    }

    @Override
    public CyMenuItem createMenuItem(CyNetworkView cyNetworkView, View<CyNode> view) {
        this.networkView = cyNetworkView;
        this.view = view;
        JMenuItem jMenu = new JMenuItem("Select as intermediate node");
        jMenu.addActionListener(this);
        return new CyMenuItem(jMenu, 1);
    }
}
