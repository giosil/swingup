package org.dew.swingup;

import java.awt.*;
import javax.swing.JOptionPane;

import org.dew.swingup.components.StatusBar;
import org.dew.swingup.dialog.*;
import org.dew.swingup.rpc.*;

/**
 * Gestore e visualizzatore messaggi.
 *
 * @version 1.0
 */
public
class GUIMessage
{
  /**
   * Mostra un messaggio di errore senza dettagli.
   * Si consiglia di usare il metodo showException.
   *
   * @param oParentComponent Componente di appartenenza
   * @param sMessage Messaggio
   */
  public static
  void showError(Component oParentComponent, String sMessage)
  {
    JOptionPane.showMessageDialog(oParentComponent,
      sMessage, "Errore",
      JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Mostra un messaggio di errore senza dettagli.
   * Si consiglia di usare il metodo showException.
   *
   * @param sMessage Messaggio
   */
  public static
  void showError(String sMessage)
  {
    JOptionPane.showMessageDialog(ResourcesMgr.mainFrame,
      sMessage, "Errore",
      JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Mostra un messaggio di avvertimento.
   *
   * @param oParentComponent Componente di appartenenza
   * @param sMessage Messaggio
   */
  public static
  void showWarning(Component oParentComponent, String sMessage)
  {
    StatusBar sb = ResourcesMgr.getStatusBar();
    if(sb != null) {
      sb.setWarning(sMessage);
    }
    
    JOptionPane.showMessageDialog(oParentComponent,
      sMessage, "Attenzione",
      JOptionPane.WARNING_MESSAGE);
  }
  
  /**
   * Mostra un messaggio di avvertimento.
   *
   * @param sMessage Messaggio
   */
  public static
  void showWarning(String sMessage)
  {
    StatusBar sb = ResourcesMgr.getStatusBar();
    if(sb != null) {
      sb.setWarning(sMessage);
    }
    
    JOptionPane.showMessageDialog(ResourcesMgr.mainFrame,
      sMessage, "Attenzione",
      JOptionPane.WARNING_MESSAGE);
  }
  
  /**
   * Mostra un messaggio di informazione.
   *
   * @param oParentComponent Componente di appartenenza
   * @param sMessage Messaggio
   */
  public static
  void showInformation(Component oParentComponent, String sMessage)
  {
    JOptionPane.showMessageDialog(oParentComponent,
      sMessage, "Informazione",
      JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Mostra un messaggio di informazione.
   *
   * @param sMessage Messaggio
   */
  public static
  void showInformation(String sMessage)
  {
    JOptionPane.showMessageDialog(ResourcesMgr.mainFrame,
      sMessage, "Informazione",
      JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Mostra una semplice maschera di immissione.
   *
   * @param sPrompt String
   * @return String
   */
  public static
  String getInput(String sPrompt)
  {
    return JOptionPane.showInputDialog(ResourcesMgr.mainFrame, sPrompt);
  }
  
  /**
   * Mostra la maschera di immissione con il campo password.
   *
   * @param sPrompt String
   * @return String
   */
  public static
  String getPasswordInput(String sPrompt)
  {
    return PasswordDialog.getInput(sPrompt);
  }
  
  /**
   * Mostra la maschera di immissione con il campo password con possibilita' di disconnessione.
   * Ritorna la password digitata. Nel caso di disconnessione ritorna un '\n'.
   *
   * @param sPrompt String
   * @param boAddDisconnect boolean
   * @return String
   */
  public static
  String getPasswordInput(String sPrompt, boolean boAddDisconnect)
  {
    return PasswordDialog.getInput(sPrompt, boAddDisconnect);
  }
  
  /**
   * Ottiene una conferma dall'utente.
   *
   * @param oParentComponent Componente di appartenenza
   * @param sPrompt Testo della domanda.
   * @return boolean (true = yes, false = no)
   */
  public static
  boolean getConfirmation(Component oParentComponent, String sPrompt)
  {
    int iResult = JOptionPane.showConfirmDialog(oParentComponent,
      sPrompt, "Conferma",
      JOptionPane.YES_NO_OPTION);
    
    return (iResult == JOptionPane.YES_OPTION);
  }
  
  /**
   * Ottiene una conferma dall'utente.
   *
   * @param sPrompt Testo della domanda.
   * @return boolean (true = yes, false = no)
   */
  public static
  boolean getConfirmation(String sPrompt)
  {
    int iResult = JOptionPane.showConfirmDialog(ResourcesMgr.mainFrame,
      sPrompt, "Conferma",
      JOptionPane.YES_NO_OPTION);
    
    return (iResult == JOptionPane.YES_OPTION);
  }
  
  /**
   * Mostra un messaggio di errore con i dettagli dell'eccezione.
   *
   * @param dialog Finestra di dialogo.
   * @param sMessage Messaggio
   * @param oThrowable Eccezione
   */
  public static
  void showException(Dialog dialog, String sMessage, Throwable oThrowable)
  {
    if(oThrowable instanceof RPCServerNotAvailableException) {
      showWarning(dialog, IConstants.sTEXT_ERR_SERVER_NA);
      return;
    }
    else
    if(oThrowable instanceof RPCCallTimedOutException) {
      showWarning(dialog, IConstants.sTEXT_ERR_TIMED_OUT);
      return;
    }
    else
    if(oThrowable instanceof RPCInvalidSessionException) {
      showWarning(IConstants.sTEXT_ERR_INV_SESSION);
      return;
    }
    else
    if(oThrowable instanceof WarningException) {
      showWarning(dialog,((WarningException) oThrowable).getMessage());
      return;
    }
    
    if(oThrowable != null) oThrowable.printStackTrace();
    
    ExceptionDialog exceptionDialog = new ExceptionDialog(dialog, sMessage, oThrowable);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    exceptionDialog.setLocation(screenSize.width/2 - exceptionDialog.getSize().width/2,
      screenSize.height/2 - exceptionDialog.getSize().height/2);
    
    exceptionDialog.setVisible(true);
  }
  
  /**
   * Mostra un messaggio di errore con i dettagli dell'eccezione.
   *
   * @param sMessage Messaggio
   * @param oThrowable Eccezione
   */
  public static
  void showException(String sMessage, Throwable oThrowable)
  {
    if(oThrowable instanceof RPCServerNotAvailableException) {
      showWarning(IConstants.sTEXT_ERR_SERVER_NA);
      return;
    }
    else
    if(oThrowable instanceof RPCCallTimedOutException) {
      showWarning(IConstants.sTEXT_ERR_TIMED_OUT);
      return;
    }
    else
    if(oThrowable instanceof RPCInvalidSessionException) {
      showWarning(IConstants.sTEXT_ERR_INV_SESSION);
      return;
    }
    else
    if(oThrowable instanceof WarningException) {
      showWarning(((WarningException) oThrowable).getMessage());
      return;
    }
    
    if(oThrowable != null) oThrowable.printStackTrace();
    
    ExceptionDialog exceptionDialog = new ExceptionDialog(ResourcesMgr.mainFrame, sMessage, oThrowable);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    exceptionDialog.setLocation(screenSize.width/2 - exceptionDialog.getSize().width/2,
      screenSize.height/2 - exceptionDialog.getSize().height/2);
    
    exceptionDialog.setVisible(true);
  }
  
  /**
   * Mostra un messaggio di errore con i dettagli dell'eccezione.
   *
   * @param dialog Finestra di dialogo.
   * @param oThrowable Eccezione
   */
  public static
  void showException(Dialog dialog, Throwable oThrowable)
  {
    showException(dialog, IConstants.sTEXT_ERR_GENERIC, oThrowable);
  }
  
  /**
   * Mostra un messaggio di errore con i dettagli dell'eccezione.
   *
   * @param oThrowable Eccezione
   */
  public static
  void showException(Throwable oThrowable)
  {
    showException(IConstants.sTEXT_ERR_GENERIC, oThrowable);
  }
}
