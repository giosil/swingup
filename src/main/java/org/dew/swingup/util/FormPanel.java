package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.net.URL;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import pv.jfcx.*;

import org.dew.swingup.*;
import org.dew.swingup.components.*;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.layout.GridLayoutExt;

/**
 * Estensione di JPanel per la costruzione e la gestione di un form.
 *
 * @author <a href="mailto:giorgio.giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class FormPanel extends JPanel implements ITagable
{
  public static final String sFORMPANEL = IResourceMgr.sPREFIX + ".formpanel";
  public static final String sFORMPANEL_MANDATORIES   = sFORMPANEL + ".mandatories";
  public static final String sFORMPANEL_ONFOCUS       = sFORMPANEL + ".onfocus";
  public static final String sFORMPANEL_CASE          = sFORMPANEL + ".case";
  public static final String sFORMPANEL_DEFAULTS_DATE = sFORMPANEL + ".defaults.date";
  
  protected int iCurrentTabIndex = -1;
  protected List listTabs;
  protected Map mapTabs;
  protected List oCurrentRow;
  protected List oRowsOfCurrentTab;
  protected List listFields;
  protected Map mapFields;
  protected FontMetrics oFontMetrics;
  protected Component oFormComponent;
  protected JTabbedPane oTabbedPane;
  protected List oMandatoryFields;
  protected List listAlias;
  protected List listLinkedFormPanel;
  protected FormPanel oMasterLinkedFormPanel;
  protected int iMasterLFP = -1;
  protected String sTitle;
  protected String sTag;
  
  protected boolean boLabelOnTop = false;
  protected int iGap = 2;
  protected Color oBGColorOnFocus = null;
  protected Color oColorCheckBoxFocus = null;
  protected FormPanel oParentFormPanel;
  protected String sIdFieldDefaultFocus;
  protected int iPreferredHeightComboBox = 0;
  
  protected int iCase = 0;
  protected boolean boDefaultDate = false;
  protected boolean boDontNotifyChange = false;
  protected int iMinSizeLabel = 0;
  protected List listFocusListener = new ArrayList(3);
  
  /**
   * Costruttore.
   */
  public
  FormPanel()
  {
    this(null);
  }
  
  /**
   * Costruttore con la definizione del titolo del form panel.
   *
   * @param sTitle Titolo del form panel.
   */
  public
  FormPanel(String sTitle)
  {
    super(new BorderLayout(2, 2));
    this.sTitle = sTitle;
    if(sTitle != null) {
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sTitle));
    }
    Font oFontLabel = (Font) UIManager.get("Label.font");
    oFontMetrics = ResourcesMgr.mainFrame.getGraphics().getFontMetrics(oFontLabel);
    init();
  }
  
  /**
   * Costruttore. L'oggetto FontMetrics serve per dimensionare la lunghezza
   * delle etichette.
   *
   * @param sTitle Titolo del form panel
   * @param fontMetrics FontMetrics
   */
  public
  FormPanel(String sTitle, FontMetrics fontMetrics)
  {
    super(new BorderLayout(2, 2));
    this.sTitle = sTitle;
    if(sTitle != null) {
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sTitle));
    }
    this.oFontMetrics = fontMetrics;
    init();
  }
  
  /**
   * Imposta il titolo del FormPanel.
   *
   * @param sTitle Titolo del FormPanel
   */
  public
  void setTitle(String sTitle)
  {
    this.sTitle = sTitle;
    if(sTitle != null) {
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sTitle));
    }
    else {
      this.setBorder(BorderFactory.createEmptyBorder());
    }
  }
  
  /**
   * Ritorna il titolo del FormPanel.
   *
   * @return String
   */
  public
  String getTitle()
  {
    return sTitle;
  }
  
  /**
   * Imposta il tag. Tale stringa non ha alcun impatto grafico, ma puo' essere
   * utilizzata per memorizzare informazioni aggiuntive (es. un url)
   * Esso e' utilizzato nell'entity editor per fornire l'eventuale testo del tab
   * che occorre riportare nei dettagli alternativi.
   *
   * @param sTag String
   */
  public
  void setTag(String sTag)
  {
    this.sTag = sTag;
  }
  
  /**
   * Ritorna il tag. Tale stringa non ha alcun impatto grafico, ma puo' essere
   * utilizzata per memorizzare informazioni aggiuntive (es. un url)
   * Esso e' utilizzato nell'entity editor per fornire l'eventuale testo del tab
   * che occorre riportare nei dettagli alternativi.
   *
   * @return String
   */
  public
  String getTag()
  {
    return sTag;
  }
  
  /**
   * Imposta il FormPanel che eventualmente contiene l'istanza di tale FormPanel.
   *
   * @param oParentFormPanel FormPanel
   */
  public
  void setParentFormPanel(FormPanel oParentFormPanel)
  {
    this.oParentFormPanel = oParentFormPanel;
  }
  
  /**
   * Ottiene il FormPanel che eventualmente contiene l'istanza di tale FormPanel.
   *
   * @return FormPanel
   */
  public
  FormPanel getParentFormPanel()
  {
    return oParentFormPanel;
  }
  
  /**
   * Imposta il flag LabelOnTop.
   *
   * @param boLabelOnTop true = etichetta sopra, false = etichetta a sinistra
   */
  public
  void setLabelOnTop(boolean boLabelOnTop)
  {
    this.boLabelOnTop = boLabelOnTop;
  }
  
  /**
   * Ottiene il flag LabelOnTop
   *
   * @return boLabelOnTop true = etichetta sopra, false = etichetta a sinistra
   */
  public
  boolean isLabelOnTop()
  {
    return boLabelOnTop;
  }
  
  /**
   * Imposta il gap tra i componenti.
   *
   * @param iGap Gap
   */
  public
  void setGap(int iGap)
  {
    this.iGap = iGap;
  }
  
  /**
   * Ottiene il gap tra i componenti.
   *
   * @return gap
   */
  public
  int getGap()
  {
    return iGap;
  }
  
  /**
   * Imposta il colore dello sfondo dei componenti quando acquistano il focus.
   *
   * @param color Color
   */
  public
  void setBGColorOnFocus(Color color)
  {
    this.oBGColorOnFocus = color;
  }
  
  /**
   * Restituisce il colore dello sfondo dei componenti quando acquistano il focus.
   *
   * @return Color
   */
  public
  Color getBGColorOnFocus()
  {
    return oBGColorOnFocus;
  }
  
  /**
   * Imposta il colore del bordo che si evidenzia nelle caselle check quando
   * esse acquistano il focus.
   *
   * @param color Color
   */
  public
  void setColorCheckBoxFocus(Color color)
  {
    this.oColorCheckBoxFocus = color;
  }
  
  /**
   * Restituisce il colore del bordo che si evidenzia nelle caselle check quando
   * esse acquistano il focus.
   *
   * @return Color
   */
  public
  Color getColorCheckBoxFocus()
  {
    return oColorCheckBoxFocus;
  }
  
  /**
   * Imposta la lista dei campi obbligatori.
   *
   * @param oMandatoryFields Lista di campi obbligatori.
   */
  public
  void setMandatoryFields(List oMandatoryFields)
  {
    this.oMandatoryFields = oMandatoryFields;
    
    String sMandatories = ResourcesMgr.config.getProperty(sFORMPANEL_MANDATORIES);
    if(sMandatories != null && sMandatories.trim().length() == 7) {
      markLabelMandatoryFields(Color.decode(sMandatories));
    }
  }
  
  public
  Color getColorMandatories()
  {
    String sMandatories = ResourcesMgr.config.getProperty(sFORMPANEL_MANDATORIES);
    if(sMandatories != null && sMandatories.trim().length() == 7) {
      return Color.decode(sMandatories);
    }
    return UIManager.getColor("Label.foreground");
  }
  
  /**
   * Restituisce la lista dei campi obbligatori.
   *
   * @return List
   */
  public
  List getMandatoryFields()
  {
    return oMandatoryFields;
  }
  
  /**
   * Imposta il campo che riceve il focus di default.
   *
   * @param sId String
   */
  public
  void setDefaultFocus(String sId)
  {
    this.sIdFieldDefaultFocus = sId;
  }
  
  /**
   * Ritorna il campo che riceve il focus di default.
   *
   * @return String
   */
  public
  String getDefaultFocus()
  {
    return sIdFieldDefaultFocus;
  }
  
  /**
   * Restituisce il TabbedPane costruito.
   *
   * @return JTabbedPane
   */
  public
  JTabbedPane getTabbedPane()
  {
    return oTabbedPane;
  }
  
  /**
   * Restituisce l'indice del Tab in cui e' contenuto il componente.
   *
   * @param component Componente
   * @return indice del tab
   */
  public
  int getTabIndex(Component component)
  {
    if(component == null) return -1;
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      if(component == oFieldInfo.component) {
        return oFieldInfo.tabIndex;
      }
    }
    return -1;
  }
  
  /**
   * Restituisce l'indice del Tab in cui e' contenuto il campo specificato.
   *
   * @param sId Identificativo del campo
   * @return indice del tab
   */
  public
  int getTabIndex(String sId)
  {
    String sRId = resolveAlias(sId);
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) return -1;
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) return -1;
    return oFieldInfo.tabIndex;
  }
  
  /**
   * Imposta lo span di un campo.
   * 
   * @param sId Identificativo del campo
   * @param span Entita' dello span
   */
  public 
  void setSpanField(String sId, int span)
  {
    String sRId = resolveAlias(sId);
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) return;
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) return;
    if(span < 1) span = 1;
    oFieldInfo.span = span;
  }
  
  /**
   * Imposta la larghezza minima delle etichette (0 default).
   * 
   * @param iMinSizeLabel Larghezza minima etichette.
   */
  public
  void setMinSizeLabel(int iMinSizeLabel)
  {
    this.iMinSizeLabel = iMinSizeLabel;
  }
  
  /**
   * Aggiunge un oggetto FocusListener che intercetta gli eventi di focus di tutti i campi.
   * 
   * @param focusListener Oggetto FocusListener
   */
  public
  void addFocusListener(FocusListener focusListener)
  {
    super.addFocusListener(focusListener);
    
    if(focusListener == null) return;
    listFocusListener.add(focusListener);
  }
  
  /**
   * Rimuove il FocusListener.
   * 
   * @param focusListener Oggetto FocusListener
   */
  public
  void removeFocusListener(FocusListener focusListener)
  {
    super.removeFocusListener(focusListener);
    
    if(focusListener == null) return;
    listFocusListener.remove(focusListener);
  }
  
  /**
   * Inizializza il Form Panel.
   */
  public
  void init()
  {
    listTabs   = new ArrayList();
    mapTabs    = new HashMap();
    mapFields  = new HashMap();
    listFields = new ArrayList();
    listAlias  = new ArrayList();
    listLinkedFormPanel    = new ArrayList();
    oMasterLinkedFormPanel = null;
    iMasterLFP       = -1;
    iCurrentTabIndex = -1;
    
    iCase = ResourcesMgr.getIntProperty(sFORMPANEL_CASE, 0);
    boDefaultDate = ResourcesMgr.getBooleanProperty(sFORMPANEL_DEFAULTS_DATE, false);
    String sOnFocus = ResourcesMgr.config.getProperty(sFORMPANEL_ONFOCUS);
    if(sOnFocus != null && sOnFocus.length() == 7) {
      setBGColorOnFocus(Color.decode(sOnFocus));
    }
    
    if(oFormComponent != null) {
      this.remove(oFormComponent);
      oFormComponent = null;
    }
    
    JTextField jTextField = new JTextField();
    Dimension dimPreferredSize = jTextField.getPreferredSize();
    iPreferredHeightComboBox = dimPreferredSize.height;
  }
  
  /**
   * Imposta il case dei campi di testo.
   * 0 = nessuna modifica, 1 = upper, 2 = lower
   *
   * @param iCase Codice case
   */
  public
  void setCase(int iCase)
  {
    this.iCase = iCase;
    
    Iterator oItKeys = mapFields.keySet().iterator();
    while(oItKeys.hasNext()) {
      String sId = oItKeys.next().toString();
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof JPVEdit) {
        ((JPVEdit) oComponent).setCase(iCase);
      }
      else
      if(oComponent instanceof JTextFieldExt) {
        ((JTextFieldExt) oComponent).setCase(iCase);
      }
      else
      if(oComponent instanceof ADecodifiableComponent) {
        ((ADecodifiableComponent) oComponent).setCase(iCase);
      }
      else
      if(oComponent instanceof JTextNote) {
        ((JTextNote) oComponent).setCase(iCase);
      }
      else
      if(oComponent instanceof FormPanel) {
        ((FormPanel) oComponent).setCase(iCase);
      }
    }
  }
  
  /**
   * Imposta il case del campo specificato.
   * 0 = nessuna modifica, 1 = upper, 2 = lower
   *
   * @param sId   Identificativo del campo
   * @param iCase Codice case
   */
  public
  void setCase(String sId, int iCase)
  {
    Component oComponent = getComponent(sId);
    
    if(oComponent instanceof JPVEdit) {
      ((JPVEdit) oComponent).setCase(iCase);
    }
    else
    if(oComponent instanceof JTextFieldExt) {
      ((JTextFieldExt) oComponent).setCase(iCase);
    }
    else
    if(oComponent instanceof ADecodifiableComponent) {
      ((ADecodifiableComponent) oComponent).setCase(iCase);
    }
    else
    if(oComponent instanceof JTextNote) {
      ((JTextNote) oComponent).setCase(iCase);
    }
    else
    if(oComponent instanceof FormPanel) {
      ((FormPanel) oComponent).setCase(iCase);
    }
  }
  
  /**
   * Ottiene il case impostato.
   *
   * @return int
   */
  public
  int getCase()
  {
    return iCase;
  }
  
  /**
   * Aggiunge un Tab alla form.
   *
   * @param sGUIData Text|Description|Icon (Anteporre un & al mnemonico)
   */
  public
  void addTab(String sGUIData)
  {
    String sTabName = sGUIData;
    if(sTabName == null) {
      sTabName = "";
    }
    if(!listTabs.contains(sTabName)) {
      listTabs.add(sTabName);
      if(sTabName.length() > 0) {
        iCurrentTabIndex = listTabs.size() - 1;
      }
    }
    else {
      return;
    }
    List oRows = new ArrayList();
    mapTabs.put(sTabName, oRows);
    oRowsOfCurrentTab = oRows;
    oCurrentRow = null;
  }
  
  /**
   * Rimuove un Tab dalla form. Per l'effettiva applicazione occorre
   * invocare rebuild.
   *
   * @param sTheTabName Nome del Tab
   */
  public
  void removeTab(String sTheTabName)
  {
    listTabs.remove(sTheTabName);
    List oRows = (List) mapTabs.remove(sTheTabName);
    if(oRows == null) return;
    for(int i = 0; i < oRows.size(); i++) {
      List oRow = (List) oRows.get(i);
      if(oRow == null) continue;
      for(int j = 0; j < oRow.size(); j++) {
        FieldInfo fi = (FieldInfo) oRow.get(j);
        if(fi == null) continue;
        listFields.remove(fi.id);
        mapFields.remove(fi.id);
      }
    }
  }
  
  /**
   * Aggiunge una nuova riga.
   */
  public
  void addRow()
  {
    List oRow = new ArrayList();
    if(oRowsOfCurrentTab == null) {
      addTab(null);
    }
    oRowsOfCurrentTab.add(oRow);
    oCurrentRow = oRow;
  }
  
  /**
   * Aggiunge un campo alla riga corrente.
   * Quando si aggiunge un componente che non estende nessun componente
   * standard occorre implementare l'interfaccia IValuable per ottenere o
   * impostare i valori da FormPanel.
   *
   * @param sId Identificativo del campo
   * @param oComponent Componente
   * @return Component
   */
  public
  Component addComponent(String sId, Component oComponent)
  {
    return addComponent(sId, null, oComponent);
  }
  
  /**
   * Aggiunge un campo alla riga corrente.
   * Quando si aggiunge un componente che non estende nessun componente
   * standard occorre implementare l'interfaccia IValuable per ottenere o
   * impostare i valori da FormPanel.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param oComponent Componente (se null viene aggiunto un campo nascosto)
   * @return Component
   */
  public
  Component addComponent(String sId, String sLabel, Component oComponent)
  {
    if(oCurrentRow == null) addRow();
    if(mapFields.containsKey(sId)) {
      System.err.println("Field \"" + sId + "\" already added into FormPanel.");
    }
    Component oComponentToAdd = oComponent;
    if(oComponent == null) {
      oComponentToAdd = new HiddenComponent(sId);
    }
    if(oComponent instanceof ADecodifiableComponent) {
      ((ADecodifiableComponent) oComponent).setCase(iCase);
    }
    oComponentToAdd.setName(sId);
    FieldInfo fi = new FieldInfo(sId, sLabel, oComponentToAdd, iCurrentTabIndex);
    mapFields.put(sId, fi);
    listFields.add(fi);
    if(!(oComponentToAdd instanceof HiddenComponent)) {
      oCurrentRow.add(fi);
    }
    return oComponentToAdd;
  }
  
  /**
   * Aggiunge un alias all'identificativo del campo.
   *
   * @param sAlias String
   * @param sId String
   */
  public
  void putAlias(String sAlias, String sId)
  {
    Alias alias = new Alias(sAlias, sId);
    listAlias.add(alias);
  }
  
  /**
   * Rimuove un alias.
   *
   * @param sAlias String
   */
  public
  void removeAlias(String sAlias)
  {
    listAlias.remove(new Alias(sAlias));
  }
  
  /**
   * Rimuove tutti gli alias.
   */
  public
  void removeAllAlias()
  {
    listAlias.clear();
  }
  
  /**
   * Ritorna la lista di alias definita.
   *
   * @return List di oggetti Alias
   */
  public
  List getListAlias()
  {
    return listAlias;
  }
  
  /**
   * Aggiunge un oggetto FormPanel. Si utilizza per creare una sezione di campi.
   *
   * @param sId Identificativo del Form Panel
   * @param oFormPanel Oggetto FormPanel
   * @return Component
   */
  public
  Component addFormPanel(String sId, FormPanel oFormPanel)
  {
    if(oFormPanel != null) {
      oFormPanel.setParentFormPanel(this);
    }
    return addComponent(sId, null, oFormPanel);
  }
  
  /**
   * Aggiunge un oggetto FormPanel. Si utilizza per creare una sezione di campi.
   *
   * @param sId Identificativo del Form Panel
   * @param oFormPanel Oggetto FormPanel
   * @param boImportFieldAsAlias Se true gli identificativi dei campi vengono
   *                             importati come alias.
   * @return Component
   */
  public
  Component addFormPanel(String sId, FormPanel oFormPanel, boolean boImportFieldAsAlias)
  {
    if(oFormPanel != null) {
      oFormPanel.setParentFormPanel(this);
    }
    if(boImportFieldAsAlias) {
      List oListId = (List) oFormPanel.getListId();
      for(int i = 0; i < oListId.size(); i++) {
        String sIdField = (String) oListId.get(i);
        putAlias(sIdField, sId + "." + sIdField);
      }
    }
    return addComponent(sId, null, oFormPanel);
  }
  
  /**
   * Aggiunge un oggetto ADataPanel. Si utilizza per inserire un pannello personalizzato.
   *
   * @param sId Identificativo del Form Panel
   * @param oDataPanel Oggetto ADataPanel
   * @return Component
   */
  public
  Component addDataPanel(String sId, ADataPanel oDataPanel)
  {
    if(oDataPanel != null) {
      oDataPanel.setParentFormPanel(this);
    }
    return addComponent(sId, null, oDataPanel);
  }
  
  /**
   * Aggiunge un oggetto ADataPanel. Si utilizza per inserire un pannello personalizzato.
   *
   * @param sId Identificativo del Form Panel
   * @param sLabel Etichetta della cornice da applicare al DataPanel
   * @param oDataPanel Oggetto ADataPanel
   * @return Component
   */
  public
  Component addDataPanel(String sId, String sLabel, ADataPanel oDataPanel)
  {
    return addComponent(sId, sLabel, oDataPanel);
  }
  
  /**
   * Restituisce l'oggetto Form Panel identificato dal parametro.
   *
   * @param sId Identificativo del Form Panel
   * @return FormPanel
   */
  public
  FormPanel getFormPanel(String sId)
  {
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
    if(oFieldInfo == null) return null;
    Component oComponent = oFieldInfo.component;
    if(oComponent instanceof FormPanel) return (FormPanel) oComponent;
    return null;
  }
  
  /**
   * Aggiunge un campo Testo senza limiti.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFieldExt)
   */
  public
  Component addTextField(String sId, String sLabel)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setSelectionOnFocus(true);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo con un limite specificato di caratteri.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxLength Lunghezza massima consentita
   * @return Component (JTextFieldExt)
   */
  public
  Component addTextField(String sId, String sLabel, int iMaxLength)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setSelectionOnFocus(true);
    oComponent.setMaxLength(iMaxLength);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo con un limite specificato di caratteri e l'opzione case.<br>
   * Nel caso in cui iMaxLength=0 e iTheCase=0 viene creato un semplice JTextField
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxLength Lunghezza massima consentita
   * @param iTheCase Opzione Case
   * @return Component (JTextFieldExt o JTextField)
   */
  public
  Component addTextField(String sId, String sLabel, int iMaxLength, int iTheCase)
  {
    if(iMaxLength != 0 || iTheCase != 0) {
      JTextFieldExt oComponent = new JTextFieldExt() {
        public void processKeyEvent(KeyEvent ke) {
          if(ke.isAltDown()) { ke.setKeyChar('\0'); }
          super.processKeyEvent(ke);
        }
      };
      oComponent.setSelectionOnFocus(true);
      oComponent.setMaxLength(iMaxLength);
      oComponent.setCase(iCase);
      return addComponent(sId, sLabel, oComponent);
    }
    else {
      JTextField oComponent = new JTextField();
      return addComponent(sId, sLabel, oComponent);
    }
  }
  
  /**
   * Aggiunge un campo Testo con la digitazione di soli caratteri numerici.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxLength Lunghezza massima consentita
   * @return Component (JTextFieldExt)
   */
  public
  Component addTextNumericField(String sId, String sLabel, int iMaxLength)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_NUMERIC);
    oComponent.setSelectionOnFocus(true);
    oComponent.setMaxLength(iMaxLength);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo senza limiti con la digitazione di soli caratteri numerici.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFieldExt)
   */
  public
  Component addTextNumericField(String sId, String sLabel)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_NUMERIC);
    oComponent.setSelectionOnFocus(true);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo con la digitazione di soli caratteri alfabetici.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxLength Lunghezza massima consentita
   * @return Component (JTextFieldExt)
   */
  public
  Component addTextLettersField(String sId, String sLabel, int iMaxLength)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_LETTERS);
    oComponent.setSelectionOnFocus(true);
    oComponent.setMaxLength(iMaxLength);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo senza limiti con la digitazione di soli caratteri alfabetici.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFieldExt)
   */
  public
  Component addTextLettersField(String sId, String sLabel)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_LETTERS);
    oComponent.setSelectionOnFocus(true);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo nascosto.
   *
   * @param sId String
   * @return Component
   */
  public
  Component addHiddenField(String sId)
  {
    return addComponent(sId, null, null);
  }
  
  /**
   * Aggiunge un campo nascosto associando un testo descrittivo
   *
   * @param sId String
   * @param sLabel String
   * @return Component
   */
  public
  Component addHiddenField(String sId, String sLabel)
  {
    return addComponent(sId, sLabel, null);
  }
  
  /**
   * Aggiunge un campo Testo con caratteri nascosti senza limiti. Utile per l'introduzione di password.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPasswordField)
   */
  public
  Component addPasswordField(String sId, String sLabel)
  {
    JPasswordField oComponent = new JPasswordField();
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo per la selezione di un file.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFile)
   */
  public
  Component addFileField(String sId, String sLabel)
  {
    JTextFile oComponent = new JTextFile();
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo per la selezione di un file.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param sFilter Filtro per la selezione del file (es. "jpg|bmp|gif");
   * @return Component (JTextFile)
   */
  public
  Component addFileField(String sId, String sLabel, String sFilter)
  {
    JTextFile oComponent = new JTextFile();
    oComponent.setFilter(sFilter);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo per la selezione di una directory.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFile)
   */
  public
  Component addDirectoryField(String sId, String sLabel)
  {
    JTextFile oComponent = new JTextFile();
    oComponent.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo statico.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPVStaticText)
   */
  public
  Component addStaticTextField(String sId, String sLabel)
  {
    JPVStaticText oComponent = new JPVStaticText();
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo statico.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param colForeground Colore del carattere
   * @param colBackground Colore di sfondo
   * @return Component (JPVStaticText)
   */
  public
  Component addStaticTextField(String sId, String sLabel, Color colForeground, Color colBackground)
  {
    JPVStaticText oComponent = new JPVStaticText();
    if(colForeground != null) {
      oComponent.setForeground(colForeground);
    }
    if(colBackground != null) {
      oComponent.setOpaque(true);
      oComponent.setBackground(colBackground);
    }
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Testo con una maschera di inserimento.
   * &#64; = carattere, # = numero, * = numero
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxLength Lunghezza massima consentita
   * @param sMask Maschera di inserimento.
   * @return Component (JPVMask)
   */
  public
  Component addMaskTextField(String sId, String sLabel, int iMaxLength, String sMask)
  {
    JPVMask oComponent = new JPVMask(sMask) {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setMaxLength(iMaxLength);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Note utilizzato per un testo con molti caratteri.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iRows Righe del campo note
   * @return Component (JTextNote)
   */
  public
  Component addNoteField(String sId, String sLabel, int iRows)
  {
    JTextNote oComponent = new JTextNote(iRows);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Note utilizzato per un testo con molti caratteri.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iRows Righe del campo note
   * @param iMaxLength Massimo numero di caratteri consentito
   * @return Component (JTextNote)
   */
  public
  Component addNoteField(String sId, String sLabel, int iRows, int iMaxLength)
  {
    JTextNote oComponent = new JTextNote(iRows, iMaxLength, iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Note utilizzato per un testo con molti caratteri.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iRows Righe del campo note
   * @param iMaxLength Massimo numero di caratteri consentito
   * @param iTheCase Case forzato dei caratteri
   * @return Component (JTextNote)
   */
  public
  Component addNoteField(String sId, String sLabel, int iRows, int iMaxLength, int iTheCase)
  {
    JTextNote oComponent = new JTextNote(iRows, iMaxLength, iTheCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Note utilizzato per un testo con molti caratteri
   * e con la possibilita' di utilizzare gli stili (bold, italic, underline).
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iRows Righe del campo note
   * @return Component (JRichTextNote)
   */
  public
  Component addRichNoteField(String sId, String sLabel, int iRows)
  {
    JRichTextNote oComponent = new JRichTextNote(iRows);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Note utilizzato per un testo con molti caratteri
   * e con la possibilita' di utilizzare gli stili (bold, italic, underline).
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iRows Righe del campo note
   * @param urlBackground URL immagine di sfondo
   * @param iMarginLeft Margine sinistro (espresso in px)
   * @return Component (JRichTextNote)
   */
  public
  Component addRichNoteField(String sId, String sLabel, int iRows, URL urlBackground, int iMarginLeft)
  {
    JRichTextNote oComponent = new JRichTextNote(iRows, urlBackground, iMarginLeft);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Numerico che accetta solo numeri interi.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPVNumeric)
   */
  public
  Component addIntegerField(String sId, String sLabel)
  {
    JPVNumeric oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(false);
    oComponent.setValueType(java.sql.Types.INTEGER);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Numerico che accetta solo numeri interi con un limitato numero di cifre.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxDigits Massimo numero di cifre
   * @return Component (JPVNumeric)
   */
  public
  Component addIntegerField(String sId, String sLabel, int iMaxDigits)
  {
    JPVNumeric oComponent = new JPVNumeric();
    oComponent.setMaxIntegers(iMaxDigits);
    oComponent.setEnableFloatPoint(false);
    oComponent.setValueType(java.sql.Types.INTEGER);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo con lo spinner.
   *
   * @param sId String
   * @param sLabel String
   * @return Component (JSpinner)
   */
  public
  Component addSpinnerField(String sId, String sLabel)
  {
    JSpinner oComponent = new JSpinner();
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Numerico che accetta numeri decimali.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPVNumeric)
   */
  public
  Component addDoubleField(String sId, String sLabel)
  {
    JPVNumeric oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(true);
    oComponent.setValueType(java.sql.Types.DOUBLE);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Numerico che accetta numeri decimali.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param sSeparator Separatore decimale
   * @return Component (JPVNumeric)
   */
  public
  Component addDoubleField(String sId, String sLabel, String sSeparator)
  {
    JPVNumeric oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(true);
    oComponent.setValueType(java.sql.Types.DOUBLE);
    oComponent.setDecimalSeparator(sSeparator);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Numerico che accetta numeri decimali.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param iMaxIntegers Massimo numero di cifre della parte intera
   * @param iMaxDecimals Massimo numero di cifre della parte decimale
   * @return Component (JPVNumeric)
   */
  public
  Component addDoubleField(String sId, String sLabel, int iMaxIntegers, int iMaxDecimals)
  {
    JPVNumeric oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(true);
    oComponent.setMaxIntegers(iMaxIntegers);
    oComponent.setMaxDecimals(iMaxDecimals);
    oComponent.setValueType(java.sql.Types.DOUBLE);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Numerico che accetta numeri decimali.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param sSeparator Separatore decimale
   * @param iMaxIntegers Massimo numero di cifre della parte intera
   * @param iMaxDecimals Massimo numero di cifre della parte decimale
   * @return Component (JPVNumeric)
   */
  public
  Component addDoubleField(String sId, String sLabel, String sSeparator, int iMaxIntegers, int iMaxDecimals)
  {
    JPVNumeric oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(true);
    oComponent.setMaxIntegers(iMaxIntegers);
    oComponent.setMaxDecimals(iMaxDecimals);
    oComponent.setValueType(java.sql.Types.DOUBLE);
    oComponent.setDecimalSeparator(sSeparator);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Importo.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPVCurrency)
   */
  public
  Component addCurrencyField(String sId, String sLabel)
  {
    JPVCurrency oComponent = new JPVCurrency();
    oComponent.setSelectionOnFocus(1);
    oComponent.setSymbol("");
    oComponent.setThousandSeparator(".");
    oComponent.setDecimalSeparator(",");
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Decimal.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFieldExt)
   */
  public
  Component addDecimalField(String sId, String sLabel)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setHorizontalAlignment(JTextField.RIGHT);
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_DECIMAL);
    oComponent.setSelectionOnFocus(true);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Decimal con precisione specificata.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param decimal Numero di decimali
   * @return Component (JTextFieldExt)
   */
  public
  Component addDecimalField(String sId, String sLabel, int decimal)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setHorizontalAlignment(JTextField.RIGHT);
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_DECIMAL);
    oComponent.setSelectionOnFocus(true);
    oComponent.setCase(iCase);
    oComponent.setDecimal(decimal);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo per le quantita' o valori interi in generale.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JTextFieldExt)
   */
  public
  Component addQuantityField(String sId, String sLabel)
  {
    JTextFieldExt oComponent = new JTextFieldExt() {
      public void processKeyEvent(KeyEvent ke) {
        if(ke.isAltDown()) { ke.setKeyChar('\0'); }
        super.processKeyEvent(ke);
      }
    };
    oComponent.setHorizontalAlignment(JTextField.RIGHT);
    oComponent.setCharsAllowed(JTextFieldExt.iCHARS_QUANTITY);
    oComponent.setSelectionOnFocus(true);
    oComponent.setCase(iCase);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Importo.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param sThousandSeparator Separatore delle migliaia
   * @param sDecimalSeparator Separatore decimale
   * @return Component (JPVCurrency)
   */
  public
  Component addCurrencyField(String sId, String sLabel, String sThousandSeparator, String sDecimalSeparator)
  {
    JPVCurrency oComponent = new JPVCurrency();
    oComponent.setSelectionOnFocus(1);
    oComponent.setSymbol("");
    oComponent.setThousandSeparator(sThousandSeparator);
    oComponent.setDecimalSeparator(sDecimalSeparator);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Importo.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param sThousandSeparator Separatore delle migliaia
   * @param sDecimalSeparator Separatore decimale
   * @param sSymbol Simbolo della valuta
   * @return Component (JPVCurrency)
   */
  public
  Component addCurrencyField(String sId, String sLabel,
    String sThousandSeparator, String sDecimalSeparator,
    String sSymbol)
  {
    JPVCurrency oComponent = new JPVCurrency();
    oComponent.setSelectionOnFocus(1);
    oComponent.setSymbol(sSymbol);
    oComponent.setThousandSeparator(sThousandSeparator);
    oComponent.setDecimalSeparator(sDecimalSeparator);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo Data.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPVDatePlus)
   */
  public
  Component addDateField(String sId, String sLabel)
  {
    JPVDatePlus oComponent = new JPVDatePlus();
    // oComponent.setSelectionOnFocus(1);
    oComponent.setFormat(JPVDatePlus.DMY);
    JPVCalendar oCalendar = oComponent.getCalendarObject();
    oCalendar.setLocaleStrings(true);
    Component oResult = addComponent(sId, sLabel, oComponent);
    if(boDefaultDate) {
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      oFieldInfo.defaultValue = new Date();
    }
    return oResult;
  }
  
  /**
   * Aggiunge un campo Data.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param oLowerLimit Limite inferiore (null = nessun limite)
   * @return Component (JPVDatePlus)
   */
  public
  Component addDateField(String sId, String sLabel, Date oLowerLimit)
  {
    JPVDatePlus oComponent = new JPVDatePlus();
    // oComponent.setSelectionOnFocus(1);
    oComponent.setFormat(JPVDatePlus.DMY);
    JPVCalendar oCalendar = oComponent.getCalendarObject();
    oCalendar.setLocaleStrings(true);
    if(oLowerLimit != null) {
      oComponent.setLowerLimit(oLowerLimit);
      oComponent.getCalendarObject().setLowerLimit(oLowerLimit);
    }
    Component oResult = addComponent(sId, sLabel, oComponent);
    if(boDefaultDate) {
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      oFieldInfo.defaultValue = new Date();
    }
    return oResult;
  }
  
  /**
   * Aggiunge un campo Data.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param oLowerLimit Limite inferiore (null = nessun limite)
   * @param oUpperLimit Limite superiore (null = nessnu limite)
   * @return Component (JPVDatePlus)
   */
  public
  Component addDateField(String sId, String sLabel, Date oLowerLimit, Date oUpperLimit)
  {
    JPVDatePlus oComponent = new JPVDatePlus();
    // oComponent.setSelectionOnFocus(1);
    oComponent.setFormat(JPVDatePlus.DMY);
    JPVCalendar oCalendar = oComponent.getCalendarObject();
    oCalendar.setLocaleStrings(true);
    if(oLowerLimit != null) {
      oComponent.setLowerLimit(oLowerLimit);
      oComponent.getCalendarObject().setLowerLimit(oLowerLimit);
    }
    if(oUpperLimit != null) {
      oComponent.setUpperLimit(oUpperLimit);
      oComponent.getCalendarObject().setUpperLimit(oUpperLimit);
    }
    Component oResult = addComponent(sId, sLabel, oComponent);
    if(boDefaultDate) {
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      oFieldInfo.defaultValue = new Date();
    }
    return oResult;
  }
  
  /**
   * Aggiunge un campo Ora.
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JPVTime)
   */
  public
  Component addTimeField(String sId, String sLabel)
  {
    JPVTime oComponent = new JPVTime();
    oComponent.setTwelveHours(false);
    oComponent.setShowSeconds(false);
    oComponent.setLeadingZero(true);
    oComponent.setBlankForNull(true);
    oComponent.setTime(null);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo booleano. (CheckBox)
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @return Component (JCheckBox)
   */
  public
  Component addBooleanField(String sId, String sLabel)
  {
    JCheckBox oComponent = new JCheckBox();
    if(sLabel != null) {
      int iSep = sLabel.indexOf('|');
      if(iSep >= 0 && iSep < sLabel.length() - 1) {
        oComponent.setText(sLabel.substring(iSep + 1));
        sLabel = sLabel.substring(0, iSep);
      }
    }
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo vuoto.
   *
   * @return Component (JPanel)
   */
  public
  Component addBlankField()
  {
    JPanel oComponent = new JPanel();
    String sId = "#" + oComponent.hashCode();
    return addComponent(sId, null, oComponent);
  }
  
  /**
   * Aggiunge un campo opzioni. (ComboBox)
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param vItems Vector
   * @return Component (JComboBox)
   */
  public
  Component addOptionsField(String sId, String sLabel, Vector vItems)
  {
    JComboBox oComponent = null;
    if(vItems == null) {
      oComponent = new JComboBox(new Vector());
    }
    else {
      oComponent = new JComboBox(vItems);
    }
    if(iPreferredHeightComboBox > 0) {
      oComponent.setPreferredSize(new Dimension(0, iPreferredHeightComboBox));
    }
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Aggiunge un campo opzioni. (ComboBox)
   *
   * @param sId Identificativo del campo
   * @param sLabel Etichetta del campo
   * @param vItems Vector
   * @param boEditable Flag editabile
   * @return Component
   */
  public
  Component addOptionsField(String sId, String sLabel, Vector vItems,
    boolean boEditable)
  {
    JComboBox oComponent = null;
    if(vItems == null) {
      oComponent = new JComboBox(new Vector());
    }
    else {
      oComponent = new JComboBox(vItems);
    }
    oComponent.setEditable(boEditable);
    if(boEditable) {
      Component oEditorComponent = oComponent.getEditor().getEditorComponent();
      Font fontTextField = UIManager.getFont("TextField.font");
      if(fontTextField != null) {
        oEditorComponent.setFont(fontTextField);
      }
    }
    if(iPreferredHeightComboBox > 0) {
      oComponent.setPreferredSize(new Dimension(0, iPreferredHeightComboBox));
    }
    return addComponent(sId, sLabel, oComponent);
  }
  
  /**
   * Popola il campo opzioni specificato dall'identificativo con la lista
   * di elementi passata nel secondo parametro.
   *
   * @param sId Identificativo del campo
   * @param listItems Lista di elementi
   */
  public
  void setOptionsItems(String sId, List listItems)
  {
    Component oComponent = getComponent(sId);
    if(oComponent instanceof JComboBox) {
      JComboBox jcb = (JComboBox) oComponent;
      jcb.removeAllItems();
      if(listItems != null) {
        for(int i = 0; i < listItems.size(); i++) {
          jcb.addItem(listItems.get(i));
        }
      }
    }
  }
  
  /**
   * Popola il campo opzioni specificato dall'identificativo con la lista
   * di elementi passata nel secondo parametro.
   *
   * @param sId Identificativo del campo
   * @param listItems Lista di elementi
   * @param boLeaveSelection true se occorre conservare la selezione
   */
  public
  void setOptionsItems(String sId, List listItems, boolean boLeaveSelection)
  {
    Component oComponent = getComponent(sId);
    if(oComponent instanceof JComboBox) {
      JComboBox jcb = (JComboBox) oComponent;
      Object oSelectedItem = jcb.getSelectedItem();
      jcb.removeAllItems();
      if(listItems != null) {
        for(int i = 0; i < listItems.size(); i++) {
          jcb.addItem(listItems.get(i));
        }
      }
      if(boLeaveSelection) {
        jcb.setSelectedItem(oSelectedItem);
      }
    }
  }
  
  /**
   * Crea una icona affianco al componente che permette di visualizzare il testo di aiuto.
   *
   * @param sId String
   * @param sText String
   */
  public
  void setHelpText(String sId, String sText)
  {
    Component oComponent = getComponent(sId);
    if(oComponent == null) return;
    Container cntParent = oComponent.getParent();
    if(cntParent == null) {
      throw new RuntimeException("FormPanel.setHelpText invoked before FormPanel.build");
    }
    LayoutManager lm = cntParent.getLayout();
    if(lm instanceof BorderLayout) {
      JLabel jlHelp = new JLabel(ResourcesMgr.getSmallImageIcon(IConstants.sICON_HELP));
      jlHelp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      jlHelp.addMouseListener(new HelpLabel_MouseListener(sText));
      cntParent.add(jlHelp, BorderLayout.EAST);
    }
  }
  
  /**
   * Crea una icona affianco al componente che permette di visualizzare una pagina di aiuto.
   *
   * @param sId String
   * @param urlPage URL della pagina di aiuto
   */
  public
  void setHelpURL(String sId, URL urlPage)
  {
    Component oComponent = getComponent(sId);
    if(oComponent == null) return;
    Container cntParent = oComponent.getParent();
    if(cntParent == null) {
      throw new RuntimeException("FormPanel.setHelpText invoked before FormPanel.build");
    }
    LayoutManager lm = cntParent.getLayout();
    if(lm instanceof BorderLayout) {
      JLabel jlHelp = new JLabel(ResourcesMgr.getSmallImageIcon(IConstants.sICON_HELP));
      jlHelp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      jlHelp.addMouseListener(new HelpLabel_MouseListener(urlPage));
      cntParent.add(jlHelp, BorderLayout.EAST);
    }
  }
  
  /**
   * Crea una icona affianco al componente che permette di gestire una richiesta di aiuto.
   *
   * @param sId String
   * @param actionListener Oggetto che implementa ActionListener
   */
  public
  void setHelpAction(String sId, ActionListener actionListener)
  {
    Component oComponent = getComponent(sId);
    if(oComponent == null) return;
    Container cntParent = oComponent.getParent();
    if(cntParent == null) {
      throw new RuntimeException("FormPanel.setHelpText invoked before FormPanel.build");
    }
    LayoutManager lm = cntParent.getLayout();
    if(lm instanceof BorderLayout) {
      JLabel jlHelp = new JLabel(ResourcesMgr.getSmallImageIcon(IConstants.sICON_HELP));
      jlHelp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      jlHelp.addMouseListener(new HelpLabel_MouseListener(actionListener));
      cntParent.add(jlHelp, BorderLayout.EAST);
    }
  }
  
  /**
   * Aggiunge un form panel collegato. Tale metodo permette di ottenere o di
   * impostare i valori di piu' form panel attraverso uno solo.
   *
   * @param oFormPanel FormPanel
   */
  public
  void addLinkedFormPanel(FormPanel oFormPanel)
  {
    if(oFormPanel == null) return;
    listLinkedFormPanel.add(oFormPanel);
  }
  
  /**
   * Il form panel indicato come master assume priorita' nel recupero
   * dei valori eseguito tramite i metodi getValue o getValues.
   *
   * @param iIndex Indice della lista di form panel collegati. Se < 0
   *               nessun form panel collegato e' master.
   */
  public
  void setMasterLinkedFormPanel(int iIndex)
  {
    if(iIndex < 0 || iIndex >= listLinkedFormPanel.size()) {
      oMasterLinkedFormPanel = null;
      iMasterLFP = -1;
      return;
    }
    
    iMasterLFP = iIndex;
    oMasterLinkedFormPanel = (FormPanel) listLinkedFormPanel.get(iIndex);
  }
  
  /**
   * Rimuove un form panel collegato.
   *
   * @param oFormPanel FormPanel
   */
  public
  void removeLinkedFormPanel(FormPanel oFormPanel)
  {
    if(oFormPanel != null && oFormPanel.equals(oMasterLinkedFormPanel)) {
      oMasterLinkedFormPanel = null;
      iMasterLFP = -1;
    }
    listLinkedFormPanel.remove(oFormPanel);
  }
  
  /**
   * Rimuove tutti i form panel collegati.
   */
  public
  void removeAllLinkedFormPanel()
  {
    listLinkedFormPanel.clear();
    oMasterLinkedFormPanel = null;
    iMasterLFP = -1;
  }
  
  /**
   * Ritorna il form panel collegato.
   *
   * @param i int
   * @return FormPanel
   */
  public
  FormPanel getLinkedFormPanel(int i)
  {
    return (FormPanel) listLinkedFormPanel.get(i);
  }
  
  /**
   * Ritorna il numero di form panel collegati.
   *
   * @return int
   */
  public
  int getCountLinkedFormPanel()
  {
    return listLinkedFormPanel.size();
  }
  
  /**
   * Ritorna il master form panel collegato.
   *
   * @return FormPanel se non vi e' un form panel master ritorna null.
   */
  public
  FormPanel getMasterLinkedFormPanel()
  {
    return oMasterLinkedFormPanel;
  }
  
  /**
   * Ritorna il componente associato al campo.
   *
   * @param sId Identificativo del campo
   * @return Component
   */
  public
  Component getComponent(String sId)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.getComponent(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return null;
    }
    Component oComponent = oFieldInfo.component;
    
    if(oComponent instanceof JTextNote) {
      return oComponent;
    }
    else
    if(oComponent instanceof JScrollPane) {
      Component[] oComponents = ((JScrollPane) oComponent).getViewport().getComponents();
      if(oComponents != null && oComponents.length > 0) {
        return oComponents[0];
      }
    }
    
    return oComponent;
  }
  
  /**
   * Ritorna il testo dell'etichetta di un campo.
   *
   * @param sId Identificativo del campo
   * @return String Testo etichetta del campo
   */
  public
  String getLabel(String sId)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.getLabel(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return null;
    }
    return oFieldInfo.label;
  }
  
  /**
   * Ritorna la JLabel dell'etichetta di un campo.
   *
   * @param sId Identificativo del campo
   * @return JLabel componente relativo all'etichetta
   */
  public
  JLabel getJLabel(String sId)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.getJLabel(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return null;
    }
    return oFieldInfo.jlabel;
  }
  
  /**
   * Imposta il valore di un campo
   *
   * @param sId Identificativo del campo
   * @param oValue Valore
   */
  public
  void setValue(String sId, Object oValue)
  {
    for(int i = 0; i < listLinkedFormPanel.size(); i++) {
      FormPanel oFormPanel = (FormPanel) listLinkedFormPanel.get(i);
      oFormPanel.setValue(sId, oValue);
    }
    
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        oFormPanel.setValue(sIdField, oValue);
        return;
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return;
    }
    Component oComponent = oFieldInfo.component;
    
    setValue(oComponent, oValue);
    
    if(!boDontNotifyChange) notifyChange();
  }
  
  /**
   * Ritorna il valore di un campo.
   *
   * @param sId Identificativo del campo
   * @return Object Valore
   */
  public
  Object getValue(String sId)
  {
    if(oMasterLinkedFormPanel != null) {
      Object oResult = oMasterLinkedFormPanel.getValue(sId);
      if(oResult != null) {
        return oResult;
      }
    }
    
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.getValue(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      for(int i = 0; i < listLinkedFormPanel.size(); i++) {
        FormPanel oFormPanel = (FormPanel) listLinkedFormPanel.get(i);
        Object oValue = oFormPanel.getValue(sId);
        if(oValue != null) return oValue;
      }
      return null;
    }
    
    Component oComponent = oFieldInfo.component;
    
    return getValue(oComponent);
  }
  
  /**
   * Ritorna il contenuto di un campo.
   *
   * @param sId Identificativo del campo
   * @return Object contenuto
   */
  public
  Object getContent(String sId)
  {
    if(oMasterLinkedFormPanel != null) {
      Object oResult = oMasterLinkedFormPanel.getContent(sId);
      if(oResult != null) {
        return oResult;
      }
    }
    
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.getContent(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      for(int i = 0; i < listLinkedFormPanel.size(); i++) {
        FormPanel oFormPanel = (FormPanel) listLinkedFormPanel.get(i);
        Object oContent = oFormPanel.getContent(sId);
        if(oContent != null) return oContent;
      }
      return null;
    }
    
    Component oComponent = oFieldInfo.component;
    
    return getContent(oComponent);
  }
  
  /**
   * Imposta i valori dei campi.
   * Se si passa null viene eseguito reset().
   *
   * @param oValues Oggetto Map con i valori dei campi
   */
  public
  void setValues(Map oValues)
  {
    if(oValues == null) {
      reset();
      return;
    }
    
    // Si evita di notificare ad ogni setValue
    this.boDontNotifyChange = true;
    
    Iterator oItEntry = oValues.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sId = entry.getKey().toString();
      setValue(sId, entry.getValue());
    }
    
    this.boDontNotifyChange = false;
    
    notifyChange();
  }
  
  /**
   * Imposta il contenuto dei campi.
   * Se si passa null viene eseguito reset().
   *
   * @param oContents Oggetto Map con i contenuti dei campi
   */
  public
  void setContents(Map oContents)
  {
    setValues(oContents);
  }
  
  /**
   * Ritorna un oggetto Map con i valori dei campi.
   *
   * @return Oggetto Map con i valori dei campi
   */
  public
  Map<String, Object> getValues()
  {
    Map<String, Object> result = new HashMap<String, Object>();
    
    for(int i = 0; i < listLinkedFormPanel.size(); i++) {
      FormPanel oFormPanel = (FormPanel) listLinkedFormPanel.get(i);
      if(i == iMasterLFP) continue;
      result.putAll(oFormPanel.getValues());
    }
    
    Iterator oItEntry = mapFields.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sId = entry.getKey().toString();
      FieldInfo oFieldInfo = (FieldInfo) entry.getValue();
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        Map oValuesOfFormPanel = ((FormPanel) oComponent).getValues();
        Iterator oItEntryVFP = oValuesOfFormPanel.entrySet().iterator();
        while(oItEntryVFP.hasNext()) {
          Map.Entry entryVFP = (Map.Entry) oItEntryVFP.next();
          String sIdField = entryVFP.getKey().toString();
          String sAlias = getAliasById(sId + "." + sIdField);
          result.put(sAlias, entryVFP.getValue());
        }
      }
      else {
        String sAlias = getAliasById(sId);
        result.put(sAlias, getValue(sId));
      }
    }
    
    if(oMasterLinkedFormPanel != null) {
      result.putAll(oMasterLinkedFormPanel.getValues());
    }
    
    return result;
  }
  
  /**
   * Ritorna un oggetto Map con il contenuto dei campi.
   * ATTENZIONE: se occorre recuperare i valori usare getValues().
   *
   * @return Oggetto Map con i valori dei campi
   */
  public
  Map<String, Object> getContents()
  {
    Map<String, Object> result = new HashMap<String, Object>();
    
    for(int i = 0; i < listLinkedFormPanel.size(); i++) {
      FormPanel oFormPanel = (FormPanel) listLinkedFormPanel.get(i);
      if(i == iMasterLFP) continue;
      result.putAll(oFormPanel.getContents());
    }
    
    Iterator oItEntry = mapFields.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sId = entry.getKey().toString();
      FieldInfo oFieldInfo = (FieldInfo) entry.getValue();
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        Map oContentsOfFormPanel = ((FormPanel) oComponent).getContents();
        Iterator oItEntryCFP = oContentsOfFormPanel.entrySet().iterator();
        while(oItEntryCFP.hasNext()) {
          Map.Entry entryCFP = (Map.Entry) oItEntryCFP.next();
          String sIdField = entryCFP.getKey().toString();
          String sAlias = getAliasById(sId + "." + sIdField);
          result.put(sAlias, entryCFP.getValue());
        }
      }
      else {
        String sAlias = getAliasById(sId);
        result.put(sAlias, getContent(sId));
      }
    }
    
    if(oMasterLinkedFormPanel != null) {
      result.putAll(oMasterLinkedFormPanel.getContents());
    }
    
    return result;
  }
  
  /**
   * Imposta i valori attraverso una lista.
   *
   * @param oValues Valori ordinati
   */
  public
  void setListValues(List oValues)
  {
    setListValues(oValues, 0);
  }
  
  /**
   * Imposta i valori attraverso una lista a partire da un indice.
   *
   * @param oValues Valori ordinati
   * @param iStartIndex Indice di partenza
   */
  public
  void setListValues(List oValues, int iStartIndex)
  {
    if(oValues == null || oValues.size() <= iStartIndex) return;
    int iSizeValues = oValues.size();
    for(int i = iStartIndex; i < listFields.size(); i++) {
      if(i >= iSizeValues) break;
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      Component oComponent = oFieldInfo.component;
      Object oValue = oValues.get(i);
      if(oComponent instanceof FormPanel) {
        ((FormPanel) oComponent).setListValues(oValues, i);
      }
      else {
        setValue(oComponent, oValue);
      }
    }
  }
  
  /**
   * Ritorna un oggetto List con i valori dei campi ordinati per inserimento.
   *
   * @return List
   */
  public
  List<Object> getListValues()
  {
    List<Object> oResult = new ArrayList<Object>(listFields.size());
    
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        oResult.addAll(((FormPanel) oComponent).getListValues());
      }
      else {
        oResult.add(getValue(oComponent));
      }
    }
    
    return oResult;
  }
  
  /**
   * Ritorna un oggetto List con i componenti dei campi ordinati per inserimento.
   *
   * @return List
   */
  public
  List<Component> getListComponents()
  {
    List<Component> oResult = new ArrayList<Component>(listFields.size());
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      oResult.add(oFieldInfo.component);
    }
    return oResult;
  }
  
  /**
   * Ritorna un oggetto List con gli identificativi dei campi ordinati per inserimento.
   *
   * @return List
   */
  public
  List<String> getListId()
  {
    List<String> oResult = new ArrayList<String>(listFields.size());
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        oResult.addAll(((FormPanel) oComponent).getListId());
      }
      else {
        oResult.add(oFieldInfo.id);
      }
    }
    return oResult;
  }
  
  /**
   * Ritorna un oggetto List con le etichette dei campi ordinati per inserimento.
   *
   * @return List
   */
  public
  List<String> getListLabel()
  {
    List<String> oResult = new ArrayList<String>(listFields.size());
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        oResult.addAll(((FormPanel) oComponent).getListLabel());
      }
      else {
        oResult.add(oFieldInfo.label);
      }
    }
    return oResult;
  }
  
  /**
   * Ritorna un oggetto List con le JLabel dei campi ordinati per inserimento.
   *
   * @return List
   */
  public
  List<JLabel> getListJLabel()
  {
    List<JLabel> oResult = new ArrayList<JLabel>(listFields.size());
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        oResult.addAll(((FormPanel) oComponent).getListJLabel());
      }
      else {
        oResult.add(oFieldInfo.jlabel);
      }
    }
    return oResult;
  }
  
  /**
   * Imposta il valore di default di un campo.
   *
   * @param sId Identificativo del campo
   * @param oValue Valore di default
   */
  public
  void setDefaultValue(String sId, Object oValue)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        oFormPanel.setDefaultValue(sIdField, oValue);
        return;
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return;
    }
    oFieldInfo.defaultValue = oValue;
  }
  
  /**
   * Ritorna il valore di default di un campo.
   *
   * @param sId Identificativo del campo
   * @return Object Valore di default
   */
  public
  Object getDefaultValue(String sId)
  {
    int iFirstPoint = sId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sId.length() - 1) {
      String sIdForm = sId.substring(0, iFirstPoint);
      String sIdField = sId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.getDefaultValue(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
    if(oFieldInfo == null) {
      return null;
    }
    return oFieldInfo.defaultValue;
  }
  
  /**
   * Imposta i valori di default.
   *
   * @param oDefaultValues Oggetto Map con i valori di default.
   */
  public
  void setDefaultValues(Map oDefaultValues)
  {
    if(oDefaultValues == null) {
      return;
    }
    Iterator oItEntry = oDefaultValues.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sId = entry.getKey().toString();
      setDefaultValue(sId, entry.getValue());
    }
  }
  
  /**
   * Ritorna un oggetto Map con i valori di default.
   *
   * @return Map
   */
  public
  Map getDefaultValues()
  {
    Map oResult = new HashMap();
    
    Iterator oItEntry = mapFields.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sId = entry.getKey().toString();
      FieldInfo oFieldInfo = (FieldInfo) entry.getValue();
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        Map oDefValuesOfFormPanel = ((FormPanel) oComponent).getDefaultValues();
        Iterator oItEntryDVFP = oDefValuesOfFormPanel.entrySet().iterator();
        while(oItEntryDVFP.hasNext()) {
          Map.Entry entryDVFP = (Map.Entry) oItEntryDVFP.next();
          String sIdField = entryDVFP.getKey().toString();
          String sAlias = getAliasById(sId + "." + sIdField);
          oResult.put(sAlias, entryDVFP.getValue());
        }
      }
      else {
        String sAlias = getAliasById(sId);
        oResult.put(sAlias, oFieldInfo.defaultValue);
      }
    }
    
    return oResult;
  }
  
  /**
   * Imposta il flag di abilitazione di un campo.
   *
   * @param sId Identificativo del campo
   * @param boEnabled Flag di abilitazione
   */
  public
  void setEnabled(String sId, boolean boEnabled)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        oFormPanel.setEnabled(sIdField, boEnabled);
        return;
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return;
    }
    oFieldInfo.component.setEnabled(boEnabled);
  }
  
  /**
   * Imposta il flag di visibilita' di un campo.
   *
   * @param sId Identificativo del campo
   * @param boVisible Flag di visibilita'
   */
  public
  void setVisible(String sId, boolean boVisible)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        oFormPanel.setVisible(sIdField, boVisible);
        return;
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return;
    }
    oFieldInfo.component.setVisible(boVisible);
    if(oFieldInfo.jlabel != null) {
      oFieldInfo.jlabel.setVisible(boVisible);
    }
  }
  
  /**
   * Imposta il flag di abilitazione dell'intero form.
   *
   * @param boEnabled Flag di abilitazione
   */
  public
  void setEnabled(boolean boEnabled)
  {
    Iterator oItKeys = mapFields.keySet().iterator();
    while(oItKeys.hasNext()) {
      String sId = oItKeys.next().toString();
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      oFieldInfo.component.setEnabled(boEnabled);
    }
    super.setEnabled(boEnabled);
  }
  
  /**
   * Imposta il flag di abilitazione soltanto al container.
   *
   * @param boEnabled Flag di abilitazione
   */
  public
  void setEnabledPanelOnly(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
  }
  
  /**
   * Seleziona il primo tab.
   */
  public
  void selectFirstTab()
  {
    if(oTabbedPane != null && oTabbedPane.getTabCount() > 1) {
      oTabbedPane.setSelectedIndex(0);
    }
  }
  
  /**
   * Richiede il focus.
   */
  public
  void requestFocus()
  {
    super.requestFocus();
    
    selectFirstTab();
    
    if(listTabs.size() == 0) {
      return;
    }
    
    if(sIdFieldDefaultFocus != null) {
      Component oComponent = getComponent(sIdFieldDefaultFocus);
      if(oComponent != null) {
        int iTabIndex = getTabIndex(sIdFieldDefaultFocus);
        if(iTabIndex >= 0) {
          oTabbedPane.setSelectedIndex(iTabIndex);
        }
        oComponent.requestFocus();
      }
      return;
    }
    
    List oRows = (List) mapTabs.get(listTabs.get(0));
    if(oRows.size() > 0) {
      List oComponents = (List) oRows.get(0);
      
      if(oComponents.size() > 0) {
        Component oComponent = ((FieldInfo) oComponents.get(0)).component;
        if(oComponent.isEnabled()) {
          oComponent.requestFocus();
        }
      }
    }
  }
  
  /**
   * Richiede il focus per un determinato campo.
   *
   * @param sId Identificativo del campo
   */
  public
  void requestFocus(String sId)
  {
    super.requestFocus();
    
    Component oComponent = getComponent(sId);
    if(oComponent != null) {
      int iTabIndex = getTabIndex(sId);
      if(iTabIndex >= 0) {
        if(oTabbedPane != null && oTabbedPane.getTabCount() > iTabIndex) {
          oTabbedPane.setSelectedIndex(iTabIndex);
        }
      }
      else {
        selectFirstTab();
      }
      oComponent.requestFocus();
    }
  }
  
  /**
   * Verifica se un campo non e' stato valorizzato.
   *
   * @param sId Identificativo del campo
   * @return boolean true = non valorizzato, false = valorizzato
   */
  public
  boolean isBlank(String sId)
  {
    Object oValue = getValue(sId);
    
    if(oValue == null) {
      return true;
    }
    else if(oValue instanceof String) {
      if(((String) oValue).trim().length() == 0) {
        return true;
      }
    }
    else if(oValue instanceof Collection) {
      if(((Collection) oValue).size() == 0) {
        return true;
      }
    }
    else if(oValue instanceof Map) {
      if(((Map) oValue).isEmpty()) {
        return true;
      }
    }
    else if(oValue instanceof CodeAndDescription) {
      CodeAndDescription cd = (CodeAndDescription) oValue;
      if(cd.getCode() == null || cd.getCode().equals("")) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Verifica che non sia stato valorizzato alcun campo.
   *
   * @return boolean true = nessun campo valorizzato, false = almeno un campo valorizzato
   */
  public
  boolean isBlank()
  {
    Map mapValues = getValues();
    
    boolean boAllBlank = true;
    Iterator oItEntry = mapValues.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      Object oValue = entry.getValue();
      
      if(oValue == null) {
        continue;
      }
      else if(oValue instanceof String) {
        if(((String) oValue).trim().length() == 0) {
          continue;
        }
      }
      else if(oValue instanceof Collection) {
        if(((Collection) oValue).size() == 0) {
          continue;
        }
      }
      else if(oValue instanceof Map) {
        if(((Map) oValue).isEmpty()) {
          continue;
        }
      }
      else if(oValue instanceof CodeAndDescription) {
        CodeAndDescription cd = (CodeAndDescription) oValue;
        if(cd.getCode() == null || cd.getCode().equals("")) {
          continue;
        }
      }
      
      boAllBlank = false;
      break;
    }
    
    return boAllBlank;
  }
  
  /**
   * Verifica che non sia stato valorizzato alcun campo.
   *
   * @param listFieldsToIgnore Lista dei campi da ignorare nel controllo
   * @return boolean true = nessun campo valorizzato, false = almeno un campo valorizzato
   */
  public
  boolean isBlank(List listFieldsToIgnore)
  {
    Map mapValues = getValues();
    
    boolean boAllBlank = true;
    Iterator oItEntry = mapValues.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sKey = (String) entry.getKey();
      Object oValue = entry.getValue();
      
      if(listFieldsToIgnore != null  &&
        listFieldsToIgnore.contains(sKey)) {
        continue;
      }
      
      if(oValue == null) {
        continue;
      }
      else if(oValue instanceof String) {
        if(((String) oValue).trim().length() == 0) {
          continue;
        }
      }
      else if(oValue instanceof Collection) {
        if(((Collection) oValue).size() == 0) {
          continue;
        }
      }
      else if(oValue instanceof Map) {
        if(((Map) oValue).isEmpty()) {
          continue;
        }
      }
      else if(oValue instanceof CodeAndDescription) {
        CodeAndDescription cd = (CodeAndDescription) oValue;
        if(cd.getCode() == null || cd.getCode().equals("")) {
          continue;
        }
      }
      
      boAllBlank = false;
      break;
    }
    
    return boAllBlank;
  }
  
  /**
   * Verifica che non sia stato valorizzato alcun campo escludendo un carattere jolly.
   * Tale metodo e' utile quando si ritiene non valorizzato un campo se esso
   * contiene un carattere jolly usato ad es. per le ricerche.
   *
   * @param c Carattere jolly da escludere nella valutazione del campo
   * @return boolean true = nessun campo valorizzato, false = almeno un campo valorizzato
   */
  public
  boolean isBlank(char c)
  {
    return isBlank(c, null);
  }
  
  /**
   * Verifica che non sia stato valorizzato alcun campo escludendo un carattere jolly.
   * Tale metodo e' utile quando si ritiene non valorizzato un campo se esso
   * contiene un carattere jolly usato ad es. per le ricerche.
   *
   * @param c Carattere jolly da escludere nella valutazione del campo
   * @param listFieldsToIgnore Lista dei campi da ignorare nel controllo
   * @return boolean true = nessun campo valorizzato, false = almeno un campo valorizzato
   */
  public
  boolean isBlank(char c, List listFieldsToIgnore)
  {
    Map mapValues = getValues();
    
    boolean boAllBlank = true;
    Iterator oItEntry = mapValues.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sKey = (String) entry.getKey();
      Object oValue = entry.getValue();
      
      if(listFieldsToIgnore != null  &&
        listFieldsToIgnore.contains(sKey)) {
        continue;
      }
      
      if(oValue == null) {
        continue;
      }
      else if(oValue instanceof String) {
        String sValue = ((String) oValue).trim();
        if(sValue.length() == 0) {
          continue;
        }
        
        for(int i = 0; i < sValue.length(); i++) {
          char ci = sValue.charAt(i);
          if(ci != c) {
            return false;
          }
        }
        
        continue;
      }
      else if(oValue instanceof Collection) {
        if(((Collection) oValue).size() == 0) {
          continue;
        }
      }
      else if(oValue instanceof Map) {
        if(((Map) oValue).isEmpty()) {
          continue;
        }
      }
      else if(oValue instanceof CodeAndDescription) {
        CodeAndDescription cd = (CodeAndDescription) oValue;
        if(cd.getCode() == null || cd.getCode().equals("")) {
          continue;
        }
      }
      
      boAllBlank = false;
      break;
    }
    
    return boAllBlank;
  }
  
  /**
   * Ritorna il flag di abilitazione di un campo.
   *
   * @param sId Identificativo del campo
   * @return boolean Flag di abilitazione
   */
  public
  boolean isEnabled(String sId)
  {
    int iFirstPoint = sId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sId.length() - 1) {
      String sIdForm = sId.substring(0, iFirstPoint);
      String sIdField = sId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        return oFormPanel.isEnabled(sIdField);
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
    if(oFieldInfo == null) {
      return false;
    }
    return oFieldInfo.component.isEnabled();
  }
  
  /**
   * Imposta il flag di Editabilita' di un campo.
   *
   * @param sId Identificativo del campo
   * @param boEditable Flag di editabilita'
   */
  public
  void setEditable(String sId, boolean boEditable)
  {
    String sRId = resolveAlias(sId);
    
    int iFirstPoint = sRId.indexOf(".");
    if(iFirstPoint > 0 && iFirstPoint < sRId.length() - 1) {
      String sIdForm = sRId.substring(0, iFirstPoint);
      String sIdField = sRId.substring(iFirstPoint + 1);
      FormPanel oFormPanel = getFormPanel(sIdForm);
      if(oFormPanel != null) {
        oFormPanel.setEditable(sIdField, boEditable);
        return;
      }
    }
    
    FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sRId);
    if(oFieldInfo == null) {
      return;
    }
    
    Component oComponent = oFieldInfo.component;
    
    if(oComponent instanceof JPVEdit) {
      ((JPVEdit) oComponent).setEditable(boEditable);
    }
    else if(oComponent instanceof JTextComponent) {
      ((JTextComponent) oComponent).setEditable(boEditable);
      Color colTextBackground = UIManager.getColor("TextField.background");
      ((JTextComponent) oComponent).setBackground(colTextBackground);
    }
    else if(oComponent instanceof JCheckBox) {
      ((JCheckBox) oComponent).setEnabled(boEditable);
    }
    else if(oComponent instanceof JComboBox) {
      ((JComboBox) oComponent).setEnabled(boEditable);
    }
    else if(oComponent instanceof ADecodifiableComponent) {
      ((ADecodifiableComponent) oComponent).setEditable(boEditable);
    }
    else if(oComponent instanceof JTextNote) {
      ((JTextNote) oComponent).setEditable(boEditable);
    }
    else if(oComponent instanceof JTextFile) {
      ((JTextFile) oComponent).setEditable(boEditable);
    }
  }
  
  /**
   * Svuota tutti i campi.
   */
  public
  void clear()
  {
    // Si evita di notificare ad ogni setValue
    this.boDontNotifyChange = true;
    
    Iterator oItKeys = mapFields.keySet().iterator();
    while(oItKeys.hasNext()) {
      String sId = oItKeys.next().toString();
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        ((FormPanel) oComponent).clear();
      }
      else {
        setValue(sId, null);
      }
    }
    
    this.boDontNotifyChange = false;
    
    notifyChange();
  }
  
  /**
   * Imposta tutti i campi ai valori di default
   */
  public
  void reset()
  {
    // Si evita di notificare ad ogni setValue
    this.boDontNotifyChange = true;
    
    Iterator oItKeys = mapFields.keySet().iterator();
    while(oItKeys.hasNext()) {
      String sId = oItKeys.next().toString();
      FieldInfo oFieldInfo = (FieldInfo) mapFields.get(sId);
      Component oComponent = oFieldInfo.component;
      if(oComponent instanceof FormPanel) {
        ((FormPanel) oComponent).reset();
      }
      else {
        setValue(sId, oFieldInfo.defaultValue);
      }
    }
    
    this.boDontNotifyChange = false;
    
    notifyChange();
  }
  
  /**
   * Costruisce il pannello.
   */
  public
  void build()
  {
    if(oFormComponent != null) {
      this.remove(oFormComponent);
    }
    
    boolean boHasNoTabs = hasNoTabs();
    
    if(!boHasNoTabs) {
      oTabbedPane = new JTabbedPane();
      oFormComponent = oTabbedPane;
      this.add(oFormComponent, BorderLayout.NORTH);
    }
    
    for(int i = 0; i < listTabs.size(); i++) {
      String sTabName = (String) listTabs.get(i);
      
      List oRows = (List) mapTabs.get(sTabName);
      int iRows = oRows.size();
      
      JPanel oTabPanel = build_TabPanel(i);
      JPanel oPrevPanel = oTabPanel;
      for(int r = 0; r < iRows; r++) {
        List oComponents = (List) oRows.get(r);
        int iCols = oComponents.size();
        
        int iColToSpan = -1;
        int iSpan = 1;
        for(int c = 0; c < iCols; c++) {
          FieldInfo oFieldInfo = (FieldInfo) oComponents.get(c);
          if(oFieldInfo.span > 1) {
            iColToSpan = c;
            iSpan = oFieldInfo.span;
            break;
          }
        }
        
        JPanel oComponentsPanel = null;
        if(iColToSpan > -1) {
          oComponentsPanel = build_ComponentsPanelWithSpan(i, r, iCols, iColToSpan, iSpan);
        }
        else {
          oComponentsPanel = build_ComponentsPanel(i, r, iCols);
        }
        
        for(int c = 0; c < iCols; c++) {
          FieldInfo oFieldInfo = (FieldInfo) oComponents.get(c);
          int iMaxLabelSize = 0;
          if(iColToSpan > -1 && c > iColToSpan) {
            iMaxLabelSize = getMaxLabelSize(oRows, c + iSpan - 1, iCols + iSpan - 1);
          }
          else {
            iMaxLabelSize = getMaxLabelSize(oRows, c, iCols + iSpan - 1);
          }
          JPanel oComponentPanel = new JPanel(new BorderLayout());
          oComponentPanel.setName(oFieldInfo.id);
          if(oFieldInfo.label != null) {
            String sLabel = oFieldInfo.label;
            if(oFieldInfo.component instanceof ADataPanel) {
              ((ADataPanel) oFieldInfo.component).setLabel(sLabel);
              oComponentPanel.add(oFieldInfo.component, BorderLayout.NORTH);
            }
            else {
              String sTextLabel = sLabel.length() > 0 ? sLabel.trim() + ":" : "";
              oFieldInfo.jlabel = build_JLabel(oFieldInfo.component, sTextLabel, i, r, c, iMaxLabelSize);
              if(oFieldInfo.jlabel != null) oFieldInfo.jlabel.setName(oFieldInfo.id);
              oComponentPanel.add(build_LabelledComponent(oFieldInfo.jlabel, oFieldInfo.component, i, r, c), BorderLayout.NORTH);
            }
          }
          else {
            oComponentPanel.add(oFieldInfo.component, BorderLayout.NORTH);
          }
          
          if(oBGColorOnFocus != null && isColorFocusable(oFieldInfo.component)) {
            if(oFieldInfo.component instanceof JSpinner) {
              Component oEditor = ((JSpinner) oFieldInfo.component).getEditor();
              if(oEditor instanceof JSpinner.DefaultEditor) {
                Component oTextField = ((JSpinner.DefaultEditor) oEditor).getTextField();
                Color_FocusListener fl = new Color_FocusListener(oTextField, oBGColorOnFocus, listFocusListener);
                oTextField.addFocusListener(fl);
              }
            }
            else {
              Color_FocusListener fl = new Color_FocusListener(oFieldInfo.component, oBGColorOnFocus, listFocusListener);
              oFieldInfo.component.addFocusListener(fl);
            }
          }
          
          if(isCheckFocusable(oFieldInfo)) {
            oFieldInfo.component.addFocusListener(new Check_FocusListener(oFieldInfo.jlabel));
          }
          
          build_addComponent(oComponentsPanel, oComponentPanel, i, r, c);
        }
        JPanel oRowPanel = new JPanel(new BorderLayout(iGap, iGap));
        oRowPanel.add(oComponentsPanel, BorderLayout.NORTH);
        oPrevPanel.add(oRowPanel, BorderLayout.CENTER);
        oPrevPanel = oRowPanel;
      }
      
      if(!boHasNoTabs) {
        String sTabTitle  = GUIUtil.getGUIText(sTabName);
        char cTabMnemonic = GUIUtil.getGUIMnemonic(sTabName);
        String sTabDescription = GUIUtil.getGUIDescription(sTabName);
        String sTabIcon = GUIUtil.getGUIIcon(sTabName);
        
        oTabbedPane.add(sTabTitle, oTabPanel);
        int iLastTabIndex = oTabbedPane.getTabCount() - 1;
        if(sTabIcon != null) {
          oTabbedPane.setIconAt(iLastTabIndex, ResourcesMgr.getSmallImageIcon(sTabIcon));
        }
        if(sTabDescription != null) {
          oTabbedPane.setToolTipTextAt(iLastTabIndex, sTabDescription);
        }
        if(cTabMnemonic != '\0') {
          oTabbedPane.setMnemonicAt(iLastTabIndex, cTabMnemonic);
        }
      }
      else {
        oFormComponent = oTabPanel;
        this.add(oFormComponent, BorderLayout.NORTH);
      }
    }
    
    if(oMandatoryFields != null && oMandatoryFields.size() > 0) {
      String sMandatories = ResourcesMgr.config.getProperty(sFORMPANEL_MANDATORIES);
      if(sMandatories != null && sMandatories.trim().length() == 7) {
        markLabelMandatoryFields(Color.decode(sMandatories));
      }
    }
    
    reset();
  }
  
  /**
   * Ricostruisce il pannello senza resettare i campi.
   */
  public
  void rebuild()
  {
    if(oFormComponent != null) {
      this.remove(oFormComponent);
    }
    
    boolean boHasNoTabs = hasNoTabs();
    
    if(!boHasNoTabs) {
      oTabbedPane = new JTabbedPane();
      oFormComponent = oTabbedPane;
      this.add(oFormComponent, BorderLayout.NORTH);
    }
    
    for(int i = 0; i < listTabs.size(); i++) {
      String sTabName = (String) listTabs.get(i);
      
      List oRows = (List) mapTabs.get(sTabName);
      int iRows = oRows.size();
      
      JPanel oTabPanel = build_TabPanel(i);
      JPanel oPrevPanel = oTabPanel;
      for(int r = 0; r < iRows; r++) {
        List oComponents = (List) oRows.get(r);
        int iCols = oComponents.size();
        
        int iColToSpan = -1;
        int iSpan = 1;
        for(int c = 0; c < iCols; c++) {
          FieldInfo oFieldInfo = (FieldInfo) oComponents.get(c);
          if(oFieldInfo.span > 1) {
            iColToSpan = c;
            iSpan = oFieldInfo.span;
            break;
          }
        }
        
        JPanel oComponentsPanel = null;
        if(iColToSpan > -1) {
          oComponentsPanel = build_ComponentsPanelWithSpan(i, r, iCols, iColToSpan, iSpan);
        }
        else {
          oComponentsPanel = build_ComponentsPanel(i, r, iCols);
        }
        
        for(int c = 0; c < iCols; c++) {
          FieldInfo oFieldInfo = (FieldInfo) oComponents.get(c);
          int iMaxLabelSize = 0;
          if(iColToSpan > -1 && c > iColToSpan) {
            iMaxLabelSize = getMaxLabelSize(oRows, c + iSpan - 1, iCols + iSpan - 1);
          }
          else {
            iMaxLabelSize = getMaxLabelSize(oRows, c, iCols + iSpan - 1);
          }
          JPanel oComponentPanel = new JPanel(new BorderLayout());
          oComponentPanel.setName(oFieldInfo.id);
          if(oFieldInfo.label != null) {
            String sLabel = oFieldInfo.label;
            if(oFieldInfo.component instanceof ADataPanel) {
              ((ADataPanel) oFieldInfo.component).setLabel(sLabel);
              oComponentPanel.add(oFieldInfo.component, BorderLayout.NORTH);
            }
            else {
              String sTextLabel = sLabel.length() > 0 ? sLabel.trim() + ":" : "";
              oFieldInfo.jlabel = build_JLabel(oFieldInfo.component, sTextLabel, i, r, c, iMaxLabelSize);
              if(oFieldInfo.jlabel != null) oFieldInfo.jlabel.setName(oFieldInfo.id);
              oComponentPanel.add(build_LabelledComponent(oFieldInfo.jlabel, oFieldInfo.component, i, r, c), BorderLayout.NORTH);
            }
          }
          else {
            oComponentPanel.add(oFieldInfo.component, BorderLayout.NORTH);
          }
          
          if(oBGColorOnFocus != null && isColorFocusable(oFieldInfo.component)) {
            if(oFieldInfo.component instanceof JSpinner) {
              Component oEditor = ((JSpinner) oFieldInfo.component).getEditor();
              if(oEditor instanceof JSpinner.DefaultEditor) {
                Component oTextField = ((JSpinner.DefaultEditor) oEditor).getTextField();
                Color_FocusListener fl = new Color_FocusListener(oTextField, oBGColorOnFocus, listFocusListener);
                oTextField.addFocusListener(fl);
              }
            }
            else {
              Color_FocusListener fl = new Color_FocusListener(oFieldInfo.component, oBGColorOnFocus, listFocusListener);
              oFieldInfo.component.addFocusListener(fl);
            }
          }
          
          if(isCheckFocusable(oFieldInfo)) {
            oFieldInfo.component.addFocusListener(new Check_FocusListener(oFieldInfo.jlabel));
          }
          
          build_addComponent(oComponentsPanel, oComponentPanel, i, r, c);
        }
        JPanel oRowPanel = new JPanel(new BorderLayout(iGap, iGap));
        oRowPanel.add(oComponentsPanel, BorderLayout.NORTH);
        oPrevPanel.add(oRowPanel, BorderLayout.CENTER);
        oPrevPanel = oRowPanel;
      }
      
      if(!boHasNoTabs) {
        String sTabTitle  = GUIUtil.getGUIText(sTabName);
        char cTabMnemonic = GUIUtil.getGUIMnemonic(sTabName);
        String sTabDescription = GUIUtil.getGUIDescription(sTabName);
        String sTabIcon = GUIUtil.getGUIIcon(sTabName);
        
        oTabbedPane.add(sTabTitle, oTabPanel);
        int iLastTabIndex = oTabbedPane.getTabCount() - 1;
        if(sTabIcon != null) {
          oTabbedPane.setIconAt(iLastTabIndex, ResourcesMgr.getSmallImageIcon(sTabIcon));
        }
        if(sTabDescription != null) {
          oTabbedPane.setToolTipTextAt(iLastTabIndex, sTabDescription);
        }
        if(cTabMnemonic != '\0') {
          oTabbedPane.setMnemonicAt(iLastTabIndex, cTabMnemonic);
        }
      }
      else {
        oFormComponent = oTabPanel;
        this.add(oFormComponent, BorderLayout.NORTH);
      }
    }
    
    if(oMandatoryFields != null && oMandatoryFields.size() > 0) {
      String sMandatories = ResourcesMgr.config.getProperty(sFORMPANEL_MANDATORIES);
      if(sMandatories != null && sMandatories.trim().length() == 7) {
        markLabelMandatoryFields(Color.decode(sMandatories));
      }
    }
  }
  
  protected
  JPanel build_TabPanel(int iTab)
  {
    JPanel jpResult = new JPanel(new BorderLayout());
    jpResult.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    return jpResult;
  }
  
  protected
  JPanel build_ComponentsPanel(int iTab, int iRow, int iCountColumns)
  {
    JPanel jpResult = new JPanel(new GridLayout(1, iCountColumns, 4, 4));
    return jpResult;
  }
  
  protected
  JPanel build_ComponentsPanelWithSpan(int iTab, int iRow, int iCountColumns, int iColToSpan, int iSpan)
  {
    JPanel jpResult = new JPanel(new GridLayoutExt(1, iCountColumns + 1, 4, 4, iColToSpan, iSpan));
    return jpResult;
  }
  
  protected
  JLabel build_JLabel(Component component, String sTextLabel, int iTab, int iRow, int iCol, int iLabelSize)
  {
    JLabel jLabel = null;
    if(boLabelOnTop) {
      jLabel = new JLabel(sTextLabel, JLabel.LEFT);
    }
    else {
      jLabel = new JLabel(sTextLabel, JLabel.RIGHT);
      Dimension oDimension = new Dimension(iLabelSize, 0);
      jLabel.setPreferredSize(oDimension);
      jLabel.setMinimumSize(oDimension);
    }
    return jLabel;
  }
  
  protected
  JPanel build_LabelledComponent(JLabel jlabel, Component component, int iTab, int iRow, int iCol)
  {
    JPanel jPanel = new JPanel(new BorderLayout(iGap, iGap));
    if(boLabelOnTop) {
      if(jlabel != null) jPanel.add(jlabel, BorderLayout.NORTH);
      jPanel.add(component, BorderLayout.CENTER);
    }
    else {
      if(jlabel != null) jPanel.add(jlabel, BorderLayout.WEST);
      jPanel.add(component, BorderLayout.CENTER);
    }
    return jPanel;
  }
  
  protected
  void build_addComponent(JPanel jpComponentsPanel, Component cJLabelAndComponent, int iTab, int iRow, int iCol)
  {
    jpComponentsPanel.add(cJLabelAndComponent);
  }
  
  protected
  void setPreferredWidth(Component component, int iWidth)
  {
    if(component instanceof JComponent) {
      ((JComponent) component).setPreferredSize(new Dimension(iWidth, iPreferredHeightComboBox));
    }
  }
  
  protected
  JPanel resize(Component component, int iWidth)
  {
    if(component instanceof JComponent) {
      ((JComponent) component).setPreferredSize(new Dimension(iWidth, iPreferredHeightComboBox));
    }
    JPanel jpResult = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    jpResult.add(component);
    return jpResult;
  }
  
  protected
  void setEmptyBorder(Component component, int iLeft, int iRight)
  {
    if(component instanceof JComponent) {
      ((JComponent) component).setBorder(BorderFactory.createEmptyBorder(0, iLeft, 0, iRight));
    }
  }
  
  public static
  Object getValue(Component oComponent)
  {
    if(oComponent instanceof IValuable) {
      return ((IValuable) oComponent).getValue();
    }
    else
    if(oComponent instanceof JPVEdit) {
      Object oValue = ((JPVEdit) oComponent).getValue();
      if(oValue instanceof String) {
        return ((String) oValue).trim();
      }
      return oValue;
    }
    else
    if(oComponent instanceof JTextComponent) {
      return ((JTextComponent) oComponent).getText().trim();
    }
    else
    if(oComponent instanceof JCheckBox) {
      return Boolean.valueOf(((JCheckBox) oComponent).isSelected());
    }
    else
    if(oComponent instanceof JComboBox) {
      return ((JComboBox) oComponent).getSelectedItem();
    }
    else
    if(oComponent instanceof ADecodifiableComponent) {
      return ((ADecodifiableComponent) oComponent).getKey();
    }
    else
    if(oComponent instanceof ADataPanel) {
      return ((ADataPanel) oComponent).getData();
    }
    else
    if(oComponent instanceof JTextFile) {
      return ((JTextFile) oComponent).getText();
    }
    else
    if(oComponent instanceof JTextNote) {
      return ((JTextNote) oComponent).getText();
    }
    else
    if(oComponent instanceof JRichTextNote) {
      return ((JRichTextNote) oComponent).getText();
    }
    else
    if(oComponent instanceof JSpinner) {
      return ((JSpinner) oComponent).getValue();
    }
    return null;
  }
  
  public static
  Object getContent(Component oComponent)
  {
    if(oComponent instanceof IContentable) {
      return ((IContentable) oComponent).getContent();
    }
    else
    if(oComponent instanceof IValuable) {
      return ((IValuable) oComponent).getValue();
    }
    else
    if(oComponent instanceof JPVEdit) {
      return ((JPVEdit) oComponent).getValue();
    }
    else
    if(oComponent instanceof JTextComponent) {
      return ((JTextComponent) oComponent).getText();
    }
    else
    if(oComponent instanceof JCheckBox) {
      return Boolean.valueOf(((JCheckBox) oComponent).isSelected());
    }
    else
    if(oComponent instanceof JComboBox) {
      return ((JComboBox) oComponent).getSelectedItem();
    }
    else
    if(oComponent instanceof ADecodifiableComponent) {
      List oValues = ((ADecodifiableComponent) oComponent).getValues();
      if(oValues == null) return null;
      return new ArrayList(oValues);
    }
    else
    if(oComponent instanceof ADataPanel) {
      return ((ADataPanel) oComponent).getContent();
    }
    else
    if(oComponent instanceof JTextFile) {
      return ((JTextFile) oComponent).getText();
    }
    else
    if(oComponent instanceof JTextNote) {
      return ((JTextNote) oComponent).getText();
    }
    else
    if(oComponent instanceof JRichTextNote) {
      return ((JRichTextNote) oComponent).getText();
    }
    else
    if(oComponent instanceof JSpinner) {
      return ((JSpinner) oComponent).getValue();
    }
    return null;
  }
  
  public static
  void setValue(Component oComponent, Object oValue)
  {
    if(oComponent instanceof IValuable) {
      AutoCompleter oAC = null;
      if(oComponent instanceof JComponent) {
        oAC = AutoCompleter.getAutoCompleter((JComponent) oComponent);
        if(oAC != null) oAC.setDontShowPopup(true);
      }
      ((IValuable) oComponent).setValue(oValue);
      if(oAC != null) oAC.setDontShowPopup(false);
    }
    else if(oComponent instanceof JPVEdit) {
      ((JPVEdit) oComponent).setValue(oValue);
    }
    else if(oComponent instanceof JTextComponent) {
      AutoCompleter oAC = AutoCompleter.getAutoCompleter((JTextComponent) oComponent);
      if(oAC != null) oAC.setDontShowPopup(true);
      if(oValue != null) {
        ((JTextComponent) oComponent).setText(oValue.toString());
        ((JTextComponent) oComponent).setCaretPosition(0);
      }
      else {
        ((JTextComponent) oComponent).setText("");
      }
      if(oAC != null) oAC.setDontShowPopup(false);
    }
    else if(oComponent instanceof JCheckBox) {
      if(oValue instanceof Boolean) {
        ((JCheckBox) oComponent).setSelected(((Boolean) oValue).booleanValue());
      }
      else {
        ((JCheckBox) oComponent).setSelected(false);
      }
    }
    else if(oComponent instanceof JComboBox) {
      JComboBox jcb = ((JComboBox) oComponent);
      if(oValue != null) {
        // Ricerca del valore nella lista di Item presenti
        Object oItemFound = null;
        for(int i = 0; i < jcb.getItemCount(); i++) {
          Object oItem = jcb.getItemAt(i);
          if(oItem == null) continue;
          if(oItem.equals(oValue)) {
            oItemFound = oItem;
            break;
          }
        }
        if(oItemFound != null) {
          jcb.setSelectedItem(oItemFound);
        }
        else {
          jcb.addItem(oValue);
          jcb.setSelectedItem(oValue);
        }
      }
      else {
        jcb.setSelectedItem(null);
      }
    }
    else if(oComponent instanceof ADecodifiableComponent) {
      if(oValue == null) {
        ((ADecodifiableComponent) oComponent).setValues(null);
      }
      else
      if(oValue instanceof List) {
        ((ADecodifiableComponent) oComponent).setValues((List) oValue);
      }
      else {
        List listValues = new ArrayList();
        listValues.add(oValue);
        listValues.add(oValue.toString());
        ((ADecodifiableComponent) oComponent).setValues(listValues);
      }
    }
    else if(oComponent instanceof ADataPanel) {
      ((ADataPanel) oComponent).setData(oValue);
    }
    else if(oComponent instanceof JTextFile) {
      if(oValue != null) {
        ((JTextFile) oComponent).setText(oValue.toString());
      }
      else {
        ((JTextFile) oComponent).setText("");
      }
    }
    else if(oComponent instanceof JTextNote) {
      if(oValue != null) {
        ((JTextNote) oComponent).setText(oValue.toString());
      }
      else {
        ((JTextNote) oComponent).setText("");
      }
    }
    else if(oComponent instanceof JRichTextNote) {
      if(oValue != null) {
        ((JRichTextNote) oComponent).setText(oValue.toString());
      }
      else {
        ((JRichTextNote) oComponent).setText("");
      }
    }
    else if(oComponent instanceof JSpinner) {
      if(oValue != null) {
        ((JSpinner) oComponent).setValue(oValue);
      }
      else {
        ((JSpinner) oComponent).setValue(new Integer(0));
      }
    }
  }
  
  /**
   * Restituisce i campi non valorizzati che appartengono alla lista dei
   * campi obbligatori.
   *
   * @return List
   */
  public
  List getListCheckMandatories()
  {
    List oResult = new ArrayList();
    if(oMandatoryFields == null) return oResult;
    for(int i = 0; i < oMandatoryFields.size(); i++) {
      String sId = (String) oMandatoryFields.get(i);
      if(isBlank(sId)) oResult.add(sId);
    }
    return oResult;
  }
  
  /**
   * Restituisce una stringa con le etichette dei campi non valorizzati
   * che appartengono alla lista dei campi obbligatori.
   *
   * @return String
   */
  public
  String getStringCheckMandatories()
  {
    StringBuffer sb = new StringBuffer();
    if(oMandatoryFields == null) return sb.toString();
    for(int i = 0; i < oMandatoryFields.size(); i++) {
      String sId = (String) oMandatoryFields.get(i);
      if(isBlank(sId)) {
        String sLabel = getLabel(sId);
        if(sLabel != null) {
          sb.append(sLabel);
          sb.append(",\n");
        }
        else {
          sb.append(sId);
          sb.append(",\n");
        }
      }
    }
    String sResult = sb.toString();
    if(sResult.length() > 2) {
      return sResult.substring(0, sResult.length() - 2);
    }
    return "";
  }
  
  /**
   * Il metodo ha effetto dopo la costruzione del FormPanel.
   *
   * @param color Color
   */
  public
  void markLabelMandatoryFields(Color color)
  {
    if(oMandatoryFields == null) return;
    Color cLabelForeground = UIManager.getColor("Label.foreground");
    List listJLabel = getListJLabel();
    for(int i = 0; i < listJLabel.size(); i++) {
      JLabel jLabel = (JLabel) listJLabel.get(i);
      if(jLabel == null) continue;
      jLabel.setForeground(cLabelForeground);
    }
    for(int i = 0; i < oMandatoryFields.size(); i++) {
      String sId = (String) oMandatoryFields.get(i);
      JLabel jlabel = getJLabel(sId);
      if(jlabel != null) {
        jlabel.setForeground(color);
      }
    }
  }
  
  protected static
  boolean isColorFocusable(Component oComponent)
  {
    // Controlla che non sia stato aggiunto gia' un Color_FocusListener...
    FocusListener[] aoFocusListeners = oComponent.getFocusListeners();
    for(int i = 0; i < aoFocusListeners.length; i++) {
      if(aoFocusListeners[i] instanceof Color_FocusListener) {
        return false;
      }
    }
    if(oComponent instanceof JPVEdit) {
      return true;
    }
    else if(oComponent instanceof JTextComponent) {
      return true;
    }
    else if(oComponent instanceof ADecodifiableComponent) {
      return true;
    }
    else if(oComponent instanceof JTextFile) {
      return true;
    }
    else if(oComponent instanceof JTextNote) {
      return true;
    }
    else if(oComponent instanceof JRichTextNote) {
      return true;
    }
    else if(oComponent instanceof JSpinner) {
      return true;
    }
    else if(oComponent instanceof IColorFocusable) {
      return true;
    }
    return false;
  }
  
  protected static
  boolean isCheckFocusable(FieldInfo fieldInfo)
  {
    if(!(fieldInfo.component instanceof JCheckBox)) {
      return false;
    }
    if(fieldInfo.jlabel == null) {
      return false;
    }
    FocusListener[] aoFocusListeners = fieldInfo.component.getFocusListeners();
    for(int i = 0; i < aoFocusListeners.length; i++) {
      if(aoFocusListeners[i] instanceof Check_FocusListener) {
        return false;
      }
    }
    return true;
  }
  
  protected
  boolean hasNoTabs()
  {
    if(listTabs.size() == 0) return true;
    if(listTabs.size() == 1) {
      String sTabName = (String) listTabs.get(0);
      if(sTabName.length() == 0) return true;
    }
    return false;
  }
  
  /**
   * Se si passa l'alias viene ritornato l'identificativo del campo a cui punta,
   * altrimenti viene ritornato il parametro inalterato senza il controllo che
   * tale identificativo esista.
   *
   * @param sId String
   * @return String
   */
  protected
  String resolveAlias(String sId)
  {
    for(int i = 0; i < listAlias.size(); i++) {
      Alias alias = (Alias) listAlias.get(i);
      if(alias.getName().equals(sId)) {
        return (String) alias.getResource();
      }
    }
    return sId;
  }
  
  protected
  String getAliasById(String sId)
  {
    for(int i = 0; i < listAlias.size(); i++) {
      Alias alias = (Alias) listAlias.get(i);
      if(alias.getResource().equals(sId)) {
        return (String) alias.getName();
      }
    }
    return sId;
  }
  
  /**
   * Restituisce la dimensione massima dell'etichetta per una determinata colonna
   * nell'insieme delle righe che hanno lo stesso numero di colonne rappresentato
   * da iCols. Per la prima colonna (0) viene preso il massimo tra tutte le righe.
   *
   * @param oRows List
   * @param iCol int
   * @param iCols int
   * @return int
   */
  protected
  int getMaxLabelSize(List oRows, int iCol, int iCols)
  {
    int iResult  = 0;
    int iDefSize = 0;
    for(int r = 0; r < oRows.size(); r++) {
      List oComponents = (List) oRows.get(r);
      int count = oComponents.size();
      int iSize = 0;
      if(count > iCol) {
        FieldInfo oFieldInfo = (FieldInfo) oComponents.get(iCol);
        if(oFieldInfo.label != null) {
          if(oFontMetrics != null) {
             iSize = (oFontMetrics.stringWidth(oFieldInfo.label + ":") * 11) / 10;
          }
          else {
             iSize = oFieldInfo.label.length() * 10;
          }
          if(iSize > iDefSize) {
            iDefSize = iSize;
          }
        }
      }
      if(count != iCols && iCol > 0) continue;
      if(count > iCol && iSize > iResult) {
        iResult = iSize;
      }
    }
    if(iDefSize == 0) iDefSize = 80;
    if(iResult  == 0) iResult  = iDefSize;
    if(iResult < iMinSizeLabel) iResult = iMinSizeLabel;
    return DefaultGUIManager.resizeForHRScreen(iResult);
  }
  
  protected
  void notifyChange()
  {
    if(boDontNotifyChange || listFields == null) return;
    for(int i = 0; i < listFields.size(); i++) {
      FieldInfo oFieldInfo = (FieldInfo) listFields.get(i);
      if(oFieldInfo.component instanceof ADataPanel) {
        ((ADataPanel) oFieldInfo.component).onParentFormPanelChanged();
      }
    }
  }
  
  protected static
  class FieldInfo
  {
    public String id;
    public Component component;
    public String label;
    public Object defaultValue;
    public JLabel jlabel;
    public int tabIndex;
    public String sHelpText;
    public int width;
    public int span;
    
    public
    FieldInfo(String sId, String sLabel, Component oComponent, int iTabIndex)
    {
      this.id = sId;
      if(sLabel != null) {
        this.label = sLabel.trim();
      }
      this.component = oComponent;
      this.tabIndex = iTabIndex;
    }
  }
  
  protected static
  class HiddenComponent extends Component implements IValuable
  {
    private Object oValue;
    private List listActionListener = new ArrayList();
    private String sId;
    
    public
    HiddenComponent(String sId)
    {
      this.sId = sId;
    }
    
    public
    void addActionListener(ActionListener al)
    {
      if(al != null) {
        listActionListener.add(al);
      }
    }
    
    public
    void removeActionListener(ActionListener al)
    {
      if(al != null) {
        listActionListener.remove(al);
      }
    }
    
    public
    void setValue(Object oValue)
    {
      this.oValue = oValue;
      
      if(listActionListener.size() > 0) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sId);
        for(int i = 0; i < listActionListener.size(); i++) {
          ActionListener al = (ActionListener) listActionListener.get(i);
          al.actionPerformed(e);
        }
      }
    }
    
    public
    Object getValue()
    {
      return oValue;
    }
  }
  
  protected static
  class Color_FocusListener implements FocusListener
  {
    private Component oComponent;
    private Color oBGColorOnFocus;
    private Color oComponentBGColor;
    private List  listFocusListener;
    
    public
    Color_FocusListener(Component theComponent, Color oBGColorOnFocus)
    {
      this.oComponent        = theComponent;
      this.oBGColorOnFocus   = oBGColorOnFocus;
      this.oComponentBGColor = oComponent.getBackground();
    }
    
    public
    Color_FocusListener(Component theComponent, Color oBGColorOnFocus, List  listFocusListener)
    {
      this.oComponent        = theComponent;
      this.oBGColorOnFocus   = oBGColorOnFocus;
      this.oComponentBGColor = oComponent.getBackground();
      this.listFocusListener = listFocusListener;
    }
    
    public
    void focusGained(FocusEvent e)
    {
      oComponent.setBackground(oBGColorOnFocus);
      if(listFocusListener == null || listFocusListener.size() == 0) {
        return;
      }
      for(int i = 0; i < listFocusListener.size(); i++) {
        FocusListener focusListener = (FocusListener) listFocusListener.get(i);
        focusListener.focusGained(e);
      }
    }
    
    public
    void focusLost(FocusEvent e)
    {
      oComponent.setBackground(oComponentBGColor);
      if(listFocusListener == null || listFocusListener.size() == 0) {
        return;
      }
      for(int i = 0; i < listFocusListener.size(); i++) {
        FocusListener focusListener = (FocusListener) listFocusListener.get(i);
        focusListener.focusLost(e);
      }
    }
  }
  
  protected static
  class HelpLabel_MouseListener extends MouseAdapter
  {
    private Object oHelpResource;
    
    public HelpLabel_MouseListener(Object oHelpResource)
    {
      this.oHelpResource = oHelpResource;
    }
    
    public void mouseClicked(MouseEvent e) {
      if(oHelpResource instanceof ActionListener) {
        try {
          ActionEvent actionEvent = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "help");
          ((ActionListener) oHelpResource).actionPerformed(actionEvent);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      else
      if(oHelpResource instanceof URL) {
        ResourcesMgr.getWorkPanel().show(new HtmlBrowser("Documentazione", IConstants.sICON_HELP,(URL) oHelpResource), "Documentazione");
      }
      else {
        if(oHelpResource == null) return;
        try {
          ResourcesMgr.getGUIManager().showGUITextMessage(null, oHelpResource.toString());
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }
  
  protected
  class Check_FocusListener implements FocusListener
  {
    protected JLabel jlabel;
    
    public Check_FocusListener(JLabel jlabel)
    {
      this.jlabel = jlabel;
    }
    
    public void focusGained(FocusEvent e) {
      if(oColorCheckBoxFocus == null) {
        jlabel.setBorder(BorderFactory.createLineBorder(UIManager.getColor("CheckBox.focus")));
      }
      else {
        jlabel.setBorder(BorderFactory.createLineBorder(oColorCheckBoxFocus));
      }
    }
    
    public void focusLost(FocusEvent e) {
      jlabel.setBorder(null);
    }
  }
}
