package org.dew.swingup.dialog;

import java.awt.*;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.util.*;

/**
 * Dialogo per cambiare le credenziali.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class ChangePasswordDialog extends AJDialog
{
  public final static int iTYPE_GENERIC            = 0;
  public final static int iTYPE_LETTERS_AND_DIGITS = 1;
  public final static int iTYPE_LETTERS            = 2;
  public final static int iTYPE_DIGITS             = 3;
  
  protected JPasswordField txtOldPassword;
  protected JPasswordField txtNewPassword;
  protected JPasswordField txtRepeatPassword;
  protected JLabel lblMessage;
  protected int iMinLength    = 8;
  protected int iTypePassword = 1;
  protected boolean boMandatory = false;
  
  public
  ChangePasswordDialog(Frame frame, boolean boMandatory)
  {
    super(frame, "Cambio password", true, boMandatory);
    this.setSize(380, 220);
    DefaultGUIManager.resizeForHRScreen(this);
    this.setResizable(false);
    this.iMinLength    = ResourcesMgr.getIntProperty(IResourceMgr.sGUIPASSWORD_MINLENGTH, 8);
    this.iTypePassword = ResourcesMgr.getIntProperty(IResourceMgr.sGUIPASSWORD_TYPE,      1);
    this.boMandatory   = boMandatory;
  }
  
  public
  ChangePasswordDialog(Frame frame, int iTheMinLength, int iTheTypePassword, boolean boMandatory)
  {
    super(frame, "Cambio password", true);
    this.setSize(380, 220);
    DefaultGUIManager.resizeForHRScreen(this);
    this.setResizable(false);
    this.iMinLength = iTheMinLength;
    this.iTypePassword = iTheTypePassword;
    this.boMandatory = boMandatory;
  }
  
  public static
  boolean showMe(JFrame jframe, boolean boMandatory)
  {
    ChangePasswordDialog oDialog = new ChangePasswordDialog(jframe, boMandatory);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oDialog.setLocation(screenSize.width/2 - oDialog.getSize().width/2, screenSize.height/2 - oDialog.getSize().height/2);
    oDialog.setVisible(true);
    return !oDialog.isCancel();
  }
  
  public static
  boolean showMe(JFrame jframe, int iTheMinLength, int iTheTypePassword, boolean boMandatory)
  {
    ChangePasswordDialog oDialog = new ChangePasswordDialog(jframe, iTheMinLength, iTheTypePassword, boMandatory);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oDialog.setLocation(screenSize.width/2 - oDialog.getSize().width/2, screenSize.height/2 - oDialog.getSize().height/2);
    oDialog.setVisible(true);
    return !oDialog.isCancel();
  }
  
  public
  Container buildGUI()
    throws Exception
  {
    JPanel oMainPanel = new JPanel( new GridLayout(5, 1, 5, 5));
    
    txtOldPassword = new JPasswordField();
    addField(oMainPanel, txtOldPassword, "Password corrente:");
    txtNewPassword = new JPasswordField();
    addField(oMainPanel, txtNewPassword, "Nuova Password:");
    txtRepeatPassword = new JPasswordField();
    addField(oMainPanel, txtRepeatPassword, "Ripeti Password:");
    lblMessage = new JLabel();
    JPanel jPanelMessage = new JPanel();
    jPanelMessage.add(lblMessage);
    oMainPanel.add(jPanelMessage);
    
    showMessage("Modifica credenziali");
    
    JPanel oResult = new JPanel();
    oResult.add(oMainPanel);
    
    return oResult;
  }
  
  public
  boolean doOk()
  {
    ISessionManager oSessionManager = ResourcesMgr.getSessionManager();
    User oUser = oSessionManager.getUser();
    
    String sUserPassword = oUser.getPassword();
    
    String sOldPassword = new String(txtOldPassword.getPassword());
    if(sOldPassword == null || sOldPassword.length() == 0) {
      showErrorMessage("Password corrente non valida");
      return false;
    }
    if(sUserPassword == null || sUserPassword.length() == 0) {
      int iPasswordHashCode = oUser.getPasswordHashCode();
      if(iPasswordHashCode != 0) {
        int iOldPasswordHashCode = sOldPassword.hashCode();
        if(iOldPasswordHashCode != iPasswordHashCode) {
          showErrorMessage("Password corrente non esatta");
          return false;
        }
        else {
          oUser.setPassword(sOldPassword);
        }
      }
    }
    else
    if(!sOldPassword.equals(sUserPassword)) {
      showErrorMessage("Password corrente non valida");
      return false;
    }
    
    String sNewPassword = new String(txtNewPassword.getPassword());
    if(sNewPassword == null || sNewPassword.length() < iMinLength) {
      showErrorMessage("Password non valida: almeno " + iMinLength + " caratteri");
      return false;
    }
    
    if(sOldPassword.equals(sNewPassword)) {
      showErrorMessage("Nuova password uguale a quella corrente");
      return false;
    }
    
    switch(iTypePassword) {
      case iTYPE_LETTERS_AND_DIGITS:
      if(!checkCharacters(sNewPassword)) {
        showErrorMessage("La password deve essere composta da lettere e numeri.");
        return false;
      }
      break;
      case iTYPE_LETTERS:
      if(!checkLetters(sNewPassword)) {
        showErrorMessage("La password deve essere composta solo da lettere.");
        return false;
      }
      break;
      case iTYPE_DIGITS:
      if(!checkDigits(sNewPassword)) {
        showErrorMessage("La password deve essere composta solo da numeri.");
        return false;
      }
      break;
    }
    
    String sRepeatPassword = new String(txtRepeatPassword.getPassword());
    if(!sNewPassword.equals(sRepeatPassword)) {
      showErrorMessage("Le password non coincidono");
      return false;
    }
    
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      System.out.println(ResourcesMgr.sLOG_PREFIX + " SessionManager.changePassword...");
      oSessionManager.changePassword(sNewPassword);
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
      boolean boRepeat = false;
      if(sExMessage != null && sExMessage.length() > 1 && sExMessage.startsWith("!")) {
        String sMessage = "Non \350 stato possibile aggiornare la password.\n";
        sMessage += "Motivo: " + sExMessage.substring(1) + "\n";
        sMessage += "Si vuole riprovare subito (SI) o rimandare a un'altra volta (NO)?";
        boRepeat = GUIMessage.getConfirmation(this, sMessage);
      }
      else {
        String sMessage = "Non \350 stato possibile aggiornare la password.\n";
        sMessage += "Si vuole riprovare subito (SI) o rimandare a un'altra volta (NO)?";
        boRepeat = GUIMessage.getConfirmation(this, sMessage);
      }
      return !boRepeat;
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    return true;
  }
  
  protected
  JPanel addField(Container container, JComponent component, String sLabel)
  {
    component.setPreferredSize(new Dimension(DefaultGUIManager.resizeForHRScreen(200), 0));
    JPanel oResult = GUIUtil.buildLabelledComponent(component, sLabel, DefaultGUIManager.resizeForHRScreen(150));
    container.add(oResult);
    return oResult;
  }
  
  protected
  boolean checkCharacters(String sPassword)
  {
    boolean boIsLetter = false;
    boolean boIsDigit = false;
    for(int i = 0; i < sPassword.length(); i++) {
      char c = sPassword.charAt(i);
      if(Character.isLetter(c)) {
        boIsLetter = true;
      }
      if(Character.isDigit(c)) {
        boIsDigit = true;
      }
    }
    return boIsLetter && boIsDigit;
  }
  
  protected static
  boolean checkLetters(String sPassword)
  {
    boolean boIsLetter = true;
    for(int i = 0; i < sPassword.length(); i++) {
      char c = sPassword.charAt(i);
      if(!Character.isLetter(c)) {
        boIsLetter = false;
      }
    }
    return boIsLetter;
  }
  
  protected static
  boolean checkDigits(String sPassword)
  {
    boolean boIsDigit = true;
    for(int i = 0; i < sPassword.length(); i++) {
      char c = sPassword.charAt(i);
      if(!Character.isDigit(c)) {
        boIsDigit = false;
      }
    }
    return boIsDigit;
  }
  
  public
  boolean doCancel()
  {
    if(boMandatory) {
      GUIMessage.showWarning("E' obbligatorio eseguire il cambio della password.");
      return false;
    }
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
