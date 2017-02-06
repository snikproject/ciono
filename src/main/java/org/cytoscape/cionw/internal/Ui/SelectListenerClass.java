package org.cytoscape.cionw.internal.Ui;

import javafx.util.Pair;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.cionw.internal.Algorithms.Instances.InstanceReader;
import org.cytoscape.cionw.internal.CyActivator;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.CyServiceRegistrar;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Listener for Nodes/Edge selection
 * <p>
 * Created by A. Zeiser on 21.11.2016.
 */
public class SelectListenerClass implements RowsSetListener {
    private CyApplicationManager applicationManager;
    private InstancePanel instancePanel;

    /**
     * Constructor, create InstancePanel (EAST)
     *
     * @param activator CyActivator
     */
    public SelectListenerClass(CyActivator activator) {
        this.applicationManager = activator.getApplicationManager();
        CyServiceRegistrar serviceRegistrar = activator.getServiceRegistrar();
        CySwingApplication application = activator.getSwingApplication();

        instancePanel = new InstancePanel();
        serviceRegistrar.registerService(instancePanel, CytoPanelComponent.class, new Properties());

        CytoPanel cytoPanelEast = application.getCytoPanel(CytoPanelName.EAST);
        // set focus on new panel
        cytoPanelEast.setSelectedIndex(cytoPanelEast.indexOfComponent(instancePanel));
    }

    @Override
    public void handleEvent(RowsSetEvent rowsSetEvent) {
        CyNetwork network = applicationManager.getCurrentNetwork();

        // get the current selected Node (via SUID)
        Collection<RowSetRecord> selectedRows = rowsSetEvent.getColumnRecords("selected");
        Long suid = null;
        for (RowSetRecord rowRecord : selectedRows) {
            CyRow row = rowRecord.getRow();
            suid = row.get("SUID", Long.class);
        }
        // Get the Name for the SUID and add it to the Instance Panel
        if (suid != null) {
            CyNode node = network.getNode(suid);
            String nodeName = network.getRow(node).get(CyNetwork.NAME, String.class);
            instancePanel.setLblSelectedNode("Selected Node : " + nodeName);

            // read all instance Pairs from the Path (from JFileChooser)
            List<Pair> instancesPair = InstanceReader.getInstancesPair(GlobalSettings.InstanceDirectory + File.separator + nodeName + ".txt");
            if (!instancesPair.isEmpty()) {
                List<String> instances = new ArrayList<>();
                for (Pair pair : instancesPair) {
                    instances.add((String) pair.getValue());
                }
                DefaultListModel newModel = instancePanel.getNewModel(instances);
                instancePanel.setInstanceList(newModel);
            } else {
                List<String> notFound = new ArrayList<>();
                notFound.add("No Instances available!");
                DefaultListModel newModel = instancePanel.getNewModel(notFound);
                instancePanel.setInstanceList(newModel);
            }
        }
    }
}
