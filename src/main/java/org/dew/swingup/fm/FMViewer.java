package org.dew.swingup.fm;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.*;
import org.dew.swingup.components.StatusBar;
import org.dew.swingup.dialog.TextDialog;
import org.dew.swingup.rpc.*;

@SuppressWarnings({"deprecation","rawtypes","unchecked"})
public
class FMViewer extends JPanel implements DropTargetListener
{
  private static final long serialVersionUID = -8862627111556303396L;
  
  public static final String sUSER_HOME = "user.home";
  
  public static final ImageIcon iconFolder    = ResourcesMgr.getImageIcon("OpenLarge.gif");
  public static final ImageIcon iconFile      = ResourcesMgr.getImageIcon("DocumentLarge.gif");
  public static final ImageIcon iconTextFile  = ResourcesMgr.getImageIcon("DocumentDrawLarge.gif");
  public static final ImageIcon iconSheetFile = ResourcesMgr.getImageIcon("SheetsLarge.gif");
  public static final ImageIcon iconDocFile   = ResourcesMgr.getImageIcon("ListLarge.gif");
  public static final ImageIcon iconJavaArc   = ResourcesMgr.getImageIcon("InitProjectLarge.gif");
  public static final ImageIcon iconArchive   = ResourcesMgr.getImageIcon("BookLarge.gif");
  public static final ImageIcon iconImage     = ResourcesMgr.getImageIcon("PaletteLarge.gif");
  public static final Color colorJavaArc      = new Color(0, 128, 0);
  
  protected DropTarget dropTarget;
  protected FMBreadCrumb breadCrumb;
  protected JTextField jtfFilter;
  protected FMPopupMenu fmPopupMenu;
  protected JList jlFiles;
  protected Vector<FMEntry> vEntries;
  protected String sCurrentDirectory = sUSER_HOME;
  protected String sPathCopied;
  protected boolean boCopy;
  
  protected String sServerName;
  protected String sWS_URL;
  protected IRPCClient oRPCClient;
  protected String sRootDirectory;
  protected String sRootFilter;
  protected boolean boReadOnly;
  protected boolean boAtLeastOneUpload = false;
  
