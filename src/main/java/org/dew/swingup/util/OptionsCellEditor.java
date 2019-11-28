package org.dew.swingup.util;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Implementazione di TableCellEditor per l'editing di campi testo tramite
 * una casella combinata.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class OptionsCellEditor extends AbstractCellEditor implements TableCellEditor
{
  JComboBox oComponent;
  
  public
  OptionsCellEditor()
  {
    super();
    oComponent = new JComboBox();
  }
  
  public
  OptionsCellEditor(Vector vItems)
  {
    super();
    oComponent = new JComboBox(vItems);
  }
  
  public
  OptionsCellEditor(Vector vItems, boolean boEditable)
  {
    super();
    oComponent = new JComboBox(vItems);
    oComponent.setEditable(boEditable);
  }
  
  public
  void setItems(List listItems)
  {
    if(listItems == null) return;
    
    oComponent.removeAllItems();
    for(int i = 0; i < listItems.size(); i++) {
      oComponent.addItem(listItems.get(i));
    }
  }
  
  public
  Object getCellEditorValue()
  {
    return oComponent.getSelectedItem();
  }
  
  public
  Component getTableCellEditorComponent(JTable oTable,
    Object oValue,
    boolean boSelected,
    int iRow,
    int iCol)
  {
    oComponent.setSelectedItem(oValue);
    return oComponent;
  }
}
