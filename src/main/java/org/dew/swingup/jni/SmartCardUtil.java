package org.dew.swingup.jni;

/**
 * Classe di utilita' per la gestione delle SmartCard.
 *
 * @version 1.0
 */
public
class SmartCardUtil
{
  /**
   * Verifica la presenza della SmartCard.
   *
   * @param boTrace boolean
   * @return boolean
   */
  public native boolean check(boolean boTrace);
}
