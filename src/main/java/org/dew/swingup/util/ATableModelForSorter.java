package org.dew.swingup.util;

import java.util.*;
import javax.swing.table.*;

/**
 * Classe astratta per l'implementazione di un oggetto TableModel con
 * funzione di ordinamento.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public abstract
class ATableModelForSorter extends AbstractTableModel
{
  protected List oData;
  
  public
  ATableModelForSorter()
  {
    super();
  }
  
  public
  ATableModelForSorter(List oData)
  {
    super();
    setData(oData);
  }
  
  /**
   * Notifica un aggiornamento della lista.
   */
  public
  void notifyUpdates()
  {
    fireTableDataChanged();
  }
  
  /**
   * Imposta i dati.
   *
   * @param oData Dati
   */
  public
  void setData(List oData)
  {
    this.oData = oData;
    fireTableDataChanged();
  }
  
  /**
   * Ottiene i dati contenuti nel modello.
   *
   * @return Lista contenente i record.
   */
  public
  List getData()
  {
    return oData;
  }
  
  /**
   * Restituisce il numero di righe.
   *
   * @return int
   */
  public
  int getRowCount()
  {
    return (oData == null) ? 0 : oData.size();
  }
  
  /**
   * Restituisce l'istanza vera e propria del dato, a differenza di
   * getValueAt che restituisce il testo che lo rappresenta.
   *
   * @param iRow int
   * @param iCol int
   * @return Object
   */
  public abstract
  Object getObjectAt(int iRow, int iCol);
  
  /**
   * Ordina i dati rispetto alla colonna iCol e con ordinamento specificato
   * da iOrder.
   *
   * @param iCol Indice colonna
   * @param iOrder Tipo ordine (es. TableSorter.iASCENDING_ORDER)
   */
  public
  void sortData(int iCol, int iOrder)
  {
    int iFirst = 0;
    int iLast = oData.size() - 1;
    boolean boSorted = true;
    do {
      for(int i = iLast; i > iFirst; i--) {
        Object o1 = getObjectAt(i, iCol);
        Object o2 = getObjectAt(i - 1, iCol);
        
        boolean isToSwap = false;
        int compare = 0;
        if(o1 instanceof Comparable && o2 instanceof Comparable) {
          compare = ((Comparable) o1).compareTo((Comparable) o2);
        }
        else {
          if(o1 == null && o2 != null) {
            compare = -1;
          }
          else
          if(o1 != null && o2 == null) {
            compare = 1;
          }
        }
        if(iOrder == TableSorter.iASCENDING_ORDER) {
          if(compare < 0) {
            isToSwap = true;
          }
        }
        else {
          if(compare > 0) {
            isToSwap = true;
          }
        }
        
        if(isToSwap) {
          
          Object oRecord_i = oData.get(i);
          Object oRecord_j = oData.get(i - 1);
          
          oData.set(i, oRecord_j);
          oData.set(i - 1, oRecord_i);
          
          boSorted = false;
        }
      }
      iFirst++;
    }
    while((iLast > iFirst) &&(!boSorted));
  }
}
