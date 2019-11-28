package org.dew.swingup.components;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Implementazione di ADecodifiableComponent costiuita da una serie di JTextField.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class JTextDecodifiable extends ADecodifiableComponent
{
  protected static final String sTOOLTIP_TEXT = "(Alt-Invio decodifica; Alt-Canc cancella)";
  
  protected int iElements = 0;
  
  protected List oValues;
  protected List listComponents;
  protected JButton cmdFind;
  
  protected Cursor oDefaultCursor = Cursor.getDefaultCursor();
  protected int[] aiWidthComponents;
  protected boolean boCodeIfKeyIsNull       = false;
  protected boolean boCheckFilterRestricted = false;
  protected boolean boFindByPressingEnter   = false;
  
  public
  JTextDecodifiable()
  {
    this(null, 2);
  }
  
  public
  JTextDecodifiable(String sEntity)
  {
    this(sEntity, 2);
  }
  
  public
  JTextDecodifiable(String sEntity, int iElements)
  {
    this(sEntity, iElements, null);
  }
  
  public
  JTextDecodifiable(String sEntity, int iElements, int[] aiWidthComponents)
  {
    this.sEntity = sEntity;
    this.iElements = iElements;
    if(aiWidthComponents != null) {
      this.aiWidthComponents = aiWidthComponents;
    }
    else {
      this.aiWidthComponents = new int[0];
    }
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
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
  
  /**
   * Impostando a true tale flag il componente controlla che la lista
   * dei valori filtrati sia valorizzata relativamente ai campi corrispondenti
   * alle caselle di testo.
   * In alcuni casi tale lista di valori potrebbe presentare piu' valori
   * rispetto ai campi sicche' quando l'utente clicca sul tasto "find" viene
   * scatenata una ricerca anche quando i campi di testo risultano vuoti.
   *
   * @param boCheckFilterRestricted boolean
   */
  public
  void setCheckFilterRestricted(boolean boCheckFilterRestricted)
  {
    this.boCheckFilterRestricted = boCheckFilterRestricted;
  }
  
  public
  boolean isCheckFilterRestricted()
  {
    return boCheckFilterRestricted;
  }
  
  /**
   * Impostando a true tale flag il componente effettua la ricerca
   * premendo il tasto Invio oltre che' Alt-Invio.
   *
   * @param boFindByPressingEnter boolean
   */
  public
  void setFindByPressingEnter(boolean boFindByPressingEnter)
  {
    this.boFindByPressingEnter = boFindByPressingEnter;
  }
  
  public
  boolean isFindByPressingEnter()
  {
    return boFindByPressingEnter;
  }
  
  public
  int getElements()
  {
    return iElements;
  }
  
  public
  void addFocusListener(FocusListener fl)
  {
    super.addFocusListener(fl);
    if(listComponents != null) {
      for(int i = 0; i < listComponents.size(); i++) {
        JTextField oComponent = (JTextField) listComponents.get(i);
        oComponent.addFocusListener(fl);
      }
    }
  }
  
  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    if(listComponents != null) {
      for(int i = 0; i < listComponents.size(); i++) {
        JTextField oComponent = (JTextField) listComponents.get(i);
        oComponent.removeFocusListener(fl);
      }
    }
  }
  
  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    if(listComponents != null) {
      for(int i = 0; i < listComponents.size(); i++) {
        Object oComponent = listComponents.get(i);
        if(oComponent instanceof JComponent) {
          ((JComponent) oComponent).addKeyListener(kl);
        }
      }
    }
  }
  
  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    if(listComponents != null) {
      for(int i = 0; i < listComponents.size(); i++) {
        Object oComponent = listComponents.get(i);
        if(oComponent instanceof JComponent) {
          ((JComponent) oComponent).removeKeyListener(kl);
        }
      }
    }
  }
  
  public
  void requestFocus()
  {
    if(listComponents != null && listComponents.size() > 0) {
      ((JTextField) listComponents.get(0)).requestFocus();
    }
    else {
      super.requestFocus();
    }
  }
  
  public
  void transferFocus()
  {
    if(listComponents != null && listComponents.size() > 0) {
      Component component = (Component) listComponents.get(listComponents.size() - 1);
      component.transferFocus();
    }
    else {
      super.transferFocus();
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
    if(listComponents != null) {
      for(int i = 0; i < listComponents.size(); i++) {
        ((JTextField) listComponents.get(i)).setCursor(oCursor);
      }
    }
  }
  
  public
  void setName(String sName)
  {
    super.setName(sName);
    for(int i = 0; i < listComponents.size(); i++) {
      ((JComponent) listComponents.get(i)).setName(sName + "." + i);
    }
  }
  
  public
  Object getKey()
  {
    if(oValues == null || oValues.size() == 0) {
      return null;
    }
    Object oKey = oValues.get(0);
    if(oKey == null && boCodeIfKeyIsNull) {
      if(listComponents != null && listComponents.size() > 0) {
        JTextField oComponent = (JTextField) listComponents.get(0);
        String sCode = oComponent.getText();
        if(sCode.trim().length() == 0) return null;
        return sCode;
      }
    }
    return oKey;
  }
  
  public
  void setValues(List listValues)
  {
    if(listValues == null) {
      reset();
      return;
    }
    Object oCurrentKey = oValues != null && oValues.size() > 0 ? oValues.get(0) : null;
    Object oNewKey     = listValues != null && listValues.size() > 0 ? listValues.get(0) : null;
    if(oCurrentKey != null && oNewKey != null && oCurrentKey.equals(oNewKey)) {
      return;
    }
    
    clear();
    
    int iMin = oValues.size();
    if(listValues.size() < oValues.size()) {
      iMin = listValues.size();
    }
    for(int i = 0; i < iMin; i++) {
      setValue(i, listValues.get(i));
    }
    notifySet();
  }
  
  public
  void setValues(List listValues, boolean boNotify)
  {
    if(listValues == null) {
      reset();
      return;
    }
    
    Object oCurrentKey = oValues != null && oValues.size() > 0 ? oValues.get(0) : null;
    Object oNewKey     = listValues != null && listValues.size() > 0 ? listValues.get(0) : null;
    if(oCurrentKey != null && oNewKey != null && oCurrentKey.equals(oNewKey)) {
      return;
    }
    
    clear();
    
    int iMin = oValues.size();
    if(listValues.size() < oValues.size()) {
      iMin = listValues.size();
    }
    for(int i = 0; i < iMin; i++) {
      setValue(i, listValues.get(i));
    }
    if(boNotify) {
      notifySet();
    }
  }
  
  public
  void setValue(int iIndex, Object oValue)
  {
    if(iIndex == 0) {
      oValues.set(0, oValue);
      return;
    }
    oValues.set(iIndex, oValue);
    setText(iIndex, oValue);
  }
  
  public
  List getValues()
  {
    return oValues;
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
  List getFilterValues()
  {
    List listResult = new ArrayList();
    for(int i = 0; i < listComponents.size(); i++) {
      JTextField jTextField = (JTextField) listComponents.get(i);
      listResult.add(jTextField.getText().trim());
    }
    return listResult;
  }
  
  protected
  boolean checkFilter(List oFilter)
  {
    if(boCheckFilterRestricted &&(oFilter.size() >= listComponents.size())) {
      for(int i = 0; i < listComponents.size(); i++) {
        Object oValue = oFilter.get(i);
        if(oValue == null) continue;
        String sText = oValue.toString().trim();
        if(sText.length() > 0) {
          return true;
        }
      }
      return false;
    }
    else {
      return super.checkFilter(oFilter);
    }
  }
  
  public
  void setBackground(Color color)
  {
    if(listComponents != null) {
      for(int i = 0; i < listComponents.size(); i++) {
        JTextField oComponent = (JTextField) listComponents.get(i);
        oComponent.setBackground(color);
      }
    }
    else {
      super.setBackground(color);
    }
  }
  
  public
  Color getBackground()
  {
    if(listComponents != null && listComponents.size() > 0) {
      return ((JTextField) listComponents.get(0)).getBackground();
    }
    return super.getBackground();
  }
  
  public
  void setEnabled(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
    for(int i = 0; i < listComponents.size(); i++) {
      JTextField oComponent = (JTextField) listComponents.get(i);
      oComponent.setEnabled(boEnabled);
    }
    cmdFind.setEnabled(boEnabled);
  }
  
  public
  void setEditable(boolean boEditable)
  {
    super.setEditable(boEditable);
    for(int i = 0; i < listComponents.size(); i++) {
      JTextField oComponent = (JTextField) listComponents.get(i);
      oComponent.setEditable(boEnabled);
    }
    cmdFind.setEnabled(boEnabled);
  }
  
  public
  JTextField getJTextField(int iIndex)
  {
    if(iIndex < 0 ||
      iIndex > listComponents.size() - 1) {
      return null;
    }
    return (JTextField) listComponents.get(iIndex);
  }
  
  protected
  void clear()
  {
    for(int i = 0; i < oValues.size(); i++) {
      oValues.set(i, null);
    }
    for(int i = 0; i < listComponents.size(); i++) {
      JTextField oComponent = (JTextField) listComponents.get(i);
      oComponent.setText("");
    }
  }
  
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());
    
    oValues = new ArrayList(iElements + 1);
    for(int i = 0; i < iElements + 1; i++) {
      oValues.add(null);
    }
    listComponents = new ArrayList(iElements);
    for(int i = 0; i < iElements; i++) {
      JTextField oComponent = new JTextField();
      int iWidth = 80;
      if(i < aiWidthComponents.length) iWidth = aiWidthComponents[i];
      Dimension preferredSize = oComponent.getPreferredSize();
      oComponent.setPreferredSize(new Dimension(iWidth, (int) Math.round(preferredSize.getHeight())));
      oComponent.setToolTipText(sTOOLTIP_TEXT);
      if(i < iElements - 1) {
        oComponent.addKeyListener(new ComponentKeyAdapter(oComponent));
      }
      else {
        oComponent.addKeyListener(new ComponentKeyAdapter(null));
      }
      
      oComponent.getDocument().addDocumentListener(new FieldDocumentListener(oComponent));
      listComponents.add(oComponent);
    }
    
    oDefaultCursor = ((Component) listComponents.get(0)).getCursor();
    
    if(iElements > 1) {
      JPanel oComponentsPanel = new JPanel(new BorderLayout(2, 2));
      oComponentsPanel.add(buildLeftSide(), BorderLayout.WEST);
      oComponentsPanel.add(buildRightSide(), BorderLayout.CENTER);
      this.add(oComponentsPanel, BorderLayout.NORTH);
    }
    else {
      cmdFind = buildFindButton();
      JPanel oComponentsPanel = new JPanel(new BorderLayout(4, 4));
      oComponentsPanel.add((Component) listComponents.get(0), BorderLayout.CENTER);
      oComponentsPanel.add(cmdFind, BorderLayout.EAST);
      this.add(oComponentsPanel, BorderLayout.NORTH);
    }
  }
  
  protected
  JPanel buildLeftSide()
  {
    if(listComponents.size() < 2) {
      return new JPanel();
    }
    
    JPanel jPanelLeftSide = new JPanel(new BorderLayout(4, 4));
    
    JPanel jFirstPanel = new JPanel(new BorderLayout(4, 4));
    Component oComponent = (Component) listComponents.get(0);
    jFirstPanel.add(oComponent, BorderLayout.WEST);
    
    JPanel jPrevPanel = jFirstPanel;
    for(int i = 1; i < listComponents.size() - 1; i++) {
      JPanel jPanel = new JPanel(new BorderLayout(4, 4));
      jPanel.add((Component) listComponents.get(i), BorderLayout.WEST);
      jPrevPanel.add(jPanel, BorderLayout.CENTER);
      jPrevPanel = jPanel;
    }
    
    jPanelLeftSide.add(jFirstPanel, BorderLayout.CENTER);
    
    return jPanelLeftSide;
  }
  
  protected
  JPanel buildRightSide()
  {
    JPanel jpanel = new JPanel(new BorderLayout(4, 4));
    
    JTextField oLastComponent = (JTextField) listComponents.get(listComponents.size() - 1);
    jpanel.add(oLastComponent, BorderLayout.CENTER);
    
    cmdFind = buildFindButton();
    jpanel.add(cmdFind, BorderLayout.EAST);
    
    return jpanel;
  }
  
  protected
  JButton buildFindButton()
  {
    JButton oResult = GUIUtil.buildFindButton();
    oResult.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent oEvent) {
        doFind(true);
      }
    }
    );
    return oResult;
  }
  
  protected
  void setText(int iIndex, Object oValue)
  {
    String sText = null;
    if(oValue == null) {
      sText = "";
    }
    else {
      sText = oValue.toString();
    }
    JTextField oComponent = (JTextField) listComponents.get(iIndex - 1);
    oComponent.setText(sText);
    SwingUtilities.invokeLater(new Field_SetCaretPosition_0(oComponent));
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
    protected Component oComponent;
    
    public
    ComponentKeyAdapter(Component oComponent)
    {
      super();
      this.oComponent = oComponent;
    }
    
    public
    void keyPressed(KeyEvent oEvent)
    {
      int iKey = oEvent.getKeyCode();
      if(iKey == KeyEvent.VK_F && oEvent.isControlDown()) { // x Win98
        doFind(false);
      }
    }
    
    public
    void keyTyped(KeyEvent oEvent)
    {
      char cKey = oEvent.getKeyChar();
      if(cKey == KeyEvent.VK_ENTER && oEvent.isAltDown()) {
        doFind(false);
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
      if(cKey == KeyEvent.VK_ENTER && !oEvent.isAltDown()) {
        if(boFindByPressingEnter) {
          doFind(false);
        }
        else if(oComponent != null) {
          oComponent.transferFocus();
        }
      }
      else
      if((cKey == 'k' || cKey == 'K') && oEvent.isAltDown()) {
        showKey();
      }
    }
  }
}
