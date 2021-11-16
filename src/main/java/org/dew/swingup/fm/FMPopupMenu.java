package org.dew.swingup.fm;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.IConstants;
import org.dew.swingup.ResourcesMgr;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public
class FMPopupMenu extends JPopupMenu implements ActionListener
{
  private static final long serialVersionUID = 7505770121193024387L;
  
  protected FMViewer fmViewer;
  protected boolean boReadOnly = false;
  
  protected JMenuItem jmiRefresh;
  protected JMenuItem jmiGoTo;
  protected JMenuItem jmiAddToFavorites;
  
  protected JMenuItem jmiDownload;
  protected JMenuItem jmiUploadFile;
  protected JMenuItem jmiUploadDir;
  
  protected JMenuItem jmiView;
  protected JMenuItem jmiHead;
  protected JMenuItem jmiTail;
  protected JMenuItem jmiFind;
  protected JMenuItem jmiInfo;
  
  protected JMenuItem jmiCopy;
  protected JMenuItem jmiCut;
  protected JMenuItem jmiPaste;
  
  protected JMenuItem jmiRename;
  protected JMenuItem jmiDelete;
  protected JMenuItem jmiTouch;
  protected JMenuItem jmiMakeDir;
  
  protected JMenuItem jmiEnv;
  protected JMenuItem jmiCommand;
  
