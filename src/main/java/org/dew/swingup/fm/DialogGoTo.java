package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DialogGoTo extends AJDialog
{
  private static final long serialVersionUID = 1043900532790852054L;
  
  protected FMViewer fmViewer;
  protected JTextField jtfPath;
  protected JList jlistFavorites;
  protected List listFavorites;
  protected String sDefault;
  protected String sResult;
  
  public DialogGoTo(FMViewer fmViewer, List listFavorites, String sDefault)
  {
    super();
    setTitle("Go to path");
    setModal(true);
    try {
      this.fmViewer      = fmViewer;
      this.listFavorites = listFavorites;
      this.sDefault      = sDefault;
      init(false);
      setSize(780, 600);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogGoTo", ex);
    }
  }
  
  public DialogGoTo(JDialog jdialog, FMViewer fmViewer, List listFavorites, String sDefault)
  {
    super(jdialog);
    setTitle("Go to path");
    setModal(true);
    try {
      this.fmViewer      = fmViewer;
      this.listFavorites = listFavorites;
      this.sDefault      = sDefault;
      init(false);
      setSize(780, 600);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogGoTo", ex);
    }
  }
  
  public static
  String showMe(FMViewer fmViewer, List listFavorites, String sDefault)
  {
    DialogGoTo dialog = new DialogGoTo(fmViewer, listFavorites, sDefault);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public static
  String showMe(JDialog jdialog, FMViewer fmViewer, List listFavorites, String sDefault)
  {
    DialogGoTo dialog = new DialogGoTo(jdialog, fmViewer, listFavorites, sDefault);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    dialog.setVisible(true);
    if(dialog.isCancel()) {
      return null;
    }
    return dialog.getResult();
  }
  
  public String getResult() {
    return sResult;
  }
  
  protected
  Container buildGUI()
    throws Exception
  {
    String sMessage = "Specificare il percorso completo della cartella che si vuole visualizzare";
    if(listFavorites != null && listFavorites.size() > 0) {
      sMessage += " o sceglierlo dalla lista dei favoriti";
    }
    JPanel jpNorth = new JPanel(new GridLayout(2, 1, 4, 4));
    jpNorth.add(new JLabel(sMessage, SwingConstants.CENTER));
    jtfPath = new JTextField();
    if(sDefault != null && sDefault.length() > 0) {
      jtfPath.setText(sDefault);
      jtfPath.setSelectionStart(0);
      jtfPath.setSelectionEnd(sDefault.length());
    }
    jpNorth.add(jtfPath);
    
    jlistFavorites = new JList();
    jlistFavorites.setFont(new Font("Monospaced", Font.PLAIN, 12));
    jlistFavorites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    if(listFavorites != null && listFavorites.size() > 0) {
      jlistFavorites.setListData(new Vector(listFavorites));
    }
    jlistFavorites.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        String sSelectedPath = (String) jlistFavorites.getSelectedValue();
        if(sSelectedPath != null && sSelectedPath.length() > 0) {
          jtfPath.setText(sSelectedPath);
          jtfPath.setSelectionStart(0);
          jtfPath.setSelectionEnd(sSelectedPath.length());
        }
      }
    });
    jlistFavorites.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          String sSelectedPath = (String) jlistFavorites.getSelectedValue();
          if(sSelectedPath != null && sSelectedPath.length() > 0) {
            jtfPath.setText(sSelectedPath);
            jtfPath.setSelectionStart(0);
            jtfPath.setSelectionEnd(sSelectedPath.length());
          }
          fireOk();
        }
      }
      public void mouseReleased(MouseEvent e) {
        // isPopupTrigger potrebbe restiruire false in Linux
        if(e.isPopupTrigger() || e.getButton() != MouseEvent.BUTTON1) {
          if(listFavorites == null || listFavorites.size() == 0) return;
          String sSelectedPath = (String) jlistFavorites.getSelectedValue();
          if(sSelectedPath != null && sSelectedPath.length() > 0) {
            if(fmViewer != null) {
              if(fmViewer.removeFromFavorites(sSelectedPath)) {
                listFavorites.remove(jlistFavorites.getSelectedIndex());
                jlistFavorites.setListData(new Vector(listFavorites));
              }
              else {
                GUIMessage.showWarning("Path not removed from favorites.");
              }
            }
            else {
              listFavorites.remove(jlistFavorites.getSelectedIndex());
              jlistFavorites.setListData(new Vector(listFavorites));
            }
          }
        }
      }
    });
    jlistFavorites.setCellRenderer(new DefaultListCellRenderer() {
      private static final long serialVersionUID = 367010851669738580L;
      public Component getListCellRendererComponent(JList list, Object value, int row, boolean isSelected, boolean hasFocus) {
        super.getListCellRendererComponent(list, value, row, isSelected, hasFocus);
        setIcon(FMViewer.iconFolder);
        return this;
      }
    });
    String sBorderTitle = "Favorites (click right button to remove)";
    JScrollPane jScrollPane = new JScrollPane(jlistFavorites);
    jScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sBorderTitle));
    
    JPanel jpResult = new JPanel(new BorderLayout(4, 4));
    jpResult.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    jpResult.add(jpNorth,     BorderLayout.NORTH);
    jpResult.add(jScrollPane, BorderLayout.CENTER);
    
    return jpResult;
  }
  
  public
  boolean doCancel()
  {
    sResult = null;
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
    if(jtfPath != null) {
      jtfPath.requestFocus();
    }
  }
  
  public
  boolean doOk()
  {
    sResult = jtfPath.getText();
    return true;
  }
}

