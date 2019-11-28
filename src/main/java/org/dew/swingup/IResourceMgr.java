package org.dew.swingup;

/**
 * Interfaccia che raccoglie le entry del file di configurazione swingup.properties
 *
 * @version 1.0
 */
public
interface IResourceMgr
{
  public static final String sBUILD                 = "20191128.1030";
  
  public static final String sPREFIX                = "swingup";
  
  public static final String sAPP_NAME              = sPREFIX + ".name";
  public static final String sAPP_VERSION           = sPREFIX + ".version";
  public static final String sAPP_DESCRIPTION       = sPREFIX + ".description";
  public static final String sAPP_COPYRIGHT         = sPREFIX + ".copyright";
  public static final String sAPP_DAT               = sPREFIX + ".dat";
  public static final String sAPP_LOG               = sPREFIX + ".log";
  public static final String sAPP_LOGGER            = sPREFIX + ".logger";
  public static final String sAPP_PLAF              = sPREFIX + ".plaf";
  public static final String sAPP_THEME             = sPREFIX + ".theme";
  public static final String sAPP_LISTENER          = sPREFIX + ".listener";
  public static final String sAPP_MENUMANAGER       = sPREFIX + ".menu";
  public static final String sAPP_SESSIONMGR        = sPREFIX + ".session";
  public static final String sAPP_GUIMGR            = sPREFIX + ".gui";
  public static final String sAPP_DISABLED          = sPREFIX + ".disabled";
  public static final String sAPP_RPC               = sPREFIX + ".rpc";
  public static final String sAPP_RPC_URL           = sPREFIX + ".rpc.url";
  public static final String sAPP_RPC_BAK           = sPREFIX + ".rpc.bak";
  public static final String sAPP_RPC_TIMEOUT       = sPREFIX + ".rpc.timeout";
  public static final String sAPP_RPC_SMARTCARD     = sPREFIX + ".rpc.smartcard";
  public static final String sAPP_RPC_BASIC_AUTH    = sPREFIX + ".rpc.basicauth";
  public static final String sAPP_HELP              = sPREFIX + ".help";
  public static final String sAPP_DEBUG             = sPREFIX + ".debug";
  public static final String sAPP_DEMO              = sPREFIX + ".demo";
  public static final String sAPP_WORKPANEL         = sPREFIX + ".workpanel";
  public static final String sAPP_ERRORSENDER       = sPREFIX + ".errorsender";
  public static final String sAPP_SHOW_SETTINGS     = sPREFIX + ".show.settings";
  public static final String sAPP_SHOW_MENU         = sPREFIX + ".show.menu";
  public static final String sAPP_SHOW_TOOLBAR      = sPREFIX + ".show.toolbar";
  public static final String sAPP_SHOW_SIDEMENU     = sPREFIX + ".show.sidemenu";
  public static final String sAPP_SHOW_STATUSBAR    = sPREFIX + ".show.statusbar";
  public static final String sAPP_SHOW_DISCLAIMER   = sPREFIX + ".show.disclaimer";
  public static final String sAPP_STATUSBAR         = sPREFIX + ".statusbar";
  public static final String sAPP_AUTOLOCK          = sPREFIX + ".autolock";
  public static final String sAPP_AUTOCOMPLETION    = sPREFIX + ".autocompletion";
  public static final String sAPP_BUILD             = sPREFIX + ".build";
  public static final String sAPP_CDS               = sPREFIX + ".cds";
  public static final String sAPP_SMARTCARD         = sPREFIX + ".smartcard";
  public static final String sAPP_EDITORS_EXPORT    = sPREFIX + ".editors.export";
  public static final String sAPP_JVM               = sPREFIX + ".jvm";
  public static final String sAPP_ASK_CONFIRM_CLOSE = sPREFIX + ".ask_confirm_close";
  
  public static final String sJATEWAY_URL           = sPREFIX + ".jateway.url";
  public static final String sJATEWAY_BACKUP        = sPREFIX + ".jateway.bak";
  public static final String sJATEWAY_EXP_IN_DAYS   = sPREFIX + ".jateway.expiration_in_days";
  public static final String sJATEWAY_ID_SERVER     = sPREFIX + ".jateway.id_server";
  
  public static final String sGUILOGIN_IDSERVICE    = sPREFIX + ".guilogin.idservice";
  public static final String sGUILOGIN_DEFUSERNAME  = sPREFIX + ".guilogin.defuser";
  public static final String sGUILOGIN_DEFPASSWORD  = sPREFIX + ".guilogin.defpwd";
  public static final String sGUILOGIN_IDCLIENT     = sPREFIX + ".guilogin.idclient";
  public static final String sGUILOGIN_HIDEIDCLIENT = sPREFIX + ".guilogin.hideidclient";
  public static final String sGUILOGIN_EXP_MONTHS   = sPREFIX + ".guilogin.expired_months";
  public static final String sGUILOGIN_EXP_DAYS     = sPREFIX + ".guilogin.expired_days";
  public static final String sGUILOGIN_EXP_FIRSTA   = sPREFIX + ".guilogin.expired_firstaccess";
  public static final String sGUILOGIN_ID_SESSION   = sPREFIX + ".guilogin.idsession";
  public static final String sGUILOGIN_SSO_SESSION  = sPREFIX + ".guilogin.ssosession";
  public static final String sGUILOGIN_AUTOLOGIN    = sPREFIX + ".guilogin.autologin";
  public static final String sGUILOGIN_SLOT         = sPREFIX + ".guilogin.slot";
  public static final String sUSER_MESSAGE          = sPREFIX + ".user.message";
  
  public static final String sGUIPASSWORD_MINLENGTH = sPREFIX + ".guipassword.minlength";
  public static final String sGUIPASSWORD_TYPE      = sPREFIX + ".guipassword.type";
  
  public static final String sLOG_PREFIX = "[SWINGUP]";
}
