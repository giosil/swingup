package org.dew.swingup.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.dew.swingup.ResourcesMgr;

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
