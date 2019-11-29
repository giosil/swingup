package org.dew.swingup.rpc;

import java.util.*;
import java.io.*;

import org.dew.rpc.util.RpcUtil;

/**
 * Implementazione di AGUIRPCClient che consente di accedere a classi locali.
 */
public
class GuiLocalRPCClient extends AGUIRPCClient
{
  protected static Map<String,Object> mapHandlers = new HashMap<String,Object>();
  
  public
  GuiLocalRPCClient()
  {
  }

  public
  String getProtocolName()
  {
    return sPROTOCOL_JAVA;
  }

  public
  void init(String sURL, String sBACKUP)
    throws Exception
  {
    System.out.println("[GuiLocalRPCClient] init");
  }

  public
  void begin()
    throws Exception
  {
    System.out.println("[GuiLocalRPCClient] begin");
  }

  public
  void commit()
    throws Exception
  {
    System.out.println("[GuiLocalRPCClient] commit");
  }

  public
  void rollback()
    throws Exception
  {
    System.out.println("[GuiLocalRPCClient] rollback");
  }

  public
  void openSession(String sPrincipal, String sCredential)
    throws Exception
  {
    System.out.println("[GuiLocalRPCClient] openSession(" + sPrincipal + ",*=)");
  }

  public
  void closeSession()
    throws Exception
  {
    System.out.println("[GuiLocalRPCClient] closeSession");
  }

  protected
  Object invoke(String sMethod, List<?> listParameters)
    throws Exception
  {
    try {
      return RpcUtil.executeMethod(mapHandlers, null, sMethod, listParameters);
    } 
    catch (Throwable ex) {
      System.err.println("[GuiLocalRPCClient] " + sMethod + ": "+ ex);
      throw new Exception(ex);
    }
  }

  protected
  Object invoke_bak(String sMethod, List<?> listParameters)
    throws Exception
  {
    throw new IOException();
  }

  public static
  void addService(Object oHandler, String sId, String sDescription)
  {
    if(sId == null || oHandler == null) return;
    mapHandlers.put(sId, oHandler);
    System.out.println("[GuiLocalRPCClient] " + sId + " (" + sDescription + ") handler added.");
  }
}
