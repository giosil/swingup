package org.dew.swingup;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.dew.swingup.util.*;

/**
 * Classe astratta per la costruzione di un Report Dialog.
 *
 * @version 1.0
 */
public abstract
class AReportDialog extends JDialog implements IWorkObject
{
  public static final String sACTIONCOMMAND_PRINT  = "print";
  public static final String sACTIONCOMMAND_CANCEL = "cancel";
  
  protected JButton btnPrint;
  protected JButton btnCancel;
  
  protected boolean boCancel = true;
  
  /**
   * Costruttore di default. Richiama JDialog(Frame owner), ma non esegue
   * il metodo init. Tale costruttore e' utile per l'estensione personalizzata
   * di AReportDialog con parametri aggiuntivi.
   */
  public
  AReportDialog()
  {
    super(ResourcesMgr.mainFrame);
  }
  
  public
  AReportDialog(Frame frame, String title, boolean modal)
  {
    super(frame, title, modal);
    
    try {
      init();
      pack();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di AReportDialog", ex);
    }
  }
  
  public
  AReportDialog(String sTitle)
  {
    this(ResourcesMgr.mainFrame, sTitle, true);
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
   * Metodo invocato quando si clicca sul pulsante Stampa.
   *
   * @return Flag di chiusura
   */
  public abstract
  boolean doPrint();
  
  /**
   * Metodo invocato quando si clicca sul pulsante Annulla.
   *
   * @return Flag di chiusura
   */
  public abstract
  boolean doCancel();
  
  public
  boolean onClosing()
  {
    return true;
  }
  
  public
  boolean isCancel()
  {
    return boCancel;
  }
  
  /**
   * Metodo di stampa.
   */
  protected
  void firePrint()
  {
    boCancel = false;
    
    if(btnPrint != null)  btnPrint.setEnabled(false);
    if(btnCancel != null) btnCancel.setEnabled(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try{
          if(doPrint()) {
            dispose();
          }
        }
        finally {
          if(btnPrint != null)  btnPrint.setEnabled(true);
          if(btnCancel != null) btnCancel.setEnabled(true);
          setCursor(Cursor.getDefaultCursor());
        }
      }
    });
  }
  
  /**
   * Metodo di annullamento.
   */
  protected
  void fireCancel()
  {
    boCancel = true;
    if(doCancel()) {
      dispose();
    }
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
  
  protected
  void init()
    throws Exception
  {
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
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
    
    btnPrint = GUIUtil.buildActionButton(IConstants.sGUIDATA_PRINT,
      sACTIONCOMMAND_PRINT);
    btnPrint.setMargin(new Insets(2, 4, 2, 4));
    btnPrint.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        firePrint();
      }
    });
    btnPrint.setDefaultCapable(true);
    getRootPane().setDefaultButton(btnPrint);
    
    btnCancel = GUIUtil.buildActionButton(IConstants.sGUIDATA_CANCEL,
      sACTIONCOMMAND_CANCEL);
    btnCancel.setMargin(new Insets(2, 4, 2, 4));
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireCancel();
      }
    });
    
    jPanelButtons.add(btnPrint);
    JComponent jcAdditionalButton = buildAdditionalButton();
    if(jcAdditionalButton != null) {
      jPanelButtons.add(jcAdditionalButton);
    }
    jPanelButtons.add(btnCancel);
    
    oContentPane.add(buildGUI(), BorderLayout.CENTER);
    oContentPane.add(jPanelButtons, BorderLayout.SOUTH);
  }
}
