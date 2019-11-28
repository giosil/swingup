package org.dew.swingup.editors;

/**
 * Ascoltatore di eventi scatenati da AEntityEditor.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public interface IEntityEditorListener
{
  /**
   * Invocato quando viene pulito il pannello delle occorrenze.
   */
  public void onClearSelection();
  
  /**
   * Invocato quando viene effettuata una ricerca.
   */
  public void onFind();
  
  /**
   * Invocato quando viene selezionato un elemento nel pannello delle occorrenze.
   */
  public void onSelection();
  
  /**
   * Invocato quando viene scelto un elemento nel pannello delle occorrenze.
   */
  public void onChoice();
  
  /**
   * Invocato quando si clicca sul tasto chiudi.
   */
  public void onExit();
  
  /**
   * Invocato DOPO la richiesta di inserire un elemento.
   */
  public void onNew();
  
  /**
   * Invocato DOPO la richiesta di modificare l'elemento.
   */
  public void onModify();
  
  /**
   * Invocato DOPO il salvataggio.
   * @param boNew true inserimento, false aggiornamento
   */
  public void onSave(boolean boNew);
  
  /**
   * Invocato DOPO la richiesta di annullamento delle modifiche.
   * @param boNew true inserimento, false aggiornamento
   */
  public void onCancel(boolean boNew);
  
  /**
   * Invocato DOPO l'eliminazione di un elemento.
   */
  public void onDelete();
}
