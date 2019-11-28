package org.dew.swingup.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Classe che serve come 'renderer' della testata della colonna della tabella
 * secondo la quale si richiede il sort. La testata di questa colonna contiene il
 * nome della colonna e un imagine (triangoletto), che da' una percezione visiva
 * del tipo di ordinamento.
 *
 * @author V.Zylyftari
 * @version $Revision: 1 $
 */
public
class TableHeadRenderer extends DefaultTableCellRenderer
{
  public
  Component getTableCellRendererComponent(JTable table      ,
    Object value      ,
    boolean isSelected,
    boolean hasFocus  ,
    int row           ,
    int column)
  {
    if(table != null) {
      JTableHeader header = table.getTableHeader();
      if(header != null) {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
      }
    }
    if(value instanceof TextAndIcon) {
      setIcon(((TextAndIcon)value).oIcon);
      setText(((TextAndIcon)value).sText);
    }
    else {
      setText((value == null) ? "" : value.toString());
      setIcon(null);
    }
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    setHorizontalAlignment(JLabel.CENTER);
    return this;
  }
}
