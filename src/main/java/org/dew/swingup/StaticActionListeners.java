package org.dew.swingup;

import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Classe che implementa una collezione di ActionListener statica
 * sulla quale e' possibile notificare un evento globale.
 *
 * @version 1.0
 */
public
class StaticActionListeners
{
  private static List listActionListeners;
  
  static {
    listActionListeners = new ArrayList();
  }
  
  /**
   * Aggiunge un oggetto ActionListener alla lista.
   *
   * @param al ActionListener
   */
  public static
  void addActionListener(ActionListener al)
  {
    if(al == null) return;
    listActionListeners.add(al);
  }
  
  /**
   * Rimuove un ActionListener dalla lista.
   *
   * @param al ActionListener
   */
  public static
  void removeActionListener(ActionListener al)
  {
    if(al == null) return;
    listActionListeners.remove(al);
  }
  
  /**
   * Rimuove tutti gli oggetti ActionListener dalla lista.
   * ATTENZIONE: swingup rimuove tutti i listener subito dopo il login
   *             e prima di richiamare IApplicationListener.afterLogin.
   */
  public static
  void removeAll()
  {
    listActionListeners.clear();
  }
  
  /**
   * Notifica un evento.
   *
   * @param oSource Se diverso da null sostituisce ActionEvent.getSource();
   * @param e Oggetto ActionEvent
   */
  public static
  void notifyActionEvent(Object oSource, ActionEvent e)
  {
    if(listActionListeners.size() == 0) return;
    
    ActionEvent oActionEvent = null;
    if(oSource == null) {
      oActionEvent = e;
    }
    else {
      oActionEvent = new ActionEvent(oSource, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
    }
    for(int i = 0; i < listActionListeners.size(); i++) {
      ActionListener al = (ActionListener) listActionListeners.get(i);
      try {
        al.actionPerformed(oActionEvent);
      }
      catch(Exception ex) {
        String sMsg = "Errore durante la notifica dell'evento";
        String sAC = oActionEvent.getActionCommand();
        if(sAC != null) {
          sMsg += " \"" + sAC + "\"";
        }
        GUIMessage.showException(sMsg, ex);
      }
    }
  }
}
