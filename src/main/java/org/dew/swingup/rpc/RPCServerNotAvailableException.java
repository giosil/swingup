package org.dew.swingup.rpc;

/**
 * Eccezione causata dalla indisponibilita' del server.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class RPCServerNotAvailableException extends Exception
{
  private static final long serialVersionUID = -2115301776497635482L;
  
  public RPCServerNotAvailableException()
  {
  }
  
  public
  RPCServerNotAvailableException(String sMessage)
  {
    super(sMessage);
  }
}
