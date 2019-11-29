package org.dew.swingup.util;

import java.util.List;

@SuppressWarnings("rawtypes")
public 
class FilterableTableModel extends SimpleTableModelForSorter
{
  private static final long serialVersionUID = 411955584216597171L;
  
  protected int[] aiVisibleColumns;
  
  public 
  FilterableTableModel(List oData, String[] asCOLUMNS, Class<?>[] acCOLUMNS_CLASSES) 
  {
    super(oData, asCOLUMNS, acCOLUMNS_CLASSES);
  }
  
  public
  void setVisibleColumns(int[] aiVisibleColumns)
  {
    this.aiVisibleColumns = aiVisibleColumns;
  }
  
  public
  int getColumnCount()
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) return super.getColumnCount();
    return aiVisibleColumns.length + 1;
  }
  
  public
  String getColumnName(int iCol)
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) return super.getColumnName(iCol);
    if(aiVisibleColumns.length <= iCol) return "";
    return super.getColumnName(aiVisibleColumns[iCol]);
  }
  
  public
  boolean isCellEditable(int iRow, int iCol)
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) return super.isCellEditable(iRow, iCol);
    if(aiVisibleColumns.length <= iCol) return false;
    return super.isCellEditable(iRow, aiVisibleColumns[iCol]);
  }
  
  public
  Class<?> getColumnClass(int iCol)
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) return super.getColumnClass(iCol);
    if(aiVisibleColumns.length <= iCol) return String.class;
    return super.getColumnClass(aiVisibleColumns[iCol]);
  }
  
  public
  Object getObjectAt(int iRow, int iCol)
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) return super.getObjectAt(iRow, iCol);
    if(aiVisibleColumns.length <= iCol) return "";
    return super.getObjectAt(iRow, aiVisibleColumns[iCol]);
  }
  
  public
  Object getValueAt(int iRow, int iCol)
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) return super.getValueAt(iRow, iCol);
    if(aiVisibleColumns.length <= iCol) return "";
    return super.getValueAt(iRow, aiVisibleColumns[iCol]);
  }
  
  public
  void setValueAt(Object oVal, int iRow, int iCol)
  {
    if(aiVisibleColumns == null || aiVisibleColumns.length == 0) {
      super.setValueAt(oVal, iRow, iCol);
      return;
    }
    if(aiVisibleColumns.length <= iCol) return;
    super.setValueAt(oVal, iRow, aiVisibleColumns[iCol]);
  }  
}
