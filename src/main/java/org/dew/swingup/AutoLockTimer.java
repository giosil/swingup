package org.dew.swingup;

/**
 * Classe utilizzata per implementare il meccanismo di autoblocco dell'applicazione.
 *
 * @version 1.0
 */
public
class AutoLockTimer implements Runnable
{
  protected boolean boStop    = false;
  protected boolean boRunning = false;
  protected int iBeats = 0;
  protected int iMaxBeats = 0;
  protected int iStep = 5000;
  
  /**
   * Costruttore.
   */
  public
  AutoLockTimer()
  {
  }
  
  /**
   * Fa partire il timer dell'autoblocco.
   */
  public synchronized
  void start()
  {
    int iAutoLock = ResourcesMgr.getIntProperty(ResourcesMgr.sAPP_AUTOLOCK, 0);
    start(iAutoLock);
  }
  
  /**
   * Fa partire il timer dell'autoblocco specificando il tempo limite espresso in minuti.
   *
   * @param iAutoLock tempo limite
   */
  public synchronized
  void start(int iAutoLock)
  {
    if(iAutoLock == 0) return;
    if(iAutoLock < 0) {
       iMaxBeats = iAutoLock * -1;
    }
    else {
       iMaxBeats = iAutoLock *(60000 / iStep);
    }
    Thread thread = new Thread(this);
    thread.setName("AutoLockTimer");
    thread.start();
  }
  
  /**
   * Resetta il timer dell'autoblocco.
   */
  public synchronized
  void reset()
  {
    iBeats = 0;
  }
  
  /**
   * Ferma il timer dell'autoblocco.
   */
  public synchronized
  void stop()
  {
    boStop  = true;
  }
  
  public
  boolean isRunning()
  {
    return boRunning;
  }
  
  /**
   * Implementazione di Runnable. Non invocare direttamente. Invocare i metodi start.
   */
  public
  void run()
  {
    boRunning = true;
    boStop = false;
    iBeats = 0;
    boolean boDoLock = false;
    try {
      while(true) {
        Thread.sleep(iStep);
        iBeats++;
        if(boStop) break;
        if(iBeats >= iMaxBeats) {
          boDoLock = true;
          break;
        }
      }
    }
    catch(Exception ex) {
    }
    boRunning = false;
    if(boDoLock && !boStop) {
      ResourcesMgr.mainFrame.lock();
    }
  }
}
