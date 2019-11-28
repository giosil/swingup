package org.dew.swingup.rpc;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.xmlrpc.*;

import org.dew.swingup.*;

/**
 * Implementazione di AGUIRPCClient che utilizza il protocollo XML-RPC.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class GuiXmlRpcClient extends AGUIRPCClient implements AsyncCallback
{
  private XmlRpcClient firstRpcClient;
  private XmlRpcClient bakRpcClient;
  
  private String sURL;
  private String sSessionId;
  
  private String    sMethodCall = null;
  private Object    oResult     = null;
  private Exception oException  = null;
  
  public
  GuiXmlRpcClient()
  {
    super();
  }
  
  public
  String getProtocolName()
  {
    return sPROTOCOL_XMLRPC;
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
      firstRpcClient = new XmlRpcClient(sURL + ";jsessionid=" + sSessionId);
    }
    else {
      firstRpcClient = new XmlRpcClient(sURL);
    }
    if(sBACKUP != null && sBACKUP.length() > 0) {
      bakRpcClient = new XmlRpcClient(sBACKUP);
    }
    this.sURL = sURL;
    
    // Nella nuova libreria nella quale e' stata riscritta (senza cambiarla)
    // l'interfaccia di org.apache.xmlrpc.XmlRpcClient e' stato utilizzato
    // il metodo equals per passare ulteriori configurazioni.
    if(firstRpcClient != null) firstRpcClient.equals(new Integer(iTimeOut));
    if(bakRpcClient   != null) bakRpcClient.equals(new Integer(iTimeOut));
    if(mapHeaders != null && !mapHeaders.isEmpty()) {
      if(firstRpcClient != null) firstRpcClient.equals(mapHeaders);
      if(bakRpcClient   != null) bakRpcClient.equals(mapHeaders);
    }
  }
  
  public
  void setHeaders(Map mapHeaders)
  {
    super.setHeaders(mapHeaders);
    // Nella nuova libreria nella quale e' stata riscritta (senza cambiarla)
    // l'interfaccia di org.apache.xmlrpc.XmlRpcClient e' stato utilizzato
    // il metodo equals per passare la mappa degli Headers.
    if(firstRpcClient != null) firstRpcClient.equals(mapHeaders);
    if(bakRpcClient   != null) bakRpcClient.equals(mapHeaders);
  }
  
  public
  void setTimeOut(int iTimeOut)
  {
    super.setTimeOut(iTimeOut);
    if(firstRpcClient != null) firstRpcClient.equals(new Integer(iTimeOut));
    if(bakRpcClient   != null) bakRpcClient.equals(new Integer(iTimeOut));
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
      firstRpcClient = new XmlRpcClient(sURL + ";jsessionid=" + sSessionId);
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
      firstRpcClient = new XmlRpcClient(sURL + ";jsessionid=" + sSessionId);
      // Quando si gestisce la sessione non e' supportato lo switch sul backup...
      bakRpcClient = null;
    }
  }
  
  protected
  Object invoke(String sMethod, Vector vParameters)
    throws Exception
  {
    int iElapsed = 0;
    sMethodCall  = null;
    oResult      = null;
    oException   = null;
    
    if(iTimeOut > 0) {
      firstRpcClient.executeAsync(sMethod, vParameters, this);
      while(!sMethod.equals(sMethodCall)) {
        Thread.sleep(100);
        iElapsed += 100;
        if(iElapsed > iTimeOut) {
          throw new RPCCallTimedOutException(sMethod + " call timed out at " + firstRpcClient.getURL());
        }
      }
    }
    else {
      firstRpcClient.executeAsync(sMethod, vParameters, new EmptyAsyncCallback());
      return null;
    }
    
    if(oException != null) {
      String sMessage = oException.getMessage();
      if(oException instanceof IOException) {
        throw new IOException(sMessage);
      }
      else
      if(oException instanceof XmlRpcException) {
        if(((XmlRpcException) oException).code == -1) {
          throw new RPCInvalidSessionException();
        }
        else {
          throw new Exception(((XmlRpcException) oException).code + ": " + sMessage);
        }
      }
      else {
        throw new Exception(sMessage);
      }
    }
    
    return oResult;
  }
  
  protected
  Object invoke_bak(String sMethod, Vector vParameters)
    throws Exception
  {
    if(bakRpcClient != null) {
      int iElapsed = 0;
      sMethodCall  = null;
      oResult      = null;
      oException   = null;
      
      if(iTimeOut > 0) {
        bakRpcClient.executeAsync(sMethod, vParameters, this);
        while(!sMethod.equals(sMethodCall)) {
          Thread.sleep(100);
          iElapsed += 100;
          if(iElapsed > iTimeOut) {
            throw new RPCCallTimedOutException(sMethod + " call timed out at " + bakRpcClient.getURL());
          }
        }
      }
      else {
        bakRpcClient.executeAsync(sMethod, vParameters, new EmptyAsyncCallback());
        return null;
      }
      
      if(oException != null) {
        String sMessage = oException.getMessage();
        if(oException instanceof IOException) {
          throw new IOException(sMessage);
        }
        else
        if(oException instanceof XmlRpcException) {
          if(((XmlRpcException) oException).code == -1) {
            throw new RPCInvalidSessionException();
          }
          else {
            throw new Exception(((XmlRpcException) oException).code + ": " + sMessage);
          }
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
  void handleResult(Object object, URL url, String method)
  {
    this.oResult     = object;
    this.sMethodCall = method;
  }
  
  public
  void handleError(Exception exception, URL url, String method)
  {
    this.oException  = exception;
    this.sMethodCall = method;
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
  
  public
  String toString()
  {
    return firstRpcClient != null ? firstRpcClient.toString() : "";
  }
  
  class EmptyAsyncCallback implements AsyncCallback
  {
    public void handleResult(Object result, URL url, String method) {
      System.out.println(ResourcesMgr.sLOG_PREFIX + " " + method + " -> " + result);
    }
    public void handleError(Exception exception, URL url, String method) {
      System.err.println(ResourcesMgr.sLOG_PREFIX + " " + method + " -> ex: " + exception);
    }
  }
}
