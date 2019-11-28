package org.dew.swingup;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import org.dew.swingup.components.StatusBar;

/**
 * Frame principale di un'applicazione swingup.
 *
 * @version 1.0
 */
public
class MainFrame extends JFrame implements ActionListener
{
  public static final String sACTIONCOMMAND_APP_CONN     = "app.connessione";
  public static final String sACTIONCOMMAND_APP_DISCONN  = "app.disconnessione";
  public static final String sACTIONCOMMAND_APP_LOCK     = "app.lock";
  public static final String sACTIONCOMMAND_APP_EXIT     = "app.exit";
  public static final String sACTIONCOMMAND_HELP_HELP    = "help.help";
  public static final String sACTIONCOMMAND_HELP_ABOUT   = "help.about";
  
  // Menu
  IMenuManager oMenuManager = null;
  
  JMenu jMenuApp = new JMenu();
  JMenuItem jMenuAppConn    = new JMenuItem();
  JMenuItem jMenuAppDisconn = new JMenuItem();
  JMenuItem jMenuAppLock    = new JMenuItem();
  JMenuItem jMenuAppExit    = new JMenuItem();
  
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpHelp   = new JMenuItem();
  JMenuItem jMenuHelpAbout  = new JMenuItem();
  
  Container oSideMenu;
  JToolBar  jToolBar;
  
  JButton btnConnect;
  JButton btnDisconnect;
  JButton btnLock;
  
  JSplitPane jSplitPane;
  AWorkPanel oWorkPanel;
  StatusBar  statusBar;
  
  boolean boReady = true;
  
