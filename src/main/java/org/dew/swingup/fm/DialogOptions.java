package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DialogOptions extends AJDialog
{
  private static final long serialVersionUID = 2954256836069129546L;
  
  protected JList jList;
  protected JCheckBox[] arrayOfCheckBox;
  protected Map mapItems;
  protected List oValues;
  protected Object oSelectedValue;
  protected String sBorderTitle;
  protected ImageIcon icon;
  protected boolean boMultipleChoice;
  protected int iIndexToSelect = -1;
  
  public DialogOptions(String sTitle, String sBorderTitle, ImageIcon icon,
    int iWidth, int iHeight, List listValues, boolean boMultipleChoice, int iIndexToSelect)
  {
    setTitle(sTitle);
    setModal(true);
    this.sBorderTitle = sBorderTitle;
    this.oValues = listValues;
    this.icon = icon;
    this.boMultipleChoice = boMultipleChoice;
    this.iIndexToSelect = iIndexToSelect;
    try {
      init(false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogOptions", ex);
    }
    this.setSize(iWidth, iHeight);
  }
  
  public DialogOptions(JDialog jdialog, String sTitle, String sBorderTitle, ImageIcon icon,
    int iWidth, int iHeight, List listValues, boolean boMultipleChoice, int iIndexToSelect)
  {
    super(jdialog);
    setTitle(sTitle);
    setModal(true);
    this.sBorderTitle = sBorderTitle;
    this.oValues = listValues;
    this.icon = icon;
    this.boMultipleChoice = boMultipleChoice;
    this.iIndexToSelect = iIndexToSelect;
    try {
      init(false);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DialogOptions", ex);
    }
    this.setSize(iWidth, iHeight);
  }
  
  public static
  Object showMe(String sTitle, String sBorderTitle, ImageIcon icon,
    int iWidth, int iHeight, List listValues)
  {
    DialogOptions dialog = new DialogOptions(sTitle, sBorderTitle,
      icon, iWidth, iHeight, listValues, false, 0);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    return dialog.getSelectedValue();
  }
  
  public static
  Object showMe(String sTitle, String sBorderTitle, ImageIcon icon,
    int iWidth, int iHeight, List listValues, boolean boSceltaMultipla)
  {
    DialogOptions dialog = new DialogOptions(sTitle, sBorderTitle,
      icon, iWidth, iHeight, listValues, boSceltaMultipla, 0);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    return dialog.getSelectedValue();
  }
  
  public static
  Object showMe(String sTitle, String sBorderTitle,
    int iWidth, int iHeight, List listValues, boolean boSceltaMultipla)
  {
    DialogOptions dialog = new DialogOptions(sTitle, sBorderTitle,
      null, iWidth, iHeight, listValues, boSceltaMultipla, -1);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    return dialog.getSelectedValue();
  }
  
  public static
  Object showMe(JDialog jdialog, String sTitle, String sBorderTitle,
    int iWidth, int iHeight, List listValues, boolean boSceltaMultipla)
  {
    DialogOptions dialog = new DialogOptions(jdialog, sTitle, sBorderTitle,
      null, iWidth, iHeight, listValues, boSceltaMultipla, -1);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    return dialog.getSelectedValue();
  }
  
  public static
  Object showMe(String sTitle, String sBorderTitle,
    int iWidth, int iHeight, List listValues, boolean boSceltaMultipla, int iIndexToSelect)
  {
    DialogOptions dialog = new DialogOptions(sTitle, sBorderTitle,
      null, iWidth, iHeight, listValues, boSceltaMultipla, iIndexToSelect);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
      screenSize.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    return dialog.getSelectedValue();
  }
  
  public
  Object getSelectedValue()
  {
    return oSelectedValue;
  }
  
  protected
  Container buildGUI()
    throws Exception
  {
    if(boMultipleChoice) {
      return buildArrayOfCheckBox();
    }
    else {
      return buildJList();
    }
  }
  
  protected
  Container buildJList()
    throws Exception
  {
    if(oValues == null || oValues.size() == 0) {
      return new JLabel("No items available", JLabel.CENTER);
    }
    jList = new JList(new Vector(oValues));
    jList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          fireOk();
        }
      }
    });
    jList.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent ke) {
        if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
          fireOk();
        }
      }
    });
    if(icon != null) {
      jList.setCellRenderer(new DefaultListCellRenderer() {
        private static final long serialVersionUID = 6001582555256878857L;
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
          super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
          setIcon(icon);
          return this;
        }
      });
    }
    if(oValues != null && oValues.size() > 0) {
      if(iIndexToSelect >= 0 && iIndexToSelect < oValues.size()) {
        jList.setSelectedIndex(iIndexToSelect);
      }
      else {
        jList.setSelectedIndex(0);
      }
    }
    JScrollPane jScrollPane = new JScrollPane(jList);
    jScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sBorderTitle));
    return jScrollPane;
  }
  
  protected
  Container buildArrayOfCheckBox()
    throws Exception
  {
    if(oValues == null || oValues.size() == 0) {
      return new JLabel("No items available", JLabel.CENTER);
    }
    arrayOfCheckBox = new JCheckBox[oValues.size()];
    mapItems = new HashMap();
    int iRows = oValues.size() < 10 ? 10 : oValues.size();
    JPanel jpGrid = new JPanel(new GridLayout(iRows, 1, 4, 4));
    jpGrid.setOpaque(true);
    jpGrid.setBackground(Color.white);
    for(int i = 0; i < oValues.size(); i++) {
      Object oItem = oValues.get(i);
      if(oItem == null) continue;
      boolean boChecked = iIndexToSelect >= 0 ? i == iIndexToSelect : false;
      arrayOfCheckBox[i] = new JCheckBox(oItem.toString(), boChecked);
      arrayOfCheckBox[i].setOpaque(true);
      arrayOfCheckBox[i].setBackground(Color.white);
      jpGrid.add(arrayOfCheckBox[i]);
      mapItems.put(new Integer(i), oItem);
    }
    JScrollPane jScrollPane = new JScrollPane(jpGrid);
    jScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sBorderTitle));
    return jScrollPane;
  }
  
  public
  boolean doCancel()
  {
    oSelectedValue = null;
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
  }
  
  public
  boolean doOk()
  {
    if(jList != null) {
      oSelectedValue = jList.getSelectedValue();
      return true;
    }
    if(arrayOfCheckBox != null && mapItems != null) {
      oSelectedValue = new ArrayList();
      for(int i = 0; i < arrayOfCheckBox.length; i++) {
        JCheckBox jCheckBox = arrayOfCheckBox[i];
        if(jCheckBox.isSelected()) {
          ((List) oSelectedValue).add(mapItems.get(new Integer(i)));
        }
      }
    }
    return true;
  }
}
