package org.dew.swingup;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;

/**
 * Classe per la gestione dei temi grafici di swingup.
 *
 * @version 1.0
 */
public
class ThemeManager
{
  public final static List listThemes = new ArrayList();
  private static List listDelta = new ArrayList();
  private static boolean boFirstTime = true;
  private static String sCurrentTheme = null;
  
  static {
    addTheme("Default",    0,   0,   0);
    addTheme("Apricot",   40,  20,   0);
    addTheme("Blue",      -5,  -5,  60);
    addTheme("Brown",      0, -20, -40);
    addTheme("Emerald",  -10,   2,   2);
    addTheme("Graphite", -40, -40, -40);
    addTheme("Green",    -20,   0, -20);
    addTheme("Ice",       50,  50,  50);
    addTheme("Pea",        0,  25,   0);
    addTheme("Pink",       0, -25, -25);
    addTheme("Sand",      20,  20,   0);
    addTheme("Violet",     0, -20,   0);
    addTheme("Yellow",    20,  20, -30);
  }
  
  /**
   * Aggiunge un tema.
   *
   * @param sName String
   * @param iDR int
   * @param iDG int
   * @param iDB int
   */
  public static
  void addTheme(String sName, int iDR, int iDG, int iDB)
  {
    listThemes.add(sName);
    int[] aiDelta = new int[3];
    aiDelta[0] = iDR;
    aiDelta[1] = iDG;
    aiDelta[2] = iDB;
    listDelta.add(aiDelta);
  }
  
  /**
   * Rimuove un tema.
   *
   * @param sName String
   */
  public static
  void removeTheme(String sName)
  {
    int i = listThemes.indexOf(sName);
    if(i < 0) {
      return;
    }
    listThemes.remove(i);
    listDelta.remove(i);
  }
  
  /**
   * Rimuove tutti i temi presenti.
   */
  public static
  void removeAllThemes()
  {
    listThemes.clear();
    listDelta.clear();
  }
  
  /**
   * Imposta il tema corrente.
   * Occorre aggiornare i componenti grafici.
   *
   * @param sName String
   * @return boolean
   */
  public static
  boolean setTheme(String sName)
  {
    sCurrentTheme = sName;
    if(sName == null) {
      sCurrentTheme = "Default";
    }
    
    if(sCurrentTheme.equals("Default") && boFirstTime) {
      boFirstTime = false;
      return true;
    }
    
    int i = listThemes.indexOf(sCurrentTheme);
    if(i < 0) {
      boFirstTime = false;
      return false;
    }
    
    int[] aiDelta = (int[]) listDelta.get(i);
    updateUIManager(aiDelta[0], aiDelta[1], aiDelta[2]);
    
    boFirstTime = false;
    return true;
  }
  
  /**
   * Restituisce il tema corrente.
   *
   * @return String
   */
  public static
  String getCurrentTheme()
  {
    return sCurrentTheme;
  }
  
  /**
   * Aggiorna i colori di UIManager applicando un delta per ogni componente.
   *
   * @param iDR int
   * @param iDG int
   * @param iDB int
   */
  public static
  void updateUIManager(int iDR, int iDG, int iDB)
  {
    List oKeys = new ArrayList();
    UIDefaults mapDef = UIManager.getLookAndFeelDefaults();
    Iterator oItKeys = mapDef.keySet().iterator();
    while(oItKeys.hasNext()) {
      Object oKey = oItKeys.next();
      oKeys.add(oKey.toString());
    }
    for(int i = 0; i < oKeys.size(); i++) {
      Object oKey = oKeys.get(i);
      Object oValue = mapDef.get(oKey);
      if(oValue instanceof Color) {
        Color c = (Color) oValue;
        UIManager.put(oKey, transform(c, iDR, iDG, iDB));
      }
    }
  }
  
  public static
  Color transform(Color color)
  {
    if(color == null) return null;
    
    if(sCurrentTheme == null) {
      return color;
    }
    
    int i = listThemes.indexOf(sCurrentTheme);
    if(i < 0) {
      return color;
    }
    
    int[] aiDelta = (int[]) listDelta.get(i);
    
    return transform(color, aiDelta[0], aiDelta[1], aiDelta[2]);
  }
  
  private static
  Color transform(Color c, int iDR, int iDG, int iDB)
  {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    
    r = r + iDR;
    if(r > 255) r = 255;
    if(r < 0) r = 0;
    
    g = g + iDG;
    if(g > 255) g = 255;
    if(g < 0) g = 0;
    
    b = b + iDB;
    if(b > 255) b = 255;
    if(b < 0) b = 0;
    
    return new Color(r, g, b);
  }
}
