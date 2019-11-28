package org.dew.swingup;

import java.util.*;

/**
 * Interfaccia che permette di implementare un gestore di Sessione.
 *
 * @version 1.0
 */
public
interface ISessionManager
{
  /**
   * Restituisce true se la sessione e' attiva.
   *
   * @return Flag attivo
   */
  public
  boolean isActive();
  
  /**
   * Restituisce un eventuale messaggio utente.
   *
   * @return Messaggio utente
   */
  public
  String getUserMessage();
  
  /**
   * Restituisce l'utente loggato.
   *
   * @return Utente
   */
  public
  User getUser();
  
  /**
   * Esegue il login.
   *
   * @param sIdService Identificativo del servizio
   * @param sUser      Nome utente
   * @param sPassword  Password
   * @param sIdClient  Identificativo della postazione
   * @throws Exception
   */
  public
  void login(String sIdService, String sUser, String sPassword, String sIdClient)
    throws Exception;
  
  /**
   * Esegue il login.
   *
   * @param sIdService    Identificativo del servizio
   * @param abSignature   Identificativo del servizio firmato digitalmente
   * @param sIdClient     Identificativo della postazione
   * @throws Exception
   */
  public
  void login(String sIdService, byte[] abSignature, String sIdClient)
    throws Exception;
  
  /**
   * Esegue il logout.
   */
  public
  void logout();
  
  /**
   * Permette di cambiare la password dell'utente loggato.
   *
   * @param sNewPassword Nuova password
   * @throws Exception
   */
  public
  void changePassword(String sNewPassword)
    throws Exception;
  
  /**
   * Restituisce i client associati al servizio.
   *
   * @param sIdService Identificativo del servizio
   * @return Lista degli identificativi dei client
   * @throws Exception
   */
  public
  List<String> getClients(String sIdService)
    throws Exception;
}
