package org.dew.swingup.util;

import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;

import pv.jfcx.*;

/**
 * Implementazione di TableCellEditor per l'editing di campi testo.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class TextCellEditor extends AbstractCellEditor implements TableCellEditor
{
  JPVEdit oComponent;
  
  public
  TextCellEditor()
  {
    super();
    oComponent = new JPVEdit();
    oComponent.setSelectAllOnDoubleClick(true);
    oComponent.setSelectionOnFocus(1);
  }
  
  public
  TextCellEditor(int iMaxLength)
  {
    super();
    oComponent = new JPVEdit();
    oComponent.setMaxLength(iMaxLength);
    oComponent.setSelectAllOnDoubleClick(true);
    oComponent.setSelectionOnFocus(1);
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
