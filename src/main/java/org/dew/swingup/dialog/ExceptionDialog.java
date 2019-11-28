package org.dew.swingup.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.util.*;

/**
 * Dialogo per mostrare un messaggio di errore.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class ExceptionDialog extends JDialog
implements ActionListener
{
  protected final static String sACTIONCOMMAND_CLOSE   = "close";
  protected final static String sACTIONCOMMAND_DETAILS = "details";
  protected final static String sACTIONCOMMAND_SEND    = "send";
  
  protected final static int iPrefWidth  = 500;
  protected final static int iPrefHeight = 100;
  
  protected JPanel jMainPanel = new JPanel();
  protected JPanel jpanelDetails = null;
  
  protected JButton btnClose;
  protected JButton btnDetails;
  protected JButton btnSend;
  
  protected String sMessage      = null;
  protected Throwable oThrowable = null;
  
  public
  ExceptionDialog(Frame frame, String sMessage, Throwable oThrowable)
  {
    super(frame, IConstants.sTEXT_MESSAGE, true);
    
    this.sMessage = sMessage;
    this.oThrowable = oThrowable;
    
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public
  ExceptionDialog(Dialog dialog, String sMessage, Throwable oThrowable)
  {
    super(dialog, IConstants.sTEXT_MESSAGE, true);
    
    this.sMessage = sMessage;
    this.oThrowable = oThrowable;
    
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    
    if(sActionCommand.equals(sACTIONCOMMAND_CLOSE)) {
      doClose();
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_DETAILS)) {
      jpanelDetails.setVisible(!jpanelDetails.isVisible());
      pack();
    }
    else
    if(sActionCommand.equals(sACTIONCOMMAND_SEND)) {
      IErrorSender oErrorSender = ResourcesMgr.getErrorSender();
      if(oErrorSender != null) {
        try{
          setCursor(new Cursor(Cursor.WAIT_CURSOR));
          System.out.println(ResourcesMgr.sLOG_PREFIX + " ErrorSender.send...");
          oErrorSender.send(sMessage, oThrowable);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
        finally {
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
      }
      if(btnSend != null) {
        btnSend.setEnabled(false);
      }
    }
  }
  
  private
  void init()
    throws Exception
  {
    this.setTitle("Errore");
    jMainPanel.setLayout(new BorderLayout());
    
    jMainPanel.add(createMessagePanel(), BorderLayout.NORTH);
    jpanelDetails = createDetailsPanel();
    jpanelDetails.setVisible(false);
    jMainPanel.add(jpanelDetails, BorderLayout.CENTER);
    
    getContentPane().add(jMainPanel);
    
    pack();
  }
  
  protected
  void doClose()
  {
    dispose();
  }
  
  private
  JPanel createMessagePanel()
  {
    JPanel jpanel = new JPanel(new BorderLayout());
    
    jpanel.setPreferredSize(new Dimension(iPrefWidth, iPrefHeight));
    DefaultGUIManager.setPreferredSizeForHRScreen(jpanel);
    
    // Message
    JPanel jPanelMessage = new JPanel();
    JLabel lblMessage = new JLabel();
    lblMessage.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
    lblMessage.setIconTextGap(10);
    if(sMessage == null) {
      lblMessage.setText(IConstants.sTEXT_ERR_GENERIC);
    }
    else {
      lblMessage.setText(sMessage);
    }
    jPanelMessage.add(lblMessage);
    
    // Buttons
    JPanel jPanelButtons = new JPanel();
    btnClose = GUIUtil.buildActionButton(IConstants.sGUIDATA_CLOSE, sACTIONCOMMAND_CLOSE);
    btnClose.addActionListener(this);
    
    btnDetails = GUIUtil.buildActionButton(IConstants.sGUIDATA_DEATILS, sACTIONCOMMAND_DETAILS);
    btnDetails.addActionListener(this);
    if(oThrowable == null) {
      btnDetails.setEnabled(false);
    }
    
    jPanelButtons.add(btnClose);
    jPanelButtons.add(btnDetails);
    
    IErrorSender oErrorSender = ResourcesMgr.getErrorSender();
    if(oErrorSender != null) {
      btnSend = GUIUtil.buildActionButton(IConstants.sGUIDATA_SEND, sACTIONCOMMAND_SEND);
      btnSend.addActionListener(this);
      jPanelButtons.add(btnSend);
    }
    
    jpanel.add(new JPanel(), BorderLayout.NORTH);
    jpanel.add(jPanelMessage, BorderLayout.CENTER);
    jpanel.add(jPanelButtons, BorderLayout.SOUTH);
    
    return jpanel;
  }
  
  private
  JPanel createDetailsPanel()
  {
    JPanel jpanel = new JPanel(new BorderLayout());
    
    JTextArea jTextArea = new JTextArea(getDetails());
    jTextArea.setEditable(false);
    jTextArea.setCaretPosition(0);
    JScrollPane jScrollPane = new JScrollPane(jTextArea);
     jScrollPane.setPreferredSize(new Dimension(iPrefWidth, iPrefHeight * 2));
    jpanel.add(jScrollPane, BorderLayout.CENTER);
    
    return jpanel;
  }
  
  private
  String getDetails()
  {
    if(oThrowable == null) {
      return "";
    }
    StringWriter sw = new StringWriter();
    oThrowable.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }
}
