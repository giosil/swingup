package org.dew.swingup.impl;

import java.awt.*;
import java.awt.event.*;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.dialog.*;
import org.dew.swingup.util.*;

/**
 * Implementazione di default dell'interfaccia IGUIManager.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
public
class DefaultGUIManager implements IGUIManager
{
  protected ActionListener oActionListener;
  
  public static double dFactorSize     = 1.0;
  public static float  fDeriveFontSize = 12;
  
  public static int    RESOLUTION_1    = 2000;
  public static double FACTOR_SIZE_1   = 1.2d;
  public static float  FONT_SIZE_1     = 16;
  
  public static int    RESOLUTION_2    = 2500;
  public static double FACTOR_SIZE_2   = 1.3d;
  public static float  FONT_SIZE_2     = 17;
  
  public static int    RESOLUTION_3    = 3000;
  public static double FACTOR_SIZE_3   = 1.4d;
  public static float  FONT_SIZE_3     = 18;
  
  public static int    RES_PROFILE     = 0;
  
  public static
  int resizeForHRScreen(int iSize)
  {
    if(RES_PROFILE == 0)    return iSize;
    if(dFactorSize < 1.01d) return iSize;
     return (int)(iSize * dFactorSize);
  }
  
  public static
  String resizeHtmlFontForHRScreen(String sFontSize)
  {
    if(RES_PROFILE == 0) return sFontSize;
    if(sFontSize == null || sFontSize.length() == 0) {
      sFontSize = "0";
    }
    else
    if(sFontSize.startsWith("+")) {
      sFontSize = sFontSize.substring(1);
    }
    int iFontSize = 0;
    try { iFontSize = Integer.parseInt(sFontSize); } catch(Exception ex) {}
     int iNewFontSize = iFontSize + RES_PROFILE * 2;
    if(iNewFontSize > 0) {
      return "+" + iNewFontSize;
    }
    return String.valueOf(iNewFontSize);
  }
  
  public static
  int setRowHeightForHRScreen(JTable jtable)
  {
    if(jtable == null) return 0;
    int iRowHeight = jtable.getRowHeight();
    if(RES_PROFILE == 0)    return iRowHeight;
    if(dFactorSize < 1.01d) return iRowHeight;
    if(iRowHeight < 2) return iRowHeight;
     int iNewRowHeight = (int)(iRowHeight * dFactorSize);
    jtable.setRowHeight(iNewRowHeight);
    return iNewRowHeight;
  }
  
  public static
  void resizeForHRScreen(Component component)
  {
    if(component == null)   return;
    if(RES_PROFILE == 0)    return;
    if(dFactorSize < 1.01d) return;
    Dimension dimension = component.getSize();
    if(dimension == null) return;
     double dNewWidth  = dimension.getWidth()  * dFactorSize;
     double dNewHeight = dimension.getHeight() * dFactorSize;
    component.setSize(new Dimension((int) dNewWidth,(int) dNewHeight));
  }
  
  public static
  void setPreferredSizeForHRScreen(JComponent jcomponent)
  {
    if(jcomponent == null)  return;
    if(RES_PROFILE == 0)    return;
    if(dFactorSize < 1.01d) return;
    Dimension dimension = jcomponent.getPreferredSize();
    if(dimension == null) return;
     double dNewWidth  = dimension.getWidth()  * dFactorSize;
     double dNewHeight = dimension.getHeight() * dFactorSize;
    jcomponent.setPreferredSize(new Dimension((int) dNewWidth,(int) dNewHeight));
  }
  
  public static
  Font deriveFontForHRScreen(Font font)
  {
    if(font == null) return font;
    if(RES_PROFILE == 0)    return font;
    if(fDeriveFontSize < 8) return font;
    return font.deriveFont(fDeriveFontSize);
  }
  
  public static
  boolean updateUIManagerForHRScreen()
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    boolean update = false;
    if(screenSize.width > RESOLUTION_3) {
      dFactorSize     = FACTOR_SIZE_3;
      fDeriveFontSize = FONT_SIZE_3;
      RES_PROFILE     = 3;
      update          = true;
    }
    else
    if(screenSize.width > RESOLUTION_2) {
      dFactorSize     = FACTOR_SIZE_2;
      fDeriveFontSize = FONT_SIZE_2;
      RES_PROFILE     = 2;
      update          = true;
    }
    else
    if(screenSize.width > RESOLUTION_1) {
      dFactorSize     = FACTOR_SIZE_1;
      fDeriveFontSize = FONT_SIZE_1;
      RES_PROFILE     = 1;
      update          = true;
    }
    else {
      dFactorSize     = 1.0;
      fDeriveFontSize = 12;
      update          = RES_PROFILE != 0;
      RES_PROFILE     = 0;
    }
    if(update) {
      Set entries =  UIManager.getLookAndFeelDefaults().entrySet();
      Iterator iterator = entries.iterator();
      while(iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        Object oKey     = entry.getKey();
        String sKeyLC   = oKey.toString().toLowerCase();
        if(sKeyLC.indexOf("font") >= 0) {
          Font font = UIManager.getDefaults().getFont(oKey);
          if(font != null) {
            font = font.deriveFont(fDeriveFontSize);
            UIManager.put(oKey, font);
          }
        }
      }
    }
    return update;
  }
  
  public
  DefaultGUIManager()
  {
  }
  
  /**
   * Imposta l'oggetto ActionListener che permette di intercettare
   * le chiamate ai vari metodi di IGUIManager.
   *
   * @param al ActionListener
   */
  public
  void setActionListener(ActionListener al)
  {
    this.oActionListener = al;
  }
  
  /**
   * Metodo invocato per l'aggiornamento delle caratteristiche del L&F.
   *
   * @throws Exception
   */
  public
  void updateUIManager()
    throws Exception
  {
    fireActionEvent(sAC_UPDATEUI);
    
    String sDisabled = ResourcesMgr.config.getProperty(IResourceMgr.sAPP_DISABLED, "#0000FF");
    Color cDisabled  = Color.decode(sDisabled);
    
    UIManager.put("TextField.inactiveForeground", cDisabled);
    UIManager.put("TextArea.inactiveForeground",  cDisabled);
    UIManager.put("Label.disabledForeground",     cDisabled);
    UIManager.put("ComboBox.disabledForeground",  cDisabled);
    UIManager.put("CheckBox.disabledText",        cDisabled);
    
    updateUIManagerForHRScreen();
  }
  
  public
  boolean showGUILogin(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_LOGIN);
    return LoginDialog.showMe(jframe);
  }
  
  public
  boolean showGUIUserMessage(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_USERMESSAGE);
    TextDialog.showUserMessage();
    return true;
  }
  
  public
  boolean showGUITextMessage(JFrame jframe, String sText)
    throws Exception
  {
    fireActionEvent(sAC_TEXTMESSAGE);
    return TextDialog.showTextMessage(sText);
  }
  
  public
  boolean showGUITextMessage(JFrame jframe, String sText, String sTitle)
    throws Exception
  {
    fireActionEvent(sAC_TEXTMESSAGE);
    return TextDialog.showTextMessage(sText, sTitle);
  }
  
  public
  void showGUIHelp(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_HELP);
    String sTitle = ResourcesMgr.getWorkPanel().getCurrentActiveTitle();
    String sHelpDoc =  ResourcesMgr.getWorkPanel().getHelpDoc(sTitle);
    ResourcesMgr.getWorkPanel().show(new HtmlBrowser("Documentazione", IConstants.sICON_HELP, ResourcesMgr.getURLHelp(sHelpDoc)), "Documentazione");
  }
  
  public
  void showGUICDS(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_CDS);
    HtmlBrowser htmlBrowser = new HtmlBrowser("Comunicazione di servizio", IConstants.sICON_CDS);
    try {
      htmlBrowser.setHomePage(ResourcesMgr.getURLCDS());
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return;
    }
    ResourcesMgr.getWorkPanel().show(htmlBrowser, "Comunicazione di servizio");
  }
  
  public
  void showGUILock(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_LOCK);
    ResourcesMgr.getStatusBar().setText("Premere invio per sbloccare");
    ClockDialog.showMe(jframe);
  }
  
  public
  void showGUIAbout(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_ABOUT);
    AboutDialog.showMe(jframe);
  }
  
  public
  boolean showGUIChangePassword(JFrame jframe, boolean boMandatory)
    throws Exception
  {
    fireActionEvent(sAC_CHANGEPASSWORD);
    
    User user = ResourcesMgr.getSessionManager().getUser();
    if(user.getPassword() == null) {
      GUIMessage.showWarning("Funzionalit\340 non supportata");
      return false;
    }
    
    return ChangePasswordDialog.showMe(jframe, boMandatory);
  }
  
  public
  boolean showGUIOptions(JFrame jframe)
    throws Exception
  {
    fireActionEvent(sAC_OPTIONS);
    ResourcesMgr.getWorkPanel().show(new OptionsDialog(jframe));
    return true;
  }
  
  protected
  void fireActionEvent(String sActionCommand)
  {
    if(oActionListener == null) return;
    
    try {
      ActionEvent e = new ActionEvent(this,
        ActionEvent.ACTION_PERFORMED,
        sActionCommand);
      oActionListener.actionPerformed(e);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
