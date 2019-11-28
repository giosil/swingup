package org.dew.swingup.util;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.*;

import org.dew.swingup.ResourcesMgr;

/**
 * Implementazione di AutoCompleter che utilizza un file di testo per
 * l'autocompletamento.
 * Tale implementazione richiede che le voci presenti nel file siano
 * ordinate.
 * Rispetto alla versione originaria sono state introdotte due modifiche:
 * - introduzione di un limite relativo al numero di occorrenze;
 * - copia automatica della voce in presenza di una sola occorrenza.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class DataFileAutoCompleter extends AutoCompleter
{
  protected URL urlFile;
  protected boolean ignoreCase = false;
  protected boolean textContained = false;
  //Salvatore
  protected int iOccorrenze = 4;
  protected boolean bActive = true;
  //Abilita Disabilita l'autocompletamento
  protected boolean bAbilAutoComplete = false;
  protected String sValue;
  
  public
  DataFileAutoCompleter(JTextComponent comp)
  {
    super(comp, true);
  }
  
  public
  DataFileAutoCompleter(JTextComponent comp, boolean boEnableToggle)
  {
    super(comp, boEnableToggle);
  }
  
  public
  DataFileAutoCompleter(JTextComponent comp, String sResource)
  {
    this(comp, sResource, false);
  }
  
  public
  DataFileAutoCompleter(JTextComponent comp, File file)
  {
    this(comp, file, false);
  }
  
  public
  DataFileAutoCompleter(JTextComponent comp, URL urlFile)
  {
    this(comp, urlFile, false);
  }
  
  /**
   * L'URL della risorsa viene ottenuto dal metodo ResourcesMgr.getURLDataFile.
   *
   * @param comp JTextComponent
   * @param sResource String
   * @param ignoreCase boolean
   * @throws Exception
   */
  public
  DataFileAutoCompleter(JTextComponent comp, String sResource, boolean ignoreCase)
  {
    super(comp, true);
    urlFile = ResourcesMgr.getURLDataFile(sResource);
    if(urlFile == null) throw new RuntimeException("Resource " + sResource + " not found.");
    this.ignoreCase = ignoreCase;
  }
  
  public
  DataFileAutoCompleter(JTextComponent comp, File file, boolean ignoreCase)
  {
    super(comp);
    try {
      urlFile = file.toURI().toURL();
    }
    catch(Exception ex) {
      throw new RuntimeException("File " + file + " not found");
    }
    this.ignoreCase = ignoreCase;
  }
  
  public
  DataFileAutoCompleter(JTextComponent comp, URL urlFile, boolean ignoreCase)
  {
    super(comp);
    if(urlFile == null) throw new NullPointerException();
    this.urlFile = urlFile;
    this.ignoreCase = ignoreCase;
  }
  
  public
  void setDataFile(File file)
    throws Exception
  {
    urlFile = file.toURI().toURL();
  }
  
  public
  void setDataFile(URL urlFile)
    throws Exception
  {
    if(urlFile == null) throw new NullPointerException();
    this.urlFile = urlFile;
  }
  
  /**
   * L'URL della risorsa viene ottenuto dal metodo ResourcesMgr.getURLDataFile.
   *
   * @param sResource String
   * @throws Exception
   */
  public
  void setDataFile(String sResource)
    throws Exception
  {
    urlFile = ResourcesMgr.getURLDataFile(sResource);
    if(urlFile == null) throw new Exception("Resource " + sResource + " not found.");
  }
  
  public
  URL getURLDataFile()
  {
    return urlFile;
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
  
  public
  void setTextContained(boolean textContained)
  {
    this.textContained = textContained;
  }
  
  public
  boolean isTextContained()
  {
    return textContained;
  }
  //Salvatore
  public
  void setNumOccorrenze(int iOccorrenze)
  {
    this.iOccorrenze = iOccorrenze;
  }
  //Salvatore
  public
  void setAbilAutoComplete(boolean bAbil)
  {
    this.bAbilAutoComplete = bAbil;
  }
  //Salvatore
  public
  boolean getAbilAutoComplete()
  {
    return this.bAbilAutoComplete;
  }
  
  protected
  boolean updateListData()
  {
    String sText = textComp.getText();
    int iLength = sText.length();
    if(iLength == 0) {
      list.setListData(new Vector());
      return false;
    }
    
    Vector vMatch = new Vector();
    InputStream is = null;
    try {
      is = urlFile.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      boolean boAtLeastOneFound = false;
      
      // Ho preferito scrivere quattro cicli while in funzione delle opzioni
      // per massimizzare il piu' possibile le prestazioni dell'algoritmo di
      // ricerca.
      
      //Salvatore
      int i = 0;
      
      if(textContained) {
        if(ignoreCase) {
          while((sLine = br.readLine()) != null) {
            if(iLength > sLine.length()) {
              continue;
            }
            if(sLine.toUpperCase().indexOf(sText.toUpperCase()) >= 0) {
              //Salvatore
              if(++i > iOccorrenze)break;
              vMatch.add(sLine);
            }
          }
          
        }
        else {
          while((sLine = br.readLine()) != null) {
            if(iLength > sLine.length()) {
              continue;
            }
            if(sLine.indexOf(sText) >= 0) {
              //Salvatore
              if(++i > iOccorrenze)break;
              vMatch.add(sLine);
            }
          }
        }
      }
      else {
        if(ignoreCase) {
          while((sLine = br.readLine()) != null) {
            if(iLength > sLine.length()) {
              continue;
            }
            if(sText.equalsIgnoreCase(sLine.substring(0, iLength))) {
              //Salvatore
              if(++i > iOccorrenze)break;
              vMatch.add(sLine);
              boAtLeastOneFound = true;
            }
            else {
              // In presenza di un file con voci ordinate
              // quando non si e' ottenuto il match e si e' trovato
              // almeno un elemento allora si puo' saltare la lettura
              // delle voci successive.
              if(boAtLeastOneFound) break;
            }
          }
        }
        else {
          while((sLine = br.readLine()) != null) {
            if(iLength > sLine.length()) {
              continue;
            }
            if(sLine.startsWith(sText)) {
              //Salvatore
              if(++i > iOccorrenze)break;
              vMatch.add(sLine);
              boAtLeastOneFound = true;
            }
            else {
              // In presenza di un file con voci ordinate
              // quando non si e' ottenuto il match e si e' trovato
              // almeno un elemento allora si puo' saltare la lettura
              // delle voci successive.
              if(boAtLeastOneFound) break;
            }
          }
        }
      }
      //Salvatore
      if(i == 1 && bActive && bAbilAutoComplete) {
        sValue = vMatch.get(0).toString();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            acceptedListItem(sValue);
          }
        });
        bActive = false;
      }
      else if(i != 1 && bAbilAutoComplete) {
        bActive = true;
      }
    }
    catch(Exception ex) {
      list.setListData(new Vector());
      ex.printStackTrace();
      return false;
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {};
    }
    
    list.setListData(vMatch);
    return true;
  }
  
  protected
  void acceptedListItem(String sSelectedItem)
  {
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
