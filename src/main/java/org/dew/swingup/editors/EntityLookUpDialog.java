package org.dew.swingup.editors;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.components.*;

/**
 * Classe che estende ALookUpDialog per l'utilizzo di un AEntityEditor
 * come GUI per il dialogo di ricerca nei componenti decodificabili.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class EntityLookUpDialog extends ALookUpDialog implements IEntityEditorListener
{
  protected JPanel oMainPanel;
  protected AEntityEditor oEntityEditor;
  protected List oKeys;
  protected List oSelectedRecord;
  protected Object oLastChoice;
  protected boolean boAllowEditing;
  
  /**
   * Costruttore.
   *
   * @param theEntityEditor AEntityEditor
   * @param theKeys Lista di simbolici [PK, campo_filtro_1, campo_filtro_2, ...]
   */
  public
  EntityLookUpDialog(AEntityEditor theEntityEditor, List theKeys)
  {
    super("");
    this.oEntityEditor = theEntityEditor;
    this.boAllowEditing = false;
    if(oEntityEditor == null) {
      GUIMessage.showError("Parametro theEntityEditor non definito nel costruttore EntityLookUpDialog.");
      return;
    }
    this.oKeys = theKeys;
    if(oKeys == null) {
      GUIMessage.showError("Lista di simbolici non definita nel costruttore EntityLookUpDialog.");
      return;
    }
    try {
      initEntityEditor();
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
  
  /**
   * Costruttore.
   *
   * @param theEntityEditor AEntityEditor
   * @param theKeys Lista di simbolici [PK, campo_filtro_1, campo_filtro_2, ...]
   * @param boAllowEditing Flag per abilitare le funzioni di editing (nuovo, modifica, ecc.)
   */
  public
  EntityLookUpDialog(AEntityEditor theEntityEditor, List theKeys, boolean boAllowEditing)
  {
    super("");
    this.oEntityEditor = theEntityEditor;
    this.boAllowEditing = boAllowEditing;
    if(oEntityEditor == null) {
      GUIMessage.showError("Parametro theEntityEditor non definito nel costruttore EntityLookUpDialog.");
      return;
    }
    this.oKeys = theKeys;
    if(oKeys == null) {
      GUIMessage.showError("Lista di simbolici non definita nel costruttore EntityLookUpDialog.");
      return;
    }
    try {
      initEntityEditor();
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
  
  public
  void setRecords(List oRecords)
  {
  }
  
  public
  void setFilter(List oFilter)
  {
    if(oFilter == null) return;
    if(oKeys   == null) return;
    int iSizeKeys = oKeys.size();
    
    boolean boAtLeastOne = false;
    Map mapFilter = new HashMap();
    for(int i = 0; i < oFilter.size(); i++) {
      if(i >= iSizeKeys - 1) break;
      Object oKey = oKeys.get(i + 1);
      Object oValue = oFilter.get(i);
      if(!isBlank(oValue)) {
        boAtLeastOne = true;
      }
      mapFilter.put(oKey, oValue);
    }
    try {
      oEntityEditor.fireReset();
      oEntityEditor.setFilterValues(mapFilter);
      if(boAtLeastOne) {
        oEntityEditor.fireFind();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
  
  protected
  boolean isBlank(Object oValue)
  {
    if(oValue == null) {
      return true;
    }
    else
    if(oValue instanceof String) {
      if(((String) oValue).trim().length() == 0) {
        return true;
      }
    }
    return false;
  }
  
  public
  List getSelectedRecord()
  {
    return oSelectedRecord;
  }
  
  public
  void onClearSelection()
  {
    oSelectedRecord = null;
  }
  
  public
  void onFind()
  {
    oSelectedRecord = null;
  }
  
  public
  void onSelection()
  {
    oSelectedRecord = null;
  }
  
  public
  void onChoice()
  {
    if(!oEntityEditor.onClosing()) return;
    oLastChoice = oEntityEditor.getChoice();
    if(oLastChoice instanceof Map) {
      oSelectedRecord = new ArrayList();
      for(int i = 0; i < oKeys.size(); i++) {
        Object oKey = oKeys.get(i);
        Object oValue = ((Map) oLastChoice).get(oKey);
        oSelectedRecord.add(oValue);
      }
    }
    dispose();
  }
  
  public
  void onExit()
  {
    if(!oEntityEditor.onClosing()) return;
    dispose();
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
    return oEntityEditor.onClosing();
  }
  
  /**
   * Metodo invocato quando si attiva la finestra di dialogo.
   */
  protected
  void onActivated()
  {
    oEntityEditor.onActivated();
  }
  
  /**
   * Metodo invocato quando si apre la finestra di dialogo.
   */
  protected
  void onOpened()
  {
    // Non si propaga l'evento onOpened poiche' esso entra in conflitto
    // con l'apertura tramite LookUpDialog (in particolare lancia il fireReset()
    // che annulla l'eventuale impostazione del filtro)
    // oEntityEditor.onOpened();
  }
  
  /**
   * Invocato DOPO la richiesta di inserire un elemento.
   */
  public
  void onNew()
  {
  }
  
  /**
   * Invocato DOPO la richiesta di modificare l'elemento.
   */
  public
  void onModify()
  {
  }
  
  /**
   * Invocato DOPO il salvataggio.
   * @param boNew true inserimento, false aggiornamento
   */
  public
  void onSave(boolean boNew)
  {
  }
  
  /**
   * Invocato DOPO la richiesta di annullamento delle modifiche.
   * @param boNew true inserimento, false aggiornamento
   */
  public
  void onCancel(boolean boNew)
  {
  }
  
  /**
   * Invocato DOPO l'eliminazione di un elemento.
   */
  public
  void onDelete()
  {
  }
  
  public
  Object getLastChoice()
  {
    return oLastChoice;
  }
  
  public
  AEntityEditor getEntityEditor()
  {
    return oEntityEditor;
  }
  
  protected
  void initEntityEditor()
    throws Exception
  {
    oEntityEditor.init(boAllowEditing);
    oEntityEditor.addEntityEditorListener(this);
    oMainPanel.add(oEntityEditor, BorderLayout.CENTER);
  }
  
  protected
  Container buildGUI(Container oFilterContainer)
    throws Exception
  {
    oMainPanel = new JPanel(new BorderLayout());
    return oMainPanel;
  }
}
