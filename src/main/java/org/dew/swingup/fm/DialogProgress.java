package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DialogProgress extends JDialog
{
  private static final long serialVersionUID = -8731399112686228586L;
  
  protected JTextArea jTextArea;
  protected JLabel jLabel;
  protected JProgressBar jProgressBar;
  protected boolean boClosable;
  protected boolean boAppendTextLabel = true;
  protected boolean boStopRequested = false;
  protected JButton btnStop;
  protected JButton btnClose;
  
  protected List listActionListener;
  protected Object oDataActionEvent;
  protected String sActionCommand;
  
  public
  DialogProgress(Frame parent, String sTitle)
  {
    super(parent, sTitle, false);
    try {
      init(true);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogProgress", ex);
    }
  }
  
  public
  DialogProgress(Frame parent, String sTitle, boolean boReport)
  {
    super(parent, sTitle, false);
    try {
      init(boReport);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogProgress", ex);
    }
  }
  
  public
  DialogProgress(JDialog parent, String sTitle)
  {
    super(parent, sTitle, false);
    try {
      init(true);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogProgress", ex);
    }
  }
  
  public
  DialogProgress(JDialog parent, String sTitle, boolean boReport)
  {
    super(parent, sTitle, false);
    try {
      init(boReport);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogProgress", ex);
    }
  }
  
  public
  void setActionCommand(String sActionCommand)
  {
    this.sActionCommand = sActionCommand;
  }
  
  public
  void setDataActionEvent(Object oDataActionEvent)
  {
    this.oDataActionEvent = oDataActionEvent;
  }
  
  public
  void addActionListener(ActionListener actionListener)
  {
    if(actionListener == null) return;
    if(listActionListener == null) {
      listActionListener = new ArrayList();
    }
    listActionListener.add(actionListener);
  }
  
  public
  void removeActionListener(ActionListener actionListener)
  {
    if(actionListener == null) return;
    if(listActionListener == null) return;
    listActionListener.remove(actionListener);
  }
  
  public
  void setClosable(boolean boClosable)
  {
    this.boClosable = boClosable;
    if(!boClosable) btnStop.setEnabled(true);
  }
  
  public
  boolean isClosable()
  {
    return boClosable;
  }
  
  public
  void append(String sText)
  {
    if(jTextArea != null) {
      jTextArea.append(sText);
    }
    if(jLabel != null) {
      String sTextLabel = "";
      if(boAppendTextLabel) {
        sTextLabel = jLabel.getText();
      }
      boAppendTextLabel = true;
      sTextLabel += sText;
      jLabel.setForeground(Color.black);
      jLabel.setText(sTextLabel);
    }
  }
  
  public
  void appendln(String sText)
  {
    if(jTextArea != null) {
      jTextArea.append(sText + '\n');
    }
    if(jLabel != null) {
      String sTextLabel = "";
      if(boAppendTextLabel) {
        sTextLabel = jLabel.getText();
      }
      sTextLabel += sText;
      jLabel.setForeground(Color.black);
      jLabel.setText(sTextLabel);
      boAppendTextLabel = false;
    }
  }
  
  public
  void showException(String sText, Exception ex)
  {
    if(jTextArea != null) {
      jTextArea.append(sText + ": ");
      String sMessage = ex.getMessage();
      if(sMessage == null || sMessage.length() == 0) sMessage = ex.toString();
      jTextArea.append(sMessage + '\n');
    }
    if(jLabel != null) {
      String sTextLabel = sText + ": ";
      String sMessage = ex.getMessage();
      if(sMessage == null || sMessage.length() == 0) sMessage = ex.toString();
      sTextLabel += sMessage;
      jLabel.setForeground(Color.red);
      jLabel.setText(sTextLabel);
    }
  }
  
  public
  void setValue(int iValue)
  {
    jProgressBar.setValue(iValue);
  }
  
  public
  boolean isStopRequested()
  {
    return boStopRequested;
  }
  
  protected
  void doStop()
  {
    boStopRequested = true;
    btnStop.setEnabled(false);
  }
  
  public
  void doClose()
  {
    if(boClosable) {
      dispose();
      if(listActionListener != null) {
        if(sActionCommand == null || sActionCommand.length() == 0) sActionCommand = "DialogProgress.close";
        if(oDataActionEvent == null) oDataActionEvent = this;
        ActionEvent actionEvent = new ActionEvent(oDataActionEvent, ActionEvent.ACTION_PERFORMED, sActionCommand);
        for(int i = 0; i < listActionListener.size(); i++) {
          ActionListener actionListener = (ActionListener) listActionListener.get(i);
          actionListener.actionPerformed(actionEvent);
        }
      }
    }
    else {
      GUIMessage.showWarning("Attendere la fine dell'elaborazione.");
    }
  }
  
  protected
  void init(boolean boReport)
  {
    boClosable = true;
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        doClose();
      }
      public void windowOpened(WindowEvent we) {}
      public void windowActivated(WindowEvent we) {}
    });
    
    JComponent jcCenter = null;
    if(boReport) {
      setSize(880, 600);
      jTextArea = new JTextArea(150, 50);
      Font fontMonospaced = new Font("Monospaced", jTextArea.getFont().getStyle(), jTextArea.getFont().getSize());
      jTextArea.setFont(fontMonospaced);
      jcCenter = new JScrollPane(jTextArea);
      jcCenter.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Report"));
    }
    else {
      setSize(800, 160);
      jLabel   = new JLabel("", JLabel.CENTER);
      jcCenter = jLabel;
      boAppendTextLabel = true;
    }
    
    btnStop = GUIUtil.buildActionButton("Stop|Stop|StopLarge.gif", "stop");
    btnStop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doStop();
      }
    });
    btnClose = GUIUtil.buildActionButton(IConstants.sGUIDATA_CLOSE, "close");
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doClose();
      }
    });
    JPanel jPanelButtons = new JPanel();
    jPanelButtons.add(btnStop);
    jPanelButtons.add(btnClose);
    getRootPane().setDefaultButton(btnClose);
    
    jProgressBar = new JProgressBar(0, 100);
    jProgressBar.setStringPainted(true);
    jProgressBar.setValue(0);
    
    JPanel jpSouth = new JPanel(new BorderLayout(4, 4));
    jpSouth.add(jProgressBar,  BorderLayout.NORTH);
    jpSouth.add(jPanelButtons, BorderLayout.SOUTH);
    
    JPanel jpanel = new JPanel(new BorderLayout(4, 4));
    jpanel.add(jcCenter, BorderLayout.CENTER);
    jpanel.add(jpSouth,  BorderLayout.SOUTH);
    jpanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    
    getContentPane().add(jpanel);
  }
}
