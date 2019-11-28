package org.dew.swingup;

import java.awt.Container;

import javax.swing.*;

/**
 * Classe astratta per la costruzione di una form generica.
 * Nell'estendere la classe, quando si vuole personalizzare l'operazione di
 * costruzione richiamare il costruttore di default e il metodo protetto init
 * responsabile della costruzione della GUI.
 *
 * @version 1.0
 */
public abstract
class AJInternalFrame extends JInternalFrame
implements IWorkObject
{
  /**
   * Costruttore di default. Non esegue il metodo init.
   * Tale costruttore e' utile per l'estensione personalizzata
   * di AJInternalFrame con parametri aggiuntivi.
   */
  public
  AJInternalFrame()
  {
    super();
  }
  
  public
  AJInternalFrame(String sTitle)
  {
    try {
      init(sTitle, null);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di AJInternalFrame", ex);
    }
  }
  
  public
  AJInternalFrame(String sTitle, String sIcon)
  {
    try {
      init(sTitle, sIcon);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di AJInternalFrame", ex);
    }
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
   * Esegue la chiusura del frame interno.
   */
  public
  void close()
  {
    ResourcesMgr.getWorkPanel().close(this);
  }
  
  /**
   * Inizializza il frame interno.
   *
   * @param sTitle Titolo
   * @param sIcon Icona
   * @throws Exception
   */
  protected
  void init(String sTitle, String sIcon)
    throws Exception
  {
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    if(sTitle == null) {
      this.setTitle("");
    }
    else {
      this.setTitle(sTitle);
    }
    this.setFrameIcon(ResourcesMgr.getImageIcon(sIcon));
    this.setResizable(true);
    this.setMaximizable(true);
    this.setIconifiable(true);
    this.setClosable(true);
    this.getContentPane().add(buildGUI());
  }
}
