package org.dew.swingup;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.impl.SimpleJInternalFrame;
import org.dew.swingup.util.*;

/**
 * Classe che rappresenta l'area di lavoro dell'applicazione MDI.
 *
 * @version 1.0
 */
public abstract
class AWorkPanel extends JPanel
{
  protected CardLayout oCardLayout;
  protected Container oMDIWorkPanel;
  protected Container oGUIWorkPanel;
  
  protected JTabbedPane jTabbedPane;
  protected WorkPanelPopupMenu workPanelPopupMenu;
  protected JDesktopPane jDesktopPane;
  protected Map mapWorkObjectsInfo;
  protected List listWorkObjects;
  
  /**
   * Costruttore di default.
   */
  public
  AWorkPanel()
  {
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di AWorkPanel", ex);
    }
  }
  
  /**
   * Costruisce la GUI.
   *
   * @return Container
   * @throws Exception
   */
  public abstract
  Container buildGUI()
    throws Exception;
  
  /**
   * Imposta il flag di abilitazione della GUI.
   *
   * @param boEnabled boolean
   */
  public abstract
  void setEnabled(boolean boEnabled);
  
  /**
   * Metodo invocato quando si verifica l'evento Opened sul Frame principale
   * dell'applicazione.
   */
  public abstract
  void onOpened();
  
  /**
   * Metodo invocato prima della chiusura dell'applicazione.
   *
   * @return Flag di chiusura
   */
  public
  boolean onClosing()
  {
    Iterator oItKeys = mapWorkObjectsInfo.keySet().iterator();
    while(oItKeys.hasNext()) {
      Object oWorkObject = oItKeys.next();
      if(oWorkObject instanceof IWorkObject) {
        IWorkObject workObject = (IWorkObject) oWorkObject;
        if(!workObject.onClosing()) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Metodo invocato prima della chiusura della sessione di lavoro.
   *
   * @return Flag di chiusura
   */
  public
  boolean onDisconnecting()
  {
    Iterator oItKeys = mapWorkObjectsInfo.keySet().iterator();
    while(oItKeys.hasNext()) {
      Object oWorkObject = oItKeys.next();
      if(oWorkObject instanceof IWorkObject) {
        IWorkObject workObject = (IWorkObject) oWorkObject;
        if(!workObject.onClosing()) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Restituisce il documento di guida corrispondente al titolo della GUI.
   *
   * @param sTitle String
   * @return String
   */
  public
  String getHelpDoc(String sTitle)
  {
    return null;
  }
  
  /**
   * Verifica l'esistenza di un tab con il titolo specificato nel parametro.
   *
   * @param sTitle String
   * @return boolean
   */
  public
  boolean existTab(String sTitle)
  {
    for(int i = 0; i < jTabbedPane.getTabCount(); i++) {
      String sTitleAt_i = jTabbedPane.getTitleAt(i);
      if(sTitleAt_i.equals(sTitle)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Seleziona un tab in fuzione del titolo.
   * Se il tab esiste viene restituito true.
   * Tale metodo puo' essere utile per la gestione dei frame ad istanza singola.
   *
   * @param sTitle String
   * @return boolean
   */
  public
  boolean selectTab(String sTitle)
  {
    for(int i = 0; i < jTabbedPane.getTabCount(); i++) {
      String sTitleAt_i = jTabbedPane.getTitleAt(i);
      if(sTitleAt_i.equals(sTitle)) {
        jTabbedPane.setSelectedIndex(i);
        return true;
      }
    }
    return false;
  }
  
  /**
   * Ottiene l'oggetto mostrato nel workpanel corrispondente al tab
   * col titolo passato come parametro.
   *
   * @param sTitle String
   * @return Object WorkObject corrispondente al Tab col titolo specificato
   */
  public
  Object getWorkObjectByTabTitle(String sTitle)
  {
    for(int i = 0; i < jTabbedPane.getTabCount(); i++) {
      String sTitleAt_i = jTabbedPane.getTitleAt(i);
      if(sTitleAt_i.equals(sTitle)) {
        if(i < listWorkObjects.size()) {
          return listWorkObjects.get(i);
        }
      }
    }
    return null;
  }
  
  /**
   * Restituisce il titolo della GUI correntemente attiva.
   *
   * @return String
   */
  public
  String getCurrentActiveTitle()
  {
    int iSelectedIndex = jTabbedPane.getSelectedIndex();
    if(iSelectedIndex < 0) return null;
    return jTabbedPane.getTitleAt(iSelectedIndex);
  }
  
  /**
   * Imposta il titolo del tab correntemente selezionato.
   *
   * @param sTitle
   */
  public
  void setTitleSelectedTab(String sTitle)
  {
    int iSelectedIndex = jTabbedPane.getSelectedIndex();
    if(iSelectedIndex < 0) return;
    jTabbedPane.setTitleAt(iSelectedIndex, sTitle);
    if(iSelectedIndex < listWorkObjects.size()) {
      Object oWorkObject = listWorkObjects.get(iSelectedIndex);
      if(oWorkObject != null) {
        WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
        if(woinfo != null && woinfo.internalFrame != null) {
          woinfo.internalFrame.setTitle(sTitle);
        }
      }
    }
  }
  
  /**
   * Imposta il titolo di un oggetto.
   *
   * @param oWorkObject Oggetto per il quale si vuole impostare il titolo
   * @param sTitle Titolo
   */
  public
  void setTitle(Object oWorkObject, String sTitle)
  {
    WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
    if(woinfo == null) return;
    int iIndexOf = jTabbedPane.indexOfComponent(woinfo.placeHolder);
    if(iIndexOf < 0) return;
    jTabbedPane.setTitleAt(iIndexOf, sTitle);
    if(woinfo.internalFrame != null) {
      woinfo.internalFrame.setTitle(sTitle);
    }
  }
  
  /**
   * Restituisce i titoli dei frame aperti.
   *
   * @return Lista dei titoli
   */
  public
  List getFrameTitles()
  {
    List listResult = new ArrayList();
    for(int i = 0; i < jTabbedPane.getTabCount(); i++) {
      listResult.add(jTabbedPane.getTitleAt(i));
    }
    return listResult;
  }
  
  /**
   * Ritorna il numero di frames interni aperti.
   *
   * @return int Frames interni aperti.
   */
  public
  int getFramesCount()
  {
    return listWorkObjects.size();
  }
  
  /**
   * Ritorna la lista degli oggetti mostrati dal WorkPanel.
   *
   * @return List
   */
  public
  List getListWorkObjects()
  {
    return listWorkObjects;
  }
  
  /**
   * Mostra il pannello MDI per la gestione delle finestre interne.
   */
  public
  void showMDIPanel()
  {
    oCardLayout.last(this);
  }
  
  /**
   * Mostra il pannello principale.
   */
  public
  void showMainPanel()
  {
    oCardLayout.first(this);
  }
  
  /**
   * Mostra un oggetto (JInternalFrame, JDialog, Container).
   * Se l'oggetto implementa l'interfaccia IWorkObject puo' gestire
   * gli eventi di apertura, chiusura e attivazione.
   *
   * @param oWorkObject Oggetto da visualizzare
   */
  public
  void show(Object oWorkObject)
  {
    if(oWorkObject instanceof JInternalFrame) {
      showInternalFrame((JInternalFrame) oWorkObject);
    }
    else
    if(oWorkObject instanceof JDialog) {
      showDialog((JDialog) oWorkObject);
    }
    else
    if(oWorkObject instanceof Container) {
      showContainer((Container) oWorkObject);
    }
    else
    if(oWorkObject instanceof User) {
      showUserInfo((User) oWorkObject);
    }
  }
  
  /**
   * Mostra un oggetto (JInternalFrame, JDialog, Container) con la descrizione.
   * Se l'oggetto implementa l'interfaccia IWorkObject puo' gestire
   * gli eventi di apertura, chiusura e attivazione.
   *
   * @param oWorkObject Oggetto da visualizzare
   * @param sDescription Descrizione
   */
  public
  void show(Object oWorkObject, String sDescription)
  {
    if(oWorkObject instanceof JInternalFrame) {
      showInternalFrame((JInternalFrame) oWorkObject, sDescription);
    }
    else
    if(oWorkObject instanceof JDialog) {
      showDialog((JDialog) oWorkObject, sDescription);
    }
    else
    if(oWorkObject instanceof Container) {
      showContainer((Container) oWorkObject, sDescription);
    }
    else
    if(oWorkObject instanceof User) {
      showUserInfo((User) oWorkObject);
    }
  }
  
  /**
   * Mostra un oggetto (JInternalFrame, JDialog, Container) con la descrizione e
   * l'icona.
   * L'icona viene utilizzata solo nel caso l'oggetto estenda la classe
   * Container.
   * Se l'oggetto implementa l'interfaccia IWorkObject puo' gestire
   * gli eventi di apertura, chiusura e attivazione.
   *
   * @param oWorkObject Oggetto da visualizzare
   * @param sDescription Descrizione
   * @param sIcon Icona
   */
  public
  void show(Object oWorkObject, String sDescription, String sIcon)
  {
    if(oWorkObject instanceof JInternalFrame) {
      showInternalFrame((JInternalFrame) oWorkObject, sDescription);
    }
    else
    if(oWorkObject instanceof JDialog) {
      showDialog((JDialog) oWorkObject, sDescription);
    }
    else
    if(oWorkObject instanceof Container) {
      showContainer((Container) oWorkObject, sDescription, sIcon);
    }
    else
    if(oWorkObject instanceof User) {
      showUserInfo((User) oWorkObject);
    }
  }
  
  /**
   * Attiva un oggetto.
   *
   * @param oWorkObject Oggetto da attivare
   */
  public
  void activate(Object oWorkObject)
  {
    WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
    
    if(woinfo == null) {
      return;
    }
    
    if(oWorkObject instanceof IWorkObject) {
      ((IWorkObject) oWorkObject).onActivated();
    }
    
    jTabbedPane.setSelectedComponent(woinfo.placeHolder);
  }
  
  /**
   * Chiude forzatamente tutti gli oggetti aperti.
   * Non sono invocati i metodi onClosing degli oggetti IWorkObject.
   */
  public
  void closeAll()
  {
    // Richiama il metodo dispose di tutti i frame interni.
    Iterator oItKeys = mapWorkObjectsInfo.keySet().iterator();
    while(oItKeys.hasNext()) {
      Object oWorkObject = oItKeys.next();
      WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
      JInternalFrame jif = woinfo.internalFrame;
      if(jif != null) {
        jif.removeInternalFrameListener(woinfo.internalFrameListener);
        jif.removeMouseListener(woinfo.mouseListener);
        jif.dispose();
      }
    }
    
    // Rimuove tutti i componenti aggiungi al pannello Desktop
    jDesktopPane.removeAll();
    
    // Rimuove tutti i pannelli dal JTabbedPane
    jTabbedPane.removeAll();
    jTabbedPane.updateUI();
    
    // Riporta in primo piano il pannello principale
    oCardLayout.first(this);
    
    // Inizializza la mappa dei work object.
    mapWorkObjectsInfo.clear();
    listWorkObjects.clear();
  }
  
  /**
   * Chiude un oggetto.
   *
   * @param oWorkObject Oggetto da chiudere
   */
  public
  void closeLater(Object oWorkObject)
  {
    SwingUtilities.invokeLater(new CloseLater(oWorkObject));
  }
  
  /**
   * Chiude un oggetto.
   *
   * @param oWorkObject Oggetto da chiudere
   */
  public
  void close(Object oWorkObject)
  {
    WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
    
    if(woinfo == null) {
      return;
    }
    
    if(oWorkObject instanceof IWorkObject) {
      if(!((IWorkObject) oWorkObject).onClosing()) {
        return;
      }
    }
    
    JInternalFrame jif = woinfo.internalFrame;
    if(jif != null) {
      jif.dispose();
      
      // IMPORTANTE: Per i frame ad una istanza occorre rimuovere i listener
      // perche' se vengono riaperti in un secondo momento tali listener
      // si aggiungono a quelli gia' presenti nell'istanza.
      jif.removeInternalFrameListener(woinfo.internalFrameListener);
      jif.removeMouseListener(woinfo.mouseListener);
      
      jDesktopPane.remove(woinfo.internalFrame);
      
      jTabbedPane.remove(woinfo.placeHolder);
      checkMDIFramesTitlesSize();
      
      onChange_jTabbedPane();
    }
    
    mapWorkObjectsInfo.remove(oWorkObject);
    listWorkObjects.remove(oWorkObject);
    if(mapWorkObjectsInfo.size() == 0) {
      oCardLayout.first(this);
    }
  }
  
  /**
   * Chiude tutti gli oggetti aperti.
   * Sono invocati i metodi onClosing degli oggetti IWorkObject.
   */
  public
  void closeAllWorkObjects()
  {
    int iCountWorkObjects = listWorkObjects.size();
    for(int i = 0; i < iCountWorkObjects; i++) {
      if(listWorkObjects.size() > 0) {
        Object oWorkObjectToClose = listWorkObjects.get(0);
        close(oWorkObjectToClose);
      }
    }
  }
  
  /**
   * Chiude tutti gli oggetti aperti tranne quello specificato.
   * Sono invocati i metodi onClosing degli oggetti IWorkObject.
   *
   * @param oWorkObject Oggetto da chiudere
   */
  public
  void closeAllExcept(Object oWorkObject)
  {
    int iCountWorkObjects = listWorkObjects.size();
    List listWorkObjectsToClose = new ArrayList();
    for(int i = 0; i < iCountWorkObjects; i++) {
      Object oWorkObjectToClose = listWorkObjects.get(i);
      if(oWorkObject == null || !oWorkObject.equals(oWorkObjectToClose)) {
        listWorkObjectsToClose.add(oWorkObjectToClose);
      }
    }
    iCountWorkObjects = listWorkObjectsToClose.size();
    for(int i = 0; i < iCountWorkObjects; i++) {
      close(listWorkObjectsToClose.get(i));
    }
  }
  
  /**
   * Massimizza l'eventuale internal frame associato all'oggetto.
   *
   * @param oWorkObject Oggetto da massimizzare
   */
  public
  void maximize(Object oWorkObject)
  {
    WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
    if(woinfo == null) return;
    JInternalFrame jif = woinfo.internalFrame;
    if(jif != null) {
      try{ jif.setMaximum(true); } catch(Exception ex ) { ex.printStackTrace(); }
    }
  }
  
  /**
   * Mostra un oggetto Container.
   *
   * @param oContainer Oggetto Container da visualizzare
   */
  protected
  void showContainer(Container oContainer)
  {
    showContainer(oContainer, null, null);
  }
  
  /**
   * Mostra un oggetto Container con la descrizione sulla barra di stato.
   *
   * @param oContainer Oggetto Container da visualizzare
   * @param sDescription Descrizione
   */
  protected
  void showContainer(Container oContainer, String sDescription)
  {
    showContainer(oContainer, sDescription, null);
  }
  
  /**
   * Mostra un oggetto Container con la descrizione sulla barra di stato e l'icona.
   *
   * @param oContainer Oggetto Container da visualizzare
   * @param sDescription Descrizione
   * @param sIcon Icona
   */
  protected
  void showContainer(Container oContainer, String sDescription, String sIcon)
  {
    if(mapWorkObjectsInfo.size() == 0) {
      oCardLayout.last(this);
    }
    
    WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oContainer);
    if(woinfo != null) {
      jTabbedPane.setSelectedComponent(woinfo.placeHolder);
      return;
    }
    
    JInternalFrame jInternalFrame = new SimpleJInternalFrame(sDescription, sIcon, oContainer);
    ResourcesMgr.getStatusBar().setText(sDescription);
    Icon oIcon = jInternalFrame.getFrameIcon();
    JPanel placeHolder = new JPanel();
    jTabbedPane.addTab(sDescription, oIcon, placeHolder);
    jTabbedPane.setSelectedComponent(placeHolder);
    checkMDIFramesTitlesSize();
    
    Status_MouseListener smf = new Status_MouseListener(sDescription);
    WPInternalFrameAdapter wpif = new WPInternalFrameAdapter(oContainer);
    jInternalFrame.addInternalFrameListener(wpif);
    jInternalFrame.addMouseListener(smf);
    
    WorkObjectInfo workObjectInfo = new WorkObjectInfo(oContainer,
      jInternalFrame,
      placeHolder,
      smf,
      wpif);
    mapWorkObjectsInfo.put(oContainer, workObjectInfo);
    listWorkObjects.add(oContainer);
    
    jDesktopPane.add(jInternalFrame);
    jInternalFrame.show();
    jDesktopPane.getDesktopManager().maximizeFrame(jInternalFrame);
  }
  
  /**
   * Mostra un oggetto JInternalFrame.
   *
   * @param jInternalFrame Finestra Interna
   */
  protected
  void showInternalFrame(JInternalFrame jInternalFrame)
  {
    showInternalFrame(jInternalFrame, null);
  }
  
  /**
   * Mostra un oggetto JInternalFrame con la descrizione sulla barra di stato.
   *
   * @param jInternalFrame Finestra Interna
   * @param sDescription Descrizione
   */
  protected
  void showInternalFrame(JInternalFrame jInternalFrame, String sDescription)
  {
    if(mapWorkObjectsInfo.size() == 0) {
      oCardLayout.last(this);
    }
    
    WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(jInternalFrame);
    if(woinfo != null) {
      jTabbedPane.setSelectedComponent(woinfo.placeHolder);
      return;
    }
    
    String sTextStatusBar = null;
    if(sDescription == null) {
      sTextStatusBar = jInternalFrame.getTitle();
    }
    else {
      sTextStatusBar = sDescription;
    }
    ResourcesMgr.getStatusBar().setText(sTextStatusBar);
    String sTitle = jInternalFrame.getTitle();
    Icon oIcon = jInternalFrame.getFrameIcon();
    JPanel placeHolder = new JPanel();
    jTabbedPane.addTab(sTitle, oIcon, placeHolder);
    if(sDescription != null) {
      jTabbedPane.setToolTipTextAt(jTabbedPane.getTabCount() - 1, sDescription);
    }
    else {
      jTabbedPane.setToolTipTextAt(jTabbedPane.getTabCount() - 1, sTitle);
    }
    jTabbedPane.setSelectedComponent(placeHolder);
    checkMDIFramesTitlesSize();
    
    Status_MouseListener smf = new Status_MouseListener(sDescription);
    WPInternalFrameAdapter wpif = new WPInternalFrameAdapter(jInternalFrame);
    jInternalFrame.addInternalFrameListener(wpif);
    jInternalFrame.addMouseListener(smf);
    
    WorkObjectInfo workObjectInfo = new WorkObjectInfo(jInternalFrame,
      jInternalFrame,
      placeHolder,
      smf,
      wpif);
    mapWorkObjectsInfo.put(jInternalFrame, workObjectInfo);
    listWorkObjects.add(jInternalFrame);
    
    jDesktopPane.add(jInternalFrame);
    jInternalFrame.show();
    jDesktopPane.getDesktopManager().maximizeFrame(jInternalFrame);
  }
  
  /**
   * Mostra un oggetto JDialog.
   *
   * @param jdialog Finestra di dialogo
   */
  protected
  void showDialog(JDialog jdialog)
  {
    showDialog(jdialog, null);
  }
  
  /**
   * Mostra un oggetto JDialog con la descrizione sulla barra di stato.
   *
   * @param jdialog Finestra di dialogo
   * @param sDescription Descrizione
   */
  protected
  void showDialog(JDialog jdialog, String sDescription)
  {
    String sTextStatusBar = null;
    if(sDescription == null) {
      sTextStatusBar = jdialog.getTitle();
    }
    else {
      sTextStatusBar = sDescription;
    }
    ResourcesMgr.getStatusBar().setText(sTextStatusBar);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    jdialog.setLocation(screenSize.width/2 - jdialog.getSize().width/2,
      screenSize.height/2 - jdialog.getSize().height/2);
    
    jdialog.setVisible(true);
  }
  
  protected
  void showUserInfo(User user)
  {
    if(user == null) {
      return;
    }
    String sUserInfo = "<html><body>";
    sUserInfo += "<p align=\"center\"><b>Informazioni utente</b></p>";
    sUserInfo += "<p align=\"center\">";
    sUserInfo += "(<i>Per cambiare la password cliccare <a href=\"cp\">qui</a></i>)";
    sUserInfo += "</p>";
    sUserInfo += "<hr>" + user.toHTML() + "<hr>";
    sUserInfo += "</body></html>";
    HtmlBrowser htmlb = new HtmlBrowser("Informazioni utente",
      IConstants.sICON_USER, sUserInfo);
    htmlb.removeDefaultHyperlinkListener();
    htmlb.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          try {
            ResourcesMgr.getGUIManager().showGUIChangePassword(ResourcesMgr.mainFrame, false);
          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    });
    show(htmlb, "Informazioni utente");
  }
  
  /**
   * Inizializza il Pannello.
   *
   * @throws Exception
   */
  private
  void init()
    throws Exception
  {
    mapWorkObjectsInfo = new HashMap();
    listWorkObjects    = new ArrayList();
    oCardLayout        = new CardLayout();
    this.setLayout(oCardLayout);
    workPanelPopupMenu = new WorkPanelPopupMenu();
    jDesktopPane = new JDesktopPane();
    oGUIWorkPanel = buildGUI();
    oMDIWorkPanel = buildMDIGUI();
    this.add(oGUIWorkPanel, "1");
    this.add(oMDIWorkPanel, "2");
  }
  
  private
  Container buildMDIGUI()
    throws Exception
  {
    JPanel oMainPanel = new JPanel();
    oMainPanel.setLayout(new BorderLayout());
    jTabbedPane = new JTabbedPane();
    jTabbedPane.addChangeListener(new ChangeListener() {
      public
      void stateChanged(ChangeEvent e) {
        onChange_jTabbedPane();
      }
    });
    jTabbedPane.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        // isPopupTrigger potrebbe restiruire false in Linux
        if(e.isPopupTrigger() || e.getButton() != MouseEvent.BUTTON1) {
          int iSelectedIndex = jTabbedPane.getSelectedIndex();
          if(iSelectedIndex >= 0 && iSelectedIndex < listWorkObjects.size()) {
            Object oWorkObject = listWorkObjects.get(iSelectedIndex);
            workPanelPopupMenu.setWorkObject(oWorkObject);
            workPanelPopupMenu.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      }
    });
    oMainPanel.add(jTabbedPane, BorderLayout.NORTH);
    jDesktopPane = new JDesktopPane();
    oMainPanel.add(jDesktopPane, BorderLayout.CENTER);
    return oMainPanel;
  }
  
  private
  void onChange_jTabbedPane()
  {
    Component placeHolder = jTabbedPane.getSelectedComponent();
    if(placeHolder == null) {
      return;
    }
    
    JInternalFrame jInternalFrame = null;
    Iterator oItWO = mapWorkObjectsInfo.keySet().iterator();
    while(oItWO.hasNext()) {
      Object oWorkObject = oItWO.next();
      WorkObjectInfo woinfo = (WorkObjectInfo) mapWorkObjectsInfo.get(oWorkObject);
      if(placeHolder.equals(woinfo.placeHolder)) {
        jInternalFrame = woinfo.internalFrame;
        break;
      }
    }
    if(jInternalFrame != null) {
      jDesktopPane.getDesktopManager().activateFrame(jInternalFrame);
      try {
        jInternalFrame.setSelected(true);
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  
  private
  void onInternalFrameOpened(Object oWorkObject)
  {
    if(oWorkObject instanceof IWorkObject) {
      ((IWorkObject) oWorkObject).onOpened();
    }
  }
  
  private
  void checkMDIFramesTitlesSize()
  {
    Dimension oSize = new Dimension(0, 0);
    if(jTabbedPane.getTabCount() > 0) {
      int iHeight = (int) jTabbedPane.getBoundsAt(0).getHeight();
      int iTabRuns = jTabbedPane.getTabRunCount();
       oSize.height = iHeight * iTabRuns + 6;
    }
    jTabbedPane.setPreferredSize(oSize);
    jTabbedPane.setMinimumSize(oSize);
    jTabbedPane.updateUI();
    
    validate();
  }
  
  class WPInternalFrameAdapter extends InternalFrameAdapter
  {
    private Object oWorkObject;
    
    public
    WPInternalFrameAdapter(Object oWorkObject)
    {
      this.oWorkObject = oWorkObject;
    }
    
    public
    void internalFrameClosing(InternalFrameEvent ife)
    {
      close(oWorkObject);
    }
    
    public
    void internalFrameActivated(InternalFrameEvent ife)
    {
      activate(oWorkObject);
    }
    
    public
    void internalFrameOpened(InternalFrameEvent ife)
    {
      onInternalFrameOpened(oWorkObject);
    }
  }
  
  static class WorkObjectInfo
  {
    public Object workObject;
    public JInternalFrame internalFrame;
    public JPanel placeHolder;
    public MouseListener mouseListener;
    public InternalFrameListener internalFrameListener;
    
    public WorkObjectInfo(Object wo,
      JInternalFrame jif,
      JPanel ph,
      MouseListener ml,
      InternalFrameListener ifl)
    {
      this.workObject = wo;
      this.internalFrame = jif;
      this.placeHolder = ph;
      this.mouseListener = ml;
      this.internalFrameListener = ifl;
    }
  }
  
  class CloseLater implements Runnable
  {
    private Object objectToClose = null;
    
    public
    CloseLater(Object object)
    {
      this.objectToClose = object;
    }
    
    public void run()
    {
      close(objectToClose);
    }
  }
  
  class WorkPanelPopupMenu extends JPopupMenu implements ActionListener
  {
    protected JMenuItem jmiCloseThis;
    protected JMenuItem jmiCloseOthers;
    protected JMenuItem jmiCloseAll;
    protected JMenuItem jmiHelp;
    
    protected Object workObject;
    
    public WorkPanelPopupMenu()
    {
      jmiCloseThis = new JMenuItem("Chiudi", ResourcesMgr.getSmallImageIcon(IConstants.sICON_EXIT));
      jmiCloseThis.setActionCommand("close_this");
      jmiCloseThis.addActionListener(this);
      jmiCloseOthers = new JMenuItem("Chiudi altre", ResourcesMgr.getSmallImageIcon(IConstants.sICON_MINUS));
      jmiCloseOthers.setActionCommand("close_others");
      jmiCloseOthers.addActionListener(this);
      jmiCloseAll = new JMenuItem("Chiudi tutte", ResourcesMgr.getSmallImageIcon(IConstants.sICON_CANCEL));
      jmiCloseAll.setActionCommand("close_all");
      jmiCloseAll.addActionListener(this);
      
      jmiHelp = new JMenuItem("Guida", ResourcesMgr.getSmallImageIcon(IConstants.sICON_HELP));
      jmiHelp.setActionCommand("help");
      jmiHelp.addActionListener(this);
      
      this.add(jmiCloseThis);
      this.add(jmiCloseOthers);
      this.add(jmiCloseAll);
      this.addSeparator();
      this.add(jmiHelp);
    }
    
    public
    void setWorkObject(Object workObject)
    {
      this.workObject = workObject;
    }
    
    public
    void actionPerformed(ActionEvent e)
    {
      String sAC = e.getActionCommand();
      if(sAC == null || sAC.length() == 0) return;
      if(sAC.equalsIgnoreCase("close_this")) {
        if(workObject != null) close(workObject);
      }
      else
      if(sAC.equalsIgnoreCase("close_others")) {
        closeAllExcept(workObject);
      }
      else
      if(sAC.equalsIgnoreCase("close_all")) {
        closeAllWorkObjects();
      }
      else
      if(sAC.equalsIgnoreCase("help")) {
        try {
          ResourcesMgr.getGUIManager().showGUIHelp(ResourcesMgr.mainFrame);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
