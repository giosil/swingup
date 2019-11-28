package org.dew.swingup;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.components.StatusBar;
import org.dew.swingup.file.DataFileUtil;
import org.dew.swingup.impl.*;
import org.dew.swingup.log.*;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.Base64Enc;

/**
 * Gestore delle risorse dell'applicazione.
 *
 * @version 1.0
 */
public
class ResourcesMgr implements IResourceMgr
{
  /**
   * Riferimento al Frame principale
   */
  public static MainFrame mainFrame = null;
  
  /**
   * Configurazione del client
   */
  public static Properties dat = new Properties();
  
  /**
   * Configurazione dell'applicativo
   */
  public static Properties config = new Properties();
  
  /**
   * Flag debug
   */
  public static boolean bDebug = false;
  
  /**
   * Flag demo
   */
  public static boolean bDemo = false;
  
  /**
   * Flag locked
   */
  public static boolean bLocked = false;
  
  private static IApplicationListener oApplicationListener;
  private static IMenuManager oMenuManager;
  private static IGUIManager oGUIManager;
  private static ISessionManager oSessionManager;
  private static AWorkPanel oWorkPanel;
  private static IRPCClient oRPCClient;
  private static ILogger oLogger;
  private static IErrorSender oErrorSender;
  private static JLabel lblWaitPlease;
  private static JWindow oWinWaitPlease;
  private static boolean boAtLeastACustomWaitMessage = false;
  private static AutoLockTimer oALT;
  private static String sURLCDSFromConfig;
  private static DateFormat defaultDateFormat;
  private static DecimalFormat defaultDecimalFormat;
  
  /**
   * Individua l'ambiente da far partire.
   */
  public static String sEnvPrefix;
  static {
    sEnvPrefix = System.getProperty("jsenv", "");
    if(sEnvPrefix == null || sEnvPrefix.length() == 0) {
      sEnvPrefix = System.getProperty("jnlp.jsenv", "");
    }
    if(sEnvPrefix.length() > 0) {
      sEnvPrefix += "_";
    }
  }
  
