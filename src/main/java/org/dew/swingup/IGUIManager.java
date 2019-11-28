package org.dew.swingup;

import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * Interfaccia che permette di implementare un gestore di GUI.
 *
 * @version 1.0
 */
public
interface IGUIManager
{
  /*
   * Costanti per identificare la GUI attraverso ActionEvent.
   */
  public static final String sAC_ABOUT          = "About";
  public static final String sAC_CHANGEPASSWORD = "ChangePassword";
  public static final String sAC_HELP           = "Help";
  public static final String sAC_LOCK           = "Lock";
  public static final String sAC_LOGIN          = "Login";
  public static final String sAC_OPTIONS        = "Options";
  public static final String sAC_USERMESSAGE    = "UserMessage";
  public static final String sAC_TEXTMESSAGE    = "TextMessage";
  public static final String sAC_UPDATEUI       = "UpdateUI";
  public static final String sAC_CDS            = "CDS";
  
  /**
   * Imposta l'oggetto ActionListener che permette di intercettare
   * le chiamate ai vari metodi di IGUIManager.
   *
   * @param al ActionListener
   */
  public void setActionListener(ActionListener al);
  
  /**
   * Metodo invocato per l'aggiornamento delle caratteristiche del L&F.
   *
   * @throws Exception
   */
  public void updateUIManager() throws Exception;
  
  /**
   * Lancia la GUI per il Login.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @return true = ok, false = annulla.
   * @throws Exception
   */
  public boolean showGUILogin(JFrame jframe) throws Exception;
  
  /**
   * Lancia la GUI per mostrare il Messaggio Utente dopo il Login.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @return true = ok, false = annulla.
   * @throws Exception
   */
  public boolean showGUIUserMessage(JFrame jframe) throws Exception;
  
  /**
   * Lancia la GUI per mostrare un testo.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @param sText Testo da mostrare.
   * @return true = ok, false = annulla.
   * @throws Exception
   */
  public boolean showGUITextMessage(JFrame jframe, String sText) throws Exception;
  
  /**
   * Lancia la GUI per mostrare un testo.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @param sText  Testo da mostrare.
   * @param sTitle Titolo del dialog.
   * @return true = ok, false = annulla.
   * @throws Exception
   */
  public boolean showGUITextMessage(JFrame jframe, String sText, String sTitle) throws Exception;
  
  /**
   * Lancia la GUI per l'help in linea.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @throws Exception
   */
  public void showGUIHelp(JFrame jframe) throws Exception;
  
  /**
   * Lancia la GUI per la visualizzazione della CDS.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @throws Exception
   */
  public void showGUICDS(JFrame jframe) throws Exception;
  
  /**
   * Lancia la GUI di about.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @throws Exception
   */
  public void showGUIAbout(JFrame jframe) throws Exception;
  
  /**
   * Lancia la GUI di blocco.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @throws Exception
   */
  public void showGUILock(JFrame jframe) throws Exception;
  
  /**
   * Lancia la GUI per cambiare la password.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @param boMandatory Specifica l'obbligatorieta' della dialog.
   * @return true = ok, false = annulla.
   * @throws Exception
   */
  public boolean showGUIChangePassword(JFrame jframe, boolean boMandatory)
    throws Exception;
  
  /**
   * Lancia la GUI delle impostazioni.
   *
   * @param jframe Frame dei riferimento del dialog.
   * @return true = ok, false = annulla.
   * @throws Exception
   */
  public boolean showGUIOptions(JFrame jframe) throws Exception;
}
