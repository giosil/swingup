package org.dew.swingup.util;

/**
 * Classe di utilita' per la definizione degli alias.
 * Utilizzata in FormPanel nella gestione degli alias.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version $Revision: 2 $
 */
public
class Alias
{
  protected String sName;
  protected Object oResource;
  
  public
  Alias(String sName)
  {
    this.sName = sName;
  }
  
  public
  Alias(String sName, Object oResource)
  {
    this.sName = sName;
    this.oResource = oResource;
  }
  
  public
  void setName(String sName)
  {
    this.sName = sName;
  }
  
  public
  String getName()
  {
    return sName;
  }
  
  public
  void setResource(Object oResource)
  {
    this.oResource = oResource;
  }
  
  public
  Object getResource()
  {
    return oResource;
  }
  
  public
  int hashCode()
  {
    if(sName == null) return 0;
    return sName.hashCode();
  }
  
  public
  boolean equals(Object anObject)
  {
    if(this == anObject) {
      return true;
    }
    
    if(anObject instanceof Alias) {
      if(sName != null) {
        return sName.equals(((Alias) anObject).getName());
      }
      else {
        return false;
      }
    }
    
    if(oResource != null) {
      return oResource.equals(anObject);
    }
    
    return false;
  }
  
  public
  String toString()
  {
    return sName + "->" + oResource;
  }
}
