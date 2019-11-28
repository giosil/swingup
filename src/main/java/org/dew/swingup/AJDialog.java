package org.dew.swingup;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.dew.swingup.util.*;

/**
 * Classe astratta per la costruzione di una finestra di Dialogo generica.
 * Nell'estendere la classe, quando si vuole personalizzare l'operazione di
 * costruzione richiamare il costruttore di default e il metodo protetto init
 * responsabile della costruzione della GUI.
 *
 * @version 1.0
 */
public abstract
class AJDialog extends JDialog implements IWorkObject
{
  public static final String sACTIONCOMMAND_OK     = "ok";
  public static final String sACTIONCOMMAND_CANCEL = "cancel";
  
  protected JButton btnOk;
  protected JButton btnCancel;
  
  protected boolean boCancel = true;
  
  /**
   * Costruttore di default. Richiama JDialog(Frame owner), ma non esegue
   * il metodo init. Tale costruttore e' utile per l'estensione personalizzata
   * di AJDialog con parametri aggiuntivi.
   */
  public
  AJDialog()
  {
    super(ResourcesMgr.mainFrame);
  }
  
  /**
   * Richiama JDialog(Dialog owner), ma non esegue
   * il metodo init. Tale costruttore e' utile per l'estensione personalizzata
   * di AJDialog con parametri aggiuntivi.
   */
  public
  AJDialog(Dialog dialog)
  {
    super(dialog);
  }
  
  public
  AJDialog(Frame frame, String title, boolean modal, boolean boHideCancel)
  {
    super(frame, title, modal);
    
    try {
      init(boHideCancel);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di AJDialog", ex);
    }
  }
  
  public
  AJDialog(Frame frame, String title, boolean modal)
  {
    this(frame, title, modal, false);
  }
  
  public
  AJDialog(Dialog dialog, String title, boolean modal, boolean boHideCancel)
  {
    super(dialog, title, modal);
    
    try {
      init(boHideCancel);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di AJDialog", ex);
    }
  }
  
  public
  AJDialog(Dialog dialog, String title, boolean modal)
  {
    this(dialog, title, modal, false);
  }
  
  public
  AJDialog(String sTitle)
  {
    this(ResourcesMgr.mainFrame, sTitle, true, false);
  }
  
  public
  AJDialog(String sTitle, boolean boHideCancel)
  {
    this(ResourcesMgr.mainFrame, sTitle, true, boHideCancel);
  }
  
  /**
   * Costruisce la GUI.
   *
   * @return Container
   * @throws Exception
   */
  protected abstract
  Container buildGUI()
    throws Exception;
  
  /**
   * Metodo invocato quando si clicca sul pulsante OK.
   *
   * @return Flag di chiusura
   */
  public
  boolean doOk()
  {
    return true;
  }
  
  /**
   * Metodo invocato quando si clicca sul pulsante Annulla.
   *
   * @return Flag di chiusura
   */
  public
  boolean doCancel()
  {
    return true;
  }
  
  /**
   * Restituisce true se e' stato cliccato il pulsante Annulla.
   *
   * @return Flag di annullamento
   */
  public
  boolean isCancel()
  {
    return boCancel;
  }
  
  /**
   * Metodo invocato esclusivamente nella chiusura volontaria della dialog.
   *
   * @return Flag di chiusura
   */
  public
  boolean onClosing()
  {
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
  }
  
  public
  void setVisibleButtons(boolean boVisible)
  {
    if(btnOk     != null) btnOk.setVisible(boVisible);
    if(btnCancel != null) btnCancel.setVisible(boVisible);
  }
  
  /**
   * Metodo di conferma.
   */
  protected
  void fireOk()
  {
    boCancel = false;
    setEnabledButtons(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try{
          if(doOk()) dispose();
        }
        finally {
          setEnabledButtons(true);
          setCursor(Cursor.getDefaultCursor());
        }
      }
    });
  }
  
  protected
  void setEnabledButtons(boolean boEnabled)
  {
    if(btnOk     != null) btnOk.setEnabled(boEnabled);
    if(btnCancel != null) btnCancel.setEnabled(boEnabled);
  }
  
  /**
   * Metodo di annullamento.
   */
  protected
  void fireCancel()
  {
    boCancel = true;
    if(doCancel()) dispose();
  }
  
  /**
   * Metodo da implementare per l'introduzione di un pulsante addizionale.
   * Se si restituisce null (implementazione di default) restano
   * i pulsanti predefiniti.
   *
   * @return JComponent
   */
  protected
  JComponent buildAdditionalButton()
  {
    return null;
  }
  
  /**
   * Inizializza il Dialog.
   *
   * @param boHideCancel Flag per nascondere il tasto Annulla
   * @throws Exception
   */
  protected
  void init(boolean boHideCancel)
    throws Exception
  {
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        if(!onClosing()) return;
        fireCancel();
      }
      public void windowOpened(WindowEvent we) {
        onOpened();
      }
      public void windowActivated(WindowEvent we) {
        onActivated();
      }
    });
    
    Container oContentPane = this.getContentPane();
    oContentPane.setLayout(new BorderLayout());
    
    JPanel jPanelButtons = new JPanel();
    
    JComponent jcAdditionalButton = buildAdditionalButton();
    if(jcAdditionalButton != null) {
      jPanelButtons.add(jcAdditionalButton);
    }
    
    btnOk = GUIUtil.buildActionButton(IConstants.sGUIDATA_OK, sACTIONCOMMAND_OK);
    btnOk.setMargin(new Insets(2, 4, 2, 4));
    btnOk.setDefaultCapable(true);
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireOk();
      }
    });
    jPanelButtons.add(btnOk);
    getRootPane().setDefaultButton(btnOk);
    
    if(!boHideCancel) {
      btnCancel = GUIUtil.buildActionButton(IConstants.sGUIDATA_CANCEL, sACTIONCOMMAND_CANCEL);
      btnCancel.setMargin(new Insets(2, 4, 2, 4));
      btnCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireCancel();
        }
      });
      jPanelButtons.add(btnCancel);
    }
    
    oContentPane.add(buildGUI(), BorderLayout.CENTER);
    oContentPane.add(jPanelButtons, BorderLayout.SOUTH);
    
    pack();
  }
}
