package org.dew.swingup.impl;

import java.awt.*;
import javax.swing.*;
import ro.architekt.javax.swing.sidemenu.JSideMenuBar;
import org.dew.swingup.*;

/**
 * Implementazione di IMenuManager che costruisce un menu vuoto.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class EmptyMenuManager implements IMenuManager
{
  JMenuBar oMenuBar;
  JSideMenuBar oSideMenuBar;
  JToolBar oToolBar;
  
  public
  EmptyMenuManager()
  {
  }
  
  public
  void build()
  {
    oMenuBar = new JMenuBar();
    oSideMenuBar = new JSideMenuBar();
    oToolBar = new DefaultToolBar();
  }
  
  public
  void setEnabled(String sIdMenu, String sIdItem, boolean boEnabled)
  {
  }
  
  public
  boolean isEnabled(String sIdMenu, String sIdItem)
  {
    return true;
  }
  
  public
  void setVisible(String sIdMenu, String sIdItem, boolean boVisible)
  {
  }
  
  public
  boolean isVisible(String sIdMenu, String sIdItem)
  {
    return true;
  }
  
  public
  JMenuBar getJMenuBar()
  {
    return oMenuBar;
  }
  
  public
  Container getSideMenu()
  {
    return oSideMenuBar;
  }
  
  public
  int getSideMenuWidth()
  {
    return 120;
  }
  
  public
  JToolBar getJToolBar()
  {
    return oToolBar;
  }
  
  public
  void actionPerformed(String sIdItem)
  {
  }
  
  public
  boolean notifyDefault(String sActionCommand)
  {
    return true;
  }
  
  public
  void update(String sUserRole)
  {
  }
}
