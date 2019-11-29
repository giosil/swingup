package org.dew.swingup.fm;

import java.io.*;
import java.util.*;
import java.security.MessageDigest;

import org.dew.rpc.util.Base64Coder;

@SuppressWarnings({"rawtypes","unchecked"})
public
class FMService
{
  public final int iMAX_ENTRIES = 5000;
  public final String sTMP_EXT  = ".fmtmp";
  public final String sVERSION  = "1.2.1";
  
  protected static Map mapProcessesNotDestroyed = new HashMap();
  
  public
  String getVersion()
  {
    return sVERSION;
  }
  
  public
  Vector ls(String sDirectory, String sFilter)
    throws Exception
  {
    Vector vResult  = new Vector();
    File fDirectory = getFile(sDirectory, false, true, false);
    List listFolder = new ArrayList();
    List listFile   = new ArrayList();
    String[] asFiles = null;
    if(sFilter != null && sFilter.length() > 0) {
      asFiles = fDirectory.list(new FMFilenameFilter(decrypt(sFilter)));
    }
    else {
      asFiles = fDirectory.list();
    }
    if(asFiles != null && asFiles.length > 0) {
      Arrays.sort(asFiles);
      for(int i = 0; i < asFiles.length; i++) {
        File file = new File(fDirectory.getAbsolutePath() + File.separator + asFiles[i]);
        Hashtable htRecord = new Hashtable();
        htRecord.put("p", file.getAbsolutePath());
        htRecord.put("n", file.getName());
        htRecord.put("d", new Date(file.lastModified()));
        htRecord.put("l", String.valueOf(file.length()));
        if(file.isDirectory()) {
          htRecord.put("t", "d");
          listFolder.add(htRecord);
        }
        else {
          htRecord.put("t", "f");
          listFile.add(htRecord);
        }
        if(i >= iMAX_ENTRIES - 1) break;
      }
    }
    Hashtable htRecordCurr = new Hashtable();
    htRecordCurr.put("p", fDirectory.getAbsolutePath());
    htRecordCurr.put("n", ".   -> " + fDirectory.getAbsolutePath());
    htRecordCurr.put("t", "d");
    vResult.add(htRecordCurr);
    File fileParent = fDirectory.getParentFile();
    if(fileParent != null) {
      Hashtable htRecord = new Hashtable();
      htRecord.put("p", fileParent.getAbsolutePath());
      htRecord.put("n", "..  -> " + fileParent.getAbsolutePath());
      htRecord.put("t", "d");
      vResult.add(htRecord);
    }
    for(int i = 0; i < listFolder.size(); i++) {
      vResult.add(listFolder.get(i));
    }
    for(int i = 0; i < listFile.size(); i++) {
      vResult.add(listFile.get(i));
    }
    return vResult;
  }
  
  public
  boolean exist(String sDirectory, String sFileName)
    throws Exception
  {
    File fDirectory = getFile(sDirectory, false, true, false);
    if(sFileName == null || sFileName.length() == 0) return true;
    String sFile = fDirectory.getAbsolutePath() + File.separator + decrypt(sFileName);
    File file = new File(sFile);
    return file.exists();
  }
  
  public
  int check(String sFile)
    throws Exception
  {
    File file = getFile(sFile, false, false, false);
    if(!file.exists()) return 0;
    if(file.isFile())  return 1;
    return 2;
  }
  
  public
  Hashtable info(String sFile)
    throws Exception
  {
    Hashtable htResult = new Hashtable();
    File file = getFile(sFile, false, false, false);
    htResult.put("p",  file.getAbsolutePath());
    htResult.put("n",  file.getName());
    htResult.put("d",  new Date(file.lastModified()));
    htResult.put("l",  String.valueOf(file.length()));
    htResult.put("dt", new Date());
    if(file.isDirectory()) {
      File[] arrayOfFile = file.listFiles();
      int iCountDir  = 0;
      int iCountFile = 0;
      if(arrayOfFile != null) {
        for(int i = 0; i < arrayOfFile.length; i++) {
          File item = arrayOfFile[i];
          if(item.isDirectory()) {
            iCountDir++;
          }
          else {
            iCountFile++;
          }
        }
      }
      htResult.put("cd", new Integer(iCountDir));
      htResult.put("cf", new Integer(iCountFile));
      htResult.put("t", "d");
    }
    else {
      htResult.put("t", "f");
    }
    return htResult;
  }
  
