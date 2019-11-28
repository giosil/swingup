package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.ListSelectionEvent;

import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.GUIMessage;

/**
 * Classe di utilita' per le tabelle (JTable).
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 */
@SuppressWarnings({"serial"})
public
class TableUtils
{
  /**
   * Imposta il font Monospaced alla tabella.
   *
   * @param oTable JTable
   */
  public static
  void setMonospacedFont(JTable oTable)
  {
    Font oFontMonospaced = new Font("Monospaced", oTable.getFont().getStyle(), oTable.getFont().getSize());
    oTable.setFont(DefaultGUIManager.deriveFontForHRScreen(oFontMonospaced));
    DefaultGUIManager.setRowHeightForHRScreen(oTable);
  }
  
  /**
   * Imposta il font Monospaced alla tabella.
   *
   * @param oTable JTable
   * @param iDeltaSize delta size
   */
  public static
  void setMonospacedFont(JTable oTable, int iDeltaSize)
  {
    Font oFontMonospaced = new Font("Monospaced", oTable.getFont().getStyle(), oTable.getFont().getSize() + iDeltaSize);
    oTable.setFont(DefaultGUIManager.deriveFontForHRScreen(oFontMonospaced));
    DefaultGUIManager.setRowHeightForHRScreen(oTable);
    if(iDeltaSize > 0) {
      int iRowHeight = oTable.getRowHeight();
      oTable.setRowHeight(iRowHeight + iDeltaSize);
    }
  }
  
  /**
   * Imposta il font Monospaced alla tabella.
   *
   * @param oTable JTable
   * @param iDeltaSize delta size
   * @param iStyle font style
   */
  public static
  void setMonospacedFont(JTable oTable, int iDeltaSize, int iStyle)
  {
    Font oFontMonospaced = new Font("Monospaced", iStyle, oTable.getFont().getSize() + iDeltaSize);
    oTable.setFont(DefaultGUIManager.deriveFontForHRScreen(oFontMonospaced));
    DefaultGUIManager.setRowHeightForHRScreen(oTable);
    if(iDeltaSize > 0) {
      int iRowHeight = oTable.getRowHeight();
      oTable.setRowHeight(iRowHeight + iDeltaSize);
    }
  }
  
  /**
   * Aggiorna una riga.
   *
   * @param oTable JTable
   * @param iRow Riga da aggiornare
   */
  public static
  void updateRow(JTable oTable, int iRow)
  {
    ListSelectionEvent lse = new ListSelectionEvent(oTable.getParent(), iRow, iRow, false);
    oTable.valueChanged(lse);
  }
  
  /**
   * Imposta i gestori predefiniti per le tabelle dei seguenti tasti:
   * VK_HOME, VK_END, VK_PAGE_UP, VK_PAGE_DOWN
   *
   * @param jtable JTable
   * @param jScrollPane JScrollPane
   */
  public static
  void putDefaultActions(JTable jtable, JScrollPane jScrollPane)
  {
    GUIUtil.putActionWhenFocused(jtable, KeyEvent.VK_HOME,      new DefaultAction(jtable, jScrollPane, DefaultAction.iHOME));
    GUIUtil.putActionWhenFocused(jtable, KeyEvent.VK_END,       new DefaultAction(jtable, jScrollPane, DefaultAction.iEND));
    GUIUtil.putActionWhenFocused(jtable, KeyEvent.VK_PAGE_UP,   new DefaultAction(jtable, jScrollPane, DefaultAction.iPAGEUP));
    GUIUtil.putActionWhenFocused(jtable, KeyEvent.VK_PAGE_DOWN, new DefaultAction(jtable, jScrollPane, DefaultAction.iPAGEDOWN));
  }
  
  /**
   * Rende i campi di una colonna dei link attivi.
   *
   * @param oTable JTable
   * @param iColumn Indice della colonna
   */
  public static
  void setLinkField(JTable oTable, int iColumn)
  {
    new LinkField(oTable, iColumn, null);
  }
  
  /**
   * Rende i campi di una colonna dei link attivi.
   *
   * @param oTable JTable
   * @param iColumn Indice della colonna
   * @param actionListener ActionListener (Se null viene implementato il comportamento predefinito)
   */
  public static
  void setLinkField(JTable oTable, int iColumn, ActionListener actionListener)
  {
    new LinkField(oTable, iColumn, actionListener);
  }
  
