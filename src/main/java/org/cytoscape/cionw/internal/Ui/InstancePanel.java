package org.cytoscape.cionw.internal.Ui;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;

/**
 * Class for the Instance pane at the Result Panel in Cytoscape (EAST)
 * <p>
 * Created by A. Zeiser on 21.11.2016.
 */
@SuppressWarnings({"WeakerAccess", "Convert2Lambda"})
public class InstancePanel extends JPanel implements CytoPanelComponent {
    public JLabel lblSelectedNode;
    public JList<String> instanceList;

    /**
     * Initialize Panel
     */
    public InstancePanel() {
        /* ******************* GUI **************************** */
        lblSelectedNode = new JLabel("Selected Node :");

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblSelectedNode))
                                .addContainerGap(11, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblSelectedNode)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 568, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        instanceList = new JList<>();
        scrollPane.setViewportView(instanceList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setLayout(groupLayout);

        /* ******************* GUI END*********************** */

        DefaultListModel lModel = new DefaultListModel();
        //noinspection unchecked
        instanceList.setModel(lModel);
    }

    /**
     * Set a new Model for the InstanceList. This will invoke a new rendering, so the list will be updated.
     *
     * @param tModel DefaultListModel with the list entries
     */
    public void setInstanceList(DefaultListModel tModel) {
        //noinspection unchecked
        this.instanceList.setModel(tModel);
    }

    /**
     * set the Text of LblSelectedNode
     *
     * @param text Text
     */
    public void setLblSelectedNode(String text) {
        this.lblSelectedNode.setText(text);
    }

    /**
     * Get a new DefaultListModel filled with the given Strings
     *
     * @param instances List of Instances
     * @return DefaultListModel filled with Strings
     */
    public DefaultListModel getNewModel(java.util.List<String> instances) {
        DefaultListModel model = new DefaultListModel<>();
        for (String instance : instances) {
            //noinspection unchecked
            model.addElement(instance);
        }
        return model;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.EAST;
    }

    @Override
    public String getTitle() {
        return "CIONw Node Instances";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
