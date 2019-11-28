package org.dew.swingup.file;

import java.io.*;
import java.net.*;
import java.util.*;

import org.dew.swingup.ResourcesMgr;

/**
 * Questa classe consente di copiare un file con la possibilita' di sostituire
 * il contenuto del file dinamicamente.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class CopyFile
{
  public static final String BEGIN_PARAM = "|[";
  public static final String END_PARAM   = "]|";
  private PrintStream out;
  private Hashtable htContentObjects = new Hashtable();
  private StringBuffer fileBuffer;
  private String sOutputFileName;
  
  /**
   * Costruttore di default.
   *
   * @throws Exception
   */
  public
  CopyFile()
    throws Exception
  {
  }
  
  /**
   * Costruttore con file di output.
   *
   * @param sOutputFileName Nome del file di output.
   * @throws Exception
   */
  public
  CopyFile(String sOutputFileName)
    throws Exception
  {
    this.sOutputFileName = sOutputFileName;
  }
  
  /**
   * Imposta il file di output.
   *
   * @param sOutputFileName Nome del file di output.
   */
  public
  void setOutputFileName(String sOutputFileName)
  {
    this.sOutputFileName = sOutputFileName;
  }
  
  /**
   * Ottiene il nome del file di output.
   *
   * @return String
   */
  public
  String getOutputFileName()
  {
    return sOutputFileName;
  }
  
  /**
   * Carica un ContentObject nel buffer.
   *
   * @param contentObject Contenuto
   * @throws Exception
   */
  public
  void loadContent(ContentObject contentObject)
    throws Exception
  {
    if(contentObject == null) {
      return;
    }
    fileBuffer = contentObject.getStringBuffer();
    if(!htContentObjects.isEmpty()) {
      Enumeration enumKeys = htContentObjects.keys();
      while(enumKeys.hasMoreElements()) {
        replaceContentInFileBuffer(enumKeys.nextElement().toString());
      }
    }
  }
  
  /**
   * Carica un testo nel buffer.
   *
   * @param content Contenuto
   * @throws Exception
   */
  public
  void loadContent(String content)
    throws Exception
  {
    if(content == null) {
      return;
    }
    fileBuffer = new StringBuffer(content);
    if(!htContentObjects.isEmpty()) {
      Enumeration enumKeys = htContentObjects.keys();
      while(enumKeys.hasMoreElements()) {
        replaceContentInFileBuffer(enumKeys.nextElement().toString());
      }
    }
  }
  
  /**
   * Carica un file nel buffer.
   *
   * @param sFileName Nome del file.
   * @throws Exception
   */
  public
  void loadFile(String sFileName)
    throws Exception
  {
    if(sFileName == null) {
      return;
    }
    int b;
    FileInputStream fileInputStream = new FileInputStream(sFileName);
    fileBuffer = new StringBuffer();
    while((b = fileInputStream.read()) != -1) {
      fileBuffer.append((char) b);
      
    }
    fileInputStream.close();
    
    if(!htContentObjects.isEmpty()) {
      Enumeration enumKeys = htContentObjects.keys();
      while(enumKeys.hasMoreElements()) {
        replaceContentInFileBuffer(enumKeys.nextElement().toString());
      }
    }
  }
  
  /**
   * Carica un file nel buffer.
   *
   * @param urlFile URL del file
   * @throws Exception
   */
  public
  void loadFile(URL urlFile)
    throws Exception
  {
    if(urlFile == null) {
      return;
    }
    int b;
    InputStream is = urlFile.openStream();
    fileBuffer = new StringBuffer();
    while((b = is.read()) != -1) {
      fileBuffer.append((char) b);
    }
    is.close();
    if(!htContentObjects.isEmpty()) {
      Enumeration enumKeys = htContentObjects.keys();
      while(enumKeys.hasMoreElements()) {
        replaceContentInFileBuffer(enumKeys.nextElement().toString());
      }
    }
  }
  
  /**
   * Rimuove un contenuto associato alla chiave specificata.
   *
   * @param name String
   */
  public
  void remove(String name)
  {
    if(name == null) {
      return;
    }
    htContentObjects.remove(name);
  }
  
  /**
   * Rimuove tutti i contenuti di sostituzione.
   */
  public
  void removeAll()
  {
    htContentObjects.clear();
  }
  
  /**
   * Imposta i contenuti di sostituzione.
   *
   * @param mapContents Map
   */
  public
  void setContents(Map mapContents)
  {
    if(mapContents == null) {
      return;
    }
    htContentObjects.putAll(mapContents);
    if(fileBuffer != null) {
      Iterator oItKeys = mapContents.keySet().iterator();
      while(oItKeys.hasNext()) {
        String sKey = oItKeys.next().toString();
        replaceContentInFileBuffer(sKey);
      }
    }
  }
  
  /**
   * Imposta un contenuto di sostituzione.
   *
   * @param name Chiave del contenuto
   * @param contentObject Contenuto
   */
  public
  void setContent(String name, Object contentObject)
  {
    if(name == null) {
      return;
    }
    if(contentObject == null) {
      contentObject = "";
    }
    htContentObjects.put(name, contentObject);
    if(fileBuffer != null) {
      replaceContentInFileBuffer(name);
    }
  }
  
  /**
   * Copia il contenuto del buffer nel file di output.
   *
   * @throws Exception
   */
  public
  void copy()
    throws Exception
  {
    if(sOutputFileName == null) {
      throw new Exception("Output file undefined.");
    }
    if(fileBuffer == null) {
      return;
    }
    
    out = getPrintStream(sOutputFileName, false);
    out.print(fileBuffer.toString());
    out.flush();
    out.close();
  }
  
  /**
   * Copia il contenuto dal file specificato dal parametro.
   *
   * @param sSourceFileName Nome del file sorgente.
   * @throws Exception
   */
  public
  void copy(String sSourceFileName)
    throws Exception
  {
    if(sOutputFileName == null) {
      throw new Exception("Output file undefined.");
    }
    if(sSourceFileName == null) {
      return;
    }
    
    out = getPrintStream(sOutputFileName, false);
    copy(new FileInputStream(sSourceFileName));
    out.close();
  }
  
  /**
   * Copia il contenuto dal file specificato dal parametro.
   *
   * @param urlSourceFile URL del file sorgente.
   * @throws Exception
   */
  public
  void copy(URL urlSourceFile)
    throws Exception
  {
    if(sOutputFileName == null) {
      throw new Exception("Output file undefined.");
    }
    if(urlSourceFile == null) {
      return;
    }
    
    out = getPrintStream(sOutputFileName, false);
    copy(urlSourceFile.openStream());
    out.close();
  }
  
  /**
   * Appende il contenuto definito nel parametro contentObject.
   *
   * @param contentObject Object
   * @throws Exception
   */
  public
  void appendContent(Object contentObject)
    throws Exception
  {
    if(contentObject == null) return;
    out = getPrintStream(sOutputFileName, true);
    out.print(contentObject.toString());
    out.close();
  }
  
  /**
   * Copia una risorsa nel percorso specificato.
   *
   * @param sResource Risorsa
   * @param sFolder Cartella
   * @param boSkipIfExists Flag che evita la copia se il file esiste.
   * @throws Exception
   * @return Percorso completo del file. Null se la risorsa non esiste.
   */
  public static
  String copyResourceInFolder(String sResource,
    String sFolder,
    boolean boSkipIfExists)
    throws Exception
  {
    if(sFolder == null) return null;
    
    URL urlResource = ResourcesMgr.getURLResource(sResource);
    if(urlResource == null) return null;
    
    String sResourceName = getResourceName(sResource);
    String sFilePath = sFolder + File.separator + sResourceName;
    File file = new File(sFilePath);
    if(boSkipIfExists && file.exists()) {
      return sFilePath;
    }
    
    FileOutputStream fos = null;
    BufferedInputStream bis = null;
    try {
      bis = new BufferedInputStream(urlResource.openStream());
      fos = new FileOutputStream(file);
      int iByte = 0;
      while((iByte = bis.read()) != -1) {
        fos.write(iByte);
      }
      bis.close();
      fos.close();
    }
    catch(Exception ex) {
      if(boSkipIfExists) file.delete();
      throw ex;
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {};
      if(bis != null) try{ bis.close(); } catch(Exception ex) {};
    }
    
    return sFilePath;
  }
  
  /**
   * Copia una risorsa in un percorso che parte dalla cartella home.
   * Se il percorso non esiste tenta di crearlo.
   *
   * @param sResource Risorsa
   * @param sFolder Cartella
   * @param boSkipIfExists Flag che evita la copia se il file esiste.
   * @throws Exception
   * @return Percorso completo del file. Null se la risorsa non esiste.
   */
  public static
  String copyResourceInHome(String sResource,
    String sFolder,
    boolean boSkipIfExists)
    throws Exception
  {
    URL urlResource = ResourcesMgr.getURLResource(sResource);
    if(urlResource == null) return null;
    
    String sResourceName = getResourceName(sResource);
    String sFilePath = null;
    if(sFolder != null) {
      String sOutputFolder = System.getProperty("user.home") +
        File.separator + sFolder;
      File folder = new File(sOutputFolder);
      if(!folder.exists()) {
        folder.mkdirs();
      }
      sFilePath = sOutputFolder + File.separator + sResourceName;
    }
    else {
      sFilePath = System.getProperty("user.home") +
        File.separator + sResourceName;
    }
    File file = new File(sFilePath);
    if(boSkipIfExists && file.exists()) {
      return sFilePath;
    }
    
    FileOutputStream fos = null;
    BufferedInputStream bis = null;
    try {
      bis = new BufferedInputStream(urlResource.openStream());
      fos = new FileOutputStream(file);
      int iByte = 0;
      while((iByte = bis.read()) != -1) {
        fos.write(iByte);
      }
      bis.close();
      fos.close();
    }
    catch(Exception ex) {
      if(boSkipIfExists) file.delete();
      throw ex;
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {};
      if(bis != null) try{ bis.close(); } catch(Exception ex) {};
    }
    
    return sFilePath;
  }
  
  /**
   * Visualizza un file presente nel class-path tramite applicazione registrata
   * nel sistema operativo.
   * Attualmente funziona solo su sistemi operativi Windows XP.
   *
   * @param sResource Risorsa
   * @throws Exception
   */
  public static
  void viewResource(String sResource)
    throws Exception
  {
    String sFilePath = copyResourceInHome(sResource, null, false);
    
    if(sFilePath != null) {
      view(sFilePath);
    }
    else {
      throw new Exception("Resource " + sResource + " not found.");
    }
  }
  
  /**
   * Visualizza un file presente nel class-path tramite applicazione registrata
   * nel sistema operativo.
   * Attualmente funziona solo su sistemi operativi Windows XP.
   *
   * @param sResource String
   * @param sTempFolder Cartella temporanea.
   * @throws Exception
   */
  public static
  void viewResource(String sResource, String sTempFolder)
    throws Exception
  {
    String sFilePath = copyResourceInHome(sResource, sTempFolder, false);
    
    if(sFilePath != null) {
      view(sFilePath);
    }
    else {
      throw new Exception("Resource " + sResource + " not found.");
    }
  }
  
  /**
   * Visualizza il file tramite applicazione registrata nel sistema operativo.
   * Attualmente funziona solo su sistemi operativi Windows XP.
   *
   * @param sFileName String
   * @throws Exception
   */
  public static
  void view(String sFileName)
    throws Exception
  {
    String sCommand = null;
    
    String sOSName = System.getProperty("os.name", "WIN");
    if(sOSName.toUpperCase().indexOf("WIN") >= 0) {
      sCommand = DataFileUtil.getWINViewer(sFileName);
    }
    else
    if(sOSName.toUpperCase().indexOf("LINUX") >= 0) {
      sCommand = DataFileUtil.getLinuxViewer(sFileName);
    }
    else {
      throw new Exception("Operation in " + sOSName + " not supported.");
    }
    
    if(sCommand == null) {
      throw new Exception("Command undefined.");
    }
    
    System.out.println(ResourcesMgr.sLOG_PREFIX + " exec(" + sCommand + ")");
    Runtime.getRuntime().exec(sCommand);
  }
  
  private
  void copy(InputStream in)
    throws Exception
  {
    int b;
    char c1;
    char c2;
    for(b = in.read(); b != -1; b = in.read()) {
      c1 = (char) b;
      if(c1 != BEGIN_PARAM.charAt(0)) {
        out.write(c1);
      }
      else {
        b = in.read();
        if(b != -1) {
          c2 = (char) b;
          if(c2 == BEGIN_PARAM.charAt(1)) {
            out.print(getContentObject(in).toString());
          }
          else {
            out.write(c1);
            out.write(c2);
          }
        }
      }
    }
    out.flush();
    in.close();
  }
  
  private
  Object getContentObject(InputStream in)
    throws IOException
  {
    int b;
    char c = '\0';
    StringBuffer nameContentObject = new StringBuffer();
    
    for(b = in.read(); b != -1; b = in.read()) {
      c = (char) b;
      if(c == END_PARAM.charAt(0)) {
        b = in.read();
        if(b != -1) {
          c = (char) b;
          if(c == END_PARAM.charAt(1)) {
            Object contentObject = htContentObjects.get(nameContentObject.
            toString());
            if(contentObject == null) {
              return "";
            }
            return contentObject;
          }
          else {
            nameContentObject.append(c);
          }
        }
      }
      else {
        nameContentObject.append(c);
      }
    }
    
    out.print(nameContentObject.toString());
    
    return "";
  }
  
  private
  void replaceContentInFileBuffer(String nameContentObject)
  {
    if(fileBuffer == null) {
      return;
    }
    String pattern = BEGIN_PARAM + nameContentObject + END_PARAM;
    int i = indexOf(fileBuffer, pattern, 0);
    while(i >= 0) {
      String content = htContentObjects.get(nameContentObject).toString();
      
      fileBuffer.delete(i, i + pattern.length());
      fileBuffer.insert(i, content);
      
      i = indexOf(fileBuffer, pattern, i + content.length());
    }
  }
  
  private
  int indexOf(StringBuffer sb, String pattern, int beginIndex)
  {
    if((beginIndex + pattern.length()) > sb.length()) {
      return -1;
    }
    char c = pattern.charAt(0);
    for(int i = beginIndex; i < sb.length(); i++) {
      if(sb.charAt(i) == c) {
        if(match(sb, i, pattern)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  private
  boolean match(StringBuffer sb, int beginIndex, String pattern)
  {
    if((beginIndex + pattern.length()) > sb.length()) {
      return false;
    }
    int j = 0;
    for(int i = beginIndex; i < beginIndex + pattern.length(); i++) {
      if(sb.charAt(i) != pattern.charAt(j)) {
        return false;
      }
      j++;
    }
    return true;
  }
  
  private static
  PrintStream getPrintStream(String sFileName, boolean boAppend)
    throws Exception
  {
    if(sFileName != null) {
      FileOutputStream fileoutputstream = new FileOutputStream(sFileName, boAppend);
      return new PrintStream(fileoutputstream, true);
    }
    else {
      return System.out;
    }
  }
  
  private static
  String getResourceName(String sResource)
  {
    if(sResource == null) return null;
    int iBegin = -1;
    for(int i = sResource.length() - 1; i >= 0; i--) {
      char c = sResource.charAt(i);
      if(c == '\\' || c == '/') {
        iBegin = i;
        break;
      }
    }
    if(iBegin >= sResource.length() - 1) {
      return "";
    }
    return sResource.substring(iBegin + 1);
  }
}
