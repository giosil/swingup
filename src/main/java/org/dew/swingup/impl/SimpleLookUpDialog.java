package org.dew.swingup.impl;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import pv.jfcx.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;
import org.dew.swingup.components.*;

/**
 * Implementazione di ALookUpDialog che costruisce una maschera di LookUp
 * costituita da un campo codice e un campo descrizione.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class SimpleLookUpDialog extends ALookUpDialog
implements ListSelectionListener
{
  protected List oRecords;
  protected List oSelectedRecord = null;
  
  private String sDateFormatPattern;
  private DateFormat df = ResourcesMgr.getDefaultDateFormat();
  
  protected FormPanel fpFilter;
  protected LookUpTableModel oTableModel;
  protected JScrollPane oScrollPane;
  protected JTable oTableRecords;
  protected JLabel jlCount;
  
  protected JButton btnSelect;
  protected JButton btnClose;
  protected JButton btnFind;
  protected JButton btnReset;
  
  boolean boFireFindOnActivated = false;
  
  public
  SimpleLookUpDialog()
  {
    super();
  }
  
  public
  SimpleLookUpDialog(FormPanel fpFilter)
  {
    super(fpFilter);
  }
  
  public
  SimpleLookUpDialog(String sTitle)
  {
    super(sTitle);
  }
  
  public
  SimpleLookUpDialog(String sTitle, FormPanel fpFilter)
  {
    super(sTitle, fpFilter);
  }
  
  public
  void setCase(int iCase)
  {
    fpFilter.setCase(iCase);
  }
  
  public
  void valueChanged(ListSelectionEvent e)
  {
    int iRow = oTableRecords.getSelectedRow();
    if(iRow < 0) {
      btnSelect.setEnabled(false);
    }
    else {
      btnSelect.setEnabled(true);
    }
  }
  
  public
  void setFireFindOnActivated(boolean boFireFindOnOpened)
  {
    this.boFireFindOnActivated = boFireFindOnOpened;
  }
  
  public
  boolean isFireFindOnActivated()
  {
    return boFireFindOnActivated;
  }
  
  /**
   * Metodo invocato prima della chiusura della finestra di dialogo.
   * Se restituisce false la finestra non viene chiusa.
   *
   * @return Flag di chiusura
   */
  public
  boolean doClosing()
  {
    return true;
  }
  
  /**
   * Metodo invocato quando si apre la finestra di dialogo.
   */
  protected
  void onOpened()
  {
    oSelectedRecord = null;
  }
  
  /**
   * Metodo invocato quando si attiva la finestra di dialogo.
   */
  protected
  void onActivated()
  {
    oSelectedRecord = null;
    
    if(boFireFindOnActivated) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if(fpFilter.isBlank()) {
            if(oRecords == null || oRecords.isEmpty()) {
              fireFind();
            }
          }
          else {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                if(oRecords != null && oRecords.size() > 0) {
                  oTableRecords.requestFocus();
                  oTableRecords.setRowSelectionInterval(0, 0);
                  oScrollPane.getVerticalScrollBar().setValue(0);
                }
              }
            });
          }
        }
      });
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if(oRecords != null && oRecords.size() > 0) {
            oTableRecords.requestFocus();
            oTableRecords.setRowSelectionInterval(0, 0);
            oScrollPane.getVerticalScrollBar().setValue(0);
          }
        }
      });
    }
  }
  
  /**
   * Imposta il pattern utilizzato per la visualizzazione delle date.
   *
   * @param sDateFormatPattern String
   */
  public
  void setDateFormatPattern(String sDateFormatPattern)
  {
    this.sDateFormatPattern = sDateFormatPattern;
    df = new SimpleDateFormat(sDateFormatPattern);
  }
  
  /**
   * Ottiene il pattern utilizzato per la visualizzazione delle date.
   *
   * @return String
   */
  public
  String getDateFormatPattern()
  {
    if(sDateFormatPattern == null) {
      return "dd/MM/yyyy";
    }
    
    return sDateFormatPattern;
  }
  
  public
  Container getFilterContainer()
  {
    return fpFilter;
  }
  
  protected
  Container buildGUI(Container oFilterContainer)
    throws Exception
  {
    if(oFilterContainer instanceof FormPanel) {
      this.fpFilter = (FormPanel) oFilterContainer;
    }
    else {
      this.fpFilter = buildDefaultFilterFormPanel();
    }
    
    JPanel oMainPanel = new JPanel();
    oMainPanel.setLayout(new BorderLayout());
    
    oTableModel = new LookUpTableModel(oRecords);
    
    oTableRecords = new JTable(oTableModel);
    oTableRecords.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTableRecords.setColumnSelectionAllowed(false);
    oTableRecords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    JPanel oResultPanel = new JPanel(new BorderLayout());
    oResultPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Risultato"));
    
    oScrollPane = new JScrollPane(oTableRecords);
    
    TableColumnResizer.setResizeColumnsListeners(oTableRecords);
    TableSorter.setSorterListener(oTableRecords);
    
    TableUtils.setMonospacedFont(oTableRecords);
    
    oTableRecords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    oTableRecords.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          onDoubleClick();
        }
      }
    });
    oTableRecords.getSelectionModel().addListSelectionListener(this);
    oTableRecords.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          onDoubleClick();
        }
        else
        if(e.getKeyChar() == '\t') {
          oTableRecords.transferFocus();
          e.consume();
        }
      }
    });
    oTableRecords.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      public
      Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int iRow, int iCol)
      {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, iRow, iCol);
        Object oRecord = oRecords.get(iRow);
        if(oRecord instanceof List) {
          List listRecord = (List) oRecord;
          if(listRecord.size() > 3) {
            Object oAddFieldValue = listRecord.get(listRecord.size() - 1);
            Color color = getColorOfAdditionalField(oAddFieldValue);
            this.setForeground(color);
            if(color.equals(Color.black)) {
              this.setFont(new Font(getFont().getName(),
                Font.PLAIN, getFont().getSize()));
            }
            else {
              this.setFont(new Font(getFont().getName(),
                Font.ITALIC, getFont().getSize()));
            }
          }
          else {
            this.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
          }
        }
        else {
          this.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        }
        return this;
      }
    });
    
    GUIUtil.putActionInFocusedWindow(oTableRecords, "ctrl F", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    
    TableUtils.putDefaultActions(oTableRecords, oScrollPane);
    
    jlCount = new JLabel();
    
    oResultPanel.add(oScrollPane, BorderLayout.CENTER);
    oResultPanel.add(jlCount, BorderLayout.SOUTH);
    
    JPanel oFilterPanel = new JPanel(new BorderLayout());
    oFilterPanel.add(fpFilter, BorderLayout.CENTER);
    oFilterPanel.add(buildGUIFilterActions(), BorderLayout.EAST);
    oMainPanel.add(oFilterPanel, BorderLayout.NORTH);
    oMainPanel.add(oResultPanel, BorderLayout.CENTER);
    oMainPanel.add(buildGUIActions(), BorderLayout.EAST);
    
    getRootPane().setDefaultButton(btnClose);
    
    return oMainPanel;
  }
  
  /**
   * Restituisce il colore della riga relativa alla tabella del risultato
   * di ricerca in funzione del campo aggiuntivo.
   *
   * @param object Object
   * @return Color
   */
  protected
  Color getColorOfAdditionalField(Object object)
  {
    if(object instanceof Boolean) {
      if(((Boolean) object).booleanValue()) {
        return Color.blue;
      }
      else {
        return Color.black;
      }
    }
    return Color.black;
  }
  
  protected
  Container buildGUIActions()
  {
    JPanel oButtonsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
    
    btnSelect = GUIUtil.buildActionButton(IConstants.sGUIDATA_SELECT, "select");
    btnSelect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onDoubleClick();
      }
    });
    btnSelect.setDefaultCapable(true);
    
    btnClose = GUIUtil.buildActionButton(IConstants.sGUIDATA_EXIT, "exit");
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(doClosing()) {
          dispose();
        }
      }
    });
    
    btnSelect.setEnabled(false);
    
    oButtonsPanel.add(btnSelect);
    oButtonsPanel.add(btnClose);
    
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEtchedBorder());
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    
    return oActionsPanel;
  }
  
  protected
  Container buildGUIFilterActions()
  {
    JPanel oButtonsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
    
    btnFind = GUIUtil.buildActionButton(IConstants.sGUIDATA_FIND, "find");
    btnFind.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fireFind();
      }
    });
    btnFind.setDefaultCapable(true);
    
    btnReset = GUIUtil.buildActionButton(IConstants.sGUIDATA_RESET, "reset");
    btnReset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        reset();
      }
    });
    
    oButtonsPanel.add(btnFind);
    oButtonsPanel.add(btnReset);
    
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 8));
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    
    return oActionsPanel;
  }
  
  protected
  FormPanel buildDefaultFilterFormPanel()
  {
    FormPanel fp = new FormPanel("Filtro") {
      public
      Component addTextField(String sId, String sLabel)
      {
        JPVEdit oComponent = new JPVEdit() {
          public void processKeyEvent(KeyEvent ke) {
            char cKey = ke.getKeyChar();
            if(ke.isAltDown()) {
              ke.setKeyChar('\0');
              if(cKey == KeyEvent.VK_ENTER) {
                doFind();
              }
              else
              if(cKey == KeyEvent.VK_DELETE ||
                cKey == KeyEvent.VK_SPACE) {
                reset();
              }
            }
            super.processKeyEvent(ke);
          }
        };
        oComponent.setSelectionOnFocus(1);
        oComponent.setSelectAllOnDoubleClick(true);
        oComponent.setCase(iCase);
        return addComponent(sId, sLabel, oComponent);
      }
    };
    fp.addRow();
    fp.addTextField("codice", "Codice");
    fp.addRow();
    fp.addTextField("descrizione", "Descrizione");
    fp.build();
    return fp;
  }
  
  public
  List getSelectedRecord()
  {
    return oSelectedRecord;
  }
  
  public
  void setFilter(List oFilter)
  {
    fpFilter.setListValues(oFilter);
    jlCount.setText("");
  }
  
  public
  void setRecords(List listRecords)
  {
    this.oRecords = listRecords;
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTableRecords);
    oTableRecords.requestFocus();
    if(oRecords != null) {
      if(oRecords.size() == 1) {
        jlCount.setText(oRecords.size() + " occorrenza trovata.");
      }
      else {
        jlCount.setText(oRecords.size() + " occorrenze trovate.");
      }
    }
  }
  
  protected
  void reset()
  {
    if(oRecords != null) oRecords.clear();
    oTableModel.notifyUpdates();
    TableSorter.resetHeader(oTableRecords);
    fpFilter.reset();
    fpFilter.requestFocus();
    btnSelect.setEnabled(false);
    jlCount.setText("");
  }
  
  protected
  void fireFind()
  {
    doFind();
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if(oRecords != null && oRecords.size() > 0) {
          oTableRecords.requestFocus();
          oTableRecords.setRowSelectionInterval(0, 0);
          oScrollPane.getVerticalScrollBar().setValue(0);
        }
      }
    });
  }
  
  protected
  void doFind()
  {
    jlCount.setText("");
    
    if(oLookUpFinder == null) {
      GUIMessage.showWarning(this, "Oggetto ILookUpFinder non impostato");
      return;
    }
    
    if(!checkFilter()) {
      GUIMessage.showWarning(this, "Campi non valorizzati correttamente");
      return;
    }
    
    btnFind.setEnabled(false);
    btnReset.setEnabled(false);
    btnSelect.setEnabled(false);
    btnClose.setEnabled(false);
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        List oFilter = fpFilter.getListValues();
        
        if(listDecodeListener != null) {
          for(int i = 0; i < listDecodeListener.size(); i++) {
            IDecodeListener oDecodeListener = (IDecodeListener) listDecodeListener.get(i);
            oDecodeListener.beforeFind(oFilter);
          }
        }
        
        List oResult = null;
        try{
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          oResult = oLookUpFinder.find(sEntity, oFilter);
          setRecords(oResult);
        }
        catch(Exception ex) {
          GUIMessage.showException("Errore durante la ricerca", ex);
          return;
        }
        finally {
          btnFind.setEnabled(true);
          btnReset.setEnabled(true);
          btnSelect.setEnabled(true);
          btnClose.setEnabled(true);
          setCursor(Cursor.getDefaultCursor());
        }
        
        if(oResult == null || oResult.size() == 0) {
          GUIMessage.showWarning("Nessun elemento trovato.");
        }
        
        btnSelect.setEnabled(false);
      }
    });
  }
  
  protected
  boolean checkFilter()
  {
    List oListId = fpFilter.getListId();
    
    for(int i = 0; i < oListId.size(); i++) {
      String sId = (String) oListId.get(i);
      Object oValue = fpFilter.getValue(sId);
      
      if(oValue instanceof String) {
        if(checkStringForSearch((String) oValue)) {
          return true;
        }
      }
      
      if(oValue != null) {
        return true;
      }
    }
    
    return false;
  }
  
  protected
  void onDoubleClick()
  {
    int iRow = oTableRecords.getSelectedRow();
    oSelectedRecord = (List) oRecords.get(iRow);
    dispose();
  }
  
  protected
  boolean checkStringForSearch(String sText)
  {
    if(sText != null) {
      for(int i = 0; i < sText.length(); i++) {
        char c = sText.charAt(i);
        if(c == '*') {
          // Caso particolare
          return true;
        }
        if(Character.isLetterOrDigit(c)) {
          return true;
        }
      }
    }
    return false;
  }
  
  class LookUpTableModel extends ATableModelForSorter
  {
    public LookUpTableModel(List oData)
    {
      super(oData);
    }
    
    public
    void notifyUpdates()
    {
      fireTableDataChanged();
    }
    
    public
    int getColumnCount()
    {
      return fpFilter.getListLabel().size();
    }
    
    public
    String getColumnName(int iCol)
    {
      String sResult = (String) fpFilter.getListLabel().get(iCol);
      if(sResult == null) {
        sResult = (String) fpFilter.getListId().get(iCol);
        if(sResult == null) return "";
      }
      return sResult;
    }
    
    public
    boolean isCellEditable(int iRow, int iCol)
    {
      return false;
    }
    
    public
    Class getColumnClass(int iCol)
    {
      return String.class;
    }
    
    public
    Object getObjectAt(int iRow, int iCol)
    {
      Object oResult = null;
      
      List oRecord = null;
      try {
        oRecord = (List) oData.get(iRow);
        if(oRecord == null ||(iCol + 1 >= oRecord.size())) {
          return null;
        }
        oResult = oRecord.get(iCol + 1);
      }
      catch(Exception ex) {
        GUIMessage.showException(ex);
      }
      
      return oResult;
    }
    
    public
    Object getValueAt(int iRow, int iCol)
    {
      Object oResult = null;
      
      List oRecord = null;
      try {
        oRecord = (List) oData.get(iRow);
        if(oRecord == null ||(iCol + 1 >= oRecord.size())) {
          return null;
        }
        oResult = oRecord.get(iCol + 1);
        if(oResult instanceof Date) {
          return df.format((Date) oResult);
        }
        else
        if(oResult instanceof Calendar) {
          return df.format(((Calendar) oResult).getTime());
        }
      }
      catch(Exception ex) {
        GUIMessage.showException(ex);
      }
      
      return oResult;
    }
    
    public
    void setValueAt(Object oVal, int iRow, int iCol)
    {
    }
  }
}
