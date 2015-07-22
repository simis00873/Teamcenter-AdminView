/*
 * ProcedureComponent.java
 *
 * Created on 20 July 2007, 11:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tcadminview.gui.procedure;

import tcadminview.gui.*;
import tcadminview.xml.DOMUtil;
import tcadminview.procedure.*;
import tcadminview.plmxmlpdm.type.*;
import tcadminview.plmxmlpdm.type.element.*;
import tcadminview.plmxmlpdm.classtype.*;
import tcadminview.plmxmlpdm.base.AttribOwnerBase;
import tcadminview.plmxmlpdm.TagTypeEnum;
import org.xml.sax.InputSource;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.*;
import java.io.*;
/**
 *
 * @author nzr4dl
 */
public class ProcedureComponent extends JPanel implements TabbedPanel {
    
    protected JTable tableDepTaskTemplateRef;
    protected JTable tableWorkflowTemplatesProcess;
    protected JTable tableWorkflowTemplatesSub;
    protected JTable tableActionsRef;
    protected JTable tableSubTemplateRef;
    protected JTree treeWorkflowProcess;
    
    protected ProcedureManager pm;
    
    
    
    /**
     * Creates a new instance of ProcedureComponent
     */
    public ProcedureComponent(JFrame frame, ProcedureManager pm){
        this.pm = pm;
        
        // Workflow Process Tree
        treeWorkflowProcess = new JTree(new ProcedureTreeModel(pm));
        treeWorkflowProcess.setCellRenderer(new ProcedureTreeCellRenderer());
        treeWorkflowProcess.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                NodeReference nr = (NodeReference)path.getLastPathComponent();
                updateNodeDetails(nr);
            }
        });
        JScrollPane scrollTreeWorkflowProcess = new JScrollPane();
        scrollTreeWorkflowProcess.setBorder(new BevelBorder(BevelBorder.LOWERED));
        scrollTreeWorkflowProcess.getViewport().add(treeWorkflowProcess);
        JPanel panelTreeWorkflowProcess = new JPanel();
        panelTreeWorkflowProcess.setLayout(new BorderLayout(Utilities.GAP_COMPONENT,Utilities.GAP_COMPONENT));
        panelTreeWorkflowProcess.setBorder(new TitledBorder(new EtchedBorder(),"Workflow Templates: PROCESS"));
        panelTreeWorkflowProcess.add(Utilities.createPanelMargined(scrollTreeWorkflowProcess));
        
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,2,Utilities.GAP_COMPONENT,Utilities.GAP_COMPONENT));
        panel.add(panelTreeWorkflowProcess);
        panel.add(createNodeDetailsPanel());
        
        
        this.setLayout(new BorderLayout(Utilities.GAP_COMPONENT,Utilities.GAP_COMPONENT));
        this.add("Center",Utilities.createPanelMargined(panel));
        
    }
    
    public boolean isEmptyPanel(){
        return (pm.getWorkflowTemplates().size() == 0);
    }
    
    protected JTree treeAttributes;
    protected JTableAdvanced tableProcedure;
    protected JTableAdvanced tableAttribute;
    
    private JPanel createNodeDetailsPanel() {
        treeAttributes = new JTree(new AttributeTreeModel());
        treeAttributes.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                NodeReference nr = (NodeReference)path.getLastPathComponent();
                updateAttribDetails(nr);
            }
        });
        treeAttributes.setCellRenderer(new ProcedureTreeCellRenderer());
        JScrollPane scrollTreeAttributes = new JScrollPane();
        scrollTreeAttributes.setPreferredSize(new Dimension(200,220));
        scrollTreeAttributes.setBorder(new BevelBorder(BevelBorder.LOWERED));
        scrollTreeAttributes.getViewport().add(treeAttributes);
        
        JPanel panelAttributes = new JPanel();
        panelAttributes.setLayout(new BorderLayout(Utilities.GAP_COMPONENT,Utilities.GAP_COMPONENT));
        panelAttributes.setBorder(new TitledBorder(new EtchedBorder(),"Attributes"));
        panelAttributes.add("Center",Utilities.createPanelMargined(scrollTreeAttributes));
        
        tableAttribute = new JTableAdvanced(new NodeTableModel());
        JScrollPane scrollTableAttribute = new JScrollPane();
        scrollTableAttribute.setPreferredSize(new Dimension(200,220));
        scrollTableAttribute.setBorder(new BevelBorder(BevelBorder.LOWERED));
        scrollTableAttribute.getViewport().add(tableAttribute);
        
        JPanel panelAttributeDetails = new JPanel();
        panelAttributeDetails.setLayout(new BorderLayout(Utilities.GAP_COMPONENT,Utilities.GAP_COMPONENT));
        panelAttributeDetails.setBorder(new TitledBorder(new EtchedBorder(),"Attribute Details"));
        panelAttributeDetails.add("Center",Utilities.createPanelMargined(scrollTableAttribute));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(Utilities.GAP_COMPONENT,Utilities.GAP_COMPONENT));
        panel.add("Center",panelAttributes);
        panel.add("South",panelAttributeDetails);
        
        return panel;
    }
    
    private void updateNodeDetails(NodeReference nr) {
        tableAttribute.setModel(new NodeTableModel(nr, pm));
        Utilities.packColumns(tableAttribute, 2);
        treeAttributes.setModel(new AttributeTreeModel(nr,pm));
        Utilities.setCascadeTreeExpansion(treeAttributes,treeAttributes.getPathForRow(0),true);
    }
    
    private void updateAttribDetails(NodeReference nr){
        tableAttribute.setModel(new NodeTableModel(nr, pm));
        Utilities.packColumns(tableAttribute, 2);
    }
    
}