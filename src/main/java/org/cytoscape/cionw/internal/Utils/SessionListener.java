package org.cytoscape.cionw.internal.Utils;

import org.cytoscape.property.CyProperty;
import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;

import java.util.Properties;

/**
 * Listener class which handel's the saving and loading of properties for cionw (e.g. max path length...)
 * <p>
 * Created by A. Zeiser on 03.12.2016.
 */
public class SessionListener implements SessionAboutToBeSavedListener, SessionLoadedListener {
    private CyProperty<Properties> cyProperty;

    /**
     * Create a new Instance of the Listeners
     *
     * @param cyProperty Current CyProperties
     */
    public SessionListener(CyProperty<Properties> cyProperty) {
        this.cyProperty = cyProperty;
    }

    @Override
    public void handleEvent(SessionAboutToBeSavedEvent sessionAboutToBeSavedEvent) {
        // save all GlobalSettings into properties
        Properties properties = this.cyProperty.getProperties();
        properties.setProperty("cionw.maxPathLength", GlobalSettings.maxPathLength);
        properties.setProperty("cionw.maxPaths", GlobalSettings.maxPaths);
        properties.setProperty("cionw.instanceDir", GlobalSettings.InstanceDirectory);
        properties.setProperty("cionw.outputDir", GlobalSettings.OutputDirector);
        // save EdgeWeights
        for (String edgeName : GlobalSettings.edgeWeights.keySet()) {
            properties.setProperty("cionw.edges." + edgeName, GlobalSettings.edgeWeights.get(edgeName).toString());
        }
    }

    @Override
    public void handleEvent(SessionLoadedEvent sessionLoadedEvent) {
        // load all GlobalSettings from properties
        Properties properties = this.cyProperty.getProperties();
        GlobalSettings.maxPathLength = properties.getProperty("cionw.maxPathLength");
        GlobalSettings.maxPaths = properties.getProperty("cionw.maxPaths");
        GlobalSettings.InstanceDirectory = properties.getProperty("cionw.instanceDir");
        GlobalSettings.OutputDirector = properties.getProperty("cionw.outputDir");
    }
}
