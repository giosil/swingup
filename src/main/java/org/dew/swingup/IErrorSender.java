package org.dew.swingup;

/**
 * Interfaccia che permette di implementare un gestore per l'invio degli
 * errori ad un sistema di rilevazione.
 *
 * @version 1.0
 */
public
interface IErrorSender
{
  /**
   * Invia un errore al sistema di rilevazione.
   *
   * @param sMessage Messaggio
   * @param oThrowable Eccezione
   */
  public void send(String sMessage, Throwable oThrowable);
}
