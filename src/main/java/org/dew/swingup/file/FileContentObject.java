package org.dew.swingup.file;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Implementazione di ContentObject che consente di inserire il contenuto
 * di un file.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes"})
public
class FileContentObject extends ContentObject
{
  public
  FileContentObject()
  {
  }
  
  public
  FileContentObject(String sFileName)
  {
    this.data = sFileName;
  }
  
  public
  FileContentObject(URL urlFile)
  {
    this.data = urlFile;
  }
  
  public
  void build()
  {
    content = new StringBuffer();
    
    InputStream is;
    int b;
    if(data == null) {
      return;
    }
    try {
      if(data instanceof URL) {
        is = ((URL) data).openStream();
      }
      else {
        is = new FileInputStream(data.toString());
      }
      while((b = is.read()) != -1) {
        content.append((char) b);
      }
      is.close();
    }
    catch(Exception ex) {
      ex.printStackTrace();
      content = new StringBuffer();
    }
    
    Iterator itAttributes = attributes.keySet().iterator();
    while(itAttributes.hasNext()) {
      replaceContent((String)itAttributes.next());
    }
  }
  
  private
  void replaceContent(String propertyName)
  {
    if(content == null) {
      return;
    }
    String pattern = CopyFile.BEGIN_PARAM + propertyName + CopyFile.END_PARAM;
    int i = indexOf(content, pattern, 0);
    while(i >= 0) {
      String value = attributes.getProperty(propertyName, "");
      content.delete(i, i + pattern.length());
      content.insert(i, value);
      i = indexOf(content, pattern, i + value.length());
    }
  }
  
  private
  int indexOf(StringBuffer sb, String pattern, int beginIndex)
  {
    if((beginIndex + pattern.length()) > sb.length()) {
      return -1;
    }
    char c = pattern.charAt(0);
    for(int i = beginIndex; i < sb.length(); i++) {
      if(sb.charAt(i) == c) {
        if(match(sb, i, pattern)) {
          return i;
        }
      }
    }
    return -1;
  }
  
  private
  boolean match(StringBuffer sb, int beginIndex, String pattern)
  {
    if((beginIndex + pattern.length()) > sb.length()) {
      return false;
    }
    int j = 0;
    for(int i = beginIndex; i < beginIndex + pattern.length(); i++) {
      if(sb.charAt(i) != pattern.charAt(j)) {
        return false;
      }
      j++;
    }
    return true;
  }
}
