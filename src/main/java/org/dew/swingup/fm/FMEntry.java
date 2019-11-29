package org.dew.swingup.fm;

import java.util.*;

@SuppressWarnings({"rawtypes"})
public
class FMEntry
{
  private String path;
  private String name;
  private String type;
  private String ext;
  private long length;
  private Date lastModified;
  private Date sysDateTime;
  private int countSubdir;
  private int countFiles;
  
  public FMEntry()
  {
  }
  
  public FMEntry(Map map)
  {
    if(map == null) return;
    path         = (String) map.get("p");
    name         = (String) map.get("n");
    type         = (String) map.get("t");
    lastModified = (Date)   map.get("d");
    length       = getLong(map.get("l"));
    countSubdir  = getInt(map.get("cd"));
    countFiles   = getInt(map.get("cf"));
    sysDateTime  = (Date)  map.get("dt");
    ext          = getExtensionFromNameOrPath();
  }
  
  public FMEntry(String sPath)
  {
    path   = sPath;
    name   = FMUtils.getFileName(sPath);
    type   = "f";
    ext    = getExtensionFromNameOrPath();
    lastModified = new Date();
    length = -1;
  }
  
  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
    this.ext  = getExtensionFromNameOrPath();
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
    this.ext  = getExtensionFromNameOrPath();
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public long getLength() {
    return length;
  }
  
  public void setLength(long length) {
    this.length = length;
  }
  
  public Date getLastModified() {
    return lastModified;
  }
  
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  
  public boolean isDirectory() {
    return type == null || type.equalsIgnoreCase("d");
  }
  
  public boolean isFile() {
    return type == null || type.equalsIgnoreCase("f");
  }
  
  public Date getSysDateTime() {
    return sysDateTime;
  }
  
  public void setSysDateTime(Date sysDateTime) {
    this.sysDateTime = sysDateTime;
  }
  
  public int getCountSubdir() {
    return countSubdir;
  }
  
  public void setCountSubdir(int countSubdir) {
    this.countSubdir = countSubdir;
  }
  
  public int getCountFiles() {
    return countFiles;
  }
  
  public void setCountFiles(int countFiles) {
    this.countFiles = countFiles;
  }
  
  public String getExtension() {
    return ext;
  }
  
  public void setExtension(String ext) {
    if(ext != null && ext.length() > 0) {
      this.ext = ext.toLowerCase();
    }
    else {
      this.ext = getExtensionFromNameOrPath();
    }
  }
  
  public long getKLength() {
    return length / 1024l;
  }
  
  public
  boolean isHidden()
  {
    if(name != null && name.length() > 1) {
      char c0 = name.charAt(0);
      char c1 = name.charAt(1);
      return c0 == '.' && c1 != ' ' && c1 != '.';
    }
    return false;
  }
  
  public
  boolean isExecutable()
  {
    if(ext == null || ext.length() == 0) return true;
    if(ext.equals("cmd") || ext.equals("bat")) return true;
    if(ext.equals("exe") || ext.equals("sh"))  return true;
    return false;
  }
  
  public
  boolean isJavaArchive()
  {
    if(ext == null) return false;
    if(ext.equals("jar") || ext.equals("lar")) return true;
    if(ext.equals("war") || ext.equals("ear")) return true;
    return false;
  }
  
  public
  boolean isArchive()
  {
    if(ext == null) return false;
    if(ext.equals("zip") || ext.equals("rar"))  return true;
    if(ext.equals("tar") || ext.equals("z"))    return true;
    if(ext.equals("gz")  || ext.equals("gzip")) return true;
    return false;
  }
  
