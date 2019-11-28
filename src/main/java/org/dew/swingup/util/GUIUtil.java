package org.dew.swingup.util;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;

/**
 * Oggetto che raccoglie una serie di utilita' per la costruzione di GUI.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class GUIUtil
{
  public final static String sCOMPONENT_BUTTON      = "Button";
  public final static String sCOMPONENT_CHECKBOX    = "CheckBox";
  public final static String sCOMPONENT_COMBOBOX    = "ComboBox";
  public final static String sCOMPONENT_LABEL       = "Label";
  public final static String sCOMPONENT_RADIOBUTTON = "RadioButton";
  public final static String sCOMPONENT_TABLE       = "Table";
  public final static String sCOMPONENT_TEXTFIELD   = "TextField";
  public final static String sCOMPONENT_TEXTAREA    = "TextArea";
  
  /**
   * Costruisce un pannello con il compenente e una etichetta alla sinistra.
   *
   * @param oComponent  Componente
   * @param sText       Testo dell'etichetta
   * @param iLabelSize  Dimensione dell'etichetta
   * @return JPanel
   */
  public static
  JPanel buildLabelledComponent(Component oComponent, String sText, int iLabelSize)
  {
    JPanel jPanel = new JPanel(new BorderLayout(4, 4));
    JLabel jLabel = new JLabel(sText, JLabel.RIGHT);
    Dimension oDimension = new Dimension(iLabelSize, 0);
    jLabel.setPreferredSize(oDimension);
    jLabel.setMinimumSize(oDimension);
    
    jPanel.add(jLabel, BorderLayout.WEST);
    jPanel.add(oComponent, BorderLayout.CENTER);
    
    return jPanel;
  }
  
  /**
   * Costruisce un pannello con il compenente e una etichetta alla sinistra o in alto.
   *
   * @param oComponent  Componente
   * @param sText       Testo dell'etichetta
   * @param iLabelSize  Dimensione dell'etichetta
   * @param boLabelOnTop  true = in alto, false = a sinistra
   * @return JPanel
   */
  public static
  JPanel buildLabelledComponent(Component oComponent,
    String sText,
    int iLabelSize,
    boolean boLabelOnTop)
  {
    JPanel jPanel = new JPanel(new BorderLayout(4, 4));
    
    if(boLabelOnTop) {
      JLabel jLabel = new JLabel(sText, JLabel.LEFT);
      jPanel.add(jLabel, BorderLayout.NORTH);
    }
    else {
      JLabel jLabel = new JLabel(sText, JLabel.RIGHT);
      Dimension oDimension = new Dimension(iLabelSize, 0);
      jLabel.setPreferredSize(oDimension);
      jLabel.setMinimumSize(oDimension);
      jPanel.add(jLabel, BorderLayout.WEST);
    }
    
    jPanel.add(oComponent, BorderLayout.CENTER);
    
    return jPanel;
  }
  
  /**
   * Costruisce un oggetto JButton con le caratteristiche specificate.
   * L'icona e i margini interni sono ridotti come in un pulsante di azione.
   *
   * @param sGUIData  Text|Description|Icon (Anteporre un & al mnemonico)
   * @param sActionCommand Testo dell'action command
   * @return JButton
   */
  public static
  JButton buildActionButton(String sGUIData, String sActionCommand)
  {
    JButton jbutton = new JButton();
    jbutton.setText(getGUIText(sGUIData));
    String sIcon = getGUIIcon(sGUIData);
    if(sIcon != null) {
      jbutton.setIcon(ResourcesMgr.getSmallImageIcon(sIcon));
    }
    jbutton.setToolTipText(getGUIDescription(sGUIData));
    jbutton.setHorizontalAlignment(SwingConstants.LEFT);
    char c = getGUIMnemonic(sGUIData);
    if(c != '\0') {
      jbutton.setMnemonic(c);
    }
    if(sActionCommand != null) {
      jbutton.setActionCommand(sActionCommand);
    }
    jbutton.setMargin(new Insets(1, 1, 1, 1));
    
    return jbutton;
  }
  
  /**
   * Costruisce un oggetto JToggleButton con le caratteristiche specificate.
   * L'icona e i margini interni sono ridotti come in un pulsante di azione.
   *
   * @param sGUIData  Text|Description|Icon1|Icon2 (Anteporre un & al mnemonico)
   * @param sActionCommand Testo dell'action command
   * @param boSelected Flag selezionato
   * @return JButton
   */
  public static
  JToggleButton buildToggleButton(String sGUIData, String sActionCommand, boolean boSelected)
  {
    JToggleButton jToggleButton = new JToggleButton();
    jToggleButton.setText(getGUIText(sGUIData));
    String sIcon1 = getGUIIcon(sGUIData);
    if(sIcon1 != null) {
      jToggleButton.setIcon(ResourcesMgr.getSmallImageIcon(sIcon1));
    }
    String sIcon2 = getToken(sGUIData, 3);
    if(sIcon2 != null) {
      jToggleButton.setSelectedIcon(ResourcesMgr.getSmallImageIcon(sIcon2));
    }
    jToggleButton.setToolTipText(getGUIDescription(sGUIData));
    jToggleButton.setHorizontalAlignment(SwingConstants.LEFT);
    char c = getGUIMnemonic(sGUIData);
    if(c != '\0') {
      jToggleButton.setMnemonic(c);
    }
    if(sActionCommand != null) {
      jToggleButton.setActionCommand(sActionCommand);
    }
    jToggleButton.setMargin(new Insets(1, 1, 1, 1));
    jToggleButton.setSelected(boSelected);
    
    return jToggleButton;
  }
  
  /**
   * Imposta le caratteristiche dell'oggetto AbstractButton specificato.
   *
   * @param jbutton Oggetto JButton
   * @param sGUIData Text|Description|Icon
   */
  public static
  void setGUIData(JButton jbutton, String sGUIData)
  {
    jbutton.setText(getGUIText(sGUIData));
    String sIcon = getGUIIcon(sGUIData);
    if(sIcon != null) {
      jbutton.setIcon(ResourcesMgr.getSmallImageIcon(sIcon));
    }
    jbutton.setToolTipText(getGUIDescription(sGUIData));
    char c = getGUIMnemonic(sGUIData);
    if(c != '\0') {
      jbutton.setMnemonic(c);
    }
    jbutton.updateUI();
  }
  
  /**
   * Imposta le caratteristiche dell'oggetto JToggleButton specificato.
   *
   * @param jToggleButton Oggetto JToggleButton
   * @param sGUIData Text|Description|Icon|SelectedIcon
   */
  public static
  void setGUIData(JToggleButton jToggleButton, String sGUIData)
  {
    jToggleButton.setText(getGUIText(sGUIData));
    String sIcon = getGUIIcon(sGUIData);
    if(sIcon != null) {
      jToggleButton.setIcon(ResourcesMgr.getSmallImageIcon(sIcon));
    }
    String sIcon2 = getToken(sGUIData, 3);
    if(sIcon2 != null) {
      jToggleButton.setSelectedIcon(ResourcesMgr.getSmallImageIcon(sIcon2));
    }
    jToggleButton.setToolTipText(getGUIDescription(sGUIData));
    char c = getGUIMnemonic(sGUIData);
    if(c != '\0') {
      jToggleButton.setMnemonic(c);
    }
    jToggleButton.updateUI();
  }
  
  /**
   * Costruisce il JButton per le ricerche di look up.
   *
   * @return JButton
   */
  public static
  JButton buildFindButton()
  {
    JButton oResult = new JButton();
    oResult.setFocusable(false);
    oResult.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_FIND));
    oResult.setMargin(new Insets(0, 0, 0, 0));
    oResult.setToolTipText("Cerca");
    return oResult;
  }
  
  /**
   * Assegna un bordo titolato al componente specificato.
   *
   * @param oComponent contenitore di destinazione.
   * @param sTitle     titolo del bordo (null = nessuno).
   */
  public static
  void buildTitledBorder(JComponent oComponent, String sTitle)
  {
    if(sTitle == null) sTitle = "";
    oComponent.setBorder(new TitledBorder(new EtchedBorder(), sTitle));
  }
  
  /**
   * Estrare il testo dalla stringa sGUIData.
   *
   * @param sGUIData Dati della gui espressi nel formato Text|Description|Icon
   * @return Testo
   */
  public static
  String getGUIText(String sGUIData)
  {
    String sText = getToken(sGUIData, 0);
    return removeMnemonicMark(sText);
  }
  
  /**
   * Estrare il mnemonico dalla stringa sGUIData.
   *
   * @param sGUIData Dati della gui espressi nel formato Text|Description|Icon
   * @return Mnemonico
   */
  public static
  char getGUIMnemonic(String sGUIData)
  {
    String sText = getToken(sGUIData, 0);
    return getMnemonic(sText);
  }
  
  /**
   * Estrare la descrizione dalla stringa sGUIData.
   *
   * @param sGUIData Dati della gui espressi nel formato Text|Description|Icon
   * @return Descrizione
   */
  public static
  String getGUIDescription(String sGUIData)
  {
    return getToken(sGUIData, 1);
  }
  
  /**
   * Estrare l'identificativo dell'icona dalla stringa sGUIData.
   *
   * @param sGUIData Dati della gui espressi nel formato Text|Description|Icon
   * @return File icona
   */
  public static
  String getGUIIcon(String sGUIData)
  {
    return getToken(sGUIData, 2);
  }
  
  /**
   * Associa un'action ad una combinazione di tasti che si attiva
   * quando il componente riceve il focus.
   *
   * @param jcomponent JComponent
   * @param sKeyStroke (es. ctrl B)
   * @param action     Action
   */
  public static
  void putActionWhenFocused(JComponent jcomponent, String sKeyStroke, Action action)
  {
    String sIdAction = jcomponent.hashCode() + ":" + sKeyStroke;
    jcomponent.getActionMap().put(sIdAction, action);
    jcomponent.getInputMap().put(KeyStroke.getKeyStroke(sKeyStroke), sIdAction);
  }
  
  /**
   * Associa un'action ad una combinazione di tasti che si attiva
   * quando il componente riceve il focus.
   *
   * @param jcomponent JComponent
   * @param iKeyEvent  cf KeyEvent
   * @param action     Action
   */
  public static
  void putActionWhenFocused(JComponent jcomponent, int iKeyEvent, Action action)
  {
    String sIdAction = jcomponent.hashCode() + ":" + iKeyEvent;
    jcomponent.getActionMap().put(sIdAction, action);
    jcomponent.getInputMap().put(KeyStroke.getKeyStroke(iKeyEvent, 0), sIdAction);
  }
  
  /**
   * Associa un'action ad una combinazione di tasti che si attiva
   * quando il componente fa parte di una Window che ha il focus.
   *
   * @param jcomponent JComponent
   * @param sKeyStroke (es. ctrl B)
   * @param action     Action
   */
  public static
  void putActionInFocusedWindow(JComponent jcomponent, String sKeyStroke, Action action)
  {
    String sIdAction = jcomponent.hashCode() + ":" + sKeyStroke;
    jcomponent.getActionMap().put(sIdAction, action);
    jcomponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke(sKeyStroke), sIdAction);
  }
  
  /**
   * Associa un'action ad una combinazione di tasti che si attiva
   * quando il componente fa parte di una Window che ha il focus.
   *
   * @param jcomponent JComponent
   * @param iKeyEvent  cf KeyEvent
   * @param action     Action
   */
  public static
  void putActionInFocusedWindow(JComponent jcomponent,
    int iKeyEvent,
    Action action)
  {
    String sIdAction = jcomponent.hashCode() + ":" + iKeyEvent;
    jcomponent.getActionMap().put(sIdAction, action);
    jcomponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke(iKeyEvent, 0), sIdAction);
  }
  
  /**
   * Restituisce il font del componente specificato apportando una variazione
   * alla dimensione.
   *
   * @param sComponentName Nome del componente (usare le costanti di GUIUtil)
   * @param iDeltaSize variazione dimensione
   * @return Font
   */
  public static
  Font getFontComponent(String sComponentName, int iDeltaSize)
  {
    Font font = UIManager.getFont(sComponentName + ".font");
    String sFontName = null;
    int iFontStyle = Font.PLAIN;
    int iFontSize = 12;
    if(font != null) {
      sFontName = font.getName();
      iFontStyle = font.getStyle();
      iFontSize = font.getSize();
    }
    else {
      sFontName = "Dialog";
    }
    return new Font(sFontName, iFontStyle, iFontSize + iDeltaSize);
  }
  
  /**
   * Restituisce il font del componente specificato apportando una variazione
   * alla dimensione e allo stile.
   *
   * @param sComponentName Nome del componente (usare le costanti di GUIUtil)
   * @param iFontStyle stile (Font.PLAIN, Font.BOLD, Font.ITALIC)
   * @param iDeltaSize variazione dimensione
   * @return Font
   */
  public static
  Font getFontComponent(String sComponentName, int iFontStyle, int iDeltaSize)
  {
    Font font = UIManager.getFont(sComponentName + ".font");
    String sFontName = null;
    int iFontSize = 12;
    if(font != null) {
      sFontName = font.getName();
      iFontSize = font.getSize();
    }
    else {
      sFontName = "Dialog";
    }
    return new Font(sFontName, iFontStyle, iFontSize + iDeltaSize);
  }
  
  /**
   * Restituisce il font Monospaced con lo stile e la grandezza del font Table.
   *
   * @return Font
   */
  public static
  Font getFontTableMonospaced()
  {
    Font font = UIManager.getFont("Table.font");
    String sFontName = "Monospaced";
    int iFontStyle   = Font.PLAIN;
    int iFontSize    = 12;
    if(font != null) {
      iFontStyle = font.getStyle();
      iFontSize  = font.getSize();
    }
    return DefaultGUIManager.deriveFontForHRScreen(new Font(sFontName, iFontStyle, iFontSize));
  }
  
  /**
   * Modifica il font passato apportando una variazione alla dimensione.
   *
   * @param font Font
   * @param iDeltaSize int
   * @return Font
   */
  public static
  Font modifyFont(Font font, int iDeltaSize)
  {
    if(font == null) return null;
    String sFontName = font.getName();
    int iFontStyle = font.getStyle();
    int iFontSize = font.getSize();
    return new Font(sFontName, iFontStyle, iFontSize + iDeltaSize);
  }
  
  /**
   * Modifica il font passato apportando una variazione alla dimensione e
   * allo stile.
   *
   * @param font Font
   * @param iFontStyle int
   * @param iDeltaSize int
   * @return Font
   */
  public static
  Font modifyFont(Font font, int iFontStyle, int iDeltaSize)
  {
    if(font == null) return null;
    String sFontName = font.getName();
    int iFontSize = font.getSize();
    return new Font(sFontName, iFontStyle, iFontSize + iDeltaSize);
  }
  
  /**
   * Costruisce un albero descritto dalla mappa e con root specificato dalla
   * stringa sRoot
   *
   * @param mapTree Mapppa che descrive l'albero
   * @param sRoot Root
   * @return TreeNode
   */
  public static
  TreeNode buildTree(Map mapTree, String sRoot)
  {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(sRoot);
    
    addLeaves(root, mapTree);
    
    return root;
  }
  
  private static
  void addLeaves(DefaultMutableTreeNode oParent, Object oData)
  {
    if(oData == null) return;
    
    if(oData instanceof Map) {
      addMapLeaves(oParent,(Map) oData);
    }
    else
    if(oData instanceof List) {
      addListLeaves(oParent,(List) oData);
    }
    else {
      DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(oData);
      dmtn.setAllowsChildren(false);
      oParent.add(dmtn);
    }
  }
  
  private static
  void addMapLeaves(DefaultMutableTreeNode oParent, Map map)
  {
    Object[] oKeys = map.keySet().toArray();
    Arrays.sort(oKeys);
    for(int i = 0; i < oKeys.length; i++) {
      Object oKey = oKeys[i];
      Object oValue = map.get(oKey);
      DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(oKey);
      oParent.add(dmtn);
      addLeaves(dmtn, oValue);
    }
  }
  
  private static
  void addListLeaves(DefaultMutableTreeNode oParent, List list)
  {
    Collections.sort(list);
    for(int i = 0; i < list.size(); i++) {
      Object oValue = list.get(i);
      addLeaves(oParent, oValue);
    }
  }
  
  private static
  String getToken(String sText, int iIndex)
  {
    if(sText == null) return null;
    int iCount   = 0;
    int iIndexOf = 0;
    int iBegin   = 0;
    iIndexOf = sText.indexOf('|');
    while(iIndexOf >= 0) {
      String sToken = sText.substring(iBegin, iIndexOf);
      if(iCount == iIndex) return sToken;
      iBegin = iIndexOf + 1;
      iIndexOf = sText.indexOf('|', iBegin);
      iCount++;
    }
    if(iCount == iIndex) {
      return sText.substring(iBegin);
    }
    return null;
  }
  
  private static
  String removeMnemonicMark(String sText)
  {
    if(sText == null) return "";
    int i = sText.indexOf('&');
    if(i < 0) return sText;
    if(sText.length() == i + 1) {
      return sText.substring(0, i);
    }
    return sText.substring(0, i) + sText.substring(i + 1);
  }
  
  private static
  char getMnemonic(String sText)
  {
    if(sText == null) return '\0';
    int i = sText.indexOf('&');
    if(i < 0) return '\0';
    if(sText.length() == i + 1) return '\0';
    return sText.charAt(i + 1);
  }
}
