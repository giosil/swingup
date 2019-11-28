package org.dew.swingup.main;

import java.awt.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.dialog.*;

/**
 * Classe che inizializza l'applicazione e fa partire la GUI.
 *
 * @version 1.0
 */
public
class Main
{
  /**
   * Argomenti dell'applicazione.
   */
  public static String[] arguments;
  
  public Main()
  {
  }
  
  /**
   * Avvio dell'applicazione swingup.
   *
   * @throws Exception
   */
  public
  void start()
    throws Exception
  {
    System.out.println(ResourcesMgr.sLOG_PREFIX + " start application...");
    
    // inizializza l'applicazione e mostra la splash screen
    init();
    
    // Mostra il disclaimer
    showDisclaimer();
    
    // Mostra la maschera di Login ed un eventuale messaggio utente
    doLogin();
    
    // Mostra il frame principale
    showMainFrame();
  }
  
  public static
  void main(String[] args)
  {
    arguments = args;
    Main main = new Main();
    try{
      main.start();
    }
    catch(Throwable th) {
      th.printStackTrace();
      
      String sMsg = th.getMessage();
      if(sMsg == null) sMsg = th.toString();
      JOptionPane.showMessageDialog(null, sMsg, "Errore", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  protected
  void doLogin()
  {
    System.out.println(ResourcesMgr.sLOG_PREFIX + " login...");
    
    IGUIManager oGUIMgr = ResourcesMgr.getGUIManager();
    try {
      if(!autoLogin()) {
        if(!oGUIMgr.showGUILogin(null)) {
          System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.end()...");
          ResourcesMgr.getApplicationListener().end();
          System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
          System.exit(1);
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.afterLogin()...");
    ResourcesMgr.getApplicationListener().afterLogin();
    
    SessionUtil.checkExpiringPassword();
    
    try {
      if(!oGUIMgr.showGUIUserMessage(null)) {
        System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.beforeLogout()...");
        ResourcesMgr.getApplicationListener().beforeLogout();
        System.out.println(ResourcesMgr.sLOG_PREFIX + " SessionManager.logout()...");
        ResourcesMgr.getSessionManager().logout();
        System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.end()...");
        ResourcesMgr.getApplicationListener().end();
        System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
        System.exit(1);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
  }
  
  protected
  boolean autoLogin()
  {
    boolean boAutologin = ResourcesMgr.getBooleanProperty(ResourcesMgr.sGUILOGIN_AUTOLOGIN);
    if(!boAutologin) return false;
    
    ISessionManager oSessionMgr = ResourcesMgr.getSessionManager();
    if(oSessionMgr == null) return false;
    
    String sIdService = ResourcesMgr.getStringProperty(ResourcesMgr.sGUILOGIN_IDSERVICE);
    String sIdClient  = ResourcesMgr.getStringProperty(ResourcesMgr.sGUILOGIN_IDCLIENT, "DEFAULT");
    
    String sIdSession = ResourcesMgr.getStringProperty(ResourcesMgr.sGUILOGIN_ID_SESSION);
    if(sIdSession != null && sIdSession.length() > 0 && !sIdSession.equals("0")) {
      try {
        System.out.println(ResourcesMgr.sLOG_PREFIX + " autologin for session " + sIdSession + "...");
        oSessionMgr.login("*", sIdSession, "", sIdClient);
      }
      catch(Throwable th) {
        th.printStackTrace();
        return false;
      }
      return oSessionMgr.isActive();
    }
    
    String sSSOIdSession = ResourcesMgr.getStringProperty(ResourcesMgr.sGUILOGIN_SSO_SESSION);
    if(sSSOIdSession != null && sSSOIdSession.length() > 0 && !sSSOIdSession.equals("0")) {
      try {
        System.out.println(ResourcesMgr.sLOG_PREFIX + " autologin for SSO session " + sSSOIdSession + "...");
        oSessionMgr.login(sIdService, sSSOIdSession, "", sIdClient);
      }
      catch(Throwable th) {
        th.printStackTrace();
        return false;
      }
      return oSessionMgr.isActive();
    }
    
    String sUsername = ResourcesMgr.getStringProperty(ResourcesMgr.sGUILOGIN_DEFUSERNAME);
    if(sUsername == null || sUsername.length() == 0) return false;
    String sPassword = ResourcesMgr.getStringProperty(ResourcesMgr.sGUILOGIN_DEFPASSWORD);
    if(sPassword == null || sPassword.length() == 0) return false;
    try {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " autologin for " + sUsername + "...");
      oSessionMgr.login(sIdService, sUsername, sPassword, sIdClient);
      // La password viene resettata in caso di autologin in maniera che all'accesso successivo
      // (senza chiudere l'applicazione) venga eventualmente proposta soltanto la username.
      ResourcesMgr.config.remove(ResourcesMgr.sGUILOGIN_DEFPASSWORD);
    }
    catch(Throwable th) {
      th.printStackTrace();
      return false;
    }
    if(!oSessionMgr.isActive()) {
      GUIMessage.showWarning("L'accesso al servizio non ha avuto successo per le credenziali specificate.");
      return false;
    }
    return true;
  }
  
  protected
  void showMainFrame()
  {
    MainFrame frame = ResourcesMgr.mainFrame;
    
    try {
      ResourcesMgr.loadWorkPanel();
      ResourcesMgr.loadMenuManager();
    }
    catch(Exception ex) {
      ex.printStackTrace();
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " build and show main frame...");
    try {
      frame.build();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la costruzione del MainFrame", ex);
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
    SwingUtilities.updateComponentTreeUI(frame);
    frame.pack();
    
    int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    screenHeight = screenHeight - screenHeight / 27;
    
    frame.setSize(new Dimension(screenWidth, screenHeight));
    
    frame.setVisible(true);
    
    AutoLockTimer oALT = ResourcesMgr.getAutoLockTimer();
    oALT.start();
  }
  
  protected
  void init()
    throws Exception
  {
    System.out.println(ResourcesMgr.sLOG_PREFIX + " show splash screen...");
    
    URL urlImageSplash = ResourcesMgr.getURLResource(ResourcesMgr.sEnvPrefix + IConstants.sRES_SPLASH);
    
    JWindow splashScreen = null;
    if(urlImageSplash != null) {
      ImageIcon imageSplash = new ImageIcon(urlImageSplash);
      
      JLabel splashLabel = new JLabel(imageSplash);
      splashLabel.setBorder(BorderFactory.createLineBorder(Color.black));
      splashScreen = new JWindow();
      splashScreen.getContentPane().add(splashLabel);
      splashScreen.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      splashScreen.setLocation(screenSize.width/2 - splashScreen.getSize().width/2,
        screenSize.height/2 - splashScreen.getSize().height/2);
      splashScreen.setVisible(true);
    }
    else {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " " + IConstants.sRES_SPLASH + " not available.");
    }
    
    // Carica il file dei configurazione.
    ResourcesMgr.loadConfig();
    
    // Controllo JVM
    if(!checkJVM()) {
      String sJVM = ResourcesMgr.config.getProperty(IResourceMgr.sAPP_JVM);
      GUIMessage.showWarning("Per eseguire correttamente l'applicazione occorre la Java Virtual Machine versione " + sJVM + ".");
      if(splashScreen != null) {
        splashScreen.setVisible(false);
      }
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
    
    // Carica il file dati salvato nel client.
    ResourcesMgr.loadDat();
    
    // Inizializza l'applicazione
    ResourcesMgr.init();
    
    // Creazione del Main Frame (in alcuni casi puo' esser comodo avere
    // gia' nella fase di partenza un riferimento al frame principale)
    Properties cfg = ResourcesMgr.config;
    String sAppName = cfg.getProperty(ResourcesMgr.sAPP_NAME);
    String sAppVers = cfg.getProperty(ResourcesMgr.sAPP_VERSION);
    System.out.println(ResourcesMgr.sLOG_PREFIX + " create main frame...");
    MainFrame frame = new MainFrame(sAppName + " ver. " + sAppVers);
    ResourcesMgr.mainFrame = frame;
    
    // Costruisce la window del messaggio di attesa
    ResourcesMgr.buildWaitPleaseWindow(null);
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " ApplicationListener.start()...");
    ResourcesMgr.getApplicationListener().start();
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " GUIManager.updateUIManager()...");
    ResourcesMgr.getGUIManager().updateUIManager();
    
    try {
      Thread.sleep(2000);
    }
    catch(InterruptedException ex) {
    }
    
    if(splashScreen != null) {
      splashScreen.setVisible(false);
    }
  }
  
  protected
  boolean checkJVM()
  {
    String sJVM = ResourcesMgr.config.getProperty(IResourceMgr.sAPP_JVM);
    if(sJVM == null || sJVM.trim().length() == 0) {
      return true;
    }
    int iFirstDot = sJVM.indexOf('.');
    if(iFirstDot < 0) return true;
    int iSecondDot = sJVM.indexOf('.', iFirstDot + 1);
    if(iSecondDot < 0) iSecondDot = sJVM.length();
    String sCfgV1 = sJVM.substring(0, iFirstDot);
    String sCfgV2 = sJVM.substring(iFirstDot + 1, iSecondDot);
    boolean boGreater = sCfgV2.charAt(sCfgV2.length() - 1) == '+';
    
    String sJavaVersion = System.getProperty("java.version");
    System.out.println(ResourcesMgr.sLOG_PREFIX + " checkJVM " + sJVM + " / " + sJavaVersion + "...");
    if(sJavaVersion == null) return true;
    iFirstDot = sJavaVersion.indexOf('.');
    if(iFirstDot < 0) return true;
    iSecondDot = sJavaVersion.indexOf('.', iFirstDot + 1);
    if(iSecondDot < 0) iSecondDot = sJavaVersion.length();
    
    String sV1 = sJavaVersion.substring(0, iFirstDot);
    String sV2 = sJavaVersion.substring(iFirstDot + 1, iSecondDot);
    
    if(boGreater) {
      int iCfgV1 = 0;
      try{ iCfgV1 = Integer.parseInt(sCfgV1); } catch(Exception ex) {}
      int iV1 = 0;
      try{ iV1 = Integer.parseInt(sV1); } catch(Exception ex) {}
      if(iV1 < iCfgV1) {
        return false;
      }
    }
    else {
      if(!sCfgV1.equals(sV1)) {
        return false;
      }
    }
    
    if(boGreater) {
      sCfgV2 = sCfgV2.substring(0, sCfgV2.length() - 1);
      int iCfgV2 = 0;
      try{ iCfgV2 = Integer.parseInt(sCfgV2); } catch(Exception ex) {}
      int iV2 = 0;
      try{ iV2 = Integer.parseInt(sV2); } catch(Exception ex) {}
      if(iV2 < iCfgV2) {
        return false;
      }
    }
    else {
      if(!sCfgV2.equals(sV2)) {
        return false;
      }
    }
    
    return true;
  }
  
  protected
  void showDisclaimer()
  {
    boolean boShowDisclaimer = ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_SHOW_DISCLAIMER, true);
    
    if(!boShowDisclaimer) {
      return;
    }
    
    String sDisclaimer = ResourcesMgr.getDisclaimer();
    
    if(sDisclaimer == null || sDisclaimer.length() == 0) {
      return;
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " show disclaimer...");
    
    Properties cfg = ResourcesMgr.config;
    String sAppName = cfg.getProperty(ResourcesMgr.sAPP_NAME);
    String sAppVers = cfg.getProperty(ResourcesMgr.sAPP_VERSION);
    
    TextDialog textDialog = new TextDialog(sAppName + " ver. " + sAppVers);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    
    textDialog.setText(sDisclaimer);
    
    textDialog.setVisible(true);
    
    if(textDialog.isCancel()) {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
      System.exit(1);
    }
  }
}
