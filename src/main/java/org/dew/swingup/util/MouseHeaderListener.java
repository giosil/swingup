package org.dew.swingup.util;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * Classe che estende MouseAdapter impiegata per la gestione dell'ordinamento di una JTable.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class MouseHeaderListener extends MouseAdapter
{
  protected JTable oTable;
  protected int[] aiPrevOrder;
  protected Map mapColoumNameIndex = new HashMap();
  protected ActionListener actionListener;
  
  public
  MouseHeaderListener(JTable oTable)
  {
    super();
    
    this.oTable = oTable;
    
    int iColCount = oTable.getColumnCount();
    aiPrevOrder = new int[iColCount];
    for(int i = 0; i < iColCount; i++) {
      String sColumnName = oTable.getColumnName(i);
      mapColoumNameIndex.put(sColumnName, new Integer(i));
      aiPrevOrder[i] = TableSorter.iDESCENDING_ORDER;
    }
  }
  
  public
  MouseHeaderListener(JTable oTable, ActionListener actionListener)
  {
    this(oTable);
    this.actionListener = actionListener;
  }
  
  public
  void mouseClicked(MouseEvent e)
  {
    if(!oTable.isEnabled()) return;
    
    int iOrder = TableSorter.iASCENDING_ORDER;
    int iColClicked = oTable.columnAtPoint(e.getPoint());
    String sColumnName = oTable.getColumnName(iColClicked);
    
    int iColToOrder = ((Integer) mapColoumNameIndex.get(sColumnName)).intValue();
    
    if(aiPrevOrder[iColToOrder] == TableSorter.iASCENDING_ORDER) {
      iOrder = TableSorter.iDESCENDING_ORDER;
    }
    setPrevOrder(iColToOrder, iOrder);
    
    TableSorter.sortTable(oTable, iColToOrder, iOrder, iColClicked, true);
    
    if(actionListener != null) {
      String command = iColToOrder + "," + iOrder + "," + iColClicked;
      ActionEvent actionEvent = new ActionEvent(e.getSource(), e.getID(), command);
      actionListener.actionPerformed(actionEvent);
    }
  }
  
  protected
  void setPrevOrder(int iIndex, int iOrder)
  {
    for(int i = 0; i < aiPrevOrder.length; i++) {
      if(i == iIndex) {
        aiPrevOrder[i] = iOrder;
      }
      else {
        aiPrevOrder[i] = TableSorter.iDESCENDING_ORDER;
      }
    }
  }
}
