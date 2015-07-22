/*
 * AdminViewFrame.java
 *
 * Created on 20 June 2007, 15:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tceav.gui;

import tceav.gui.access.AccessManagerComponent;
import tceav.gui.procedure.ProcedureManagerComponent;
import tceav.manager.ManagerAdapter;
import tceav.manager.access.AccessManager;
import tceav.manager.procedure.ProcedureManager;
import tceav.manager.compare.CompareAccessManager;
import tceav.utils.CustomFileFilter;
import tceav.Settings;
import tceav.gui.compare.*;
import tceav.gui.*;
import tceav.resources.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import org.xml.sax.InputSource;

/**
 *
 * @author NZR4DL
 */
public class AdminViewFrame extends JFrame{
    
    /** Window for showing Tree. */
    private AdminViewFrame parentFrame;
    private JTabbedPane tabbedpane;
    private EmptyComponent emptyPane;
    private ImageIcon iconProcedure;
    private ImageIcon iconClose;
    private ImageIcon iconExit;
    private ImageIcon iconApp;
    private ImageIcon iconRuleTree;
    private ImageIcon iconCompare;
    private JPanel mainPanel;
    
    private final String TABPANE = "TABPANE";
    private final String EMPTYPANE = "EMPTYPANE";
    
    /**
     * Creates a new instance of AdminViewFrame
     */
    public AdminViewFrame() {
        super("TcAV");
        parentFrame = this;
        
        try {
            Settings.load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Load Settings Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            if (Settings.getUserInterface().equals("")) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                Settings.setUserInterface(UIManager.getCrossPlatformLookAndFeelClassName());
            } else
                UIManager.setLookAndFeel(Settings.getUserInterface());
            // If you want the System L&F instead, comment out the above line and
            // uncomment the following:
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "GUI Error", JOptionPane.ERROR_MESSAGE);
        }
        
        try {
            iconRuleTree = ResourceLoader.getImage(ImageEnum.amRuletree);
            iconProcedure = ResourceLoader.getImage(ImageEnum.pmWorkflow);
            iconClose = ResourceLoader.getImage(ImageEnum.utilClose);
            iconExit  = ResourceLoader.getImage(ImageEnum.utilExit);
            iconApp = ResourceLoader.getImage(ImageEnum.appLogo);
            iconCompare = ResourceLoader.getImage(ImageEnum.utilCompare);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error Load Images", JOptionPane.ERROR_MESSAGE);
        }
        
        JMenuBar menuBar = constructMenuBar();
        JPanel toolbar = constructToolBar();
        JPanel statusBar = constructStatusBar();
        
        tabbedpane = new JTabbedPane();
        tabbedpane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedpane.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if(tabbedpane.getTabCount() > 1)
                    showBarComponent((TabbedPanel)tabbedpane.getSelectedComponent());
            }
        });
        emptyPane = new EmptyComponent();
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());
        mainPanel.add(tabbedpane, TABPANE);
        mainPanel.add(emptyPane, EMPTYPANE);
        ((CardLayout)mainPanel.getLayout()).show(mainPanel,EMPTYPANE);
        addBarComponent(emptyPane);
        showBarComponent(emptyPane);
        
        
        //this.getContentPane().setLayout(new BorderLayout(1,1));
        this.getContentPane().add("Center", mainPanel);
        this.getContentPane().add("North", toolbar);
        this.getContentPane().add("South", statusBar);
        this.setJMenuBar(menuBar);
        this.setIconImage(iconApp.getImage());
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        
        this.pack();
        this.setSize(new Dimension(Settings.getFrameSizeX(),Settings.getFrameSizeY()));
        this.setLocation(
                Settings.getFrameLocationX(),
                Settings.getFrameLocationY());
        this.setVisible(true);
        this.setTitle(ResourceStrings.getApplicationNameShort());
    }
    
    public JFileChooser createFileChooser(String type) {
        String path = "";
        JFileChooser fc = new JFileChooser();
        
        if(type.equals(ManagerAdapter.ACCESS_MANAGER_TYPE)) {
            path = Settings.getAmLoadPath();
            fc.setCurrentDirectory(new File(path));
            fc.addChoosableFileFilter(new CustomFileFilter(
                    new String[]{"txt",""},"Text File (*.txt; *.)"));
        } else if(type.equals(ManagerAdapter.PROCEDURE_MANAGER_TYPE)) {
            path = Settings.getPmLoadPath();
            fc.setCurrentDirectory(new File(path));
            fc.addChoosableFileFilter(new CustomFileFilter(
                    new String[]{"xml","plmxml"},"XML File (*.xml; *.plmxml)"));
        }
        
        return fc;
    }
    
    public JFrame getFrame() {
        return this;
    }
    
    private JPanel statusBarPanel;
    
    private JPanel constructStatusBar() {
        statusBarPanel = new JPanel();
        statusBarPanel.setBorder(new EmptyBorder(1,1,1,1));
        statusBarPanel.setLayout(new CardLayout());
        return statusBarPanel;
    }
    
    private void addBarComponent(TabbedPanel tab) {
        statusBarPanel.add(tab.getStatusBar(), Integer.toString(tab.hashCode()));
        toolBarPanel.add(tab.getToolBar(), Integer.toString(tab.hashCode()));
    }
    
    private void removeBarComponent(TabbedPanel tab) {
        ((CardLayout)statusBarPanel.getLayout()).removeLayoutComponent(tab.getStatusBar());
        ((CardLayout)toolBarPanel.getLayout()).removeLayoutComponent(tab.getToolBar());
    }
    
    private void showBarComponent(TabbedPanel tab) {
        ((CardLayout)statusBarPanel.getLayout()).show(statusBarPanel, Integer.toString(tab.hashCode()));
        ((CardLayout)toolBarPanel.getLayout()).show(toolBarPanel, Integer.toString(tab.hashCode()));
    }
    
    public void addTabbedPane(TabbedPanel tab) {
        ((CardLayout)mainPanel.getLayout()).show(mainPanel, TABPANE);
        tabbedpane.addTab(tab.getTitle(), tab.getIcon(), tab);
        tabbedpane.setSelectedComponent(tab);
        addBarComponent(tab);
        showBarComponent(tab);
        
        buttonClose.setEnabled(true);
        menuClose.setEnabled(true);
        
    }
    
    private void removeTabbedPane(TabbedPanel tab) {
        if(tabbedpane.getTabCount() <= 0)
            return;
        
        tabbedpane.remove(tabbedpane.getSelectedIndex());
        removeBarComponent(tab);
        if(tabbedpane.getTabCount() == 0) {
            ((CardLayout)mainPanel.getLayout()).show(mainPanel, EMPTYPANE);
            showBarComponent(emptyPane);
            buttonClose.setEnabled(false);
            menuClose.setEnabled(false);
        }
    }
    
    private JButton buttonClose;
    private JButton buttonCompare;
    private JPanel toolBarPanel;
    
    private JPanel constructToolBar() {
        JToolBar toolbar = new JToolBar("Main ToolBar");
        
        JButton buttonOpenRuleTree = new JButton("Load Tree");
        buttonOpenRuleTree.setOpaque(false);
        buttonOpenRuleTree.setIcon(iconRuleTree);
        buttonOpenRuleTree.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonOpenRuleTree.setToolTipText("Import TcAE Ruletree File");
        buttonOpenRuleTree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionLoadRuleTree();
            }
        });
        
        JButton buttonOpenProcedure = new JButton("Load Procedure");
        buttonOpenProcedure.setOpaque(false);
        buttonOpenProcedure.setIcon(iconProcedure);
        buttonOpenProcedure.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonOpenProcedure.setToolTipText("Import TcAE Procedure File");
        buttonOpenProcedure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionLoadProcedure();
            }
        });
        
        buttonCompare = new JButton("Compare");
        buttonCompare.setOpaque(false);
        buttonCompare.setIcon(iconCompare );
        buttonCompare.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonCompare.setToolTipText("Compare tabbs");
        buttonCompare.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionCompare();
            }
        });
        
        buttonClose = new JButton("Close Tab");
        buttonClose.setOpaque(false);
        buttonClose.setIcon(iconClose);
        buttonClose.setEnabled(false);
        buttonClose.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonClose.setToolTipText("Close the current tabb");
        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionCloseTab();
            }
        });
        
        JButton buttonExit = new JButton("Exit");
        buttonExit.setOpaque(false);
        buttonExit.setIcon(iconExit);
        buttonExit.setHorizontalTextPosition(SwingConstants.RIGHT);
        buttonExit.setToolTipText("End this application");
        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        
        toolbar.setMargin(GUIutilities.GAP_INSETS);
        toolbar.add(buttonOpenRuleTree);
        toolbar.add(buttonOpenProcedure);
        toolbar.addSeparator();
        toolbar.add(buttonCompare);
        toolbar.addSeparator();
        toolbar.add(buttonClose);
        toolbar.add(buttonExit);
        
        toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new CardLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0,0));//FlowLayout(FlowLayout.LEFT,0,0));
        panel.add(toolbar, BorderLayout.WEST);//, BorderLayout.WEST);
        panel.add(toolBarPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JCheckBoxMenuItem menuItemSaveSettingsOnExit;
    private JMenuItem menuClose;
    private JMenuItem menuCompare;
    
    /** Construct a menu. */
    private JMenuBar constructMenuBar() {
        JMenu menu;
        JMenuBar menuBar = new JMenuBar();
        JMenuItem menuItem;
        
        
        
        /* Good ol exit. */
        menu = new JMenu("File");
        menu.setMnemonic('F');
        menuBar.add(menu);
        
        menuItem = menu.add(new JMenuItem("Load RuleTree", 'R'));
        menuItem.setAccelerator(KeyStroke.getKeyStroke('R', Event.CTRL_MASK));
        menuItem.setIcon(iconRuleTree);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionLoadRuleTree();
            }
        });
        
        menuItem = menu.add(new JMenuItem("Load Procedure", 'P'));
        menuItem.setAccelerator(KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
        menuItem.setIcon(iconProcedure);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionLoadProcedure();
            }
        });
        
        menu.addSeparator();
        
        menuClose = menu.add(new JMenuItem("Close Tab", 'C'));
        menuClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
        menuClose.setIcon(iconClose);
        menuClose.setEnabled(false);
        menuClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionCloseTab();
            }
        });
        
        menuItem = menu.add(new JMenuItem("Exit", 'E'));
        menuItem.setIcon(iconExit);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        
        /* Tools */
        menu = new JMenu("Tools");
        menu.setMnemonic('T');
        menuBar.add(menu);
        
        menuCompare = menu.add(new JMenuItem("Compare", 'o'));
        menuCompare.setIcon(iconCompare );
        menuCompare.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
        menuCompare.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionCompare();
            }
        });
        
        
        /* Edit. */
        menu = new JMenu("Edit");
        menu.setMnemonic('E');
        menuBar.add(menu);
        
        menuItem = menu.add(new JMenuItem("Save Settings", 'S'));
        menuItem.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSaveSettings();
            }
        });
        menu.addSeparator();
        menuItemSaveSettingsOnExit = new JCheckBoxMenuItem("Save Settings on Exit");
        menuItemSaveSettingsOnExit.setSelected(Settings.isSaveSettingsOnExit());
        menuItem = menu.add(menuItemSaveSettingsOnExit);
        menuItem.setMnemonic('X');
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Settings.setSaveSettingsOnExit(menuItemSaveSettingsOnExit.isSelected());
                actionSaveSettings();
            }
        });
        
        /* Look and Feel */
        menu = new JMenu("Interface");
        menuBar.add(menu);
        menu.setMnemonic('I');
        
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        ButtonGroup lafMenuGroup = new ButtonGroup();
        
        for (int counter = 0; counter < lafInfo.length; counter++) {
            menuItem = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(lafInfo[counter].getName()));
            lafMenuGroup.add(menuItem);
            if(lafInfo[counter].getClassName().equals(UIManager.getLookAndFeel().getClass().getName()))
                lafMenuGroup.setSelected(menuItem.getModel(),true);
            menuItem.addActionListener(new ChangeLookAndFeelAction(lafInfo[counter].getClassName()));
            menuItem.setEnabled(isAvailableLookAndFeel(lafInfo[counter].getClassName()));
        }
        
        /* Misc. */
        menu = new JMenu("Help");
        menu.setMnemonic('H');
        menuBar.add(menu);
        
        menuItem = menu.add(new JMenuItem("Change Log", 'C'));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                URL url = null;
                JEditorPane html = new JEditorPane();
                try {
                    url = tceav.resources.ResourceStrings.getChangeLog();
                    html = new JEditorPane(url);
                } catch (Exception ex) {
                    System.err.println("Failed to open file");
                    url = null;
                }
                if(html != null) {
                    JScrollPane scroll = new JScrollPane();
                    scroll.setPreferredSize(new Dimension(500,500));
                    scroll.getViewport().add(html);
                    scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));
                    JPanel panel = new JPanel(true);
                    panel.setLayout(new GridLayout(1,1));
                    panel.setBorder(new TitledBorder("Change Log"));
                    JOptionPane.showMessageDialog(getFrame(),scroll,"Change Log",JOptionPane.PLAIN_MESSAGE,null);
                }
            }
        });
        
        menuItem = menu.add(new JMenuItem("About", 'A'));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        getFrame(),
                        ResourceStrings.getAboutInfo());
            }
        });
        
        return menuBar;
    }
    
    protected void actionUserInterface(String laf) {
        try {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(parentFrame);
            Settings.setUserInterface(laf);
            // If you want the System L&F instead, comment out the above line and
            // uncomment the following:
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "GUI Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    class ChangeLookAndFeelAction extends AbstractAction {
        String laf;
        protected ChangeLookAndFeelAction(String laf) {
            super("ChangeTheme");
            this.laf = laf;
        }
        
        public void actionPerformed(ActionEvent e) {
            actionUserInterface(laf);
        }
    }
    /**
     * A utility function that layers on top of the LookAndFeel's
     * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
     * is supported. Returns false if the LookAndFeel is not supported
     * and/or if there is any kind of error checking if the LookAndFeel
     * is supported.
     *
     * The L&F menu will use this method to detemine whether the various
     * L&F options should be active or inactive.
     *
     */
    protected boolean isAvailableLookAndFeel(String laf) {
        try {
            Class lnfClass = Class.forName(laf);
            LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
            return newLAF.isSupportedLookAndFeel();
        } catch(Exception e) { // If ANYTHING weird happens, return false
            return false;
        }
    }
    
    private void actionSaveSettings() {
        try{
            Settings.setFrameSizeX(parentFrame.getSize().width);
            Settings.setFrameSizeY(parentFrame.getSize().height);
            Settings.setFrameLocationX(parentFrame.getLocation().x);
            Settings.setFrameLocationY(parentFrame.getLocation().y);
            Settings.store();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Save Settings Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actionCloseTab() {
        removeTabbedPane((TabbedPanel)tabbedpane.getSelectedComponent());
        System.gc();
    }
    
    private void actionExit() {
        Settings.setFrameSizeX(this.getSize().width);
        Settings.setFrameSizeY(this.getSize().height);
        Settings.setFrameLocationX(this.getLocation().x);
        Settings.setFrameLocationY(this.getLocation().y);
        actionSaveSettings();
        System.exit(0);
    }
    
    private void actionLoadRuleTree() {
        new Thread() {
            public void run() {
                JFileChooser fc = createFileChooser(ManagerAdapter.ACCESS_MANAGER_TYPE);
                int result = fc.showOpenDialog(getFrame());
                if(result == JFileChooser.APPROVE_OPTION) {
                    try {
                        Settings.setAmLoadPath(fc.getCurrentDirectory().getPath());
                        AccessManager am = new AccessManager();
                        
                        try {
                            am.readFile(fc.getSelectedFile());
                        } catch (Exception ex) {
                            throw new Exception("Corrupted File: "+fc.getSelectedFile().getName());
                        }
                        
                        if(!am.isValid()) {
                            throw new Exception("No rule tree found in file "+fc.getSelectedFile().getName());
                        }
                        
                        AccessManagerComponent amComponent = new AccessManagerComponent(parentFrame, am);
                        addTabbedPane(amComponent);
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Ruletree File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                System.gc();
            }
        }.start();
    }
    
    private void actionLoadProcedure() {
        new Thread() {
            public void run() {
                JFileChooser fc = createFileChooser(ManagerAdapter.PROCEDURE_MANAGER_TYPE);
                int result = fc.showOpenDialog(getFrame());
                if(result == JFileChooser.APPROVE_OPTION) {
                    try {
                        
                        Settings.setPmLoadPath(fc.getCurrentDirectory().getPath());
                        ProcedureManager pm = new ProcedureManager(parentFrame);
                        try {
                            pm.readFile(fc.getSelectedFile());
                        } catch (Exception ex) {
                            throw new Exception("Corrupted File: "+fc.getSelectedFile().getName());
                        }
                        
                        if(!pm.isValid()) {
                            throw new Exception("No workflow processes found in file"+fc.getSelectedFile().getName());
                        }
                        
                        ProcedureManagerComponent proComponent = new ProcedureManagerComponent(parentFrame, pm);
                        addTabbedPane(proComponent);
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Procedure File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                System.gc();
            }
        }.start();
    }
    
    private void actionCompare(){
        new Thread() {
            public void run() {
                CompareTabChooser chooser = new CompareTabChooser(parentFrame);
                
                int result = JOptionPane.showConfirmDialog(getFrame(),chooser,"Compare Managers",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null);
                
                if(result == JOptionPane.CANCEL_OPTION)
                    return;
                
                if(chooser.getSelectionMode().equals(ManagerAdapter.PROCEDURE_MANAGER_TYPE)) {
                    JOptionPane.showMessageDialog(parentFrame, "The ability to compare procedures has not yet been implemented.", "Unsupport Feature", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if((chooser.getSelectedFiles()[0] == null) || (chooser.getSelectedFiles()[1] == null)){
                    JOptionPane.showMessageDialog(parentFrame, "You need to select 1st and 2nd file.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if(chooser.getSelectionMode().equals(ManagerAdapter.ACCESS_MANAGER_TYPE)) {
                    File files[] = chooser.getSelectedFiles();
                    AccessManager[] am = new AccessManager[files.length];
                    try {
                        for(int k=0; k<files.length; k++) {
                            am[k] = new AccessManager();
                            
                            try {
                                am[k].readFile(files[k]);
                            } catch (Exception ex) {
                                throw new Exception("Corrupted File: "+files[k].getName());
                            }
                            
                            if(!am[k].isValid()) {
                                throw new Exception("No rule tree found in file "+files[k].getName());
                            }
                        }
                        
                        CompareAccessManager cam = new CompareAccessManager(am);
                        
                        if(!cam.isValid()) {
                            throw new Exception("Incompatible Comparison"+cam.getAccessManagers()[0].getFile().getName()+"::"+cam.getAccessManagers()[1].getFile().getName());
                        }
                        CompareAccessManagerComponent camComponent = new CompareAccessManagerComponent(parentFrame, cam);
                        addTabbedPane(camComponent);
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Ruletree File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.start();
    }
    
}