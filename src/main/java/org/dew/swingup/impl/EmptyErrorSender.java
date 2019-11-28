package org.dew.swingup.impl;

import org.dew.swingup.*;

/**
 * Implementazione di IErrorSender.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class EmptyErrorSender implements IErrorSender
{
  public
  EmptyErrorSender()
  {
  }
  
  public
  void send(String sMessage, Throwable oThrowable)
  {
    System.out.println("Message = " + sMessage + ", Throwable = " + oThrowable);
  }
}
