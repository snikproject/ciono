package org.cytoscape.cionw.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.*;
import org.cytoscape.cionw.internal.Ui.ControlPanel;
import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.util.Properties;


/**
 * Creates a new menu item under Apps menu section.
 */
@SuppressWarnings("WeakerAccess")
public class MenuAction extends AbstractCyAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuAction.class);
    public CyActivator activator;
    public CyApplicationManager applicationManager;
    public CyNetworkView networkView;
    public CyNetwork network;
    public CyServiceRegistrar serviceRegistrar;
    public CySwingApplication application;
    private CyProperty properties;

    /**
     * Create a new Menu Entry for CIONw under Apps
     *
     * @param menuTitle    Title of the MenuEntry
     * @param activator    CyActivator
     * @param cyProperties CyProperties
     */
    public MenuAction(final String menuTitle, CyActivator activator, CyProperty<Properties> cyProperties) {
        super(menuTitle, activator.getApplicationManager(), null, null);
        setPreferredMenu("Apps");
        this.applicationManager = activator.getApplicationManager();
        this.activator = activator;
        this.serviceRegistrar = activator.getServiceRegistrar();
        this.application = activator.getSwingApplication();
        this.properties = cyProperties;
    }

    public void actionPerformed(ActionEvent e) {
        this.networkView = applicationManager.getCurrentNetworkView();
        this.network = networkView.getModel();

        // read all EdgeWeights from Properties
        Properties properties = (Properties) this.properties.getProperties();
        GlobalSettings.getAllEdgeTypes(network);
        for (String edgeName : GlobalSettings.edgeWeights.keySet()) {
            if (properties.containsKey("cionw.edges." + edgeName)) {
                GlobalSettings.edgeWeights.put(edgeName, Double.valueOf(properties.getProperty("cionw.edges." + edgeName)));
            }
        }
        LOGGER.info("Reads all Properties");

        ControlPanel controlPanel = new ControlPanel(activator, network);
        serviceRegistrar.registerService(controlPanel, CytoPanelComponent.class, new Properties());
        LOGGER.info("ControlPanel registered");

        // set focus (select tab) on control panel
        CytoPanel cytoPanelWest = application.getCytoPanel(CytoPanelName.WEST);
        cytoPanelWest.setSelectedIndex(cytoPanelWest.indexOfComponent(controlPanel));
        // set focus (select tab) on instance panel
        CytoPanel cytoPanelEast = application.getCytoPanel(CytoPanelName.EAST);
        // set focus on last panel (hopefully its our panel)
        cytoPanelEast.setSelectedIndex(cytoPanelEast.getCytoPanelComponentCount());
    }
}
