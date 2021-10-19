package org.dew.swingup.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 * Implementazione di AutoCompleter che utilizza una Collection di valori
 * per l'autocompletamento.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class CollectionAutoCompleter extends AutoCompleter
{
  private Collection colData;
  private boolean ignoreCase;
  
  public
  CollectionAutoCompleter(Object comp)
  {
    super(comp);
  }
  
  public
  CollectionAutoCompleter(Object comp, boolean boEnableToggle)
  {
    super(comp, boEnableToggle);
  }
  
  public
  CollectionAutoCompleter(Object comp, Collection colData)
  {
    this(comp, colData, false);
  }
  
  public
  CollectionAutoCompleter(Object comp, Collection colData, boolean ignoreCase)
  {
    super(comp, true);
    if(colData == null) {
      this.colData = new ArrayList();
    }
    else {
      this.colData = colData;
    }
    this.ignoreCase = ignoreCase;
  }
  
  public
  void setData(Collection colData)
  {
    if(colData == null) {
      this.colData = new ArrayList();
    }
    else {
      this.colData = colData;
    }
  }
  
  public
  Collection getData()
  {
    return colData;
  }
  
  public
  void setIgnoreCase(boolean ignoreCase)
  {
    this.ignoreCase = ignoreCase;
  }
  
  public
  boolean isIgnoreCase()
  {
    return ignoreCase;
  }
  
  protected
  boolean updateListData()
  {
    if(textComp == null) return false;
    String sText = textComp.getText();
    int iLength = sText.length();
    if(iLength == 0) {
      list.setListData(new Vector(colData));
      return true;
    }
    
    Vector vMatch = new Vector();
    Iterator iter = colData.iterator();
    while(iter.hasNext()) {
      String sEntry = (String)iter.next();
      if(iLength >= sEntry.length()) {
        continue;
      }
      if(ignoreCase) {
        if(sText.equalsIgnoreCase(sEntry.substring(0, iLength))) {
          vMatch.add(sEntry);
        }
      }
      else {
        if(sEntry.startsWith(sText)) {
          vMatch.add(sEntry);
        }
      }
    }
    
    list.setListData(vMatch);
    return true;
  }
  
  protected
  void acceptedListItem(String sSelectedItem)
  {
    if(textComp == null) {
      return;
    }
    if(sSelectedItem == null) {
      return;
    }
    
    textComp.setText(sSelectedItem);
    
    // int iPrefixLen = textComp.getDocument().getLength();
    // try{
    //  textComp.getDocument().insertString(textComp.getCaretPosition(),
    //                                      sSelectedItem.substring(iPrefixLen),
    //                                      null);
    // }
    // catch(Exception ex) {
    //   ex.printStackTrace();
    // }
    
    popup.setVisible(false);
  }
}
