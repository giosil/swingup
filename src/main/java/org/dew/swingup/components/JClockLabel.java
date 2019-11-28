package org.dew.swingup.components;

import java.text.*;
import java.util.*;

import javax.swing.*;

/**
 * Componente che mostra la data e l'ora corrente. Estende JLabel.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class JClockLabel extends JLabel implements Runnable
{
  SimpleDateFormat oDateFormat;
  
  public
  JClockLabel()
  {
    super();
    oDateFormat = new SimpleDateFormat();
    setText(oDateFormat.format(new Date()));
  }
  
  public
  JClockLabel(Icon oIcon)
  {
    super();
    oDateFormat = new SimpleDateFormat();
    setIcon(oIcon);
    setText(oDateFormat.format(new Date()));
  }
  
  public
  JClockLabel(String sPattern)
  {
    super();
    if(sPattern != null) {
      oDateFormat = new SimpleDateFormat(sPattern);
    }
    else {
      oDateFormat = new SimpleDateFormat();
    }
    setText(oDateFormat.format(new Date()));
  }
  
  public
  JClockLabel(Icon oIcon, String sPattern)
  {
    super();
    if(sPattern != null) {
      oDateFormat = new SimpleDateFormat(sPattern);
    }
    else {
      oDateFormat = new SimpleDateFormat();
    }
    setIcon(oIcon);
    setText(oDateFormat.format(new Date()));
  }
  
  /**
   * Lancia il Thread che aggiorna l'ora.
   */
  public
  void start()
  {
    new Thread(this).start();
  }
  
  public
  void run()
  {
    try {
      while(true) {
        Thread.sleep(2000);
        setText(oDateFormat.format(new Date()));
      }
    }
    catch(Exception ex) {
    }
  }
}