  /**
   * Rende i campi di una colonna dei link attivi.
   *
   * @param oTable JTable
   * @param iColumn Indice della colonna
   * @param actionListener ActionListener (Se null viene implementato il comportamento predefinito)
   * @param boRowColInActionCommand Flag che riporta Row,Col nell'ActionCommand
   */
  public static
  void setLinkField(JTable oTable, int iColumn, ActionListener actionListener, boolean boRowColInActionCommand)
  {
    new LinkField(oTable, iColumn, actionListener, boRowColInActionCommand);
  }
  
  public static
  JTable getJTableFromContainer(Container container)
  {
    JTable oResult = null;
    if(container == null) return null;
    Component[] components = container.getComponents();
    if(components == null) return null;
    for(int i = 0; i < components.length; i++) {
      Component component = components[i];
      if(component instanceof JTable) {
        return (JTable) component;
      }
      if(component instanceof JPanel) {
        oResult = getJTableFromContainer((JPanel) component);
        if(oResult != null) return oResult;
      }
      else
      if(component instanceof JScrollPane) {
        oResult = getJTableFromContainer((JScrollPane) component);
        if(oResult != null) return oResult;
      }
      else
      if(component instanceof JViewport) {
        oResult = getJTableFromContainer((JViewport) component);
        if(oResult != null) return oResult;
      }
    }
    return null;
  }
  
  public static
  void exportTableToFile(JTable oTable)
    throws Exception
  {
    JFileChooser oFileChooser = new JFileChooser();
    File oDefSelectedFile = new File("export.csv");
    oFileChooser.setSelectedFile(oDefSelectedFile);
    int iResult = oFileChooser.showSaveDialog(ResourcesMgr.mainFrame);
    if(iResult != JFileChooser.APPROVE_OPTION) return;
    exportTableToFile(oTable, oFileChooser.getSelectedFile().getAbsolutePath());
  }
  
  public static
  void exportTableToFile(JTable oTable, String sFilePath)
    throws Exception
  {
    TableModel tableModel = oTable.getModel();
    int iTableRowCount    = tableModel.getRowCount();
    int iTableColumnCount = tableModel.getColumnCount();
    FileOutputStream fos = null;
    PrintWriter pw = null;
    try {
      fos = new FileOutputStream(sFilePath, false);
      pw  = new PrintWriter(fos);
      StringBuffer sbRow = new StringBuffer();
      // Header
      for(int c = 0; c < iTableColumnCount; c++) {
        sbRow.append(tableModel.getColumnName(c));
        sbRow.append(';');
      }
      pw.println(sbRow);
      pw.flush();
      // Body
      for(int r = 0; r < iTableRowCount; r++) {
        sbRow = new StringBuffer();
        for(int c = 0; c < iTableColumnCount; c++) {
          String sValue = TableUtils.getStringValue(tableModel, r, c);
          sbRow.append(sValue);
          sbRow.append(';');
        }
        pw.println(sbRow);
        pw.flush();
      }
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {}
      if(pw  != null) try{ pw.close();  } catch(Exception ex) {}
    }
  }
  
  public static
  void exportTableToHTMLFile(JTable oTable)
    throws Exception
  {
    JFileChooser oFileChooser = new JFileChooser();
    File oDefSelectedFile = new File("export.html");
    oFileChooser.setSelectedFile(oDefSelectedFile);
    int iResult = oFileChooser.showSaveDialog(ResourcesMgr.mainFrame);
    if(iResult != JFileChooser.APPROVE_OPTION) return;
    exportTableToHTMLFile(oTable, oFileChooser.getSelectedFile().getAbsolutePath());
  }
  
