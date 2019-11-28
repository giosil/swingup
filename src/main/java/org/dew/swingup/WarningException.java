package org.dew.swingup;

/**
 * Eccezione utilizzata per mostrare un messaggio di warning.
 * In generale GUIMessage.showException visualizza l'eccezione ed i suoi
 * dettagli mostrando un messaggio di errore.
 * Utilizzando WarningException, GUIMessage.showException mostra un semplice
 * messaggio di warning.
 *
 * @version 1.0
 */
public
class WarningException extends Exception
{
  private static final long serialVersionUID = -4718326894634611099L;
  
  /**
   * Construttore con l'impostazione del messaggio.
   * @param sMessage String
   */
  public
  WarningException(String sMessage)
  {
    super(sMessage);
  }
}
