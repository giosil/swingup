package org.dew.swingup;

import java.awt.event.*;

/**
 * Oggetto ActionListener utilizzato per gestire la scelta di una voce di menu.
 *
 * @version 1.0
 */
public
class AMenuManager_ActionListner implements ActionListener
{
  IMenuManager aMenuManager;
  String sIdItem;
  
  public
  AMenuManager_ActionListner(IMenuManager aMenuManager, String sIdItem)
  {
    this.aMenuManager = aMenuManager;
    this.sIdItem = sIdItem;
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    aMenuManager.actionPerformed(sIdItem);
  }
}
