package org.dew.swingup.impl;

import java.util.*;

import org.dew.swingup.*;
import org.dew.swingup.rpc.*;

import java.security.cert.*;

/**
 * Implementazione di default dell'interfaccia ISessionManager.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class DefaultSessionManager implements ISessionManager
{
  private final static String sDEF_ROLE = "admin";
  private User oUser;
  
  private boolean boActive = false;
  private String sUserMessage;
  
  public
  DefaultSessionManager()
  {
  }
  
  public
  boolean isActive()
  {
    return boActive;
  }
  
  public
  String getUserMessage()
  {
    return sUserMessage;
  }
  
  public
  User getUser()
  {
    return oUser;
  }
  
  public
  void login(String sIdService, String sUserName, String sPassword, String sIdClient)
    throws Exception
  {
    oUser = new User();
    oUser.setUserName(sUserName);
    oUser.setPassword(sPassword);
    oUser.setRole(sDEF_ROLE);
    oUser.setFirstName("-");
    oUser.setLastName("-");
    oUser.setEmail(sUserName + "@info.it");
    oUser.setDatePassword(new Date());
    oUser.setDateLastAccess(new Date());
    oUser.setCurrentIdClient(sIdClient);
    Vector vGroups = new Vector(1);
    vGroups.add("DEFAULT");
    oUser.setGroups(vGroups);
    Vector vGrants = new Vector(1);
    vGrants.add("DEFAULT");
    oUser.setGrants(vGrants);
    Vector vStructures = new Vector(1);
    vStructures.add("DEFAULT");
    oUser.setStructures(vStructures);
    Vector vSubStructures = new Vector(1);
    vSubStructures.add("DEFAULT");
    oUser.setSubStructures(vSubStructures);
    
    Map mapResources = new HashMap();
    oUser.setResources(mapResources);
    boActive = true;
    
    String sWS_URL = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_URL);
    String sWS_BAK = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_BAK);
    IRPCClient oRPCClient = ResourcesMgr.createIRPCClient();
    if(oRPCClient != null) {
      oRPCClient.init(sWS_URL, sWS_BAK);
      ResourcesMgr.setDefaultRPCClient(oRPCClient);
    }
  }
  
  public
  void login(String sIdService, byte[] abSignature, String sIdClient)
    throws Exception
  {
    X509Certificate x509Certificate = SessionUtil.getX509CertificateFromSignature(abSignature);
    String sCommonName = SessionUtil.getCommonName(x509Certificate);
    String sTaxCode    = SessionUtil.getTaxCode(x509Certificate);
    String sSubjectDN  = SessionUtil.getSubjectDistinguishedName(x509Certificate);
    String sIssuerDN   = SessionUtil.getIssuerDistinguishedName(x509Certificate);
    
    oUser = new User();
    oUser.setCertSubjectDN(sSubjectDN);
    oUser.setCertIssuerDN(sIssuerDN);
    oUser.setUserName(sCommonName);
    oUser.setUserClass("Classe");
    oUser.setPassword(null);
    oUser.setRole(sDEF_ROLE);
    oUser.setFirstName("-");
    oUser.setLastName("-");
    oUser.setEmail("user@info.it");
    oUser.setDatePassword(new Date());
    oUser.setDateLastAccess(new Date());
    oUser.setCurrentIdClient(sIdClient);
    oUser.setTaxCode(sTaxCode);
    Vector vGroups = new Vector(1);
    vGroups.add("DEFAULT");
    oUser.setGroups(vGroups);
    Vector vGrants = new Vector(1);
    vGrants.add("DEFAULT");
    oUser.setGrants(vGrants);
    Vector vStructures = new Vector(1);
    vStructures.add("DEFAULT");
    oUser.setStructures(vStructures);
    Vector vSubStructures = new Vector(1);
    vSubStructures.add("DEFAULT");
    oUser.setSubStructures(vSubStructures);
    
    Map mapResources = new HashMap();
    oUser.setResources(mapResources);
    boActive = true;
    
    String sWS_URL = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_URL);
    String sWS_BAK = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_BAK);
    IRPCClient oRPCClient = ResourcesMgr.createIRPCClient();
    if(oRPCClient != null) {
      oRPCClient.init(sWS_URL, sWS_BAK);
      ResourcesMgr.setDefaultRPCClient(oRPCClient);
    }
  }
  
  public
  void logout()
  {
    oUser = null;
    boActive = false;
  }
  
  public
  void changePassword(String sNewPassword)
    throws Exception
  {
    oUser.setPassword(sNewPassword);
    oUser.setDatePassword(new Date());
  }
  
  public
  List getClients(String sIdServices)
    throws Exception
  {
    List oResult = new ArrayList(3);
    oResult.add("DEFAULT");
    oResult.add("EXTERNAL");
    return oResult;
  }
}
