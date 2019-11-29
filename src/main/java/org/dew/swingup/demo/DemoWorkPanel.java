package org.dew.swingup.demo;

import org.dew.swingup.*;
import org.dew.swingup.util.GUIUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.URL;

import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("rawtypes")

public
class DemoWorkPanel extends AWorkPanel
{
  private static final long serialVersionUID = -2152144060321676515L;
  
  protected boolean boOpened = false;
  protected JPanel   jpMainPanel;
  protected JLabel   jlTitle;
  protected JPanel   jpMenu;
  protected JLabel[] arrayOfJLabel;
  
  protected static final int iWIDTH_MENU = 360;
  
  public
  Container buildGUI()
    throws Exception
  {
    jpMenu = new JPanel();
    jpMenu.setPreferredSize(new Dimension(iWIDTH_MENU, 0));
    jpMenu.setOpaque(true);
    jpMenu.setBackground(Color.white);
    
    String sJSEnv = System.getProperty("jsenv");
    if(sJSEnv == null || sJSEnv.length() == 0) {
      sJSEnv = System.getProperty("jnlp.jsenv");
    }
    
    URL urlImageWorkPanel = null;
    if(sJSEnv != null && sJSEnv.length() > 0) {
      urlImageWorkPanel = ResourcesMgr.getURLResource("images/" + sJSEnv + "_workpanel.jpg");
      if(urlImageWorkPanel == null) {
        urlImageWorkPanel = ResourcesMgr.getURLResource("images/workpanel.jpg");
      }
    }
    else {
      urlImageWorkPanel = ResourcesMgr.getURLResource("images/workpanel.jpg");
    }
    JLabel jlImage = null;
    if(urlImageWorkPanel != null) {
      jlImage = new JLabel(new ImageIcon(urlImageWorkPanel));
    }
    else {
      jlImage = new JLabel();
    }
    jlImage.setOpaque(true);
    jlImage.setBackground(Color.white);
    
    jlTitle = new JLabel("Benvenuti in Admin", SwingConstants.LEFT);
    jlTitle.setFont(GUIUtil.modifyFont(jlTitle.getFont(), 20));
    jlTitle.setForeground(Color.gray);
    jlTitle.setOpaque(true);
    jlTitle.setBackground(Color.white);
    jlTitle.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
        }
      }
    });
    
    JPanel jpEast = new JPanel(new BorderLayout());
    jpEast.setOpaque(true);
    jpEast.setBackground(Color.white);
    jpEast.add(jlImage, BorderLayout.SOUTH);
    
    jpMainPanel = new JPanel(new BorderLayout(4, 4));
    jpMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 10));
    jpMainPanel.setOpaque(true);
    jpMainPanel.setBackground(Color.white);
    jpMainPanel.add(jlTitle,  BorderLayout.NORTH);
    jpMainPanel.add(jpMenu,   BorderLayout.WEST);
    jpMainPanel.add(jpEast,   BorderLayout.EAST);
    return jpMainPanel;
  }
  
  protected
  boolean buildMenu()
  {
    User user = ResourcesMgr.getSessionManager().getUser();
    
    Map mapEnabledFlags = user.getResourcesByFather("menu");
    boolean boAtLeastOne = false;
    arrayOfJLabel = new JLabel[4];
    // boAtLeastOne deve essere a destra di || poiche' altrimenti addMenu rischia di non essere "valutato" e quindi eseguito.
    boAtLeastOne = addMenu(mapEnabledFlags, "images/checks.jpg",   "Single Frame", "test.single")   || boAtLeastOne;
    boAtLeastOne = addMenu(mapEnabledFlags, "images/stat.png",     "Multi Frames", "test.frame")    || boAtLeastOne;
    boAtLeastOne = addMenu(mapEnabledFlags, "images/docs.jpg",     "File Manager", "test.filemgr")  || boAtLeastOne;
    boAtLeastOne = addMenu(mapEnabledFlags, "images/calendar.png", "Calendar",     "test.calendar") || boAtLeastOne;
    
    jpMenu  = new JPanel(new GridLayout(arrayOfJLabel.length, 1, 4, 4));
    jpMenu.setPreferredSize(new Dimension(iWIDTH_MENU, 0));
    jpMenu.setOpaque(true);
    jpMenu.setBackground(Color.white);
    for(int i = 0; i < arrayOfJLabel.length; i++) {
      if(arrayOfJLabel[i] == null) continue;
      arrayOfJLabel[i].setBorder(BorderFactory.createEmptyBorder(0, iWIDTH_MENU, 0, 0));
      jpMenu.add(arrayOfJLabel[i]);
    }
    return boAtLeastOne;
  }
  
  /**
   * Imposta il flag di abilitazione della GUI.
   *
   * @param boEnabled boolean
   */
  public
  void setEnabled(boolean boEnabled)
  {
    if(boEnabled) {
      boolean boAtLeastOne = buildMenu();
      if(boAtLeastOne) {
        jlTitle.setText("Attivit\340 principali di amministrazione");
      }
      else {
        jlTitle.setText("Benvenuti in CFAdmin");
      }
      for(int i = 0; i < arrayOfJLabel.length; i++) {
        if(arrayOfJLabel[i] == null) continue;
        arrayOfJLabel[i].setBorder(BorderFactory.createEmptyBorder(0, iWIDTH_MENU + iWIDTH_MENU/10, 0, 0));
      }
      jpMainPanel.add(jpMenu, BorderLayout.WEST);
      new Thread(new AnimazioneMenu()).start();
    }
    else {
      jlTitle.setText("");
      jpMainPanel.remove(jpMenu);
    }
  }
  
  public
  void onOpened()
  {
    boOpened = true;
  }
  
  /**
   * Restituisce la pagina della guida in linea.
   *
   * @param sTitle String
   * @return String
   */
  public
  String getHelpDoc(String sTitle)
  {
    return null;
  }
  
  protected
  void onClick(String sName)
  {
    if(sName == null) return;
    IMenuManager menuManager = ResourcesMgr.getMenuManager();
    menuManager.actionPerformed(sName);
  }
  
  protected
  boolean addMenu(Map mapEnabledFlags, String sImageIcon, String sText, String sName)
  {
    int iFree = -1;
    for(int i = 0; i < arrayOfJLabel.length; i++) {
      if(arrayOfJLabel[i] == null) {
        iFree = i;
        break;
      }
    }
    if(iFree == -1) return false;
    boolean boEnabled = mapEnabledFlags == null || mapEnabledFlags.isEmpty();
    if(!boEnabled) {
      Object oEnabled = mapEnabledFlags.get(sName);
      boEnabled = oEnabled != null ? "1SYTsyt".indexOf(oEnabled.toString()) >= 0 : false;
    }
    if(boEnabled) {
      arrayOfJLabel[iFree] = buildMenuJLabel(sImageIcon, sText, sName);
      return true;
    }
    return false;
  }
  
  protected
  JLabel buildMenuJLabel(String sImageIcon, String sText, String sName)
  {
    JLabel jlabel = new JLabel();
    if(sImageIcon != null && sImageIcon.length() > 0) {
      URL urlImageIcon = ResourcesMgr.getURLResource(sImageIcon);
      if(urlImageIcon != null) {
        jlabel.setIcon(new ImageIcon(urlImageIcon));
      }
    }
    jlabel.setFont(GUIUtil.modifyFont(jlabel.getFont(), 10));
    jlabel.setText(sText);
    jlabel.setIconTextGap(16);
    jlabel.setHorizontalAlignment(SwingConstants.LEFT);
    jlabel.setOpaque(true);
    jlabel.setBackground(Color.white);
    jlabel.setName(sName);
    jlabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    jlabel.setForeground(new Color(0, 0, 128));
    jlabel.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        Color cFg = ((JComponent) e.getSource()).getForeground();
        if(cFg.equals(Color.white)) return;
        ((JComponent) e.getSource()).setForeground(new Color(128, 128, 255));
      }
      public void mouseExited(MouseEvent e) {
        Color cFg = ((JComponent) e.getSource()).getForeground();
        if(cFg.equals(Color.white)) return;
        ((JComponent) e.getSource()).setForeground(new Color(0, 0, 128));
      }
      public void mouseClicked(MouseEvent e) {
        Color cFg = ((JComponent) e.getSource()).getForeground();
        if(cFg.equals(Color.white)) return;
        onClick(((JComponent) e.getSource()).getName());
      }
    });
    return jlabel;
  }
  
  class AnimazioneMenu implements Runnable
  {
    public void run() {
      if(arrayOfJLabel == null) return;
      for(int i = 0; i < arrayOfJLabel.length; i++) {
        if(arrayOfJLabel[i] == null) continue;
        arrayOfJLabel[i].setForeground(Color.white);
      }
      int iStep = iWIDTH_MENU / 10;
      for(int l = iWIDTH_MENU; l >= 0; l = l - iStep) {
        try{ Thread.sleep(60); } catch(Exception ex) {}
        for(int i = 0; i < arrayOfJLabel.length; i++) {
          if(arrayOfJLabel[i] == null) continue;
          arrayOfJLabel[i].setBorder(BorderFactory.createEmptyBorder(0, l, 0, 0));
          if(l <= iStep) {
            arrayOfJLabel[i].setForeground(new Color(0, 0, 128));
          }
          arrayOfJLabel[i].updateUI();
        }
      }
    }
  }
}
