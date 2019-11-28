package org.dew.swingup.dialog;

import java.awt.*;
import java.awt.event.*;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.util.*;

/**
 * Dialogo per la gestione della login.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class LoginDialog extends AJDialog
{
  protected final static String sOTHER_CLIENTS = "altre...";
  
  protected JTextField txtUserName;
  protected JPasswordField txtPassword;
  protected JComboBox cmbClient;
  protected JLabel lblMessage;
  protected JButton cmdFind;
  protected JPanel oPanelIdClient;
  
  protected String sIdService;
  protected JButton btnSmartCard;
  
  public
  LoginDialog(Frame frame)
  {
    super(frame, "Login - " + ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_NAME), true);
    this.setSize(350, 220);
    DefaultGUIManager.resizeForHRScreen(this);
    this.setResizable(false);
  }
  
  public static
  boolean showMe(JFrame jframe)
  {
    LoginDialog loginDialog = new LoginDialog(jframe);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    loginDialog.setLocation(screenSize.width/2 - loginDialog.getSize().width/2,
      screenSize.height/2 - loginDialog.getSize().height/2);
    loginDialog.setVisible(true);
    return !loginDialog.isCancel();
  }
  
  public
  Container buildGUI()
    throws Exception
  {
    JPanel oMainPanel = new JPanel(new GridLayout(5, 1, 5, 5));
    
    txtUserName = new JTextField();
    addField(oMainPanel, txtUserName, "Utente:");
    txtPassword = new JPasswordField();
    addField(oMainPanel, txtPassword, "Password:");
    cmbClient = new JComboBox();
    oPanelIdClient = addField(oMainPanel, cmbClient, "Postazione:");
    lblMessage = new JLabel();
    JPanel jPanelMessage = new JPanel();
    jPanelMessage.add(lblMessage);
    oMainPanel.add(jPanelMessage);
    
    boolean boHideIdClient = ResourcesMgr.getBooleanProperty(ResourcesMgr.sGUILOGIN_HIDEIDCLIENT, true);
    oPanelIdClient.setVisible(!boHideIdClient);
    cmbClient.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        String sSelectedIdClient = (String) cmbClient.getSelectedItem();
        if(sSelectedIdClient != null && sSelectedIdClient.equals(sOTHER_CLIENTS)) {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              cmbClient.showPopup();
            }
          });
          loadClients(getDefIdClient());
        }
      }
    });
    
    sIdService = ResourcesMgr.config.getProperty(ResourcesMgr.sGUILOGIN_IDSERVICE);
    if(sIdService == null) {
      System.err.println(ResourcesMgr.sLOG_PREFIX + " " + ResourcesMgr.sGUILOGIN_IDSERVICE + " undefined.");
    }
    
    showMessage("Digitare le credenziali");
    
    String sDefUserName = ResourcesMgr.config.getProperty(ResourcesMgr.sGUILOGIN_DEFUSERNAME);
    String sDefPassword = ResourcesMgr.config.getProperty(ResourcesMgr.sGUILOGIN_DEFPASSWORD);
    String sDefIdClient = getDefIdClient();
    
    if(sDefIdClient != null) {
      cmbClient.removeAllItems();
      cmbClient.addItem(sDefIdClient);
      cmbClient.addItem(sOTHER_CLIENTS);
    }
    else {
      loadClients(null);
    }
    
    setDefaults(sDefUserName, sDefPassword, sDefIdClient);
    
    JPanel oResult = new JPanel();
    oResult.add(oMainPanel);
    return oResult;
  }
  
  protected
  JComponent buildAdditionalButton()
  {
    boolean boSmartCard = ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_SMARTCARD, false);
    if(!boSmartCard) return null;
    btnSmartCard = GUIUtil.buildActionButton(IConstants.sGUIDATA_SMARTCARD, "smartcard");
    btnSmartCard.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireSmartCard();
      }
    });
    return btnSmartCard;
  }
  
  protected
  void setEnabledButtons(boolean boEnabled)
  {
    super.setEnabledButtons(boEnabled);
    if(btnSmartCard != null) btnSmartCard.setEnabled(boEnabled);
  }
  
  protected
  void fireSmartCard()
  {
    boCancel = false;
    setEnabledButtons(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try{
          if(doLoginBySmartCard()) {
            dispose();
          }
        }
        finally {
          setEnabledButtons(true);
          setCursor(Cursor.getDefaultCursor());
        }
      }
    });
  }
  
  protected
  boolean doLoginBySmartCard()
  {
    String sIdClient = (String) cmbClient.getSelectedItem();
    if(oPanelIdClient.isVisible()) {
      if(sIdClient == null || sIdClient.length() == 0) {
        showErrorMessage("Selezionare il client");
        return false;
      }
    }
    
    SmartCardManager smartCardManager;
    try {
      smartCardManager = new SmartCardManager();
    }
    catch(Exception ex) {
      ex.printStackTrace();
      showErrorMessage("Lettore Smart Card non accessibile");
      return false;
    }
    
    String sUserName = txtUserName.getText();
    if(sUserName != null && sUserName.equalsIgnoreCase("check")) {
      try {
        X509Certificate x509Certificate = smartCardManager.getX509CertificateObject(true, 0);
        if(x509Certificate == null) {
          GUIMessage.showWarning(this, "Verifica firma digitale con Smart Card fallita.");
        }
        else {
          boolean boExpired = false;
          Date dNotBefore = x509Certificate.getNotBefore();
          Date dNotAfter  = x509Certificate.getNotAfter();
          if(dNotAfter != null) boExpired = dNotAfter.before(new Date());
          String sExpired = boExpired ? " (Scaduto)" : "";
          DateFormat dateFormat = ResourcesMgr.getDefaultDateFormat();
          String sNotBefore = dNotBefore != null ? dateFormat.format(dNotBefore) : "";
          String sNotAfter  = dNotAfter  != null ? dateFormat.format(dNotAfter)  : "";
          String sMessage = "Firma con Smart Card verificata con successo. Certificato:\n\n";
          Principal subjectDN = x509Certificate.getSubjectDN();
          if(subjectDN != null) {
            sMessage += subjectDN.getName() + "\n\n";
          }
          Object serialNumber = x509Certificate.getSerialNumber();
          sMessage += "SerialNumber: " + serialNumber + "\n\n";
          Principal issuerDN = x509Certificate.getIssuerDN();
          if(issuerDN != null) {
            sMessage += "Emesso da: " + issuerDN.getName() + "\n\n";
          }
          sMessage += "Validit\340: " + sNotBefore + " - " + sNotAfter + sExpired + "\n";
          if(boExpired) {
            GUIMessage.showWarning(this, sMessage);
          }
          else {
            GUIMessage.showInformation(this, sMessage);
          }
        }
      }
      catch(Exception ex) {
        GUIMessage.showException(this, ex);
      }
      return false;
    }
    
    byte[] abSignature = null;
    try {
      int iSlot = ResourcesMgr.getIntProperty(ResourcesMgr.sGUILOGIN_SLOT);
      
      if(sIdService == null || sIdService.length() == 0) sIdService = "?";
      abSignature = smartCardManager.signWithPKCS11(sIdService.getBytes(), iSlot);
      if(abSignature == null || abSignature.length < 2) return false;
    }
    catch(Exception ex) {
      ex.printStackTrace();
      showErrorMessage(ex.getMessage());
      return false;
    }
    
    ResourcesMgr.config.setProperty(ResourcesMgr.sAPP_RPC_SMARTCARD, "1");
    
    ISessionManager oSessionMgr = ResourcesMgr.getSessionManager();
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      oSessionMgr.login(sIdService, abSignature, sIdClient);
    }
    catch(Exception ex) {
      ex.printStackTrace();
      String sExMessage = ex.getMessage();
      if(sExMessage != null && sExMessage.length() > 1 && sExMessage.startsWith("!")) {
        showErrorMessage(sExMessage.substring(1));
      }
      else {
        showErrorMessage("Servizio non disponibile");
      }
      return false;
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    if(!oSessionMgr.isActive()) {
      showErrorMessage("Utente non riconosciuto");
      return false;
    }
    
    ResourcesMgr.config.setProperty(ResourcesMgr.sGUILOGIN_IDCLIENT, sIdClient);
    ResourcesMgr.dat.setProperty(ResourcesMgr.sGUILOGIN_IDCLIENT, sIdClient);
    ResourcesMgr.saveDat();
    
    return true;
  }
  
  protected
  JPanel addField(Container container, JComponent component, String sLabel)
  {
    component.setPreferredSize(new Dimension(200, 0));
    JPanel oResult = GUIUtil.buildLabelledComponent(component, sLabel, DefaultGUIManager.resizeForHRScreen(100));
    container.add(oResult);
    return oResult;
  }
  
  protected
  String getDefIdClient()
  {
    return ResourcesMgr.config.getProperty(ResourcesMgr.sGUILOGIN_IDCLIENT);
  }
  
  protected
  void loadClients(String sSelectedIdClient)
  {
    ISessionManager oSessionMgr = ResourcesMgr.getSessionManager();
    
    List oResult = null;
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      oResult = oSessionMgr.getClients(sIdService);
    }
    catch(Exception ex) {
      showErrorMessage("Lettura postazioni non riuscita");
      return;
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    cmbClient.removeAllItems();
    if(oResult != null) {
      for(int i = 0; i < oResult.size(); i++) {
        cmbClient.addItem(oResult.get(i));
      }
    }
    
    if(sSelectedIdClient != null) {
      cmbClient.setSelectedItem(sSelectedIdClient);
    }
  }
  
  public
  void setDefaults(String sDefUserName, String sDefPassword, String sDefIdClient)
  {
    if(sDefUserName != null) {
      txtUserName.setText(sDefUserName);
      txtUserName.setSelectionStart(0);
      txtUserName.setSelectionEnd(txtUserName.getText().length());
      txtUserName.requestFocus();
    }
    else {
      txtUserName.setText("");
    }
    
    if(sDefPassword != null) {
      txtPassword.setText(sDefPassword);
    }
    else {
      txtPassword.setText("");
    }
    
    if(sDefIdClient != null) {
      cmbClient.setSelectedItem(sDefIdClient);
    }
  }
  
  public
  boolean doOk()
  {
    ResourcesMgr.config.setProperty(ResourcesMgr.sAPP_RPC_SMARTCARD, "0");
    
    String sUserName = txtUserName.getText();
    if(sUserName == null || sUserName.length() == 0) {
      showErrorMessage("Utente non valido");
      return false;
    }
    String sPassword = new String(txtPassword.getPassword());
    if(sPassword == null || sPassword.length() == 0) {
      showErrorMessage("Password non valida");
      return false;
    }
    String sIdClient = (String) cmbClient.getSelectedItem();
    if(oPanelIdClient.isVisible()) {
      if(sIdClient == null || sIdClient.length() == 0) {
        showErrorMessage("Selezionare il client");
        return false;
      }
    }
    
    ISessionManager oSessionMgr = ResourcesMgr.getSessionManager();
    
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      oSessionMgr.login(sIdService, sUserName, sPassword, sIdClient);
    }
    catch(Exception ex) {
      System.err.println("[LoginDialog] " + ex);
      String sExMessage = ex.getMessage();
      if(sExMessage != null && sExMessage.length() > 1 && sExMessage.startsWith("!")) {
        showErrorMessage(sExMessage.substring(1));
      }
      else {
        showErrorMessage("Servizio non disponibile");
      }
      return false;
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    if(!oSessionMgr.isActive()) {
      showErrorMessage("Utente non riconosciuto");
      return false;
    }
    
    ResourcesMgr.config.setProperty(ResourcesMgr.sGUILOGIN_IDCLIENT, sIdClient);
    ResourcesMgr.dat.setProperty(ResourcesMgr.sGUILOGIN_IDCLIENT, sIdClient);
    ResourcesMgr.saveDat();
    
    return true;
  }
  
  public
  boolean doCancel()
  {
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
  }
  
  protected
  void showErrorMessage(String sText)
  {
    lblMessage.setForeground(Color.red);
    lblMessage.setText(sText);
  }
  
  protected
  void showMessage(String sText)
  {
    lblMessage.setForeground(Color.blue);
    lblMessage.setText(sText);
  }
}
