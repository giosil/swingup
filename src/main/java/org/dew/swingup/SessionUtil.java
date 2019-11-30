package org.dew.swingup;

import java.io.*;
import java.security.cert.*;
import java.security.*;
import java.util.*;

/**
 * Classe che raccoglie le utilita' per il controllo della sessione per
 * il cambio della password al primo accesso oppure dopo un periodo di scadenza, ecc.
 *
 * @version 1.0
 */
public
class SessionUtil
{
  /**
   * Verifica se la password e' da rinnovare.
   */
  public static
  void checkExpiringPassword()
  {
    User user = ResourcesMgr.getSessionManager().getUser();
    
    String sPassword = user.getPassword();
    
    // Controllo della scadenza della password al primo accesso
    if(user.getDateLastAccess() == null && sPassword != null && sPassword.length() > 0) {
      // Primo accesso
      boolean boExpiredFA = ResourcesMgr.getBooleanProperty(ResourcesMgr.sGUILOGIN_EXP_FIRSTA, false);
      if(boExpiredFA) {
        GUIMessage.showWarning("Questo \350 il Suo primo accesso. Il sistema Le richieder\340 di cambiare la password.");
        try {
          ResourcesMgr.getGUIManager().showGUIChangePassword(ResourcesMgr.mainFrame, true);
        }
        catch(Exception ex) {
          ex.printStackTrace();
          System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
          System.exit(1);
        }
        return;
      }
    }
    
    // Controllo della scadenza della password rispetto a quando e' stata aggiornata l'ultima volta.
    if(user.getDateLastAccess() == null && sPassword != null && sPassword.length() > 0) {
      int iExpirationDays = ResourcesMgr.getIntProperty(ResourcesMgr.sGUILOGIN_EXP_DAYS, 0);
      if(iExpirationDays > 0) {
        int iDays = getDaysFrom(user.getDatePassword());
        if(iExpirationDays <= iDays) {
          GUIMessage.showWarning("La Sua password risulta scaduta. Il sistema Le richieder\340 di cambiare la password.");
          try {
            ResourcesMgr.getGUIManager().showGUIChangePassword(ResourcesMgr.mainFrame, true);
          }
          catch(Exception ex) {
            ex.printStackTrace();
            System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
            System.exit(1);
          }
          return;
        }
      }
      else {
        int iMonths = getMonthsFrom(user.getDatePassword());
        int iExpirationMonths = ResourcesMgr.getIntProperty(ResourcesMgr.sGUILOGIN_EXP_MONTHS, 0);
        if(iExpirationMonths > 0 && iExpirationMonths <= iMonths) {
          GUIMessage.showWarning("La Sua password risulta scaduta. Il sistema Le richieder\340 di cambiare la password.");
          try {
            ResourcesMgr.getGUIManager().showGUIChangePassword(ResourcesMgr.mainFrame, true);
          }
          catch(Exception ex) {
            ex.printStackTrace();
            System.out.println(ResourcesMgr.sLOG_PREFIX + " exit application");
            System.exit(1);
          }
          return;
        }
      }
    }
  }
  
  public static
  int getMonthsFrom(Date oDate)
  {
    if(oDate == null) return 0;
    
    Calendar oData2 = new GregorianCalendar();
    oData2.setTime(oDate);
    int iY2 = oData2.get(Calendar.YEAR);
    int iM2 = oData2.get(Calendar.MONTH) + 1;
    int iD2 = oData2.get(Calendar.DAY_OF_MONTH);
    Calendar oData1 = new GregorianCalendar();
    int iY1 = oData1.get(Calendar.YEAR);
    int iM1 = oData1.get(Calendar.MONTH) + 1;
    int iD1 = oData1.get(Calendar.DAY_OF_MONTH);
    int iDeltaY = 0;
    if(iM2 < iM1) {
      iDeltaY = 0;
    }
    else
    if(iM2 == iM1 && iD2 <= iD1) {
      iDeltaY = 0;
    }
    else {
      iDeltaY = 1;
    }
    int iDeltaM = 0;
    if(iD2 > iD1) iDeltaM = 1;
    // Con la seguente formula si calcola il numero di mesi con approssimazione per difetto.
     int iResult = (iY1 - iY2 - iDeltaY) * 12 +(iM1 +(iDeltaY * 12) - iM2 - iDeltaM);
    return iResult;
  }
  
