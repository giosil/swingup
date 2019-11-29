package org.dew.swingup.util;

import java.awt.Color;

import javax.swing.JComponent;

public 
class Blinker implements Runnable
{
  protected JComponent jcomponent;

  public Blinker(JComponent jcomponent)
  {
    this.jcomponent = jcomponent;
  }

  public
  void blink()
  {
    Thread thread = new Thread(this);
    thread.start();
  }
  
  public static 
  void blink(JComponent jcomponent)
  {
    Blinker blinker = new Blinker(jcomponent);
    blinker.blink();
  }
  
  public void run() {
    if(jcomponent == null) return;
    try {
      Color colorBackground = jcomponent.getBackground();
      Color colorBlink = new Color(255, 180, 100);
      for(int i = 0; i < 3; i++) {
        jcomponent.setBackground(colorBlink);
        Thread.sleep(250);
        jcomponent.setBackground(colorBackground);
        Thread.sleep(250);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }  
  }
}