  public static
  void exportTableToHTMLFile(JTable oTable, String sFilePath)
    throws Exception
  {
    TableModel tableModel = oTable.getModel();
    int iTableRowCount    = tableModel.getRowCount();
    int iTableColumnCount = tableModel.getColumnCount();
    FileOutputStream fos = null;
    PrintWriter pw = null;
    try {
      fos = new FileOutputStream(sFilePath, false);
      pw  = new PrintWriter(fos);
      pw.println("<html>");
      pw.println("<head>");
      pw.println("<title>Export</title>");
      pw.println("</head>");
      pw.println("<body>");
      pw.println("<table>");
      StringBuffer sbRow = new StringBuffer();
      // Header
      for(int c = 0; c < iTableColumnCount; c++) {
        sbRow.append("<th>" + tableModel.getColumnName(c) + "</th>");
      }
      pw.println("<tr bgcolor=\"#eeeeee\">");
      pw.println(sbRow);
      pw.println("</tr>");
      pw.flush();
      // Body
      for(int r = 0; r < iTableRowCount; r++) {
        sbRow = new StringBuffer();
        for(int c = 0; c < iTableColumnCount; c++) {
          String sValue = TableUtils.getStringValue(tableModel, r, c);
          sbRow.append("<td>" + sValue + "</td>");
        }
        pw.println("<tr>");
        pw.println(sbRow);
        pw.println("</tr>");
        pw.flush();
      }
      pw.println("</table>");
      pw.println("</body>");
      pw.println("</html>");
      pw.flush();
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {}
      if(pw  != null) try{ pw.close();  } catch(Exception ex) {}
    }
  }
  
  protected static
  String getStringValue(TableModel tableModel, int iRow, int iCol)
  {
    Object oValue = tableModel.getValueAt(iRow, iCol);
    if(oValue == null) {
      return "";
    }
    else
    if(oValue instanceof Date) {
      return formatDate((Date) oValue);
    }
    else
    if(oValue instanceof Calendar) {
      return formatCalendar((Calendar) oValue);
    }
    else
    if(oValue instanceof Boolean) {
      if(((Boolean) oValue).booleanValue()) {
        return "X";
      }
      else {
        return "";
      }
    }
    return oValue.toString();
  }
  
  protected static
  String formatDate(Date date)
  {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    String sYear  = String.valueOf(calendar.get(Calendar.YEAR));
    String sMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
    if(sMonth.length() < 2) sMonth = "0" + sMonth;
    String sDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    if(sDay.length() < 2) sDay = "0" + sDay;
    return sDay + "/" + sMonth + "/" + sYear;
  }
  
  protected static
  String formatCalendar(Calendar calendar)
  {
    String sYear = String.valueOf(calendar.get(Calendar.YEAR));
    String sMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
    if(sMonth.length() < 2) sMonth = "0" + sMonth;
    String sDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    if(sDay.length() < 2) sDay = "0" + sDay;
    return sDay + "/" + sMonth + "/" + sYear;
  }
  
  static class LinkField
  {
    protected JTable _oTable;
    protected int _iColumn;
    protected ActionListener _actionListener;
    protected boolean _boRowColInActionCommand = false;
    
    public
    LinkField(JTable jtable, int iColumn, ActionListener actionListener)
    {
      this(jtable, iColumn, actionListener, false);
    }
    
    public
    LinkField(JTable jtable, int iColumn, ActionListener actionListener, boolean boRowColInActionCommand)
    {
      this._oTable         = jtable;
      this._iColumn        = iColumn;
      this._actionListener = actionListener;
      this._boRowColInActionCommand = boRowColInActionCommand;
      
      _oTable.addMouseMotionListener(new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
          int iColumn = _oTable.columnAtPoint(e.getPoint());
          if(iColumn == _iColumn) {
            _oTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          }
          else {
            _oTable.setCursor(Cursor.getDefaultCursor());
          }
        }
      });
      
