package org.dew.swingup.log;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * Implementazione di ILogger che traccia le informazioni su due file di cui
 * uno destinato a tracciare le informazioni attraverso i metodi error.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class DoubleFileLogger implements ILogger
{
  private PrintWriter outStream_d;
  private FileOutputStream oFile_d;
  private PrintWriter outStream_e;
  private FileOutputStream oFile_e;
  private boolean boDebug = false;
  private DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
  
  public
  DoubleFileLogger()
  {
  }
  
  public
  DoubleFileLogger(String sFileName, boolean boDebug)
  {
    init(sFileName, boDebug);
  }
  
  public
  void init(String sLog, boolean boDebug)
  {
    this.boDebug = boDebug;
    String sUserHome = System.getProperty("user.home");
    String sFilePath = sUserHome + File.separator + sLog;
    String sErroFilePath = sFilePath + ".err";
    
    try {
      oFile_d = new FileOutputStream(sFilePath, false);
      outStream_d = new PrintWriter(oFile_d);
      
      oFile_e = new FileOutputStream(sErroFilePath, false);
      outStream_e = new PrintWriter(oFile_e);
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
    if(outStream_d == null) return;
    outStream_d.println(getLogPrefix() + sText);
    outStream_d.flush();
  }
  
  public
  void info(String sText, Throwable throwable)
  {
    if(outStream_d == null) return;
    outStream_d.print(getLogPrefix() + sText + " ");
    throwable.printStackTrace(outStream_d);
    outStream_d.flush();
  }
  
  public
  void debug(String sText)
  {
    if(!boDebug) return;
    if(outStream_d == null) return;
    outStream_d.println(getLogPrefix() + sText);
    outStream_d.flush();
  }
  
  public
  void debug(String sText, Throwable throwable)
  {
    if(!boDebug) return;
    if(outStream_d == null) return;
    outStream_d.print(getLogPrefix() + sText + " ");
    throwable.printStackTrace(outStream_d);
    outStream_d.flush();
  }
  
  public
  void error(String sText)
  {
    if(outStream_e == null) return;
    outStream_e.println(getLogPrefix() + sText);
    outStream_e.flush();
  }
  
  public
  void error(String sText, Throwable throwable)
  {
    if(outStream_e == null) return;
    outStream_e.print(getLogPrefix() + sText + " ");
    throwable.printStackTrace(outStream_e);
    outStream_e.flush();
  }
  
  protected
  void finalize()
  {
    try {
      if(outStream_d != null) {
        oFile_d.close();
      }
      if(outStream_e != null) {
        oFile_e.close();
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
