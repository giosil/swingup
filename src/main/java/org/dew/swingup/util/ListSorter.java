package org.dew.swingup.util;

import java.util.*;

/**
 * Classe di utilita' per l'ordinamento di liste contenenti oggetti complessi.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class ListSorter
{
  public static
  List reverse(List list)
  {
    List listResult = new ArrayList(list.size());
    for(int i=list.size()-1; i >= 0; i--) {
      listResult.add(list.get(i));
    }
    return listResult;
  }
  
  public static
  void sortListOfList(List listData, int iIndex)
  {
    if(listData == null || listData.size() < 2) return;
    int iFirst = 0;
    int iLast = listData.size() - 1;
    boolean boSorted = true;
    do {
      for(int i = iLast; i > iFirst; i--) {
        List l1 = (List) listData.get(i);
        List l2 = (List) listData.get(i - 1);
        Object o1  = l1.get(iIndex);
        Object o2  = l2.get(iIndex);
        boolean lt = false;
        if(o1 instanceof Comparable && o2 instanceof Comparable) {
          lt = ((Comparable) o1).compareTo((Comparable) o2) < 0;
        }
        else {
          lt = o1 == null && o2 != null;
        }
        if(lt) {
          listData.set(i,     l2);
          listData.set(i - 1, l1);
          boSorted = false;
        }
      }
      iFirst++;
    }
    while((iLast > iFirst) &&(!boSorted));
  }
  
  public static
  void sameSortListOfList(List listData1, List listData2, int iIndex)
  {
    if(listData1 == null || listData1.size() < 2) return;
    if(listData2 == null || listData2.size() < 2) return;
    for(int i = 0; i < listData1.size(); i++) {
      List l1 = (List) listData1.get(i);
      Object o1  = l1.get(iIndex);
      
      int k = -1;
      for(int j = 0; j < listData2.size(); j++) {
        List l2 = (List) listData2.get(j);
        Object o2  = l2.get(iIndex);
        if(o1 == null && o2 == null) {
          k = j;
          break;
        }
        else
        if(o1 != null && o1.equals(o2)) {
          k = j;
          break;
        }
      }
      
      if(k >= 0 && i < listData2.size()) {
        Object o_i = listData2.get(i);
        Object o_k = listData2.get(k);
        listData2.set(i, o_k);
        listData2.set(k, o_i);
      }
    }
  }
  
  public static
  void sortListOfMap(List listData, Object oKey)
  {
    if(listData == null || listData.size() < 2) return;
    int iFirst = 0;
    int iLast  = listData.size() - 1;
    boolean boSorted = true;
    do {
      for(int i = iLast; i > iFirst; i--) {
        Map m1 = (Map) listData.get(i);
        Map m2 = (Map) listData.get(i - 1);
        Object o1  = m1.get(oKey);
        Object o2  = m2.get(oKey);
        boolean lt = false;
        if(o1 instanceof Comparable && o2 instanceof Comparable) {
          lt = ((Comparable) o1).compareTo((Comparable) o2) < 0;
        }
        else {
          lt = o1 == null && o2 != null;
        }
        if(lt) {
          listData.set(i,   m2);
          listData.set(i-1, m1);
          boSorted = false;
        }
      }
      iFirst++;
    }
    while((iLast > iFirst) &&(!boSorted));
  }
  
  public static
  void sameSortListOfMap(List listData1, List listData2, Object oKey)
  {
    if(listData1 == null || listData1.size() < 2) return;
    if(listData2 == null || listData2.size() < 2) return;
    for(int i = 0; i < listData1.size(); i++) {
      Map m1 = (Map) listData1.get(i);
      Object o1  = m1.get(oKey);
      
      int k = -1;
      for(int j = 0; j < listData2.size(); j++) {
        Map m2 = (Map) listData2.get(j);
        Object o2  = m2.get(oKey);
        if(o1 == null && o2 == null) {
          k = j;
          break;
        }
        else
        if(o1 != null && o1.equals(o2)) {
          k = j;
          break;
        }
      }
      
      if(k >= 0 && i < listData2.size()) {
        Object o_i = listData2.get(i);
        Object o_k = listData2.get(k);
        listData2.set(i, o_k);
        listData2.set(k, o_i);
      }
    }
  }
}
