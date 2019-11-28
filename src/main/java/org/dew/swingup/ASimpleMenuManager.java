package org.dew.swingup;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

import ro.architekt.javax.swing.sidemenu.*;

import org.dew.swingup.impl.*;

/**
 * Implementazione di IMenuManager che consente di costruire rapidamente menu.
 *
 * @version 1.0
 */
public abstract
class ASimpleMenuManager implements IMenuManager
{
  // Menu bar
  protected JMenuBar jMenuBar = new JMenuBar();
  
  // Side Menu Bar
  protected JSideMenuBar jSideMenuBar = new JSideMenuBar();
  
  protected List listMenu = null;
  protected List listItems = null;
  protected List listStartupEnabledFlags = null;
  
  protected Map mapMenu = null;
  protected Map mapItems = null;
  
  protected static final String sMENU_ID                = "menu.id";
  protected static final String sMENU_TEXT              = "menu.text";
  protected static final String sMENU_DESCRIPTION       = "menu.desc";
  protected static final String sMENU_MNEMONIC          = "menu.mnemonic";
  protected static final String sMENU_ENABLED           = "menu.enabled";
  protected static final String sMENU_VISIBLE           = "menu.visible";
  protected static final String sMENU_JMENU             = "menu.jm";
  protected static final String sMENU_JSIDEMENU         = "menu.jsm";
  protected static final String sMENU_LISTITEMS         = "menu.items";
  
  protected static final String sMENUITEM_ID            = "item.id";
  protected static final String sMENUITEM_IDMENU        = "item.idmenu";
  protected static final String sMENUITEM_TEXT          = "item.text";
  protected static final String sMENUITEM_SMALLICON     = "item.small";
  protected static final String sMENUITEM_LARGEICON     = "item.large";
  protected static final String sMENUITEM_DISABLEDICON  = "item.disicon";
  protected static final String sMENUITEM_DESCRIPTION   = "item.desc";
  protected static final String sMENUITEM_MNEMONIC      = "item.mnemonic";
  protected static final String sMENUITEM_ENABLED       = "item.enabled";
  protected static final String sMENUITEM_VISIBLE       = "item.visible";
  protected static final String sMENUITEM_JMENUITEM     = "item.jmi";
  protected static final String sMENUITEM_JSIDEMENUITEM = "item.jsmi";
  protected static final String sMENUITEM_INDEX         = "item.index";
  
  protected boolean boSettings = ResourcesMgr.getBooleanProperty(IResourceMgr.sAPP_SHOW_SETTINGS, false);
  
  public static final String sMENU_SETTINGS = "settings";
  public static final String sMENUITEM_PASSWORD = "password";
  public static final String sMENUITEM_OPTIONS = "options";
  public static final String sACTIONCOMMAND_PASSWORD = sMENU_SETTINGS + "." + sMENUITEM_PASSWORD;
  public static final String sACTIONCOMMAND_OPTIONS  = sMENU_SETTINGS + "." + sMENUITEM_OPTIONS;
  
  protected JToolBar oToolBar;
  
  // Gap tra le voci del menu laterale.
  protected int iGapItems = 0;
  
  /**
   * Costruttore.
   */
  public
  ASimpleMenuManager()
  {
  }
  
  /**
   * Metodo invocato quando si clicca su una voce di menu identificata da sIdItem.
   *
   * @param sIdMenuItem Identificativo della voce di menu
   */
  protected abstract
  void onClick(String sIdMenuItem);
  
  /**
   * Crea il menu.
   * Utilizzare il metodo addMenu per aggiungere un Menu.
   */
  protected abstract
  void initMenu();
  
  /**
   * Crea le voci di menu.
   * Utilizzare il metodo addMenuItem per aggiungere una vode di Menu.
   */
  protected abstract
  void initItems();
  
  /**
   * Abilita/Disabilita le voci di menu in accordo con il ruolo dell'utente.
   * Utilizzare il metodo setEnabled per abilitare/disabilitare una voce di menu.
   *
   * @param sUserRole Ruolo utente
   */
  protected abstract
  void enable(String sUserRole);
  
  /**
   * Costruisce il menu.
   */
  public
  void build()
  {
    listMenu = new ArrayList();
    listItems = new ArrayList();
    listStartupEnabledFlags = new ArrayList();
    mapMenu = new HashMap();
    mapItems = new HashMap();
    
    initMenu();
    initItems();
    if(boSettings) {
      addMenuSettings();
    }
    buildMenu();
  }
  
  public
  JMenuBar getJMenuBar()
  {
    return jMenuBar;
  }
  
  public
  JToolBar getJToolBar()
  {
    if(oToolBar != null) return oToolBar;
    oToolBar = new DefaultToolBar();
    return oToolBar;
  }
  
  public
  Container getSideMenu()
  {
    return jSideMenuBar;
  }
  
  public
  int getSideMenuWidth()
  {
    return 120;
  }
  
