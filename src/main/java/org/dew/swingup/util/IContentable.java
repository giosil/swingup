package org.dew.swingup.util;

/**
 * Interfaccia da implementare per ottenere o impostare un contenuto.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public interface IContentable
{
  /**
   * Ottiene il contenuto.
   *
   * @return Object
   */
  public Object getContent();
  
  /**
   * Imposta il contenuto.
   *
   * @param oContent Object
   */
  public void setContent(Object oContent);
}