  public
  boolean isTextFile()
  {
    if(ext == null) return false;
    if(ext.equals("xml")  || ext.equals("jrxml"))  return true;
    if(ext.equals("htm")  || ext.equals("html"))   return true;
    if(ext.equals("txt")  || ext.equals("text"))   return true;
    if(ext.equals("cfg")  || ext.equals("ini"))    return true;
    if(ext.equals("java") || ext.equals("mf"))     return true;
    if(ext.equals("conf") || ext.equals("config")) return true;
    if(ext.equals("prop") || ext.equals("properties"))  return true;
    if(ext.equals("log")  || ext.equals("dat"))    return true;
    if(ext.equals("js")   || ext.equals("css"))    return true;
    if(ext.equals("sh")   || ext.equals("sql"))    return true;
    if(ext.equals("php")  || ext.equals("jsp"))    return true;
    if(ext.equals("cpp")  || ext.equals("h"))      return true;
    if(ext.equals("c")    || ext.equals("inc"))    return true;
    if(ext.equals("bas")  || ext.equals("asp"))    return true;
    if(ext.equals("pl")   || ext.equals("py"))     return true;
    if(ext.equals("bat")  || ext.equals("cmd"))    return true;
    if(ext.equals("inf")  || ext.equals("info"))   return true;
    if(ext.equals("xsl")  || ext.equals("xsd"))    return true;
    return false;
  }
  
  public
  boolean isImageFile()
  {
    if(ext == null) return false;
    if(ext.equals("jpg") || ext.equals("jpeg")) return true;
    if(ext.equals("gif") || ext.equals("bmp"))  return true;
    if(ext.equals("jpe") || ext.equals("png"))  return true;
    if(ext.equals("tif") || ext.equals("tiff")) return true;
    if(ext.equals("ico") || ext.equals("dib"))  return true;
    if(ext.equals("xcf") || ext.equals("psd"))  return true;
    return false;
  }
  
  public
  boolean isDocFile()
  {
    if(ext == null) return false;
    if(ext.equals("pdf") || ext.equals("ps"))   return true;
    if(ext.equals("xps") || ext.equals("rtf"))  return true;
    if(ext.equals("doc") || ext.equals("docx")) return true;
    if(ext.equals("odt") || ext.equals("sdw"))  return true;
    return false;
  }
  
  public
  boolean isSpreadsheetFile()
  {
    if(ext == null) return false;
    if(ext.equals("xls") || ext.equals("xlsx")) return true;
    if(ext.equals("csv") || ext.equals("dif"))  return true;
    if(ext.equals("doc") || ext.equals("docx")) return true;
    if(ext.equals("ods") || ext.equals("sdc"))  return true;
    return false;
  }
  
  public String getStringSysDateTime() {
    if(lastModified == null) return "";
    return formatDate(lastModified, true, null) + " " + formatTime(lastModified);
  }
  
  public String getStringLastModified() {
    if(lastModified == null) return "";
    return formatDate(lastModified, true, null) + " " + formatTime(lastModified);
  }
  
  public String getStringLength() {
    if(length > 1024) {
      return String.valueOf(length / 1024l) + " Kbytes";
    }
    return String.valueOf(length) + " bytes";
  }
  
  public String getInfo() {
    String sResult = "";
    long lKLength = length / 1024l;
    if(path  != null && path.length() > 0) sResult += "Path          : " + path + "\n";
    if(name  != null && name.length() > 0) sResult += "Name          : " + name + "\n";
    if(type  != null && type.length() > 0) sResult += "Type          : " + type + "\n";
    if(lastModified != null) sResult += "Last Mod.     : " + formatDate(lastModified, true, null) + " " + formatTime(lastModified) + "\n";
    if(length  != 0) sResult += "Length        : " + length   + " bytes (" + lKLength + " Kbytes)\n";
    if(type != null && type.equalsIgnoreCase("d")) sResult += "Subdirectories: " + countSubdir + "\n";
    if(type != null && type.equalsIgnoreCase("d")) sResult += "Count Files   : " + countFiles  + "\n";
    if(sysDateTime != null) sResult += "Sys DateTime  : " + formatDate(sysDateTime, true, null) + " " + formatTime(sysDateTime) + "\n";
    return sResult;
  }
  
  public String getShortInfo() {
    String sResult = "";
    if(name != null && name.length() > 0) sResult += name + " ";
    if(lastModified != null) sResult += " " + formatDate(lastModified, true, null) + " " + formatTime(lastModified) + " ";
    if(type != null && type.equalsIgnoreCase("f")) {
      if(length >= 1024l) {
        long lKLength = length / 1024l;
        sResult += " (" + lKLength + " Kbytes)";
      }
      else {
        sResult += " (" + length + " bytes)";
      }
    }
    return sResult;
  }
  
