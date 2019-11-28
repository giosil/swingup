package org.dew.swingup.components;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JDialog;

import org.dew.swingup.*;

/**
 * Classe astratta per l'implementazione di una finestra di dialogo utilizzata
 * per la ricerca e selezione di entita'.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial"})
public abstract
class ALookUpDialog extends JDialog
{
  protected String sEntity;
  protected ILookUpFinder oLookUpFinder;
  protected Container oFilterContainer;
  protected List listDecodeListener;
  
  /**
   * Costruttore di default.
   */
  public ALookUpDialog()
  {
    this("Ricerca", null, null);
  }
  
  /**
   * Costruttore.
   *
   * @param oFilterContainer Container del filtro
   */
  public ALookUpDialog(Container oFilterContainer)
  {
    this(null, null, oFilterContainer);
  }
  
  /**
   * Costruttore.
   *
   * @param sTitle Titolo della finestra
   */
  public ALookUpDialog(String sTitle)
  {
    this(sTitle, null, null);
  }
  
  /**
   * Costruttore.
   *
   * @param sTitle Titolo della finestra
   * @param oFilterContainer Container del filtro
   */
  public ALookUpDialog(String sTitle, Container oFilterContainer)
  {
    this(sTitle, null, oFilterContainer);
  }
  
  /**
   * Costruttore.
   *
   * @param sTitle Titolo della finestra
   * @param sEntity Identificativo dell'entita'
   */
  public ALookUpDialog(String sTitle, String sEntity)
  {
    this(sTitle, sEntity, null);
  }
  
  /**
   * Costruttore.
   *
   * @param sTitle  Titolo della finestra
   * @param sEntity Identificativo dell'entita'
   * @param oFilterContainer Container del filtro
   */
  public ALookUpDialog(String sTitle, String sEntity, Container oFilterContainer)
  {
    super(ResourcesMgr.mainFrame, sTitle, true);
    
    this.sEntity = sEntity;
    this.oFilterContainer = oFilterContainer;
    
    try {
      init();
      pack();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di ALookUpDialog", ex);
    }
  }
  
  /**
   * Imposta l'identificativo dell'entita'.
   *
   * @param sEntity Identificativo dell'entita'
   */
  public
  void setEntity(String sEntity)
  {
    this.sEntity = sEntity;
  }
  
  /**
   * Restituisce l'identificativo dell'entita'.
   *
   * @return Identificativo dell'entita
   */
  public
  String getEntity()
  {
    return sEntity;
  }
  
  /**
   * Imposta il LookUpFinder che gestisce la ricerca vera e propria.
   *
   * @param oLookUpFinder Oggetto ILookUpFinder
   */
  public
  void setLookUpFinder(ILookUpFinder oLookUpFinder)
  {
    this.oLookUpFinder = oLookUpFinder;
  }
  
  /**
   * Imposta gli ascoltatori del componente decodificabile.
   *
   * @param oDecodeListeners List
   */
  public
  void setDecodeListeners(List oDecodeListeners)
  {
    this.listDecodeListener = oDecodeListeners;
  }
  
  /**
   * Restituisce il Container del filtro.
   *
   * @return Container
   */
  public
  Container getFilterContainer()
  {
    return oFilterContainer;
  }
  
  /**
   * Imposta i parametri di ricerca.
   *
   * @param oFilter Lista contenente i parametri di ricerca
   */
  public abstract
  void setFilter(List oFilter);
  
  /**
   * Imposta i record eventualmente proponibili prima di una ricerca.
   *
   * @param oRecords Lista contenente i record trovati
   */
  public abstract
  void setRecords(List oRecords);
  
  /**
   * Restituisce il record selezionato.
   *
   * @return Record selezionato
   */
  public abstract
  List getSelectedRecord();
  
  /**
   * Costruisce la GUI.
   *
   * @param oFilterContainer Eventuale container del filtro
   * @return Container
   * @throws Exception
   */
  protected abstract
  Container buildGUI(Container oFilterContainer)
    throws Exception;
  
  /**
   * Metodo invocato prima della chiusura della finestra di dialogo.
   * Se restituisce false la finestra non viene chiusa.
   *
   * @return Flag di chiusura
   */
  public
  boolean doClosing()
  {
    return true;
  }
  
  /**
   * Mostra la dialog impostando il filtro ed eventuali risultati disponibili.
   *
   * @param listFilter Lista valori di filtro
   * @param listResult Lista risultati disponibili
   */
  public
  void showMe(List listFilter, List listResult)
  {
    if(listFilter != null) setFilter(listFilter);
    if(listResult != null) setRecords(listResult);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(screenSize.width/2 - getSize().width/2,
      screenSize.height/2 - getSize().height/2);
    setVisible(true);
  }
  
  /**
   * Metodo invocato quando si attiva la finestra di dialogo.
   */
  protected
  void onActivated()
  {
  }
  
  /**
   * Metodo invocato quando si apre la finestra di dialogo.
   */
  protected
  void onOpened()
  {
  }
  
  /**
   * Inizializza la dialog.
   * @throws Exception
   */
  private
  void init()
    throws Exception
  {
    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        if(doClosing()) {
          dispose();
        }
      }
      public void windowOpened(WindowEvent we) {
        onOpened();
      }
      public void windowActivated(WindowEvent we) {
        onActivated();
      }
    });
    
    this.getContentPane().add(buildGUI(oFilterContainer));
  }
}
