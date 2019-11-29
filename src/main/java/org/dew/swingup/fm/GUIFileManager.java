package org.dew.swingup.fm;

import java.awt.*;
import javax.swing.*;

import org.dew.swingup.*;

public
class GUIFileManager extends JPanel implements IWorkObject
{
  private static final long serialVersionUID = -6043515941739975144L;
  
  protected FMViewer fmViewer;
  
  public
  GUIFileManager(String sWS_URL)
  {
    try {
      init(null, sWS_URL, null, null, false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUIFileManager", ex);
    }
  }
  
  public
  GUIFileManager(String sServerName, String sWS_URL)
  {
    try {
      init(sServerName, sWS_URL, null, null, false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUIFileManager", ex);
    }
  }
  
  public
  GUIFileManager(String sServerName, String sWS_URL, String sRootDirectory, String sRootFilter)
  {
    try {
      init(sServerName, sWS_URL, sRootDirectory, sRootFilter, false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUIFileManager", ex);
    }
  }
  
  public
  GUIFileManager(String sServerName, String sWS_URL, String sRootDirectory, String sRootFilter, boolean boReadOnly)
  {
    try {
      init(sServerName, sWS_URL, sRootDirectory, sRootFilter, boReadOnly);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUIFileManager", ex);
    }
  }
  
  public
  GUIFileManager()
  {
    this(null);
  }
  
  protected
  void init(String sServerName, String sWS_URL, String sRootDirectory, String sRootFilter, boolean boReadOnly)
    throws Exception
  {
    this.setLayout(new BorderLayout());
    if(sServerName == null || sServerName.length() == 0) {
      if(sWS_URL != null && sWS_URL.length() > 0) {
        sServerName = getHost(sWS_URL);
      }
      else {
        sServerName = "BackEnd";
      }
    }
    if(sRootDirectory != null && sRootDirectory.length() > 0) {
      fmViewer = new FMViewer(sServerName, sWS_URL, sRootDirectory, sRootFilter, boReadOnly);
    }
    else {
      fmViewer = new FMViewer(sServerName, sWS_URL);
    }
    this.add(fmViewer, BorderLayout.CENTER);
  }
  
  public void onActivated() {
  }
  
  public boolean onClosing() {
    return true;
  }
  
  public void onOpened() {
    if(fmViewer != null) {
      fmViewer.doRefresh();
    }
  }
  
  public static
  String getHost(String sURLService)
  {
    String sResult = null;
    int iSepProtocol = sURLService.indexOf("//");
    if(iSepProtocol > 0) {
      int iSepPort = sURLService.indexOf(':', iSepProtocol + 2);
      if(iSepPort > 0) {
        sResult = sURLService.substring(iSepProtocol + 2, iSepPort);
      }
      else {
        int iSepCtx  = sURLService.indexOf('/', iSepProtocol + 2);
        if(iSepCtx > 0) {
          sResult = sURLService.substring(iSepProtocol + 2, iSepCtx);
        }
        else {
          sResult = sURLService.substring(iSepProtocol + 2);
        }
      }
    }
    else {
      int iSepCtx  = sURLService.indexOf('/', iSepProtocol + 2);
      if(iSepCtx > 0) {
        sResult = sURLService.substring(0, iSepCtx);
      }
      else {
        sResult = sURLService;
      }
    }
    return sResult;
  }
}