  public static
  int getDaysFrom(Date oDate)
  {
    if(oDate == null) return 0;
    
    long lTimeDataFine   = System.currentTimeMillis();
    long lTimeDataInizio = oDate.getTime();
    long lDiffTime       = lTimeDataFine - lTimeDataInizio;
    if(lDiffTime <= 0) return 0;
     int iDivisione  = (int)((lDiffTime) /(1000 * 60 * 60 * 24));
     int iResto      = (int)((lDiffTime) %(1000 * 60 * 60 * 24));
     int iRestoInOre = iResto /(1000 * 60 * 60);
    int iResult     = iDivisione;
    if(iRestoInOre > 12) { // Passaggio dall'ora solare all'ora legale...
      iResult++;
    }
    return iResult;
  }
  
  /**
   * Restituisce il MAC Address della scheda di rete installata.
   * ATTENZIONE: FUNZIONA ESCLUSIVAMENTE SU WINDOWS. SI BASA SULL'ELABORAZIONE DEL COMANDO IPCONFIG /ALL.
   *
   * @return String
   */
  public static
  String getMACAddress()
  {
    String sResult = null;
    try {
      Process pid = Runtime.getRuntime().exec("ipconfig /all");
      BufferedReader in = new BufferedReader(new InputStreamReader(pid.getInputStream()));
      String sLine = null;
      while((sLine = in.readLine()) != null) {
        if(getOccurrences(sLine, '-') == 5) {
          int i = sLine.lastIndexOf('-');
          sResult = sLine.substring(i - 14, i + 3);
          break;
        }
      }
      in.close();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    System.out.println(ResourcesMgr.sLOG_PREFIX + " MAC Address = " + sResult);
    return sResult;
  }
  
  protected static
  int getOccurrences(String sText, char c)
  {
    int iResult = 0;
    for(int i = 0; i < sText.length(); i++) {
      if(sText.charAt(i) == c) iResult++;
    }
    return iResult;
  }
  
  public static
  String getTaxCode(X509Certificate x509certificate)
  {
    if(x509certificate == null) return null;
    
    Principal subjectDN = x509certificate.getSubjectDN();
    String sSubjectDNName = subjectDN.getName();
    
    String sKey = "SERIALNUMBER";
    int iBegin = sSubjectDNName.indexOf(sKey + "=");
    if(iBegin >= 0) {
      int iEnd = sSubjectDNName.indexOf(',', iBegin);
      if(iEnd < 0) iEnd = sSubjectDNName.length();
      iBegin += sKey.length() + 1;
      String sSerialNumber = sSubjectDNName.substring(iBegin, iEnd);
      int iSep = sSerialNumber.indexOf(':');
      if(iSep >= 0) {
        return sSerialNumber.substring(iSep + 1);
      }
      else {
        return sSerialNumber;
      }
    }
    else {
      sKey = "CN";
      iBegin = sSubjectDNName.indexOf(sKey + "=");
      if(iBegin >= 0) {
        int iEnd = sSubjectDNName.indexOf(',', iBegin);
        if(iEnd < 0) iEnd = sSubjectDNName.length();
        iBegin += sKey.length() + 1;
        String sCN = sSubjectDNName.substring(iBegin, iEnd);
        if(sCN.startsWith("\"")) {
          sCN = sCN.substring(1);
        }
        if(sCN.length() > 16) {
          sCN = sCN.substring(0, 16);
        }
        return sCN;
      }
    }
    return null;
  }
  
  public static
  String getCommonName(X509Certificate x509certificate)
    throws Exception
  {
    if(x509certificate == null) return null;
    
    Principal subjectDN = x509certificate.getSubjectDN();
    String sSubjectDNName = subjectDN.getName();
    
    String sResult = null;
    String sKey = "CN";
    int iBegin = sSubjectDNName.indexOf(sKey + "=");
    if(iBegin >= 0) {
      int iEnd = sSubjectDNName.indexOf(',', iBegin);
      if(iEnd > 0) {
        iBegin += sKey.length() + 1;
        sResult = sSubjectDNName.substring(iBegin, iEnd);
      }
      else {
        iBegin += sKey.length() + 1;
        sResult = sSubjectDNName.substring(iBegin);
      }
    }
    if(sResult != null && sResult.startsWith("\"") && sResult.length() > 2) {
      return sResult.substring(1, sResult.length()-1);
    }
    return sResult;
  }
  
  public static
  String getIssuerDistinguishedName(X509Certificate x509certificate)
    throws Exception
  {
    if(x509certificate == null) return null;
    Principal issuerDN = x509certificate.getIssuerDN();
    return issuerDN.getName();
  }
  
  public static
  String getSubjectDistinguishedName(X509Certificate x509certificate)
    throws Exception
  {
    if(x509certificate == null) return null;
    Principal subjectDN = x509certificate.getSubjectDN();
    return subjectDN.getName();
  }
}
