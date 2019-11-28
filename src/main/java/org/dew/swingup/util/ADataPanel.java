package org.dew.swingup.util;

import java.awt.*;
import javax.swing.*;

import org.dew.swingup.*;

/**
 * Classe astratta per la costruzione di un pannello contenente dati.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public abstract
class ADataPanel extends JPanel
{
  private FormPanel oParentFormPanel;
  
  /**
   * Costruttore. Esso invoca il metodo protetto init.
   */
  public
  ADataPanel()
  {
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di ADataPanel", ex);
    }
  }
  
  /**
   * Costruttore alternativo per l'estensione con parametri di costruzione
   * alternativi.
   *
   * @param boBuildGUI se false non viene eseguito il metodo protetto init.
   */
  public
  ADataPanel(boolean boBuildGUI)
  {
    if(boBuildGUI) {
      try {
        init();
      }
      catch(Exception ex) {
        GUIMessage.showException("Errore durante l'inizializzazione di ADataPanel", ex);
      }
    }
  }
  
  /**
   * Imposta il FormPanel che eventualmente contiene l'istanza di ADataPanel.
   *
   * @param oParentFormPanel FormPanel
   */
  public
  void setParentFormPanel(FormPanel oParentFormPanel)
  {
    this.oParentFormPanel = oParentFormPanel;
  }
  
  /**
   * Ottiene il FormPanel che eventualmente contiene l'istanza di ADataPanel.
   *
   * @return FormPanel
   */
  public
  FormPanel getParentFormPanel()
  {
    return oParentFormPanel;
  }
  
  /**
   * Metodo invocato per notificare che il FormPanel padre e' stato aggiornato.
   */
  public
  void onParentFormPanelChanged()
  {
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
   * Imposta i dati.
   *
   * @param oData Oggetto che contiene i dati
   */
  public abstract
  void setData(Object oData);
  
  /**
   * Restituisce i dati.
   *
   * @return Object
   */
  public abstract
  Object getData();
  
  /**
   * Imposta il contenuto.
   * L'implementazione predefinita richiama setData.
   *
   * @param oContent Object
   */
  public
  void setContent(Object oContent)
  {
    setData(oContent);
  }
  
  /**
   * Restituisce il contenuto.
   * L'implementazione predefinita richiama getData.
   *
   * @return Object
   */
  public
  Object getContent()
  {
    return getData();
  }
  
  /**
   * Imposta l'etichetta del pannello.
   *
   * @param sText String
   */
  public
  void setLabel(String sText)
  {
    if(sText != null) {
      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sText));
    }
    else {
      this.setBorder(BorderFactory.createEmptyBorder());
    }
  }
  
  /**
   * Inizializza il DataPanel.
   *
   * @throws Exception
   */
  protected
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());
    this.setDoubleBuffered(true);
    this.setOpaque(true);
    this.add(buildGUI(), BorderLayout.CENTER);
  }
}
