package org.dew.swingup.fm;

import java.io.File;
import java.util.*;

import javax.swing.JDialog;

import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.rpc.IRPCClient;

@SuppressWarnings({"rawtypes","unchecked"})
public
class FMUtils
{
  public static final String sUSER_HOME = "user.home";
  
  // La chiave puo' contenere caratteri che appartengono all'insieme [32 (spazio) - 95 (_)]
  public static String sENCRYPTION_KEY = "@X<:S=?'B;F)<=B>D@?=:D';@=B<?C;)@:'/=?A-X0=;(?1<X!";
  
  public static JDialog parentDialog = null;
  
  public static void replaceName(List listOfMap, int iIndex, String sNameToReplace, String sNewName) {
    if(listOfMap != null && listOfMap.size() > iIndex) {
      Map map = (Map) listOfMap.get(iIndex);
      String sName = (String) map.get("n");
      if(sName == null) return;
      int iIndexOf = sName.indexOf(sNameToReplace);
      if(iIndexOf < 0) iIndexOf = sName.indexOf(sNameToReplace.replace('/', '\\'));
      if(iIndexOf >= 0) {
        if(sName.startsWith("..")) {
          sName = "..  -> " + sNewName + sName.substring(iIndexOf + sNameToReplace.length());
        }
        else
        if(sName.startsWith(".")) {
          sName = ".   -> " + sNewName + sName.substring(iIndexOf + sNameToReplace.length());
        }
        else {
          sName = sNewName + sName.substring(iIndexOf + sNameToReplace.length());
        }
        map.put("n", sName);
      }
    }
  }
  
  public static String getFileName(String sFilePath) {
    if(sFilePath == null) return null;
    int iLength = sFilePath.length();
    for(int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iIndex);
      if(c == '/' || c == '\\') {
        return sFilePath.substring(iIndex + 1);
      }
    }
    return sFilePath;
  }
  
  public static String getFolder(String sFilePath) {
    int iLength = sFilePath.length();
    for(int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iIndex);
      if(c == '/' || c == '\\') {
        return sFilePath.substring(0, iIndex);
      }
    }
    return "";
  }
  
  public static char getSeparator(String sFilePath) {
    int iLength = sFilePath.length();
    for(int i = 0; i < iLength; i++) {
      char c = sFilePath.charAt(i);
      if(c == '/' || c == '\\') return c;
    }
    return '/';
  }
  
  public static boolean isPath(String sText) {
    if(sText == null) return false;
    int iLength = sText.length();
    for(int i = 0; i < iLength; i++) {
      char c = sText.charAt(i);
      if(c == '/' || c == '\\') return true;
    }
    return false;
  }
  
  public static
  List getFiles(String sPrefix, File fLocalDirectory)
  {
    File[] afFiles  = fLocalDirectory.listFiles();
    String sDirName = fLocalDirectory.getName();
    List listResult = new ArrayList();
    if(afFiles != null && afFiles.length > 0) {
      for(int i = 0; i < afFiles.length; i++) {
        File file = afFiles[i];
        if(file.isDirectory()) {
          if(sPrefix != null && sPrefix.length() > 0) {
            listResult.addAll(getFiles(sPrefix + File.separator + sDirName, file));
          }
          else {
            listResult.addAll(getFiles(sDirName, file));
          }
          continue;
        }
        String sFile = null;
        if(sPrefix != null && sPrefix.length() > 0) {
          sFile = sPrefix + File.separator + sDirName + File.separator + file.getName();
        }
        else {
          sFile = sDirName + File.separator + file.getName();
        }
        listResult.add(sFile);
      }
    }
    return listResult;
  }
  
  public static
  String getVersion(String sWS_URL)
  {
    try {
      IRPCClient oRPCClient = createRPCClient(sWS_URL);
      return (String) oRPCClient.execute("FM.getVersion", new Vector(), false);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }
  
  public static
  IRPCClient createRPCClient(String sURLWebService)
    throws Exception
  {
    if(sURLWebService == null) sURLWebService = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_URL);
    IRPCClient oRPCClient = ResourcesMgr.createIRPCClient();
    oRPCClient.init(sURLWebService, null);
    return oRPCClient;
  }
  
  public static
  String encrypt(String sText)
  {
    if(sText == null) return null;
    String sKey = sENCRYPTION_KEY;
    int k = 0;
    StringBuffer sb = new StringBuffer(sText.length());
    for(int i = 0; i < sText.length(); i++) {
      if(k >= sKey.length() - 1) {
        k = 0;
      }
      else {
        k++;
      }
      int c = sText.charAt(i);
      int d = sKey.charAt(k);
      int r = c;
      if(c >= 32 && c <= 126) {
        r = r - d;
        if(r < 32) {
          r = 127 + r - 32;
        }
      }
      sb.append((char) r);
    }
    return sb.toString();
  }
}
