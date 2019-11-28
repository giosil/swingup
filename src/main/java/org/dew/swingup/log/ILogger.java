package org.dew.swingup.log;

/**
 * Interfaccia che permette di implementare un gestore di logging.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
interface ILogger
{
  /**
   * Inizializza il logger.
   *
   * @param sLog Destinazione (es. nome file)
   * @param boDebug flag di debug
   */
  public
  void init(String sLog, boolean boDebug);
  
  /**
   * Imposta il flag di debug.
   *
   * @param boDebug boolean
   */
  public
  void setDebug(boolean boDebug);
  
  /**
   * Traccia un testo con un livello di info.
   *
   * @param sText
   */
  public
  void info(String sText);
  
  /**
   * Traccia un testo e l'oggetto Throwable con un livello di info.
   *
   * @param sText
   * @param throwable
   */
  public
  void info(String sText, Throwable throwable);
  
  /**
   * Traccia un testo con un livello di debug.
   *
   * @param sText
   */
  public
  void debug(String sText);
  
  /**
   * Traccia un testo e l'oggetto Throwable con un livello di debug.
   *
   * @param sText
   * @param throwable
   */
  public
  void debug(String sText, Throwable throwable);
  
  /**
   * Traccia un testo con un livello di error.
   *
   * @param sText
   */
  public
  void error(String sText);
  
  /**
   * Traccia un testo e l'oggetto Throwable con un livello di error.
   *
   * @param sText
   * @param throwable
   */
  public
  void error(String sText, Throwable throwable);
}