  public
  void actionPerformed(String sIdMenuItem)
  {
    if(!isEnabled(sIdMenuItem)) return;
    if(boSettings) {
      if(sIdMenuItem.equals(sACTIONCOMMAND_PASSWORD)) {
        try {
          if(notifyDefault(sIdMenuItem)) {
            ResourcesMgr.getGUIManager().showGUIChangePassword(ResourcesMgr.mainFrame, false);
          }
        }
        catch(Exception ex) {
          GUIMessage.showException(ex);
        }
      }
      else
      if(sIdMenuItem.equals(sACTIONCOMMAND_OPTIONS)) {
        try {
          if(notifyDefault(sIdMenuItem)) {
            ResourcesMgr.getGUIManager().showGUIOptions(ResourcesMgr.mainFrame);
          }
        }
        catch(Exception ex) {
          GUIMessage.showException(ex);
        }
      }
    }
    onClick(sIdMenuItem);
  }
  
  /**
   * Metodo invocato quano si clicca su una voce di menu predefinita come
   * ad es. Menu help, Opzioni, ecc.
   * Se si restituisce false non viene eseguito il comportamento di default.
   *
   * @param sActionCommand String
   * @return boolean
   */
  public
  boolean notifyDefault(String sActionCommand)
  {
    return true;
  }
  
  /**
   * Aggiorna le abilitazioni del menu.
   *
   * @param sUserRole String
   */
  public
  void update(String sUserRole)
  {
    if(sUserRole == null) {
      setEnabledAll(false);
    }
    else {
      if(boSettings) {
        enableMenuSettings();
      }
      setStartupEnabledFlags();
      enable(sUserRole);
      rebuidSideMenu();
    }
  }
  
  /**
   * Imposta il flag di abilitazione della voce di menu.
   *
   * @param sIdMenu String
   * @param sIdItem String
   * @param boEnabled boolean
   */
  public
  void setEnabled(String sIdMenu, String sIdItem, boolean boEnabled)
  {
    if(sIdMenu == null) {
      setEnabledAll(boEnabled);
      return;
    }
    
    if(sIdItem != null) {
      Map mapMenuItemInfo = (Map) mapItems.get(sIdMenu + "." + sIdItem);
      if(mapMenuItemInfo == null) {
        return;
      }
      setMenuItemEnabled(mapMenuItemInfo, boEnabled);
      
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      if(boEnabled) {
        setMenuEnabled(mapMenuInfo, true);
      }
      else {
        if(isAllMenuItemsDisabled(mapMenuInfo)) {
          setMenuEnabled(mapMenuInfo, false);
        }
      }
    }
    else {
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      if(mapMenuInfo == null) {
        return;
      }
      setMenuEnabled(mapMenuInfo, boEnabled);
      
      Iterator oIt = mapItems.keySet().iterator();
      while(oIt.hasNext()) {
        String sIdMenuItem = (String) oIt.next();
        Map mapMenuItemInfo = (Map) mapItems.get(sIdMenuItem);
        String sIdMenuOfItem = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
        if(sIdMenuOfItem.equals(sIdMenu)) {
          setMenuItemEnabled(mapMenuItemInfo, boEnabled);
        }
      }
    }
  }
  
