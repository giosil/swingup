package org.dew.swingup.rpc;

/**
 * Eccezione relativa ad una operazione fuori tempo limite.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class RPCCallTimedOutException extends Exception
{
  private static final long serialVersionUID = -7546679071124606968L;
  
  /**
   * Costruttore
   */
  public
  RPCCallTimedOutException()
  {
  }
  
  /**
   * Construttore con l'impostazione del messaggio.
   * @param sMessage String
   */
  public
  RPCCallTimedOutException(String sMessage)
  {
    super(sMessage);
  }
}
