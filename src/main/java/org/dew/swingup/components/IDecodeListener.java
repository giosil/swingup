package org.dew.swingup.components;

import java.util.*;

/**
 * Interfaccia per l'implementazione di un ascoltatore del componente di decodifica.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
public
interface IDecodeListener
{
  /**
   * Metodo invocato dopo il reset del componente decodificabile.
   */
  public void reset();
  
  /**
   * Metodo invocato prima della ricerca.
   *
   * @param oFilter Parametri di ricerca.
   */
  public void beforeFind(List oFilter);
  
  /**
   * Metodo invocato dopo il setValues del componente decodificabile.
   */
  public void set();
}
