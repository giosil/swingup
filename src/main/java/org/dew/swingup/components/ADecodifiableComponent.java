package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;

/**
 * Classe astratta per l'implementazione di un campo decodificabile.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public abstract
class ADecodifiableComponent extends JPanel
{
  public final static int iCASE_NOTMODIFIED = 0;
  public final static int iCASE_UPPER = 1;
  public final static int iCASE_LOWER = 2;
  
  protected int iCase = 0;
  
  protected static final String sDEFAULT_ACTIONCOMMAND = "find";
  
  protected String sEntity;
  
  protected String sActionCommand;
  protected List listActionListener = new ArrayList();
  protected List listDecodeListener = new ArrayList();
  protected ILookUpFinder oLookUpFinder;
  protected ALookUpDialog oLookUpDialog;
  protected boolean boEnabled = true;
  protected boolean boEditable = true;
  
  /**
   * Restituisce la chiave del campo decodificato.
   *
   * @return Chiave.
   */
  public abstract
  Object getKey();
  
  /**
   * Imposta i valori del campo decodificabile.
   *
   * @param oValues valori
   */
  public abstract
  void setValues(List oValues);
  
  /**
   * Imposta i valori del campo decodificabile.
   *
   * @param oValues valori
   * @param boNotify Flag di notifica (vedi IDecodeListener.set)
   */
  public abstract
  void setValues(List oValues, boolean boNotify);
  
  /**
   * Imposta un singolo valore.
   *
   * @param iIndex indice
   * @param oValue valore
   */
  public abstract
  void setValue(int iIndex, Object oValue);
  
  /**
   * Restituisce i valori del campo decodificabile.
   * Se null non e' stato decodificato.
   *
   * @return Valori del campo decodificabile.
   */
  public abstract
  List getValues();
  
  /**
   * Pulisce tutti i campi.
   */
  public abstract
  void reset();
  
  /**
   * Pulisce tutti i campi.
   *
   * @param boNotify Flag di notifica (vedi IDecodeListener.reset)
   */
  public abstract
  void reset(boolean boNotify);
  
  /**
   * Restituisce i valori del filtro.
   *
   * @return List di String.
   */
  public abstract
  List getFilterValues();
  
  /**
   * Imposta il case del testo.
   * 0 = nessuna modifica
   * 1 = upper
   * 2 = lower
   *
   * @param iCase int
   */
  public
  void setCase(int iCase)
  {
    this.iCase = iCase;
  }
  
  /**
   * Ottiene il case del testo.
   *
   * @return int
   */
  public
  int getCase()
  {
    return iCase;
  }
  
  /**
   * Imposta l'oggetto ILookUpFinder.
   *
   * @param oLookUpFinder LookUpFinder
   */
  public
  void setLookUpFinder(ILookUpFinder oLookUpFinder)
  {
    this.oLookUpFinder = oLookUpFinder;
  }
  
  /**
   * Restituisce l'oggetto ILookUpFinder.
   *
   * @return ILookUpFinder
   */
  public
  ILookUpFinder getLookUpFinder()
  {
    return oLookUpFinder;
  }
  
  /**
   * Imposta l'oggetto ALookUpDialog.
   *
   * @param oLookUpDialog LookUpDialog
   */
  public
  void setLookUpDialog(ALookUpDialog oLookUpDialog)
  {
    this.oLookUpDialog = oLookUpDialog;
    this.oLookUpDialog.setDecodeListeners(listDecodeListener);
  }
  
  /**
   * Restituisce l'oggetto ALookUpDialog
   *
   * @return ALookUpDialog
   */
  public
  ALookUpDialog getLookUpDialog()
  {
    return oLookUpDialog;
  }
  
  /**
   * Aggiunge un oggetto ActionListener.
   *
   * @param oActionListener Ascoltatore
   */
  public
  void addActionListener(ActionListener oActionListener)
  {
    if(oActionListener != null) {
      listActionListener.add(oActionListener);
    }
  }
  
  /**
   * Aggiunge un oggetto IDecodeListener.
   *
   * @param oDecodeListener Ascoltatore
   */
  public
  void addDecodeListener(IDecodeListener oDecodeListener)
  {
    if(oDecodeListener != null) {
      listDecodeListener.add(oDecodeListener);
    }
  }
  
  /**
   * Rimuove un oggetto ActionListener.
   *
   * @param oActionListener Ascoltatore
   */
  public
  void removeActionListener(ActionListener oActionListener)
  {
    if(oActionListener != null) {
      listActionListener.remove(oActionListener);
    }
  }
  
  /**
   * Rimuove un oggetto IDecodeListener.
   *
   * @param oDecodeListener Ascoltatore
   */
  public
  void removeDecodeListener(IDecodeListener oDecodeListener)
  {
    if(oDecodeListener != null) {
      listDecodeListener.remove(oDecodeListener);
    }
  }
  
  /**
   * Imposta la stringa ActionCommand.
   *
   * @param sActionCommand Codice comando
   */
  public
  void setActionCommand(String sActionCommand)
  {
    this.sActionCommand = sActionCommand;
  }
  
  /**
   * Restituisce la stringa ActionCommand.
   *
   * @return ActionCommand Codice comando
   */
  public
  String getActionCommand()
  {
    return sActionCommand;
  }
  
  /**
   * Imposta l'identificativo dell'entita'.
   *
   * @param sEntity Identificativo dell'entita
   */
  public
  void setEntity(String sEntity)
  {
    this.sEntity = sEntity;
  }
  
  /**
   * Restituisce l'identificativo dell'entita'.
   *
   * @return Identificativo dell'entita
   */
  public
  String getEntity()
  {
    return sEntity;
  }
  
  /**
   * Imposta il flag abilitato.
   *
   * @param boEnabled Flag abilitato
   */
  public
  void setEnabled(boolean boEnabled)
  {
    this.boEnabled = boEnabled;
  }
  
  /**
   * Restituisce il flag abilitato.
   *
   * @return Flag abilitato
   */
  public
  boolean isEnabled()
  {
    return boEnabled;
  }
  
  /**
   * Imposta il flag editabile.
   *
   * @param boEditable Flag editabile
   */
  public
  void setEditable(boolean boEditable)
  {
    this.boEditable = boEditable;
  }
  
  /**
   * Restituisce il flag editabile.
   *
   * @return Flag editabile
   */
  public
  boolean isEditable()
  {
    return boEditable;
  }
  
  /**
   * Esegue l'operazione di decodifica senza il ricorso alla finestra
   * di dialogo.
   */
  public
  void decode()
  {
    if(oLookUpFinder == null)
    return;
    
    List oFilter = getFilterValues();
    
    notifyBeforeFind(oFilter);
    
    List listResult = null;
    try {
      setWaitCursor();
      if(checkFilter(oFilter)) {
        listResult = oLookUpFinder.find(sEntity, oFilter);
      }
      else {
        listResult = new Vector();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la ricerca", ex);
      return;
    }
    finally {
      setDefaultCursor();
    }
    
    if(listResult != null && listResult.size() == 1) {
      Object oRecord = listResult.get(0);
      if(oRecord instanceof List) {
        setValues((List) oRecord);
      }
      return;
    }
  }
  
  /**
   * Scatena una ricerca.
   *
   * @param boShowDialogAnyway Flag per mostrare il dialogo
   */
  public
  void doFind(boolean boShowDialogAnyway)
  {
    if(listActionListener.size() == 0) {
      doDefaultFind(boShowDialogAnyway);
      requestFocus();
      return;
    }
    
    int iModifiers = ActionEvent.ACTION_FIRST;
    if(boShowDialogAnyway) {
      iModifiers = ActionEvent.ACTION_LAST;
    }
    
    ActionEvent e = null;
    if(sActionCommand == null) {
      e = new ActionEvent(this, this.hashCode(), sDEFAULT_ACTIONCOMMAND, iModifiers);
    }
    else {
      e = new ActionEvent(this, this.hashCode(), sActionCommand, iModifiers);
    }
    
    for(int i = 0; i < listActionListener.size(); i++) {
      ActionListener oActionListener = (ActionListener) listActionListener.get(i);
      oActionListener.actionPerformed(e);
    }
    
    requestFocus();
  }
  
  /**
   * Ricerca di default.
   *
   * @param boShowDialogAnyway Flag per mostrare il dialogo
   */
  public
  void doDefaultFind(boolean boShowDialogAnyway)
  {
    if(oLookUpFinder == null || oLookUpDialog == null)
    return;
    
    oLookUpDialog.setEntity(sEntity);
    oLookUpDialog.setLookUpFinder(oLookUpFinder);
    
    List oFilter = getFilterValues();
    
    notifyBeforeFind(oFilter);
    
    List listResult = null;
    try {
      setWaitCursor();
      if(checkFilter(oFilter)) {
        listResult = oLookUpFinder.find(sEntity, oFilter);
      }
      else {
        listResult = new Vector();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la ricerca", ex);
      return;
    }
    finally {
      setDefaultCursor();
    }
    
    if(!boShowDialogAnyway) {
      if(listResult != null && listResult.size() == 1) {
        Object oRecord = listResult.get(0);
        if(oRecord instanceof List) {
          setValues((List) oRecord);
        }
        return;
      }
    }
    
    oLookUpDialog.setFilter(oFilter);
    oLookUpDialog.setRecords(listResult);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oLookUpDialog.setLocation(screenSize.width/2 - oLookUpDialog.getSize().width/2,
      screenSize.height/2 - oLookUpDialog.getSize().height/2);
    oLookUpDialog.setVisible(true);
    
    List oRecord = (List) oLookUpDialog.getSelectedRecord();
    if(oRecord == null || oRecord.size() == 0) {
      return;
    }
    
    setValues(oRecord);
  }
  
  public
  List invokeFind(List oFilter)
    throws Exception
  {
    return invokeFind(oFilter, true);
  }
  
  public
  List invokeFind(List oFilter, boolean boNotify)
    throws Exception
  {
    if(boNotify) {
      notifyBeforeFind(oFilter);
    }
    
    if(oLookUpFinder == null) {
      return new ArrayList();
    }
    
    List oResult = null;
    try{
      setWaitCursor();
      oResult = oLookUpFinder.find(sEntity, oFilter);
    }
    finally{
      setDefaultCursor();
    }
    
    return oResult;
  }
  
  protected
  void setWaitCursor()
  {
    getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }
  
  protected
  void setDefaultCursor()
  {
    getRootPane().setCursor(Cursor.getDefaultCursor());
    this.setCursor(Cursor.getDefaultCursor());
  }
  
  protected
  boolean checkFilter(List oFilter)
  {
    for(int i = 0; i < oFilter.size(); i++) {
      Object oValue = oFilter.get(i);
      if(oValue == null) continue;
      String sText = oValue.toString().trim();
      if(sText.length() > 0) {
        return true;
      }
    }
    
    return false;
  }
  
  protected
  void notifySet()
  {
    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      oDecodeListener.set();
    }
  }
  
  protected
  void notifyReset()
  {
    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      oDecodeListener.reset();
    }
  }
  
  protected
  void notifyBeforeFind(List oFilter)
  {
    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      oDecodeListener.beforeFind(oFilter);
    }
  }
  
  public void finalize()
  {
    if(oLookUpDialog != null) {
      try{ oLookUpDialog.dispose(); } catch(Throwable th) {}
    }
  }
}