  public
  String execute(String sDirectory, String sCommandLine, Vector vTextToType)
    throws Exception
  {
    if(sCommandLine == null || sCommandLine.length() == 0) {
      throw new Exception("FM#Invalid command line.");
    }
    sCommandLine = decrypt(sCommandLine);
    File fDirectory = getFile(sDirectory, false, true, false);
    if(sCommandLine.startsWith("./") && sCommandLine.length() > 2) {
      sCommandLine = fDirectory.getAbsolutePath() + "/" + sCommandLine.substring(2);
    }
    StringBuffer sbResult = new StringBuffer();
    Runtime runtime    = Runtime.getRuntime();
    Process process    = runtime.exec(sCommandLine, null, fDirectory);
    String sKeyProcess = getTimeStamp() + " " + sCommandLine;
    mapProcessesNotDestroyed.put(sKeyProcess, process);
    PrintWriter pw     = null;
    BufferedReader brI = null;
    BufferedReader brE = null;
    try {
      if(vTextToType != null && vTextToType.size() > 0) {
        pw = new PrintWriter(process.getOutputStream());
        for(int i = 0; i < vTextToType.size(); i++) {
          pw.println((String) vTextToType.get(i));
        }
        pw.close();
      }
      brI = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String sLine = null;
      while((sLine = brI.readLine()) != null) {
        sbResult.append(normalize(sLine) + "\n");
      }
      brE = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      while((sLine = brE.readLine()) != null) {
        sbResult.append("[Err] " + normalize(sLine) + "\n");
      }
    }
    finally {
      if(pw  != null) try{ pw.close();  } catch(Exception ex) {}
      if(brE != null) try{ brE.close(); } catch(Exception ex) {}
      if(brI != null) try{ brI.close(); } catch(Exception ex) {}
      if(process != null) {
        try{
          process.destroy();
          mapProcessesNotDestroyed.remove(sKeyProcess);
        }
        catch(Exception ex) {
        }
      }
    }
    return sbResult.toString();
  }
  
  public
  Vector getProcesses()
    throws Exception
  {
    Vector vResult = new Vector();
    Iterator iterator = mapProcessesNotDestroyed.keySet().iterator();
    while(iterator.hasNext()) {
      vResult.add(iterator.next());
    }
    Collections.sort(vResult);
    return vResult;
  }
  
  public
  boolean kill(String sKeyProcess)
    throws Exception
  {
    sKeyProcess = decrypt(sKeyProcess);
    Process process = (Process) mapProcessesNotDestroyed.get(sKeyProcess);
    if(process == null) return false;
    process.destroy();
    mapProcessesNotDestroyed.remove(sKeyProcess);
    return true;
  }
  
  public
  Hashtable env()
    throws Exception
  {
    return System.getProperties();
  }
  
  public
  String env(String sKey)
    throws Exception
  {
    return System.getProperty(sKey, "");
  }
  
  public
  String env(String sKey, String sValue)
    throws Exception
  {
    System.setProperty(sKey, sValue);
    return sValue;
  }
  
  public
  boolean delete(String sFile)
    throws Exception
  {
    return delete(getFile(sFile, false, false, false));
  }
  
  public
  boolean mkdir(String sDirectory, String sSubDirectoryName)
    throws Exception
  {
    File fDirectory    = getFile(sDirectory, false, true, false);
    String sNewDirPath = fDirectory.getAbsolutePath() + File.separator + decrypt(sSubDirectoryName);
    File file = new File(sNewDirPath);
    if(file.exists()) {
      throw new Exception("FM#Path " + sNewDirPath + " already exist.");
    }
    return file.mkdir();
  }
  
  public
  boolean mkdirs(String sPath)
    throws Exception
  {
    File file = getFile(sPath, false, false, true);
    return file.exists();
  }
  
  public
  boolean touch(String sFile)
    throws Exception
  {
    File file = getFile(sFile, true, false, false);
    return file.setLastModified(System.currentTimeMillis());
  }
  
  public
  boolean rename(String sFile, String sNewName)
    throws Exception
  {
    File file = getFile(sFile, false, false, false);
    sNewName = decrypt(sNewName);
    if(file.getName().equalsIgnoreCase(sNewName)) return true;
    String sFolder = getFolder(file.getAbsolutePath());
    if(sFolder == null || sFolder.length() == 0) return false;
    return file.renameTo(new File(sFolder + File.separator + sNewName));
  }
  
