package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Implementazione di ADecodifiableComponent costiuita da una JTextField
 * e una JComboBox.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class JTextComboDecodifiable extends ADecodifiableComponent
{
  protected static final String sTOOLTIP_TEXT = "(Alt-Invio decodifica; Alt-Canc cancella)";
  
  protected JTextField jtfCode;
  protected JComboBox  jcbDescription;
  protected int iWidthCodeField;
  
  protected List oValues;
  protected List oLastValuesSetted;
  protected List listLastFindResult = null;
  
  protected Cursor oDefaultCursor = Cursor.getDefaultCursor();
  
  protected boolean boCodeIfKeyIsNull = false;
  protected boolean boShowCodeInDescription = false;
  
  public
  JTextComboDecodifiable()
  {
    this(null, 80);
  }
  
  public
  JTextComboDecodifiable(String sEntity)
  {
    this(sEntity, 80);
  }
  
  public
  JTextComboDecodifiable(String sEntity, int iWidthCodeField)
  {
    this.sEntity = sEntity;
    this.iWidthCodeField = iWidthCodeField;
    
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public
  void setShowCodeInDescription(boolean boFlag)
  {
    this.boShowCodeInDescription = boFlag;
  }
  
  public
  boolean isShowCodeInDescription()
  {
    return boShowCodeInDescription;
  }
  
  /**
   * Impostando a true tale flag il componente restituisce il testo
   * digitato nel campo codice se la chiave e' null.
   *
   * @param boCodeIfKeyIsNull boolean
   */
  public
  void setCodeIfKeyIsNull(boolean boCodeIfKeyIsNull)
  {
    this.boCodeIfKeyIsNull = boCodeIfKeyIsNull;
  }
  
  public
  boolean isCodeIfKeyIsNull()
  {
    return boCodeIfKeyIsNull;
  }
  
  public
  void addFocusListener(FocusListener fl)
  {
    super.addFocusListener(fl);
    
    if(jtfCode != null) {
      jtfCode.addFocusListener(fl);
    }
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().addFocusListener(fl);
    }
  }
  
  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    
    if(jtfCode != null) {
      jtfCode.removeFocusListener(fl);
    }
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().removeFocusListener(fl);
    }
  }
  
  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    
    if(jtfCode != null) {
      jtfCode.addKeyListener(kl);
    }
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().addKeyListener(kl);
    }
  }
  
  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    
    if(jtfCode != null) {
      jtfCode.removeKeyListener(kl);
    }
    
    if(jcbDescription != null) {
      jcbDescription.getEditor().getEditorComponent().removeKeyListener(kl);
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
  void setName(String sName)
  {
    super.setName(sName);
    jtfCode.setName(sName + ".0");
    jcbDescription.setName(sName + ".1");
  }
  
  public
  void requestFocus()
  {
    jtfCode.requestFocus();
  }
  
  public
  void setCursor(Cursor cursor)
  {
    super.setCursor(cursor);
    
    Cursor oCursor = cursor;
    if(cursor.equals(Cursor.getDefaultCursor())) {
      oCursor = oDefaultCursor;
    }
    jtfCode.setCursor(oCursor);
    jcbDescription.setCursor(oCursor);
    jcbDescription.getEditor().getEditorComponent().setCursor(oCursor);
  }
  
  public
  void setBackground(Color color)
  {
    if(jtfCode == null) {
      super.setBackground(color);
      return;
    }
    jtfCode.setBackground(color);
    jcbDescription.getEditor().getEditorComponent().setBackground(color);
  }
  
  public
  Color getBackground()
  {
    if(jtfCode == null) {
      return super.getBackground();
    }
    return jtfCode.getBackground();
  }
  
  public
  void setEnabled(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
    jtfCode.setEnabled(boEnabled);
    jcbDescription.setEnabled(boEnabled);
  }
  
  public
  void setEditable(boolean boEditable)
  {
    super.setEditable(boEditable);
    jtfCode.setEditable(boEditable);
    jcbDescription.setEditable(boEditable);
  }
  
  public
  Object getKey()
  {
    if(oValues == null || oValues.size() == 0) {
      return null;
    }
    
    Object oKey = oValues.get(0);
    
    if(oKey == null && boCodeIfKeyIsNull) {
      String sCode = jtfCode.getText();
      if(sCode.trim().length() == 0) return null;
      return jtfCode.getText();
    }
    
    return oKey;
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
    List listResult = new ArrayList(2);
    listResult.add(jtfCode.getText().trim());
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
    
    Object oCurrentKey = oValues != null && oValues.size() > 0 ? oValues.get(0) : null;
    Object oNewKey     = listValues != null && listValues.size() > 0 ? listValues.get(0) : null;
    if(oCurrentKey != null && oNewKey != null && oCurrentKey.equals(oNewKey)) {
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
    
    Object oCurrentKey = oValues != null && oValues.size() > 0 ? oValues.get(0) : null;
    Object oNewKey     = listValues != null && listValues.size() > 0 ? listValues.get(0) : null;
    if(oCurrentKey != null && oNewKey != null && oCurrentKey.equals(oNewKey)) {
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
      if(oValue != null) {
        jtfCode.setText(oValue.toString());
        SwingUtilities.invokeLater(new Field_SetCaretPosition_0(jtfCode));
      }
      else {
        jtfCode.setText("");
      }
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
      }
      else {
        jcbDescription.setSelectedItem("");
      }
    }
  }
  
  protected
  void clear()
  {
    setNullValues();
    jtfCode.setText("");
    jcbDescription.removeAllItems();
    jcbDescription.setSelectedItem("");
  }
  
  protected
  void doFindByCode()
  {
    if(oLookUpFinder == null) {
      GUIMessage.showWarning("LookUpFinder non impostato");
      return;
    };
    
    String sCode = jtfCode.getText();
    
    List listFilter = new ArrayList(2);
    listFilter.add(sCode);
    listFilter.add(null);
    
    notifyBeforeFind(listFilter);
    
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
    setNullValues();
    
    if(listResult == null || listResult.size() == 0) {
      return;
    }
    
    if(listResult.size() == 1) {
      Object oRecord = listResult.get(0);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 2) {
          setValue(0,((List) oRecord).get(0));
          setValue(1,((List) oRecord).get(1));
          Object oDescription = ((List) oRecord).get(2);
          if(oDescription != null) {
            oValues.set(2, oDescription.toString());
            jcbDescription.addItem(oDescription.toString());
          }
          else {
            oValues.set(2, "");
            jcbDescription.addItem("");
          }
          
          notifySet();
        }
      }
      return;
    }
    
    Object oFirstEditorItem = null;
    for(int i = 0; i < listResult.size(); i++) {
      Object oRecord = listResult.get(i);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 2) {
          Object oItem = null;
          if(boShowCodeInDescription) {
            oItem = ((List) oRecord).get(2) + " (" +
              ((List) oRecord).get(1) + ")";
          }
          else {
            oItem = ((List) oRecord).get(2);
          }
          if(oItem != null) {
            jcbDescription.addItem(oItem.toString());
            if(oFirstEditorItem == null) {
              oFirstEditorItem = ((List) oRecord).get(2);
            }
          }
        }
      }
    }
    
    if(jcbDescription.getItemCount() > 0) {
      jcbDescription.showPopup();
      jcbDescription.getEditor().setItem(oFirstEditorItem);
    }
  }
  
  protected
  void doFindByDescription()
  {
    if(oLookUpFinder == null) {
      GUIMessage.showWarning("LookUpFinder non impostato");
      return;
    };
    
    String sDescription = getDescription();
    
    List listFilter = new ArrayList(2);
    listFilter.add(null);
    listFilter.add(sDescription);
    
    notifyBeforeFind(listFilter);
    
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
    setNullValues();
    jtfCode.setText("");
    
    if(listResult == null || listResult.size() == 0) {
      jcbDescription.getEditor().setItem(sDescription);
      return;
    }
    
    if(listResult.size() == 1) {
      Object oRecord = listResult.get(0);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 2) {
          setValue(0,((List) oRecord).get(0));
          setValue(1,((List) oRecord).get(1));
          Object oDescription = ((List) oRecord).get(2);
          if(oDescription != null) {
            oValues.set(2, oDescription.toString());
            jcbDescription.addItem(oDescription.toString());
          }
          else {
            oValues.set(2, "");
            jcbDescription.addItem("");
          }
        }
        
        notifySet();
      }
      
      return;
    }
    
    Object oFirstEditorItem = null;
    for(int i = 0; i < listResult.size(); i++) {
      Object oRecord = listResult.get(i);
      if(oRecord instanceof List) {
        if(((List) oRecord).size() > 2) {
          Object oItem = null;
          if(boShowCodeInDescription) {
            oItem = ((List) oRecord).get(2) + " (" +
              ((List) oRecord).get(1) + ")";
          }
          else {
            oItem = ((List) oRecord).get(2);
          }
          if(oItem != null) {
            jcbDescription.addItem(oItem.toString());
            if(oFirstEditorItem == null) {
              oFirstEditorItem = ((List) oRecord).get(2);
            }
          }
        }
      }
    }
    
    if(jcbDescription.getItemCount() > 0) {
      jcbDescription.showPopup();
      jcbDescription.getEditor().setItem(oFirstEditorItem);
    }
  }
  
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());
    
    jtfCode = new JTextField();
    Dimension dimPreferredSize = jtfCode.getPreferredSize();
    int iPreferredHeight = dimPreferredSize.height;
    jtfCode.setPreferredSize(new Dimension(iWidthCodeField, iPreferredHeight));
    jtfCode.addKeyListener(new ComponentKeyAdapter(false));
    jtfCode.getDocument().addDocumentListener(new FieldDocumentListener(jtfCode));
    jtfCode.setToolTipText(sTOOLTIP_TEXT);
    
    jcbDescription = new JComboBox();
    jcbDescription.setPreferredSize(new Dimension(0, iPreferredHeight));
    jcbDescription.setEditable(true);
    jcbDescription.setToolTipText(sTOOLTIP_TEXT);
    jcbDescription.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
    Component oEditorComponent = jcbDescription.getEditor().getEditorComponent();
    oEditorComponent.setFont(jtfCode.getFont());
    oEditorComponent.addKeyListener(new ComponentKeyAdapter(true));
    jcbDescription.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int iIndex = jcbDescription.getSelectedIndex();
        if(listLastFindResult != null &&
          iIndex >= 0 &&
          listLastFindResult.size() - 1 >= iIndex) {
          Object oRecord = listLastFindResult.get(iIndex);
          if(oRecord instanceof List) {
            if(((List) oRecord).size() > 2) {
              setValue(0,((List) oRecord).get(0));
              setValue(1,((List) oRecord).get(1));
              Object oDescription = ((List) oRecord).get(2);
              if(oDescription != null) {
                oValues.set(2, oDescription.toString());
                SwingUtilities.invokeLater(new ARunnable(oDescription) {
                  public void run() {
                    jcbDescription.getEditor().setItem(oData);
                  }
                });
              }
              else {
                oValues.set(2, "");
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
    
    this.add(jtfCode, BorderLayout.WEST);
    this.add(jcbDescription, BorderLayout.CENTER);
  }
  
  protected
  void setNullValues()
  {
    oValues.set(0, null); // Key
    oValues.set(1, null); // Code
    oValues.set(2, null); // Description
  }
  
  protected
  void showKey()
  {
    GUIMessage.showInformation("Chiave = " + getKey());
  }
  
  class Field_SetCaretPosition_0 implements Runnable
  {
    protected JTextField jtextField;
    
    public Field_SetCaretPosition_0(JTextField jtextField) {
      this.jtextField = jtextField;
    }
    
    public void run() {
      jtextField.setCaretPosition(0);
    }
  }
  
  class FieldDocumentListener implements DocumentListener
  {
    JTextField jtextfied = null;
    
    public
    FieldDocumentListener(JTextField jtextfied)
    {
      this.jtextfied = jtextfied;
    }
    
    public
    void insertUpdate(DocumentEvent e)
    {
      String sText = jtextfied.getText();
      switch(iCase) {
        case iCASE_UPPER:
        if(!sText.equals(sText.toUpperCase())) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              jtextfied.setText(jtextfied.getText().toUpperCase());
            }
          });
        }
        break;
        case iCASE_LOWER:
        if(!sText.equals(sText.toLowerCase())) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              jtextfied.setText(jtextfied.getText().toLowerCase());
            }
          });
        }
        break;
      }
    }
    
    public
    void removeUpdate(DocumentEvent e)
    {
    }
    
    public
    void changedUpdate(DocumentEvent e)
    {
    }
  }
  
  class ComponentKeyAdapter extends KeyAdapter
  {
    protected boolean boFindByDescription = false;
    
    public
    ComponentKeyAdapter(boolean boFindByDescription)
    {
      super();
      this.boFindByDescription = boFindByDescription;
    }
    
    public
    void keyPressed(KeyEvent oEvent)
    {
      int iKey = oEvent.getKeyCode();
      if(iKey == KeyEvent.VK_F && oEvent.isControlDown()) { // x Win98
        if(boFindByDescription) {
          doFindByDescription();
        }
        else {
          doFindByCode();
        }
      }
    }
    
    public
    void keyTyped(KeyEvent oEvent)
    {
      char cKey = oEvent.getKeyChar();
      if(cKey == KeyEvent.VK_ENTER && oEvent.isAltDown()) {
        if(boFindByDescription) {
          doFindByDescription();
        }
        else {
          doFindByCode();
        }
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
        if(boFindByDescription) {
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
}
