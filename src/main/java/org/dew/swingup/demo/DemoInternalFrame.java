package org.dew.swingup.demo;

import java.awt.*;

import javax.swing.*;

import org.dew.swingup.AJInternalFrame;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.*;

public
class DemoInternalFrame extends AJInternalFrame
{
  private static final long serialVersionUID = 745053459502827444L;
  
  public
  DemoInternalFrame()
  {
    super("Single", "BookLarge.gif");
  }
  
  protected
  Container buildGUI()
    throws Exception
  {
    System.out.println("DemoInternalFrame.buildGUI");
    
    FormPanel formPanel = new FormPanel("FormPanel");
    formPanel.addRow();
    formPanel.addTextField("s", "Text");
    formPanel.addDateField("d", "Date");
    formPanel.addTimeField("t", "Time");
    formPanel.addIntegerField("i", "Integer");
    formPanel.addCurrencyField("c", "Currency");
    formPanel.build();
    
    JPanel jpResult = new JPanel();
    jpResult.add(formPanel);
    return jpResult;
  }
  
  public
  boolean onClosing()
  {
    System.out.println("DemoInternalFrame.onClosing");
    
    return GUIMessage.getConfirmation("Si vuole chiudere?");
  }
  
  public
  void onActivated()
  {
    System.out.println("DemoInternalFrame.onActivated");
  }
  
  public
  void onOpened()
  {
    System.out.println("DemoInternalFrame.onOpened");
  }
}