  /**
   * Costruttore della finestra principale.
   *
   * @param sTitle Titolo della finestra principale.
   */
  public
  MainFrame(String sTitle)
  {
    try {
      init(sTitle);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione del MainFrame", ex);
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
  }
  
  /**
   * Restituisce il pannello di lavoro.
   *
   * @return Oggetto AWorkPanel
   */
  public
  AWorkPanel getWorkPanel()
  {
    return oWorkPanel;
  }
  
  /**
   * Restituisce la Tool Bar.
   *
   * @return Oggetto JToolBar
   */
  public
  JToolBar getToolBar()
  {
    return jToolBar;
  }
  
  /**
   * Restituisce il riferimento alla Status Bar.
   *
   * @return Oggetto StatusBar
   */
  public
  StatusBar getStatusBar()
  {
    return statusBar;
  }
  
  /**
   * Restituisce il riferimento allo split pane.
   *
   * @return JSplitPane
   */
  public
  JSplitPane getSplitPane()
  {
    return jSplitPane;
  }
  
  /**
   * Gestore eventi.
   *
   * @param e Oggetto ActionEvent
   */
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    
    if(!ResourcesMgr.getMenuManager().notifyDefault(sActionCommand)) {
      return;
    }
    
    if(sActionCommand.equals(sACTIONCOMMAND_APP_CONN)) {
      connect();
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_APP_DISCONN)) {
      disconnect();
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_APP_LOCK)) {
      lock();
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_APP_EXIT)) {
      exit();
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_HELP_HELP)) {
      try {
        ResourcesMgr.getGUIManager().showGUIHelp(this);
      }
      catch(Exception ex) {
        GUIMessage.showException(ex);
      }
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_HELP_ABOUT)) {
      try {
        ResourcesMgr.getGUIManager().showGUIAbout(this);
      }
      catch(Exception ex) {
        GUIMessage.showException(ex);
      }
    }
  }
  
  /**
   * Inizializza il frame.
   *
   * @param sTitle Titolo
   * @throws Exception
   */
  protected
  void init(String sTitle)
    throws Exception
  {
    // Frame setting
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
      public void windowOpened(WindowEvent e) {
        ResourcesMgr.getWorkPanel().onOpened();
        URL urlCDS = ResourcesMgr.getURLCDS();
        if(urlCDS != null) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              try {
                ResourcesMgr.getGUIManager().showGUICDS(ResourcesMgr.mainFrame);
              }
              catch(Exception ex) {
                GUIMessage.showException("Errore durante la visualizzazione della CDS", ex);
              }
            }
          });
        }
      }
    });
    
    JPanel contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(new BorderLayout());
    this.setTitle(sTitle);
    this.setIconImage(ResourcesMgr.getAppIcon(true).getImage());
  }
  
  /**
   * Costruisce il frame principale.
   *
   * @throws Exception
   */
  public
  void build()
    throws Exception
  {
    JPanel contentPane = (JPanel) this.getContentPane();
    
    ISessionManager oSessionMgr = ResourcesMgr.getSessionManager();
    User oUser = oSessionMgr.getUser();
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " MenuManager.build()...");
    oMenuManager = ResourcesMgr.getMenuManager();
    oMenuManager.build();
    
    // Si richiama il getJToolBar prima dell'oMenuManager.update poiche' esso potrebbe
    // abilitare o disabilitare pulsanti custom della toolbar.
    jToolBar = oMenuManager.getJToolBar();
    
    String sUserRole = null;
    if(oUser != null) {
      sUserRole = oUser.getRole();
    }
    System.out.println(ResourcesMgr.sLOG_PREFIX + " MenuManager.update(\"" + sUserRole + "\")...");
    oMenuManager.update(sUserRole);
    
    // MenuBar
    JMenuBar jMenuBar = oMenuManager.getJMenuBar();
    if(jMenuBar == null) {
      jMenuBar = new JMenuBar();
    }
    addDefaultMenuItems(jMenuBar);
    if(ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_SHOW_MENU, true)) {
      setJMenuBar(jMenuBar);
    }
    
    // ToolBar
    if(ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_SHOW_TOOLBAR, true)) {
      if(jToolBar != null) {
        contentPane.add(jToolBar, BorderLayout.NORTH);
        btnConnect = ResourcesMgr.getToolBarButtonByActionCommand(IConstants.sAC_TOOLBAR_CONNECT);
        btnDisconnect = ResourcesMgr.getToolBarButtonByActionCommand(IConstants.sAC_TOOLBAR_DISCONNECT);
        btnLock = ResourcesMgr.getToolBarButtonByActionCommand(IConstants.sAC_TOOLBAR_LOCK);
      }
    }
    
    // Status Bar
    String sStatusBarMask = ResourcesMgr.config.getProperty(IResourceMgr.sAPP_STATUSBAR);
    if(sStatusBarMask != null) {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " new StatusBar(\"" + sStatusBarMask + "\")...");
    }
    else {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " new StatusBar(null)...");
    }
    statusBar = new StatusBar(sStatusBarMask);
    if(oUser != null) {
      boReady = true;
      statusBar.setReady(boReady);
      statusBar.setUser(oUser);
      statusBar.setConnected(true);
      
      jMenuAppConn.setEnabled(false);
      jMenuAppDisconn.setEnabled(true);
      jMenuAppLock.setEnabled(true);
      if(btnConnect != null) {
        btnConnect.setEnabled(false);
      }
      if(btnDisconnect != null) {
        btnDisconnect.setEnabled(true);
      }
      if(btnLock != null) {
        btnLock.setEnabled(true);
      }
    }
    else {
      boReady = false;
      statusBar.setReady(boReady);
      statusBar.setConnected(false);
      
      jMenuAppConn.setEnabled(true);
      jMenuAppDisconn.setEnabled(false);
      jMenuAppLock.setEnabled(false);
      if(btnConnect != null) {
        btnConnect.setEnabled(true);
      }
      if(btnDisconnect != null) {
        btnDisconnect.setEnabled(false);
      }
      if(btnLock != null) {
        btnLock.setEnabled(false);
      }
    }
    
    if(ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_SHOW_STATUSBAR, true)) {
      contentPane.add(statusBar, BorderLayout.SOUTH);
    }
    
    oWorkPanel = ResourcesMgr.getWorkPanel();
    oWorkPanel.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent me) {
        statusBar.setReady(boReady);
      }
    });
    oWorkPanel.setEnabled(oUser != null);
    
    boolean boShowSideMenu = ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_SHOW_SIDEMENU, true);
    oSideMenu = oMenuManager.getSideMenu();
    if(oSideMenu != null && boShowSideMenu) {
      jSplitPane = new JSplitPane();
      jSplitPane.setDividerLocation(oMenuManager.getSideMenuWidth());
      jSplitPane.setOneTouchExpandable(true);
      JPanel jpRight  = new JPanel(new BorderLayout());
      jpRight.add(oWorkPanel, BorderLayout.CENTER);
      
      jSplitPane.setLeftComponent(oSideMenu);
      jSplitPane.setRightComponent(jpRight);
      
      contentPane.add(jSplitPane, BorderLayout.CENTER);
    }
    else {
      contentPane.add(oWorkPanel, BorderLayout.CENTER);
    }
  }
  
  protected
  void addDefaultMenuItems(JMenuBar jMenuBar)
  {
    jMenuApp.setText("Applicazione");
    jMenuApp.setToolTipText("Menu Applicazione");
    jMenuApp.addMouseListener(new Status_MouseListener("Menu Applicazione"));
    jMenuApp.setMnemonic('A');
    
    jMenuAppConn.setText("Connessione");
    jMenuAppConn.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_CONNECT));
    jMenuAppConn.setMnemonic('C');
    jMenuAppConn.setActionCommand(sACTIONCOMMAND_APP_CONN);
    jMenuAppConn.addActionListener(this);
    jMenuAppConn.addMouseListener(new Status_MouseListener("Connessione al server"));
    jMenuAppConn.setEnabled(false);
    
    jMenuAppDisconn.setText("Disconnessione");
    jMenuAppDisconn.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_DISCONNECT));
    jMenuAppDisconn.setMnemonic('D');
    jMenuAppDisconn.setActionCommand(sACTIONCOMMAND_APP_DISCONN);
    jMenuAppDisconn.addActionListener(this);
    jMenuAppDisconn.addMouseListener(new Status_MouseListener("Disconnessione al server"));
    
    jMenuAppLock.setText("Blocco");
    jMenuAppLock.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_LOCK));
    jMenuAppLock.setMnemonic('B');
    jMenuAppLock.setActionCommand(sACTIONCOMMAND_APP_LOCK);
    jMenuAppLock.addActionListener(this);
    jMenuAppLock.addMouseListener(new Status_MouseListener("Blocco applicazione"));
    jMenuAppLock.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    
    jMenuAppExit.setText("Uscita");
    jMenuAppExit.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_EXITSMALL));
    jMenuAppExit.setMnemonic('U');
    jMenuAppExit.setActionCommand(sACTIONCOMMAND_APP_EXIT);
    jMenuAppExit.addActionListener(this);
    jMenuAppExit.addMouseListener(new Status_MouseListener("Esce dall'applicazione"));
    
    jMenuApp.add(jMenuAppConn);
    jMenuApp.add(jMenuAppDisconn);
    jMenuApp.add(jMenuAppLock);
    jMenuApp.addSeparator();
    jMenuApp.add(jMenuAppExit);
    
    jMenuHelp.setText("Help");
    jMenuHelp.setToolTipText("Menu Help");
    jMenuHelp.addMouseListener(new Status_MouseListener("Menu Help"));
    jMenuHelp.setMnemonic('H');
    
    jMenuHelpHelp.setText("Documentazione");
    jMenuHelpHelp.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_HELP));
    jMenuHelpHelp.setMnemonic('D');
    jMenuHelpHelp.setActionCommand(sACTIONCOMMAND_HELP_HELP);
    jMenuHelpHelp.addActionListener(this);
    jMenuHelpHelp.addMouseListener(new Status_MouseListener("Mostra la documentazione"));
    jMenuHelpHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    
    jMenuHelpAbout.setText("Informazioni");
    jMenuHelpAbout.setIcon(ResourcesMgr.getImageIcon(IConstants.sICON_ABOUT));
    jMenuHelpAbout.setMnemonic('I');
    jMenuHelpAbout.setActionCommand(sACTIONCOMMAND_HELP_ABOUT);
    jMenuHelpAbout.addActionListener(this);
    jMenuHelpAbout.addMouseListener(new Status_MouseListener("Mostra le informazioni dell'applicazione"));
    jMenuHelpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    
    jMenuHelp.add(jMenuHelpHelp);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuHelpAbout);
    
    jMenuBar.add(jMenuApp, 0);
    jMenuBar.add(jMenuHelp);
  }
  
  /**
   * Chiusura dell'applicazione.
   */
  public
  void exit()
  {
    ISessionManager oSessionManager = ResourcesMgr.getSessionManager();
    
    if(oSessionManager.isActive()) {
      boolean boAskConfirmClose = ResourcesMgr.getBooleanProperty(IResourceMgr.sAPP_ASK_CONFIRM_CLOSE, true);
      if(boAskConfirmClose) {
        int iResult = JOptionPane.showConfirmDialog(this, IConstants.sTEXT_MSG_CLOSE_SESSION, IConstants.sTEXT_CONFERMA, JOptionPane.YES_NO_OPTION);
        if(iResult != JOptionPane.YES_OPTION) return;
      }
    }
    
    if(oWorkPanel != null) {
      if(!oWorkPanel.onClosing()) {
        return;
      }
    }
    
    if(oSessionManager.isActive()) {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.beforeLogout()...");
      ResourcesMgr.getApplicationListener().beforeLogout();
      System.out.println(ResourcesMgr.sLOG_PREFIX + " SessionManager.logout()...");
      oSessionManager.logout();
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.end()...");
    ResourcesMgr.getApplicationListener().end();
    System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
    System.exit(0);
  }
  
  /**
   * Effettua la connessione al server.
   *
   * @return boolean
   */
  public
  boolean connect()
  {
    System.out.println(ResourcesMgr.sLOG_PREFIX + " MainFrame.connect()...");
    
    boolean boResult = false;
    try {
      boResult = ResourcesMgr.getGUIManager().showGUILogin(this);
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
      return false;
    }
    
    if(!boResult) {
      boReady = false;
      statusBar.setReady(boReady);
      statusBar.setConnected(false);
      return false;
    }
    
    StaticActionListeners.removeAll();
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.afterLogin()...");
    ResourcesMgr.getApplicationListener().afterLogin();
    
    SessionUtil.checkExpiringPassword();
    
    try {
      boResult = ResourcesMgr.getGUIManager().showGUIUserMessage(this);
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
      return false;
    }
    
    if(!boResult) {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.beforeLogout()...");
      ResourcesMgr.getApplicationListener().beforeLogout();
      System.out.println(ResourcesMgr.sLOG_PREFIX + " SessionManager.logout()...");
      ResourcesMgr.getSessionManager().logout();
      boReady = false;
      statusBar.setReady(boReady);
      statusBar.setConnected(false);
      return false;
    }
    
    jMenuAppConn.setEnabled(false);
    jMenuAppDisconn.setEnabled(true);
    jMenuAppLock.setEnabled(true);
    if(btnConnect != null) {
      btnConnect.setEnabled(false);
    }
    if(btnDisconnect != null) {
      btnDisconnect.setEnabled(true);
    }
    if(btnLock != null) {
      btnLock.setEnabled(true);
    }
    
    User oUser = ResourcesMgr.getSessionManager().getUser();
    
    String sUserRole = oUser.getRole();
    System.out.println(ResourcesMgr.sLOG_PREFIX + " MenuManager.update(\"" + sUserRole + "\")...");
    oMenuManager.update(sUserRole);
    
    boReady = true;
    statusBar.setReady(boReady);
    statusBar.setUser(oUser);
    statusBar.setConnected(true);
    oWorkPanel.setEnabled(true);
    
    AutoLockTimer oALT = ResourcesMgr.getAutoLockTimer();
    oALT.start();
    
    URL urlCDS = ResourcesMgr.getURLCDS();
    if(urlCDS != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            ResourcesMgr.getGUIManager().showGUICDS(ResourcesMgr.mainFrame);
          }
          catch(Exception ex) {
            GUIMessage.showException("Errore durante la visualizzazione della CDS", ex);
          }
        }
      });
    }
    
    return true;
  }
  
  /**
   * Effettua la disconnessione.
   *
   * @return boolean
   */
  public
  boolean disconnect()
  {
    int iResult = JOptionPane.showConfirmDialog(this, IConstants.sTEXT_MSG_CLOSE_SESSION, IConstants.sTEXT_CONFERMA, JOptionPane.YES_NO_OPTION);
    if(iResult != JOptionPane.YES_OPTION) return false;
    
    if(oWorkPanel != null) {
      if(!oWorkPanel.onDisconnecting()) {
        return false;
      }
    }
    
    AutoLockTimer oALT = ResourcesMgr.getAutoLockTimer();
    oALT.stop();
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.beforeLogout()...");
    ResourcesMgr.getApplicationListener().beforeLogout();
    System.out.println(ResourcesMgr.sLOG_PREFIX + " SessionManager.logout()...");
    ResourcesMgr.getSessionManager().logout();
    
    StaticActionListeners.removeAll();
    
    jMenuAppConn.setEnabled(true);
    jMenuAppDisconn.setEnabled(false);
    jMenuAppLock.setEnabled(false);
    if(btnConnect != null) {
      btnConnect.setEnabled(true);
    }
    if(btnDisconnect != null) {
      btnDisconnect.setEnabled(false);
    }
    if(btnLock != null) {
      btnLock.setEnabled(false);
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " MenuManager.update(null)...");
    oMenuManager.update(null);
    
    AWorkPanel oWorkPanel = ResourcesMgr.getWorkPanel();
    oWorkPanel.closeAll();
    oWorkPanel.setEnabled(false);
    
    boReady = false;
    statusBar.setReady(boReady);
    statusBar.setConnected(false);
    
    return true;
  }
  
  /**
   * Effettua il blocco dell'applicazione.
   */
  public
  void lock()
  {
    if(SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          doLock();
        }
      });
    }
    else {
      doLock();
    }
  }
  
  protected
  void doLock()
  {
    if(ResourcesMgr.bLocked) return;
    try {
      AutoLockTimer oALT = ResourcesMgr.getAutoLockTimer();
      oALT.stop();
      
      AWorkPanel oWorkPanel = ResourcesMgr.getWorkPanel();
      int iFramesCount = oWorkPanel.getFramesCount();
      if(iFramesCount > 0) {
        oWorkPanel.showMainPanel();
      }
      
      ResourcesMgr.bLocked = true;
      
      ResourcesMgr.getGUIManager().showGUILock(this);
      
      ResourcesMgr.bLocked = false;
      
      // In alcuni meccanismi di lock si potrebbe desiderare anche la disconnessione
      // per cui non si procede con le successive righe di codice
      if(!boReady) return;
      
      if(iFramesCount > 0) {
        oWorkPanel.showMDIPanel();
      }
      
      oALT.start();
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
}
