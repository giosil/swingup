package org.dew.swingup.util;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Estensione di DefaultTableCellRenderer per il rendering delle date.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class DateCellRender extends DefaultTableCellRenderer
{
  public
  DateCellRender()
  {
    super();
  }
  
  public
  Component getTableCellRendererComponent(JTable oTable, Object oValue,
    boolean boSelected, boolean boFocus,
    int iRow, int iCol)
  {
    return super.getTableCellRendererComponent(oTable,
      oValue,
      boSelected,
      boFocus,
      iRow, iCol);
  }
}
