package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import org.dew.swingup.*;
import org.dew.swingup.rpc.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DeleteManager implements Runnable
{
  protected FMViewer fmViewer;
  protected IRPCClient oRPCClient;
  protected List listOfFMEntry;
  
  public
  DeleteManager(FMViewer fmViewer, List listOfFMEntry)
  {
    this.fmViewer = fmViewer;
    this.listOfFMEntry = listOfFMEntry;
  }
  
  public
  DeleteManager(IRPCClient oRPCClient, List listOfFMEntry)
  {
    this.oRPCClient    = oRPCClient;
    this.listOfFMEntry = listOfFMEntry;
  }
  
  public
  void run()
  {
    if(listOfFMEntry == null || listOfFMEntry.size() == 0) return;
    if(oRPCClient == null && fmViewer == null) return;
    if(oRPCClient == null) oRPCClient = fmViewer.createRPCClient();
    
    DialogProgress dialogProgress = null;
    try {
      if(fmViewer != null) {
        if(FMUtils.parentDialog != null) {
          dialogProgress = new DialogProgress(FMUtils.parentDialog, "Delete files");
        }
        else {
          dialogProgress = new DialogProgress(ResourcesMgr.mainFrame, "Delete files");
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
      
      int iPercentage = 0;
      int iEntries    = listOfFMEntry.size();
      for(int i = 0; i < iEntries; i++) {
        FMEntry fmEntry = (FMEntry) listOfFMEntry.get(i);
        if(fmEntry == null) continue;
        if(dialogProgress != null) {
          if(dialogProgress.isStopRequested()) {
            break;
          }
        }
        delete(fmEntry, dialogProgress);
         iPercentage = (i + 1) * 100 / iEntries;
        dialogProgress.setValue(iPercentage);
      }
      
      if(dialogProgress != null) {
        if(dialogProgress.isStopRequested()) {
          dialogProgress.appendln("Delete of " + listOfFMEntry.size() + " elements stopped.");
        }
        else {
          dialogProgress.setValue(100);
          dialogProgress.appendln("Delete of " + listOfFMEntry.size() + " elements completed.");
        }
      }
      else {
        System.out.println("Delete of " + listOfFMEntry.size() + " elements completed.");
      }
    }
    finally {
      if(dialogProgress != null) dialogProgress.setClosable(true);
    }
  }
  
  private
  boolean delete(FMEntry fmEntry, DialogProgress dialogProgress)
  {
    if(fmEntry == null) return false;
    if(dialogProgress != null) {
      dialogProgress.append("Delete \"" + fmEntry.getPath() + "\"...");
    }
    else {
      System.out.print("Delete \"" + fmEntry.getPath() + "\"...");
    }
    String sEntryPath = fmEntry.getPath();
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sEntryPath));
      if(fmEntry.isDirectory()) {
        Map mapResult = (Map) oRPCClient.execute("FM.info", vParameters, false);
        Integer oCountDirectories = (Integer) mapResult.get("cd");
        Integer oCountFiles       = (Integer) mapResult.get("cf");
        int iCountDirectories     = oCountDirectories != null ? oCountDirectories.intValue() : 0;
        int iCountFiles           = oCountFiles != null ? oCountFiles.intValue() : 0;
        if(iCountDirectories > 0 || iCountFiles > 0) {
          boolean boConfirm = false;
          if(dialogProgress != null) {
            boConfirm = GUIMessage.getConfirmation(dialogProgress, "La cartella " + sEntryPath + " contiene " + iCountDirectories + " cartelle e " + iCountFiles + " file. Continuare?");
          }
          else {
            boConfirm = GUIMessage.getConfirmation("La cartella " + sEntryPath + " contiene " + iCountDirectories + " cartelle e " + iCountFiles + " file. Continuare?");
          }
          if(!boConfirm) return true;
        }
      }
      Boolean oResult = (Boolean) oRPCClient.execute("FM.delete", vParameters, false);
      if(oResult == null || !oResult.booleanValue()) {
        if(dialogProgress != null) {
          dialogProgress.appendln("not performed.");
        }
        else {
          System.out.println("not performed.");
        }
        return false;
      }
    }
    catch(Exception ex) {
      if(dialogProgress != null) {
        dialogProgress.showException("Exception during delete " + fmEntry.getName(), ex);
      }
      else {
        ex.printStackTrace();
      }
      return false;
    }
    if(dialogProgress != null) {
      dialogProgress.appendln("Ok");
    }
    else {
      System.out.println("Ok");
    }
    return true;
  }
}

