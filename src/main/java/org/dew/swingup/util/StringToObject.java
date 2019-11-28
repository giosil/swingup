package org.dew.swingup.util;

import java.util.*;

/**
 * Classe di utilita' che converte una stringa in un oggetto complesso
 * ad esempio costituito da liste e mappe.
 * Le liste sono delimitate da [ e ]
 * Le mappe sono delimitate da { e }
 * Gli elementi e le coppie sono separati da ,
 * Nella coppia, la chiave e' separata dal valore da =
 * Cio' che non e' una lista o una mappa viene convertito in stringa.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class StringToObject
{
  public static
  Object parse(String s)
  {
    if(s == null) {
      return null;
    }
    
    if(s.length() < 2) {
      return s;
    }
    
    char c0 = s.charAt(0);
    
    if(c0 == '[') {
      int iEndOfList = endOfList(s, 1);
      if(iEndOfList < 0) {
        return s;
      }
      else {
        return toList(s, 1, iEndOfList);
      }
    }
    else
    if(c0 == '{') {
      int iEndOfMap = endOfMap(s, 1);
      if(iEndOfMap < 0) {
        return s;
      }
      else {
        return toMap(s, 1, iEndOfMap);
      }
    }
    
    return s;
  }
  
  private static
  List toList(String s, int iBegin, int iEnd)
  {
    List oResult = new ArrayList();
    
    StringBuffer sbItem = new StringBuffer();
    boolean boEndItem = true;
    
    for(int i = iBegin; i < iEnd; i++) {
      char c = s.charAt(i);
      if(c == ',') {
        boEndItem = true;
        oResult.add(sbItem.toString().trim());
        sbItem = new StringBuffer();
      }
      else
      if(c == '[' && boEndItem) {
        int iEndOfList = endOfList(s, i + 1);
        if(iEndOfList > 0) {
          List oNestedList = toList(s, i + 1, iEndOfList);
          oResult.add(oNestedList);
          int iNextSep = nextSeparator(s, iEndOfList);
          if(iNextSep < 0) {
            return oResult;
          }
          i = iNextSep;
        }
      }
      else
      if(c == '{' && boEndItem) {
        int iEndOfMap = endOfMap(s, i + 1);
        if(iEndOfMap > 0) {
          Map oNestedMap = toMap(s, i + 1, iEndOfMap);
          oResult.add(oNestedMap);
          int iNextSep = nextSeparator(s, iEndOfMap);
          if(iNextSep < 0) {
            return oResult;
          }
          i = iNextSep;
        }
      }
      else {
        boEndItem = false;
        sbItem.append(c);
      }
    }
    
    if(!boEndItem) {
      oResult.add(sbItem.toString().trim());
    }
    
    return oResult;
  }
  
  private static
  Map toMap(String s, int iBegin, int iEnd)
  {
    HashMap oResult = new HashMap();
    
    String sKey = null;
    
    StringBuffer sbVal = new StringBuffer();
    boolean boEndEntry = true;
    
    for(int i = iBegin; i < iEnd; i++) {
      char c = s.charAt(i);
      if(boEndEntry) {
        int iEndOfKey = endOfKey(s, i);
        if(iEndOfKey < 0) {
          return oResult;
        }
        sKey = s.substring(i, iEndOfKey).trim();
        sbVal = new StringBuffer();
        boEndEntry = false;
        i = iEndOfKey;
      }
      else {
        if(c == ',') {
          boEndEntry = true;
          oResult.put(sKey, sbVal.toString().trim());
        }
        else
        if(c == '[' && boEndEntry) {
          int iEndOfList = endOfList(s, i + 1);
          if(iEndOfList > 0) {
            List oNestedList = toList(s, i + 1, iEndOfList);
            oResult.put(sKey, oNestedList);
            int iNextSep = nextSeparator(s, iEndOfList);
            if(iNextSep < 0) {
              return oResult;
            }
            i = iNextSep;
          }
        }
        else
        if(c == '{' && boEndEntry) {
          int iEndOfMap = endOfMap(s, i + 1);
          if(iEndOfMap > 0) {
            Map oNestedMap = toMap(s, i + 1, iEndOfMap);
            oResult.put(sKey, oNestedMap);
            int iNextSep = nextSeparator(s, iEndOfMap);
            if(iNextSep < 0) {
              return oResult;
            }
            i = iNextSep;
          }
        }
        else {
          boEndEntry = false;
          sbVal.append(c);
        }
      }
    }
    
    if(!boEndEntry) {
      oResult.put(sKey, sbVal.toString().trim());
    }
    
    return oResult;
  }
  
  private static
  int endOfKey(String s, int iBegin)
  {
    if(s.length() <= iBegin) {
      return -1;
    }
    
    boolean boIsInNestedMap = false;
    
    for(int i = iBegin; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == '{') {
        boIsInNestedMap = true;
      }
      else
      if(c == '}') {
        if(boIsInNestedMap) {
          boIsInNestedMap = false;
        }
      }
      else
      if(c == '=' && !boIsInNestedMap) {
        return i;
      }
    }
    
    return -1;
  }
  
  private static
  int nextSeparator(String s, int iBegin)
  {
    if(s.length() <= iBegin) {
      return -1;
    }
    
    return s.indexOf(',', iBegin);
  }
  
  private static
  int endOfList(String s, int iBegin)
  {
    if(s.length() <= iBegin) {
      return -1;
    }
    
    boolean boIsEndOfNestedList = false;
    
    for(int i = iBegin; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == '[') {
        boIsEndOfNestedList = true;
      }
      else
      if(c == ']') {
        if(boIsEndOfNestedList) {
          boIsEndOfNestedList = false;
        }
        else {
          return i;
        }
      }
    }
    
    return -1;
  }
  
  private static
  int endOfMap(String s, int iBegin)
  {
    if(s.length() <= iBegin) {
      return -1;
    }
    
    boolean boIsEndOfNestedMap = false;
    
    for(int i = iBegin; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == '{') {
        boIsEndOfNestedMap = true;
      }
      else
      if(c == '}') {
        if(boIsEndOfNestedMap) {
          boIsEndOfNestedMap = false;
        }
        else {
          return i;
        }
      }
    }
    
    return -1;
  }
}
