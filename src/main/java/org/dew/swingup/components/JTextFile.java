package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.util.*;
import java.util.List;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Componente utile per la scelta di un file.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class JTextFile extends JPanel
{
  protected static final String sTOOLTIP_TEXT = "(Alt-Invio cerca; Alt-Canc cancella)";
  
  protected JTextField txtFile;
  protected JButton btnFind;
  
  protected List listDecodeListener = new ArrayList();
  protected String sCurrDir = null;
  protected String sFilter = null;
  protected int iFileSelectionMode = JFileChooser.FILES_ONLY;
  
  public
  JTextFile()
  {
    try{
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Imposta la modalita' di selezione. Valori ammessi: <br />
   * JFileChooser.FILES_ONLY <br />
   * JFileChooser.DIRECTORIES_ONLY <br />
   * JFileChooser.FILES_AND_DIRECTORIES <br />
   *
   * @param iFileSelectionMode int
   */
  public
  void setFileSelectionMode(int iFileSelectionMode)
  {
    this.iFileSelectionMode = iFileSelectionMode;
  }
  
  public
  int getFileSelectionMode()
  {
    return iFileSelectionMode;
  }
  
  public
  void setFilter(String sFilter)
  {
    this.sFilter = sFilter;
  }
  
  public
  void setCurrentDirectory(String sCurrentDirectory)
  {
    this.sCurrDir = sCurrentDirectory;
  }
  
  public
  String getCurrentDirectory()
  {
    return sCurrDir;
  }
  
  public
  void addFocusListener(FocusListener fl)
  {
    super.addFocusListener(fl);
    
    if(txtFile != null) {
      txtFile.addFocusListener(fl);
    }
  }
  
  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    
    if(txtFile != null) {
      txtFile.removeFocusListener(fl);
    }
  }
  
  public
  void transferFocus()
  {
    if(txtFile != null) {
      txtFile.transferFocus();
    }
    else {
      super.transferFocus();
    }
  }
  
  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    
    if(txtFile != null) {
      txtFile.addKeyListener(kl);
    }
  }
  
  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    
    if(txtFile != null) {
      txtFile.removeKeyListener(kl);
    }
  }
  
  public
  void setBackground(Color color)
  {
    if(txtFile != null) {
      txtFile.setBackground(color);
    }
    else {
      super.setBackground(color);
    }
  }
  
  public
  Color getBackground()
  {
    if(txtFile != null) {
      return txtFile.getBackground();
    }
    
    return super.getBackground();
  }
  
  public
  void setName(String sName)
  {
    super.setName(sName);
    txtFile.setName(sName);
  }
  
  /**
   * Abilita/Disabilita il componente.
   *
   * @param boEnabled boolean
   */
  public
  void setEnabled(boolean boEnabled)
  {
    txtFile.setEnabled(boEnabled);
    btnFind.setEnabled(boEnabled);
  }
  
  /**
   * Aggiunge un oggetto IDecodeListener.
   *
   * @param oDecodeListener Oggetto IDecodeListener
   */
  public
  void addDecodeListener(IDecodeListener oDecodeListener)
  {
    if(oDecodeListener != null) {
      listDecodeListener.add(oDecodeListener);
    }
  }
  
  /**
   * Rimuove un oggetto IDecodeListener.
   *
   * @param oDecodeListener Oggetto IDecodeListener
   */
  public
  void removeDecodeListener(IDecodeListener oDecodeListener)
  {
    if(oDecodeListener != null) {
      listDecodeListener.remove(oDecodeListener);
    }
  }
  
  /**
   * Imposta il flag editabile.
   *
   * @param boEditable boolean
   */
  public
  void setEditable(boolean boEditable)
  {
    txtFile.setEditable(boEditable);
  }
  
  /**
   * Imposta direttamente il testo.
   *
   * @param sText String
   */
  public
  void setText(String sText)
  {
    txtFile.setText(sText);
    
    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      if(sText != null && sText.trim().length() > 0) {
        oDecodeListener.set();
      }
      else {
        oDecodeListener.reset();
      }
    }
  }
  
  /**
   * Ottiene il testo contenuto nella casella.
   *
   * @return String
   */
  public
  String getText()
  {
    return txtFile.getText();
  }
  
  /**
   * Ritorna la cartella in cui e' contenuto il file selezionato.
   *
   * @return String
   */
  public
  String getFolder()
  {
    String sText = txtFile.getText();
    
    int iLength = sText.length();
    for(int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sText.charAt(iLength - i);
      if(c == '/' || c == '\\') {
        return sText.substring(0, iIndex);
      }
    }
    
    return "";
  }
  
  /**
   * Cancella il testo contenuto nella casella.
   */
  public
  void reset()
  {
    txtFile.setText("");
    
    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      oDecodeListener.reset();
    }
  }
  
  /**
   * Lancia la dialog per la selezione del file.
   */
  public
  void doFind()
  {
    JFileChooser oFileChooser = new JFileChooser();
    oFileChooser.setFileSelectionMode(iFileSelectionMode);
    
    if(sFilter != null) {
      JSFileFilter jsFileFilter = new JSFileFilter();
      StringTokenizer st = new StringTokenizer(sFilter, "|");
      while(st.hasMoreTokens()) {
        String sToken = st.nextToken();
        jsFileFilter.add(sToken);
      }
      jsFileFilter.setDescription("Files " + sFilter.replace('|', ','));
      oFileChooser.setFileFilter(jsFileFilter);
    }
    
    for(int i = 0; i < listDecodeListener.size(); i++) {
      IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
      List oFilter = new ArrayList();
      oFilter.add(oFileChooser);
      oDecodeListener.beforeFind(oFilter);
    }
    
    if(sCurrDir != null) {
      oFileChooser.setCurrentDirectory(new File(sCurrDir));
    }
    else {
      String sText = txtFile.getText();
      if(sText != null && sText.trim().length() > 0) {
        File fCurrentDirectory = new File(sText);
        if(fCurrentDirectory.isDirectory()) {
          oFileChooser.setCurrentDirectory(fCurrentDirectory);
        }
        else {
          int iLastSlash = sText.lastIndexOf('/');
          if(iLastSlash < 0) {
            iLastSlash = sText.lastIndexOf('\\');
          }
          if(iLastSlash > 0) {
            fCurrentDirectory = new File(sText.substring(0, iLastSlash));
            if(fCurrentDirectory.isDirectory()) {
              oFileChooser.setCurrentDirectory(fCurrentDirectory);
            }
          }
        }
      }
    }
    
    int iResult = oFileChooser.showOpenDialog(ResourcesMgr.mainFrame);
    
    if(iResult == JFileChooser.APPROVE_OPTION) {
      txtFile.setText(oFileChooser.getSelectedFile().getAbsolutePath());
      for(int i = 0; i < listDecodeListener.size(); i++) {
        IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
        oDecodeListener.set();
      }
    }
    
    requestFocus();
  }
  
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());
    
    JPanel oComponents = new JPanel(new BorderLayout(4, 4));
    txtFile = new JTextField();
    txtFile.setToolTipText(sTOOLTIP_TEXT);
    txtFile.addKeyListener(new KeyAdapter() {
      public
      void keyPressed(KeyEvent oEvent)
      {
        int iKey = oEvent.getKeyCode();
        if(iKey == KeyEvent.VK_F && oEvent.isControlDown()) { // x Win98
          doFind();
        }
      }
      
      public
      void keyTyped(KeyEvent oEvent)
      {
        char cKey = oEvent.getKeyChar();
        if(cKey == KeyEvent.VK_ENTER && oEvent.isAltDown()) {
          doFind();
        }
        else
        if(cKey == KeyEvent.VK_DELETE && oEvent.isAltDown()) {
          reset();
        }
        else
        if(cKey == KeyEvent.VK_SPACE && oEvent.isAltDown()) {
          reset();
        }
      }
    });
    btnFind = GUIUtil.buildFindButton();
    btnFind.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent oEvent) {
        doFind();
      }
    }
    );
    
    oComponents.add(txtFile, BorderLayout.CENTER);
    oComponents.add(btnFind, BorderLayout.EAST);
    
    this.add(oComponents, BorderLayout.NORTH);
  }
  
  class JSFileFilter extends FileFilter
  {
    String sDescription = null;
    List listExtentions = new ArrayList();
    
    public JSFileFilter()
    {
    }
    
    public
    void add(String sExtention)
    {
      listExtentions.add(sExtention);
    }
    
    public
    void setDescription(String sDescription)
    {
      this.sDescription = sDescription;
    }
    
    public
    boolean accept(File file)
    {
      if(file == null) return false;
      
      if(file.isDirectory()) {
        return true;
      }
      
      String sFileName = file.getName();
      int iDot = sFileName.lastIndexOf('.');
      if(iDot < 0) {
        return false;
      }
      
      String sExtention = null;
      if(iDot == sFileName.length() - 1) {
        sExtention = "";
      }
      else {
        sExtention = sFileName.substring(iDot + 1);
      }
      
      for(int i = 0; i < listExtentions.size(); i++) {
        String sEntry = (String) listExtentions.get(i);
        if(sEntry.startsWith("*") && sEntry.length() > 1) {
          if(sExtention.toLowerCase().endsWith(sEntry.substring(1).toLowerCase())) {
            return true;
          }
        }
        else
        if(sEntry.endsWith("*") && sEntry.length() > 1) {
          if(sExtention.toLowerCase().startsWith(sEntry.substring(0, sEntry.length()-1).toLowerCase())) {
            return true;
          }
        }
        else
        if(sEntry.equalsIgnoreCase(sExtention)) {
          return true;
        }
      }
      
      return false;
    }
    
    public
    String getDescription()
    {
      return sDescription;
    }
  }
}
