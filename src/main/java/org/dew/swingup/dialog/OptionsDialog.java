package org.dew.swingup.dialog;

import java.awt.*;

import javax.swing.*;

import java.util.*;
import java.util.List;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Dialogo utilizzato per la modifica delle preferenze utente.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class OptionsDialog extends AJDialog
{
  JComboBox oComboLAFs;
  JComboBox oComboThemes;
  JCheckBox oDebugCheck;
  String sCurrLAF;
  String sCurrTheme;
  
  public static List listAllLookAndFeel = new ArrayList(18);
  static {
    listAllLookAndFeel.add("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    listAllLookAndFeel.add("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    listAllLookAndFeel.add("javax.swing.plaf.metal.MetalLookAndFeel");
    listAllLookAndFeel.add("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    listAllLookAndFeel.add("com.incors.plaf.kunststoff.KunststoffLookAndFeel");
    listAllLookAndFeel.add("com.jgoodies.looks.windows.WindowsLookAndFeel");
    listAllLookAndFeel.add("com.jgoodies.looks.plastic.PlasticLookAndFeel");
    listAllLookAndFeel.add("net.sourceforge.napkinlaf.NapkinLookAndFeel");
    listAllLookAndFeel.add("com.nilo.plaf.nimrod.NimRODLookAndFeel");
    listAllLookAndFeel.add("org.jvnet.substance.SubstanceLookAndFeel");
    listAllLookAndFeel.add("de.muntjak.tinylookandfeel.TinyLookAndFeel");
    listAllLookAndFeel.add("com.digitprop.tonic.TonicLookAndFeel");
    listAllLookAndFeel.add("net.infonode.gui.laf.InfoNodeLookAndFeel");
    listAllLookAndFeel.add("net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
    listAllLookAndFeel.add("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
    listAllLookAndFeel.add("mdlaf.MaterialLookAndFeel");
    listAllLookAndFeel.add("com.alee.laf.WebLookAndFeel");
  }
  
  public
  OptionsDialog(Frame frame)
  {
    super(frame, "Opzioni", true);
    this.setResizable(false);
  }
  
  public
  OptionsDialog()
  {
    super("Opzioni");
    this.setResizable(false);
  }
  
  public
  Container buildGUI()
    throws Exception
  {
    JPanel oPanel = new JPanel(new GridLayout(3, 1, 4, 4));
    oPanel.setBorder(BorderFactory.createTitledBorder("Impostazioni generali"));
    
    sCurrLAF = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_PLAF, "javax.swing.plaf.metal.MetalLookAndFeel");
    
    List oLAFs = new ArrayList();
    addLAF(oLAFs, sCurrLAF);
    for(int i = 0; i < listAllLookAndFeel.size(); i++) {
      addLAF(oLAFs, (String) listAllLookAndFeel.get(i));
    }
    
    List listThemes = ThemeManager.listThemes;
    sCurrTheme = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_THEME);
    
    int iSelected = 0;
    oComboLAFs = new JComboBox();
    for(int i = 0; i < oLAFs.size(); i++) {
      String sLAF = (String) oLAFs.get(i);
      oComboLAFs.addItem(sLAF);
      if(sLAF.equals(sCurrLAF)) {
        iSelected = i;
      }
    }
    oComboLAFs.setSelectedIndex(iSelected);
    
    int iSelectedTheme = 0;
    oComboThemes = new JComboBox();
    for(int i = 0; i < listThemes.size(); i++) {
      String sTheme = (String) listThemes.get(i);
      oComboThemes.addItem(sTheme);
      if(sCurrTheme != null && sTheme.equals(sCurrTheme)) {
        iSelectedTheme = i;
      }
    }
    oComboThemes.setSelectedIndex(iSelectedTheme);
    
    boolean boDebug = ResourcesMgr.bDebug;
    oDebugCheck = new JCheckBox();
    oDebugCheck.setSelected(boDebug);
    
    oPanel.add(GUIUtil.buildLabelledComponent(oComboLAFs,   "Look & Feel: ", 100));
    oPanel.add(GUIUtil.buildLabelledComponent(oComboThemes, "Tema: ",        100));
    oPanel.add(GUIUtil.buildLabelledComponent(oDebugCheck,  "Debug: ",       100));
    
    return oPanel;
  }
  
  protected static
  void addLAF(List oLAFs, String sClassName)
  {
    if(oLAFs.contains(sClassName)) return;
    try {
      Class.forName(sClassName);
    }
    catch(Throwable th) {
      return;
    }
    oLAFs.add(sClassName);
  }
  
  public
  boolean doOk()
  {
    String sNewLAF = oComboLAFs.getSelectedItem().toString();
    String sNewTheme = oComboThemes.getSelectedItem().toString();
    boolean boDebug = oDebugCheck.isSelected();
    
    boolean boRestart = false;
    if(!sNewTheme.equals(sCurrTheme)) {
      if(GUIMessage.getConfirmation(this,
        "Il cambiamento del tema richiede la chiusura delle finestre aperte. Procedere?")) {
        boRestart = true;
      }
    }
    
    try {
      UIManager.setLookAndFeel(sNewLAF);
      ThemeManager.setTheme(sNewTheme);
      ResourcesMgr.config.setProperty(ResourcesMgr.sAPP_THEME, sNewTheme);
      ResourcesMgr.dat.setProperty(ResourcesMgr.sAPP_THEME, sNewTheme);
      SwingUtilities.updateComponentTreeUI(ResourcesMgr.mainFrame);
      ResourcesMgr.getGUIManager().updateUIManager();
      
      ResourcesMgr.config.setProperty(ResourcesMgr.sAPP_PLAF, sNewLAF);
      ResourcesMgr.dat.setProperty(ResourcesMgr.sAPP_PLAF, sNewLAF);
      
      ResourcesMgr.bDebug = boDebug;
      ResourcesMgr.config.setProperty(ResourcesMgr.sAPP_DEBUG, boDebug ? "1" : "0");
      ResourcesMgr.dat.setProperty(ResourcesMgr.sAPP_DEBUG, boDebug ? "1" : "0");
      ResourcesMgr.getLogger().setDebug(boDebug);
      
      ResourcesMgr.saveDat();
      
      if(boRestart) {
        restartMainFrame();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    
    return true;
  }
  
  private
  void restartMainFrame()
  {
    System.out.println(ResourcesMgr.sLOG_PREFIX + " restart GUI...");
    ResourcesMgr.mainFrame.dispose();
    
    Properties cfg = ResourcesMgr.config;
    String sAppName = cfg.getProperty(ResourcesMgr.sAPP_NAME);
    String sAppVers = cfg.getProperty(ResourcesMgr.sAPP_VERSION);
    
    MainFrame frame = new MainFrame(sAppName + " ver. " + sAppVers);
    ResourcesMgr.mainFrame = frame;
    
    try {
      ResourcesMgr.loadWorkPanel();
      ResourcesMgr.loadMenuManager();
      ResourcesMgr.buildWaitPleaseWindow(null);
    }
    catch(Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException("Error during restart.");
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " show main frame...");
    try {
      frame.build();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la costruzione del MainFrame", ex);
    }
    SwingUtilities.updateComponentTreeUI(frame);
    frame.pack();
    
    int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    screenHeight = screenHeight - screenHeight / 27;
    
    frame.setSize(new Dimension(screenWidth, screenHeight));
    
    frame.setVisible(true);
  }
  
  public
  boolean doCancel()
  {
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
  }
}
