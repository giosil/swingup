package org.dew.swingup.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.util.GUIUtil;

/**
 * Dialogo per l'immissione del campo password.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class PasswordDialog extends AJDialog
{
  protected JLabel oPrompt;
  protected JPasswordField oPasswordField;
  protected String sResult = null;
  protected boolean boAddDisconnect = false;
  protected JButton btnDisconnect;
  protected JDialog thisDialog;
  
  public
  PasswordDialog()
  {
    this(null);
  }
  
  public
  PasswordDialog(boolean boAddDisconnect)
  {
    this(null, boAddDisconnect);
  }
  
  public
  PasswordDialog(String sPrompt)
  {
    this(sPrompt, false);
  }
  
  public
  PasswordDialog(String sPrompt, boolean boAddDisconnect)
  {
    super();
    this.thisDialog = this;
    setTitle("Password");
    setModal(true);
    this.boAddDisconnect = boAddDisconnect;
    try {
      init(false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di PasswordDialog", ex);
    }
    if(boAddDisconnect) {
      this.setSize(360, 150);
    }
    else {
      this.setSize(320, 150);
    }
    DefaultGUIManager.resizeForHRScreen(this);
    this.setResizable(false);
    if(sPrompt != null) oPrompt.setText(sPrompt);
  }
  
  protected
  JComponent buildAdditionalButton()
  {
    if(boAddDisconnect) {
      btnDisconnect = GUIUtil.buildActionButton(IConstants.sGUIDATA_DISCONN, IConstants.sAC_TOOLBAR_DISCONNECT);
      btnDisconnect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if(ResourcesMgr.mainFrame.disconnect()) {
            boCancel = false;
            sResult = "\n";
            dispose();
          }
        }
      });
      return btnDisconnect;
    }
    return null;
  }
  
  public static
  String getInput(String sPrompt)
  {
    PasswordDialog oDialog = null;
    if(sPrompt != null) {
      oDialog = new PasswordDialog(sPrompt);
    }
    else {
      oDialog = new PasswordDialog();
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oDialog.setLocation(screenSize.width/2 - oDialog.getSize().width/2,
      screenSize.height/2 - oDialog.getSize().height/2);
    oDialog.setVisible(true);
    if(oDialog.isCancel()) return null;
    return oDialog.getResult();
  }
  
  public static
  String getInput(String sPrompt, boolean boAddDisconnect)
  {
    PasswordDialog oDialog = null;
    if(sPrompt != null) {
      oDialog = new PasswordDialog(sPrompt, boAddDisconnect);
    }
    else {
      oDialog = new PasswordDialog(boAddDisconnect);
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oDialog.setLocation(screenSize.width/2 - oDialog.getSize().width/2,
      screenSize.height/2 - oDialog.getSize().height/2);
    oDialog.setVisible(true);
    if(oDialog.isCancel()) return null;
    return oDialog.getResult();
  }
  
  public
  Container buildGUI()
    throws Exception
  {
    JPanel oResult = new JPanel(new BorderLayout());
    JPanel oMainPanel = new JPanel(new GridLayout(2, 1, 4, 4));
    oMainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    oPrompt = new JLabel("Digitare la password");
    oPasswordField = new JPasswordField();
    oMainPanel.add(oPrompt);
    oMainPanel.add(oPasswordField);
    oResult.add(oMainPanel, BorderLayout.NORTH);
    return oResult;
  }
  
  /**
   * Ritorna la password digitata. Nel caso di disconnessione ritorna un '\n'.
   * @return String
   */
  public
  String getResult()
  {
    return sResult;
  }
  
  public
  boolean doOk()
  {
    sResult = ((JTextComponent) oPasswordField).getText();
    return true;
  }
  
  public
  boolean doCancel()
  {
    sResult = null;
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
}
