package org.dew.swingup.rpc;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.log.*;
import org.dew.swingup.components.*;
import org.dew.swingup.util.SmartCardManager;

/**
 * Classe astratta che permette di implementare un oggetto specializzato nella chiamata di procedure remote.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
public abstract
class AGUIRPCClient implements IRPCClient
{
  protected boolean boUseBakup = false;
  protected ILogger oLogger = ResourcesMgr.getLogger();
  protected int iTimeOut    = ResourcesMgr.getIntProperty(ResourcesMgr.sAPP_RPC_TIMEOUT, 30000);;
  protected String sSessionIdSetted = null;
  
  protected boolean boSmartCard = false;
  protected SmartCardManager smartCardManager;
  protected Map mapHeaders;
  
  public
  AGUIRPCClient()
  {
    boSmartCard = ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_RPC_SMARTCARD, false);
    if(boSmartCard) {
      try {
        smartCardManager = new SmartCardManager();
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  
  /**
   * Imposta il time out espresso in millisecondi (ms).
   * Quando viene impostato a 0 l'invocazione RPC consiste nell'inviare
   * un messaggio di notifica.
   *
   * @param iTimeOut int
   */
  public
  void setTimeOut(int iTimeOut)
  {
    this.iTimeOut = iTimeOut;
  }
  
  /**
   * Imposta il time out predefinito.
   */
  public
  void setDefaultTimeOut()
  {
    int iRPCTimeOut = ResourcesMgr.getIntProperty(ResourcesMgr.sAPP_RPC_TIMEOUT, 30000);
    setTimeOut(iRPCTimeOut);
  }
  
  /**
   * Ritorna il time out impostato.
   *
   * @return int
   */
  public
  int getTimeOut()
  {
    return iTimeOut;
  }
  
  /**
   * Imposta l'header della chiamata.
   *
   * @param mapHeaders mappa Headers
   */
  public
  void setHeaders(Map<String,Object> mapHeaders)
  {
    this.mapHeaders = mapHeaders;
  }
  
  /**
   * Metodo di inizializzazione.
   *
   * @param sURL URL del servizio RPC
   * @param sBAKCUP URL del servizio di backup RPC
   * @throws Exception
   */
  public abstract
  void init(String sURL, String sBAKCUP)
    throws Exception;
  
  /**
   * Invoca un metodo remoto al servizio RPC predefinito.
   *
   * @param sMethod Metodo
   * @param listParameters Parametri
   * @return Object
   * @throws Exception
   */
  protected abstract
  Object invoke(String sMethod, List<?> listParameters)
    throws Exception;
  
  /**
   * Invoca un metodo remoto al servizio RPC di backup.
   *
   * @param sMethod Metodo
   * @param listParameters Parametri
   * @return Object
   * @throws Exception
   */
  protected abstract
  Object invoke_bak(String sMethod, List<?> listParameters)
    throws Exception;
  
  public
  String getSessionId()
  {
    if(sSessionIdSetted == null) {
      return String.valueOf(System.currentTimeMillis());
    }
    
    return sSessionIdSetted;
  }
  
  public
  void setSessionId(String sSessionId)
    throws Exception
  {
    this.sSessionIdSetted = sSessionId;
  }
  
  public
  String getProtocolName()
  {
    return "";
  }
  
  public
  Object execute(String sMethod, List<?> listParameters)
    throws Exception
  {
    if(boSmartCard) {
      if(!smartCardManager.check()) {
        throw new WarningException("Smart card non disponibile. Comunicazione interrotta.");
      }
    }
    
    Object oResult = null;
    
    StatusBar oStatusBar = ResourcesMgr.getStatusBar();
    
    if(oStatusBar != null) {
      oStatusBar.setWait();
    }
    
    long lBegin = System.currentTimeMillis();
    try {
      if(ResourcesMgr.bDebug) oLogger.debug("[RPC] " + sMethod + "(" + listParameters + ")");
      if(!boUseBakup) {
        oResult = invoke(sMethod, listParameters);
      }
      else {
        oResult = invoke_bak(sMethod, listParameters);
      }
      long lElapsed = System.currentTimeMillis() - lBegin;
      oLogger.debug("[RPC] " + sMethod + " -> " + lElapsed + " ms");
    }
    catch(IOException ioex) {
      System.err.println("[RPC] IOException: " + ioex);
      oLogger.error("[RPC] Exception in " + sMethod, ioex);
      if(!isServerNotAvailable(ioex)) {
        throw ioex;
      }
      // Switch
      oLogger.debug("[RPC] Switch to backup");
      boUseBakup = !boUseBakup;
      try{
        if(!boUseBakup) {
          oResult = invoke(sMethod, listParameters);
        }
        else {
          oResult = invoke_bak(sMethod, listParameters);
        }
        long lElapsed = System.currentTimeMillis() - lBegin;
        oLogger.debug("[RPC] " + sMethod + " -> " + lElapsed + " ms");
      }
      catch(IOException ioexbak) {
        System.err.println("[RPC] IOException: " + ioexbak);
        oLogger.error("[RPC] Exception in " + sMethod, ioexbak);
        if(!isServerNotAvailable(ioexbak)) {
          throw ioexbak;
        }
        boUseBakup = !boUseBakup;
        throw new RPCServerNotAvailableException();
      }
      catch(Exception ex) {
        oLogger.error("[RPC] Exception in " + sMethod, ex);
        throw ex;
      }
    }
    catch(Exception ex) {
      oLogger.error("[RPC] Exception in " + sMethod, ex);
      throw ex;
    }
    finally {
      if(oStatusBar != null) {
        oStatusBar.setPreviousStatus();
      }
    }
    
    return oResult;
  }
  
  public
  Object execute(String sMethod, List<?> listParameters, boolean boShowWaitPlease)
    throws Exception
  {
    if(boSmartCard) {
      if(!smartCardManager.check()) {
        throw new WarningException("Smart card non disponibile. Comunicazione interrotta.");
      }
    }
    
    if(boShowWaitPlease) {
      ResourcesMgr.setVisibleWaitPleaseWindow(true);
    }
    
    StatusBar oStatusBar = ResourcesMgr.getStatusBar();
    JFrame oMainFrame = ResourcesMgr.mainFrame;
    
    if(oStatusBar != null) {
      oStatusBar.setWait();
    }
    
    if(oMainFrame != null) {
      oMainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    Object oResult = null;
    long lBegin = System.currentTimeMillis();
    try {
      if(ResourcesMgr.bDebug) oLogger.debug("[RPC] " + sMethod + "(" + listParameters + ")");
      if(!boUseBakup) {
        oResult = invoke(sMethod, listParameters);
      }
      else {
        oResult = invoke_bak(sMethod, listParameters);
      }
      long lElapsed = System.currentTimeMillis() - lBegin;
      oLogger.debug("[RPC] " + sMethod + " -> " + lElapsed + " ms");
    }
    catch(IOException ioex) {
      System.err.println("[RPC] IOException: " + ioex);
      oLogger.error("[RPC] Exception in " + sMethod, ioex);
      if(!isServerNotAvailable(ioex)) {
        throw ioex;
      }
      // Switch
      oLogger.debug("[RPC] Switch to backup");
      boUseBakup = !boUseBakup;
      try{
        if(!boUseBakup) {
          oResult = invoke(sMethod, listParameters);
        }
        else {
          oResult = invoke_bak(sMethod, listParameters);
        }
        long lElapsed = System.currentTimeMillis() - lBegin;
        oLogger.debug("[RPC] " + sMethod + " -> " + lElapsed + " ms");
      }
      catch(IOException ioexbak) {
        System.err.println("[RPC] IOException: " + ioexbak);
        oLogger.error("[RPC] Exception in " + sMethod, ioexbak);
        if(!isServerNotAvailable(ioexbak)) {
          throw ioexbak;
        }
        boUseBakup = !boUseBakup;
        throw new RPCServerNotAvailableException();
      }
      catch(Exception ex) {
        oLogger.error("[RPC] Exception in " + sMethod, ex);
        throw ex;
      }
    }
    catch(Exception ex) {
      oLogger.error("[RPC] Exception in " + sMethod, ex);
      throw ex;
    }
    finally {
      if(boShowWaitPlease) {
        ResourcesMgr.setVisibleWaitPleaseWindow(false);
      }
      
      if(oStatusBar != null) {
        oStatusBar.setPreviousStatus();
      }
      
      if(oMainFrame != null) {
        oMainFrame.setCursor(Cursor.getDefaultCursor());
      }
    }
    return oResult;
  }
  
  protected
  boolean isServerNotAvailable(IOException ioex)
  {
    String sMessage = ioex.getMessage();
    if(sMessage == null) return true;
    if(sMessage.startsWith("null")) {
      // null value not supported by XML-RPC
      return false;
    }
    return true;
  }
}
