package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;

import org.dew.swingup.ResourcesMgr;

import org.dew.swingup.util.FormPanel;

public
class BGColorFocusListener implements FocusListener
{
  private Component oComponent;
  private Color oBGColorOnFocus;
  private Color oComponentBGColor;

  public
  BGColorFocusListener(Component theComponent)
  {
    this.oComponent = theComponent;
  String sOnFocus = ResourcesMgr.config.getProperty(FormPanel.sFORMPANEL_ONFOCUS);
    if(sOnFocus != null && sOnFocus.length() == 7) {
      oBGColorOnFocus = Color.decode(sOnFocus);
    }
    this.oComponentBGColor = oComponent.getBackground();
  }

  public
  void focusGained(FocusEvent e)
  {
    oComponent.setBackground(oBGColorOnFocus);
  }

  public
  void focusLost(FocusEvent e)
  {
    oComponent.setBackground(oComponentBGColor);
  }
}
