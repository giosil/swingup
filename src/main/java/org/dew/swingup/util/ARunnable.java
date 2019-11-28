package org.dew.swingup.util;

/**
 * Classe astratta che implementa Runnable.
 * Essa puo' essere utilizzata quando si vuole passare un parametro
 * a cui si puo' accedere tramite il riferimento oData.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public abstract
class ARunnable implements Runnable
{
  protected Object oData;
  
  public
  ARunnable()
  {
  }
  
  public
  ARunnable(Object oData)
  {
    this.oData = oData;
  }
  
  public
  void setData(Object oData)
  {
    this.oData = oData;
  }
  
  public
  Object getData()
  {
    return oData;
  }
  
  public
  String toString()
  {
    if(oData == null) return "";
    
    return oData.toString();
  }
}
