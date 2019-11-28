package org.dew.swingup.util;

/**
 * Interfaccia da implementare per ottenere o impostare un valore.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public interface IValuable
{
  /**
   * Ottiene il valore.
   *
   * @return Object
   */
  public Object getValue();
  
  /**
   * Imposta il valore.
   *
   * @param oValue Object
   */
  public void setValue(Object oValue);
}
