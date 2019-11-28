package org.dew.swingup.impl;

import java.awt.event.*;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Tale estensione di JToolBar rappresenta la toolbar predefinita di swingup.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class DefaultToolBar extends JToolBar implements ActionListener
{
  protected JButton btnConnect;
  protected JButton btnDisconnect;
  protected JButton btnLock;
  protected JButton btnExit;
  
  public
  DefaultToolBar()
  {
    super();
    init();
  }
  
  /**
   * Gestore eventi.
   *
   * @param e
   */
  public
  void actionPerformed(ActionEvent e)
  {
    String sAC = e.getActionCommand();
    if(sAC.equals(IConstants.sAC_TOOLBAR_CONNECT)) {
      ResourcesMgr.mainFrame.connect();
    }
    else
    if(sAC.equals(IConstants.sAC_TOOLBAR_DISCONNECT)) {
      ResourcesMgr.mainFrame.disconnect();
    }
    else
    if(sAC.equals(IConstants.sAC_TOOLBAR_LOCK)) {
      ResourcesMgr.mainFrame.lock();
    }
    else
    if(sAC.equals(IConstants.sAC_TOOLBAR_EXIT)) {
      ResourcesMgr.mainFrame.exit();
    }
  }
  
  protected
  void beforeAddDefaultButtons()
  {
  }
  
  protected
  void afterAddDefaultButtons()
  {
  }
  
  protected
  void init()
  {
    btnConnect = GUIUtil.buildActionButton(IConstants.sGUIDATA_CONNECT, IConstants.sAC_TOOLBAR_CONNECT);
    btnConnect.addActionListener(this);
    btnConnect.addMouseListener(new Status_MouseListener("Apre la connessione al server"));
    btnConnect.setEnabled(false);
    
    btnDisconnect = GUIUtil.buildActionButton(IConstants.sGUIDATA_DISCONN, IConstants.sAC_TOOLBAR_DISCONNECT);
    btnDisconnect.addActionListener(this);
    btnDisconnect.addMouseListener(new Status_MouseListener("Chiude la connessione al server"));
    
    btnLock = GUIUtil.buildActionButton(IConstants.sGUIDATA_LOCK, IConstants.sAC_TOOLBAR_LOCK);
    btnLock.addActionListener(this);
    btnLock.addMouseListener(new Status_MouseListener("Blocca l'applicazione"));
    btnLock.setEnabled(false);
    
    btnExit = GUIUtil.buildActionButton(IConstants.sGUIDATA_APPEXIT, IConstants.sAC_TOOLBAR_EXIT);
    btnExit.addActionListener(this);
    btnExit.addMouseListener(new Status_MouseListener("Esce dall'applicazione"));
    
    beforeAddDefaultButtons();
    
    this.add(btnConnect);
    this.add(btnDisconnect);
    this.add(btnLock);
    this.add(btnExit);
    
    afterAddDefaultButtons();
  }
}
