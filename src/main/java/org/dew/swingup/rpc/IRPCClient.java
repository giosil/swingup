package org.dew.swingup.rpc;

import java.util.*;

/**
 * Interfaccia che permette di implementare un oggetto specializzato nella
 * chiamata di procedure remote.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
public interface IRPCClient
{
  public static final String sPROTOCOL_XMLRPC   = "xmlrpc";
  public static final String sPROTOCOL_SOAP     = "soap";
  public static final String sPROTOCOL_HTTP     = "http";
  public static final String sPROTOCOL_JOLT     = "jolt";
  public static final String sPROTOCOL_COPSI    = "copsi";
  public static final String sPROTOCOL_RMI      = "rmi";
  public static final String sPROTOCOL_RMI_IIOP = "rmi-iiop";
  public static final String sPROTOCOL_CORBA    = "corba";
  public static final String sPROTOCOL_DCOM     = "dcom";
  public static final String sPROTOCOL_JSONRPC  = "jsonrpc";
  
  /**
   * Inizializza il client RPC.
   *
   * @param sURL URL del servizio RPC
   * @param sBAKCUP URL del servizio di backup RPC
   * @throws Exception
   */
  public
  void init(String sURL, String sBAKCUP)
    throws Exception;
  
  /**
   * Imposta il time out.
   *
   * @param iTimeOut int
   */
  public
  void setTimeOut(int iTimeOut);
  
  /**
   * Imposta il time out predefinito.
   */
  public
  void setDefaultTimeOut();
  
  /**
   * Ritorna il time out impostato.
   *
   * @return int
   */
  public
  int getTimeOut();
  
  /**
   * Imposta l'header della chiamata.
   *
   * @param mapHeaders mappa Headers
   */
  public
  void setHeaders(Map mapHeaders);
  
  /**
   * Apre una nuova sessione.
   *
   * @param sPrincipal  Utente per l'apertura della sessione
   * @param sCredential Credenziale per l'apertura della sessione
   * @throws Exception
   */
  public
  void openSession(String sPrincipal, String sCredential)
    throws Exception;
  
  /**
   * Chiude una sessione.
   *
   * @throws Exception
   */
  public
  void closeSession()
    throws Exception;
  
  /**
   * Restituisce l'identificativo di sessione eventualmente aperto.
   *
   * @return String
   */
  public
  String getSessionId();
  
  /**
   * Imposta l'identificativo di sessione.
   *
   * @param sSessionId Identificativo di sessione
   * @throws Exception
   */
  public
  void setSessionId(String sSessionId)
    throws Exception;
  
  /**
   * Inizia una transazione. Tale metodo e' utilizzato per i protocolli che
   * supportano le transazioni.
   *
   * @throws Exception
   */
  public
  void begin()
    throws Exception;
  
  /**
   * Completa la transazione. Tale metodo e' utilizzato per i protocolli che
   * supportano le transazioni.
   *
   * @throws Exception
   */
  public
  void commit()
    throws Exception;
  
  /**
   * Annulla la transazione. Tale metodo e' utilizzato per i protocolli che
   * supportano le transazioni.
   *
   * @throws Exception
   */
  public
  void rollback()
    throws Exception;
  
  /**
   * Restituisce il nome del protocollo utilizzato per l'invocazione
   * dei metodi remoti.
   *
   * @return String
   */
  public
  String getProtocolName();
  
  /**
   * Esegue un metodo remoto.
   *
   * @param sMethod Metodo
   * @param vParameters Parametri
   * @return Risultato
   * @throws Exception
   */
  public
  Object execute(String sMethod, Vector vParameters)
    throws Exception;
  
  /**
   * Esegue un metodo remoto.
   *
   * @param sMethod Metodo
   * @param vParameters Parametri
   * @param boShowWaitPlease Flag per mostrare o meno la gui di attesa
   * @return Risultato
   * @throws Exception
   */
  public
  Object execute(String sMethod, Vector vParameters, boolean boShowWaitPlease)
    throws Exception;
}
