package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;
import java.io.*;

import javax.swing.*;

import org.dew.rpc.util.Base64Coder;
import org.dew.swingup.*;
import org.dew.swingup.rpc.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class UploadManager implements Runnable
{
  protected FMViewer fmViewer;
  protected IRPCClient oRPCClient;
  protected DialogProgress dialogProgress;
  
  protected String sDirectory;
  protected int iBlock = 100 * 1024;
  protected boolean boDirectory;
  protected boolean boListOfFile;
  protected List listOfFile;
  
  protected File fUpload;
  protected String sNewName;
  protected List<ActionListener> listOfActionListener = new ArrayList<ActionListener>();
  
  public
  UploadManager(FMViewer fmViewer, String sDirectory, boolean boDirectory)
  {
    this.fmViewer    = fmViewer;
    this.sDirectory  = sDirectory;
    this.boDirectory = boDirectory;
  }
  
  public
  UploadManager(IRPCClient oRPCClient, String sDirectory, boolean boDirectory)
  {
    this.oRPCClient  = oRPCClient;
    this.sDirectory  = sDirectory;
    this.boDirectory = boDirectory;
  }
  
  public
  UploadManager(FMViewer fmViewer, String sDirectory, List listOfFile)
  {
    this.fmViewer     = fmViewer;
    this.sDirectory   = sDirectory;
    this.boListOfFile = true;
    this.listOfFile   = listOfFile;
  }
  
  public
  UploadManager(IRPCClient oRPCClient, String sDirectory, List listOfFile)
  {
    this.oRPCClient   = oRPCClient;
    this.sDirectory   = sDirectory;
    this.boListOfFile = true;
    this.listOfFile   = listOfFile;
  }
  
  public
  UploadManager(IRPCClient oRPCClient, String sDirectory, File theFileToUpload)
  {
    this.oRPCClient   = oRPCClient;
    this.sDirectory   = sDirectory;
    this.fUpload      = theFileToUpload;
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
  void setNewName(String sNewName)
  {
    this.sNewName = sNewName;
  }
  
  public
  void run()
  {
    if(boListOfFile) {
      uploadListOfFile();
    }
    else
    if(boDirectory) {
      uploadDirectory(null);
    }
    else {
      uploadFile(fUpload);
    }
  }
  
  public
  void uploadListOfFile()
  {
    if(sDirectory == null) sDirectory = FMViewer.sUSER_HOME;
    if(oRPCClient == null && fmViewer == null) return;
    if(listOfFile == null || listOfFile.size() == 0) return;
    if(oRPCClient == null) oRPCClient = fmViewer.createRPCClient();
    File firstFile = null;
    int iCountFilesToUpload = 0;
    for(int i = 0; i < listOfFile.size(); i++) {
      Object oFile = listOfFile.get(i);
      if(!(oFile instanceof File)) continue;
      File file = (File) oFile;
      if(file.exists()) {
        iCountFilesToUpload++;
        if(iCountFilesToUpload == 1) {
          firstFile = file;
        }
      }
    }
    if(iCountFilesToUpload == 0) return;
    if(fmViewer != null) {
      if(iCountFilesToUpload == 1) {
        if(FMUtils.parentDialog != null) {
          dialogProgress = new DialogProgress(FMUtils.parentDialog, "Upload \"" + firstFile.getName() + "\"", firstFile.isDirectory());
        }
        else {
          dialogProgress = new DialogProgress(ResourcesMgr.mainFrame, "Upload \"" + firstFile.getName() + "\"", firstFile.isDirectory());
        }
      }
      else {
        if(FMUtils.parentDialog != null) {
          dialogProgress = new DialogProgress(FMUtils.parentDialog, "Upload " + iCountFilesToUpload + " files", true);
        }
        else {
          dialogProgress = new DialogProgress(ResourcesMgr.mainFrame, "Upload " + iCountFilesToUpload + " files", true);
        }
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
      if(iCountFilesToUpload > 1) {
        dialogProgress.appendln("Upload " + iCountFilesToUpload + " files in \"" + sDirectory + "\"...");
      }
      dialogProgress.setValue(0);
    }
    else {
      System.out.print("Upload " + iCountFilesToUpload + " files in \"" + sDirectory + "\"...");
    }
    try {
      for(int i = 0; i < listOfFile.size(); i++) {
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            dialogProgress.appendln("stopped.");
            break;
          }
        }
        Object oFile = listOfFile.get(i);
        if(!(oFile instanceof File)) continue;
        File file = (File) oFile;
        if(file.exists()) {
          if(file.isDirectory()) {
            uploadDirectory(file);
          }
          else {
            if(dialogProgress != null) {
              dialogProgress.append("Upload \"" + file.getName() + "\" in \"" + FMUtils.getFileName(sDirectory) + "\"...");
              dialogProgress.setValue(0);
            }
            else {
              System.out.print("Upload \"" + file.getName() + "\" in \"" + sDirectory + "\"...");
            }
            uploadFile(file);
          }
        }
      }
      if(iCountFilesToUpload > 1) {
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            dialogProgress.appendln("Upload files stopped.");
          }
          else {
            dialogProgress.appendln("Upload files completed.");
          }
        }
        else {
          System.out.println("Upload files completed.");
        }
      }
    }
    finally {
      if(dialogProgress != null) dialogProgress.setClosable(true);
    }
  }
  
  public
  void uploadFile(File fileToUpload)
  {
    if(sDirectory == null) sDirectory = FMViewer.sUSER_HOME;
    if(oRPCClient == null && fmViewer == null) return;
    String sFilePath = null;
    if(fileToUpload == null) {
      JFileChooser jFileChooser = new JFileChooser();
      String sUploadFolder = ResourcesMgr.dat.getProperty("upload.folder");
      if(sUploadFolder != null && sUploadFolder.length() > 0) {
        File fileUploadFolder = new File(sUploadFolder);
        if(fileUploadFolder.exists() && fileUploadFolder.isDirectory()) {
          jFileChooser.setCurrentDirectory(fileUploadFolder);
        }
      }
      jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int iResult = jFileChooser.showOpenDialog(ResourcesMgr.mainFrame);
      if(iResult != JFileChooser.APPROVE_OPTION) return;
      fileToUpload = jFileChooser.getSelectedFile();
      if(fileToUpload == null) return;
      sFilePath = fileToUpload.getAbsolutePath();
      sUploadFolder = FMUtils.getFolder(sFilePath);
      ResourcesMgr.dat.setProperty("upload.folder", sUploadFolder);
      ResourcesMgr.saveDat();
    }
    else {
      sFilePath = fileToUpload.getAbsolutePath();
    }
    if(oRPCClient == null) oRPCClient = fmViewer.createRPCClient();
    String sFileName = FMUtils.getFileName(sFilePath);
    if(sFileName == null || sFileName.length() == 0) return;
    boolean boDialogProgressCreated = false;
    try {
      // Controllo esistenza file
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sDirectory));
      vParameters.add(FMUtils.encrypt(sFileName));
      Boolean oResult = (Boolean) oRPCClient.execute("FM.exist", vParameters);
      if(oResult != null && oResult.booleanValue()) {
        boolean boConfirm = false;
        if(dialogProgress != null) {
          boConfirm = GUIMessage.getConfirmation(dialogProgress, "File " + sFileName + " esistente. Lo si vuole sovrascrivere?");
        }
        else {
          boConfirm = GUIMessage.getConfirmation("File " + sFileName + " esistente. Lo si vuole sovrascrivere?");
        }
        if(!boConfirm) return;
      }
      
      if(fmViewer != null && dialogProgress == null) {
        if(FMUtils.parentDialog != null) {
          dialogProgress = new DialogProgress(FMUtils.parentDialog, "Upload \"" + sFilePath + "\"", false);
        }
        else {
          dialogProgress = new DialogProgress(ResourcesMgr.mainFrame, "Upload \"" + sFilePath + "\"", false);
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
        dialogProgress.append("Upload \"" + sFileName + "\" in \"" + FMUtils.getFileName(sDirectory) + "\"...");
        dialogProgress.setValue(0);
        boDialogProgressCreated = true;
      }
      else {
        System.out.print("Upload \"" + sFileName + "\" in \"" + sDirectory + "\"...");
      }
      
      uploadFile(sDirectory, sFilePath, false);
    }
    catch(Exception ex) {
      if(dialogProgress != null) {
        dialogProgress.showException("Exception during upload " + sFileName, ex);
      }
      else {
        GUIMessage.showException("Exception during upload " + sFileName, ex);
      }
    }
    finally {
      if(boDialogProgressCreated) dialogProgress.setClosable(true);
    }
  }
  
  public
  void uploadDirectory(File fLocalDirectory)
  {
    if(sDirectory == null) sDirectory = FMViewer.sUSER_HOME;
    if(oRPCClient == null && fmViewer == null) return;
    if(fLocalDirectory == null) {
      JFileChooser jFileChooser = new JFileChooser();
      String sUploadFolder = ResourcesMgr.dat.getProperty("upload.folder");
      if(sUploadFolder != null && sUploadFolder.length() > 0) {
        File fileUploadFolder = new File(sUploadFolder);
        if(fileUploadFolder.exists() && fileUploadFolder.isDirectory()) {
          jFileChooser.setCurrentDirectory(fileUploadFolder);
        }
      }
      jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      int iResult = jFileChooser.showOpenDialog(ResourcesMgr.mainFrame);
      if(iResult != JFileChooser.APPROVE_OPTION) return;
      fLocalDirectory = jFileChooser.getSelectedFile();
      if(fLocalDirectory == null) return;
      sUploadFolder = FMUtils.getFolder(fLocalDirectory.getAbsolutePath());
      ResourcesMgr.dat.setProperty("upload.folder", sUploadFolder);
      ResourcesMgr.saveDat();
    }
    if(oRPCClient == null) oRPCClient = fmViewer.createRPCClient();
    String sDirectoryName = fLocalDirectory.getName();
    String sParent = fLocalDirectory.getParent();
    boolean boDialogProgressCreated = false;
    try {
      // Controllo esistenza directory
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sDirectory));
      vParameters.add(FMUtils.encrypt(sDirectoryName));
      Boolean oResult = (Boolean) oRPCClient.execute("FM.exist", vParameters);
      if(oResult != null && oResult.booleanValue()) {
        boolean boConfirm = false;
        if(dialogProgress != null) {
          boConfirm = GUIMessage.getConfirmation(dialogProgress, "Directory " + sDirectoryName + " esistente. La si vuole sovrascrivere?");
        }
        else {
          boConfirm = GUIMessage.getConfirmation("Directory " + sDirectoryName + " esistente. La si vuole sovrascrivere?");
        }
        if(!boConfirm) return;
      }
      
      if(fmViewer != null && dialogProgress == null) {
        if(FMUtils.parentDialog != null) {
          dialogProgress = new DialogProgress(FMUtils.parentDialog, "Upload directory \"" + fLocalDirectory.getAbsolutePath() + "\"", true);
        }
        else {
          dialogProgress = new DialogProgress(ResourcesMgr.mainFrame, "Upload directory \"" + fLocalDirectory.getAbsolutePath() + "\"", true);
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
        boDialogProgressCreated = true;
      }
      
      char cRemoteSeparator = FMUtils.getSeparator(sDirectory);
      List listFiles = FMUtils.getFiles(null, fLocalDirectory);
      if(listFiles == null || listFiles.size() == 0) {
        if(dialogProgress != null) {
          GUIMessage.showWarning(dialogProgress, "Non ci sono file di trasferire nella cartella selezionata.");
        }
        else {
          GUIMessage.showWarning("Non ci sono file di trasferire nella cartella selezionata.");
        }
        return;
      }
      Collections.sort(listFiles);
      for(int i = 0; i < listFiles.size(); i++) {
        String sFile     = (String) listFiles.get(i);
        String sFolder   = FMUtils.getFolder(sFile);
        String sFilePath = sParent + File.separator + sFile;
        String sRemDir   = sDirectory + cRemoteSeparator + sFolder.replace('\\', cRemoteSeparator).replace('/', cRemoteSeparator);
        if(dialogProgress != null) {
          dialogProgress.append("Upload \"" + sFile + "\" in \"" + FMUtils.getFileName(sDirectory) + "\"...");
          dialogProgress.setValue(0);
        }
        else {
          System.out.print("Upload \"" + sFile + "\" in \"" + sDirectory + "\"...");
        }
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            dialogProgress.appendln("stopped.");
            break;
          }
        }
        uploadFile(sRemDir, sFilePath, true);
      }
      if(dialogProgress != null) {
        if(dialogProgress.isStopRequested()) {
          dialogProgress.appendln("Upload directory \"" + sDirectoryName + "\" stopped.");
        }
        else {
          dialogProgress.appendln("Upload directory \"" + sDirectoryName + "\" completed.");
        }
      }
      else {
        System.out.println("Upload directory \"" + sDirectoryName + "\" completed.");
      }
    }
    catch(Exception ex) {
      if(dialogProgress != null) {
        dialogProgress.showException("Exception during upload " + sDirectoryName, ex);
      }
      else {
        ex.printStackTrace();
      }
    }
    finally {
      if(boDialogProgressCreated) dialogProgress.setClosable(true);
    }
  }
  
  private
  boolean uploadFile(String sRemoteDirectory, String sFilePath, boolean boMakeDirs)
    throws Exception
  {
    Boolean oResult  = null;
    String sFileName = FMUtils.getFileName(sFilePath);
    // Start Upload
    Vector vParameters = new Vector();
    vParameters.add(FMUtils.encrypt(sRemoteDirectory));
    vParameters.add(FMUtils.encrypt(sFileName));
    vParameters.add(new Boolean(boMakeDirs));
    String sTmpFile = (String) oRPCClient.execute("FM.startUpload", vParameters);
    // Upload content
    vParameters.clear();
    vParameters.add(FMUtils.encrypt(sTmpFile));
    vParameters.add(new byte[0]);
    MessageDigest md = MessageDigest.getInstance("MD5");
    FileInputStream is = null;
    try {
      is = new FileInputStream(sFilePath);
      int iAvailable   = is.available();
       int iBufferLegth = 50 * 1024;
      int iBytesReaded = 0;
      int iTransferred = 0;
      int iPercentage  = 0;
      byte[] abBuffer = new byte[iBufferLegth];
      while((iBytesReaded = is.read(abBuffer)) > 0) {
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            dialogProgress.appendln("stopped.");
            return false;
          }
        }
        md.update(abBuffer, 0, iBytesReaded);
        if(iBytesReaded < iBufferLegth) {
          byte[] abReductBuffer = new byte[iBytesReaded];
          System.arraycopy(abBuffer, 0, abReductBuffer, 0, iBytesReaded);
          vParameters.set(1, abReductBuffer);
        } else {
          vParameters.set(1, abBuffer);
        }
        oRPCClient.execute("FM.appendContent", vParameters, false);
        iTransferred += iBytesReaded;
         iPercentage = (iTransferred * 100) / iAvailable;
        if(dialogProgress != null) {
          dialogProgress.setValue(iPercentage);
        }
      }
      if(iAvailable == 0) {
        vParameters.set(1, new byte[0]);
        oRPCClient.execute("FM.appendContent", vParameters, false);
        if(dialogProgress != null) {
          dialogProgress.setValue(100);
        }
      }
      if(dialogProgress != null) {
        if(dialogProgress.isStopRequested()) {
          dialogProgress.appendln("stopped.");
          return false;
        }
      }
      // End upload
      String sMD5 = Base64Coder.encodeLines(md.digest());
      vParameters.set(1, sMD5 != null ? sMD5.trim() : "");
      oResult = (Boolean) oRPCClient.execute("FM.endUpload", vParameters, false);
      if(dialogProgress != null) {
        dialogProgress.setValue(100);
        if(oResult != null && oResult.booleanValue()) {
          dialogProgress.appendln("Ok");
        }
        else {
          dialogProgress.appendln("canceled.");
        }
      }
      else {
        if(oResult != null && oResult.booleanValue()) {
          System.out.println("Ok");
        }
        else {
          System.out.println("canceled.");
        }
      }
    }
    finally {
      if(is != null) try { is.close(); } catch(Exception ex) {}
    }
    if(oResult != null && oResult.booleanValue()) {
      if(sNewName != null && sNewName.length() > 0) {
        vParameters.clear();
        vParameters.add(FMUtils.encrypt(sRemoteDirectory + "/" + sFileName));
        vParameters.add(FMUtils.encrypt(sNewName));
        oRPCClient.execute("FM.rename", vParameters, false);
      }
      if(fmViewer != null) {
        fmViewer.setAtLeastOneUpload(true);
      }
      if(listOfActionListener != null && listOfActionListener.size() > 0) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sFilePath);
        for(int i = 0; i < listOfActionListener.size(); i++) {
          ActionListener al = listOfActionListener.get(i);
          al.actionPerformed(e);
        }
      }
    }
    return oResult != null && oResult.booleanValue();
  }
}

