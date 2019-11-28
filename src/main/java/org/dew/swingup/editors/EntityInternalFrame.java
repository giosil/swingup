package org.dew.swingup.editors;

import java.awt.*;
import javax.swing.*;

import org.dew.swingup.*;

/**
 * Implementazione di IEntityMgr per mostrare l'EntityEditor in una finestra
 * interna.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class EntityInternalFrame extends AJInternalFrame implements IEntityMgr
{
  AEntityEditor oEntityEditor;
  JPanel oMainPanel;
  boolean boFireFindAfterOpen = false;
  
  /**
   * Costruttore.
   */
  public
  EntityInternalFrame()
  {
    try {
      init("", null);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di EntityInternalFrame", ex);
    }
  }
  
  /**
   * Inizializza l'AEntityEditor e il frame interno che lo conterra'.
   *
   * @param oEntityEditor AEntityEditor
   * @param sTitle Titolo del frame
   * @param sIcon Icona
   */
  public
  void init(AEntityEditor oEntityEditor, String sTitle, String sIcon)
  {
    init(oEntityEditor, sTitle, sIcon, true);
  }
  
  /**
   * Inizializza l'AEntityEditor e il frame interno che lo conterra'.
   *
   * @param oEntityEditor AEntityEditor
   * @param sTitle Titolo del frame
   * @param sIcon Icona
   * @param boAllowEditing Consente l'editing all'interno dell'AEntityEditor.
   */
  public
  void init(AEntityEditor oEntityEditor, String sTitle, String sIcon,
    boolean boAllowEditing)
  {
    this.oEntityEditor = oEntityEditor;
    oEntityEditor.setEntityMgr(this);
    try {
      oEntityEditor.init(boAllowEditing);
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    this.setTitle(sTitle);
    this.setFrameIcon(ResourcesMgr.getImageIcon(sIcon));
    oMainPanel.add(oEntityEditor, BorderLayout.CENTER);
  }
  
  public
  void setStartupFilterValues(Object oValues)
  {
    if(oEntityEditor != null) {
      oEntityEditor.setStartupFilterValues(oValues);
    }
  }
  
  public
  void setFireFindAfterOpened(boolean boValue)
  {
    this.boFireFindAfterOpen = boValue;
  }
  
  public
  boolean isFireFindAfterOpened()
  {
    return boFireFindAfterOpen;
  }
  
  public
  Object getChoice()
  {
    if(oEntityEditor != null) {
      return oEntityEditor.getChoice();
    }
    
    return null;
  }
  
  public
  void doExit()
  {
    close();
  }
  
  protected
  Container buildGUI()
    throws Exception
  {
    oMainPanel = new JPanel(new BorderLayout());
    
    return oMainPanel;
  }
  
  public
  boolean onClosing()
  {
    if(oEntityEditor != null) {
      return oEntityEditor.onClosing();
    }
    
    return true;
  }
  
  public
  void onActivated()
  {
    if(oEntityEditor != null) {
      oEntityEditor.onActivated();
    }
  }
  
  public
  void onOpened()
  {
    if(oEntityEditor != null) {
      oEntityEditor.onOpened();
      if(boFireFindAfterOpen) {
        try{
          oEntityEditor.fireFind();
        }
        catch(Exception ex) {
          GUIMessage.showException(ex);
        }
      }
    }
  }
}
