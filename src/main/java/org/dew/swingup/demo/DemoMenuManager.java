package org.dew.swingup.demo;

import org.dew.swingup.*;
import org.dew.swingup.fm.GUIFileManager;

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
      oWorkPanel.show(new DemoWorkObject("Questo \350 un container!"), "Frame", "PaletteLarge.gif");
    }
    else
    if(sIdItem.equals("test.filemgr")) {
      oWorkPanel.show(new GUIFileManager(), "File Manager", "OpenProjectLarge.gif");
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
    
    addMenuItem("test",       // Id Menu
      "single",               // Id Item
      "&Single",              // Testo
      "Istanza singola",      // Descrizione
      "BookLarge.gif",        // Small Icon
      "BookLarge.gif",        // Large Icon
      true);                  // Enabled
    
    addMenuItem("test",       // Id Menu
      "frame",                // Id Item
      "&Frame",               // Testo
      "Mostra un container",  // Descrizione
      "PaletteLarge.gif",     // Small Icon
      "PaletteLarge.gif",     // Large Icon
      true);                  // Enabled
    
    addMenuItem("test",       // Id Menu
      "filemgr",              // Id Item
      "File &Mgr",            // Testo
      "File Manager",         // Descrizione
      "OpenProjectLarge.gif", // Small Icon
      "OpenProjectLarge.gif", // Large Icon
      true);                  // Enabled
  }
}
