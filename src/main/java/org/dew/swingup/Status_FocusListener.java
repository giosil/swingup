package org.dew.swingup;

import java.awt.event.*;

/**
 * Oggetto FocusListener utilizzato per mostrare un testo
 * sulla barra di stato quando un componente acquista il focus.
 *
 * @version 1.0
 */
public
class Status_FocusListener implements FocusListener
{
  String sTextStatusBar;
  
  public Status_FocusListener(String sTextStatusBar)
  {
    this.sTextStatusBar = sTextStatusBar;
  }
  
  public
  void focusGained(FocusEvent e)
  {
    ResourcesMgr.getStatusBar().setText(sTextStatusBar);
  }
  
  public
  void focusLost(FocusEvent e)
  {
    ResourcesMgr.getStatusBar().setPreviousStatus();
  }
}
