package org.dew.swingup.util;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.*;

import java.awt.event.*;

/**
 * Utilita' per addattare la larghezza delle colonne di una JTable in base a percentuali specificate.
 *
 * @author <a href="mailto:giorgio.giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 */
public
class TableColumnResizerPerc
{
  public static
  void setResizeColumnsListeners(final JTable jtable, final float[] columnWidthPercentage)
  {
    jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    JComponent parent = (JComponent) jtable.getParent();
    parent.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        normalizeColumnsSize(jtable, columnWidthPercentage);
      }
    });
    jtable.addAncestorListener(new AncestorListener() {
      public void ancestorAdded(AncestorEvent event) {
        normalizeColumnsSize(jtable, columnWidthPercentage);
      }
      public void ancestorRemoved(AncestorEvent event) {}
      public void ancestorMoved(AncestorEvent event) {}
    });
  }
  
  private static
  void normalizeColumnsSize(final JTable jtable, final float[] columnWidthPercentage) 
  {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        TableColumnModel tableColumnModel = jtable.getColumnModel();
        int width = jtable.getParent().getWidth();
        int columnCount = tableColumnModel.getColumnCount();
        int filledSpace = 0;
        for (int i = 0; i < columnCount; i++) {
          TableColumn tableColumn = tableColumnModel.getColumn(i);
          int preferredWidth = 0;
          if(i == columnCount-1) {
            preferredWidth = width - filledSpace;
          }
          else
          if(columnWidthPercentage != null && columnWidthPercentage.length > i) {
            preferredWidth = Math.round((columnWidthPercentage[i] * width) / 100.0f);
          }
          else {
            preferredWidth = width / columnCount;
          }
          filledSpace += preferredWidth;
          tableColumn.setPreferredWidth(preferredWidth);
        }
      }
    });
  }
}
