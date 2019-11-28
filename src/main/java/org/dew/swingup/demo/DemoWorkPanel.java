package org.dew.swingup.demo;

import org.dew.swingup.*;

import java.awt.*;
import javax.swing.*;

public
class DemoWorkPanel extends AWorkPanel
{
  private static final long serialVersionUID = -2152144060321676515L;
  
  JLabel oLabel;
  
  /**
   * Costruisce la GUI.
   *
   * @return Container
   * @throws Exception
   */
  public
  Container buildGUI()
    throws Exception
  {
    JPanel oPanel = new JPanel();
    
    oLabel = new JLabel("WorkPanel Enabled");
    oPanel.add(oLabel);
    
    return oPanel;
  }
  
  /**
   * Imposta il flag di abilitazione della GUI.
   *
   * @param boEnabled boolean
   */
  public
  void setEnabled(boolean boEnabled)
  {
    if(boEnabled) {
      oLabel.setText("WorkPanel Enabled");
    }
    else {
      oLabel.setText("WorkPanel NOT Enabled");
    }
  }
  
  /**
   * Metodo invocato quando si verifica l'evento Opened sul Frame principale
   * dell'applicazione.
   */
  public
  void onOpened()
  {
  }
  
  /**
   * Metodo invocato prima della chiusura del work object.
   * Se restituisce false il work object non viene chiuso.
   *
   * @return Flag di chiusura
   */
  public
  boolean onClosing()
  {
    return true;
  }
  
  public
  String getHelpDoc(String sTitle)
  {
    if(sTitle != null && sTitle.equals("Editor")) {
      return "disclaimer.htm";
    }
    return null;
  }
}
