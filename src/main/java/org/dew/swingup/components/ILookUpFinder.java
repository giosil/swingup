package org.dew.swingup.components;

import java.util.List;

/**
 * Interfaccia ILookUpFinder per l'implementazione di un Finder specializzato
 * nella ricerca di entita' ad es. nei campi decodificabili.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public interface ILookUpFinder
{
  /**
   * Esegue una ricerca dell'entita identificata da sEntity e con i
   * parametri di ricerca impostati da oFilter.
   *
   * @param sEntity Identificativo della entita'.
   * @param oFilter Parametri di ricerca
   * @return        Risultato della ricerca (List di List).
   * @throws Exception Eccezione generata.
   */
  public List<Object> find(String sEntity, List<Object> oFilter) throws Exception;
}
