package org.dew.swingup.fm;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import org.dew.swingup.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DialogInput extends AJDialog
{
  private static final long serialVersionUID = -8824662265614578595L;
  
  protected JTextField jtfFileName;
  protected String sTitle;
  protected List listOfJLabelText;
  protected String sDefault;
  protected String sResult;
  
  public DialogInput(String sTitle, List listOfJLabelText, String sDefault)
  {
    super();
    setTitle(sTitle);
    setModal(true);
    try {
      this.listOfJLabelText = listOfJLabelText;
      this.sDefault = sDefault;
      init(false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogInput", ex);
    }
  }
  
  public DialogInput(JDialog jdialog, String sTitle, List listOfJLabelText, String sDefault)
  {
    super(jdialog);
    setTitle(sTitle);
    setModal(true);
    try {
      this.listOfJLabelText = listOfJLabelText;
      this.sDefault = sDefault;
      init(false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogInput", ex);
    }
  }
  
  public static
  String showMe(String sTitle, List listOfJLabelText, String sDefault)
  {
    DialogInput dialog = new DialogInput(sTitle, listOfJLabelText, sDefault);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public static
  String showMeForRename(String sTitle, String sFilePath)
  {
    List listOfJLabelText = new ArrayList();
    listOfJLabelText.add("Rinonimare il seguente file");
    listOfJLabelText.add(FMUtils.getFileName(sFilePath));
    listOfJLabelText.add("in");
    DialogInput dialog = new DialogInput(sTitle, listOfJLabelText, FMUtils.getFileName(sFilePath));
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public static
  String showMeForRename(JDialog jdialog, String sTitle, String sFilePath)
  {
    List listOfJLabelText = new ArrayList();
    listOfJLabelText.add("Rinonimare il seguente file");
    listOfJLabelText.add(FMUtils.getFileName(sFilePath));
    listOfJLabelText.add("in");
    DialogInput dialog = new DialogInput(jdialog, sTitle, listOfJLabelText, FMUtils.getFileName(sFilePath));
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public static
  String showMeForDonwloadDir(String sTitle, String sDirPath)
  {
    List listOfJLabelText = new ArrayList();
    listOfJLabelText.add("Download non supportato per le cartelle, ma soltanto per i file.");
    listOfJLabelText.add("Specificare il nome del file presente nella seguente cartella remota");
    listOfJLabelText.add(sDirPath);
    listOfJLabelText.add("che si vuole scaricare in locale");
    DialogInput dialog = new DialogInput(sTitle, listOfJLabelText, "");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public static
  String showMeForDonwloadDir(JDialog jdialog, String sTitle, String sDirPath)
  {
    List listOfJLabelText = new ArrayList();
    listOfJLabelText.add("Download non supportato per le cartelle, ma soltanto per i file.");
    listOfJLabelText.add("Specificare il nome del file presente nella seguente cartella remota");
    listOfJLabelText.add(sDirPath);
    listOfJLabelText.add("che si vuole scaricare in locale");
    DialogInput dialog = new DialogInput(jdialog, sTitle, listOfJLabelText, "");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public String getResult() {
    return sResult;
  }
  
  protected
  Container buildGUI()
    throws Exception
  {
    JPanel jpResult = null;
    if(listOfJLabelText != null && listOfJLabelText.size() > 0) {
      jpResult = new JPanel(new GridLayout(listOfJLabelText.size() + 1, 1, 4, 4));
      for(int i = 0; i < listOfJLabelText.size(); i++) {
        Object oText = listOfJLabelText.get(i);
        JLabel jlabel = null;
        if(oText != null) {
          String sText = oText.toString();
          jlabel = new JLabel(sText, SwingConstants.CENTER);
          if(FMUtils.isPath(sText)) {
            jlabel.setForeground(Color.blue);
          }
        }
        else {
          jlabel = new JLabel();
        }
        jpResult.add(jlabel);
      }
    }
    else {
      jpResult = new JPanel(new GridLayout(2, 1, 4, 4));
      jpResult.add(new JLabel(getTitle(), SwingConstants.CENTER));
    }
    jpResult.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    
    jtfFileName = new JTextField();
    if(sDefault != null && sDefault.length() > 0) {
      jtfFileName.setText(sDefault);
      jtfFileName.setSelectionStart(0);
      jtfFileName.setSelectionEnd(sDefault.length());
    }
    jpResult.add(jtfFileName);
    
    return jpResult;
  }
  
  public
  boolean doCancel()
  {
    sResult = null;
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
    if(jtfFileName != null) {
      jtfFileName.requestFocus();
    }
  }
  
  public
  boolean doOk()
  {
    sResult = jtfFileName.getText();
    return true;
  }
}
