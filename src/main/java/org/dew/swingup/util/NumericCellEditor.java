package org.dew.swingup.util;

import java.awt.*;

import javax.swing.table.*;
import javax.swing.*;

import pv.jfcx.*;

/**
 * Implementazione di TableCellEditor per l'editing di campi numerici.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class NumericCellEditor extends AbstractCellEditor
implements TableCellEditor
{
  JPVNumeric oComponent;
  
  public
  NumericCellEditor()
  {
    super();
    oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(false);
    oComponent.setSelectAllOnDoubleClick(true);
    oComponent.setSelectionOnFocus(1);
    oComponent.setValueType(java.sql.Types.INTEGER);
  }
  
  public
  NumericCellEditor(boolean boEnableFloatPoint)
  {
    super();
    oComponent = new JPVNumeric();
    oComponent.setEnableFloatPoint(boEnableFloatPoint);
    oComponent.setSelectAllOnDoubleClick(true);
    oComponent.setSelectionOnFocus(1);
    if(boEnableFloatPoint) {
      oComponent.setValueType(java.sql.Types.DOUBLE);
    }
    else {
      oComponent.setValueType(java.sql.Types.INTEGER);
    }
  }
  
  public
  NumericCellEditor(int iMaxDigits, boolean boEnableFloatPoint)
  {
    super();
    oComponent = new JPVNumeric();
    oComponent.setMaxIntegers(iMaxDigits);
    oComponent.setEnableFloatPoint(boEnableFloatPoint);
    oComponent.setSelectAllOnDoubleClick(true);
    oComponent.setSelectionOnFocus(1);
    if(boEnableFloatPoint) {
      oComponent.setValueType(java.sql.Types.DOUBLE);
    }
    else {
      oComponent.setValueType(java.sql.Types.INTEGER);
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
