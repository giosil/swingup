package org.dew.swingup.util;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import pv.jfcx.*;

/**
 * Implementazione di TableCellEditor per l'editing di campi data.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class DateCellEditor extends AbstractCellEditor
implements TableCellEditor
{
  JPVDatePlus oComponent;
  
  public
  DateCellEditor()
  {
    super();
    
    oComponent = new JPVDatePlus();
    oComponent.setFormat(JPVDatePlus.DMY);
    JPVCalendar oCalendar = oComponent.getCalendarObject();
    oCalendar.setLocaleStrings(true);
  }
  
  public
  DateCellEditor(Date oLowerLimit, Date oUpperLimit)
  {
    super();
    
    oComponent = new JPVDatePlus();
    oComponent.setFormat(JPVDatePlus.DMY);
    JPVCalendar oCalendar = oComponent.getCalendarObject();
    oCalendar.setLocaleStrings(true);
    if(oLowerLimit != null) {
      oComponent.setLowerLimit(oLowerLimit);
      oComponent.getCalendarObject().setLowerLimit(oLowerLimit);
    }
    if(oUpperLimit != null) {
      oComponent.setUpperLimit(oUpperLimit);
      oComponent.getCalendarObject().setUpperLimit(oUpperLimit);
    }
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