      if(_actionListener != null) {
        _oTable.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();
            int iColumn = _oTable.columnAtPoint(point);
            if(iColumn == _iColumn) {
              int iRow = _oTable.rowAtPoint(point);
              String sValue = null;
              if(_boRowColInActionCommand) {
                sValue = iRow + "," + iColumn;
              }
              else {
                Object oValue = _oTable.getValueAt(iRow, iColumn);
                if(oValue != null) sValue = oValue.toString();
              }
              ActionEvent ae = new ActionEvent(_oTable, ActionEvent.ACTION_PERFORMED, sValue);
              _actionListener.actionPerformed(ae);
            }
          }
        });
      }
      else {
        _oTable.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();
            int iColumn = _oTable.columnAtPoint(point);
            if(iColumn == _iColumn) {
              int iRow = _oTable.rowAtPoint(point);
              Object oValue = _oTable.getValueAt(iRow, iColumn);
              if(oValue == null) return;
              String sValue = oValue.toString();
              if(sValue.trim().length() == 0) return;
              if(sValue.indexOf('@') > 0) {
                try {
                  ResourcesMgr.openMailClent(sValue);
                }
                catch(Exception ex) {
                  GUIMessage.showException("Errore durante l'apertura del client di posta", ex);
                }
              }
              else {
                if(sValue.toLowerCase().indexOf("http") >= 0) {
                  try {
                    ResourcesMgr.openBrowser(sValue);
                  }
                  catch(Exception ex) {
                    GUIMessage.showException("Errore durante l'apertura del browser", ex);
                  }
                }
                else {
                  try {
                    ResourcesMgr.viewFile(sValue);
                  }
                  catch(Exception ex) {
                    GUIMessage.showException("Errore durante la visualizzazione dell'elemento selezionato", ex);
                  }
                }
              }
            }
          }
        });
      }
      
      String sColumnName = _oTable.getColumnName(_iColumn);
      if(sColumnName == null) return;
      DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
        public
        Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int iRow, int iCol)
        {
          super.getTableCellRendererComponent(table, value, isSelected, hasFocus, iRow, iCol);
          if(value != null) {
            this.setText("<html><a href=\"\">" + value + "</a></html>");
          }
          return this;
        }
      };
      _oTable.getColumn(sColumnName).setCellRenderer(dtcr);
    }
  }
  
  static class DefaultAction extends AbstractAction
  {
    protected JTable     jTable;
    protected JScrollBar jScrollBar;
    protected int iAction = 0;
    
    public static final int iHOME     = 0;
    public static final int iEND      = 1;
    public static final int iPAGEUP   = 2;
    public static final int iPAGEDOWN = 3;
    
    public
    DefaultAction(JTable jTable, JScrollPane jScrollPane, int iAction)
    {
      this.jTable = jTable;
      this.jScrollBar = jScrollPane.getVerticalScrollBar();
      this.iAction = iAction;
    }
    
    public
    void actionPerformed(ActionEvent e)
    {
      int iRowCount = jTable.getRowCount();
      int iRowToSelect = -1;
      int iMin = jScrollBar.getMinimum();
      int iMax = jScrollBar.getMaximum();
      int iDif = iMax-iMin;
      int iNewValue = iMin;
      switch(iAction) {
        case iHOME:
        iNewValue = iMin;
        iRowToSelect = iRowCount > 0 ? 0 : -1;
        break;
        case iEND:
        iNewValue = iMax;
        iRowToSelect = iRowCount > 0 ? iRowCount-1 : -1;
        break;
        case iPAGEUP:
        iNewValue = jScrollBar.getValue() - iMax / 10;
        if(iDif > 0 && iRowCount > 0) {
           iRowToSelect = (iRowCount *(iNewValue - iMin)) / iDif;
          if(iRowToSelect < 1) {
            iRowToSelect = 0;
          }
          else
          if(iRowToSelect < iRowCount - 1) {
            iRowToSelect = iRowToSelect + 1;
          }
        }
        break;
        case iPAGEDOWN:
        iNewValue = jScrollBar.getValue() + iMax / 10;
        if(iDif > 0 && iRowCount > 0) {
           iRowToSelect = (iRowCount *(iNewValue - iMin)) / iDif;
          if(iRowToSelect < iRowCount - 1) iRowToSelect = iRowToSelect + 1;
          if(iRowToSelect > iRowCount - 1) iRowToSelect = iRowCount - 1;
        }
        break;
        default:
        break;
      }
      jScrollBar.setValue(iNewValue);
      selectRow(iRowToSelect);
    }
    
    protected
    void selectRow(final int iRowToSelect)
    {
      if(iRowToSelect < 0) return;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          jTable.getSelectionModel().setSelectionInterval(iRowToSelect, iRowToSelect);
        }
      });
    }
  }
}
