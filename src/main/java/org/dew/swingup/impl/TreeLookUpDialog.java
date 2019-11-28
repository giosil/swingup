package org.dew.swingup.impl;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.util.*;
import java.util.List;

import org.dew.swingup.*;
import org.dew.swingup.util.*;
import org.dew.swingup.components.*;

/**
 * Implementazione di ALookUpDialog che costruisce una maschera di LookUp
 * costituita da un campo codice e un campo descrizione.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class TreeLookUpDialog extends ALookUpDialog
{
  protected List oSelectedRecord = null;
  protected JTree oTree;
  protected boolean boCancel = false;
  
  JButton btnSelect;
  JButton btnClose;
  
  public
  TreeLookUpDialog()
  {
    super();
    setSize(600, 500);
  }
  
  public
  TreeLookUpDialog(String sTitle)
  {
    super(sTitle);
    setSize(600, 500);
  }
  
  /**
   * Metodo invocato prima della chiusura della finestra di dialogo.
   * Se restituisce false la finestra non viene chiusa.
   *
   * @return Flag di chiusura
   */
  public
  boolean doClosing()
  {
    return true;
  }
  
  /**
   * Metodo invocato quando si apre la finestra di dialogo.
   */
  protected
  void onOpened()
  {
    oSelectedRecord = null;
    if(oLookUpFinder != null) {
      try {
        showTree(oLookUpFinder.find(sEntity, new ArrayList()));
      }
      catch(Exception ex) {
        GUIMessage.showException("Errore durante la costruzione dell'albero", ex);
      }
    }
  }
  
  /**
   * Metodo invocato quando si attiva la finestra di dialogo.
   */
  protected
  void onActivated()
  {
  }
  
  protected
  Container buildGUI(Container oFilterContainer)
    throws Exception
  {
    JPanel oMainPanel = new JPanel();
    oMainPanel.setLayout(new BorderLayout());
    
    oTree = new JTree(getBlankTreeModel());
    oTree.setShowsRootHandles(true);
    oTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)
        oTree.getLastSelectedPathComponent();
        if(dmtn == null) {
          oSelectedRecord = null;
          btnSelect.setEnabled(false);
          return;
        }
        Object oUserObject = dmtn.getUserObject();
        if(oUserObject == null) {
          oSelectedRecord = null;
          btnSelect.setEnabled(false);
          return;
        }
        if(dmtn.getParent() == null) {
          oSelectedRecord = null;
          btnSelect.setEnabled(false);
          return;
        }
        int iLevel = dmtn.getLevel();
        String sParent = dmtn.getParent().toString();
        String sItem = oUserObject.toString();
        
        oSelectedRecord = normalize(sItem, iLevel, sParent);
        
        btnSelect.setEnabled(true);
      }
    });
    oTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          boCancel = false;
          dispose();
        }
      }
    });
    
    oMainPanel.add(new JScrollPane(oTree), BorderLayout.CENTER);
    oMainPanel.add(buildGUIActions(), BorderLayout.EAST);
    
    getRootPane().setDefaultButton(btnClose);
    
    return oMainPanel;
  }
  
  protected
  Container buildGUIActions()
  {
    JPanel oButtonsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
    
    btnSelect = GUIUtil.buildActionButton(IConstants.sGUIDATA_SELECT, "select");
    btnSelect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boCancel = false;
        dispose();
      }
    });
    btnSelect.setDefaultCapable(true);
    
    btnClose = GUIUtil.buildActionButton(IConstants.sGUIDATA_EXIT, "exit");
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boCancel = true;
        dispose();
      }
    });
    
    btnSelect.setEnabled(false);
    
    oButtonsPanel.add(btnSelect);
    oButtonsPanel.add(btnClose);
    
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEtchedBorder());
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    
    return oActionsPanel;
  }
  
  public
  List getSelectedRecord()
  {
    if(boCancel) return null;
    
    return oSelectedRecord;
  }
  
  public
  void setFilter(List oFilter)
  {
  }
  
  public
  void setRecords(List oRecords)
  {
  }
  
  protected
  List normalize(String sItem, int iLevel, String sFater)
  {
    List listResult = new ArrayList();
    listResult.add(sItem);
    listResult.add(sItem);
    listResult.add(sItem);
    return listResult;
  }
  
  protected
  void showTree(List oResult)
    throws Exception
  {
    if(oResult == null || oResult.size() == 0) {
      oTree.setModel(getBlankTreeModel());
      return;
    }
    
    Object oFirstElement = oResult.get(0);
    
    if(oFirstElement instanceof Map) {
      Map mapTree = (Map) oFirstElement;
      oTree.setModel(new DefaultTreeModel(GUIUtil.buildTree(mapTree, sEntity)));
    }
  }
  
  protected
  TreeModel getBlankTreeModel()
  {
    DefaultMutableTreeNode dmtRoot = new DefaultMutableTreeNode("*", true);
    DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("*", false);
    dmtRoot.add(dmtn);
    return new DefaultTreeModel(dmtRoot);
  }
}
