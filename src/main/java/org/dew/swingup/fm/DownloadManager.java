package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.rpc.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DownloadManager implements Runnable
{
  protected FMViewer fmViewer;
  protected IRPCClient oRPCClient;
  protected List listOfFMEntry;
  protected String sLocalFolder;
  protected int iBlock = 100 * 1024;
  protected boolean boViewAfterDownload = false;
  protected boolean boAskIfOverwrite    = true;
  
  protected List<ActionListener> listOfActionListener = new ArrayList<ActionListener>();
  
  public
  DownloadManager(FMViewer fmViewer, List listOfFMEntry)
  {
    this.fmViewer = fmViewer;
    this.listOfFMEntry = listOfFMEntry;
  }
  
  public
  DownloadManager(IRPCClient oRPCClient, List listOfFMEntry)
  {
    this.oRPCClient = oRPCClient;
    this.listOfFMEntry = listOfFMEntry;
  }
  
  public
  DownloadManager(FMViewer fmViewer, List listOfFMEntry, boolean boViewAfterDownload)
  {
    this.fmViewer = fmViewer;
    this.listOfFMEntry = listOfFMEntry;
    this.boViewAfterDownload = boViewAfterDownload;
  }
  
  public
  void addActionListener(ActionListener actionListener)
  {
    if(actionListener == null) return;
    listOfActionListener.add(actionListener);
  }
  
  public
  void removeActionListener(ActionListener actionListener)
  {
    if(actionListener == null) return;
    listOfActionListener.remove(actionListener);
  }
  
  public
  void setDefaultLocalFolder()
  {
    sLocalFolder = System.getProperty("user.home") + File.separator + ".cfadmin";
    File fLocalFolder = new File(sLocalFolder);
    if(!fLocalFolder.exists()) {
      fLocalFolder.mkdirs();
    }
  }
  
  public
  void setAskIfOverwrite(boolean boAskIfOverwrite)
  {
    this.boAskIfOverwrite = boAskIfOverwrite;
  }
  
  public
  void run()
  {
    if(listOfFMEntry == null || listOfFMEntry.size() == 0) return;
    if(oRPCClient == null && fmViewer == null) return;
    if(boViewAfterDownload) {
      if(sLocalFolder == null || sLocalFolder.length() == 0) {
        setDefaultLocalFolder();
      }
    }
    if(sLocalFolder == null || sLocalFolder.length() == 0) {
      JFileChooser jFileChooser = new JFileChooser();
      String sDownloadFolder = ResourcesMgr.dat.getProperty("download.folder");
      if(sDownloadFolder != null && sDownloadFolder.length() > 0) {
        File fileDownloadFolder = new File(sDownloadFolder);
        if(fileDownloadFolder.exists() && fileDownloadFolder.isDirectory()) {
          jFileChooser.setCurrentDirectory(fileDownloadFolder);
        }
      }
      jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int iResult = jFileChooser.showOpenDialog(ResourcesMgr.mainFrame);
      if(iResult != JFileChooser.APPROVE_OPTION) return;
      sLocalFolder = jFileChooser.getSelectedFile().getAbsolutePath();
      if(sLocalFolder == null || sLocalFolder.length() == 0) return;
      char cLast = sLocalFolder.charAt(sLocalFolder.length() - 1);
      if(cLast == '\\' || cLast == '/') {
        sLocalFolder = sLocalFolder.substring(0, sLocalFolder.length() - 1);
      }
      sDownloadFolder = FMUtils.getFolder(sLocalFolder);
      ResourcesMgr.dat.setProperty("download.folder", sDownloadFolder);
      ResourcesMgr.saveDat();
    }
    if(oRPCClient == null) oRPCClient = fmViewer.createRPCClient();
    
    FMEntry fmLastEntryDownloaded = null;
    DialogProgress dialogProgress = null;
    boolean boIsStopRequested = false;
    try {
      if(fmViewer != null) {
        if(FMUtils.parentDialog != null) {
          dialogProgress = new DialogProgress(FMUtils.parentDialog, "Download files", listOfFMEntry.size() > 1);
        }
        else {
          dialogProgress = new DialogProgress(ResourcesMgr.mainFrame, "Download files", listOfFMEntry.size() > 1);
        }
        dialogProgress.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            fmViewer.doRefresh();
          }
        });
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        dialogProgress.setLocation(sz.width/2 - dialogProgress.getSize().width/2, sz.height/2 - dialogProgress.getSize().height/2);
        dialogProgress.setVisible(true);
        dialogProgress.setClosable(false);
      }
      
      for(int i = 0; i < listOfFMEntry.size(); i++) {
        FMEntry fmEntry = (FMEntry) listOfFMEntry.get(i);
        if(fmEntry == null) continue;
        if(!fmEntry.isFile()) {
          if(dialogProgress != null) {
            dialogProgress.appendln("Download folder \"" + fmEntry.getPath() + "\" not supported.");
            dialogProgress.setValue(0);
          }
          else {
            System.out.println("Download folder \"" + fmEntry.getPath() + "\" not supported.");
          }
        }
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            break;
          }
        }
        download(fmEntry, dialogProgress);
        fmLastEntryDownloaded = fmEntry;
      }
      
      if(dialogProgress != null) {
        boIsStopRequested = dialogProgress.isStopRequested();
        if(listOfFMEntry.size() > 1) {
          if(boIsStopRequested) {
            dialogProgress.appendln("Download stopped.");
          }
          else {
            dialogProgress.appendln("Download completed.");
          }
        }
      }
      else {
        System.out.println("Download completed.");
      }
    }
    finally {
      if(dialogProgress != null) dialogProgress.setClosable(true);
      if(listOfFMEntry.size() == 1 && boViewAfterDownload && !boIsStopRequested) {
        if(fmLastEntryDownloaded != null) {
          String sLocalFilePath = sLocalFolder + File.separator + fmLastEntryDownloaded.getName().replace(' ', '_');
          try {
            if(dialogProgress != null) dialogProgress.doClose();
            ResourcesMgr.viewFile(sLocalFilePath);
          }
          catch(Exception ex) {
            if(FMUtils.parentDialog != null) {
              GUIMessage.showException(FMUtils.parentDialog, "Impossibile visualizzare il file scaricato", ex);
            }
            else {
              GUIMessage.showException("Impossibile visualizzare il file scaricato", ex);
            }
          }
        }
      }
      if(listOfFMEntry.size() == 1 && !boIsStopRequested && listOfActionListener != null && listOfActionListener.size() > 0) {
        String sLocalFilePath = sLocalFolder + File.separator + fmLastEntryDownloaded.getName().replace(' ', '_');
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sLocalFilePath);
        for(int i = 0; i < listOfActionListener.size(); i++) {
          ActionListener al = listOfActionListener.get(i);
          al.actionPerformed(e);
        }
      }
    }
  }
  
  private
  boolean download(FMEntry fmEntry, DialogProgress dialogProgress)
  {
    String sLocalFilePath = null;
    if(boViewAfterDownload) {
      sLocalFilePath = sLocalFolder + File.separator + fmEntry.getName().replace(' ', '_');
    }
    else {
      sLocalFilePath = sLocalFolder + File.separator + fmEntry.getName();
    }
    File file = new File(sLocalFilePath);
    if(file.exists() && !boViewAfterDownload && boAskIfOverwrite) {
      boolean boConfirm = false;
      if(dialogProgress != null) {
        boConfirm = GUIMessage.getConfirmation(dialogProgress, "Si vuole sovrascrivere il file " + fmEntry.getName() + "?");
      }
      else {
        boConfirm = GUIMessage.getConfirmation("Si vuole sovrascrivere il file " + fmEntry.getName() + "?");
      }
      if(!boConfirm) return true;
    }
    long lLength   = fmEntry.getLength();
    int iParts     = 1;
    int iRemainder = 0;
    if(lLength >= 0) {
      iParts     = (int)(lLength / iBlock);
      iRemainder = (int)(lLength % iBlock);
    }
    if(iRemainder > 0) iParts++;
    if(dialogProgress != null) {
      dialogProgress.append("Download \"" + fmEntry.getPath() + "\"...");
      dialogProgress.setValue(0);
    }
    else {
      System.out.print("Download \"" + fmEntry.getPath() + "\"...");
    }
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      vParameters.add(new Integer(1));
      vParameters.add(new Integer(iBlock));
      for(int iPart = 1; iPart <= iParts; iPart++) {
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            dialogProgress.appendln("stopped.");
            return false;
          }
        }
        vParameters.set(1, new Integer(iPart));
        byte[] arrayOfByte = (byte[]) oRPCClient.execute("FM.getContent", vParameters);
        fos.write(arrayOfByte);
         int iPercentage = iPart * 100 / iParts;
        if(dialogProgress != null) {
          dialogProgress.setValue(iPercentage);
        }
      }
      if(dialogProgress != null) {
        dialogProgress.setValue(100);
        dialogProgress.appendln("Ok");
      }
      else {
        System.out.println("Ok");
      }
    }
    catch(Exception ex) {
      if(dialogProgress != null) {
        dialogProgress.showException("Exception during download " + fmEntry.getName(), ex);
      }
      else {
        ex.printStackTrace();
      }
      return false;
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {}
    }
    return true;
  }
}
