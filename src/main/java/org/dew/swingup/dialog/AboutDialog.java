package org.dew.swingup.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.impl.DefaultGUIManager;
import org.dew.swingup.util.*;

/**
 * Dialogo di About.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class AboutDialog extends JDialog implements ActionListener
{
  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton btnOK;
  JLabel imageLabel = new JLabel();
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  ImageIcon appIcon = new ImageIcon();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  String product = "";
  String version = "";
  String copyright = "";
  String comments = "";
  
  public
  AboutDialog(Frame parent,
    String sProduct, String sVersion, String sCopyright, String sComments,
    ImageIcon appIcon)
  {
    super(parent);
    
    this.product   = sProduct;
    this.version   = sVersion;
    this.copyright = sCopyright;
    this.comments  = sComments;
    this.appIcon   = appIcon;
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    if(e.getSource() == btnOK) cancel();
  }
  
  /**
   * Mostra la finestra di dialogo contenente le informazioni dell'applicazione.
   *
   * @param jframe Frame proprietario
   */
  public static
  void showMe(JFrame jframe)
  {
    String sAppName      = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_NAME);
    String sAppVers      = "ver. " + ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_VERSION);
    String sCopyRight    = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_COPYRIGHT);
    String sDescription  = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_DESCRIPTION);
    JDialog aboutDialog  = new AboutDialog(jframe, sAppName, sAppVers, sCopyRight, sDescription, ResourcesMgr.getAppIcon(false));
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    aboutDialog.setLocation(screenSize.width/2 - aboutDialog.getSize().width/2,
      screenSize.height/2 - aboutDialog.getSize().height/2);
    aboutDialog.setVisible(true);
  }
  
  protected
  void init()
    throws Exception
  {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    
    this.setModal(true);
    this.setTitle("Informazioni");
    this.setSize(new Dimension(600, 500));
    DefaultGUIManager.resizeForHRScreen(this);
    this.setResizable(true);
    imageLabel.setIcon(appIcon);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    
    label1.setText(product);
    Font oFont = new Font(label1.getFont().getName(), label1.getFont().getStyle(), label1.getFont().getSize() + 4);
    label1.setFont(oFont);
    
    label2.setText(version);
    label3.setText(copyright);
    label4.setText(comments);
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
    btnOK = GUIUtil.buildActionButton(IConstants.sGUIDATA_OK, "ok");
    btnOK.addActionListener(this);
    btnOK.setDefaultCapable(true);
    getRootPane().setDefaultButton(btnOK);
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(btnOK, null);
    
    panel1.add(panel2, BorderLayout.NORTH);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(buildInfoContainer(), BorderLayout.CENTER);
  }
  
  protected
  void processWindowEvent(WindowEvent e)
  {
    if(e.getID() == WindowEvent.WINDOW_CLOSING) cancel();
    super.processWindowEvent(e);
  }
  
  protected
  void cancel()
  {
    dispose();
  }
  
  protected
  Container buildInfoContainer()
  {
    JTabbedPane oTabbedPane = new JTabbedPane();
    
    JTable oConfTable = buildConfigurationTable();
    JScrollPane oConfSP = new JScrollPane(oConfTable);
    TableColumnResizer.setResizeColumnsListeners(oConfTable);
    TableSorter.setSorterListener(oConfTable);
    TableSorter.sortTable(oConfTable, 0);
    
    JTable oSysTable = buildSystemTable();
    JScrollPane oSysSP = new JScrollPane(oSysTable);
    TableColumnResizer.setResizeColumnsListeners(oSysTable);
    TableSorter.setSorterListener(oSysTable);
    TableSorter.sortTable(oSysTable, 0);
    
    oTabbedPane.add("Configurazione", oConfSP);
    oTabbedPane.add("Sistema", oSysSP);
    
    return oTabbedPane;
  }
  
  protected
  JTable buildSystemTable()
  {
    String[] asCOLUMNS   = {"Chiave", "Valore"};
    String[] asSYMBOLICS = {"key",    "value"};
    
    List oEntries = new ArrayList();
    Iterator oIterator = System.getProperties().keySet().iterator();
    while(oIterator.hasNext()) {
      String sKey = (String) oIterator.next();
      String sValue = System.getProperty(sKey);
      Map oRecord = new HashMap();
      oRecord.put("key", sKey);
      oRecord.put("value", sValue);
      oEntries.add(oRecord);
    }
    
    SimpleTableModelForSorter oTableModel = new SimpleTableModelForSorter(oEntries, asCOLUMNS, asSYMBOLICS);
    JTable oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    TableUtils.setMonospacedFont(oTable);
    oTable.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          cancel();
        }
      }
    });
    
    return oTable;
  }
  
  protected
  JTable buildConfigurationTable()
  {
    String[] asCOLUMNS   = {"Chiave", "Valore"};
    String[] asSYMBOLICS = {"key",    "value"};
    
    List oEntries = new ArrayList();
    Iterator oIterator = ResourcesMgr.config.keySet().iterator();
    while(oIterator.hasNext()) {
      String sKey = (String) oIterator.next();
      String sValue = ResourcesMgr.config.getProperty(sKey);
      Map oRecord = new HashMap();
      oRecord.put("key", sKey);
      oRecord.put("value", sValue);
      oEntries.add(oRecord);
    }
    
    SimpleTableModelForSorter oTableModel = new SimpleTableModelForSorter(oEntries, asCOLUMNS, asSYMBOLICS);
    JTable oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    TableUtils.setMonospacedFont(oTable);
    oTable.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          cancel();
        }
      }
    });
    
    return oTable;
  }
}
