package org.dew.swingup.rpc;

import java.io.*;
import java.net.*;
import java.util.*;

import org.dew.rpc.client.AsyncCallback;
import org.dew.soap.rpc.client.SoapRpcInvoker;
import org.dew.swingup.*;

/**
 * Implementazione di AGUIRPCClient che utilizza il protocollo SOAP-RPC (JSON in messaggi SOAP).
 */
@SuppressWarnings({"rawtypes"})
public
class GuiSoapRpcClient extends AGUIRPCClient implements AsyncCallback
{
  private SoapRpcInvoker firstRpcClient;
  private SoapRpcInvoker bakRpcClient;
  
  private String sURL;
  private String sSessionId;
  
  private String    sMethodCall = null;
  private Object    oResult     = null;
  private Throwable oException  = null;
  
  public
  GuiSoapRpcClient()
  {
    super();
  }
  
  public
  String getProtocolName()
  {
    return sPROTOCOL_SOAP;
  }
  
  public
  void init(String sURL, String sBACKUP)
    throws Exception
  {
    // Nel caso in cui vi e' gia' l'istanza di default, si recupera l'eventuale id di sessione.
    IRPCClient rpcClient = ResourcesMgr.getDefaultRPCClient();
    if((sSessionId == null || sSessionId.length() == 0) && rpcClient != null) {
      String sDefSessionId = rpcClient.getSessionId();
      if(sDefSessionId != null && sDefSessionId.length() > 0) {
        this.sSessionId = sDefSessionId;
      }
    }
    // Nel caso in cui l'id di sessione e' gia' stato impostato si crea il client
    // appendendo alla URL il parametro standard jsessionid.
    if(sSessionId != null && sSessionId.length() > 0) {
      firstRpcClient = new SoapRpcInvoker(sURL + ";jsessionid=" + sSessionId);
    }
    else {
      firstRpcClient = new SoapRpcInvoker(sURL);
    }
    if(sBACKUP != null) {
      bakRpcClient = new SoapRpcInvoker(sBACKUP);
    }
    this.sURL = sURL;
    if(firstRpcClient != null) firstRpcClient.getTransport().setTimeOut(iTimeOut);
    if(bakRpcClient   != null) bakRpcClient.getTransport().setTimeOut(iTimeOut);
    if(mapHeaders != null && !mapHeaders.isEmpty()) {
      if(firstRpcClient != null) firstRpcClient.getTransport().setHeaders(mapHeaders);
      if(bakRpcClient   != null) bakRpcClient.getTransport().setHeaders(mapHeaders);
    }
  }
  
  public
  void setHeaders(Map<String,Object> mapHeaders)
  {
    super.setHeaders(mapHeaders);
    if(firstRpcClient != null) firstRpcClient.getTransport().setHeaders(mapHeaders);
    if(bakRpcClient   != null) bakRpcClient.getTransport().setHeaders(mapHeaders);
  }
  
  public
  void setTimeOut(int iTimeOut)
  {
    super.setTimeOut(iTimeOut);
    if(firstRpcClient != null) firstRpcClient.getTransport().setTimeOut(iTimeOut);
    if(bakRpcClient   != null) bakRpcClient.getTransport().setTimeOut(iTimeOut);
  }
  
  public
  void begin()
    throws Exception
  {
  }
  
  public
  void commit()
    throws Exception
  {
  }
  
  public
  void rollback()
    throws Exception
  {
  }
  
  public
  void openSession(String sPrincipal, String sCredential)
    throws Exception
  {
    String sThePrincipal = "";
    String sTheCredential = "";
    if(sPrincipal != null) {
      sThePrincipal = sPrincipal;
    }
    if(sCredential != null) {
      sTheCredential = sCredential;
    }
    
    sSessionId = getSessionId(sURL + "?cmd=open" +
      "&principal=" + URLEncoder.encode(sThePrincipal, "UTF-8") +
      "&credential=" + URLEncoder.encode(sTheCredential, "UTF-8"));
    System.out.println(ResourcesMgr.sLOG_PREFIX + " HTTP session " + sSessionId + " opened");
    if(sSessionId != null && sSessionId.trim().length() > 0) {
      firstRpcClient = new SoapRpcInvoker(sURL + ";jsessionid=" + sSessionId);
      // Quando si mantiene la sessione non e' supportato lo switch sul backup...
      bakRpcClient = null;
    }
  }
  
