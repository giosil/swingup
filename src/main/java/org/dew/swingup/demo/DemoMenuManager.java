package org.dew.swingup.demo;

import org.dew.swingup.*;

public
class DemoMenuManager extends ASimpleMenuManager
{
  public
  void enable(String sUserRole)
  {
  }
  
  protected
  void onClick(String sIdItem)
  {
    AWorkPanel oWorkPanel = ResourcesMgr.getWorkPanel();
    
    if(sIdItem.equals("test.single")) {
      if(!oWorkPanel.selectTab("Single")) {
        DemoInternalFrame oTestFrame = new DemoInternalFrame();
        oWorkPanel.show(oTestFrame, "Frame a istanza singola");
      }
    }
    else
    if(sIdItem.equals("test.frame")) {
      oWorkPanel.show(new DemoWorkObject("Questo \350 un container!"), "Frame", "BlueFlagLarge.gif");
    }
  }
  
  protected
  void initMenu()
  {
    addMenu("test", "&Test", "Menu Test", true);
  }
  
  protected
  void initItems()
  {
    // Gap tra le voci del menu laterale.
    iGapItems = 0;
    
    addMenuItem("test",        // Id Menu
      "single",              // Id Item
      "&Single",             // Testo
      "Istanza singola",     // Descrizione
      "RedFlagLarge.gif",    // Small Icon
      "RedFlagLarge.gif",    // Large Icon
      true);                 // Enabled
    
    addMenuItem("test",        // Id Menu
      "frame",               // Id Item
      "&Frame",              // Testo
      "Mostra un container", // Descrizione
      "BlueFlagLarge.gif",   // Small Icon
      "BlueFlagLarge.gif",   // Large Icon
      true);                 // Enabled
  }
}