  public
  FMViewer(String sServerName, String sWS_URL)
  {
    this.sServerName = sServerName;
    this.sWS_URL     = sWS_URL;
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di FMViewer", ex);
    }
  }
  
  public
  FMViewer(String sServerName, String sWS_URL, String sRootDirectory, String sRootFilter)
  {
    this.sServerName    = sServerName;
    this.sWS_URL        = sWS_URL;
    this.sRootDirectory = sRootDirectory;
    this.sRootFilter    = sRootFilter;
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di FMViewer", ex);
    }
  }
  
  public
  FMViewer(String sServerName, String sWS_URL, String sRootDirectory, String sRootFilter, boolean boReadOnly)
  {
    this.sServerName    = sServerName;
    this.sWS_URL        = sWS_URL;
    this.sRootDirectory = sRootDirectory;
    this.sRootFilter    = sRootFilter;
    this.boReadOnly     = boReadOnly;
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di FMViewer", ex);
    }
  }
  
  public
  Vector<FMEntry> getEntries()
  {
    return vEntries;
  }
  
  public
  String getRootDirectory()
  {
    return sRootDirectory;
  }
  
  public
  String getSelectedEntryPath()
  {
    FMEntry fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) return null;
    return fmEntry.getPath();
  }
  
  public
  String getVersion()
  {
    try {
      return (String) oRPCClient.execute("FM.getVersion", new Vector(), false);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }
  
  public
  boolean isAtLeastOneUpload()
  {
    return boAtLeastOneUpload;
  }
  
  public
  void setAtLeastOneUpload(boolean boValue)
  {
    this.boAtLeastOneUpload = boValue;
  }
  
  public
  void doGoTo()
  {
    String sGoTo = null;
    if(FMUtils.parentDialog != null) {
      sGoTo = DialogGoTo.showMe(FMUtils.parentDialog, this, getFavorites(), sCurrentDirectory);
    }
    else {
      sGoTo = DialogGoTo.showMe(this, getFavorites(), sCurrentDirectory);
    }
    if(sGoTo == null || sGoTo.length() == 0) return;
    sCurrentDirectory = sGoTo;
    doRefresh();
  }
  
  public
  void doAddToFavorites()
  {
    FMEntry fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null || !fmEntry.isDirectory()) return;
    if(addToFavorites(fmEntry.getPath())) {
      GUIMessage.showInformation(fmEntry.getPath() + " added to favorites.");
    }
  }
  
  public
  boolean addToFavorites(String sPath)
  {
    if(sPath == null || sPath.length() == 0) {
      GUIMessage.showWarning("Invalid path to add.");
      return false;
    }
    List listKeys = new ArrayList();
    Iterator iterator = ResourcesMgr.dat.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String sKey = (String) entry.getKey();
      if(sKey.startsWith("fm." + sServerName + ".")) {
        listKeys.add(sKey);
        String sValue = (String) entry.getValue();
        if(sValue != null && sValue.equals(sPath)) {
          GUIMessage.showWarning(sPath + " already added to favorites.");
          return false;
        }
      }
    }
    Collections.sort(listKeys);
    // Si riscrivono i progressivi poiche' le cancellazioni creano dei buchi
    // e si potrebbe raggiungere erroneamente il limite di 100 voci.
    int iProg = -1;
    for(int i = 0; i < listKeys.size(); i++) {
      String sKey   = (String) listKeys.get(i);
      String sValue = (String) ResourcesMgr.dat.remove(sKey);
      if(!FMUtils.isPath(sValue)) continue;
      iProg++;
      String sProg = iProg < 10 ? "0" + iProg : String.valueOf(iProg);
      ResourcesMgr.dat.setProperty("fm." + sServerName + "." + sProg, sValue);
    }
    if(iProg >= 0) {
      iProg++;
      if(iProg == 100) {
        GUIMessage.showWarning("E' stato raggiunto il limite di 100 favoriti.");
        return false;
      }
      String sProg = iProg < 10 ? "0" + iProg : String.valueOf(iProg);
      ResourcesMgr.dat.setProperty("fm." + sServerName + "." + sProg, sPath);
    }
    else {
      ResourcesMgr.dat.setProperty("fm." + sServerName + ".00", sPath);
    }
    ResourcesMgr.saveDat();
    return true;
  }
  
  public
  boolean removeFromFavorites(String sPath)
  {
    if(sPath == null || sPath.length() == 0) return false;
    Iterator iterator = ResourcesMgr.dat.entrySet().iterator();
    boolean boFound = false;
    while(iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String sKey = (String) entry.getKey();
      if(sKey.startsWith("fm." + sServerName + ".")) {
        String sValue = (String) entry.getValue();
        if(sValue != null && sValue.equals(sPath)) {
          iterator.remove();
          boFound = true;
        }
      }
    }
    ResourcesMgr.saveDat();
    return boFound;
  }
  
  public
  List getFavorites()
  {
    List listKeys = new ArrayList();
    Iterator iterator = ResourcesMgr.dat.keySet().iterator();
    while(iterator.hasNext()) {
      String sKey = (String) iterator.next();
      if(sKey.startsWith("fm." + sServerName + ".")) {
        listKeys.add(sKey);
      }
    }
    Collections.sort(listKeys);
    List listResult = new ArrayList();
    for(int i = 0; i < listKeys.size(); i++) {
      String sKey   = (String) listKeys.get(i);
      String sValue = (String) ResourcesMgr.dat.getProperty(sKey);
      if(sValue == null || sValue.length() == 0) continue;
      listResult.add(sValue);
    }
    return listResult;
  }
  
  public
  void doRefresh()
  {
    boolean boIsRoot = false;
    if(sCurrentDirectory == null || sCurrentDirectory.length() == 0 || sCurrentDirectory.equalsIgnoreCase(sUSER_HOME)) {
      if(sRootDirectory != null && sRootDirectory.length() > 0) {
        sCurrentDirectory = sRootDirectory;
      }
      else {
        sCurrentDirectory = sUSER_HOME;
      }
      boIsRoot = true;
    }
    if(sRootDirectory != null && sRootDirectory.length() > 0) {
      if(!sCurrentDirectory.startsWith(sRootDirectory)) {
        GUIMessage.showWarning("Accesso non consentito alle cartelle superiori alla root.");
        sCurrentDirectory = sRootDirectory;
        boIsRoot = true;
      }
      else if(sCurrentDirectory.length() <= sRootDirectory.length() + 1) {
        // sCurrentDirectory potrebbe avere un carattere in piu' (il separatore)
        boIsRoot = true;
      }
    }
    if(sCurrentDirectory.endsWith("_out")) {
      boolean boConfirm = GUIMessage.getConfirmation("Tale cartella potrebbe contenere molti elementi. Proseguire?");
      if(!boConfirm) {
        String sSubDir = GUIMessage.getInput("Volendo si pu\362 specificare una sottocartella di " + sCurrentDirectory);
        if(sSubDir == null || sSubDir.length() == 0) return;
        char cRemoteSeparator = FMUtils.getSeparator(sCurrentDirectory);
        sCurrentDirectory = sCurrentDirectory + cRemoteSeparator + sSubDir;
      }
    }
    List listResult = null;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sCurrentDirectory));
      if(boIsRoot && sRootFilter != null && !sRootFilter.equals("*.*")) {
        vParameters.add(FMUtils.encrypt(sRootFilter));
      }
      else {
        vParameters.add(FMUtils.encrypt(jtfFilter.getText()));
      }
      listResult = (List) oRPCClient.execute("FM.ls", vParameters, true);
    }
    catch(Exception ex) {
      showException("Exception in FM.ls(" + sCurrentDirectory + ")", ex);
      return;
    }
    if(sRootDirectory != null && sRootDirectory.length() > 0) {
      if(boIsRoot) listResult.remove(1); // Si toglie ..
      FMUtils.replaceName(listResult, 0, sRootDirectory, "cartella");
      FMUtils.replaceName(listResult, 1, sRootDirectory, "cartella");
    }
    int iDirectories = 0;
    int iFiles       = 0;
    if(listResult != null && listResult.size() > 0) {
      vEntries = new Vector<FMEntry>();
      for(int i = 0; i < listResult.size(); i++) {
        FMEntry fmEntry = new FMEntry((Map) listResult.get(i));
        if(fmEntry.isDirectory()) {
          // Si escludono . e ..
          if(i > 1) iDirectories++;
        }
        else {
          iFiles++;
        }
        vEntries.add(fmEntry);
      }
      FMEntry fmEntryCurrDir = vEntries.get(0);
      sCurrentDirectory = fmEntryCurrDir.getPath();
      if(boIsRoot && sRootDirectory != null && sRootDirectory.length() > 0) {
        sRootDirectory = sCurrentDirectory;
        breadCrumb.setPathToHide(sCurrentDirectory, "cartella");
      }
      breadCrumb.setPath(sCurrentDirectory);
      jlFiles.setListData(vEntries);
    }
    else {
      vEntries = new Vector<FMEntry>();
      jlFiles.setListData(vEntries);
      breadCrumb.setPath(null);
    }
    int iElements = iDirectories + iFiles;
    StatusBar statusBar = ResourcesMgr.getStatusBar();
    statusBar.setText(iElements + " elements (" + iDirectories + " directories, " + iFiles + " files)");
  }
  
  public
  void doViewText(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) return;
    if(fmEntry.isDirectory()) {
      sCurrentDirectory = fmEntry.getPath();
      doRefresh();
      return;
    }
    long lKLength = fmEntry.getKLength();
    if(lKLength > 1024) {
      boolean boConfirm = GUIMessage.getConfirmation("Il file supera 1024 KBytes. Lo si vuole aprire lo stesso?");
      if(!boConfirm) return;
    }
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      String sResult = (String) oRPCClient.execute("FM.getTextContent", vParameters, true);
      if(FMUtils.parentDialog != null) {
        TextDialog.showTextMessage(FMUtils.parentDialog, fmEntry.getName(), sResult);
      }
      else {
        GUITextViewer.showMe(fmEntry, sResult);
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.getTextContent", ex);
    }
  }
  
  public
  void doView(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) return;
    if(fmEntry.isDirectory()) {
      sCurrentDirectory = fmEntry.getPath();
      doRefresh();
      return;
    }
    if(fmEntry.isTextFile()) {
      doViewText(fmEntry);
      return;
    }
    String sExtension = fmEntry.getExtension();
    if(sExtension != null &&(sExtension.equalsIgnoreCase("csv") || sExtension.equalsIgnoreCase("imp"))) {
      doViewText(fmEntry);
      return;
    }
    boolean boSupported = ResourcesMgr.isViewFileSupported(fmEntry.getName());
    if(!boSupported) {
      String sMsg = "L'apertura automatica del file selezionato non \350 attualmente\n";
      sMsg += "supportata per questo sistema operativo.\n";
      sMsg += "Il file verr\340 scaricato nella cartella che indicherete,\n";
      sMsg += "successivamente potrete aprirlo dal sistema operativo.";
      GUIMessage.showWarning(sMsg);
      doDownload(fmEntry);
      return;
    }
    List listOfFMEntry = new ArrayList();
    listOfFMEntry.add(fmEntry);
    Thread thread = new Thread(new DownloadManager(this, listOfFMEntry, true));
    thread.start();
  }
  
  public
  void doHead(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null || fmEntry.isDirectory()) return;
    String sRows = GUIMessage.getInput("Inserire il numero di righe (100 se vuoto)");
    int iRows = 100;
    if(sRows != null && sRows.length() > 0) {
      try { iRows = Integer.parseInt(sRows); } catch(Exception ex) {}
    }
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      vParameters.add(new Integer(iRows));
      String sResult = (String) oRPCClient.execute("FM.head", vParameters, true);
      if(FMUtils.parentDialog != null) {
        TextDialog.showTextMessage(FMUtils.parentDialog, "head " + iRows + " " + fmEntry.getName(), sResult);
      }
      else {
        GUITextViewer.showMe(fmEntry, "head " + iRows + " " + fmEntry.getName(), sResult);
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.head", ex);
    }
  }
  
  public
  void doTail(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null || fmEntry.isDirectory()) return;
    String sRows = GUIMessage.getInput("Inserire il numero di righe (100 se vuoto)");
    int iRows = 100;
    if(sRows != null && sRows.length() > 0) {
      try { iRows = Integer.parseInt(sRows); } catch(Exception ex) {}
    }
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      vParameters.add(new Integer(iRows));
      String sResult = (String) oRPCClient.execute("FM.tail", vParameters, true);
      if(FMUtils.parentDialog != null) {
        TextDialog.showTextMessage(FMUtils.parentDialog, "tail " + iRows + " " + fmEntry.getName(), sResult);
      }
      else {
        GUITextViewer.showMe(fmEntry, "tail " + iRows + " " + fmEntry.getName(), sResult);
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.tail", ex);
    }
  }
  
  public
  void doFind(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null || fmEntry.isDirectory()) return;
    String sText = GUIMessage.getInput("Inserire il testo da ricercare (case insensitive) nel file selezionato");
    if(sText == null || sText.length() == 0) return;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      vParameters.add(sText);
      vParameters.add(new Integer(1000));
      List listResult = (List) oRPCClient.execute("FM.find", vParameters, true);
      GUITextViewer.showMe(fmEntry, listResult);
    }
    catch(Exception ex) {
      showException("Exception in FM.find", ex);
    }
  }
  
  public
  void doCut(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null || fmEntry.isDirectory()) return;
    sPathCopied  = fmEntry.getPath();
    StringSelection oStringSelection = new StringSelection(sPathCopied);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(oStringSelection, null);
    boCopy = false;
  }
  
  public
  void doCopy(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) return;
    StringSelection oStringSelection = new StringSelection(fmEntry.getPath());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(oStringSelection, null);
    if(fmEntry.isFile()) {
      sPathCopied  = fmEntry.getPath();
      boCopy = true;
    }
    else {
      sPathCopied = null;
      boCopy = false;
    }
  }
  
  public
  void doPaste(FMEntry fmEntry)
  {
    if(sPathCopied  == null || sPathCopied.length()  == 0) return;
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    String sDirectory = null;
    if(fmEntry == null) {
      sDirectory = sCurrentDirectory;
    }
    else
    if(fmEntry.isDirectory()) {
      sDirectory = fmEntry.getPath();
    }
    else {
      sDirectory = sCurrentDirectory;
    }
    boolean boConfirm = false;
    if(boCopy) {
      boConfirm = GUIMessage.getConfirmation("Copiare " + FMUtils.getFileName(sPathCopied) + " in " + sDirectory + " ?");
    }
    else {
      boConfirm = GUIMessage.getConfirmation("Spostare " + FMUtils.getFileName(sPathCopied) + " in " + sDirectory + " ?");
    }
    if(!boConfirm) return;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sPathCopied));
      vParameters.add(FMUtils.encrypt(sDirectory));
      if(boCopy) {
        Boolean oResult = (Boolean) oRPCClient.execute("FM.copy", vParameters, true);
        if(oResult == null || !oResult.booleanValue()) {
          GUIMessage.showWarning("File " + sPathCopied + " non copiato.");
          return;
        }
      }
      else {
        Boolean oResult = (Boolean) oRPCClient.execute("FM.move", vParameters, true);
        if(oResult == null || !oResult.booleanValue()) {
          GUIMessage.showWarning("File " + sPathCopied + " non spostato.");
          return;
        }
        sPathCopied = null;
      }
    }
    catch(Exception ex) {
      if(boCopy) {
        showException("Exception in FM.copy", ex);
      }
      else {
        showException("Exception in FM.move", ex);
      }
    }
    doRefresh();
  }
  
  public
  void doDelete(FMEntry fmEntry)
  {
    List listOfFMEntry = getSelectedEntries(fmEntry);
    if(listOfFMEntry == null || listOfFMEntry.size() == 0) return;
    if(listOfFMEntry.size() == 1) {
      fmEntry = (FMEntry) listOfFMEntry.get(0);
      doDeleteEntry(fmEntry);
      doRefresh();
    }
    else {
      boolean boConfirm = GUIMessage.getConfirmation("Sei sicuro di voler cancellare i " + listOfFMEntry.size() + " elementi selezionati?");
      if(!boConfirm) return;
      Thread thread = new Thread(new DeleteManager(this, listOfFMEntry));
      thread.start();
    }
  }
  
  public
  void doDeleteEntry(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) return;
    String sEntryPath = fmEntry.getPath();
    String sEntryName = fmEntry.getName();
    boolean boConfirm = false;
    if(fmEntry.isDirectory()) {
      boConfirm = GUIMessage.getConfirmation("Sei sicuro di voler cancellare la directory " + sEntryName + " ?");
    }
    else {
      boConfirm = GUIMessage.getConfirmation("Sei sicuro di voler cancellare il file " + sEntryName + " ?");
    }
    if(!boConfirm) return;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sEntryPath));
      if(fmEntry.isDirectory()) {
        Map mapResult = (Map) oRPCClient.execute("FM.info", vParameters, false);
        Integer oCountDirectories = (Integer) mapResult.get("cd");
        Integer oCountFiles   = (Integer) mapResult.get("cf");
        int iCountDirectories = oCountDirectories != null ? oCountDirectories.intValue() : 0;
        int iCountFiles       = oCountFiles != null ? oCountFiles.intValue() : 0;
        if(iCountDirectories > 0 || iCountFiles > 0) {
          boConfirm = GUIMessage.getConfirmation("La cartella " + sEntryName + " contiene " + iCountDirectories + " cartelle e " + iCountFiles + " file. Continuare?");
          if(!boConfirm) return;
        }
      }
      Boolean oResult = (Boolean) oRPCClient.execute("FM.delete", vParameters, true);
      if(oResult == null || !oResult.booleanValue()) {
        GUIMessage.showWarning("Elemento " + sEntryName + " non cancellato.");
        return;
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.delete", ex);
    }
  }
  
  public
  void doMakeDir(FMEntry fmEntry)
  {
    String sDirectory = null;
    if(fmEntry != null) {
      if(fmEntry.isFile()) {
        GUIMessage.showWarning("Occorre selezionare una cartella per creare una sottocartella.");
        return;
      }
      sDirectory = fmEntry.getPath();
    }
    else {
      sDirectory = sCurrentDirectory;
      if(sDirectory == null) sDirectory = sUSER_HOME;
    }
    String sDirectoryName = GUIMessage.getInput("Immettere il nome della cartella da creare");
    if(sDirectoryName == null || sDirectoryName.length() == 0) return;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sDirectory));
      vParameters.add(FMUtils.encrypt(sDirectoryName));
      Boolean oResult = (Boolean) oRPCClient.execute("FM.mkdir", vParameters, true);
      if(oResult == null || !oResult.booleanValue()) {
        GUIMessage.showWarning("Cartella " + sDirectoryName + " non creata.");
        return;
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.mkdir", ex);
    }
    doRefresh();
  }
  
  public
  void doInfo(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) return;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      Map mapResult = (Map) oRPCClient.execute("FM.info", vParameters, true);
      FMEntry fmInfo = new FMEntry(mapResult);
      GUITextViewer.showMe("Info of " + fmEntry.getName(), fmInfo.getInfo());
    }
    catch(Exception ex) {
      showException("Exception in FM.info", ex);
    }
  }
  
  public
  void doEnv()
  {
    try {
      Map mapResult = (Map) oRPCClient.execute("FM.env", new Vector(), true);
      if(sServerName != null && sServerName.length() > 0) {
        GUITextViewer.showMe(sServerName + " Environmnet", mapResult);
      }
      else {
        GUITextViewer.showMe("Environmnet", mapResult);
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.env", ex);
    }
  }
  
  public
  void doCommand()
  {
    String sTitle = "";
    if(sServerName != null && sServerName.length() > 0) {
      sTitle += sServerName + " ";
    }
    if(sCurrentDirectory == null || sCurrentDirectory.length() == 0) sCurrentDirectory = sUSER_HOME;
    if(sCurrentDirectory.length() > 18) {
      sTitle += "..." + sCurrentDirectory.substring(sCurrentDirectory.length() - 18);
    }
    GUICommand.showMe(this, sCurrentDirectory, sTitle);
  }
  
  public
  String doExecute(String sCommandLine, String sDirectory, Vector vTextToType)
  {
    if(sCommandLine == null || sCommandLine.length() == 0) return "Invalid command.";
    if(sCurrentDirectory == null || sCurrentDirectory.length() == 0) sCurrentDirectory = sUSER_HOME;
    if(sDirectory == null) sDirectory = sCurrentDirectory;
    if(vTextToType == null) vTextToType = new Vector();
    String sResult = null;
    try {
      // Thread-Safe
      IRPCClient oRPCClient = FMUtils.createRPCClient(sWS_URL);
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sDirectory));
      vParameters.add(FMUtils.encrypt(sCommandLine));
      vParameters.add(vTextToType);
      sResult = (String) oRPCClient.execute("FM.execute", vParameters, true);
      if(sResult == null || sResult.length() == 0) sResult = "";
    }
    catch(Exception ex) {
      sResult = "[" + ex.toString() + "]";
    }
    return sResult;
  }
  
  public
  void doTouch(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) {
      GUIMessage.showWarning("Selezionare il file da aggiornare");
      return;
    }
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(fmEntry.getPath()));
      Boolean oResult = (Boolean) oRPCClient.execute("FM.touch", vParameters, true);
      if(oResult == null || !oResult.booleanValue()) {
        GUIMessage.showWarning("File " + fmEntry.getPath() + " non aggiornato.");
        return;
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.touch", ex);
    }
  }
  
  public
  void doRename(FMEntry fmEntry)
  {
    if(fmEntry == null) fmEntry = (FMEntry) jlFiles.getSelectedValue();
    if(fmEntry == null) {
      GUIMessage.showWarning("Selezionare il file da rinominare");
      return;
    }
    String sEntryPath = fmEntry.getPath();
    String sNewFileName = null;
    if(FMUtils.parentDialog != null) {
      sNewFileName = DialogInput.showMeForRename("Rinomina", sEntryPath);
    }
    else {
      sNewFileName = DialogInput.showMeForRename("Rinomina", sEntryPath);
    }
    if(sNewFileName == null || sNewFileName.length() == 0) return;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sEntryPath));
      vParameters.add(FMUtils.encrypt(sNewFileName));
      Boolean oResult = (Boolean) oRPCClient.execute("FM.rename", vParameters, true);
      if(oResult == null || !oResult.booleanValue()) {
        GUIMessage.showWarning("File " + sEntryPath + " non rinominato.");
        return;
      }
    }
    catch(Exception ex) {
      showException("Exception in FM.rename", ex);
    }
    doRefresh();
  }
  
  public
  void doDownload(FMEntry fmEntry)
  {
    List listOfFMEntry = getSelectedEntries(fmEntry);
    if(listOfFMEntry == null || listOfFMEntry.size() == 0) {
      GUIMessage.showWarning("Selezionare un file per effettuare il download");
      return;
    }
    if(listOfFMEntry.size() == 1) {
      FMEntry fmEntry0 = (FMEntry) listOfFMEntry.get(0);
      if(fmEntry0.isDirectory()) {
        String sDirPath  = fmEntry0.getPath();
        String sFileName = null;
        if(FMUtils.parentDialog != null) {
          sFileName = DialogInput.showMeForDonwloadDir(FMUtils.parentDialog, "Download file", sDirPath);
        }
        else {
          sFileName = DialogInput.showMeForDonwloadDir("Download file", sDirPath);
        }
        if(sFileName == null || sFileName.length() == 0) return;
        char cRemSeparator = FMUtils.getSeparator(sDirPath);
        String sFilePath = sDirPath + cRemSeparator + sFileName;
        FMEntry fmInfo = getInfo(sFilePath);
        if(fmInfo == null) return;
        if(fmInfo.isDirectory()) {
          GUIMessage.showWarning("L'elemento specificato non \350 un file, ma una cartella.");
          return;
        }
        listOfFMEntry.set(0, fmInfo);
      }
    }
    Thread thread = new Thread(new DownloadManager(this, listOfFMEntry));
    thread.start();
  }
  
  public
  FMEntry getInfo(String sFilePath)
  {
    FMEntry fmEntry = null;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sFilePath));
      Map mapResult = (Map) oRPCClient.execute("FM.info", vParameters, true);
      fmEntry = new FMEntry(mapResult);
    }
    catch(Exception ex) {
      showException("Exception in FM.info", ex);
    }
    return fmEntry;
  }
  
  public
  int check(String sFilePath)
  {
    Number oResult = null;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sFilePath));
      oResult = (Number) oRPCClient.execute("FM.check", vParameters, true);
    }
    catch(Exception ex) {
      showException("Exception in FM.check", ex);
    }
    return oResult != null ? oResult.intValue() : 0;
  }
  
  public
  List getProcesses()
  {
    List listResult = null;
    try {
      Vector vParameters = new Vector();
      listResult = (List) oRPCClient.execute("FM.getProcesses", vParameters, true);
    }
    catch(Exception ex) {
      showException("Exception in FM.getProcesses", ex);
    }
    return listResult;
  }
  
  public
  boolean kill(String sKeyProcess)
  {
    Boolean oResult = null;
    try {
      Vector vParameters = new Vector();
      vParameters.add(FMUtils.encrypt(sKeyProcess));
      oResult = (Boolean) oRPCClient.execute("FM.kill", vParameters, true);
    }
    catch(Exception ex) {
      showException("Exception in FM.kill", ex);
    }
    return oResult != null ? oResult.booleanValue() : false;
  }
  
  public
  void doUpload(FMEntry fmEntry, boolean boDirectory)
  {
    String sDirectory = null;
    if(fmEntry != null) {
      if(fmEntry.isFile()) {
        GUIMessage.showWarning("Occorre selezionare una cartella per l'upload.");
        return;
      }
      sDirectory = fmEntry.getPath();
    }
    else {
      sDirectory = sCurrentDirectory;
    }
    Thread thread = new Thread(new UploadManager(this, sDirectory, boDirectory));
    thread.start();
  }
  
  public
  void doUpload(FMEntry fmEntry, List listOfFile)
  {
    if(listOfFile == null || listOfFile.size() == 0) return;
    String sDirectory = null;
    if(fmEntry != null) {
      if(fmEntry.isFile()) {
        GUIMessage.showWarning("Occorre selezionare una cartella per l'upload.");
        return;
      }
      sDirectory = fmEntry.getPath();
    }
    else {
      sDirectory = sCurrentDirectory;
    }
    Thread thread = new Thread(new UploadManager(this, sDirectory, listOfFile));
    thread.start();
  }
  
  public
  List getSelectedEntries(FMEntry fmEntry)
  {
    List listResult = new ArrayList();
    if(fmEntry != null) listResult.add(fmEntry);
    else {
      Object[] aoSelectedValues = jlFiles.getSelectedValues();
      if(aoSelectedValues != null && aoSelectedValues.length > 0) {
        for(int i = 0; i < aoSelectedValues.length; i++) {
          listResult.add((FMEntry) aoSelectedValues[i]);
        }
      }
    }
    return listResult;
  }
  
  public
  void showException(String sMessage, Exception ex)
  {
    String sExceptionMessage = ex.getMessage();
    if(sExceptionMessage != null) {
      int iIndexOf = sExceptionMessage.indexOf("FM#");
      if(iIndexOf >= 0) {
        GUIMessage.showWarning(sExceptionMessage.substring(iIndexOf + 3));
        return;
      }
    }
    GUIMessage.showException(sMessage, ex);
  }
  
  public
  IRPCClient createRPCClient()
  {
    try {
      return FMUtils.createRPCClient(sWS_URL);
    }
    catch(Exception ex) {
      showException("Exception in FMViewer.createRPCClient()", ex);
    }
    return null;
  }
  
  // DropTargetListener
  public void dragExit(DropTargetEvent dte) {
  }
  public void dragEnter(DropTargetDragEvent dtde) {
  }
  public void dragOver(DropTargetDragEvent dtde) {
  }
  public void dropActionChanged(DropTargetDragEvent dtde) {
  }
  public void drop(DropTargetDropEvent dtde) {
    int action = dtde.getDropAction();
    dtde.acceptDrop(action);
    try {
      Transferable t = dtde.getTransferable();
      List listFiles = null;
      if(t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        listFiles = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
      }
      else {
        // Linux
        DataFlavor dfLinux = new DataFlavor("text/uri-list;class=java.lang.String");
        String urls = (String) t.getTransferData(dfLinux);
        if((urls != null)&&(urls.length() > 0)) {
          listFiles = new ArrayList();
          StringTokenizer st = new StringTokenizer(urls);
          while(st.hasMoreTokens()) {
            listFiles.add(new File(new URI(st.nextToken())));
          }
        }
      }
      doUpload(null, listFiles);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    dtde.dropComplete(true);
  }
  
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout(4, 4));
    this.setBorder(BorderFactory.createTitledBorder("Files"));
    
    oRPCClient = FMUtils.createRPCClient(sWS_URL);
    
    fmPopupMenu = new FMPopupMenu(this, boReadOnly);
    jlFiles = new JList();
    jlFiles.setFont(new Font("Monospaced", Font.PLAIN, 12));
    jlFiles.setCellRenderer(new DefaultListCellRenderer() {
      private static final long serialVersionUID = 1956938105205146907L;
      
      public Component getListCellRendererComponent(JList list, Object value, int row, boolean isSelected, boolean hasFocus) {
        super.getListCellRendererComponent(list, value, row, isSelected, hasFocus);
        FMEntry fmEntry = (FMEntry) value;
        if(fmEntry.isDirectory()) {
          setIcon(iconFolder);
          if(fmEntry.isHidden()) {
            setForeground(Color.darkGray);
            setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
        }
        else {
          if(fmEntry.isHidden()) {
            setForeground(Color.darkGray);
            setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
          if(fmEntry.isImageFile()) {
            setIcon(iconImage);
          }
          else
          if(fmEntry.isExecutable()) {
            setIcon(iconFile);
            setForeground(Color.red);
          }
          else
          if(fmEntry.isJavaArchive()) {
            setIcon(iconJavaArc);
            setForeground(colorJavaArc);
          }
          else
          if(fmEntry.isArchive()) {
            setIcon(iconArchive);
          }
          else
          if(fmEntry.isTextFile()) {
            setIcon(iconTextFile);
            setForeground(Color.blue);
          }
          else
          if(fmEntry.isDocFile()) {
            setIcon(iconDocFile);
          }
          else
          if(fmEntry.isSpreadsheetFile()) {
            setIcon(iconSheetFile);
          }
          else {
            setIcon(iconFile);
          }
        }
        return this;
      }
    });
    jlFiles.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        // isPopupTrigger potrebbe restiruire false in Linux
        if(e.isPopupTrigger() || e.getButton() != MouseEvent.BUTTON1) {
          int iIndex = jlFiles.locationToIndex(e.getPoint());
          if(iIndex >= 0) {
            boolean boContains = false;
            int[] aiIndices = jlFiles.getSelectedIndices();
            if(aiIndices != null) {
              for(int i = 0; i < aiIndices.length; i++) {
                if(iIndex == aiIndices[i]) {
                  boContains = true;
                  break;
                }
              }
            }
            if(!boContains) jlFiles.setSelectedIndex(iIndex);
          }
          fmPopupMenu.enableMenuItemsBySelection(jlFiles.getSelectedValues(), sPathCopied);
          fmPopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) doView(null);
      }
    });
    jlFiles.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        FMEntry fmEntry = (FMEntry) jlFiles.getSelectedValue();
        if(fmEntry == null) return;
        StatusBar statusBar = ResourcesMgr.getStatusBar();
        statusBar.setText(fmEntry.getShortInfo());
      }
    });
    dropTarget = new DropTarget(jlFiles, this);
    JScrollPane jScrollPane = new JScrollPane(jlFiles);
    breadCrumb = new FMBreadCrumb();
    breadCrumb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String sAC = e.getActionCommand();
        if(sAC != null && sAC.length() > 0) {
          sCurrentDirectory = sAC;
          doRefresh();
        }
      }
    });
    jtfFilter = new JTextField();
    jtfFilter.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          doRefresh();
        }
      }
    });
    jtfFilter.setPreferredSize(new Dimension(120, 0));
    JLabel jlFilter = new JLabel("Filter:");
    jlFilter.setPreferredSize(new Dimension(40, 0));
    JPanel jpFilter = new JPanel(new BorderLayout());
    jpFilter.add(jlFilter,  BorderLayout.WEST);
    jpFilter.add(jtfFilter, BorderLayout.CENTER);
    JPanel jpNorth = new JPanel(new BorderLayout());
    jpNorth.add(breadCrumb, BorderLayout.CENTER);
    jpNorth.add(jpFilter,   BorderLayout.EAST);
    this.add(jpNorth,     BorderLayout.NORTH);
    this.add(jScrollPane, BorderLayout.CENTER);
  }
}
