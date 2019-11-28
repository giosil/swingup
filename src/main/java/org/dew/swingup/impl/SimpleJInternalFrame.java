package org.dew.swingup.impl;

import java.awt.*;
import javax.swing.*;
import org.dew.swingup.*;

/**
 * Semplice implementazione di AJInternalFrame.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class SimpleJInternalFrame extends AJInternalFrame
{
  protected Container oContainer;
  
  public
  SimpleJInternalFrame(String sText, String sIcon, Container oContainer)
  {
    this.oContainer = oContainer;
    
    try{
      init(sText, sIcon);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di SimpleJInternalFrame", ex);
    }
  }
  
  public
  Container buildGUI()
    throws java.lang.Exception
  {
    if(oContainer == null) {
      return new JPanel();
    }
    
    return oContainer;
  }
  
  public
  boolean onClosing()
  {
    if(oContainer instanceof IWorkObject) {
      return ((IWorkObject) oContainer).onClosing();
    }
    
    return true;
  }
  
  public
  void onActivated()
  {
    if(oContainer instanceof IWorkObject) {
      ((IWorkObject) oContainer).onActivated();
    }
  }
  
  public
  void onOpened()
  {
    if(oContainer instanceof IWorkObject) {
      ((IWorkObject) oContainer).onActivated();
    }
  }
}
