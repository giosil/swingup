package org.dew.swingup.demo;

import javax.swing.*;

import org.dew.swingup.*;

public
class DemoWorkObject extends JLabel implements IWorkObject
{
  private static final long serialVersionUID = -3822370923215121634L;
  
  public DemoWorkObject(String sText)
  {
    super(sText, JLabel.CENTER);
  }
  
  public boolean onClosing()
  {
    System.out.println("DemoWorkObject.onClosing");
    return GUIMessage.getConfirmation("Vuoi chiudere la finestra?");
  }
  
  public void onActivated()
  {
    System.out.println("DemoWorkObject.onActivated");
  }
  
  public void onOpened()
  {
    System.out.println("DemoWorkObject.onOpened");
  }
}
