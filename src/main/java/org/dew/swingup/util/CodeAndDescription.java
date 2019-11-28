package org.dew.swingup.util;

import java.util.*;

/**
 * Classe di utilita' per contenere un Codice e una Descrizione.
 * Utile per i componenti come JComboBox in cui occorre inserire oggetti composti
 * da un codice e una descrizione.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class CodeAndDescription implements Map.Entry
{
  private Object code;
  private String description;
  
  public final String sCODE = "code";
  public final String sDESCRIPTION = "description";
  
  public CodeAndDescription(Object theCode, String theDescription)
  {
    this.code = theCode;
    this.description = theDescription;
  }
  
  public
  Object getCode()
  {
    return code;
  }
  
  public
  void setCode(Object code)
  {
    this.code = code;
  }
  
  public
  String getDescription()
  {
    return description;
  }
  
  public
  void setDescription(String description)
  {
    this.description = description;
  }
  
  public
  List toList()
  {
    List oResult = new ArrayList();
    oResult.add(code);
    oResult.add(description);
    return oResult;
  }
  
  public
  Map toMap()
  {
    Map oResult = new HashMap();
    oResult.put(sCODE, code);
    oResult.put(sDESCRIPTION, description);
    return oResult;
  }
  
  public
  int hashCode()
  {
    if(code == null) return 0;
    return code.hashCode();
  }
  
  public
  boolean equals(Object anObject)
  {
    if(this == anObject) {
      return true;
    }
    
    if(anObject instanceof CodeAndDescription) {
      if(code != null) {
        return code.equals(((CodeAndDescription) anObject).getCode());
      }
      else {
        return (((CodeAndDescription) anObject).getCode() == null);
      }
    }
    
    if(code == null) {
      return (anObject == null);
    }
    
    return code.equals(anObject);
  }
  
  public
  String toString()
  {
    if(description == null) return "";
    return description;
  }
  
  // Map.Entry
  public Object getKey() {
    if(description == null) return "";
    return description;
  }
  
  public Object getValue() {
    return code;
  }
  
  public Object setValue(Object value) {
    Object oldValue = code;
    this.code = value;
    return oldValue;
  }
}
