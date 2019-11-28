package org.dew.swingup.util;

import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.*;

import org.dew.swingup.*;

/**
 * Classe che permette di gestire l'ordinamento di una JTable.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class TableSorter
{
  protected static final Icon oIconAscOrder  = ResourcesMgr.getImageIcon(IConstants.sICON_ASCE_ORDER);
  protected static final Icon oIconDescOrder = ResourcesMgr.getImageIcon(IConstants.sICON_DESC_ORDER);
  
  public static final int iASCENDING_ORDER  = 0;
  public static final int iDESCENDING_ORDER = 1;
  
  /**
   * Aggiunge un MouseListener sull'intestazione della tabella che provoca
   * un ordinamento dei dati per quella colonna.
   * Affinche' l'ordinamento abbia effetto occorre che la JTable abbia
   * un TableModel che derivi da ATableModelForSorter.
   *
   * @param oTable Tabella
   */
  public static
  void setSorterListener(final JTable oTable)
  {
    oTable.setColumnSelectionAllowed(false);
    JTableHeader oJTableHeader = oTable.getTableHeader();
    oJTableHeader.addMouseListener(new MouseHeaderListener(oTable));
  }
  
  /**
   * Aggiunge un MouseListener sull'intestazione della tabella che provoca
   * un ordinamento dei dati per quella colonna.
   * Attraverso l'oggetto ActionListener e' possibile intercettare l'evento di 
   * ordinamento.
   * L'ActionCommand e' cosi' composto: iColToOrder + "," + iOrder + "," + iColClicked.
   * Affinche' l'ordinamento abbia effetto occorre che la JTable abbia
   * un TableModel che derivi da ATableModelForSorter.
   *
   * @param oTable Tabella
   * @param actionListener Oggetto ActionListener 
   */
  public static
  void setSorterListener(final JTable oTable, final ActionListener actionListener)
  {
    oTable.setColumnSelectionAllowed(false);
    JTableHeader oJTableHeader = oTable.getTableHeader();
    oJTableHeader.addMouseListener(new MouseHeaderListener(oTable, actionListener));
  }
  
  /**
   * Elimina le icone di ordinamento sulle colonne e reimposta
   * l'ampiezza delle colonne.
   *
   * @param oTable Tabella
   */
  public static
  void resetHeader(final JTable oTable)
  {
    TableColumnModel oColumnModel = oTable.getColumnModel();
    int iColCount = oTable.getColumnCount();
    for(int i = 0; i < iColCount; i++) {
      TableColumn oTableColumn = oColumnModel.getColumn(i);
      oTableColumn.setHeaderValue(oTable.getColumnName(i));
      oTableColumn.setPreferredWidth(oTableColumn.getPreferredWidth() + 1);
    }
  }
  
  /**
   * Ordina una JTable rispetto alla colonna specificata da iCol.
   *
   * @param oTable         JTable da ordinare.
   * @param iCol           Colonna rispetto alla quale ordinare la JTable.
   * @param iOrder         Ordinamento.
   * @param iColClicked    Colonna sulla quale riportare l'icona.
   * @param boUpdateHeader Se true mostra le icone di ordinamento.
   */
  public static
  void sortTable(JTable oTable, int iCol, int iOrder, int iColClicked, boolean boUpdateHeader)
  {
    TableModel oTableModel = oTable.getModel();
    
    int iRowCount = oTableModel.getRowCount();
    if(iRowCount == 0) {
      return;
    }
    
    if(boUpdateHeader) {
      updateHeader(oTable, iColClicked, iOrder);
    }
    
    if(oTableModel instanceof ATableModelForSorter) {
      ((ATableModelForSorter) oTableModel).sortData(iCol, iOrder);
      ((ATableModelForSorter) oTableModel).fireTableDataChanged();
    }
  }
  
  /**
   * Ordina una JTable rispetto alla colonna specificata da iCol presente in sActionCommand.
   *
   * @param oTable         JTable da ordinare.
   * @param sActionCommand ActionCommand cosi' composto: iColToOrder + "," + iOrder + "," + iColClicked.
   * @param boUpdateHeader Se true mostra le icone di ordinamento.
   */
  public static
  void sortTable(JTable oTable, String sActionCommand, boolean boUpdateHeader)
  {
    if(sActionCommand == null || sActionCommand.length() == 0) {
      return;
    }
    int iSep1 = sActionCommand.indexOf(',');
    if(iSep1 < 0) return;
    int iSep2 = sActionCommand.indexOf(',', iSep1 + 1);
    if(iSep2 < 0) return;
    String sCol = sActionCommand.substring(0, iSep1);
    int iCol = 0;
    try {  iCol = Integer.parseInt(sCol.trim()); } catch(Exception ex) { return; }
    String sOrder = sActionCommand.substring(iSep1 + 1, iSep2);
    int iOrder = 0;
    try {  iOrder = Integer.parseInt(sOrder.trim()); } catch(Exception ex) { return; }
    String sColClicked = sActionCommand.substring(iSep2 + 1);
    int iColClicked = 0;
    try {  iColClicked = Integer.parseInt(sColClicked.trim()); } catch(Exception ex) { return; }
    
    TableModel oTableModel = oTable.getModel();
    
    int iRowCount = oTableModel.getRowCount();
    if(iRowCount == 0) {
      return;
    }
    if(iCol < 0 || iCol >= iRowCount) {
      return;
    }
    
    if(boUpdateHeader) {
      updateHeader(oTable, iColClicked, iOrder);
    }
    
    if(oTableModel instanceof ATableModelForSorter) {
      ((ATableModelForSorter) oTableModel).sortData(iCol, iOrder);
      ((ATableModelForSorter) oTableModel).fireTableDataChanged();
    }
  }
  
  /**
   * Ordina una JTable con ordine ascendente rispetto alla colonna specificata
   * da iCol.
   *
   * @param oTable Tabella
   * @param iCol Indice della colonna da ordinare
   */
  public static
  void sortTable(JTable oTable, int iCol)
  {
    sortTable(oTable, iCol, TableSorter.iASCENDING_ORDER, iCol, false);
  }
  
  protected static
  void updateHeader(JTable oTable, int iCol, int iOrder)
  {
    int iColumnCount = oTable.getColumnCount();
    
    for(int i = 0; i < iColumnCount; i++) {
      TableColumn oTableColumn = oTable.getColumnModel().getColumn(i);
      String sColumnName = oTable.getColumnName(i);
      
      if(i == iCol) {
        oTableColumn.setHeaderRenderer(new TableHeadRenderer());
        if(iOrder == iASCENDING_ORDER) {
          oTableColumn.setHeaderValue(new TextAndIcon(sColumnName, oIconAscOrder));
        }
        else {
          oTableColumn.setHeaderValue(new TextAndIcon(sColumnName, oIconDescOrder));
        }
      }
      else {
        oTableColumn.setHeaderValue(sColumnName);
      }
    }
    
    TableColumn oTableColumn = oTable.getColumnModel().getColumn(iCol);
    oTableColumn.setPreferredWidth(oTableColumn.getPreferredWidth() + 1);
  }
}
