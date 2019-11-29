package org.dew.swingup.demo;

import java.util.Map;

import org.dew.swingup.*;
import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.editors.EntityInternalFrame;
import org.dew.swingup.editors.IEntityMgr;
import org.dew.swingup.fm.GUIFileManager;

@SuppressWarnings("rawtypes")
public
class DemoMenuManager extends ASimpleMenuManager
{
  public
  void enable(String sUserRole)
  {
    User user = ResourcesMgr.getSessionManager().getUser();
    Map mapEnabledFlags = user.getResourcesByFather("menu");
    setEnabled(mapEnabledFlags);
  }
  
  protected
  void onClick(String sIdItem)
  {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    
    if(sIdItem.equals("test.single")) {
      if(!workPanel.selectTab("Single")) {
        AEntityEditor entityEditor = new DemoEntityEditor();
        IEntityMgr entityMgr = new EntityInternalFrame();
        entityMgr.init(entityEditor, "Single Frame", "BookLarge.gif");
        workPanel.show(entityMgr);
      }
    }
    else
    if(sIdItem.equals("test.frame")) {
      workPanel.show(new DemoWorkObject("DemoWorkObject"), "Frame", "PaletteLarge.gif");
    }
    else
    if(sIdItem.equals("test.filemgr")) {
      workPanel.show(new GUIFileManager(), "File Manager", "OpenProjectLarge.gif");
    }
    else
    if(sIdItem.equals("test.calendar")) {
      workPanel.show(new DemoCalendar(), "Calendar", "Calendar.gif");
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
    
    addMenuItem("test",       // Id Menu
      "calendar",             // Id Item
      "&Calendar",            // Testo
      "Calendar Manager",     // Descrizione
      "Calendar.gif",         // Small Icon
      "Calendar.gif",         // Large Icon
      true);                  // Enabled
  }
}
