package org.dew.swingup.editors;

import java.io.*;
import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Estensione di JPanel progettata per l'editing di una entit&agrave;.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public abstract
class AEntityEditor extends JPanel implements IEntityEditorConstants, ActionListener, ListSelectionListener, IWorkObject
{
  protected IEntityMgr oEntityMgr;
  
  protected Container cntFilter;
  protected Container cntResult;
  protected Container cntDetail;
  protected Container cntActions;
  protected Container cntFilterActions;
  protected Container cntBigDetail;
  protected Container cntOtherDetail;
  protected JTabbedPane oTabbedPane;
  protected JPanel oEditorPanel;
  protected JPanel oBigDetailPanel;
  protected JPanel oOtherDetailPanel;
  
  protected JButton btnFind;
  protected JButton btnReset;
  
  protected JButton btnNew;
  protected JButton btnOpen;
  protected JButton btnSave;
  protected JButton btnCancel;
  protected JButton btnDelete;
  protected JButton btnPrint;
  protected JButton btnSelect;
  protected JButton btnClose;
  protected JButton btnToggle;
  
  protected boolean boNew = false;
  protected boolean boEditing = false;
  protected boolean boGlobalNotificationEnabled = true;
  
  protected Object oStartupFilterValues;
  protected boolean boAllowEditing = false;
  protected int iMaxRowsActions = 8;
  protected ArrayList oEntityEditorListeners = new ArrayList();
  protected ArrayList oActionListeners = new ArrayList();
  protected int iEditorStatus = iSTATUS_STARTUP;
  
  protected boolean boInitPerformed = false;
  protected boolean boExportResultOnFireSelect = false;
  
  protected Object oChoice;
  
  protected String sMSG_CONFIRM_ON_DELETE    = "Eliminare l'elemento selezionato?";
  protected String sMSG_CONFIRM_ON_ENABLING  = "Abilitare l'elemento selezionato?";
  protected String sMSG_CONFIRM_ON_DISABLING = "Disabilitare l'elemento selezionato?";
  protected String sMSG_CONFIRM_ON_CANCEL    = "Annullare la modifica?";
  
  protected String sBORDER_TITLE_RESULT      = "Risultato";
  
