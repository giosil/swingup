package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import org.dew.swingup.*;

/**
 * Barra di stato dell'applicazione.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class StatusBar extends JPanel
{
  protected String sPrevText;
  protected Icon oPrevIcon;
  protected User userLogged;
  protected boolean boIsConnected = false;
  protected boolean boLastTextIsWarning = false;
  
  protected JLabel jlAppStatus;
  protected JLabel jlClient;
  protected JLabel jlAppUser;
  protected JLabel jlConnected;
  protected JClockLabel jlClock;
  
  protected ImageIcon iconStatus;
  protected ImageIcon iconClient;
  protected ImageIcon iconUser;
  protected ImageIcon iconConnected;
  protected ImageIcon iconDisconnected;
  protected ImageIcon iconReady;
  protected ImageIcon iconWait;
  protected ImageIcon iconClock;
  
  protected boolean boShowAppStatus = true;
  protected boolean boShowClient = true;
  protected boolean boShowUser = true;
  protected boolean boShowConnected = true;
  protected boolean boShowClock = true;
  
  /**
   * Costruttore
   */
  public
  StatusBar()
  {
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Costruttore con maschera di flag per la visualizzazione delle componenti.
   *
   * @param sMaskShowFlags String
   */
  public
  StatusBar(String sMaskShowFlags)
  {
    try {
      setShowFlags(sMaskShowFlags);
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Restituisce il componente JLabel relativo al testo della status bar.
   *
   * @return JLabel
   */
  public
  JLabel getJLabelStatusText()
  {
    return jlAppStatus;
  }
  
  /**
   * Restituisce il componente JLabel relativo alla postazione.
   *
   * @return JLabel
   */
  public
  JLabel getJLabelClient()
  {
    return jlClient;
  }
  
  /**
   * Restituisce il componente JLabel relativo all'utente.
   *
   * @return JLabel
   */
  public
  JLabel getJLabelUser()
  {
    return jlAppUser;
  }
  
  /**
   * Restituisce il componente JLabel relativo allo stato di connessione.
   *
   * @return JLabel
   */
  public
  JLabel getJLabelConnection()
  {
    return jlConnected;
  }
  
  /**
   * Restituisce il componente JLabel relativo all'orologio.
   *
   * @return JLabel
   */
  public
  JLabel getJLabelClock()
  {
    return jlClock;
  }
  
  /**
   * Imposta lo stato di connessione al server.
   *
   * @param bConnected Flag connesso
   */
  public
  void setConnected(boolean bConnected)
  {
    this.boIsConnected = bConnected;
    
    if(!boShowConnected) return;
    
    if(bConnected) {
      jlConnected.setText(IConstants.sTEXT_CONNECTED);
      jlConnected.setIcon(iconConnected);
    }
    else {
      jlAppUser.setText("");
      jlAppUser.setToolTipText("Nessun utente loggato");
      userLogged = null;
      jlConnected.setText(IConstants.sTEXT_DISCONNECTED);
      jlConnected.setIcon(iconDisconnected);
    }
  }
  
  /**
   * Ritorna lo stato di connessione.
   *
   * @return true se connesso, false altrimenti.
   */
  public
  boolean isConnected()
  {
    return boIsConnected;
  }
  
  /**
   * Imposta l'utente loggato
   *
   * @param user Utente
   */
  public
  void setUser(User user)
  {
    userLogged = user;
    
    if(user == null) {
      jlAppUser.setText("");
      jlAppUser.setToolTipText("Nessun utente loggato");
      return;
    }
    
    String sUserName = user.getUserName();
    String sUser = sUserName;
    String sUserClass = user.getUserClass();
    if(sUserClass != null && sUserClass.trim().length() > 0) {
      sUser += " (" + sUserClass + ")";
    }
    if(sUser.length() > 14) {
      sUser = sUser.substring(0, 14) + "...";
    }
    
    jlAppUser.setText(sUser);
    jlAppUser.setToolTipText("Ruolo: " + user.getRole());
    jlClient.setText(user.getCurrentIdClient());
  }
  
  /**
   * Imposta lo stato precedente
   */
  public
  void setPreviousStatus()
  {
    if(!boShowAppStatus) return;
    
    update(sPrevText, oPrevIcon);
  }
  
  /**
   * Imposta il testo della barra di stato.
   *
   * @param sText Testo
   */
  public
  void setText(String sText)
  {
    if(!boShowAppStatus) return;
    
    if(boLastTextIsWarning) {
      sPrevText = IConstants.sTEXT_READY;
      oPrevIcon = iconReady;
    }
    else {
      sPrevText = jlAppStatus.getText();
      oPrevIcon = jlAppStatus.getIcon();
    }
    boLastTextIsWarning = false;
    
    String sStatusText = "";
    if(sText != null) {
      sStatusText = sText;
    }
    
    update(sStatusText, iconStatus);
  }
  
  /**
   * Imposta il testo e l'icona della barra di stato.
   *
   * @param sText Testo
   * @param oIcon Icona
   */
  public
  void setText(String sText, Icon oIcon)
  {
    if(!boShowAppStatus) return;
    
    if(boLastTextIsWarning) {
      sPrevText = IConstants.sTEXT_READY;
      oPrevIcon = iconReady;
    }
    else {
      sPrevText = jlAppStatus.getText();
      oPrevIcon = jlAppStatus.getIcon();
    }
    boLastTextIsWarning = false;
    
    String sStatusText = "";
    if(sText != null) {
      sStatusText = sText;
    }
    
    update(sStatusText, oIcon);
  }
  
  /**
   * Imposta il testo e l'icona della barra di stato.
   *
   * @param sText Testo
   * @param sIcon Icona
   */
  public
  void setText(String sText, String sIcon)
  {
    if(!boShowAppStatus) return;
    
    setText(sText, ResourcesMgr.getSmallImageIcon(sIcon));
  }
  
  /**
   * Imposta il testo con l'iconda di warning.
   *
   * @param sText Testo
   */
  public
  void setWarning(String sText)
  {
    if(!boShowAppStatus) return;
    
    setText(sText, ResourcesMgr.getSmallImageIcon(IConstants.sICON_WARNING));
    
    boLastTextIsWarning = true;
  }
  
  /**
   * Imposta il testo e l'icona di attesa.
   */
  public
  void setWait()
  {
    if(!boShowAppStatus) return;
    
    String sText = jlAppStatus.getText();
    if(!sText.equals(IConstants.sTEXT_WAIT)) {
      sPrevText = sText;
      oPrevIcon = jlAppStatus.getIcon();
    }
    
    update(IConstants.sTEXT_WAIT, iconWait);
  }
  
  /**
   * Imposta lo stato di pronto.
   *
   * @param boReady Flag pronto
   */
  public
  void setReady(boolean boReady)
  {
    if(!boShowAppStatus) return;
    
    if(boReady) {
      update(IConstants.sTEXT_READY, iconReady);
    }
    else {
      update(IConstants.sTEXT_NOTREADY, iconReady);
    }
  }
  
  /**
   * Mostra le informazioni dell'utente loggato.
   */
  public
  void showUserInfo()
  {
    if(userLogged == null) {
      return;
    }
    
    AWorkPanel oWorkPanel = ResourcesMgr.getWorkPanel();
    
    oWorkPanel.show(userLogged);
  }
  
  /**
   * Mostra la GUI delle opzioni.
   */
  public
  void showOptions()
  {
    try {
      ResourcesMgr.getGUIManager().showGUIOptions(ResourcesMgr.mainFrame);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * Mostra le informazioni di servizio (ad. esempio CDS).
   */
  public
  void showServiceInfo()
  {
    URL urlCDS = ResourcesMgr.getURLCDS();
    if(urlCDS != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {
            ResourcesMgr.getGUIManager().showGUICDS(ResourcesMgr.mainFrame);
          }
          catch(Exception ex) {
            GUIMessage.showException("Errore durante la visualizzazione della CDS", ex);
          }
        }
      });
    }
  }
  
  protected
  void update(final String sText, final Icon oIcon)
  {
    AutoLockTimer oALT = ResourcesMgr.getAutoLockTimer();
    oALT.reset();
    jlAppStatus.setText(sText);
    jlAppStatus.setIcon(oIcon);
    // Refresh GUI
    Graphics g = jlAppStatus.getGraphics();
    if(g != null) {
      Rectangle r = jlAppStatus.getBounds();
      g.clearRect(0, 0, r.width, r.height);
    }
    jlAppStatus.update(g);
  }
  
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout(4,4));
    
    iconStatus = ResourcesMgr.getSmallImageIcon(IConstants.sICON_HELP);
    iconClient = ResourcesMgr.getSmallImageIcon(IConstants.sICON_CLIENT);
    iconUser = ResourcesMgr.getSmallImageIcon(IConstants.sICON_USER);
    iconConnected = ResourcesMgr.getSmallImageIcon(IConstants.sICON_CONNECT);
    iconDisconnected = ResourcesMgr.getSmallImageIcon(IConstants.sICON_DISCONNECT);
    iconReady = ResourcesMgr.getSmallImageIcon(IConstants.sICON_READY);
    iconWait = ResourcesMgr.getSmallImageIcon(IConstants.sICON_WAIT);
    iconClock = ResourcesMgr.getSmallImageIcon(IConstants.sICON_CLOCK);
    
    jlAppStatus = new JLabel(iconStatus, 0);
    jlAppStatus.setHorizontalAlignment(SwingConstants.LEFT);
    jlAppStatus.setText("");
    jlAppStatus.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          showOptions();
        }
      }
    });
    
    int iColums = 0;
    iColums = iColums +(boShowClient ? 1 : 0);
    iColums = iColums +(boShowUser ? 1 : 0);
    iColums = iColums +(boShowConnected ? 1 : 0);
    iColums = iColums +(boShowClock ? 1 : 0);
    JPanel jpEAST = new JPanel(new GridLayout(1,iColums,4,4));
    
    jlClient = new JLabel(iconClient);
    jlClient.setText(IConstants.sTEXT_DISCONNECTED);
    jlClient.setHorizontalAlignment(SwingConstants.LEFT);
    JPanel jPanelClient = new JPanel(new BorderLayout(4,4));
    jPanelClient.setBorder(BorderFactory.createEtchedBorder());
    jPanelClient.add(jlClient, BorderLayout.CENTER);
    jPanelClient.add(new JPanel(), BorderLayout.EAST);
    
    jlAppUser = new JLabel(iconUser);
    jlAppUser.setText("");
    jlAppUser.setHorizontalAlignment(SwingConstants.LEFT);
    jlAppUser.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          showUserInfo();
        }
      }
    });
    JPanel jPanelUser = new JPanel(new BorderLayout(4,4));
    jPanelUser.setBorder(BorderFactory.createEtchedBorder());
    jPanelUser.add(jlAppUser, BorderLayout.CENTER);
    jPanelUser.add(new JPanel(), BorderLayout.EAST);
    
    jlConnected = new JLabel(iconDisconnected);
    jlConnected.setText(IConstants.sTEXT_DISCONNECTED);
    jlConnected.setHorizontalAlignment(SwingConstants.LEFT);
    jlConnected.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          showServiceInfo();
        }
      }
    });
    JPanel jPanelConnected = new JPanel(new BorderLayout(4,4));
    jPanelConnected.setBorder(BorderFactory.createEtchedBorder());
    jPanelConnected.add(jlConnected, BorderLayout.CENTER);
    jPanelConnected.add(new JPanel(), BorderLayout.EAST);
    
    jlClock = new JClockLabel(iconClock);
    jlClock.start();
    jlClock.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          if(!boIsConnected) return;
          try {
            ResourcesMgr.mainFrame.lock();
          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    });
    JPanel jPanelClock = new JPanel(new BorderLayout(4,4));
    jPanelClock.setBorder(BorderFactory.createEtchedBorder());
    jPanelClock.add(jlClock, BorderLayout.CENTER);
    jPanelClock.add(new JPanel(), BorderLayout.EAST);
    
    if(boShowClient) jpEAST.add(jPanelClient);
    if(boShowUser) jpEAST.add(jPanelUser);
    if(boShowConnected) jpEAST.add(jPanelConnected);
    if(boShowClock) jpEAST.add(jPanelClock);
    
    if(boShowAppStatus) {
      this.add(jlAppStatus, BorderLayout.CENTER);
    }
    
    if(boShowClient || boShowUser || boShowConnected || boShowClock) {
      this.add(jpEAST, BorderLayout.EAST);
    }
  }
  
  protected
  void setShowFlags(String sMask)
  {
    boolean[] abFlags = getArrayOfBoolean(sMask);
    
    if(abFlags.length >= 1) boShowAppStatus = abFlags[0];
    
    if(abFlags.length >= 2) boShowClient = abFlags[1];
    
    if(abFlags.length >= 3) boShowUser = abFlags[2];
    
    if(abFlags.length >= 4) boShowConnected = abFlags[3];
    
    if(abFlags.length >= 5) boShowClock = abFlags[4];
  }
  
  protected
  boolean[] getArrayOfBoolean(String sMask)
  {
    if(sMask == null) {
      boolean[] abResult = {};
      return abResult;
    }
    
    int iLength = sMask.length();
    boolean[] abResult = new boolean[iLength];
    for(int i = 0; i < iLength; i++) {
      char c = sMask.charAt(i);
      abResult[i] = (c == '1');
    }
    
    return abResult;
  }
}
