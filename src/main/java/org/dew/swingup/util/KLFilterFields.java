package org.dew.swingup.util;

import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.util.FormPanel;

import java.awt.event.*;
import java.util.List;

import javax.swing.*;

@SuppressWarnings("rawtypes")
public 
class KLFilterFields extends KeyAdapter 
{
  protected AEntityEditor aEntityEditor;
  protected JTable oTable;
  
  public KLFilterFields(AEntityEditor aEntityEditor, JTable oTable)
  {
    this.aEntityEditor = aEntityEditor;
    this.oTable = oTable;
  }
  
  public static void addKeyListener(FormPanel formPanel, AEntityEditor aEntityEditor, JTable oTable) {
    List listComponents = formPanel.getListComponents();
    KLFilterFields kl = null;
    for(int i = 0; i < listComponents.size(); i++) {
      Object oComponent = listComponents.get(i);
      if(oComponent instanceof JTextField) {
        if(kl == null) kl = new KLFilterFields(aEntityEditor, oTable);
        ((JTextField) oComponent).addKeyListener(kl);
      }
    }
  }
  
  public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ENTER && aEntityEditor != null) {
      try {
        aEntityEditor.fireFind();
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    else
    if(e.getKeyCode() == KeyEvent.VK_DOWN && oTable != null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if(oTable.getModel().getRowCount() > 0) {
            oTable.getSelectionModel().setSelectionInterval(0, 0);
            oTable.requestFocus();
          }
        }
      });
    }
  }  
}
