package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class GUICommand extends JPanel implements ActionListener, IWorkObject
{
  private static final long serialVersionUID = -7370999985336804942L;
  
  protected FMViewer  fmViewer;
  protected String    sDirectory;
  protected JComboBox cbCommandLine;
  protected JTextArea jTextArea;
  
  protected JButton btnExecute;
  protected JButton btnKill;
  protected JButton btnReset;
  
  public
  GUICommand(FMViewer fmViewer, String sDirectory)
  {
    this.fmViewer   = fmViewer;
    this.sDirectory = sDirectory;
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUICommand", ex);
    }
  }
  
  public void onActivated() {
  }
  
  public boolean onClosing() {
    return true;
  }
  
  public void onOpened() {
    if(cbCommandLine != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          cbCommandLine.requestFocus();
        }
      });
    }
  }
  
  public static void showMe(FMViewer fmViewer, String sDirectory, String sTitle) {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    workPanel.show(new GUICommand(fmViewer, sDirectory), sTitle, "ExecuteProjectLarge.gif");
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    try {
      this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      if(sActionCommand.equals("execute")) {
        doExecute();
      }
      else
      if(sActionCommand.equals("kill")) {
        doKill();
      }
      else
      if(sActionCommand.equals("reset")) {
        doReset();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    finally {
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  
  public
  void doExecute()
  {
    if(fmViewer == null) {
      GUIMessage.showWarning("The FMViewer instance is null.");
      return;
    }
    String sCommandLine = cbCommandLine.getEditor().getItem().toString().trim();
    if(sCommandLine == null || sCommandLine.length() == 0) {
      GUIMessage.showWarning("Command line is empty.");
      return;
    }
    
    if(sCommandLine.equalsIgnoreCase("dir") || sCommandLine.startsWith("dir ")) {
      sCommandLine = "cmd /c " + sCommandLine;
    }
    else
    if(sCommandLine.equalsIgnoreCase("clear") || sCommandLine.equalsIgnoreCase("cls")) {
      cbCommandLine.addItem(sCommandLine);
      doReset();
      return;
    }
    else
    if(sCommandLine.equalsIgnoreCase("exit")) {
      ResourcesMgr.getWorkPanel().close(this);
      return;
    }
    else
    if(sCommandLine.startsWith("./") && sCommandLine.length() > 2) {
      sCommandLine = sDirectory + "/" + sCommandLine.substring(2);
    }
    else
    if(sCommandLine.startsWith("cd ")) {
      jTextArea.append("> " + sCommandLine + "\n");
      if(sCommandLine.length() > 3) {
        String sPath = sCommandLine.substring(3);
        if(sPath.equals(".")) {
          jTextArea.append(sDirectory + "\n");
        }
        else
        if(sPath.equals("..")) {
          String sParent = FMUtils.getFolder(sDirectory);
          if(sParent != null && sParent.length() > 0) {
            String sRootDirectory = fmViewer.getRootDirectory();
            if(sRootDirectory != null && !sParent.startsWith(sRootDirectory)) {
              GUIMessage.showWarning("Accesso non consentito alle cartelle superiori alla root.");
              return;
            }
            sDirectory = sParent;
            jTextArea.append(sDirectory + "\n");
          }
          else {
            jTextArea.append("[Err] Parent not available.\n");
          }
        }
        else {
          char c0 = sPath.charAt(0);
          if(c0 != '\\' && c0 != '/') {
            char cRemSep = FMUtils.getSeparator(sDirectory);
            sPath = sDirectory + cRemSep + sPath;
          }
          int iCheck = fmViewer.check(sPath);
          if(iCheck == 0) {
            jTextArea.append("Path \"" + sPath + "\" not exist.\n");
            return;
          }
          else
          if(iCheck == 2) {
            sDirectory = sPath;
            jTextArea.append(sDirectory + "\n");
          }
          else {
            jTextArea.append("[Err] " + sPath + " is not directory.\n");
          }
        }
      }
      else {
        jTextArea.append(sDirectory + "\n");
      }
      cbCommandLine.addItem(sCommandLine);
      cbCommandLine.getEditor().setItem("");
      return;
    }
    else
    if(sCommandLine.equalsIgnoreCase("FM.getVersion")) {
      jTextArea.append("> " + sCommandLine + "\n");
      String sVersion = fmViewer.getVersion();
      jTextArea.append(sVersion + "\n");
      cbCommandLine.addItem(sCommandLine);
      cbCommandLine.getEditor().setItem("");
      return;
    }
    
    Vector vTextToType = new Vector();
    int iSepTextToType = sCommandLine.indexOf('[');
    if(iSepTextToType > 0) {
      String sTextToType = sCommandLine.substring(iSepTextToType);
      sCommandLine = sCommandLine.substring(0, iSepTextToType).trim();
      if(sTextToType.length() > 1) {
        char cLast = sTextToType.charAt(sTextToType.length() - 1);
        if(cLast != ']') sTextToType += "]";
        Object oTextToType = StringToObject.parse(sTextToType);
        if(oTextToType instanceof Collection) {
          Iterator iterator = ((Collection) oTextToType).iterator();
          while(iterator.hasNext()) {
            Object oItem = iterator.next();
            if(oItem != null) vTextToType.add(oItem.toString());
          }
        }
      }
    }
    
    if(vTextToType != null && vTextToType.size() > 0) {
      jTextArea.append("> " + sCommandLine + " " + vTextToType + "\n");
    }
    else {
      jTextArea.append("> " + sCommandLine + "\n");
    }
    
    long lBegin = System.currentTimeMillis();
    String sResult = fmViewer.doExecute(sCommandLine, sDirectory, vTextToType);
    long lElapsed = System.currentTimeMillis() - lBegin;
    
    jTextArea.append(sResult);
    jTextArea.append("\n");
    jTextArea.append("[elapsed " + lElapsed + " ms]");
    jTextArea.append("\n");
    
    cbCommandLine.addItem(sCommandLine);
    cbCommandLine.getEditor().setItem("");
    
    if(sResult != null && sResult.indexOf("RPCCallTimedOutException") >= 0 && lElapsed > 25000) {
      GUIMessage.showWarning("Processo bloccato o in attesa di input. Sar\340 mostrata la dialog di kill.");
      doKill();
    }
  }
  
  public
  void doKill()
  {
    if(fmViewer == null) {
      GUIMessage.showWarning("The FMViewer instance is null.");
      return;
    }
    List listProcesses = fmViewer.getProcesses();
    if(listProcesses == null) return;
    if(listProcesses.size() == 0) {
      GUIMessage.showInformation("There are no process to kill.");
      return;
    }
    List listChoice = null;
    if(FMUtils.parentDialog != null) {
      listChoice = (List) DialogOptions.showMe(FMUtils.parentDialog, "Processes to kill", "Select the processes to kill",
        600, 500, listProcesses, true);
    }
    else {
      listChoice = (List) DialogOptions.showMe("Processes to kill", "Select the processes to kill",
        600, 500, listProcesses, true);
    }
    if(listChoice == null || listChoice.size() == 0) {
      GUIMessage.showWarning("Non \350 stato selezionato alcun processo. Nessun processo sar\340 killato.");
      return;
    }
    for(int i = 0; i < listChoice.size(); i++) {
      String sKeyProcess = (String) listChoice.get(i);
      if(!fmViewer.kill(sKeyProcess)) {
        GUIMessage.showWarning("Process " + sKeyProcess + " not killed.");
        return;
      }
    }
    GUIMessage.showInformation("Processes killed successfully");
  }
  
  public
  void doReset()
  {
    cbCommandLine.getEditor().setItem("");
    jTextArea.setText("");
  }
  
  protected
  Container buildButtonsContainer()
  {
    JPanel oButtonsPanel = new JPanel(new GridLayout(3, 1, 4, 4));
    
    btnExecute = GUIUtil.buildActionButton("&Execute|Execute command line|ExecuteProjectLarge.gif", "execute");
    btnExecute.addActionListener(this);
    
    btnKill = GUIUtil.buildActionButton("&Kill proc.|Kill processes|ThumbDownLarge.gif", "kill");
    btnKill.addActionListener(this);
    
    btnReset = GUIUtil.buildActionButton(IConstants.sGUIDATA_RESET, "reset");
    btnReset.addActionListener(this);
    
    oButtonsPanel.add(btnExecute);
    oButtonsPanel.add(btnKill);
    oButtonsPanel.add(btnReset);
    
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 8));
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    
    return oActionsPanel;
  }
  
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());
    
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    
    cbCommandLine = new JComboBox();
    cbCommandLine.setEditable(true);
    cbCommandLine.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          String sCommandLine = cbCommandLine.getEditor().getItem().toString().trim();
          if(sCommandLine == null || sCommandLine.length() == 0) {
            return;
          }
          doExecute();
        }
      }
    });
    
    JPanel jpCommand = new JPanel(new BorderLayout(4, 4));
    jpCommand.add(cbCommandLine, BorderLayout.NORTH);
    jpCommand.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Command Line"));
    
    jTextArea = new JTextArea();
    jTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    jTextArea.setForeground(new Color(0, 0, 128));
    JScrollPane jsCenter = new JScrollPane(jTextArea);
    jsCenter.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Output"));
    
    JPanel jpNorth = new JPanel(new BorderLayout(4, 4));
    jpNorth.add(jpCommand, BorderLayout.CENTER);
    jpNorth.add(buildButtonsContainer(), BorderLayout.EAST);
    
    this.add(jpNorth,  BorderLayout.NORTH);
    this.add(jsCenter, BorderLayout.CENTER);
  }
}
