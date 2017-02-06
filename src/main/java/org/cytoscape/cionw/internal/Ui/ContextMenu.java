package org.cytoscape.cionw.internal.Ui;

import org.cytoscape.cionw.internal.Utils.GlobalSettings;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;

import java.util.Collection;
import java.util.Objects;

/**
 * Abstract Class, which provides a method for all select Node Actions.
 * <p>
 * Created by A. Zeiser on 22.12.2016.
 */
abstract class ContextMenu {
    /**
     * Unhighlight all Nodes which are not the Start or End Nodes
     *
     * @param networkView current CyNetworkView
     */
    void correctNodeHighlighting(CyNetworkView networkView) {
        CyNetwork model = networkView.getModel();
        CyTable nodeTable = model.getDefaultNodeTable();
        Collection<CyRow> markedRows = nodeTable.getMatchingRows("selected", true);
        for (CyRow row : markedRows) {
            Long rowSuid = row.get("SUID", Long.class);
            if (GlobalSettings.startNode != null) {
                Long startNodeSuid = GlobalSettings.startNode.getSUID();
                if (Objects.equals(rowSuid, startNodeSuid)) {
                    continue;
                }
            }
            if (GlobalSettings.endNode != null) {
                Long endNodeSuid = GlobalSettings.endNode.getSUID();
                if (Objects.equals(rowSuid, endNodeSuid)) {
                    continue;
                }
            }
            // selected Row is not endNode or startNode. Deselect the Row
            row.set("selected", false);
        }
    }
}