  public
  AEntityEditor()
  {
    super(new BorderLayout());
    boExportResultOnFireSelect = ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_EDITORS_EXPORT, false);
  }
  
  /**
   * Inizializza l'entity editor.
   *
   * @param boAllowEditing boolean che specifica se permettere l'editing.
   * @throws Exception
   */
  public
  void init(boolean boAllowEditing)
    throws Exception
  {
    if(boInitPerformed) return;
    this.boAllowEditing = boAllowEditing;
    
    beforeBuildGUI();
    this.add(buildGUI());
    afterBuildGUI();
    
    boInitPerformed = true;
  }
  
  /**
   * Restituisce il container del filtro.
   *
   * @return Container
   */
  public
  Container getFilterContainer()
  {
    return cntFilter;
  }
  
  /**
   * Restituisce il container del risultato della ricerca.
   *
   * @return Container
   */
  public
  Container getResultContainer()
  {
    return cntResult;
  }
  
  /**
   * Restituisce il container del dettaglio.
   *
   * @return Container
   */
  public
  Container getDetailContainer()
  {
    return cntDetail;
  }
  
  /**
   * Restituisce il container del dettaglio alternativo.
   *
   * @return Container
   */
  public
  Container getBigDetailContainer()
  {
    return cntBigDetail;
  }
  
  /**
   * Restituisce l'altro container del dettaglio alternativo.
   *
   * @return Container
   */
  public
  Container getOtherDetailContainer()
  {
    return cntOtherDetail;
  }
  
  /**
   * Imposta i valori del filtro da applicare allo startup.
   *
   * @param oValues Valori
   */
  public
  void setStartupFilterValues(Object oValues)
  {
    this.oStartupFilterValues = oValues;
  }
  
  /**
   * Imposta il gestore dell'entity.
   *
   * @param oEntityMgr IEntityMgr
   */
  public
  void setEntityMgr(IEntityMgr oEntityMgr)
  {
    this.oEntityMgr = oEntityMgr;
  }
  
  /**
   * Imposta il flag di visibilit&agrave; del pannello azioni.
   *
   * @param boActionsVisible boolean
   */
  public
  void setActionsVisible(boolean boActionsVisible)
  {
    cntActions.setVisible(boActionsVisible);
  }
  
  /**
   * Restituisce il flag di visibilit&agrave; del pannello azioni.
   *
   * @return boolean
   */
  public
  boolean isActionsVisible()
  {
    return cntActions.isVisible();
  }
  
  /**
   * Aggiunge un ascoltatore di eventi relativi alla pressione dei pulsanti.
   *
   * @param oActionListener ActionListener
   */
  public
  void addActionListener(ActionListener oActionListener)
  {
    if(oActionListener == null) return;
    if(!oActionListeners.contains(oActionListener)) {
      oActionListeners.add(oActionListener);
    }
  }
  
  /**
   * Rimuove un ascoltatore di eventi relativi alla pressione dei pulsanti.
   *
   * @param oActionListener ActionListener
   */
  public
  void removeActionListener(ActionListener oActionListener)
  {
    oActionListeners.remove(oActionListener);
  }
  
  /**
   * Aggiunge un ascoltatore di eventi AEntityEditor.
   *
   * @param oEntityEditorListener IEntityEditorListener
   */
  public
  void addEntityEditorListener(IEntityEditorListener oEntityEditorListener)
  {
    if(oEntityEditorListener == null) return;
    if(!oEntityEditorListeners.contains(oEntityEditorListener)) {
      oEntityEditorListeners.add(oEntityEditorListener);
    }
  }
  
  /**
   * Rimuove un ascoltatore di eventi AEntityEditor.
   *
   * @param oEntityEditorListener IEntityEditorListener
   */
  public
  void removeEntityEditorListener(IEntityEditorListener oEntityEditorListener)
  {
    oEntityEditorListeners.remove(oEntityEditorListener);
  }
  
  /**
   * Imposta il flag di norifica globale (vedi StaticActionListeners).
   *
   * @param boFlag boolean
   */
  public
  void setEnableGlobalNotification(boolean boFlag)
  {
    this.boGlobalNotificationEnabled = boFlag;
  }
  
  /**
   * Ritorna il flag di norifica globale (vedi StaticActionListeners).
   *
   * @return boolean
   */
  public
  boolean isGlobalNotificationEnabled()
  {
    return boGlobalNotificationEnabled;
  }
  
  /**
   * Restituisce l'entit&agrave; scelta dall'utente.
   *
   * @return Object
   */
  public
  Object getChoice()
  {
    return oChoice;
  }
  
  /**
   * Imposta l'entit&agrave; scelta.
   *
   * @param oChoice Object
   */
  public
  void setChoice(Object oChoice)
  {
    this.oChoice = oChoice;
  }
  
  /**
   * Restituisce lo stato dell'editor.
   *
   * @return intero che codifica lo stato dell'editor.
   */
  public
  int getEditorStatus()
  {
    return iEditorStatus;
  }
  
  public
  void onActivated()
  {
    if(iEditorStatus != iSTATUS_STARTUP) {
      return;
    }
    oChoice = null;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Container oFilterContainer = getFilterContainer();
        if(oFilterContainer != null && oFilterContainer.isEnabled()) {
          oFilterContainer.requestFocus();
        }
      }
    });
  }
  
  public
  void onOpened()
  {
    try {
      fireReset();
      setFilterValues(oStartupFilterValues);
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Container oFilterContainer = getFilterContainer();
        if(oFilterContainer != null) {
          oFilterContainer.requestFocus();
        }
      }
    });
  }
  
  /**
   * Ridefinire tale metodo per le impostazioni da effettuare prima
   * della costruzione della GUI.
   */
  protected
  void beforeBuildGUI()
    throws Exception
  {
  }
  
  /**
   * Ridefinire tale metodo per le impostazioni da effettuare dopo
   * la costruzione della GUI.
   */
  protected
  void afterBuildGUI()
    throws Exception
  {
  }
  
  protected
  Container buildGUI()
    throws Exception
  {
    oEditorPanel = new JPanel(new BorderLayout(4, 4));
    
    cntFilter        = buildGUIFilter();
    cntResult        = buildGUIResult();
    cntDetail        = buildGUIDetail();
    cntActions       = buildGUIActions();
    cntFilterActions = buildGUIFilterActions();
    cntBigDetail     = buildGUIBigDetail();
    cntOtherDetail   = buildGUIOtherDetail();
    
    if(cntFilter != null) {
      JPanel oFilterPanel = new JPanel(new BorderLayout());
      oFilterPanel.add(cntFilter,        BorderLayout.CENTER);
      oFilterPanel.add(cntFilterActions, BorderLayout.EAST);
      oEditorPanel.add(oFilterPanel,     BorderLayout.NORTH);
    }
    else {
      cntFilter = new JPanel();
      oEditorPanel.add(cntFilter, BorderLayout.NORTH);
    }
    
    if(cntResult != null) {
      if(sBORDER_TITLE_RESULT != null && sBORDER_TITLE_RESULT.length() > 0) {
        JPanel oResultPanel = new JPanel(new BorderLayout());
        oResultPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sBORDER_TITLE_RESULT));
        oResultPanel.add(cntResult, BorderLayout.CENTER);
        oEditorPanel.add(oResultPanel, BorderLayout.CENTER);
      }
      else {
        oEditorPanel.add(cntResult, BorderLayout.CENTER);
      }
    }
    else {
      cntResult = new JPanel();
      oEditorPanel.add(cntResult, BorderLayout.CENTER);
    }
    
    oEditorPanel.add(cntActions, BorderLayout.EAST);
    
    if(cntDetail != null) {
      oEditorPanel.add(cntDetail, BorderLayout.SOUTH);
    }
    else {
      cntDetail = new JPanel();
    }
    
    setEditorStatus(iSTATUS_STARTUP);
    
    if(cntBigDetail != null || cntOtherDetail != null) {
      JPanel oResult = new JPanel(new BorderLayout());
      oTabbedPane = new JTabbedPane();
      oTabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          JPanel oPanel = (JPanel) oTabbedPane.getSelectedComponent();
          oPanel.add(cntActions, BorderLayout.EAST);
          oPanel.updateUI();
        }
      });
      oResult.add(oTabbedPane, BorderLayout.CENTER);
      
      oTabbedPane.addTab("Ricerca", oEditorPanel);
      
      if(cntBigDetail != null) {
        String sTabLabelBigDetail = null;
        if(cntBigDetail instanceof ITagable) {
          sTabLabelBigDetail = ((ITagable) cntBigDetail).getTag();
        }
        if(sTabLabelBigDetail == null ||
          sTabLabelBigDetail.trim().length() == 0) {
          sTabLabelBigDetail = "Dettaglio";
        }
        oBigDetailPanel = new JPanel(new BorderLayout(4, 4));
        oBigDetailPanel.add(cntBigDetail, BorderLayout.CENTER);
        oTabbedPane.addTab(sTabLabelBigDetail, oBigDetailPanel);
      }
      
      if(cntOtherDetail != null) {
        String sTabLabelOtherDetail = null;
        if(cntOtherDetail instanceof ITagable) {
          sTabLabelOtherDetail = ((ITagable) cntOtherDetail).getTag();
        }
        if(sTabLabelOtherDetail == null ||
          sTabLabelOtherDetail.trim().length() == 0) {
          sTabLabelOtherDetail = "Altro";
        }
        oOtherDetailPanel = new JPanel(new BorderLayout(4, 4));
        oOtherDetailPanel.add(cntOtherDetail, BorderLayout.CENTER);
        oTabbedPane.addTab(sTabLabelOtherDetail, oOtherDetailPanel);
      }
      
      return oResult;
    }
    return oEditorPanel;
  }
  
  public
  void valueChanged(ListSelectionEvent e)
  {
    if(e.getValueIsAdjusting()) return;
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      if(onSelection()) {
        setEditorStatus(iSTATUS_VIEW);
        for(int i = 0; i < oEntityEditorListeners.size(); i++) {
          ((IEntityEditorListener) oEntityEditorListeners.get(i)).onSelection();
        }
      }
      else {
        onLostSelection();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore nella selezione", ex);
    }
    finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    for(int i = 0; i < oActionListeners.size(); i++) {
      ((ActionListener) oActionListeners.get(i)).actionPerformed(e);
    }
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    try {
      if(sActionCommand.equals(sACTION_FIND))    fireFind();   else
      if(sActionCommand.equals(sACTION_RESET))   fireReset();  else
      if(sActionCommand.equals(sACTION_TOGGLE))  fireToggle(); else
      if(sActionCommand.equals(sACTION_NEW))     fireNew();    else
      if(sActionCommand.equals(sACTION_OPEN))    fireOpen();   else
      if(sActionCommand.equals(sACTION_SAVE))    fireSave();   else
      if(sActionCommand.equals(sACTION_CANCEL))  fireCancel(); else
      if(sActionCommand.equals(sACTION_DELETE))  fireDelete(); else
      if(sActionCommand.equals(sACTION_PRINT))   firePrint();  else
      if(sActionCommand.equals(sACTION_SELECT))  fireSelect(); else
      if(sActionCommand.equals(sACTION_EXIT))    fireExit();
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
  
  public
  void fireFind()
    throws Exception
  {
    oChoice = null;
    
    if(btnFind != null)  btnFind.setEnabled(false);
    if(btnReset != null) btnReset.setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          boolean boCheckBeforeFind = checkBeforeFind();
          if(boCheckBeforeFind) {
            doFind();
          }
          if(boCheckBeforeFind) {
            for(int i = 0; i < oEntityEditorListeners.size(); i++) {
              ((IEntityEditorListener) oEntityEditorListeners.get(i)).onFind();
            }
            if(boGlobalNotificationEnabled) {
              ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_FIND);
              StaticActionListeners.notifyActionEvent(this, e);
            }
          }
        }
        catch(Exception ex) {
          GUIMessage.showException("Errore durante la ricerca", ex);
        }
        finally {
          if(btnFind != null)  btnFind.setEnabled(true);
          if(btnReset != null) btnReset.setEnabled(true);
          setCursor(Cursor.getDefaultCursor());
        }
      }
    });
  }
  
  public
  void fireReset()
    throws Exception
  {
    oChoice = null;
    boNew = false;
    setEditorStatus(iSTATUS_STARTUP);
    Container oFilterContainer = getFilterContainer();
    if(oFilterContainer != null) {
      oFilterContainer.requestFocus();
    }
    doReset();
    for(int i = 0; i < oEntityEditorListeners.size(); i++) {
      ((IEntityEditorListener) oEntityEditorListeners.get(i)).onClearSelection();
      if(boGlobalNotificationEnabled) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_RESET);
        StaticActionListeners.notifyActionEvent(this, e);
      }
    }
  }
  
  public
  void fireToggle()
    throws Exception
  {
    if(isElementEnabled()) {
      if(!GUIMessage.getConfirmation(sMSG_CONFIRM_ON_DISABLING)) return;
    }
    else {
      if(!GUIMessage.getConfirmation(sMSG_CONFIRM_ON_ENABLING)) return;
    }
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      doToggle();
      if(boGlobalNotificationEnabled) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_TOGGLE);
        StaticActionListeners.notifyActionEvent(this, e);
      }
    }
    finally {
      setCursor(Cursor.getDefaultCursor());
    }
    setEditorStatus(iSTATUS_VIEW);
  }
  
  public
  void fireNew()
    throws Exception
  {
    if(checkBeforeNew()) {
      boNew = true;
      boEditing = true;
      setEditorStatus(iSTATUS_EDITING);
      doNew();
      for(int i = 0; i < oEntityEditorListeners.size(); i++) {
        ((IEntityEditorListener) oEntityEditorListeners.get(i)).onNew();
      }
      if(boGlobalNotificationEnabled) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_NEW);
        StaticActionListeners.notifyActionEvent(this, e);
      }
    }
  }
  
  public
  void fireOpen()
    throws Exception
  {
    if(checkBeforeOpen()) {
      boNew = false;
      boEditing = true;
      setEditorStatus(iSTATUS_EDITING);
      doOpen();
      for(int i = 0; i < oEntityEditorListeners.size(); i++) {
        ((IEntityEditorListener) oEntityEditorListeners.get(i)).onModify();
      }
      if(boGlobalNotificationEnabled) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_OPEN);
        StaticActionListeners.notifyActionEvent(this, e);
      }
    }
  }
  
  public
  boolean fireSave()
    throws Exception
  {
    boolean boResult = false;
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      boResult = doSave(boNew);
      if(boResult) {
        for(int i = 0; i < oEntityEditorListeners.size(); i++) {
          ((IEntityEditorListener) oEntityEditorListeners.get(i)).onSave(boNew);
        }
        if(boGlobalNotificationEnabled) {
          ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_SAVE);
          StaticActionListeners.notifyActionEvent(this, e);
        }
      }
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    if(boResult) {
      boNew = false;
      boEditing = false;
      setEditorStatus(iSTATUS_VIEW);
    }
    return boResult;
  }
  
  public
  boolean fireCancel()
    throws Exception
  {
    boolean boResult = true;
    // Annullando sMSG_CONFIRM_ON_CANCEL si puo' saltare la richiesta di conferma.
    if(sMSG_CONFIRM_ON_CANCEL != null && sMSG_CONFIRM_ON_CANCEL.length() > 0) {
      boResult = GUIMessage.getConfirmation(sMSG_CONFIRM_ON_CANCEL);
    }
    if(boResult) {
      boolean boOldNew = boNew;
      boNew = false;
      boEditing = false;
      if(boOldNew) {
        setEditorStatus(iSTATUS_STARTUP);
      }
      else {
        setEditorStatus(iSTATUS_VIEW);
      }
      doCancel();
      for(int i = 0; i < oEntityEditorListeners.size(); i++) {
        ((IEntityEditorListener) oEntityEditorListeners.get(i)).onCancel(boOldNew);
      }
      if(boGlobalNotificationEnabled) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_CANCEL);
        StaticActionListeners.notifyActionEvent(this, e);
      }
    }
    return boResult;
  }
  
  public
  void fireDelete()
    throws Exception
  {
    boolean boConfirm = true;
    // Annullando sMSG_CONFIRM_ON_CANCEL si puo' saltare la richiesta di conferma.
    if(sMSG_CONFIRM_ON_DELETE != null && sMSG_CONFIRM_ON_DELETE.length() > 0) {
      boConfirm = GUIMessage.getConfirmation(sMSG_CONFIRM_ON_DELETE);
    }
    if(boConfirm) {
      try {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean boCheckBeforeDelete = checkBeforeDelete();
        if(boCheckBeforeDelete) {
          doDelete();
        }
        if(boCheckBeforeDelete) {
          for(int i = 0; i < oEntityEditorListeners.size(); i++) {
            ((IEntityEditorListener) oEntityEditorListeners.get(i)).onDelete();
          }
          if(boGlobalNotificationEnabled) {
            ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_DELETE);
            StaticActionListeners.notifyActionEvent(this, e);
          }
        }
      }
      finally {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    }
  }
  
  public
  void firePrint()
    throws Exception
  {
    if(btnPrint != null)  btnPrint.setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          doPrint();
          if(boGlobalNotificationEnabled) {
            ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_PRINT);
            StaticActionListeners.notifyActionEvent(this, e);
          }
        }
        catch(Exception ex) {
          GUIMessage.showException("Errore durante la generazione del report", ex);
        }
        finally {
          if(btnPrint != null)  btnPrint.setEnabled(true);
          setCursor(Cursor.getDefaultCursor());
        }
      }
    });
  }
  
  public
  void fireSelect()
    throws Exception
  {
    onChoiceMade();
    for(int i = 0; i < oEntityEditorListeners.size(); i++) {
      ((IEntityEditorListener) oEntityEditorListeners.get(i)).onChoice();
    }
    if(boGlobalNotificationEnabled) {
      ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_SELECT);
      StaticActionListeners.notifyActionEvent(this, e);
    }
    // Esce solo quando l'entity editor e' utilizzato come dialog di look up.
    if(btnSelect != null && btnSelect.isEnabled()) {
      fireExit();
    }
    else {
      if(boExportResultOnFireSelect) {
        exportDataResult();
      }
    }
  }
  
  public
  void fireExit()
    throws Exception
  {
    if(oEntityMgr != null) {
      oEntityMgr.doExit();
    }
    else {
      ResourcesMgr.getWorkPanel().close(this);
    }
    for(int i = 0; i < oEntityEditorListeners.size(); i++) {
      ((IEntityEditorListener) oEntityEditorListeners.get(i)).onExit();
    }
    if(boGlobalNotificationEnabled) {
      ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sACTION_EXIT);
      StaticActionListeners.notifyActionEvent(this, e);
    }
  }
  
  public
  boolean onClosing()
  {
    boolean boResult = true;
    if(boEditing) {
      try {
        boResult = fireCancel();
      }
      catch(Exception ex) {
        GUIMessage.showException(ex);
        return false;
      }
    }
    return boResult;
  }
  
  protected
  void setEditorStatus(int iStatus)
  {
    iEditorStatus = iStatus;
    switch(iStatus) {
      case iSTATUS_STARTUP:
      btnFind.setEnabled(true);
      btnReset.setEnabled(true);
      if(boAllowEditing) {
        GUIUtil.setGUIData(btnToggle, IConstants.sGUIDATA_DISABLE);
        btnToggle.setEnabled(false);
        btnNew.setEnabled(true);
        btnOpen.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);
        btnPrint.setEnabled(true);
      }
      if(btnSelect != null) btnSelect.setEnabled(false);
      btnClose.setEnabled(true);
      
      if(cntFilter != null)      cntFilter.setEnabled(true);
      if(cntResult != null)      cntResult.setEnabled(true);
      if(cntDetail != null)      cntDetail.setEnabled(false);
      if(cntBigDetail != null)   cntBigDetail.setEnabled(false);
      if(cntOtherDetail != null) cntOtherDetail.setEnabled(false);
      
      break;
      case iSTATUS_VIEW:
      btnFind.setEnabled(true);
      btnReset.setEnabled(true);
      if(boAllowEditing) {
        if(isElementEnabled()) {
          GUIUtil.setGUIData(btnToggle, IConstants.sGUIDATA_DISABLE);
        }
        else {
          GUIUtil.setGUIData(btnToggle, IConstants.sGUIDATA_ENABLE);
        }
        btnToggle.setEnabled(true);
        btnNew.setEnabled(true);
        btnOpen.setEnabled(true);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(true);
        btnPrint.setEnabled(true);
      }
      if(btnSelect != null) btnSelect.setEnabled(true);
      btnClose.setEnabled(true);
      
      if(cntFilter != null)      cntFilter.setEnabled(true);
      if(cntResult != null)      cntResult.setEnabled(true);
      if(cntDetail != null)      cntDetail.setEnabled(false);
      if(cntBigDetail != null)   cntBigDetail.setEnabled(false);
      if(cntOtherDetail != null) cntOtherDetail.setEnabled(false);
      
      break;
      case iSTATUS_EDITING:
      btnFind.setEnabled(false);
      btnReset.setEnabled(false);
      if(boAllowEditing) {
        if(isElementEnabled()) {
          GUIUtil.setGUIData(btnToggle, IConstants.sGUIDATA_DISABLE);
        }
        else {
          GUIUtil.setGUIData(btnToggle, IConstants.sGUIDATA_ENABLE);
        }
        btnToggle.setEnabled(false);
        btnNew.setEnabled(false);
        btnOpen.setEnabled(false);
        btnSave.setEnabled(true);
        btnCancel.setEnabled(true);
        btnDelete.setEnabled(false);
        btnPrint.setEnabled(false);
      }
      if(btnSelect != null) btnSelect.setEnabled(false);
      btnClose.setEnabled(true);
      
      if(cntFilter != null)      cntFilter.setEnabled(false);
      if(cntResult != null)      cntResult.setEnabled(false);
      if(cntDetail != null)      cntDetail.setEnabled(true);
      if(cntBigDetail != null)   cntBigDetail.setEnabled(true);
      if(cntOtherDetail != null) cntOtherDetail.setEnabled(true);
      
      break;
    }
    onChangeEditorStatus(iStatus);
  }
  
  protected
  void onLostSelection()
  {
    if(btnToggle != null) btnToggle.setEnabled(false);
    if(btnOpen   != null) btnOpen.setEnabled(false);
    if(btnDelete != null) btnDelete.setEnabled(false);
    if(btnSelect != null) btnSelect.setEnabled(false);
    for(int i = 0; i < oEntityEditorListeners.size(); i++) {
      ((IEntityEditorListener) oEntityEditorListeners.get(i)).onClearSelection();
    }
  }
  
  protected
  void exportDataResult()
  {
    if(cntResult == null) return;
    JTable jTable = TableUtils.getJTableFromContainer(cntResult);
    if(jTable == null) return;
    JFileChooser oFileChooser = new JFileChooser();
    File oDefSelectedFile = new File("result.csv");
    oFileChooser.setSelectedFile(oDefSelectedFile);
    int iResult = oFileChooser.showSaveDialog(ResourcesMgr.mainFrame);
    if(iResult != JFileChooser.APPROVE_OPTION) return;
    String sFilePath = oFileChooser.getSelectedFile().getAbsolutePath();
    try {
      TableUtils.exportTableToFile(jTable, sFilePath);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'esportazione della tabella", ex);
    }
    GUIMessage.showInformation("Esportazione conclusa con successo.");
  }
  
  private
  Container buildGUIActions()
  {
    List<JButton> listActions = new ArrayList<JButton>();
    
    if(boAllowEditing) {
      btnToggle = GUIUtil.buildActionButton(IConstants.sGUIDATA_DISABLE, sACTION_TOGGLE);
      btnToggle.addActionListener(this);
      btnNew = GUIUtil.buildActionButton(IConstants.sGUIDATA_NEW, sACTION_NEW);
      btnNew.addActionListener(this);
      btnOpen = GUIUtil.buildActionButton(IConstants.sGUIDATA_OPEN, sACTION_OPEN);
      btnOpen.addActionListener(this);
      btnSave = GUIUtil.buildActionButton(IConstants.sGUIDATA_SAVE, sACTION_SAVE);
      btnSave.addActionListener(this);
      btnCancel = GUIUtil.buildActionButton(IConstants.sGUIDATA_CANCEL, sACTION_CANCEL);
      btnCancel.addActionListener(this);
      btnDelete = GUIUtil.buildActionButton(IConstants.sGUIDATA_DELETE, sACTION_DELETE);
      btnDelete.addActionListener(this);
      btnPrint = GUIUtil.buildActionButton(IConstants.sGUIDATA_PRINT, sACTION_PRINT);
      btnPrint.addActionListener(this);
      btnClose = GUIUtil.buildActionButton(IConstants.sGUIDATA_EXIT, sACTION_EXIT);
      btnClose.addActionListener(this);
      
      listActions.add(btnToggle);
      listActions.add(btnNew);
      listActions.add(btnOpen);
      listActions.add(btnSave);
      listActions.add(btnCancel);
      listActions.add(btnDelete);
      listActions.add(btnPrint);
      listActions.add(btnClose);
    }
    else {
      btnSelect = GUIUtil.buildActionButton(IConstants.sGUIDATA_SELECT, sACTION_SELECT);
      btnSelect.addActionListener(this);
      btnClose = GUIUtil.buildActionButton(IConstants.sGUIDATA_EXIT, sACTION_EXIT);
      btnClose.addActionListener(this);
      
      listActions.add(btnSelect);
      listActions.add(btnClose);
    }
    
    checkActions(listActions, boAllowEditing);
    
    int iSize = listActions.size();
    int iRows = (iSize < iMaxRowsActions) ? iSize : iMaxRowsActions;
    int iCols = iSize / iMaxRowsActions +((iSize % iMaxRowsActions) > 0 ? 1 : 0);
    JPanel oButtonsPanel = new JPanel(new GridLayout(iRows, iCols, 1, 1));
    for(int r = 0; r < iRows; r++) {
      for(int c = 0; c < iCols; c++) {
         int i = c * iRows + r;
        Component oComponent = null;
        if(i < iSize) {
          oComponent = (Component) listActions.get(i);
        }
        if(oComponent == null) oComponent = new JPanel();
        oButtonsPanel.add(oComponent);
      }
    }
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEtchedBorder());
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    return oActionsPanel;
  }
  
  private
  Container buildGUIFilterActions()
  {
    List<JButton> listFilterActions = new ArrayList<JButton>();
    
    btnFind = GUIUtil.buildActionButton(IConstants.sGUIDATA_FIND, sACTION_FIND);
    btnFind.addActionListener(this);
    
    btnReset = GUIUtil.buildActionButton(IConstants.sGUIDATA_RESET, sACTION_RESET);
    btnReset.addActionListener(this);
    
    listFilterActions.add(btnFind);
    listFilterActions.add(btnReset);
    
    checkFilterActions(listFilterActions);
    
    JPanel oButtonsPanel = new JPanel(new GridLayout(listFilterActions.size(),
      1, 1, 1));
    for(int i = 0; i < listFilterActions.size(); i++) {
      oButtonsPanel.add((Component) listFilterActions.get(i));
    }
    
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 8));
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    
    return oActionsPanel;
  }
  
  /**
   * Tale metodo si puo' ridefinire per modificare i pulsanti di Filtro.
   *
   * @param listDefFilterActions List
   */
  protected
  void checkFilterActions(List<JButton> listDefFilterActions)
  {
  }
  
  /**
   * Tale metodo si puo' ridefinire per modificare i pulsanti di Azione.
   *
   * @param listDefActions List
   * @param boAllowEditing boolean
   */
  protected
  void checkActions(List<JButton> listDefActions, boolean boAllowEditing)
  {
  }
  
  /**
   * Rimuove un oggetto AbstractButton dalla lista basandosi
   * sull'ActionCommand.
   *
   * @param listButton List
   * @param sActionCommand String
   * @return Oggetto JButton rimosso
   */
  protected
  JButton removeButtonByActionCommand(List listButton, String sActionCommand)
  {
    if(listButton == null) return null;
    int iToRemove = -1;
    for(int i = 0; i < listButton.size(); i++) {
      Object oButton = listButton.get(i);
      if(oButton instanceof JButton) {
        String sAC = ((JButton) oButton).getActionCommand();
        if(sAC != null && sAC.equals(sActionCommand)) {
          iToRemove = i;
        }
      }
    }
    if(iToRemove >= 0) {
      return (JButton) listButton.remove(iToRemove);
    }
    return null;
  }
  
  /**
   * Recupera un pulsante dalla lista fornita basandosi con l'ActionCommand.
   *
   * @param listButton List
   * @param sActionCommand String
   * @return JButton
   */
  protected
  JButton getButtonByActionCommand(List listButton, String sActionCommand)
  {
    if(listButton == null) return null;
    for(int i = 0; i < listButton.size(); i++) {
      Object oButton = listButton.get(i);
      if(oButton instanceof JButton) {
        String sAC = ((JButton) oButton).getActionCommand();
        if(sAC != null && sAC.equals(sActionCommand)) {
          return (JButton) oButton;
        }
      }
    }
    return null;
  }
  
  /**
   * Verifica prima della ricerca.
   *
   * @throws Exception
   * @return boolean = true se si puo' proseguire con la ricerca, false altrimenti.
   */
  protected
  boolean checkBeforeFind()
    throws Exception
  {
    return true;
  }
  
  /**
   * Verifica prima della cancellazione di un record.
   *
   * @throws Exception
   * @return boolean = true se l'elemento e' cancellabile, false altrimenti.
   */
  protected
  boolean checkBeforeDelete()
    throws Exception
  {
    return true;
  }
  
  /**
   * Verifica prima della modifica di un record.
   *
   * @throws Exception
   * @return boolean = true se l'elemento e' modificabile, false altrimenti.
   */
  protected
  boolean checkBeforeOpen()
    throws Exception
  {
    return true;
  }
  
  /**
   * Verifica prima della creazione di un nuovo record.
   *
   * @throws Exception
   * @return boolean = true se l'elemento e' modificabile, false altrimenti.
   */
  protected
  boolean checkBeforeNew()
    throws Exception
  {
    return true;
  }
  
  /**
   * Restituisce l'elemento correntemente selezionato.
   *
   * @throws Exception
   * @return Object
   */
  public abstract
  Object getCurrentSelection() throws Exception;
  
  /**
   * Costruisce il container del filtro.
   *
   * @return Container
   */
  protected abstract
  Container buildGUIFilter();
  
  /**
   * Costruisce il container del risultato.
   *
   * @return Container
   */
  protected abstract
  Container buildGUIResult();
  
  /**
   * Costruisce il container del dettaglio.
   *
   * @return Container
   */
  protected abstract
  Container buildGUIDetail();
  
  /**
   * Costruisce il container del dettaglio alternativo.
   * Se si restituisce null non viene attivato il tab relativo al dettaglio
   * alternativo.
   * Se si restituisce un oggetto FormPanel si puo' impostare la label del tab
   * attraverso l'attributo tag dell'oggetto FormPanel stesso.
   * In caso contrario si puo' impostare la label tramite un oggetto
   * Container che implementa l'interfaccia org.dew.swingup.util.ITaggable.
   *
   * @return Container
   */
  protected abstract
  Container buildGUIBigDetail();
  
  /**
   * Costruisce l'altro container del dettaglio alternativo.
   * Se si restituisce null non viene attivato il tab relativo all'altro
   * dettaglio alternativo.
   * Se si restituisce un oggetto FormPanel si puo' impostare la label del tab
   * attraverso l'attributo tag dell'oggetto FormPanel stesso.
   * In caso contrario si puo' impostare la label tramite un oggetto
   * Container che implementa l'interfaccia org.dew.swingup.util.ITaggable.
   *
   * @return Container
   */
  protected abstract
  Container buildGUIOtherDetail();
  
  /**
   * Metodo invocato ad ogni cambiamento di stato dopo l'invocazione del metodo
   * setEditorStatus. Occorre ridefinirlo quando si vuole impostare una diversa
   * politica di abilitazione dei pulsanti.
   *
   * @param iStatus Stato corrente dell'Entity Editor
   */
  protected abstract
  void onChangeEditorStatus(int iStatus);
  
  /**
   * Metodo invocato alla pressione del pulsante Cerca.
   *
   * @throws Exception
   */
  protected abstract
  void doFind() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Annulla del pannello filtro.
   *
   * @throws Exception
   */
  protected abstract
  void doReset() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Nuovo.
   *
   * @throws Exception
   */
  protected abstract
  void doNew() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Modifica.
   *
   * @throws Exception
   */
  protected abstract
  void doOpen() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Salva.
   *
   * @param boNew true = Nuovo, false = Da aggiornare
   * @throws Exception
   * @return boolean true = ok, false = salvataggio non possibile (es. campi obbligatori non valorizzati)
   */
  protected abstract
  boolean doSave(boolean boNew) throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Abilita/Disabilita.
   *
   * @throws Exception
   */
  protected abstract
  void doToggle() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Annulla.
   *
   * @throws Exception
   */
  protected abstract
  void doCancel() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Elimina.
   *
   * @throws Exception
   */
  protected abstract
  void doDelete() throws Exception;
  
  /**
   * Metodo invocato alla pressione del pulsante Stampa.
   *
   * @throws Exception
   */
  protected abstract
  void doPrint() throws Exception;
  
  /**
   * Metodo invocato alla selezione di un record.
   *
   * @throws Exception
   * @return boolean true = elemento selezionato, false = elemento non selezionato
   */
  protected abstract
  boolean onSelection() throws Exception;
  
  /**
   * Imposta i valori del filtro.
   *
   * @param oValues Valori
   * @throws Exception
   */
  protected abstract
  void setFilterValues(Object oValues) throws Exception;
  
  /**
   * Metodo invocato alla scelta di un record.
   *
   * @throws Exception
   */
  protected abstract
  void onChoiceMade() throws Exception;
  
  /**
   * Metodo invocato quando si vuole ottenere lo stato di un elemento selezionato.
   *
   * @return Flag di abilitazione dell'elemento selezionato.
   */
  protected abstract
  boolean isElementEnabled();
}