  public
  boolean move(String sFile, String sDirectory)
    throws Exception
  {
    File file       = getFile(sFile, true, false, false);
    File fDirectory = getFile(sDirectory, false, true, false);
    String sPathDirectory = fDirectory.getAbsolutePath();
    String sNewFilePath   = null;
    char cLast = sPathDirectory.charAt(sPathDirectory.length() - 1);
    if(cLast == '/' || cLast == '\\') {
      sNewFilePath = sPathDirectory + file.getName();
    }
    else {
      sNewFilePath = sPathDirectory + File.separator + file.getName();
    }
    boolean boResult = file.renameTo(new File(sNewFilePath));
    if(!boResult) {
      // Nel caso in cui la directory si trova in un volume diverso
      // il rename potrebbe restituire false. In tal caso si procede
      // alla copia del file e alla successiva cancellazione.
      boResult = copy(sFile, sDirectory);
      if(boResult) delete(sFile);
    }
    return boResult;
  }
  
  public
  boolean copy(String sFile, String sDirectory)
    throws Exception
  {
    File file       = getFile(sFile, true, false, false);
    File fDirectory = getFile(sDirectory, false, true, false);
    String sPathDirectory = fDirectory.getAbsolutePath();
    String sCopyFilePath  = null;
    char cLast = sPathDirectory.charAt(sPathDirectory.length() - 1);
    if(cLast == '/' || cLast == '\\') {
      sCopyFilePath = sPathDirectory + file.getName();
    }
    else {
      sCopyFilePath = sPathDirectory + File.separator + file.getName();
    }
    sFile = decrypt(sFile);
    if(sCopyFilePath.equals(sFile)) {
      sCopyFilePath = sCopyFilePath + ".copy";
    }
    BufferedInputStream  bis = null;
    BufferedOutputStream bos = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(file));
      bos = new BufferedOutputStream(new FileOutputStream(sCopyFilePath, false));
      byte[] buffer = new byte[4096];
      int bytesRead = 0;
      while((bytesRead = bis.read(buffer)) != -1) // read
      bos.write(buffer, 0, bytesRead); // write
    }
    finally {
      if(bos != null) try{ bos.close(); } catch(Exception ex) {}
      if(bis != null) try{ bis.close(); } catch(Exception ex) {}
    }
    return true;
  }
  
  public
  byte[] getContent(String sFile, int iPart, int iBlock)
    throws Exception
  {
    if(iPart <= 0) return new byte[0];
    File file = getFile(sFile, true, false, false);
    long lPart      = (long) iPart;
    long lBlock     = (long) iBlock;
    long lLength    = file.length();
     long lRemainder = (lPart * lBlock) - lLength;
    if(lRemainder > lBlock) return new byte[0];
    if(lRemainder > 0 && lRemainder < lBlock) {
      // Se viene richiesta l'ultima parte ed essa e' di lunghezza
      // inferiore al blocco si ridimensiona il blocco da leggere.
      iBlock = (int)(lBlock - lRemainder);
    }
    byte[] abResult = new byte[iBlock];
     long lPosition  = (lPart - 1l) * lBlock;
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(file, "r");
      raf.seek(lPosition);
      raf.readFully(abResult);
    }
    finally {
      if(raf != null) try{ raf.close(); } catch(Exception ex) {}
    }
    return abResult;
  }
  
  public
  String startUpload(String sFile)
    throws Exception
  {
    String sTmpFile = decrypt(sFile) + sTMP_EXT;
    File fTmpFile = new File(sTmpFile);
    if(fTmpFile.exists()) {
      fTmpFile.delete();
    }
    return sTmpFile;
  }
  
  public
  String startUpload(String sDirectory, String sFileName, boolean boMakeDirs)
    throws Exception
  {
    File fDirectory = getFile(sDirectory, false, true, boMakeDirs);
    String sFile    = fDirectory.getAbsolutePath() + File.separator + decrypt(sFileName);
    String sTmpFile = sFile + sTMP_EXT;
    File fTmpFile = new File(sTmpFile);
    if(fTmpFile.exists()) {
      fTmpFile.delete();
    }
    return sTmpFile;
  }
  
  public
  boolean endUpload(String sTmpFile, String sMD5)
    throws Exception
  {
    if(sTmpFile == null || sTmpFile.length() == 0) return false;
    File fileTmp = getFile(sTmpFile, true, false, false);
    sTmpFile = fileTmp.getAbsolutePath();
    // Controllo MD5
    if(sMD5 != null && sMD5.length() > 0) {
      MessageDigest md = MessageDigest.getInstance("MD5");
      FileInputStream fis = new FileInputStream(fileTmp);
      int iBytesReaded = 0;
      byte[] abBuffer = new byte[1024];
      while((iBytesReaded = fis.read(abBuffer)) > 0) {
        md.update(abBuffer, 0, iBytesReaded);
      }
      fis.close();
      String sMD5TmpFile = Base64Coder.encodeLines(md.digest());
      sMD5TmpFile = sMD5TmpFile != null ? sMD5TmpFile.trim() : "";
      if(!sMD5TmpFile.equals(sMD5)) return false;
    }
    // Si ottiene il nome vero del file
    String sFile = null;
    if(sTmpFile.endsWith(sTMP_EXT)) {
      sFile = sTmpFile.substring(0, sTmpFile.length() - sTMP_EXT.length());
    }
    else {
      sFile = sTmpFile;
    }
    File file = new File(sFile);
    // Se il file esiste viene cancellato
    if(file.exists()) {
      if(!file.delete()) return false;
    }
    // Si rinomina il file temporaneo
    return fileTmp.renameTo(file);
  }
  
  public
  boolean appendContent(String sFile, byte[] abContent)
    throws Exception
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(decrypt(sFile), true);
      fos.write(abContent);
    }
    finally {
      if(fos != null) try { fos.close(); } catch(Exception ex) {}
    }
    return true;
  }
  
  public
  boolean appendContent(String sDirectory, String sFileName, byte[] abContent)
    throws Exception
  {
    File fDirectory = getFile(sDirectory, false, true, false);
    String sFile = fDirectory.getAbsolutePath() + File.separator + decrypt(sFileName);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(sFile, true);
      fos.write(abContent);
    }
    finally {
      if(fos != null) try { fos.close(); } catch(Exception ex) {}
    }
    return true;
  }
  
  public
  String getTextContent(String sFile)
    throws Exception
  {
    File file = getFile(sFile, true, false, false);
    StringBuffer sbResult = new StringBuffer();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        sbResult.append(normalize(sLine));
        sbResult.append('\n');
      }
    }
    finally {
      if(br  != null) try{ br.close(); } catch(Exception ex) {}
    }
    return sbResult.toString();
  }
  
  public
  String head(String sFile, int iRows)
    throws Exception
  {
    if(iRows <= 0) return "";
    File file = getFile(sFile, true, false, false);
    int iRow = 0;
    StringBuffer sbResult = new StringBuffer();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        sbResult.append(normalize(sLine));
        sbResult.append('\n');
        iRow++;
        if(iRow >= iRows) break;
      }
    }
    finally {
      if(br  != null) try{ br.close(); } catch(Exception ex) {}
    }
    return sbResult.toString();
  }
  
  public
  String tail(String sFile, int iRows)
    throws Exception
  {
    if(iRows <= 0) return "";
    File file = getFile(sFile, true, false, false);
    String[] asRows = new String[iRows];
    int i = 0;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        if(i == iRows) {
          for(int j = 1; j < iRows; j++) {
            asRows[j - 1] = asRows[j];
          }
          i--;
        }
        asRows[i++] = sLine;
      }
    }
    finally {
      if(br  != null) try{ br.close(); } catch(Exception ex) {}
    }
    StringBuffer sbResult = new StringBuffer();
    for(int j = 0; j < iRows; j++) {
      String sRow = asRows[j];
      sbResult.append(normalize(sRow));
      sbResult.append('\n');
      if(sRow == null) break;
    }
    return sbResult.toString();
  }
  
  public
  Vector find(String sFile, String sText, int iMaxResults)
    throws Exception
  {
    Vector vResult = new Vector();
    File file = getFile(sFile, true, false, false);
    String sTextLC = sText.toLowerCase();
    int iRow = 0;
    int iResults = 0;
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        iRow++;
        String sLineLC = sLine.toLowerCase();
        if(sLineLC.indexOf(sTextLC) >= 0) {
          Hashtable ht = new Hashtable();
          ht.put("r", new Integer(iRow));
          ht.put("t", normalize(sLine));
          vResult.add(ht);
          iResults++;
          if(iResults >= iMaxResults) break;
        }
      }
    }
    finally {
      if(br  != null) try{ br.close(); } catch(Exception ex) {}
    }
    return vResult;
  }
  
  private static
  File getFile(String sPath, boolean boCheckIsFile, boolean boCheckIsDirectory, boolean boMakeDirs)
    throws Exception
  {
    if(sPath == null || sPath.length() == 0) {
      throw new Exception("FM#Invalid path.");
    }
    sPath = decrypt(sPath);
    if(sPath.equalsIgnoreCase("user.home")) sPath = "";
    boolean boAbsolute = false;
    if(sPath.length() > 0) {
      char c0 = sPath.charAt(0);
      boAbsolute = c0 == '/' || c0 == '\\';
      if(!boAbsolute && sPath.length() > 1) {
        boAbsolute = sPath.charAt(1) == ':';
      }
    }
    if(!boAbsolute) {
      sPath = System.getProperty("user.home") + File.separator + sPath;
    }
    File file = new File(sPath);
    if(boMakeDirs) {
      if(!file.exists()) {
        if(file.mkdirs()) {
          return file;
        }
        else {
          throw new Exception("FM#Can't make dirs for " + sPath);
        }
      }
    }
    if(!boMakeDirs && !file.exists()) {
      throw new Exception("FM#Path not exist.");
    }
    if(boCheckIsFile && !file.isFile()) {
      throw new Exception("FM#Path is not a file.");
    }
    if(boCheckIsDirectory && !file.isDirectory()) {
      throw new Exception("FM#Path is not a directory.");
    }
    return file;
  }
  
  private static
  boolean delete(File file)
    throws Exception
  {
    if(file == null) return false;
    if(file.getAbsolutePath().length() < 6) return false;
    boolean boResult = true;
    File[] arrayOfFile = file.listFiles();
    if(arrayOfFile != null) {
      for(int i = 0; i < arrayOfFile.length; i++) {
        File fileToDelete = arrayOfFile[i];
        if(fileToDelete.isDirectory()) {
          boResult = boResult && delete(fileToDelete);
        }
        else {
          boResult = boResult && fileToDelete.delete();
        }
      }
    }
    return boResult && file.delete();
  }
  
  private static
  String getFolder(String sFilePath)
  {
    if(sFilePath == null) return "";
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
  
  private static
  String normalize(String sText)
  {
    if(sText == null) return "";
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < sText.length(); i++) {
      char c = sText.charAt(i);
      if(c == 9 || c == 10 || c == 13) sb.append(c); else
      if(c >= 32 && c <= 255) sb.append(c);
      else sb.append('?');
    }
    return sb.toString();
  }
  
  private static
  String decrypt(String sText)
  {
    if(sText == null) return null;
    String sKey = FMUtils.sENCRYPTION_KEY;
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
        r = r + d;
        if(r > 126) {
          r = 31 + r - 126;
        }
      }
      sb.append((char) r);
    }
    return sb.toString();
  }
  
  public static String getTimeStamp() {
    Calendar cal   = Calendar.getInstance();
    int iYear      = cal.get(java.util.Calendar.YEAR);
    int iMonth     = cal.get(java.util.Calendar.MONTH) + 1;
    int iDay       = cal.get(java.util.Calendar.DATE);
    String sDay    = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    String sMonth  = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    int iHour      = cal.get(Calendar.HOUR_OF_DAY);
    int iMinute    = cal.get(Calendar.MINUTE);
    int iSecond    = cal.get(Calendar.SECOND);
    String sHour   = iHour   < 10 ? "0" + iHour   : String.valueOf(iHour);
    String sMinute = iMinute < 10 ? "0" + iMinute : String.valueOf(iMinute);
    String sSecond = iSecond < 10 ? "0" + iSecond : String.valueOf(iSecond);
    return iYear + sMonth + sDay + "_" + sHour + sMinute + sSecond;
  }
  
  class FMFilenameFilter implements FilenameFilter
  {
    protected String sFilter;
    protected boolean boNoHidden = false;
    public FMFilenameFilter(String sTheFilter)
    {
      if(sTheFilter == null || sTheFilter.length() == 0) {
        this.sFilter = "*.*";
      }
      else {
        this.sFilter = sTheFilter.toLowerCase();
      }
      if(sFilter.endsWith("&!h") && sFilter.length() > 3) {
        sFilter = sFilter.substring(0, sFilter.length() - 3);
        boNoHidden = true;
      }
      else
      if(sFilter.equals("!h")) {
        boNoHidden = true;
        sFilter = "*.*";
      }
    }
    public boolean accept(File dir, String name) {
      if(boNoHidden && name.startsWith(".")) return false;
      if(sFilter.equals("*.*") || sFilter.equals("**") || sFilter.equals("*")) return true;
      char c0 = sFilter.charAt(0);
      char cL = sFilter.charAt(sFilter.length()-1);
      name = name.toLowerCase();
      if(c0 == '*' && cL == '*') {
        return name.indexOf(sFilter.substring(1, sFilter.length()-1)) >= 0;
      }
      else
      if(c0 == '*') {
        return name.endsWith(sFilter.substring(1));
      }
      else
      if(cL == '*') {
        return name.startsWith(sFilter.substring(0, sFilter.length()-1));
      }
      return name.equals(sFilter);
    }
  }
}
