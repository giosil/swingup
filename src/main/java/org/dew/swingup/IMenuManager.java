package org.dew.swingup;

import java.awt.Container;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/**
 * Interfaccia che permette di implementare un gestore di menu utilizzato da
 * MainFrame.
 *
 * @version 1.0
 */
public
interface IMenuManager
{
  /**
   * Costruisce il menu dell'applicazione.
   */
  public void build();
  
  /**
   * Aggiorna le abilitazioni del menu in accordo con il ruolo dell'utente.
   *
   * @param sUserRole Ruolo utente
   */
  public void update(String sUserRole);
  
  /**
   * Imposta il flag di abilitazione di una voce di menu.
   *
   * @param sIdMenu Identificativo del menu
   * @param sIdItem Identificativo della voce di menu
   * @param boEnabled flag di abilitazione
   */
  public void setEnabled(String sIdMenu, String sIdItem, boolean boEnabled);
  
  /**
   * Restituisce il flag di abilitazione di una voce di menu.
   *
   * @param sIdMenu Identificativo del menu
   * @param sIdItem Identificativo della voce di menu
   * @return boolean flag di abilitazione
   */
  public boolean isEnabled(String sIdMenu, String sIdItem);
  
  /**
   * Imposta il flag di visibilita' di una voce di menu.
   *
   * @param sIdMenu Identificativo del menu
   * @param sIdItem Identificativo della voce di menu
   * @param boVisible flag di visibilita'
   */
  public void setVisible(String sIdMenu, String sIdItem, boolean boVisible);
  
  /**
   * Restituisce il flag di visibilita' di una voce di menu.
   *
   * @param sIdMenu Identificativo del menu
   * @param sIdItem Identificativo della voce di menu
   * @return boolean flag di visibilita'
   */
  public boolean isVisible(String sIdMenu, String sIdItem);
  
  /**
   * Restituisce un oggetto JMenuBar.
   *
   * @return JMenuBar
   */
  public JMenuBar getJMenuBar();
  
  /**
   * Restituisce un oggetto JToolBar.
   *
   * @return JToolBar
   */
  public JToolBar getJToolBar();
  
  /**
   * Restituisce un container generico per il menu laterale.
   *
   * @return Container
   */
  public Container getSideMenu();
  
  /**
   * Restituisce la larghezza iniziale del menu laterale.
   *
   * @return Width
   */
  public int getSideMenuWidth();
  
  /**
   * Metodo invocato quando si clicca su una voce di menu identificata da sIdItem.
   *
   * @param sIdMenuItem Identificativo della voce di menu
   */
  public void actionPerformed(String sIdMenuItem);
  
  /**
   * Metodo invocato quano si clicca su una voce di menu predefinita come
   * ad es. Menu help, Opzioni, ecc.
   * Se si restituisce false non viene eseguito il comportamento di default.
   *
   * @param sActionCommand String
   * @return boolean
   */
  public boolean notifyDefault(String sActionCommand);
}
