package org.dew.swingup.util;

import java.awt.*;
import javax.swing.*;

public 
class JLabelGradient extends JLabel 
{
  private static final long serialVersionUID = 1186409275856435222L;
  
  protected Color colorFrom;
  protected Color colorTo;
  
  public 
  JLabelGradient(String sText, int horizontalAlignment, Color colorFrom, Color colorTo)
  {
    super(sText, horizontalAlignment);
    setOpaque(true);
    if(colorFrom != null) {
      this.colorFrom = colorFrom;
    }
    else {
      this.colorFrom = getBackground();
    }
    if(colorFrom != null) {
      this.colorTo = colorTo;
    }
    else {
      this.colorTo = getBackground().darker();
    }
  }
  
  protected 
  void paintComponent(Graphics g)
  {
    if(!isOpaque()){
      super.paintComponent(g);
      return;
    }
    Graphics2D g2d = (Graphics2D)g;
    int w = getWidth();
    int h = getHeight();
    GradientPaint gp = new GradientPaint(0, 0, colorFrom, w, 0, colorTo);
    g2d.setPaint(gp);
    g2d.fillRect(0,0,w,h);
    setOpaque(false);
    super.paintComponent(g);
    setOpaque(true);
  }
}

