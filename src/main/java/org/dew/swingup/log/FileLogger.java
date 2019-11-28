package org.dew.swingup.log;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * Implementazione di ILogger che traccia le informazioni su un file.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class FileLogger implements ILogger
{
  private PrintWriter outStream;
  private FileOutputStream oFile;
  private boolean boDebug = false;
  private DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
  
  public
  FileLogger()
  {
  }
  
  public
  FileLogger(String sFileName, boolean boDebug)
  {
    init(sFileName, boDebug);
  }
  
  public
  void init(String sLog, boolean boDebug)
  {
    this.boDebug = boDebug;
    String sUserHome = System.getProperty("user.home");
    String sFilePath = sUserHome + File.separator + sLog;
    try {
      oFile = new FileOutputStream(sFilePath, false);
      outStream = new PrintWriter(oFile);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public
  void setDebug(boolean boDebug)
  {
    this.boDebug = boDebug;
  }
  
  public
  void info(String sText)
  {
    if(outStream == null) return;
    outStream.println(getLogPrefix() + sText);
    outStream.flush();
  }
  
  public
  void info(String sText, Throwable throwable)
  {
    if(outStream == null) return;
    outStream.print(getLogPrefix() + sText + " ");
    throwable.printStackTrace(outStream);
    outStream.flush();
  }
  
  public
  void debug(String sText)
  {
    if(!boDebug) return;
    if(outStream == null) return;
    outStream.println(getLogPrefix() + sText);
    outStream.flush();
  }
  
  public
  void debug(String sText, Throwable throwable)
  {
    if(!boDebug) return;
    if(outStream == null) return;
    outStream.print(getLogPrefix() + sText + " ");
    throwable.printStackTrace(outStream);
    outStream.flush();
  }
  
  public
  void error(String sText)
  {
    if(outStream == null) return;
    outStream.println(getLogPrefix() + sText);
    outStream.flush();
  }
  
  public
  void error(String sText, Throwable throwable)
  {
    if(outStream == null) return;
    outStream.print(getLogPrefix() + sText + " ");
    throwable.printStackTrace(outStream);
    outStream.flush();
  }
  
  protected
  void finalize()
  {
    try {
      if(outStream != null) {
        oFile.close();
      }
    }
    catch(IOException ex) {
    }
  }
  
  private
  String getLogPrefix()
  {
    String sResult = null;
    String sMethodAndRow;
    try {
      try {
        throw new Throwable();
      }
      catch(Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        t.printStackTrace(pw);
        pw.close();
        sMethodAndRow = getMethodAndRow(baos.toByteArray(), 3);
        sResult = df.format(new Date()) + " " + sMethodAndRow + " ";
      }
    }
    catch(Throwable t) {
      sResult = "";
    }
    
    return sResult;
  }
  
  private
  String getMethodAndRow(byte[] aBytes, int iDepth)
  {
    StringBuffer sb = new StringBuffer();
    
    int iCountCRLF = 0;
    boolean boTraceOn = false;
    for(int i = 0; i < aBytes.length; i++) {
      byte b = aBytes[i];
      if(b == '\n') {
        iCountCRLF++;
      }
      
      if(iCountCRLF == iDepth) {
        if(b == ' ') {
          boTraceOn = true;
          continue;
        }
        else
        if(b == ':') {
          boTraceOn = true;
          continue;
        }
        else
        if(b == '(') {
          boTraceOn = false;
          sb.append(':');
          continue;
        }
        else
        if(b == ')') {
          break;
        }
        
        if(boTraceOn) {
          sb.append((char) b);
        }
      }
    }
    
    return sb.toString();
  }
}