  public
  void closeSession()
    throws Exception
  {
    String sId = getSessionId(sURL + ";jsessionid=" + sSessionId + "?cmd=close");
    if(sId == null) {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " HTTP session " + sSessionId + " closed");
    }
    else {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " HTTP session " + sId + " NOT closed!");
    }
    sSessionId = null;
  }
  
  public
  String getSessionId()
  {
    if(sSessionId == null) return "";
    return sSessionId;
  }
  
  public
  void setSessionId(String sSessionId)
    throws Exception
  {
    this.sSessionId = sSessionId;
    
    if(sSessionId != null && sSessionId.trim().length() > 0 && firstRpcClient != null) {
      firstRpcClient = new SoapRpcInvoker(sURL + ";jsessionid=" + sSessionId);
      // Quando si gestisce la sessione non e' supportato lo switch sul backup...
      bakRpcClient = null;
    }
  }
  
  protected
  Object invoke(String sMethod, List<?> listParameters)
    throws Exception
  {
    int iElapsed = 0;
    sMethodCall  = null;
    oResult      = null;
    oException   = null;
    
    if(iTimeOut > 0) {
      firstRpcClient.invokeAsync(sMethod, listParameters, this);
      while(!sMethod.equals(sMethodCall)) {
        Thread.sleep(100);
        iElapsed += 100;
        if(iElapsed > iTimeOut) {
          throw new RPCCallTimedOutException(sMethod + " call timed out at " + firstRpcClient.getURL());
        }
      }
    }
    else {
      firstRpcClient.invokeAsync(sMethod, listParameters, null);
      return null;
    }
    
    if(oException != null) {
      String sMessage = oException.getMessage();
      if(oException instanceof IOException) {
        throw new IOException(sMessage);
      }
      else {
        throw new Exception(sMessage);
      }
    }
    
    return oResult;
  }
  
  protected
  Object invoke_bak(String sMethod, List<?> listParameters)
    throws Exception
  {
    if(bakRpcClient != null) {
      int iElapsed = 0;
      sMethodCall  = null;
      oResult      = null;
      oException   = null;
      
      if(iTimeOut > 0) {
        bakRpcClient.invokeAsync(sMethod, listParameters, this);
        while(!sMethod.equals(sMethodCall)) {
          Thread.sleep(100);
          iElapsed += 100;
          if(iElapsed > iTimeOut) {
            throw new RPCCallTimedOutException(sMethod + " call timed out at " + bakRpcClient.getURL());
          }
        }
      }
      else {
        bakRpcClient.invokeAsync(sMethod, listParameters, null);
        return null;
      }
      
      if(oException != null) {
        String sMessage = oException.getMessage();
        if(oException instanceof IOException) {
          throw new IOException(sMessage);
        }
        else {
          throw new Exception(sMessage);
        }
      }
      
      return oResult;
    }
    throw new IOException();
  }
  
  public
  String toString()
  {
    return firstRpcClient != null ? firstRpcClient.toString() : "";
  }
  
  public
  void handleResult(String sMethod, Collection colArgs, Object result)
  {
    this.oResult     = result;
    this.sMethodCall = sMethod;
  }
  
  public
  void handleError(String sMethod, Collection colArgs, Throwable error)
  {
    this.oException  = error;
    this.sMethodCall = sMethod;
  }
  
  protected static
  String getSessionId(String sURL)
    throws Exception
  {
    URLConnection urlConn = new URL(sURL).openConnection();
    String sCookie = urlConn.getHeaderField("Set-Cookie");
    if(sCookie == null) return null;
    int iBegin = sCookie.indexOf("JSESSIONID=");
    if(iBegin < 0) {
      iBegin = sCookie.indexOf("jsessionid=");
    }
    if(iBegin < 0) return null;
    int iEnd = sCookie.indexOf(';');
    if(iEnd < 0) iEnd = sCookie.length();
    return sCookie.substring(iBegin + 11, iEnd);
  }
}