  public FMPopupMenu(FMViewer fmViewer, boolean boReadOnly)
  {
    this.fmViewer   = fmViewer;
    this.boReadOnly = boReadOnly;
    buildPopupMenu();
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    try {
      fmViewer.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      if(sActionCommand.equals("refresh"))        fmViewer.doRefresh();
      else if(sActionCommand.equals("goto"))      fmViewer.doGoTo();
      else if(sActionCommand.equals("addtofav"))  fmViewer.doAddToFavorites();
      else if(sActionCommand.equals("download"))  fmViewer.doDownload(null);
      else if(sActionCommand.equals("upload"))    fmViewer.doUpload(null, false);
      else if(sActionCommand.equals("uploaddir")) fmViewer.doUpload(null, true);
      else if(sActionCommand.equals("view"))      fmViewer.doView(null);
      else if(sActionCommand.equals("head"))      fmViewer.doHead(null);
      else if(sActionCommand.equals("tail"))      fmViewer.doTail(null);
      else if(sActionCommand.equals("find"))      fmViewer.doFind(null);
      else if(sActionCommand.equals("info"))      fmViewer.doInfo(null);
      else if(sActionCommand.equals("cut"))       fmViewer.doCut(null);
      else if(sActionCommand.equals("copy"))      fmViewer.doCopy(null, true);
      else if(sActionCommand.equals("paste"))     fmViewer.doPaste(null, true, true);
      else if(sActionCommand.equals("rename"))    fmViewer.doRename(null);
      else if(sActionCommand.equals("delete"))    fmViewer.doDelete(null);
      else if(sActionCommand.equals("touch"))     fmViewer.doTouch(null);
      else if(sActionCommand.equals("makedir"))   fmViewer.doMakeDir(null);
      else if(sActionCommand.equals("env"))       fmViewer.doEnv();
      else if(sActionCommand.equals("command"))   fmViewer.doCommand();
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    finally {
      fmViewer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  
  public
  void enableMenuItemsBySelection(Object[] aoSelection, Object oCopied)
  {
    if(aoSelection == null || aoSelection.length == 0) {
      jmiRefresh.setEnabled(true);
      jmiGoTo.setEnabled(true);
      jmiAddToFavorites.setEnabled(false);
      
      jmiDownload.setEnabled(true);
      jmiUploadFile.setEnabled(true);
      jmiUploadDir.setEnabled(true);
      
      jmiView.setEnabled(false);
      jmiHead.setEnabled(false);
      jmiTail.setEnabled(false);
      jmiFind.setEnabled(false);
      jmiInfo.setEnabled(false);
      
      jmiCut.setEnabled(false);
      jmiCopy.setEnabled(false);
      jmiPaste.setEnabled(oCopied != null);
      
      jmiRename.setEnabled(false);
      jmiDelete.setEnabled(false);
      jmiTouch.setEnabled(false);
      jmiMakeDir.setEnabled(true);
      
      jmiEnv.setEnabled(true);
      jmiCommand.setEnabled(true);
    }
    else
    if(aoSelection.length == 1) {
      Object oItemSelected = aoSelection[0];
      boolean boItemSelectedIsFile = true;
      String sItemName = null;
      if(oItemSelected instanceof FMEntry) {
        boItemSelectedIsFile = ((FMEntry) oItemSelected).isFile();
        sItemName = ((FMEntry) oItemSelected).getName();
      }
      if(boItemSelectedIsFile) {
        jmiRefresh.setEnabled(true);
        jmiGoTo.setEnabled(true);
        jmiAddToFavorites.setEnabled(false);
        
        jmiDownload.setEnabled(true);
        jmiUploadFile.setEnabled(true);
        jmiUploadDir.setEnabled(true);
        
        jmiView.setEnabled(true);
        jmiHead.setEnabled(true);
        jmiTail.setEnabled(true);
        jmiFind.setEnabled(true);
        jmiInfo.setEnabled(true);
        
        jmiCut.setEnabled(true);
        jmiCopy.setEnabled(true);
        jmiPaste.setEnabled(oCopied != null);
        
        jmiRename.setEnabled(true);
        jmiDelete.setEnabled(true);
        jmiTouch.setEnabled(true);
        jmiMakeDir.setEnabled(true);
        
        jmiEnv.setEnabled(true);
        jmiCommand.setEnabled(true);
      }
      else {
        jmiRefresh.setEnabled(true);
        jmiGoTo.setEnabled(true);
        jmiAddToFavorites.setEnabled(true);
        
        jmiDownload.setEnabled(true);
        jmiUploadFile.setEnabled(true);
        jmiUploadDir.setEnabled(true);
        
        jmiView.setEnabled(false);
        jmiHead.setEnabled(false);
        jmiTail.setEnabled(false);
        jmiFind.setEnabled(false);
        jmiInfo.setEnabled(true);
        
        jmiCut.setEnabled(false);
        jmiCopy.setEnabled(sItemName != null &&(sItemName.startsWith(". ") || sItemName.startsWith("..")));
        jmiPaste.setEnabled(oCopied != null);
        
        jmiRename.setEnabled(true);
        jmiDelete.setEnabled(true);
        jmiTouch.setEnabled(true);
        jmiMakeDir.setEnabled(true);
        
        jmiEnv.setEnabled(true);
        jmiCommand.setEnabled(true);
      }
    }
    else {
      jmiRefresh.setEnabled(true);
      jmiGoTo.setEnabled(true);
      jmiAddToFavorites.setEnabled(false);
      
      jmiDownload.setEnabled(true);
      jmiUploadFile.setEnabled(true);
      jmiUploadDir.setEnabled(true);
      
      jmiView.setEnabled(false);
      jmiHead.setEnabled(false);
      jmiTail.setEnabled(false);
      jmiFind.setEnabled(false);
      jmiInfo.setEnabled(false);
      
      jmiCut.setEnabled(false);
      jmiCopy.setEnabled(false);
      jmiPaste.setEnabled(oCopied != null);
      
      jmiRename.setEnabled(false);
      jmiDelete.setEnabled(true);
      jmiTouch.setEnabled(false);
      jmiMakeDir.setEnabled(true);
      
      jmiEnv.setEnabled(true);
      jmiCommand.setEnabled(true);
    }
  }
  
  protected
  void buildPopupMenu()
  {
    jmiRefresh = new JMenuItem("Ricarica", ResourcesMgr.getSmallImageIcon("Refresh24.gif"));
    jmiRefresh.setActionCommand("refresh");
    jmiRefresh.addActionListener(this);
    jmiGoTo = new JMenuItem("Vai a percorso favorito", ResourcesMgr.getSmallImageIcon("FastForward24.gif"));
    jmiGoTo.setActionCommand("goto");
    jmiGoTo.addActionListener(this);
    jmiAddToFavorites = new JMenuItem("Aggiungi ai favoriti", ResourcesMgr.getSmallImageIcon("Bookmarks24.gif"));
    jmiAddToFavorites.setActionCommand("addtofav");
    jmiAddToFavorites.addActionListener(this);
    
    jmiDownload = new JMenuItem("Scarica", ResourcesMgr.getSmallImageIcon("DownLarge.gif"));
    jmiDownload.setActionCommand("download");
    jmiDownload.addActionListener(this);
    jmiUploadFile = new JMenuItem("Carica File", ResourcesMgr.getSmallImageIcon("UpLarge.gif"));
    jmiUploadFile.setActionCommand("upload");
    jmiUploadFile.addActionListener(this);
    jmiUploadDir = new JMenuItem("Carica Directory", ResourcesMgr.getSmallImageIcon("UpLarge.gif"));
    jmiUploadDir.setActionCommand("uploaddir");
    jmiUploadDir.addActionListener(this);
    
    jmiView = new JMenuItem("Vedi", ResourcesMgr.getSmallImageIcon("DocumentMagLarge.gif"));
    jmiView.setActionCommand("view");
    jmiView.addActionListener(this);
    jmiHead = new JMenuItem("Prime righe", ResourcesMgr.getSmallImageIcon("DocumentLarge.gif"));
    jmiHead.setActionCommand("head");
    jmiHead.addActionListener(this);
    jmiTail = new JMenuItem("Ultime righe", ResourcesMgr.getSmallImageIcon("DocumentLarge.gif"));
    jmiTail.setActionCommand("tail");
    jmiTail.addActionListener(this);
    jmiFind = new JMenuItem("Cerca", ResourcesMgr.getSmallImageIcon("MagnifyLarge.gif"));
    jmiFind.setActionCommand("find");
    jmiFind.addActionListener(this);
    jmiInfo = new JMenuItem("Propriet\340", ResourcesMgr.getSmallImageIcon("About16.gif"));
    jmiInfo.setActionCommand("info");
    jmiInfo.addActionListener(this);
    
    jmiRename = new JMenuItem("Rinomina", ResourcesMgr.getSmallImageIcon("DocumentDrawLarge.gif"));
    jmiRename.setActionCommand("rename");
    jmiRename.addActionListener(this);
    jmiDelete = new JMenuItem("Cancella", ResourcesMgr.getSmallImageIcon("DeleteDocumentSmall.gif"));
    jmiDelete.setActionCommand("delete");
    jmiDelete.addActionListener(this);
    jmiTouch = new JMenuItem("Aggiorna data", ResourcesMgr.getSmallImageIcon("ClockLarge.gif"));
    jmiTouch.setActionCommand("touch");
    jmiTouch.addActionListener(this);
    jmiMakeDir = new JMenuItem("Crea cartella", ResourcesMgr.getSmallImageIcon("OpenLarge.gif"));
    jmiMakeDir.setActionCommand("makedir");
    jmiMakeDir.addActionListener(this);
    
    jmiCut = new JMenuItem("Talia", ResourcesMgr.getImageIcon(IConstants.sICON_CUT));
    jmiCut.setActionCommand("cut");
    jmiCut.addActionListener(this);
    jmiCopy = new JMenuItem("Copia", ResourcesMgr.getImageIcon(IConstants.sICON_COPY));
    jmiCopy.setActionCommand("copy");
    jmiCopy.addActionListener(this);
    jmiPaste = new JMenuItem("Incolla", ResourcesMgr.getImageIcon(IConstants.sICON_PASTE));
    jmiPaste.setActionCommand("paste");
    jmiPaste.addActionListener(this);
    
    jmiEnv = new JMenuItem("Ambiente", ResourcesMgr.getSmallImageIcon("HammerLarge.gif"));
    jmiEnv.setActionCommand("env");
    jmiEnv.addActionListener(this);
    jmiCommand = new JMenuItem("Comandi", ResourcesMgr.getSmallImageIcon("GearwheelLarge.gif"));
    jmiCommand.setActionCommand("command");
    jmiCommand.addActionListener(this);
    
    this.add(jmiRefresh);
    this.add(jmiGoTo);
    this.add(jmiAddToFavorites);
    this.addSeparator();
    this.add(jmiDownload);
    if(!boReadOnly) {
      this.add(jmiUploadFile);
      this.add(jmiUploadDir);
    }
    this.addSeparator();
    this.add(jmiView);
    this.add(jmiHead);
    this.add(jmiTail);
    this.add(jmiFind);
    this.add(jmiInfo);
    if(!boReadOnly) {
      this.addSeparator();
      this.add(jmiRename);
      this.add(jmiDelete);
      this.add(jmiTouch);
      this.add(jmiMakeDir);
      this.addSeparator();
      this.add(jmiCut);
      this.add(jmiCopy);
      this.add(jmiPaste);
      this.addSeparator();
      this.add(jmiEnv);
      this.add(jmiCommand);
    }
  }
}
