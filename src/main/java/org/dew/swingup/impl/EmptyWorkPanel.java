package org.dew.swingup.impl;

import java.awt.*;

import javax.swing.*;

import org.dew.swingup.*;

/**
 * Implementazione di default di AWorkPanel.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class EmptyWorkPanel extends AWorkPanel
{
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
    return new JPanel();
  }
  
  /**
   * Imposta il flag di abilitazione della GUI.
   *
   * @param boEnabled boolean
   */
  public
  void setEnabled(boolean boEnabled)
  {
  }
  
  /**
   * Metodo invocato quando si verifica l'evento Opened sul Frame principale
   * dell'applicazione.
   */
  public
  void onOpened()
  {
  }
}
