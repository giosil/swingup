package org.dew.swingup;

/**
 * Interfaccia che permette di implementare gestire alcuni eventi dell'applicazione.
 *
 * @version 1.0
 */
public
interface IApplicationListener
{
  /**
   * Metodo invocato dopo l'inizializzazione dell'applicazione.
   */
  public void start();
  
  /**
   * Metodo invocato dopo il login.
   */
  public void afterLogin();
  
  /**
   * Metodo invocato prima del logout.
   */
  public void beforeLogout();
  
  /**
   * Metodo invocato prima dell'uscita dall'applicazione.
   */
  public void end();
}
