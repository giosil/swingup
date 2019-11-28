package org.dew.swingup.editors;

import org.dew.swingup.*;

/**
 * Interfaccia che estende IWorkObject da implementare per la gestione di
 * un entity editor.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public interface IEntityMgr extends IWorkObject
{
  /**
   * Inizializza Entity Manager.
   *
   * @param oEntityEditor AEntityEditor
   * @param sTitle Titolo
   * @param sIcon Icona
   */
  public void init(AEntityEditor oEntityEditor, String sTitle, String sIcon);
  
  /**
   * Inizializza Entity Manager.
   *
   * @param oEntityEditor AEntityEditor
   * @param sTitle Titolo
   * @param sIcon Icona
   * @param boAllowEditing Consente l'editing all'interno dell'AEntityEditor.
   */
  public void init(AEntityEditor oEntityEditor, String sTitle,
    String sIcon, boolean boAllowEditing);
  
  /**
   * Filtro iniziale.
   *
   * @param oValues Dati del filtro
   */
  public void setStartupFilterValues(Object oValues);
  
  /**
   * Se true viene lanciato AEntityEditor.fireFind() dopo l'apertura.
   *
   * @param boValue Flag
   */
  public void setFireFindAfterOpened(boolean boValue);
  
  /**
   * Restituisce il flag del FireFindAfterOpened.
   *
   * @return boolean
   */
  public boolean isFireFindAfterOpened();
  
  /**
   * Restituisce la scelta fatta dall'utente nel caso l'EntityEditor e'
   * visualizzato come Dialog di scelta.
   *
   * @return Object
   */
  public Object getChoice();
  
  /**
   * Metodo da implementare per la chiusura della GUI.
   */
  public void doExit();
}
