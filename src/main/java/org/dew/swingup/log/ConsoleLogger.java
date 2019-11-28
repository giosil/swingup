package org.dew.swingup.log;

/**
 * Implementazione di ILogger che traccia le informazioni sulla console.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class ConsoleLogger implements ILogger
{
  private boolean boDebug = false;
  
  public
  ConsoleLogger()
  {
  }
  
  public
  ConsoleLogger(boolean boDebug)
  {
    init(null, boDebug);
  }
  
  public
  void init(String sLog, boolean boDebug)
  {
    this.boDebug = boDebug;
  }
  
  public
  void setDebug(boolean boDebug)
  {
    this.boDebug = boDebug;
  }
  
  public
  void info(String sText)
  {
    System.out.println(sText);
  }
  
  public
  void info(String sText, Throwable throwable)
  {
    System.out.print(sText + " ");
    throwable.printStackTrace();
  }
  
  public
  void debug(String sText)
  {
    if(!boDebug) return;
    System.out.println(sText);
  }
  
  public
  void debug(String sText, Throwable throwable)
  {
    if(!boDebug) return;
    System.out.print(sText + " ");
    throwable.printStackTrace();
  }
  
  public
  void error(String sText)
  {
    System.out.println(sText);
  }
  
  public
  void error(String sText, Throwable throwable)
  {
    System.out.print(sText + " ");
    throwable.printStackTrace();
  }
}
