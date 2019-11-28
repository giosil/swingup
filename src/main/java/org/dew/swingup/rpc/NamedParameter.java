package org.dew.swingup.rpc;

import java.util.Map;

/**
 * Classe di utilita' per l'utilizzo di parametri nominali.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
public
class NamedParameter implements Map.Entry
{
  String name;
  Object value;
  
  public
  NamedParameter(String sName, Object oValue)
  {
    this.name = sName;
    this.value = oValue;
  }
  
  public
  void setName(String name)
  {
    this.name = name;
  }
  
  public
  String getName()
  {
    return name;
  }
  
  public
  Object setValue(Object value)
  {
    Object oldValue = this.value;
    this.value = value;
    return oldValue;
  }
  
  public
  Object getValue()
  {
    return value;
  }
  
  public
  int hashCode()
  {
    if(name == null) return 0;
    return name.hashCode();
  }
  
  public
  boolean equals(Object anObject)
  {
    if(this == anObject) {
      return true;
    }
    if(anObject instanceof NamedParameter) {
      if(name != null) {
        return name.equals(((NamedParameter) anObject).getName());
      }
      else {
        return (((NamedParameter) anObject).getName() == null);
      }
    }
    return false;
  }
  
  public
  String toString()
  {
    return name + "=" + value;
  }
  
  // Map.Entry
  public Object getKey() {
    if(name == null) return "";
    return name;
  }
}
