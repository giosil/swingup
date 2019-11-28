package org.dew.swingup;

/**
 * Interfaccia da implementare opzionalmente per la gestione dei principali
 * eventi di un oggetto GUI mostrato con AWorkPanel.
 *
 * @version 1.0
 */
public
interface IWorkObject
{
  /**
   * Metodo invocato prima della chiusura del work object.
   * Se restituisce false il work object non viene chiuso.
   *
   * @return Flag di chiusura
   */
  public boolean onClosing();
  
  /**
   * Metodo invocato all'attivazione del work object.
   */
  public void onActivated();
  
  /**
   * Metodo invocato all'apertura del work object.
   */
  public void onOpened();
}
