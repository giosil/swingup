package org.dew.swingup.editors;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.dew.swingup.*;

/**
 * Implementazione di IEntityMgr per mostrare l'EntityEditor in una finestra
 * di dialogo.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class EntityDialog extends JDialog
implements IEntityMgr
{
  AEntityEditor oEntityEditor;
  boolean boFireFindAfterOpen = false;
  
  public
  EntityDialog()
  {
    super(ResourcesMgr.mainFrame, true);
  }
  
  public
  EntityDialog(Frame oFrame)
  {
    super(oFrame, true);
  }
  
  public
  void init(AEntityEditor oTheEntityEditor, String sTitle,
    String sIcon)
  {
    init(oTheEntityEditor, sTitle, sIcon, false);
  }
  
  public
  void init(AEntityEditor oTheEntityEditor, String sTitle,
    String sIcon, boolean boAllowEditing)
  {
    oEntityEditor = oTheEntityEditor;
    oEntityEditor.setEntityMgr(this);
    try {
      oEntityEditor.init(boAllowEditing);
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    
    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        if(!onClosing()) {
          return;
        }
        doExit();
      }
      
      public void windowOpened(WindowEvent we) {
        onOpened();
      }
      
      public void windowActivated(WindowEvent we) {
        onActivated();
      }
    });
    
    this.setTitle(sTitle);
    this.getContentPane().add(oEntityEditor);
    this.pack();
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
    dispose();
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
