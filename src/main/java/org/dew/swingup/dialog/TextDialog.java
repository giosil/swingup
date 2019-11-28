package org.dew.swingup.dialog;

import java.awt.*;
import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;

/**
 * Dialogo per mostrare un testo.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class TextDialog extends AJDialog
{
  JTextArea jTextArea;
  
  public
  TextDialog(Frame frame, String title, boolean modal)
  {
    super(frame, title, modal);
    this.setSize(600, 500);
    DefaultGUIManager.resizeForHRScreen(this);
  }
  
  public
  TextDialog(Dialog dialog, String title, boolean modal)
  {
    super(dialog, title, modal);
    this.setSize(600, 500);
    DefaultGUIManager.resizeForHRScreen(this);
  }
  
  public
  TextDialog(String sTitle)
  {
    super(ResourcesMgr.mainFrame, sTitle, true);
    this.setSize(640, 500);
    DefaultGUIManager.resizeForHRScreen(this);
  }
  
  /**
   * Mostra l'eventuale messaggio utente.
   *
   * @return true se l'utente ha cliccato OK.
   */
  public static
  boolean showUserMessage()
  {
    String sUserMessage = ResourcesMgr.getSessionManager().getUserMessage();
    if(sUserMessage == null || sUserMessage.trim().length() == 0) {
      sUserMessage = ResourcesMgr.config.getProperty(ResourcesMgr.sUSER_MESSAGE);
      if(sUserMessage == null || sUserMessage.trim().length() == 0) {
        return true;
      }
      sUserMessage = substString(sUserMessage, "\\n", "\n");
      sUserMessage = substString(sUserMessage, "\\t", "\t");
    }
    
    TextDialog textDialog = new TextDialog(IConstants.sTEXT_MESSAGE);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    textDialog.setText(sUserMessage);
    textDialog.setVisible(true);
    if(textDialog.isCancel()) {
      return false;
    }
    return true;
  }
  
  /**
   * Mostra un messaggio di testo.
   *
   * @param sText Testo
   * @return true se l'utente ha cliccato OK.
   */
  public static
  boolean showTextMessage(String sText)
  {
    if(sText == null || sText.trim().length() == 0) {
      return true;
    }
    TextDialog textDialog = new TextDialog(IConstants.sTEXT_MESSAGE);
    if(sText.length() < 150) {
      textDialog.setSize(300, 250);
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    textDialog.setText(sText);
    textDialog.setVisible(true);
    if(textDialog.isCancel()) {
      return false;
    }
    return true;
  }
  
  /**
   * Mostra un messaggio di testo.
   *
   * @param dialog Dialog di origine
   * @param sTitle Titolo della dialog
   * @param sText  Testo
   * @return true se l'utente ha cliccato OK.
   */
  public static
  boolean showTextMessage(Dialog dialog, String sTitle, String sText)
  {
    if(sText == null || sText.trim().length() == 0) {
      return true;
    }
    TextDialog textDialog = null;
    if(dialog != null) {
      textDialog = new TextDialog(dialog, sTitle, true);
    }
    else {
      textDialog = new TextDialog(sTitle);
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    textDialog.setText(sText);
    textDialog.setVisible(true);
    if(textDialog.isCancel()) {
      return false;
    }
    return true;
  }
  
  /**
   * Mostra un messaggio di testo.
   *
   * @param sText Testo
   * @param sTitle Titolo della dialog
   * @return true se l'utente ha cliccato OK.
   */
  public static
  boolean showTextMessage(String sText, String sTitle)
  {
    if(sText == null || sText.trim().length() == 0) {
      return true;
    }
    TextDialog textDialog = new TextDialog(sTitle);
    if(sText.length() < 150) {
      textDialog.setSize(300, 250);
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    textDialog.setText(sText);
    textDialog.setVisible(true);
    if(textDialog.isCancel()) {
      return false;
    }
    return true;
  }
  
  /**
   * Mostra la dialog con la casella di testo editabile.
   *
   * @param dialog Dialog di origine
   * @param sTitle Titolo della dialog
   * @param sText Testo
   * @return testo inserito dall'utente (null se l'utente ha cliccato annulla).
   */
  public static
  String showEditableTextDialog(Dialog dialog, String sTitle, String sText)
  {
    TextDialog textDialog = null;
    if(dialog != null) {
      textDialog = new TextDialog(dialog, sTitle, true);
    }
    else {
      textDialog = new TextDialog(sTitle);
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    textDialog.setEditable(true);
    textDialog.setText(sText);
    textDialog.setVisible(true);
    if(textDialog.isCancel()) {
      return null;
    }
    return textDialog.getText();
  }
  
  /**
   * Mostra la dialog con la casella di testo editabile.
   *
   * @param sText Testo
   * @param sTitle Titolo della dialog
   * @return testo inserito dall'utente (null se l'utente ha cliccato annulla).
   */
  public static
  String showEditableTextDialog(String sText, String sTitle)
  {
    TextDialog textDialog = new TextDialog(sTitle);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    textDialog.setLocation(screenSize.width/2 - textDialog.getSize().width/2,
      screenSize.height/2 - textDialog.getSize().height/2);
    textDialog.setEditable(true);
    textDialog.setText(sText);
    textDialog.setVisible(true);
    if(textDialog.isCancel()) {
      return null;
    }
    return textDialog.getText();
  }
  
  /**
   * Imposta il testo.
   *
   * @param sText Testo del messaggio
   */
  public
  void setText(String sText)
  {
    jTextArea.setText(sText);
    if(!jTextArea.isEditable()) {
      jTextArea.setCaretPosition(0);
    }
  }
  
  /**
   * Ottiene il testo.
   *
   * @return Testo
   */
  public
  String getText()
  {
    return jTextArea.getText();
  }
  
  /**
   * Imposta il flag editabile.
   *
   * @param boEditable Flag editabile.
   */
  public
  void setEditable(boolean boEditable)
  {
    jTextArea.setEditable(boEditable);
  }
  
  /**
   * Restituisce il flag editabile.
   *
   * @return boolean
   */
  public
  boolean isEditable()
  {
    return jTextArea.isEditable();
  }
  
  public
  Container buildGUI()
    throws Exception
  {
    jTextArea = new JTextArea();
    Font oFontMonospaced = new Font("Monospaced", jTextArea.getFont().getStyle(), jTextArea.getFont().getSize());
    jTextArea.setFont(oFontMonospaced);
    jTextArea.setText("");
    jTextArea.setEditable(false);
    JScrollPane jScrollPane = new JScrollPane(jTextArea);
    return jScrollPane;
  }
  
  public
  boolean doOk()
  {
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
  
  public static
  String substString(String sText, String s1, String s2)
  {
    int iParLen  = s1.length();
    int iTextLen = sText.length();
    int iIndexOf = sText.indexOf(s1);
    while(iIndexOf >= 0) {
      String sLeft = sText.substring(0, iIndexOf);
      String sRight = null;
      if(iIndexOf + iParLen >= iTextLen) {
        sRight = "";
      }
      else {
        sRight = sText.substring(iIndexOf + iParLen);
      }
      sText = sLeft + s2 + sRight;
      iIndexOf = sText.indexOf(s1);
    }
    return sText;
  }
}
