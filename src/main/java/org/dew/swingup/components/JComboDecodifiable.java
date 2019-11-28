package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;

/**
 * Implementazione di ADecodifiableComponent costiuita da una JComboBox
 * con testo libero.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class JComboDecodifiable extends ADecodifiableComponent
{
  protected static final String sTOOLTIP_TEXT = "(Alt-Invio decodifica; Alt-Canc cancella)";
  
  protected JComboBox jcbDescription;
  
  protected List oValues;
  protected List oLastValuesSetted;
  protected List listLastFindResult = null;
  
  protected Cursor oDefaultCursor = Cursor.getDefaultCursor();
  
  protected boolean boEnterToFind  = false;
  protected boolean boJustSelected = false;
  
  public
  JComboDecodifiable()
  {
    this(null);
  }
  
  public
  JComboDecodifiable(String sEntity)
  {
    this.sEntity = sEntity;
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
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
      jcbDescription.getEditor().getEditorComponent().addFocusListener(fl);
    }
  }
  
  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().removeFocusListener(fl);
    }
  }
  
  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().addKeyListener(kl);
    }
  }
  
  public
  void transferFocus()
  {
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().transferFocus();
    }
  }
  
  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().removeKeyListener(kl);
    }
  }
  
  public
  void requestFocus()
  {
    jcbDescription.requestFocus();
  }
  
  public
  void setCursor(Cursor cursor)
  {
    super.setCursor(cursor);
    
    Cursor oCursor = cursor;
    if(cursor.equals(Cursor.getDefaultCursor())) {
      oCursor = oDefaultCursor;
    }
    jcbDescription.setCursor(oCursor);
    jcbDescription.getEditor().getEditorComponent().setCursor(oCursor);
  }
  
  public
  void setBackground(Color color)
  {
    if(jcbDescription == null) {
      super.setBackground(color);
      return;
    }
    jcbDescription.getEditor().getEditorComponent().setBackground(color);
  }
  
  public
  Color getBackground()
  {
    if(jcbDescription == null) {
      return super.getBackground();
    }
    return jcbDescription.getEditor().getEditorComponent().getBackground();
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
    oValues.set(0, sDescription);
    jcbDescription.getEditor().setItem(sDescription);
    
    return sDescription;
  }
  
  public
  String getDescription()
  {
    String sResult = null;
    
    ComboBoxEditor oComboBoxEditor= jcbDescription.getEditor();
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
    
    ComboBoxEditor oComboBoxEditor = jcbDescription.getEditor();
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
    List listResult = new ArrayList(1);
    listResult.add(getDescription());
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
    
    clear();
    
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
    
    // Il testo potrebbe subire una trasformazione (es. uppercase)...
    oValues.set(0, sDescription);
    jcbDescription.getEditor().setItem(sDescription);
    
    return oValues;
  }
  
  public
  void setValue(int iIndex, Object oValue)
  {
    if(iIndex == 0) {
      oValues.set(0, oValue);
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
      }
      else {
        jcbDescription.setSelectedItem("");
      }
    }
  }
  
  protected
  void clear()
  {
    oValues.set(0, null);
    jcbDescription.removeAllItems();
    jcbDescription.setSelectedItem("");
  }
  
  protected
  void doFindByDescription()
  {
    if(oLookUpFinder == null) {
      GUIMessage.showWarning("LookUpFinder non impostato");
      return;
    };
    
    String sDescription = getDescription();
    
    List listFilter = new ArrayList(1);
    listFilter.add(sDescription);
    
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
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la ricerca", ex);
      return;
    }
    finally {
      setDefaultCursor();
    }
    
    listLastFindResult = listResult;
    jcbDescription.removeAllItems();
    oValues.set(0, null);
    
    if(listResult == null || listResult.size() == 0) {
      jcbDescription.getEditor().setItem(sDescription);
      return;
    }
    
    if(listResult.size() == 1) {
      Object oRecord = listResult.get(0);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 0) {
          Object oDescription = ((List) oRecord).get(0);
          if(oDescription != null) {
            oValues.set(0, oDescription.toString());
            jcbDescription.addItem(oDescription.toString());
          }
          else {
            oValues.set(0, "");
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
          Object oItem = ((List) oRecord).get(0);
          if(oItem != null) {
            jcbDescription.addItem(oItem.toString());
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
        boJustSelected = false;
        if(listLastFindResult != null &&
          iIndex >= 0 &&
          listLastFindResult.size() - 1 >= iIndex) {
          Object oRecord = listLastFindResult.get(iIndex);
          if(oRecord instanceof List) {
            if(((List) oRecord).size() > 0) {
              Object oDescription = ((List) oRecord).get(0);
              if(oDescription != null) {
                oValues.set(0, oDescription.toString());
              }
              else {
                oValues.set(0, "");
              }
              Component compEditor = jcbDescription.getEditor().getEditorComponent();
              if(compEditor instanceof JTextField) {
                ((JTextField) compEditor).setCaretPosition(0);
                boJustSelected = true;
              }
              notifySet();
            }
          }
        }
      }
    });
    
    oValues = new ArrayList(1);
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
      if(cKey == KeyEvent.VK_ENTER &(oEvent.isAltDown() || boEnterToFind)) {
        if(boJustSelected) {
          boJustSelected = false;
          return;
        }
        doFindByDescription();
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
      if(cKey == KeyEvent.VK_SPACE && oEvent.isAltDown()) {
        reset();
      }
      else
      if((cKey == 'k' || cKey == 'K') && oEvent.isAltDown()) {
        showKey();
      }
      else {
        // L'editor fornito con i look and feel jgoodies e windows hanno questo comportamento:
        // il setItem effettua anche la selezione del testo.
        // Cio' comporta che l'utente puo' scrivere un solo carattere, poiche' ad ogni digitazione
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
