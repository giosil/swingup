package org.dew.swingup.file;

import java.util.Properties;

/**
 * Classe astratta che permette di definire un oggetto inseribile in CopyFile.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public abstract
class ContentObject
{
  protected Properties attributes = new Properties();
  protected StringBuffer content;
  protected Object data;
  
  /**
   * Imposta i dati che verranno trasformati.
   *
   * @param data Oggetto che contiene i dati
   */
  public
  void setData(Object data)
  {
    this.data = data;
  }
  
  /**
   * Ottiene i dati impostati tramite il metodo setData(Object data)
   *
   * @return data
   */
  public
  Object getData()
  {
    return data;
  }
  
  /**
   * Imposta gli attributi per la trasformazione dell'oggetto.
   *
   * @param name Nome dell'attributo
   * @param value Valore dell'attributo
   */
  public
  void setAttribute(String name, Object value)
  {
    if(name == null) {
      return;
    }
    if(value != null) {
      attributes.put(name, value.toString());
    }
    else {
      attributes.remove(name);
    }
  }
  
  /**
   * Ottiene il valore dell'attributo identificato con <i>name</i>, null se non esiste.
   *
   * @param name Nome dell'attributo
   * @return Valore dell'attributo
   */
  public
  String getAttribute(String name)
  {
    return attributes.getProperty(name);
  }
  
  /**
   * Imposta gli attributi.
   *
   * @param attributes Attributi
   */
  public
  void setAttributes(Properties attributes)
  {
    this.attributes = attributes;
  }
  
  /**
   * Ottiene il valore di tutti gli attributi immessi.
   *
   * @return Oggetto Properties contenente gli attributi.
   */
  public
  Properties getAttributes()
  {
    return attributes;
  }
  
  /**
   * Costruisce il contenuto dai dati impostati con setData(Object data).
   */
  public abstract void build();
  
  /**
   * Ottiene il riferimento all'oggetto che rappresenta il contenuto.
   *
   * @return StringBuffer che rappresenta il contenuto
   */
  public
  StringBuffer getStringBuffer()
  {
    if(content == null) {
      return new StringBuffer();
    }
    return content;
  }
  
  /**
   * Redefinisce il metodo toString() ereditato da Object.
   *
   * @return String Stringa rappresentante il contenuto.
   */
  public
  String toString()
  {
    if(content == null) {
      return "";
    }
    return content.toString();
  }
}
