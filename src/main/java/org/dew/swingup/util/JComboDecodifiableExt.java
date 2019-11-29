package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.components.*;

/**
 * Implementazione di ADecodifiableComponent presa da JComboDecodifiable ed
 * estesa per gestire chiave e codice mnemonico non in visualizzazione.
 *
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class JComboDecodifiableExt extends ADecodifiableComponent
{
  private static final long serialVersionUID = -1738978116755669777L;

  protected static final String sTOOLTIP_TEXT = "(Invio decodifica; Alt-Canc cancella)";

  protected JComboBox jcbDescription;

  protected List oValues;
  protected List oLastValuesSetted;
  protected List listLastFindResult = null;

  protected Cursor oDefaultCursor = Cursor.getDefaultCursor();

  protected boolean boEnterToFind = false;
  
  protected Map mapDescId   = new HashMap();
  protected Map mapDescCode = new HashMap();
  
  protected boolean boJustSelected = false;

  public
  JComboDecodifiableExt()
  {
    this(null);
  }

  public
  JComboDecodifiableExt(String sEntity)
  {
    this.sEntity = sEntity;
    try {
      init();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    setEnterToFind(true);
  }

  public
  void setEnterToFind(boolean boValue)
  {
    this.boEnterToFind = boValue;
  }

  public
  boolean isEnterToFind()
  {
    return boEnterToFind;
  }

  public
  void addFocusListener(FocusListener fl)
  {
    super.addFocusListener(fl);
    if(jcbDescription != null) {
      ComboBoxEditor cbEditor = jcbDescription.getEditor();
      if(cbEditor != null) {
        Component cmpEditorComponent = cbEditor.getEditorComponent();
          if(cmpEditorComponent != null) {
            cmpEditorComponent.addFocusListener(fl);
          }
      }
    }
  }

  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    if(jcbDescription != null) {
      ComboBoxEditor cbEditor = jcbDescription.getEditor();
      if(cbEditor != null) {
        Component cmpEditorComponent = cbEditor.getEditorComponent();
          if(cmpEditorComponent != null) {
            cmpEditorComponent.removeFocusListener(fl);
          }
      }
    }
  }

  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    if(jcbDescription != null) {
      ComboBoxEditor cbEditor = jcbDescription.getEditor();
      if(cbEditor != null) {
        Component cmpEditorComponent = cbEditor.getEditorComponent();
          if(cmpEditorComponent != null) {
            cmpEditorComponent.addKeyListener(kl);
          }
      }
    }    
  }

  public
  void transferFocus()
  {
    if(jcbDescription != null) {
      ComboBoxEditor cbEditor = jcbDescription.getEditor();
      if(cbEditor != null) {
        Component cmpEditorComponent = cbEditor.getEditorComponent();
          if(cmpEditorComponent != null) {
            cmpEditorComponent.transferFocus();
          }
          else {
            jcbDescription.transferFocus();
          }
      }
      else {
        jcbDescription.transferFocus();
      }
    }     
  }

  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    if(jcbDescription != null) {
      ComboBoxEditor cbEditor = jcbDescription.getEditor();
      if(cbEditor != null) {
        Component cmpEditorComponent = cbEditor.getEditorComponent();
          if(cmpEditorComponent != null) {
            cmpEditorComponent.removeKeyListener(kl);
          }
      }
    } 
  }

  public
  void requestFocus()
  {
    if(jcbDescription != null) {
      jcbDescription.requestFocus();
    }
  }
  
  public
  void setToolTipText(String sText)
  {
    if(jcbDescription != null) {
      jcbDescription.setToolTipText(sText);
    }
  }
  
  public
  void setCursor(Cursor cursor)
  {
    super.setCursor(cursor);

    Cursor oCursor = cursor;
    if(cursor.equals(Cursor.getDefaultCursor())) {
      oCursor = oDefaultCursor;
    }
    if(jcbDescription != null) {
      jcbDescription.setCursor(oCursor);
      ComboBoxEditor cbEditor = jcbDescription.getEditor();
      if(cbEditor != null) {
        Component cmpEditorComponent = cbEditor.getEditorComponent();
        if(cmpEditorComponent != null) {
          cmpEditorComponent.setCursor(oCursor);
        }
      }
    }
  }

  public
  void setBackground(Color color)
  {
    if(jcbDescription == null) {
      super.setBackground(color);
      return;
    }
    ComboBoxEditor cbEditor = jcbDescription.getEditor();
    if(cbEditor != null) {
      Component cmpEditorComponent = cbEditor.getEditorComponent();
      if(cmpEditorComponent != null) {
        cmpEditorComponent.setBackground(color);
      }
    }
  }

  public
  Color getBackground()
  {
    if(jcbDescription == null) {
      return super.getBackground();
    }
    ComboBoxEditor cbEditor = jcbDescription.getEditor();
    if(cbEditor != null) {
      Component cmpEditorComponent = cbEditor.getEditorComponent();
      if(cmpEditorComponent != null) {
        return cmpEditorComponent.getBackground();
      }
    }
    return Color.white;
  }

  public
  void setEnabled(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
    jcbDescription.setEnabled(boEnabled);
  }

  public
  void setEditable(boolean boEditable)
  {
    super.setEditable(boEditable);
    jcbDescription.setEditable(boEditable);
  }

  public
  void setName(String sName)
  {
    super.setName(sName);
    jcbDescription.setName(sName + ".0");
  }

  /**
   * Si ottiene il testo riportato nella combo editabile.
   *
   * @return Object
   */
  public
  Object getKey()
  {
    String sDescription = getDescription();

    // Il testo potrebbe subire una trasformazione (es. uppercase)...
    oValues.set(2, sDescription);
    jcbDescription.getEditor().setItem(sDescription);
    Component compEditor = jcbDescription.getEditor().getEditorComponent();
    if(compEditor instanceof JTextField) {
      ((JTextField) compEditor).setCaretPosition(0);
    }

    if(sDescription == null) return null;
    String sDescNorm = sDescription.trim().toUpperCase();
    return mapDescId.get(sDescNorm);
  }

  public
  String getDescription()
  {
    String sResult = null;

    ComboBoxEditor oComboBoxEditor = jcbDescription.getEditor();
    Object oEditedItem = oComboBoxEditor.getItem();
    if(oEditedItem == null) return null;

    sResult = oEditedItem.toString().trim();
    if(sResult.length() == 0) return null;
    switch(iCase) {
      case iCASE_UPPER:
        sResult = sResult.toUpperCase();
        break;
      case iCASE_LOWER:
        sResult = sResult.toLowerCase();
        break;
    }

    return sResult;
  }

  /**
   * E' simile a getDescription() a meno del trim.
   * E' usato per il controllo della digitazione.
   *
   * @return String
   */
  protected
  String getTextCombo()
  {
    String sResult = null;

    ComboBoxEditor oComboBoxEditor= jcbDescription.getEditor();
    Object oEditedItem = oComboBoxEditor.getItem();
    if(oEditedItem == null) return null;

    sResult = oEditedItem.toString();
    if(sResult.length() == 0) return null;
    switch(iCase) {
      case iCASE_UPPER:
        sResult = sResult.toUpperCase();
        break;
      case iCASE_LOWER:
        sResult = sResult.toLowerCase();
        break;
    }

    return sResult;
  }

  public
  List getFilterValues()
  {
  String sDescription = getDescription();
  char c0 = sDescription != null && sDescription.length() > 0 ? sDescription.charAt(0) : '\0';
    List listResult = new ArrayList(2);
    if(Character.isLetter(c0)) {
      listResult.add("");
      listResult.add(sDescription);
    }
    else {
      listResult.add(sDescription);
      listResult.add("");
    }
    return listResult;
  }

  public
  void reset()
  {
    clear();

    notifyReset();
  }

  public
  void reset(boolean boNotify)
  {
    clear();

    if(boNotify) {
      notifyReset();
    }
  }

  public
  void setValues(List listValues)
  {
    if(listValues == null) {
      reset();
      oLastValuesSetted = null;
      return;
    }

    if(listValues.size() > 2) {
        clear();

      Object oId   = listValues.get(0);
      Object oCode = listValues.get(1);
      Object oDesc = listValues.get(2);
      String sDesc     = oDesc != null ? oDesc.toString() : "";
      String sDescNorm = sDesc.trim().toUpperCase();
      mapDescId.put(sDescNorm, oId);
      mapDescCode.put(sDescNorm, oCode);
    }
    for(int i = 0; i < listValues.size(); i++) {
      setValue(i, listValues.get(i));
    }

    oLastValuesSetted = listValues;

    notifySet();
  }

  public
  void setValues(List listValues, boolean boNotify)
  {
    if(listValues == null) {
      reset();
      oLastValuesSetted = null;
      return;
    }

    clear();

    if(listValues.size() > 2) {
      Object oId   = listValues.get(0);
      Object oCode = listValues.get(1);
      Object oDesc = listValues.get(2);
      String sDesc     = oDesc != null ? oDesc.toString() : "";
      String sDescNorm = sDesc.trim().toUpperCase();
      mapDescId.put(sDescNorm, oId);
      mapDescCode.put(sDescNorm, oCode);
    }
    for(int i = 0; i < listValues.size(); i++) {
      setValue(i, listValues.get(i));
    }

    oLastValuesSetted = listValues;

    if(boNotify) {
      notifySet();
    }
  }

  public
  List getValues()
  {
    String sDescription = getDescription();
    if(sDescription == null) {
      oValues.set(0, null);
      oValues.set(1, null);
      oValues.set(2, null);
      return oValues;
    }

    // Il testo potrebbe subire una trasformazione (es. uppercase)...
    String sDescNorm = sDescription.trim().toUpperCase();
    oValues.set(0, mapDescId.get(sDescNorm));
    oValues.set(1, mapDescCode.get(sDescNorm));
    oValues.set(2, sDescription);
    jcbDescription.getEditor().setItem(sDescription);
    Component compEditor = jcbDescription.getEditor().getEditorComponent();
    if(compEditor instanceof JTextField) {
      ((JTextField) compEditor).setCaretPosition(0);
    }

    return oValues;
  }

  public
  void setValue(int iIndex, Object oValue)
  {
  if(iIndex == 0) {
    oValues.set(0, oValue);
  }
  else
  if(iIndex == 1) {
    oValues.set(1, oValue);
  }
  else
    if(iIndex == 2) {
      oValues.set(2, oValue);
      if(oValue != null) {
        switch(iCase) {
          case iCASE_UPPER:
            jcbDescription.setSelectedItem(oValue.toString().toUpperCase());
            break;
          case iCASE_LOWER:
            jcbDescription.setSelectedItem(oValue.toString().toLowerCase());
            break;
          default:
            jcbDescription.setSelectedItem(oValue.toString());
        }
        Component compEditor = jcbDescription.getEditor().getEditorComponent();
        if(compEditor instanceof JTextField) {
          ((JTextField) compEditor).setCaretPosition(0);
        }
      }
      else {
        jcbDescription.setSelectedItem("");
      }
    }
  }

  protected
  void clear()
  {
    if(oValues != null && oValues.size() > 2) {
      oValues.set(0, null);
      oValues.set(1, null);
      oValues.set(2, null);
    }
    jcbDescription.removeAllItems();
    jcbDescription.setSelectedItem("");
    if(mapDescId   != null) mapDescId.clear();
    if(mapDescCode != null) mapDescCode.clear();
  }

  protected
  void doFindByDescription()
  {
    if(oLookUpFinder == null) {
      GUIMessage.showWarning("LookUpFinder non impostato");
      return;
    }

    String sDescription = getDescription();
    char cLast = sDescription != null && sDescription.length() > 0 ? sDescription.charAt(sDescription.length() - 1) : '\0';

    List listFilter = new ArrayList(2);
    if(Character.isDigit(cLast)) {
        listFilter.add(sDescription);
        listFilter.add("");
    }
    else {
        listFilter.add("");
        listFilter.add(sDescription);
    }

    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      oDecodeListener.beforeFind(listFilter);
    }

    List listResult = null;
    try {
      setWaitCursor();
      if(checkFilter(listFilter)) {
        listResult = oLookUpFinder.find(sEntity, listFilter);
      }
      else {
        listResult = new Vector();
      }
    }
    catch (Exception ex) {
      GUIMessage.showException("Errore durante la ricerca", ex);
      return;
    }
    finally {
      setDefaultCursor();
    }
    
    listLastFindResult = listResult;
    jcbDescription.removeAllItems();
    oValues.set(0, null);
    oValues.set(1, null);
    oValues.set(2, null);

    if(listResult == null || listResult.size() == 0) {
      jcbDescription.getEditor().setItem(sDescription);
      return;
    }
    
    // Mantenere il clear delle mappe in questa posizione
    mapDescId.clear();
    mapDescCode.clear();
    
    if(listResult.size() == 1) {
      Object oRecord = listResult.get(0);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 2) {
          Object oId          = ((List) oRecord).get(0);
          Object oCode        = ((List) oRecord).get(1);
          Object oDescription = ((List) oRecord).get(2);
          if(oDescription != null) {
            String sDesc     = oDescription.toString();
            String sDescNorm = sDesc.trim().toUpperCase();
            oValues.set(0, oId);
            oValues.set(1, oCode);
            oValues.set(2, sDesc);
            jcbDescription.addItem(sDesc);
            mapDescId.put(sDescNorm, oId);
            mapDescCode.put(sDescNorm, oCode);
          }
          else {
            oValues.set(0, null);
            oValues.set(1, null);
            oValues.set(2, null);
            jcbDescription.addItem("");
          }
        }

        notifySet();
      }
      return;
    }

    for(int i = 0; i < listResult.size(); i++) {
      Object oRecord = listResult.get(i);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 0) {
          Object oId          = ((List) oRecord).get(0);
          Object oCode        = ((List) oRecord).get(1);
          Object oDescription = ((List) oRecord).get(2);
          if(oDescription != null) {
          String sDesc     = oDescription.toString();
          String sDescNorm = sDesc.trim().toUpperCase();
            jcbDescription.addItem(sDesc);
            mapDescId.put(sDescNorm, oId);
            mapDescCode.put(sDescNorm, oCode);
          }
        }
      }
    }

    if(jcbDescription.getItemCount() > 0) {
      jcbDescription.showPopup();
    }
  }

  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());

    JTextField jTextField = new JTextField();
    Dimension dimPreferredSize = jTextField.getPreferredSize();
    int iPreferredHeightComboBox = dimPreferredSize.height;

    jcbDescription = new JComboBox();
    jcbDescription.setPreferredSize(new Dimension(0, iPreferredHeightComboBox));
    jcbDescription.setEditable(true);
    jcbDescription.setToolTipText(sTOOLTIP_TEXT);
    Component oEditorComponent = jcbDescription.getEditor().getEditorComponent();
    Font fontTextField = UIManager.getFont("TextField.font");
    if(fontTextField != null) {
      oEditorComponent.setFont(fontTextField);
    }
    oEditorComponent.addKeyListener(new ComponentKeyAdapter());
    jcbDescription.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int iIndex = jcbDescription.getSelectedIndex();
        if(listLastFindResult != null &&
           iIndex >= 0 &&
           listLastFindResult.size() - 1 >= iIndex) {
          Object oRecord = listLastFindResult.get(iIndex);
          if(oRecord instanceof List) {
            if(((List) oRecord).size() > 0) {
              Object oDescription = ((List) oRecord).get(2);
              if(oDescription != null) {
                String sDesc = oDescription.toString();
                String sDescNorm = sDesc.trim().toUpperCase();
              oValues.set(0, mapDescId.get(sDescNorm));
              oValues.set(1, mapDescCode.get(sDescNorm));
                oValues.set(2, oDescription.toString());
                Component compEditor = jcbDescription.getEditor().getEditorComponent();
                if(compEditor instanceof JTextField) {
                  ((JTextField) compEditor).setCaretPosition(0);
                  boJustSelected = true;
                }
              }
              else {
              oValues.set(0, null);
              oValues.set(1, null);
                oValues.set(2, null);
              }

              notifySet();
            }
          }
        }
      }
    });

    oValues = new ArrayList(3);
    oValues.add(null);
    oValues.add(null);
    oValues.add(null);

    this.add(jcbDescription, BorderLayout.CENTER);
  }

  protected
  void showKey()
  {
    GUIMessage.showInformation("Chiave = " + getKey());
  }
  
  class ComponentKeyAdapter extends KeyAdapter
  {
    public
    ComponentKeyAdapter()
    {
      super();
    }

    public
    void keyPressed(KeyEvent oEvent)
    {
      int iKey = oEvent.getKeyCode();
      if(iKey == KeyEvent.VK_F && oEvent.isControlDown()) { // x Win98
        doFindByDescription();
      }
    }

    public
    void keyTyped(KeyEvent oEvent)
    {
      char cKey = oEvent.getKeyChar();
      if(cKey == KeyEvent.VK_ENTER && (oEvent.isAltDown() || boEnterToFind)) {
      if(boJustSelected) {
        boJustSelected = false;
        return;
      }
        doFindByDescription();
      }
      else
      if(cKey == KeyEvent.VK_DELETE && oEvent.isAltDown()) {
        reset();
      }
      else
      if(cKey == KeyEvent.VK_DELETE) {
        if(oEvent.isAltDown()) {
            reset();
        }
        else {
          if(oValues != null && oValues.size() > 0) {
            Object oKey = oValues.get(0);
            if(oKey != null && oKey.toString().length() > 0) {
              reset();
            }
          }
        }
      }
      else
      if(cKey == KeyEvent.VK_BACK_SPACE) {
      if(oValues != null && oValues.size() > 0) {
        Object oKey = oValues.get(0);
        if(oKey != null && oKey.toString().length() > 0) {
          reset();
        }
      }
      }      
      else
      if((cKey == 'k' || cKey == 'K') && oEvent.isAltDown()) {
        showKey();
      }
      else {
        // L'editor fornito con i look and feel jgoodies hanno questo comportamento:
        // il setItem effettua anche la selezione del testo.
        // Ciò comporta che l'utente può scrivere un solo carattere, poiché ad ogni digitazione
        // viene cancellato quanto scritto in precedenza.
      String sClassNameEditor = jcbDescription.getEditor().getClass().getName();
        if(sClassNameEditor.startsWith("com.jgoodies.")) return;
        if(sClassNameEditor.startsWith("com.sun.java.swing.plaf.windows.")) return;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              String sTextCombo = getTextCombo();
              jcbDescription.getEditor().setItem(sTextCombo);
            }
        });
      }
    }
  }
}