  /**
   * Inizializza l'applicazione swingup.
   * Attenzione: occorre invocare prima loadConfig().
   * Tale metodo esegue nell'ordine:
   * 1. Impostazione del LookAndFeel
   * 2. Impostazione del tema di colori
   * 3. loadLogger()
   * 4. loadSessionManager()
   * 5. loadGUIManager()
   * 6. loadErrorSender()
   * 7. loadApplicationListener()
   * 8. Inizializzazione di AutoLockTimer (per la gestione dell'autolock)
   * @throws Exception
   */
  public static
  void init()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " swingup init...");
    
    // Imposta il Look And Feel
    String sLookAndFeel = config.getProperty(ResourcesMgr.sAPP_PLAF, "javax.swing.plaf.metal.MetalLookAndFeel");
    System.out.println(sLOG_PREFIX + " UIManager.setLookAndFeel(\"" + sLookAndFeel + "\")...");
    try {
      Class.forName(sLookAndFeel);
    }
    catch(Exception ex) {
      System.out.println(sLOG_PREFIX + " LookAndFeel " + sLookAndFeel + " not available.");
      sLookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
    }
    UIManager.setLookAndFeel(sLookAndFeel);
    // Imposta il tema di colori
    String sTheme = config.getProperty(ResourcesMgr.sAPP_THEME);
    if(sTheme != null) {
      System.out.println(sLOG_PREFIX + " ThemeManager.setTheme(\"" + sTheme + "\")");
      ThemeManager.setTheme(sTheme);
    }
    
    loadLogger();
    loadSessionManager();
    loadGUIManager();
    loadErrorSender();
    loadApplicationListener();
    oALT = new AutoLockTimer();
  }
  
  /**
   * Chiude forzatamente l'applicazione con il codice di uscita specificato.
   *
   * @param iExitCode int
   */
  public static
  void abort(int iExitCode)
  {
    System.out.println(ResourcesMgr.sLOG_PREFIX + " abort(" + iExitCode + ")");
    System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
    System.exit(iExitCode);
  }
  
  /**
   * Restituisce l'oggetto DateFormat di default.
   * L'implementazione predefinita di java tiene conto delle impostazioni
   * internazionali, ma formatta anche l'ora mentre molto spesso occorre
   * formattare esclusivamente la data.
   * L'implementazione proposta in swingup utilizza il pattern MM/dd/yyyy
   * se user.country e' US oppure EN. Altrimenti usa dd/MM/yyyy.
   *
   * @return DateFormat
   */
  public static
  DateFormat getDefaultDateFormat()
  {
    if(defaultDateFormat != null) return defaultDateFormat;
    
    String sUserCountry = System.getProperty("user.country");
    if(sUserCountry != null &&
      (sUserCountry.equalsIgnoreCase("US") ||
      sUserCountry.equalsIgnoreCase("EN"))) {
      defaultDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    }
    else {
      defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }
    
    return defaultDateFormat;
  }
  
  /**
   * Imposta l'oggetto DateFormat di default.
   *
   * @param theDefaultDateFormat DateFormat
   */
  public static
  void setDefaultDateFormat(DateFormat theDefaultDateFormat)
  {
    defaultDateFormat = theDefaultDateFormat;
  }
  
  /**
   * Restituisce l'oggetto DecimalFormat di default utilizzato
   * per la formattazione delle valute.
   * L'implementazione proposta in swingup utilizza il pattern #,##0.00.
   *
   * @return DecimalFormat
   */
  public static
  DecimalFormat getDefaultDecimalFormat()
  {
    if(defaultDecimalFormat != null) return defaultDecimalFormat;
    defaultDecimalFormat = new DecimalFormat("#,##0.00");
    return defaultDecimalFormat;
  }
  
  /**
   * Imposta l'oggetto DecimalFormat di default.
   *
   * @param theDefaultDecimalFormat DecimalFormat
   */
  public static
  void setDefaultDecimalFormat(DecimalFormat theDefaultDecimalFormat)
  {
    defaultDecimalFormat = theDefaultDecimalFormat;
  }
  
  /**
   * Restituisce l'oggetto Logger che consente di tracciare delle informazioni
   * su un file di log locale.
   *
   * @return Oggetto Logger
   */
  public static
  ILogger getLogger()
  {
    return oLogger;
  }
  
  /**
   * Restituisce un oggetto IRPCClient identificato dalla classe specificata
   * dal parametro swingup.rpc del file di configurazione.
   *
   * @return IRPCClient
   */
  public static
  IRPCClient getDefaultRPCClient()
  {
    return oRPCClient;
  }
  
  /**
   * Restituisce un oggetto IApplicationListener identificato dalla classe
   * specificata dal parametro swingup.listener del file di configurazione.
   *
   * @return IApplicationListener
   */
  public static
  IApplicationListener getApplicationListener()
  {
    return oApplicationListener;
  }
  
  /**
   * Restituisce un oggetto IMenuManager identificato dalla classe specificata
   * dal parametro swingup.menu del file di configurazione.
   * Esso rappresenta il gestore del menu dell'applicazione.
   *
   * @return IMenuManager
   */
  public static
  IMenuManager getMenuManager()
  {
    return oMenuManager;
  }
  
  /**
   * Restituisce un oggetto IGUIManager identificato dalla classe specificata
   * dal parametro swingup.gui del file di configurazione.
   * Esso rappresenta il gestore delle principali gui dell'applicazione.
   *
   * @return IGUILoginManager
   */
  public static
  IGUIManager getGUIManager()
  {
    return oGUIManager;
  }
  
  /**
   * Restituisce un oggetto ISessionManager identificato dalla classe specificata
   * dal parametro swingup.session del file di configurazione.
   * Esso rappresenta il gestore della sessione utente.
   *
   * @return ISessionManager
   */
  public static
  ISessionManager getSessionManager()
  {
    return oSessionManager;
  }
  
  /**
   * Restituisce il Pannello di Lavoro.
   *
   * @return AWorkPanel
   */
  public static
  AWorkPanel getWorkPanel()
  {
    return oWorkPanel;
  }
  
  /**
   * Restituisce l'oggetto IErrorSender che consente di inviare errori
   * ad un sistema di rilevazione.
   *
   * @return Oggetto IErrorSender
   */
  public static
  IErrorSender getErrorSender()
  {
    return oErrorSender;
  }
  
  /**
   * Restituisce il riferimento alla barra di stato dell'applicazione.
   *
   * @return StatusBar
   */
  public static
  StatusBar getStatusBar()
  {
    if(mainFrame == null) return null;
    return mainFrame.getStatusBar();
  }
  
  /**
   * Restituisce il riferimento all'oggetto AutoLockTimer utilizzato
   * per l'auto lock dell'applicazione.
   *
   * @return AutoLockTimer
   */
  public static
  AutoLockTimer getAutoLockTimer()
  {
    return oALT;
  }
  
  /**
   * Restituisce una nuova istanza della classe specificata
   * dal parametro di configurazione swingup.rpc.
   *
   * @return Oggetto IRPCClient.
   */
  public static
  IRPCClient createIRPCClient()
  {
    System.out.println(sLOG_PREFIX + " create RPC client...");
    
    String sRPCClientClassName = config.getProperty(sAPP_RPC);
    if(sRPCClientClassName == null) {
      oLogger.error(sLOG_PREFIX + " createIRPCClient: " + sAPP_RPC + " is null");
      System.out.println(sLOG_PREFIX + " " + sAPP_RPC + " is null.");
      return null;
    }
    
    Class<?> oRPCClientClass = null;
    try {
      oRPCClientClass = Class.forName(sRPCClientClassName);
    }
    catch(ClassNotFoundException ex) {
      oLogger.error(sLOG_PREFIX + " createIRPCClient", ex);
      ex.printStackTrace();
      return null;
    }
    
    Object oNewInstance = null;
    try {
      oNewInstance = oRPCClientClass.newInstance();
    }
    catch(InstantiationException ex) {
      oLogger.error(sLOG_PREFIX + " createIRPCClient", ex);
      ex.printStackTrace();
      return null;
    }
    catch(IllegalAccessException ex) {
      oLogger.error(sLOG_PREFIX + " createIRPCClient", ex);
      ex.printStackTrace();
      return null;
    }
    
    if(!(oNewInstance instanceof IRPCClient)) {
      oLogger.error(sLOG_PREFIX + "createIRPCClient: " + sRPCClientClassName + " is not IRPCClient");
      System.out.println(sLOG_PREFIX + " " + sRPCClientClassName + " is not IRPCClient.");
      return null;
    }
    
    System.out.println(sLOG_PREFIX + " " + oNewInstance.getClass().getName() + " loaded");
    
    IRPCClient rpcClient = ((IRPCClient) oNewInstance);
    
    int iRPCTimeOut = getIntProperty(sAPP_RPC_TIMEOUT, 30000);
    rpcClient.setTimeOut(iRPCTimeOut);
    System.out.println(sLOG_PREFIX + " RPC TimeOut = " + iRPCTimeOut);
    
    boolean boBasicAuthentication = getBooleanProperty(sAPP_RPC_BASIC_AUTH);
    if(boBasicAuthentication) {
      ISessionManager sessionManager = getSessionManager();
      if(sessionManager != null) {
        User user = sessionManager.getUser();
        if(user != null) {
          String sUserName = user.getUserName();
          if(sUserName != null && sUserName.length() > 0) {
            String sPass  = null;
            String sToken = user.getToken();
            if(sToken != null && sToken.length() > 0) {
              sPass = sToken;
            }
            else {
              sPass = user.getPassword();
              if(sPass == null || sPass.length() == 0) {
                int iPasswordHashCode = user.getPasswordHashCode();
                if(iPasswordHashCode != 0) {
                  sPass = "#" + iPasswordHashCode;
                }
                else {
                  sPass = "?";
                }
              }
            }
            Map<String,Object> mapHeaders = new HashMap<String,Object>(1);
            mapHeaders.put("Authorization", "Basic " + Base64Enc.encodeString(sUserName + ":" + sPass));
            rpcClient.setHeaders(mapHeaders);
            System.out.println(sLOG_PREFIX + " RPC Basic Authentication activated for " + sUserName);
          }
        }
      }
    }
    return rpcClient;
  }
  
  /**
   * Imposta l'oggetto IRPCClient di default.
   *
   * @param oDefaultRPCClient RPC Client predefinito
   */
  public static
  void setDefaultRPCClient(IRPCClient oDefaultRPCClient)
  {
    oRPCClient = oDefaultRPCClient;
  }
  
  /**
   * Restituisce una icona presente in <b>icone.jar</b>.
   *
   * @param sIconName Nome del file che rappresenta l'icona.
   * @return ImageIcon
   */
  public static
  ImageIcon getImageIcon(String sIconName)
  {
    if(sIconName == null || sIconName.length() == 0) sIconName = IConstants.sICON_DEFAULT;
    URL urlResource = Thread.currentThread().getContextClassLoader().getResource("icons/" + sIconName);
    if(urlResource == null) {
      urlResource = Thread.currentThread().getContextClassLoader().getResource("icons/" + IConstants.sICON_DEFAULT);
    }
    return new ImageIcon(urlResource);
  }
  
  /**
   * Restituisce una icona presente in <b>icone.jar</b>.
   * La dimensione dell'icona restituita e' 16x16.
   * Se l'icona caricata e' piu' grande viene ridotta.
   *
   * @param sIconName Nome del file che rappresenta l'icona.
   * @return ImageIcon
   */
  public static
  ImageIcon getSmallImageIcon(String sIconName)
  {
    if(sIconName == null || sIconName.length() == 0) sIconName = IConstants.sICON_DEFAULT;
    
    URL urlResource = Thread.currentThread().getContextClassLoader().getResource("icons/" + sIconName);
    if(urlResource == null) {
      urlResource = Thread.currentThread().getContextClassLoader().getResource("icons/" + IConstants.sICON_DEFAULT);
    }
    
    ImageIcon oIcon = new ImageIcon(urlResource);
    if(oIcon.getIconWidth() > 16 || oIcon.getIconHeight() > 16) {
      return new ImageIcon(oIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
    }
    
    return oIcon;
  }
  
  /**
   * Restituisce l'URL della Comunicazione di Servizio.
   *
   * @return URL della comunicazione di servizio.
   */
  public static
  URL getURLCDS()
  {
    URL url = null;
    String sURLCDS = config.getProperty(sAPP_CDS);
    try {
      if(sURLCDS != null && sURLCDS.length() > 0) url = new URL(sURLCDS);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    return url;
  }
  
  /**
   * Reimposta l'URL della Comunicazione di Servizio al valore
   * presente nel file di configurazione.
   */
  public static
  void resumeURLCDSFromConfig()
  {
    System.out.println(sLOG_PREFIX + " resumeURLCDSFromConfig: " + sURLCDSFromConfig);
    if(sURLCDSFromConfig != null) {
      config.setProperty(sAPP_CDS, sURLCDSFromConfig);
    }
    else {
      config.remove(sAPP_CDS);
    }
  }
  
  /**
   * Restituisce l'URL della pagina iniziale della guida in linea presente
   * in <b>help.jar</b> o disponibile in un sito web (cf. swingup.help).
   *
   * @param sHelpDoc Documento di help
   * @return URL della pagina iniziale della guida in linea.
   */
  public static
  URL getURLHelp(String sHelpDoc)
  {
    URL url = null;
    
    String sURLHelp = config.getProperty(sAPP_HELP);
    try {
      if(sURLHelp != null && sURLHelp.length() > 0) {
        if(sHelpDoc != null && sHelpDoc.length() > 0) {
          int iLastSlash = sURLHelp.lastIndexOf('/');
          if(iLastSlash >= 0) {
            sURLHelp = sURLHelp.substring(0, iLastSlash + 1) + sHelpDoc;
          }
          else {
            sURLHelp += sHelpDoc;
          }
        }
        url = new URL(sURLHelp);
      }
      else {
        String sResource = IConstants.sRES_HELP;
        if(sHelpDoc != null && sHelpDoc.length() > 0) {
          int iLastSlash = sResource.lastIndexOf('/');
          if(iLastSlash >= 0) {
            sResource = sResource.substring(0, iLastSlash + 1) + sHelpDoc;
          }
          else {
            sResource += sHelpDoc;
          }
        }
        url = Thread.currentThread().getContextClassLoader().getResource("help/" + sResource);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    
    return url;
  }
  
  /**
   * Restituisce l'URL di una risorsa.
   *
   * @param sResource Risorsa
   * @return URL risorsa
   */
  public static
  URL getURLResource(String sResource)
  {
    return Thread.currentThread().getContextClassLoader().getResource(sResource);
  }
  
  /**
   * Restituisce l'URL di un file dati.
   *
   * @param sDataFile File dati.
   * @return URL file dati.
   */
  public static
  URL getURLDataFile(String sDataFile)
  {
    String sPath = IConstants.sRES_DATA_FOLDER + "/" + sDataFile;
    return Thread.currentThread().getContextClassLoader().getResource(sPath);
  }
  
  /**
   * Restituisce l'URL del template utilizzato per la generazione di un report.
   * I template devono essere collocati nella cartella reports delle risorse.
   *
   * @param sFileName Nome del file modello
   * @return URL del template
   */
  public static
  URL getReportTemplate(String sFileName)
  {
    String sPath = IConstants.sRES_REPORTS_FOLDER + "/" + sFileName;
    return Thread.currentThread().getContextClassLoader().getResource(sPath);
  }
  
  /**
   * Restituisce il testo del file disclaimer.txt se presente nelle
   * risorse dell'applicazione.
   *
   * @return Testo del disclaimer
   */
  public static
  String getDisclaimer()
  {
    StringBuffer sb = new StringBuffer();
    InputStream oIn = null;
    try {
      URL urlDisclaimer = getURLResource(sEnvPrefix + IConstants.sRES_DISCLAIMER);
      if(urlDisclaimer == null) {
        return "";
      }
      oIn = urlDisclaimer.openStream();
      for(int c = oIn.read(); c >= 0; c = oIn.read()) {
        sb.append((char) c);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    finally {
      if(oIn != null) try { oIn.close(); } catch(Exception ex) {};
    }
    
    return sb.toString();
  }
  
  /**
   * Salva le informazioni memorizzate nell'oggetto dat sul client.
   * Tale file e' specificato dal parametro swingup.dat del file di configurazione.
   */
  public static
  void saveDat()
  {
    String sFileName = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_DAT);
    if(sFileName == null || sFileName.length() == 0) return;
    
    String sUserHome = System.getProperty("user.home");
    String sFilePath = sUserHome + File.separator + sFileName;
    
    System.out.println(sLOG_PREFIX + " save data file " + sFilePath + "...");
    
    File file = null;
    FileOutputStream fos = null;
    try{
      file = new File(sFilePath);
      fos = new FileOutputStream(file);
      dat.store(fos, ResourcesMgr.sAPP_DAT);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {}
    }
  }
  
  /**
   * Restituisce l'icona dell'applicazione.
   *
   * @param boSmall Se true restituisce l'icona logosmall.gif, altrimenti logo.gif
   * @return ImageIcon
   */
  public static
  ImageIcon getAppIcon(boolean boSmall)
  {
    String sFileName = sEnvPrefix + IConstants.sRES_LOGO;
    if(boSmall) sFileName = sEnvPrefix + IConstants.sRES_LOGOSMALL;
    try {
      URL urlAppIcon = getURLResource(sFileName);
      if(urlAppIcon == null) return null;
      return new ImageIcon(urlAppIcon);
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
  /**
   * Restituisce un valore intero presente in configurazione.
   *
   * @param sEntry Chiave di configurazione
   * @return valore
   */
  public static
  int getIntProperty(String sEntry)
  {
    String sValue = config.getProperty(sEntry, "0");
    try {
      return Integer.parseInt(sValue.trim());
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }
  
  /**
   * Restituisce un valore intero presente in configurazione.
   *
   * @param sEntry Chiave di configurazione
   * @param iDefault Valore predefinito
   * @return valore
   */
  public static
  int getIntProperty(String sEntry, int iDefault)
  {
    String sValue = config.getProperty(sEntry, String.valueOf(iDefault));
    try {
      return Integer.parseInt(sValue.trim());
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return 0;
    }
  }
  
  /**
   * Restituisce un valore boolean presente in configurazione.
   *
   * @param sEntry Chiave di configurazione
   * @return valore
   */
  public static
  boolean getBooleanProperty(String sEntry)
  {
    String sValue = (String) config.get(sEntry);
    if(sValue == null || sValue.length() == 0) {
      return false;
    }
    else
    if(sValue.equals("0") || sValue.equalsIgnoreCase("false") || sValue.equalsIgnoreCase("N") || sValue.equalsIgnoreCase("F")) {
      return false;
    }
    return true;
  }
  
  /**
   * Restituisce un valore boolean presente in configurazione.
   *
   * @param sEntry Chiave di configurazione
   * @param boDefault Valore predefinito
   * @return valore
   */
  public static
  boolean getBooleanProperty(String sEntry, boolean boDefault)
  {
    String sValue = (String) config.get(sEntry);
    if(sValue == null || sValue.length() == 0) {
      return boDefault;
    }
    else
    if(sValue.equals("0") || sValue.equalsIgnoreCase("false") || sValue.equalsIgnoreCase("N") || sValue.equalsIgnoreCase("F")) {
      return false;
    }
    return true;
  }
  
  /**
   * Restituisce un valore Stringa presente in configurazione.
   *
   * @param sEntry Chiave di configurazione
   * @return valore
   */
  public static
  String getStringProperty(String sEntry)
  {
    return config.getProperty(sEntry);
  }
  
  /**
   * Restituisce un valore Stringa presente in configurazione.
   *
   * @param sEntry Chiave di configurazione
   * @param sDefault Valore predefinito
   * @return valore
   */
  public static
  String getStringProperty(String sEntry, String sDefault)
  {
    return config.getProperty(sEntry, sDefault);
  }
  
  /**
   * Restituisce una lista di valori presente in configurazione.
   * Essa viene ottenuta facendo il parsing di un testo contenente
   * gli elementi separati da virgola.
   *
   * @param sEntry Chiave di configurazione
   * @return List
   */
  public static
  List<String> getListProperty(String sEntry)
  {
    String sListValues = (String) config.get(sEntry);
    if(sListValues == null) return null;
    List<String> listResult = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(sListValues, ",");
    while(st.hasMoreTokens()) {
      String sToken = st.nextToken().trim();
      listResult.add(sToken);
    }
    return listResult;
  }
  
  /**
   * Imposta il flag di visibilita' della finestra di attesa.
   *
   * @param boVisible boolean
   */
  public static
  void setVisibleWaitPleaseWindow(boolean boVisible)
  {
    if(oWinWaitPlease == null) buildWaitPleaseWindow(null);
    if(boAtLeastACustomWaitMessage && lblWaitPlease != null) lblWaitPlease.setText("     " + IConstants.sTEXT_WAIT + "     ");
    oWinWaitPlease.setVisible(boVisible);
    if(boVisible) {
      oWinWaitPlease.update(oWinWaitPlease.getGraphics());
    }
  }
  
  /**
   * Imposta il flag di visibilita' della finestra di attesa.
   *
   * @param boVisible boolean
   * @param sMessage messaggio
   */
  public static
  void setVisibleWaitPleaseWindow(boolean boVisible, String sMessage)
  {
    if(oWinWaitPlease == null) buildWaitPleaseWindow(null);
    if(boVisible && sMessage != null && sMessage.length() > 0 && lblWaitPlease != null) {
      lblWaitPlease.setText(sMessage);
      boAtLeastACustomWaitMessage = true;
    }
    else
    if(boAtLeastACustomWaitMessage && lblWaitPlease != null) {
      lblWaitPlease.setText("     " + IConstants.sTEXT_WAIT + "     ");
    }
    oWinWaitPlease.setVisible(boVisible);
    if(boVisible) {
      oWinWaitPlease.update(oWinWaitPlease.getGraphics());
    }
  }
  
  /**
   * Costruisce la finestra da mostrare nelle elaborazioni lunghe.
   *
   * @param oContainer Eventuale container che va a sostituire il container
   *                   di default. Se null viene costruito quello di default.
   */
  public static
  void buildWaitPleaseWindow(Container oContainer)
  {
    Container oGUIWaitPlease = null;
    
    if(oContainer == null) {
      lblWaitPlease = new JLabel("     " + IConstants.sTEXT_WAIT + "     ", ResourcesMgr.getImageIcon(IConstants.sICON_WAIT), SwingConstants.CENTER);
      lblWaitPlease.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
      JPanel oMainPanel = new JPanel(new BorderLayout());
      oMainPanel.add(lblWaitPlease, BorderLayout.CENTER);
      oMainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      oGUIWaitPlease = oMainPanel;
    }
    else {
      oGUIWaitPlease = oContainer;
    }
    
    if(ResourcesMgr.mainFrame != null) {
      oWinWaitPlease = new JWindow(ResourcesMgr.mainFrame);
    }
    else {
      oWinWaitPlease = new JWindow();
    }
    oWinWaitPlease.getContentPane().add(oGUIWaitPlease);
    oWinWaitPlease.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oWinWaitPlease.setLocation(screenSize.width/2 - oWinWaitPlease.getSize().width/2,
      screenSize.height/2 - oWinWaitPlease.getSize().height/2);
  }
  
  /**
   * Carica informazioni salvate sul client.
   */
  public static
  void loadDat()
  {
    String sFileName = config.getProperty(sAPP_DAT);
    if(sFileName == null) return;
    String sUserHome = System.getProperty("user.home");
    String sFilePath = sUserHome + File.separator + sFileName;
    
    System.out.println(sLOG_PREFIX + " load data file " + sFilePath + "...");
    InputStream oIn = null;
    try {
      File file = new File(sFilePath);
      if(file.exists()) {
        oIn = new FileInputStream(file);
        dat.load(oIn);
        Iterator<Object> oItKeys = dat.keySet().iterator();
        while(oItKeys.hasNext()) {
          String sKey = (String) oItKeys.next();
          if(sKey.startsWith(sPREFIX)) {
            String sValue = dat.getProperty(sKey);
            System.out.println(sLOG_PREFIX + " config.setProperty(\"" + sKey + "\",\"" + sValue + "\")");
            config.setProperty(sKey, sValue);
          }
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    finally {
      if(oIn != null) try { oIn.close(); } catch(Exception oEx) {}
    }
  }
  
  public static
  void loadConfig()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load configuration file " + sEnvPrefix + IConstants.sRES_CFG + "...");
    
    InputStream oIn = null;
    try {
      URL urlCfg = getURLResource(sEnvPrefix + IConstants.sRES_CFG);
      if(urlCfg == null) {
        throw new Exception(sEnvPrefix + IConstants.sRES_CFG + " not found.");
      }
      oIn = urlCfg.openStream();
      config.load(oIn);
      
      sURLCDSFromConfig = config.getProperty(sAPP_CDS);
      
      // Carica in config le proprieta' di sistema che iniziano per swingup
      System.out.println(sLOG_PREFIX + " check System properties...");
      Iterator<Object> oItKeys = System.getProperties().keySet().iterator();
      while(oItKeys.hasNext()) {
        String sKey = oItKeys.next().toString();
        if(sKey.startsWith(sPREFIX)) {
          String sSysValue = System.getProperty(sKey);
          System.out.println(sLOG_PREFIX + " config.setProperty(\"" + sKey + "\",\"" + sSysValue + "\")");
          config.setProperty(sKey, sSysValue);
        }
        if(sKey.startsWith("jnlp." + sPREFIX)) {
          String sSysValue = System.getProperty(sKey);
          String sJSKey    = sKey.substring(5);
          System.out.println(sLOG_PREFIX + " config.setProperty(\"" + sJSKey + "\",\"" + sSysValue + "\")");
          config.setProperty(sJSKey, sSysValue);
        }
      }
      
      // Imposta il build
      config.setProperty(sAPP_BUILD, sBUILD);
      
      bDebug = getBooleanProperty(sAPP_DEBUG, false);
      bDemo  = getBooleanProperty(sAPP_DEMO, false);
    }
    finally {
      try { oIn.close(); } catch(Exception oEx) {}
    }
  }
  
  public static
  void loadWorkPanel()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load WorkPanel...");
    
    String sClassName = config.getProperty(sAPP_WORKPANEL);
    if(sClassName == null) {
      oWorkPanel = new EmptyWorkPanel();
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof AWorkPanel)) {
      throw new Exception(sClassName + " is not AWorkPanel");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oWorkPanel = (AWorkPanel) object;
  }
  
  public static
  void loadMenuManager()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load Menu Manager...");
    
    String sClassName = config.getProperty(sAPP_MENUMANAGER);
    if(sClassName == null) {
      oMenuManager = new EmptyMenuManager();
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof IMenuManager)) {
      throw new Exception(sClassName + " is not IMenuManager");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oMenuManager = (IMenuManager) object;
  }
  
  /**
   * Visualizza il file tramite applicazione registrata nel sistema operativo.
   * Attualmente funziona solo su sistemi operativi Windows XP.
   *
   * @param sFileName String
   * @throws Exception
   */
  public static
  void viewFile(String sFileName)
    throws Exception
  {
    String sCommand = null;
    String sOSName = System.getProperty("os.name", "WIN");
    if(sOSName.toUpperCase().indexOf("WIN") >= 0) {
      sCommand = DataFileUtil.getWINViewer(sFileName);
    }
    else
    if(sOSName.toUpperCase().indexOf("LINUX") >= 0) {
      sCommand = DataFileUtil.getLinuxViewer(sFileName);
    }
    else {
      sCommand = DataFileUtil.getMACViewer(sFileName);
    }
    if(sCommand == null || sCommand.length() == 0) {
      throw new Exception("viewFile in " + sOSName + " not supported.");
    }
    System.out.println(ResourcesMgr.sLOG_PREFIX + " exec(" + sCommand + ")");
    Runtime.getRuntime().exec(sCommand);
  }
  
  /**
   * Verifica il supporto alla visualizzazione automatica del file.
   *
   * @param sFileName String
   * @throws Exception
   */
  public static
  boolean isViewFileSupported(String sFileName)
  {
    String sCommand = null;
    String sOSName = System.getProperty("os.name", "WIN");
    if(sOSName.toUpperCase().indexOf("WIN") >= 0) {
      sCommand = DataFileUtil.getWINViewer(sFileName);
    }
    else
    if(sOSName.toUpperCase().indexOf("LINUX") >= 0) {
      sCommand = DataFileUtil.getLinuxViewer(sFileName);
    }
    else {
      sCommand = DataFileUtil.getMACViewer(sFileName);
    }
    if(sCommand == null || sCommand.length() == 0) {
      return false;
    }
    return true;
  }
  
  /**
   * Apre il browser con l'URL specificato.
   * Attualmente funziona solo su sistemi operativi Windows XP.
   *
   * @param sURL URL
   * @throws Exception
   */
  public static
  void openBrowser(String sURL)
    throws Exception
  {
    if(sURL == null || sURL.length() == 0) return;
    StringBuffer sbURL = new StringBuffer();
    sURL = sURL.trim();
    for(int i = 0; i < sURL.length(); i++) {
      char c = sURL.charAt(i);
      if(c == '&') sbURL.append("\"&\""); else sbURL.append(c);
    }
    String sOSName = System.getProperty("os.name", "WIN");
    if(sOSName.toUpperCase().indexOf("WIN") >= 0) {
      String sCommand = "cmd /C start " + sbURL;
      System.out.println(sLOG_PREFIX + " exec(" + sCommand + ")");
      Runtime.getRuntime().exec(sCommand);
    }
    else
    if(sOSName.toUpperCase().indexOf("LINUX") >= 0) {
      String sBrowser = dat.getProperty("linux.browser");
      if(sBrowser == null || sBrowser.length() == 0) {
        sBrowser = config.getProperty("linux.browser", "firefox");
      }
      String sCommand = sBrowser + " " + sbURL;
      System.out.println(sLOG_PREFIX + " exec(" + sCommand + ")");
      Runtime.getRuntime().exec(sCommand);
    }
    else {
      String sBrowser = dat.getProperty(sOSName.replace(' ', '_') + ".browser");
      if(sBrowser == null || sBrowser.length() == 0) {
        sBrowser = config.getProperty(sOSName.replace(' ', '_') + ".browser");
      }
      if(sBrowser == null || sBrowser.length() == 0) {
        sBrowser = "open"; // Comando MAC
      }
      String sCommand = sBrowser + " " + sbURL;
      System.out.println(sLOG_PREFIX + " exec(" + sCommand + ")");
      Runtime.getRuntime().exec(sCommand);
    }
  }
  
  /**
   * Apre il client di posta con l'indirizzo specificato.
   *
   * @param sEMail Indirizzo di posta elettronica.
   * @throws Exception
   */
  public static
  void openMailClent(String sEMail)
    throws Exception
  {
    String sOSName = System.getProperty("os.name", "WIN");
    if(sOSName.toUpperCase().indexOf("WIN") >= 0) {
      String sCommand = null;
      if(sEMail != null) {
        sCommand = "cmd /C start mailto:" + sEMail.trim();
      }
      else {
        sCommand = "cmd /C start mailto:";
      }
      System.out.println(sLOG_PREFIX + " exec(" + sCommand + ")");
      Runtime.getRuntime().exec(sCommand);
    }
    else
    if(sOSName.toUpperCase().indexOf("LINUX") >= 0) {
      String sMailClient = dat.getProperty("linux.mail");
      if(sMailClient == null || sMailClient.length() == 0) {
        sMailClient = config.getProperty("linux.mail", "thunderbird -compose");
      }
      String sCommand = null;
      if(sEMail != null && sEMail.length() > 0) {
        if(sMailClient.equals("thunderbird -compose")) {
          sCommand = sMailClient + " to=" + sEMail.trim();
        }
        else {
          sCommand = sMailClient + " " + sEMail.trim();
        }
      }
      else {
        sCommand = sMailClient;
      }
      System.out.println(sLOG_PREFIX + " exec(" + sCommand + ")");
      Runtime.getRuntime().exec(sCommand);
    }
    else {
      String sMailClient = dat.getProperty(sOSName.replace(' ', '_') + ".mail");
      if(sMailClient == null || sMailClient.length() == 0) {
        sMailClient = config.getProperty(sOSName.replace(' ', '_') + ".mail");
      }
      String sCommand = null;
      if(sMailClient == null || sMailClient.length() == 0) {
        sCommand = "open mailto:" + sEMail.trim(); // valido su MAC
      }
      else {
        if(sEMail != null) {
          sCommand = sMailClient + " \"" + sEMail.trim() + "\"";
        }
        else {
          sCommand = sMailClient;
        }
      }
      System.out.println(sLOG_PREFIX + " exec(" + sCommand + ")");
      Runtime.getRuntime().exec(sCommand);
    }
  }
  
  public static
  JButton getToolBarButtonByActionCommand(String sActionCommand)
  {
    JToolBar oToolBar = oMenuManager.getJToolBar();
    if(oToolBar == null) return null;
    int iCount = oToolBar.getComponentCount();
    for(int i = 0; i < iCount; i++) {
      Component oComponent = oToolBar.getComponentAtIndex(i);
      if(oComponent instanceof JButton) {
        String sAC = ((JButton) oComponent).getActionCommand();
        if(sAC != null && sAC.equals(sActionCommand)) {
          return (JButton) oComponent;
        }
      }
    }
    return null;
  }
  
  private static
  void loadApplicationListener()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load Application Listener...");
    
    String sClassName = config.getProperty(sAPP_LISTENER);
    
    if(sClassName == null) {
      oApplicationListener = new ApplicationAdapter();
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof IApplicationListener)) {
      throw new Exception(sClassName + " is not IApplicationListener");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oApplicationListener = (IApplicationListener) object;
  }
  
  private static
  void loadGUIManager()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load GUI Manager...");
    
    String sClassName = config.getProperty(sAPP_GUIMGR);
    
    if(sClassName == null) {
      oGUIManager = new DefaultGUIManager();
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof IGUIManager)) {
      throw new Exception(sClassName + " is not IGUIManager");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oGUIManager = (IGUIManager) object;
  }
  
  private static
  void loadSessionManager()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load Session Manager...");
    
    String sClassName = config.getProperty(sAPP_SESSIONMGR);
    
    if(sClassName == null) {
      oSessionManager = new DefaultSessionManager();
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof ISessionManager)) {
      throw new Exception(sClassName + " is not ISessionManager");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oSessionManager = (ISessionManager) object;
  }
  
  private static
  void loadErrorSender()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load Error Sender...");
    
    String sClassName = config.getProperty(sAPP_ERRORSENDER);
    
    if(sClassName == null) {
      oErrorSender = new EmptyErrorSender();
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof IErrorSender)) {
      throw new Exception(sClassName + " is not IErrorSender");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oErrorSender = (IErrorSender) object;
  }
  
  private static
  void loadLogger()
    throws Exception
  {
    System.out.println(sLOG_PREFIX + " load Logger manager...");
    
    String sClassName = config.getProperty(sAPP_LOGGER);
    String sLog = config.getProperty(sAPP_LOG);
    
    if(sClassName == null) {
      if(sLog != null) {
        oLogger = new FileLogger(sLog, bDebug);
      }
      else {
        oLogger = new ConsoleLogger(bDebug);
      }
      return;
    }
    
    Class<?> oClass = Class.forName(sClassName);
    
    Object object = oClass.newInstance();
    
    if(!(object instanceof ILogger)) {
      throw new Exception(sClassName + " is not ILogger");
    }
    
    System.out.println(sLOG_PREFIX + " " + object.getClass().getName() + " loaded");
    
    oLogger = (ILogger) object;
    
    if(sLog == null) sLog = "swingup.log";
    
    oLogger.init(sLog, bDebug);
  }
}
