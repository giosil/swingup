package org.dew.swingup.dialog;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.List;

/**
 * Dialogo che permette di selezionare una voce da una lista.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class ListDialog extends JDialog
{
  protected JList oList;
  protected List oValues;
  
  protected Object oSelectedValue;
  protected String sBoderTitle;
  
  public
  ListDialog(Frame parent, String sTitle, List oValues)
  {
    super(parent, sTitle, true);
    
    this.oValues = oValues;
    try {
      init();
      pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public
  ListDialog(Frame parent, String sTitle, String sBoderTitle, List oValues)
  {
    super(parent, sTitle, true);
    
    this.oValues     = oValues;
    this.sBoderTitle = sBoderTitle;
    try {
      init();
      pack();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public static
  Object showMe(Frame parent, String sTitle, List oValues)
  {
    ListDialog listDialog = new ListDialog(parent, sTitle, oValues);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    listDialog.setLocation(screenSize.width/2 - listDialog.getSize().width/2,
      screenSize.height/2 - listDialog.getSize().height/2);
    
    listDialog.setVisible(true);
    
    return listDialog.getSelectedValue();
  }
  
  public static
  Object showMe(Frame parent, String sTitle, int iWidth, int iHeight, List oValues)
  {
    ListDialog listDialog = new ListDialog(parent, sTitle, oValues);
    listDialog.setSize(iWidth, iHeight);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    listDialog.setLocation(screenSize.width/2 - listDialog.getSize().width/2,
      screenSize.height/2 - listDialog.getSize().height/2);
    
    listDialog.setVisible(true);
    
    return listDialog.getSelectedValue();
  }
  
  public static
  Object showMe(Frame parent, String sTitle, String sBorderTitle, List oValues)
  {
    ListDialog listDialog = new ListDialog(parent, sTitle, sBorderTitle, oValues);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    listDialog.setLocation(screenSize.width/2 - listDialog.getSize().width/2,
      screenSize.height/2 - listDialog.getSize().height/2);
    
    listDialog.setVisible(true);
    
    return listDialog.getSelectedValue();
  }
  
  public static
  Object showMe(Frame parent, String sTitle, String sBorderTitle, int iWidth, int iHeight, List oValues)
  {
    ListDialog listDialog = new ListDialog(parent, sTitle, sBorderTitle, oValues);
    listDialog.setSize(iWidth, iHeight);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    listDialog.setLocation(screenSize.width/2 - listDialog.getSize().width/2,
      screenSize.height/2 - listDialog.getSize().height/2);
    
    listDialog.setVisible(true);
    
    return listDialog.getSelectedValue();
  }
  
  public
  Object getSelectedValue()
  {
    return oSelectedValue;
  }
  
  public
  JList getJList()
  {
    return oList;
  }
  
  protected
  void init()
  {
    if(oValues != null && oValues.size() > 0) {
      oList = new JList(new Vector(oValues));
    }
    else {
      oList = new JList();
    }
    oList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          onDoubleClick();
        }
      }
    });
    oList.addKeyListener(new KeyAdapter() {
      public
      void keyPressed(KeyEvent ke) {
        if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
          onDoubleClick();
        }
      }
    });
    if(oValues != null && oValues.size() > 0) {
      oList.setSelectedIndex(0);
    }
    JScrollPane jScrollPane = new JScrollPane(oList);
    if(sBoderTitle != null && sBoderTitle.length() > 0) {
      jScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), sBoderTitle));
    }
    getContentPane().add(jScrollPane);
  }
  
  protected
  void onDoubleClick()
  {
    oSelectedValue = oList.getSelectedValue();
    dispose();
  }
}
