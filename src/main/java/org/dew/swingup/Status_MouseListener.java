package org.dew.swingup;

import java.awt.event.*;

/**
 * Oggetto MouseListener utilizzato per mostrare un testo
 * sulla barra di stato quando si entra con il puntatore del mouse.
 *
 * @version 1.0
 */
public
class Status_MouseListener extends MouseAdapter
{
  String sTextStatusBar;
  
  public
  Status_MouseListener(String sTextStatusBar)
  {
    this.sTextStatusBar = sTextStatusBar;
  }
  
  public
  void mouseEntered(MouseEvent me)
  {
    ResourcesMgr.getStatusBar().setText(sTextStatusBar);
  }
  
  public
  void mouseExited(MouseEvent me)
  {
    ResourcesMgr.getStatusBar().setPreviousStatus();
  }
}
