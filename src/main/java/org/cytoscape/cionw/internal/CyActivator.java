package org.cytoscape.cionw.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cionw.internal.Ui.ContextMenuEndNode;
import org.cytoscape.cionw.internal.Ui.ContextMenuIntermediateNode;
import org.cytoscape.cionw.internal.Ui.ContextMenuStartNode;
import org.cytoscape.cionw.internal.Ui.SelectListenerClass;
import org.cytoscape.cionw.internal.Utils.PropertyReader;
import org.cytoscape.cionw.internal.Utils.SessionListener;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.events.SessionAboutToBeSavedListener;
import org.cytoscape.session.events.SessionLoadedListener;
import org.osgi.framework.BundleContext;

import java.util.Properties;

@SuppressWarnings("WeakerAccess")
public class CyActivator extends AbstractCyActivator {
    public CyApplicationManager applicationManager;
    public CyServiceRegistrar serviceRegistrar;
    public CySwingApplication swingApplication;

    @Override
    public void start(BundleContext context) throws Exception {
        this.applicationManager = getService(context, CyApplicationManager.class);
        this.serviceRegistrar = getService(context, CyServiceRegistrar.class);
        this.swingApplication = getService(context, CySwingApplication.class);

        // create PropertyReader
        PropertyReader pReader = new PropertyReader("cionw", "properties");
        Properties pReaderProperties = new Properties();
        pReaderProperties.setProperty("cyPropertyName", "cionw.properties");
        registerAllServices(context, pReader, pReaderProperties);

        // read properties into GlobalSettings
        //noinspection unchecked
        CyProperty<Properties> cyProperties = getService(context, CyProperty.class, "(cyPropertyName=cionw.properties)");

        // create Menu Entry
        MenuAction action = new MenuAction("CIONw", this, cyProperties);
        SelectListenerClass selectListenerClass = new SelectListenerClass(this);
        registerAllServices(context, action, new Properties());
        registerService(context, selectListenerClass, RowsSetListener.class, new Properties());

        // Create all Context Menu entries and registered them
        ContextMenuStartNode contextMenuStartNode = new ContextMenuStartNode();
        ContextMenuEndNode contextMenuEndNode = new ContextMenuEndNode();
        ContextMenuIntermediateNode contextMenuIntermediateNode = new ContextMenuIntermediateNode();
        Properties contextMenuProperties = new Properties();
        registerAllServices(context, contextMenuStartNode, contextMenuProperties);
        registerAllServices(context, contextMenuEndNode, contextMenuProperties);
        registerAllServices(context, contextMenuIntermediateNode, contextMenuProperties);

        // register SessionListener
        SessionListener listener = new SessionListener(cyProperties);
        registerService(context, listener, SessionLoadedListener.class, new Properties());
        registerService(context, listener, SessionAboutToBeSavedListener.class, new Properties());
    }

    public CyApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public CyServiceRegistrar getServiceRegistrar() {
        return serviceRegistrar;
    }

    public CySwingApplication getSwingApplication() {
        return swingApplication;
    }

}
