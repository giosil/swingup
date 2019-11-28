package org.dew.swingup.file;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.net.*;

import org.dew.swingup.ResourcesMgr;

/**
 * Questa classe consente di effettuare ricerche di testo all'interno
 * di file dati.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class DataFileUtil
{
  public static char cSEPARATOR = ':';
  
  /**
   * Restituisce tutti i record del file dati.
   *
   * @param sDataFile Nome della risorsa
   * @throws Exception
   * @return List
   */
  public static
  List selectAll(String sDataFile)
    throws Exception
  {
    List listResult = new ArrayList();
    
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        if(sLine.trim().length() == 0) continue;
        listResult.add(getRecord(sLine));
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    
    return listResult;
  }
  
  /**
   * Effettua una ricerca con il pattern specificato.
   *
   * @param sDataFile Nome della risorsa
   * @param sPattern Pattern dell'espressione regolare.
   * @throws Exception
   * @return List
   */
  public static
  List selectPattern(String sDataFile, String sPattern)
    throws Exception
  {
    List listResult = new ArrayList();
    
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    
    Pattern pattern = Pattern.compile(sPattern);
    
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        if(sLine.trim().length() == 0) continue;
        if(pattern.matcher(sLine).matches()) {
          listResult.add(getRecord(sLine));
        }
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    
    return listResult;
  }
  
  /**
   * Effettua una ricerca con la modalita' del LIKE di SQL.
   *
   * @param sDataFile Nome della risorsa
   * @param sValue Testo da ricercare
   * @throws Exception
   * @return List
   */
  public static
  List selectLike(String sDataFile, String sValue)
    throws Exception
  {
    List listResult = new ArrayList();
    
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    
    String sPattern = normalizeForPattern(sValue);
    sPattern += getHexString(cSEPARATOR) + "*";
    Pattern pattern = Pattern.compile(sPattern);
    
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        if(sLine.trim().length() == 0) continue;
        if(pattern.matcher(sLine).matches()) {
          listResult.add(getRecord(sLine));
        }
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    
    return listResult;
  }
  
  /**
   * Effettua una ricerca con la modalita' del LIKE di SQL.
   *
   * @param sDataFile Nome della risorsa
   * @param iField Indice del campo
   * @param sValue Testo da ricercare
   * @throws Exception
   * @return List
   */
  public static
  List selectLike(String sDataFile, int iField, String sValue)
    throws Exception
  {
    List listResult = new ArrayList();
    
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    
    Pattern pattern = Pattern.compile(normalizeForPattern(sValue));
    
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        String sToken = getToken(sLine, iField);
        if(sToken != null && pattern.matcher(sToken).matches()) {
          listResult.add(getRecord(sLine));
        }
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    
    return listResult;
  }
  
  /**
   * Effettua una ricerca esatta del testo.
   * L'ottimizzazione implementata richiede che il file sia ordinato.
   *
   * @param sDataFile Nome della risorsa
   * @param sValue Testo da ricercare
   * @throws Exception
   * @return List
   */
  public static
  List select(String sDataFile, String sValue)
    throws Exception
  {
    List listResult = new ArrayList();
    
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      boolean boAtLeastOneFound = false;
      while((sLine = br.readLine()) != null) {
        if(sLine.equals(sValue)) {
          listResult.add(getRecord(sLine));
          boAtLeastOneFound = true;
        }
        else {
          // In presenza di un file con voci ordinate
          // quando non si e' ottenuto il match e si e' trovato
          // almeno un elemento allora si puo' saltare la lettura
          // delle voci successive.
          if(boAtLeastOneFound) break;
        }
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    
    return listResult;
  }
  
  /**
   * Effettua una ricerca sul primo campo.
   * L'ottimizzazione implementata richiede che il file sia ordinato.
   *
   * @param sDataFile Nome della risorsa
   * @param sValue Testo da ricercare
   * @throws Exception
   * @return List
   */
  public static
  List selectOnFirst(String sDataFile, String sValue)
    throws Exception
  {
    List listResult = new ArrayList();
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      boolean boAtLeastOneFound = false;
      while((sLine = br.readLine()) != null) {
        if(sLine.startsWith(sValue + cSEPARATOR)) {
          listResult.add(getRecord(sLine));
          boAtLeastOneFound = true;
        }
        else {
          // In presenza di un file con voci ordinate
          // quando non si e' ottenuto il match e si e' trovato
          // almeno un elemento allora si puo' saltare la lettura
          // delle voci successive.
          if(boAtLeastOneFound) break;
        }
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    return listResult;
  }
  
  /**
   * Effettua una ricerca su un campo specificato.
   *
   * @param sDataFile Nome della risorsa
   * @param iField Indice del campo
   * @param sValue Testo da ricercare
   * @throws Exception
   * @return List
   */
  public static
  List select(String sDataFile, int iField, String sValue)
    throws Exception
  {
    List listResult = new ArrayList();
    URL urlFile = ResourcesMgr.getURLDataFile(sDataFile);
    if(urlFile == null) throw new Exception("DataFile " + sDataFile + " not found");
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        String sToken = getToken(sLine, iField);
        if(sToken != null && sToken.equals(sValue)) {
          listResult.add(getRecord(sLine));
        }
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {}
    }
    return listResult;
  }
  
  private static
  List getRecord(String sText)
  {
    List listResult = new ArrayList();
    int iIndexOf = 0;
    int iBegin = 0;
    iIndexOf = sText.indexOf(cSEPARATOR);
    while(iIndexOf >= 0) {
      String sToken = sText.substring(iBegin, iIndexOf);
      listResult.add(sToken);
      iBegin = iIndexOf + 1;
      iIndexOf = sText.indexOf(cSEPARATOR, iBegin);
    }
    listResult.add(sText.substring(iBegin));
    return listResult;
  }
  
  private static
  String getToken(String sText, int iIndex)
  {
    if(sText == null) return null;
    int iCount = 0;
    int iIndexOf = 0;
    int iBegin = 0;
    iIndexOf = sText.indexOf(cSEPARATOR);
    while(iIndexOf >= 0) {
      String sToken = sText.substring(iBegin, iIndexOf);
      if(iCount == iIndex) return sToken;
      iBegin = iIndexOf + 1;
      iIndexOf = sText.indexOf(cSEPARATOR, iBegin);
      iCount++;
    }
    if(iCount == iIndex) {
      return sText.substring(iBegin);
    }
    return null;
  }
  
  /**
   * Ricava il pattern con la modalita' del LIKE di SQL.
   *
   * @param sText String
   * @return String
   */
  public static
  String normalizeForPattern(String sText)
  {
    if(sText == null) return "";
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < sText.length(); i++) {
      char c = sText.charAt(i);
      if(c == '%') {
        sb.append(".*");
      }
      else
      if(c == '_') {
        sb.append(".");
      }
      else
      if(!Character.isLetterOrDigit(c)) {
        sb.append("\\u00" + Integer.toHexString(c));
      }
      else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
  /**
   * Costruisce la rappresentazione esadecimale del carattere specificato.
   *
   * @param c carattere
   * @return String
   */
  public static
  String getHexString(char c)
  {
    return "\\u00" + Integer.toHexString(c);
  }
  
  /**
   * Restituisce l'estensione di un file.
   *
   * @param sFileName String
   * @return String
   */
  public static
  String getExtension(String sFileName)
  {
    if(sFileName == null) return "";
    int iLastDot = sFileName.lastIndexOf('.');
    if(iLastDot < 0) return "";
    if(iLastDot >= sFileName.length() - 1) return "";
    return sFileName.substring(iLastDot + 1);
  }
  
  /**
   * Restituisce il comando da eseguire sui sistemi operativi Windows per aprire un file.
   *
   * @param sFileName String
   * @return String
   */
  public static
  String getWINViewer(String sFileName)
  {
    return "cmd /C start " + formatDOSPath(sFileName);
  }
  
  /**
   * Restituisce il comando da eseguire sui sistemi operativi Linux per aprire un file.
   *
   * @param sFileName String
   * @return String
   */
  public static
  String getLinuxViewer(String sFileName)
  {
    String sViewer = ResourcesMgr.config.getProperty("linux.viewer");
    if(sViewer == null || sViewer.length() == 0) {
      sViewer = ResourcesMgr.dat.getProperty("linux.viewer");
    }
    if(sViewer != null && sViewer.length() > 0) {
      return sViewer + " " + formatUNIXPath(sFileName);
    }
    return "firefox " + formatUNIXPath(sFileName);
  }
  
  /**
   * Restituisce il comando da eseguire sui sistemi operativi MAC per aprire un file.
   *
   * @param sFileName String
   * @return String
   */
  public static
  String getMACViewer(String sFileName)
  {
    String sViewer = ResourcesMgr.config.getProperty("mac.viewer");
    if(sViewer == null || sViewer.length() == 0) {
      sViewer = ResourcesMgr.dat.getProperty("mac.viewer");
    }
    if(sViewer != null && sViewer.length() > 0) {
      return sViewer + " " + formatUNIXPath(sFileName);
    }
    return "open " + formatUNIXPath(sFileName);
  }
  
  /**
   * Restituisce il comando da eseguire su altri sistemi operativi
   * per aprire un file.
   *
   * @param sOSName Nome sistema operativo
   * @param sFileName String
   * @return String
   */
  public static
  String getViewer(String sOSName, String sFileName)
  {
    if(sOSName == null || sOSName.length() == 0) return null;
    if(sOSName.toUpperCase().indexOf("WIN") >= 0) {
      return getWINViewer(sFileName);
    }
    else
    if(sOSName.toUpperCase().indexOf("LINUX") >= 0) {
      return getLinuxViewer(sFileName);
    }
    String sViewer = ResourcesMgr.dat.getProperty(sOSName.replace(' ', '_') + ".viewer");
    if(sViewer == null || sViewer.length() == 0) {
      sViewer = ResourcesMgr.config.getProperty(sOSName.replace(' ', '_') + ".viewer");
    }
    if(sViewer != null && sViewer.length() > 0) {
      return sViewer + " " + formatUNIXPath(sFileName);
    }
    return getMACViewer(sFileName);
  }
  
  
  /**
   * Racchiude tra doppi apici le cartelle che contengono spazi.
   *
   * @param sFileName String
   * @return String
   */
  public static
  String formatDOSPath(String sFileName)
  {
    int iCount = 0;
    for(int i = 0; i < sFileName.length(); i++) {
      char c = sFileName.charAt(i);
      if(c == '\\' || c == '/') iCount++;
    }
    if(iCount < 2) return sFileName;
    int iBeginName = -1;
    StringBuffer sb = new StringBuffer();
    int j = 0;
    for(int i = 0; i < sFileName.length(); i++) {
      char c = sFileName.charAt(i);
      if(c == '\\' || c == '/') {
        j++;
        if(j == 1) {
          sb.append(c);
          sb.append('"');
        }
        else
        if(j == iCount) {
          sb.append('"');
          sb.append(c);
          iBeginName = sb.length();
        }
        else {
          sb.append('"');
          sb.append(c);
          sb.append('"');
        }
      }
      else {
        sb.append(c);
      }
    }
    String sResult = sb.toString();
    if(iBeginName > 0) {
      // Se il nome contiene uno spazio lo si mette tra doppi apici
      int iSpace = sResult.indexOf(' ', iBeginName);
      if(iSpace > 0) {
        sResult = sResult.substring(0, iBeginName) + "\"" + sResult.substring(iBeginName) + "\"";
      }
    }
    return sResult;
  }
  
  /**
   * Sostituisce gli spazi con la sequenza \[spazio].
   *
   * @param sFileName String
   * @return String
   */
  public static
  String formatUNIXPath(String sFileName)
  {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < sFileName.length(); i++) {
      char c = sFileName.charAt(i);
      if(c == ' ') {
        sb.append('\\');
        sb.append(' ');
      }
      else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
