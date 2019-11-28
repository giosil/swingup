package org.dew.swingup.util;

import javax.swing.*;

/**
 * Classe di appoggio che serve per identificare la colonna secondo la quale sono
 * ordinati i dati della tabella.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version $Revision: 2 $
 */
public
class TextAndIcon
{
  public String sText;
  public Icon   oIcon;
  
  /**
   * Costruttore.
   * @param sText testo della colonna.
   * @param oIcon icona di ascendente o discendente.
   */
  public
  TextAndIcon(String sText, Icon oIcon)
  {
    this.sText = sText;
    this.oIcon = oIcon;
  }
}