  /**
   * Imposta i flag di abilitazione attraverso una mappa di voci di menu.
   * La mappa deve avere come chiavi le voci di menu e come valori
   * oggetti che esprimono il flag di abilitazione:
   *
   * String: "0", "N" per false, altro per true
   * Integer: > 0 per true, = 0 per false
   * Boolean: true, false
   *
   * @param mapEnabledFlags Map
   */
  public
  void setEnabled(Map mapEnabledFlags)
  {
    if(mapEnabledFlags == null) {
      return;
    }
    
    Iterator oItEntry = mapEnabledFlags.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sMenuItem = (String) entry.getKey();
      boolean boEnabled = false;
      Object oEnabled = entry.getValue();
      if(oEnabled instanceof String) {
        String s = (String) oEnabled;
        boEnabled = !s.startsWith("0") && !s.startsWith("N");
      }
      else
      if(oEnabled instanceof Integer) {
        boEnabled = ((Integer) oEnabled).intValue() != 0;
      }
      else
      if(oEnabled instanceof Boolean) {
        boEnabled = ((Boolean) oEnabled).booleanValue();
      }
      
      Map mapMenuItemInfo = (Map) mapItems.get(sMenuItem);
      if(mapMenuItemInfo == null) {
        continue;
      }
      setMenuItemEnabled(mapMenuItemInfo, boEnabled);
    }
    
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      if(isAllMenuItemsDisabled(mapMenuInfo)) {
        setMenuEnabled(mapMenuInfo, false);
      }
      else {
        setMenuEnabled(mapMenuInfo, true);
      }
    }
  }
  
  /**
   * Imposta il flag di abilitazione attraverso una lista di voci di menu.
   *
   * @param oMenuItems List
   * @param boEnabled boolean
   */
  public
  void setEnabled(List oMenuItems, boolean boEnabled)
  {
    if(oMenuItems == null) {
      return;
    }
    
    for(int i = 0; i < oMenuItems.size(); i++) {
      String sMenuItem = (String) oMenuItems.get(i);
      
      Map mapMenuItemInfo = (Map) mapItems.get(sMenuItem);
      if(mapMenuItemInfo == null) {
        continue;
      }
      setMenuItemEnabled(mapMenuItemInfo, boEnabled);
    }
    
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      if(isAllMenuItemsDisabled(mapMenuInfo)) {
        setMenuEnabled(mapMenuInfo, false);
      }
      else {
        setMenuEnabled(mapMenuInfo, true);
      }
    }
  }
  
  /**
   * Imposta i flag di visibilita' attraverso una mappa di voci di menu.
   * La mappa deve avere come chiavi le voci di menu e come valori
   * oggetti che esprimono il flag di abilitazione:
   *
   * String: "0", "N" per false, altro per true
   * Integer: > 0 per true, = 0 per false
   * Boolean: true, false
   *
   * @param mapVisibleFlags Map
   */
  public
  void setVisible(Map mapVisibleFlags)
  {
    if(mapVisibleFlags == null) {
      return;
    }
    
    Iterator oItEntry = mapVisibleFlags.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      String sMenuItem = (String) entry.getKey();
      boolean boVisible = false;
      Object oVisible = entry.getValue();
      if(oVisible instanceof String) {
        String s = (String) oVisible;
        boVisible = !s.startsWith("0") && !s.startsWith("N");
      }
      else
      if(oVisible instanceof Integer) {
        boVisible = ((Integer) oVisible).intValue() != 0;
      }
      else
      if(oVisible instanceof Boolean) {
        boVisible = ((Boolean) oVisible).booleanValue();
      }
      
      Map mapMenuItemInfo = (Map) mapItems.get(sMenuItem);
      if(mapMenuItemInfo == null) {
        continue;
      }
      setMenuItemVisible(mapMenuItemInfo, boVisible);
    }
    
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      if(isAllMenuItemsInvisible(mapMenuInfo)) {
        setMenuVisible(mapMenuInfo, false);
      }
      else {
        setMenuVisible(mapMenuInfo, true);
      }
    }
  }
  
  /**
   * Imposta il flag di visibilita' attraverso una lista di voci di menu.
   *
   * @param oMenuItems List
   * @param boVisible boolean
   */
  public
  void setVisible(List oMenuItems, boolean boVisible)
  {
    if(oMenuItems == null) {
      return;
    }
    
    for(int i = 0; i < oMenuItems.size(); i++) {
      String sMenuItem = (String) oMenuItems.get(i);
      
      Map mapMenuItemInfo = (Map) mapItems.get(sMenuItem);
      if(mapMenuItemInfo == null) {
        continue;
      }
      setMenuItemVisible(mapMenuItemInfo, boVisible);
    }
    
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      if(isAllMenuItemsInvisible(mapMenuInfo)) {
        setMenuVisible(mapMenuInfo, false);
      }
      else {
        setMenuVisible(mapMenuInfo, true);
      }
    }
  }
  
  /**
   * Ottiene il flag di abilitazione di una voce di menu.
   *
   * @param sIdMenu String
   * @param sIdItem String
   * @return boolean
   */
  public
  boolean isEnabled(String sIdMenu, String sIdItem)
  {
    if(sIdItem == null) {
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      if(mapMenuInfo == null) {
        return false;
      }
      return ((Boolean) mapMenuInfo.get(sMENU_ENABLED)).booleanValue();
    }
    
    Map mapMenuItemInfo = (Map) mapItems.get(sIdMenu + "." + sIdItem);
    if(mapMenuItemInfo == null) {
      return false;
    }
    return ((Boolean) mapMenuItemInfo.get(sMENUITEM_ENABLED)).booleanValue();
  }
  
  /**
   * Imposta il flag di visibilita' della voce di menu.
   *
   * @param sIdMenu String
   * @param sIdItem String
   * @param boVisible boolean
   */
  public
  void setVisible(String sIdMenu, String sIdItem, boolean boVisible)
  {
    if(sIdMenu == null) {
      setVisibleAll(boVisible);
      return;
    }
    
    if(sIdItem != null) {
      Map mapMenuItemInfo = (Map) mapItems.get(sIdMenu + "." + sIdItem);
      if(mapMenuItemInfo == null) {
        return;
      }
      setMenuItemVisible(mapMenuItemInfo, boVisible);
      
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      if(boVisible) {
        setMenuVisible(mapMenuInfo, true);
      }
      else {
        if(isAllMenuItemsInvisible(mapMenuInfo)) {
          setMenuVisible(mapMenuInfo, false);
        }
      }
    }
    else {
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      if(mapMenuInfo == null) {
        return;
      }
      setMenuVisible(mapMenuInfo, boVisible);
      
      Iterator oIt = mapItems.keySet().iterator();
      while(oIt.hasNext()) {
        String sIdMenuItem = (String) oIt.next();
        Map mapMenuItemInfo = (Map) mapItems.get(sIdMenuItem);
        String sIdMenuOfItem = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
        if(sIdMenuOfItem.equals(sIdMenu)) {
          setMenuItemVisible(mapMenuItemInfo, boVisible);
        }
      }
    }
  }
  
  /**
   * Ottiene il flag di visibilita' di una voce di menu.
   *
   * @param sIdMenu String
   * @param sIdItem String
   * @return boolean
   */
  public
  boolean isVisible(String sIdMenu, String sIdItem)
  {
    if(sIdItem == null) {
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      if(mapMenuInfo == null) {
        return false;
      }
      return ((Boolean) mapMenuInfo.get(sMENU_VISIBLE)).booleanValue();
    }
    
    Map mapMenuItemInfo = (Map) mapItems.get(sIdMenu + "." + sIdItem);
    if(mapMenuItemInfo == null) {
      return false;
    }
    return ((Boolean) mapMenuItemInfo.get(sMENUITEM_VISIBLE)).booleanValue();
  }
  
  /**
   * Aggiunge un Menu
   *
   * @param sId          Identificativo del menu
   * @param sText        Testo del menu
   * @param sDescription Descrizione
   * @param boEnabled    Flag abilitato
   */
  protected
  void addMenu(String sId, String sText, String sDescription, boolean boEnabled)
  {
    String sTextMenu = getText(sText);
    char cMnemonic = getMnemonic(sText);
    
    Map mapMenuInfo = buildMenuInfo(sId, sTextMenu, sDescription, cMnemonic, boEnabled);
    
    listMenu.add(mapMenuInfo);
    
    mapMenu.put(sId, mapMenuInfo);
    
    if(boEnabled) {
      listStartupEnabledFlags.add(sId);
    }
  }
  
  /**
   * Aggiunge un separatore.
   *
   * @param sIdMenu      Identificativo del menu
   */
  protected
  void addSeparator(String sIdMenu)
  {
    Map mapMenuItemInfo = new HashMap();
    mapMenuItemInfo.put(sMENUITEM_ID, "-");
    mapMenuItemInfo.put(sMENUITEM_IDMENU, sIdMenu);
    listItems.add(mapMenuItemInfo);
  }
  
  /**
   * Aggiunge una voce di menu
   *
   * @param sIdMenu      Identificativo del menu
   * @param sIdItem      Itendificativo della voce di menu
   * @param sText        Testo della voce di menu
   * @param sDescription Descrizione
   * @param sIcon        Icona
   * @param boEnabled    Flag abilitato
   */
  protected
  void addMenuItem(String sIdMenu, String sIdItem, String sText, String sDescription, String sIcon, boolean boEnabled)
  {
    String sTextMenuItem = getText(sText);
    char cMnemonic = getMnemonic(sText);
    
    Map mapMenuItemInfo = buildMenuItemInfo(sIdMenu, sIdItem, sTextMenuItem, sDescription, cMnemonic, sIcon, sIcon, boEnabled);
    
    listItems.add(mapMenuItemInfo);
    
    mapItems.put(sIdMenu + "." + sIdItem, mapMenuItemInfo);
    
    Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
    List listItemsByMenu = (List) mapMenuInfo.get(sMENU_LISTITEMS);
    listItemsByMenu.add(mapMenuItemInfo);
    
    if(boEnabled) {
      listStartupEnabledFlags.add(sIdMenu + "." + sIdItem);
    }
  }
  
  /**
   * Aggiunge una voce di menu
   *
   * @param sIdMenu      Identificativo del menu
   * @param sIdItem      Itendificativo della voce di menu
   * @param sText        Testo della voce di menu
   * @param sDescription Descrizione
   * @param sSmallIcon   Icona piccola (16x16)
   * @param sLargeIcon   Icona grande  (32x32)
   * @param boEnabled    Flag abilitato
   */
  protected
  void addMenuItem(String sIdMenu, String sIdItem, String sText, String sDescription, String sSmallIcon, String sLargeIcon, boolean boEnabled)
  {
    String sTextMenuItem = getText(sText);
    char cMnemonic = getMnemonic(sText);
    
    Map mapMenuItemInfo = buildMenuItemInfo(sIdMenu, sIdItem, sTextMenuItem, sDescription, cMnemonic, sSmallIcon, sLargeIcon, boEnabled);
    
    listItems.add(mapMenuItemInfo);
    
    mapItems.put(sIdMenu + "." + sIdItem, mapMenuItemInfo);
    
    Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
    List listItemsByMenu = (List) mapMenuInfo.get(sMENU_LISTITEMS);
    listItemsByMenu.add(mapMenuItemInfo);
    
    if(boEnabled) {
      listStartupEnabledFlags.add(sIdMenu + "." + sIdItem);
    }
  }
  
  protected
  boolean isEnabled(String sIdMenuItem)
  {
    Map mapMenuItemInfo = (Map) mapItems.get(sIdMenuItem);
    if(mapMenuItemInfo == null) return false;
    return ((Boolean) mapMenuItemInfo.get(sMENUITEM_ENABLED)).booleanValue();
  }
  
  /**
   * Imposta il flag di abilitazione a tutte le voci di menu'.
   *
   * @param boEnabled boolean
   */
  protected
  void setEnabledAll(boolean boEnabled)
  {
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      setMenuEnabled(mapMenuInfo, boEnabled);
    }
    
    for(int i = 0; i < listItems.size(); i++) {
      Map mapMenuItemInfo = (Map) listItems.get(i);
      setMenuItemEnabled(mapMenuItemInfo, boEnabled);
    }
  }
  
  /**
   * Imposta il flag di visibilita' a tutte le voci di menu'.
   *
   * @param boVisible boolean
   */
  protected
  void setVisibleAll(boolean boVisible)
  {
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      setMenuVisible(mapMenuInfo, boVisible);
    }
    
    for(int i = 0; i < listItems.size(); i++) {
      Map mapMenuItemInfo = (Map) listItems.get(i);
      setMenuItemVisible(mapMenuItemInfo, boVisible);
    }
  }
  
  protected
  void setStartupEnabledFlags()
  {
    for(int i = 0; i < listStartupEnabledFlags.size(); i++) {
      String sId = (String) listStartupEnabledFlags.get(i);
      if(sId.indexOf('.') >= 0) {
        // Voce di menu
        Map mapMenuItemInfo = (Map) mapItems.get(sId);
        if(mapMenuItemInfo == null) continue;
        setMenuItemEnabled(mapMenuItemInfo, true);
      }
      else {
        // Menu
        Map mapMenuInfo = (Map) mapMenu.get(sId);
        if(mapMenuInfo == null) continue;
        setMenuEnabled(mapMenuInfo, true);
      }
    }
  }
  
  protected
  boolean isAllMenuItemsDisabled(Map mapMenuInfo)
  {
    if(mapMenuInfo == null) return false;
    
    List listItemsByMenu = (List) mapMenuInfo.get(sMENU_LISTITEMS);
    for(int i = 0; i < listItemsByMenu.size(); i++) {
      Map mapItem = (Map) listItemsByMenu.get(i);
      if(((Boolean) mapItem.get(sMENUITEM_ENABLED)).booleanValue()) {
        return false;
      }
    }
    
    return true;
  }
  
  protected
  boolean isAllMenuItemsInvisible(Map mapMenuInfo)
  {
    if(mapMenuInfo == null) return false;
    
    List listItemsByMenu = (List) mapMenuInfo.get(sMENU_LISTITEMS);
    for(int i = 0; i < listItemsByMenu.size(); i++) {
      Map mapItem = (Map) listItemsByMenu.get(i);
      if(((Boolean) mapItem.get(sMENUITEM_VISIBLE)).booleanValue()) {
        return false;
      }
    }
    
    return true;
  }
  
  protected
  void setMenuEnabled(Map mapMenuInfo, boolean boEnabled)
  {
    if(mapMenuInfo == null) return;
    
    JMenu jMenu = (JMenu) mapMenuInfo.get(sMENU_JMENU);
    jMenu.setEnabled(boEnabled);
    
    JSideMenu jSideMenu = (JSideMenu) mapMenuInfo.get(sMENU_JSIDEMENU);
    jSideMenu.setEnabled(boEnabled);
    
    mapMenuInfo.put(sMENU_ENABLED, Boolean.valueOf(boEnabled));
  }
  
  protected
  void setMenuVisible(Map mapMenuInfo, boolean boVisible)
  {
    if(mapMenuInfo == null) return;
    
    JMenu jMenu = (JMenu) mapMenuInfo.get(sMENU_JMENU);
    jMenu.setVisible(boVisible);
    
    JSideMenu jSideMenu = (JSideMenu) mapMenuInfo.get(sMENU_JSIDEMENU);
    jSideMenu.setVisible(boVisible);
    
    mapMenuInfo.put(sMENU_VISIBLE, Boolean.valueOf(boVisible));
  }
  
  protected
  void setMenuItemEnabled(Map mapMenuItemInfo, boolean boEnabled)
  {
    if(mapMenuItemInfo == null) return;
    
    JMenuItem jMenuItem = (JMenuItem) mapMenuItemInfo.get(sMENUITEM_JMENUITEM);
    if(jMenuItem == null) {
      return;
    }
    jMenuItem.setEnabled(boEnabled);
    
    JSideMenuItem jSideMenuItem = (JSideMenuItem) mapMenuItemInfo.get(sMENUITEM_JSIDEMENUITEM);
    ImageIcon imageIcon = null;
    if(boEnabled) {
      imageIcon = (ImageIcon) mapMenuItemInfo.get(sMENUITEM_LARGEICON);
    }
    else {
      imageIcon = (ImageIcon) mapMenuItemInfo.get(sMENUITEM_DISABLEDICON);
    }
    jSideMenuItem.setIcon(imageIcon);
    
    mapMenuItemInfo.put(sMENUITEM_ENABLED, Boolean.valueOf(boEnabled));
  }
  
  protected
  void setMenuItemVisible(Map mapMenuItemInfo, boolean boVisible)
  {
    if(mapMenuItemInfo == null) return;
    
    JMenuItem jMenuItem = (JMenuItem) mapMenuItemInfo.get(sMENUITEM_JMENUITEM);
    if(jMenuItem == null) {
      return;
    }
    jMenuItem.setVisible(boVisible);
    
    String sIdMenu = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
    Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
    if(mapMenuInfo == null) {
      return;
    }
    
    boolean boOldFlagVisible = ((Boolean) mapMenuItemInfo.get(sMENUITEM_VISIBLE)).booleanValue();
    if(boOldFlagVisible == boVisible) return;
    
    mapMenuItemInfo.put(sMENUITEM_VISIBLE, Boolean.valueOf(boVisible));
    
    JSideMenu jSideMenu = (JSideMenu) mapMenuInfo.get(sMENU_JSIDEMENU);
    jSideMenu.removeAll();
    
    List listItemsByMenu = (List) mapMenuInfo.get(sMENU_LISTITEMS);
    for(int i = 0; i < listItemsByMenu.size(); i++) {
      Map mapItem = (Map) listItemsByMenu.get(i);
      if(((Boolean) mapItem.get(sMENUITEM_VISIBLE)).booleanValue()) {
        jSideMenu.add((JSideMenuItem) mapItem.get(sMENUITEM_JSIDEMENUITEM));
      }
    }
  }
  
  protected
  void buildMenu()
  {
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      
      jMenuBar.add(buildJMenu(mapMenuInfo));
      jSideMenuBar.add(buildJSideMenu(mapMenuInfo));
    }
    
    for(int i = 0; i < listItems.size(); i++) {
      Map mapMenuItemInfo = (Map) listItems.get(i);
      
      String sIdMenu = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
      String sIdItem = (String) mapMenuItemInfo.get(sMENUITEM_ID);
      
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      JMenu jmenu = (JMenu) mapMenuInfo.get(sMENU_JMENU);
      JSideMenu jSideMenu = (JSideMenu) mapMenuInfo.get(sMENU_JSIDEMENU);
      
      if(sIdItem.equals("-")) {
        jmenu.addSeparator();
      }
      else {
        jmenu.add(buildJMenuItem(mapMenuItemInfo));
        jSideMenu.add(buildJSideMenuItem(mapMenuItemInfo));
      }
    }
  }
  
  /**
   * E' necessario richiamare tale metodo quando si rendono invisibili
   * alcuni menu. Infatti nel componente JSideMenuBar si presentano
   * alcuni spazi vuoti che danno un brutto effetto.
   */
  protected
  void rebuidSideMenu()
  {
    jSideMenuBar.removeAll();
    
    for(int i = 0; i < listMenu.size(); i++) {
      Map mapMenuInfo = (Map) listMenu.get(i);
      Boolean oBoVisible = (Boolean) mapMenuInfo.get(sMENU_VISIBLE);
      if(oBoVisible != null && !oBoVisible.booleanValue()) continue;
      jSideMenuBar.add(buildJSideMenu(mapMenuInfo));
    }
    
    for(int i = 0; i < listItems.size(); i++) {
      Map mapMenuItemInfo = (Map) listItems.get(i);
      
      Boolean oBoVisible = (Boolean) mapMenuItemInfo.get(sMENUITEM_VISIBLE);
      if(oBoVisible != null && !oBoVisible.booleanValue()) continue;
      
      String sIdMenu = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
      String sIdItem = (String) mapMenuItemInfo.get(sMENUITEM_ID);
      
      Map mapMenuInfo = (Map) mapMenu.get(sIdMenu);
      JSideMenu jSideMenu = (JSideMenu) mapMenuInfo.get(sMENU_JSIDEMENU);
      
      if(!sIdItem.equals("-")) {
        jSideMenu.add(buildJSideMenuItem(mapMenuItemInfo));
      }
    }
  }
  
  protected
  JMenu buildJMenu(Map mapMenuInfo)
  {
    String sText = (String) mapMenuInfo.get(sMENU_TEXT);
    String sDescription = (String) mapMenuInfo.get(sMENU_DESCRIPTION);
    Character oMnemonic = (Character) mapMenuInfo.get(sMENU_MNEMONIC);
    boolean boEnabled = ((Boolean) mapMenuInfo.get(sMENU_ENABLED)).booleanValue();
    
    JMenu jMenu = new JMenu(sText);
    jMenu.addMouseListener(new Status_MouseListener(sDescription));
    jMenu.setToolTipText(sDescription);
    jMenu.setEnabled(boEnabled);
    
    if(oMnemonic != null) {
      jMenu.setMnemonic(oMnemonic.charValue());
    }
    
    mapMenuInfo.put(sMENU_JMENU, jMenu);
    
    return jMenu;
  }
  
  protected
  JSideMenu buildJSideMenu(Map mapMenuInfo)
  {
    String sText = (String) mapMenuInfo.get(sMENU_TEXT);
    String sDescription = (String) mapMenuInfo.get(sMENU_DESCRIPTION);
    boolean boEnabled = ((Boolean) mapMenuInfo.get(sMENU_ENABLED)).booleanValue();
    
    JSideMenu jSideMenu = new JSideMenu(sText);
    jSideMenu.setToolTipText(sDescription);
    jSideMenu.setEnabled(boEnabled);
    
    Color colorMenuBG = ThemeManager.transform(new Color(128, 128, 128));
    jSideMenu.setMenuBackgroundColor(colorMenuBG);
    
    mapMenuInfo.put(sMENU_JSIDEMENU, jSideMenu);
    
    return jSideMenu;
  }
  
  protected
  JMenuItem buildJMenuItem(Map mapMenuItemInfo)
  {
    String sIdItem = (String) mapMenuItemInfo.get(sMENUITEM_ID);
    String sIdMenu = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
    String sText = (String) mapMenuItemInfo.get(sMENUITEM_TEXT);
    ImageIcon oSmallIcon = (ImageIcon) mapMenuItemInfo.get(sMENUITEM_SMALLICON);
    String sDescription = (String) mapMenuItemInfo.get(sMENUITEM_DESCRIPTION);
    boolean boEnabled = ((Boolean) mapMenuItemInfo.get(sMENUITEM_ENABLED)).booleanValue();
    Character oMnemonic = (Character) mapMenuItemInfo.get(sMENUITEM_MNEMONIC);
    
    JMenuItem jMenuItem = new JMenuItem();
    jMenuItem.setText(sText);
    if(oSmallIcon != null) {
      jMenuItem.setIcon(oSmallIcon);
    }
    jMenuItem.addActionListener(new AMenuManager_ActionListner(this, sIdMenu + "." + sIdItem));
    jMenuItem.addMouseListener(new Status_MouseListener(sDescription));
    jMenuItem.setEnabled(boEnabled);
    if(oMnemonic != null) {
      jMenuItem.setMnemonic(oMnemonic.charValue());
    }
    
    mapMenuItemInfo.put(sMENUITEM_JMENUITEM, jMenuItem);
    
    return jMenuItem;
  }
  
  protected
  JSideMenuItem buildJSideMenuItem(Map mapMenuItemInfo)
  {
    String sIdItem = (String) mapMenuItemInfo.get(sMENUITEM_ID);
    String sIdMenu = (String) mapMenuItemInfo.get(sMENUITEM_IDMENU);
    String sText = (String) mapMenuItemInfo.get(sMENUITEM_TEXT);
    ImageIcon oSmallIcon = (ImageIcon) mapMenuItemInfo.get(sMENUITEM_SMALLICON);
    ImageIcon oLargeIcon = (ImageIcon) mapMenuItemInfo.get(sMENUITEM_LARGEICON);
    if(oSmallIcon == null) {
      oSmallIcon = oLargeIcon;
    }
    ImageIcon oDisabledIcon = (ImageIcon) mapMenuItemInfo.get(sMENUITEM_DISABLEDICON);
    boolean boEnabled = ((Boolean) mapMenuItemInfo.get(sMENUITEM_ENABLED)).booleanValue();
    
    JSideMenuItem jSideMenuItem = null;
    if(boEnabled) {
      jSideMenuItem = new MyJSideMenuItem(sText, oSmallIcon, oLargeIcon);
    }
    else {
      jSideMenuItem = new MyJSideMenuItem(sText, oSmallIcon, oDisabledIcon);
    }
    jSideMenuItem.addActionListener(new AMenuManager_ActionListner(this, sIdMenu + "." + sIdItem));
    
    mapMenuItemInfo.put(sMENUITEM_JSIDEMENUITEM, jSideMenuItem);
    
    return jSideMenuItem;
  }
  
  protected
  Map buildMenuInfo(String sId, String sText, String sDescription, char cMnemonic, boolean boEnabled)
  {
    Map mapResult = new HashMap();
    mapResult.put(sMENU_ID, sId);
    mapResult.put(sMENU_TEXT, sText);
    mapResult.put(sMENU_DESCRIPTION, sDescription);
    mapResult.put(sMENU_ENABLED, Boolean.valueOf(boEnabled));
    mapResult.put(sMENU_VISIBLE, Boolean.TRUE);
    mapResult.put(sMENU_LISTITEMS, new ArrayList());
    if(cMnemonic != '\0') {
      mapResult.put(sMENU_MNEMONIC, new Character(cMnemonic));
    }
    
    return mapResult;
  }
  
  protected
  Map buildMenuItemInfo(String sIdMenu, String sIdItem, String sText, String sDescription, char cMnemonic, String sSmallIcon, String sLargeIcon, boolean boEnabled)
  {
    Map mapResult = new HashMap();
    
    ImageIcon oSmallIcon = null;
    if(sSmallIcon != null) {
      oSmallIcon = ResourcesMgr.getImageIcon(sSmallIcon);
    }
    ImageIcon oLargeIcon = ResourcesMgr.getImageIcon(sLargeIcon);
    ImageIcon oDisabledIcon = new ImageIcon(GrayFilter.createDisabledImage(oLargeIcon.getImage()));
    
    mapResult.put(sMENUITEM_ID, sIdItem);
    mapResult.put(sMENUITEM_IDMENU, sIdMenu);
    mapResult.put(sMENUITEM_TEXT, sText);
    mapResult.put(sMENUITEM_DESCRIPTION, sDescription);
    mapResult.put(sMENUITEM_SMALLICON, oSmallIcon);
    mapResult.put(sMENUITEM_LARGEICON, oLargeIcon);
    mapResult.put(sMENUITEM_DISABLEDICON, oDisabledIcon);
    mapResult.put(sMENUITEM_ENABLED, Boolean.valueOf(boEnabled));
    mapResult.put(sMENUITEM_VISIBLE, Boolean.TRUE);
    if(cMnemonic != '\0') {
      mapResult.put(sMENUITEM_MNEMONIC, new Character(cMnemonic));
    }
    
    return mapResult;
  }
  
  protected
  void addMenuSettings()
  {
    addMenu(sMENU_SETTINGS, "&Impostazioni", "Menu Impostazioni", true);
    
    addMenuItem(sMENU_SETTINGS,  // Id Menu
    sMENUITEM_PASSWORD,  // Id Item
    "&Password", // Testo
    "Per cambiare password",    // Descrizione
    IConstants.sICON_PASSWORD,  // Small Icon
    IConstants.sICON_PASSWORD,  // Large Icon
    true);           // Enabled
    
    addMenuItem(sMENU_SETTINGS, // Id Menu
    sMENUITEM_OPTIONS,  // Id Item
    "&Opzioni", // Testo
    "Apre il dialog Opzioni",  // Descrizione
    IConstants.sICON_SETTINGS, // Small Icon
    IConstants.sICON_SETTINGS, // Large Icon
    true);                     // Enabled
  }
  
  public
  void enableMenuSettings()
  {
    setEnabled(sMENU_SETTINGS, sMENUITEM_PASSWORD, true);
    setEnabled(sMENU_SETTINGS, sMENUITEM_OPTIONS, true);
  }
  
  private static
  String getText(String sText)
  {
    if(sText == null) {
      return "";
    }
    int i = sText.indexOf('&');
    if(i < 0) {
      return sText;
    }
    if(sText.length() == i + 1) {
      return sText.substring(0, i);
    }
    return sText.substring(0, i) + sText.substring(i + 1);
  }
  
  private static
  char getMnemonic(String sText)
  {
    if(sText == null) {
      return '\0';
    }
    int i = sText.indexOf('&');
    if(i < 0) {
      return '\0';
    }
    if(sText.length() == i + 1) {
      return '\0';
    }
    return sText.charAt(i + 1);
  }
  
  public
  String toString()
  {
    String sResult = "";
    
    if(listItems == null) return sResult;
    
    for(int i = 0; i < listItems.size(); i++) {
      Map mapMenuItem = (Map) listItems.get(i);
      String sIdItem = (String) mapMenuItem.get(sMENUITEM_ID);
      if(sIdItem == null || sIdItem.equals("-")) continue;
      String sIdMenu = (String) mapMenuItem.get(sMENUITEM_IDMENU);
      Boolean boEnabled = (Boolean) mapMenuItem.get(sMENUITEM_ENABLED);
      sResult += sIdMenu + "." + sIdItem + "\t" + boEnabled + "\n";
    }
    
    return sResult;
  }
  
  class MyJSideMenuItem extends JSideMenuItem
  {
    public MyJSideMenuItem()
    {
      super();
    }
    
    public MyJSideMenuItem(String text)
    {
      super(text);
    }
    
    public MyJSideMenuItem(String text, Icon smallIcon, Icon largeIcon)
    {
      super(text, smallIcon, largeIcon);
    }
    
    public int getPreferredHeightWithGivenWidth(int width)
    {
      return getViewType() != 1 ? 54 + iGapItems : 20 + iGapItems;
    }
  }
}
