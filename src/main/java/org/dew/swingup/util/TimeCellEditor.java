package org.dew.swingup.util;

import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;

import pv.jfcx.*;

/**
 * Implementazione di TableCellEditor per l'editing di campi ora.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class TimeCellEditor extends AbstractCellEditor implements TableCellEditor
{
  JPVTime oComponent;
  
  public
  TimeCellEditor()
  {
    super();
    
    oComponent = new JPVTime();
    oComponent.setTwelveHours(false);
    oComponent.setShowSeconds(false);
    oComponent.setLeadingZero(true);
    oComponent.setBlankForNull(true);
    oComponent.setTime(null);
    oComponent.setSelectionOnFocus(1);
    oComponent.setSelectAllOnDoubleClick(true);
  }
  
  public
  Object getCellEditorValue()
  {
    return oComponent.getValue();
  }
  
  public
  Component getTableCellEditorComponent(JTable oTable,
    Object oValue,
    boolean boSelected,
    int iRow,
    int iCol)
  {
    oComponent.setValue(oValue);
    return oComponent;
  }
}
