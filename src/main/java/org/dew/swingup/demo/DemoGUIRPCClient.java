package org.dew.swingup.demo;

import java.util.*;
import java.io.*;

import org.dew.swingup.rpc.*;

@SuppressWarnings("rawtypes")
public
class DemoGUIRPCClient extends AGUIRPCClient
{
  public DemoGUIRPCClient()
  {
    super();
    System.out.println("DemoGUIRPCClient.DemoGUIRPCClient()");
  }
  
  public
  void init(String sURL, String sBAKCUP)
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.init(" + sURL + "," + sBAKCUP + ")");
  }
  
  public
  void openSession(String sPrincipal, String sCredential)
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.openSession(" + sPrincipal + "," + sCredential + ")");
  }
  
  public
  void closeSession()
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.closeSession()");
  }
  
  public
  void begin()
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.begin()");
  }
  
  public
  void commit()
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.commit()");
  }
  
  public
  void rollback()
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.rollback()");
  }
  
  protected
  Object invoke(String sMethod, Vector vParameters)
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.invoke(" + sMethod + "," + vParameters + ")");
    throw new IOException();
  }
  
  protected
  Object invoke_bak(String sMethod, Vector vParameters)
    throws Exception
  {
    System.out.println("DemoGUIRPCClient.invoke_bak(" + sMethod + "," + vParameters + ")");
    try {
      Thread.sleep(2000);
    }
    catch(InterruptedException ex) {
    }
    if(vParameters != null && vParameters.size() > 0) {
      return vParameters.get(0);
    }
    return null;
  }
}