  public
  String toString()
  {
    return name;
  }
  
  public
  boolean equals(Object object)
  {
    if(object instanceof FMEntry) {
      FMEntry fmEntry = (FMEntry) object;
      String sPath = fmEntry.getPath();
      if(sPath == null && path == null) return true;
      return sPath != null && sPath.equals(path);
    }
    return false;
  }
  
  public
  int hashCode()
  {
    if(path == null) return 0;
    return path.hashCode();
  }
  
  public static long getLong(Object oValue) {
    if(oValue == null) return 0l;
    if(oValue instanceof Number) {
      Number number = (Number) oValue;
      return number.longValue();
    }
    String sValue = oValue.toString();
    try {
      return Long.parseLong(sValue);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    return 0l;
  }
  
  public static int getInt(Object oValue) {
    if(oValue == null) return 0;
    if(oValue instanceof Number) {
      Number number = (Number) oValue;
      return number.intValue();
    }
    String sValue = oValue.toString();
    try {
      return Integer.parseInt(sValue);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    return 0;
  }
  
  public static String formatDate(Object oDate, boolean boWeekDay, String sLang) {
    Calendar cal = null;
    if(oDate instanceof Date) {
      cal = Calendar.getInstance();
      cal.setTime((Date) oDate);
    } else if(oDate instanceof Calendar) {
      cal = (Calendar) oDate;
    }
    boolean boIT = sLang == null || sLang.length() == 0 || sLang.toUpperCase().indexOf("IT") >= 0;
    if(cal == null) return "";
    int iYear = cal.get(java.util.Calendar.YEAR);
    int iMonth = cal.get(java.util.Calendar.MONTH) + 1;
    int iDay = cal.get(java.util.Calendar.DATE);
    String sDay   = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    String sMonth = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    if(!boWeekDay) return iDay + "/" + sMonth + "/" + iYear;
    int iDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
    String sDayOfWeek = "";
    switch(iDayOfWeek) {
      case Calendar.MONDAY:    sDayOfWeek = boIT ? "Lun" : "Mon"; break;
      case Calendar.TUESDAY:   sDayOfWeek = boIT ? "Mar" : "Tue"; break;
      case Calendar.WEDNESDAY: sDayOfWeek = boIT ? "Mer" : "Wed"; break;
      case Calendar.THURSDAY:  sDayOfWeek = boIT ? "Gio" : "Thu"; break;
      case Calendar.FRIDAY:    sDayOfWeek = boIT ? "Ven" : "Fri"; break;
      case Calendar.SATURDAY:  sDayOfWeek = boIT ? "Sab" : "Sat"; break;
      case Calendar.SUNDAY:    sDayOfWeek = boIT ? "Dom" : "Sun"; break;
    }
    return sDayOfWeek + " " + sDay + "/" + sMonth + "/" + iYear;
  }
  
  public static String formatTime(Object oDateTime) {
    Calendar cal = null;
    if(oDateTime instanceof Date) {
      cal = Calendar.getInstance();
      cal.setTime((Date) oDateTime);
    } else if(oDateTime instanceof Calendar) {
      cal = (Calendar) oDateTime;
    }
    if(cal == null) return "";
    int iHour   = cal.get(Calendar.HOUR_OF_DAY);
    int iMinute = cal.get(Calendar.MINUTE);
    int iSecond = cal.get(Calendar.SECOND);
    String sHour   = iHour   < 10 ? "0" + iHour   : String.valueOf(iHour);
    String sMinute = iMinute < 10 ? "0" + iMinute : String.valueOf(iMinute);
    String sSecond = iSecond < 10 ? "0" + iSecond : String.valueOf(iSecond);
    return sHour + ":" + sMinute + ":" + sSecond;
  }
  
  private
  String getExtensionFromNameOrPath()
  {
    String sFile = null;
    if(name != null) {
      sFile = name;
    }
    else
    if(path != null) {
      sFile = path;
    }
    else {
      return "";
    }
    String sResult = "";
    int iDot = sFile.lastIndexOf('.');
    if(iDot >= 0 && iDot < sFile.length() - 1) {
      sResult = sFile.substring(iDot + 1).toLowerCase();
    }
    return sResult;
  }
}
