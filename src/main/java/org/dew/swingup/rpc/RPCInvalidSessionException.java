package org.dew.swingup.rpc;

/**
 * Eccezione causata da una sessione non valida.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class RPCInvalidSessionException extends Exception
{
  /**
   * Costruttore
   */
  public
  RPCInvalidSessionException()
  {
  }
  
  /**
   * Construttore con l'impostazione del messaggio.
   * @param sMessage String
   */
  public
  RPCInvalidSessionException(String sMessage)
  {
    super(sMessage);
  }
}
