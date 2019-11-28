package org.dew.swingup.util;

import java.util.*;
import java.text.*;

import org.dew.swingup.ResourcesMgr;

/**
 * Classe di utilita' per la definizione di un modello per le tabelle
 * con la funzionalita' di ordinamento.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public
class SimpleTableModelForSorter extends ATableModelForSorter
{
  protected String[] asCOLUMNS;
  protected Object[] aoSYMBOLICS;
  protected Class[]  acCOLUMNS_CLASSES;
  protected int[] aiEDITABLE_COLUMNS;
  protected int[] aiEDITABLE_ROWS;
  
  protected String sDateFormatPattern;
  protected DateFormat df            = ResourcesMgr.getDefaultDateFormat();
  protected DecimalFormat dfCurrency;
  protected List oListTimeFields     = new ArrayList();
  protected List oListCurrencyFields = new ArrayList();
  protected boolean boEditable = true;
  
  // AbstractTableModel is Serializable!
  static final long serialVersionUID = 6755075420598368944L;
  
  /**
   * Costruttore utilizzato quando i dati sono liste di liste.
   *
   * @param asCOLUMNS String[]
   */
  public
  SimpleTableModelForSorter(String[] asCOLUMNS)
  {
    super();
    this.asCOLUMNS = asCOLUMNS;
  }
  
  /**
   * Costruttore utilizzato quando i dati sono liste di mappe.
   *
   * @param asCOLUMNS String[]
   * @param aoSYMBOLICS Object[]
   */
  public
  SimpleTableModelForSorter(String[] asCOLUMNS, Object[] aoSYMBOLICS)
  {
    super();
    this.asCOLUMNS = asCOLUMNS;
    this.aoSYMBOLICS = aoSYMBOLICS;
  }
  
  /**
   * Costruttore utilizzato quando i dati sono liste di liste.
   *
   * @param oData List
   * @param asCOLUMNS String[]
   */
  public
  SimpleTableModelForSorter(List oData, String[] asCOLUMNS)
  {
    super(oData);
    this.asCOLUMNS = asCOLUMNS;
  }
  
  /**
   * Costruttore utilizzato quando i dati sono liste di liste.
   *
   * @param oData List
   * @param asCOLUMNS String[]
   * @param acCOLUMNS_CLASSES Class[]
   */
  public
  SimpleTableModelForSorter(List oData, String[] asCOLUMNS,
    Class[] acCOLUMNS_CLASSES)
  {
    super(oData);
    this.asCOLUMNS = asCOLUMNS;
    this.acCOLUMNS_CLASSES = acCOLUMNS_CLASSES;
  }
  
  /**
   * Costruttore utilizzato quando i dati sono liste di mappe.
   *
   * @param oData List
   * @param asCOLUMNS String[]
   * @param aoSYMBOLICS Object[]
   */
  public
  SimpleTableModelForSorter(List oData, String[] asCOLUMNS, Object[] aoSYMBOLICS)
  {
    super(oData);
    this.asCOLUMNS = asCOLUMNS;
    this.aoSYMBOLICS = aoSYMBOLICS;
  }
  
  /**
   * Costruttore utilizzato quando i dati sono liste di mappe.
   *
   * @param oData List
   * @param asCOLUMNS String[]
   * @param aoSYMBOLICS Object[]
   * @param acCOLUMNS_CLASSES Class[]
   */
  public
  SimpleTableModelForSorter(List oData, String[] asCOLUMNS,
    Object[] aoSYMBOLICS, Class[] acCOLUMNS_CLASSES)
  {
    super(oData);
    this.asCOLUMNS = asCOLUMNS;
    this.aoSYMBOLICS = aoSYMBOLICS;
    this.acCOLUMNS_CLASSES = acCOLUMNS_CLASSES;
  }
  
  /**
   * Imposta il pattern utilizzato per la visualizzazione delle date.
   *
   * @param sDateFormatPattern String
   */
  public
  void setDateFormatPattern(String sDateFormatPattern)
  {
    this.sDateFormatPattern = sDateFormatPattern;
    df = new SimpleDateFormat(sDateFormatPattern);
  }
  
  /**
   * Ottiene il pattern utilizzato per la visualizzazione delle date.
   *
   * @return String
   */
  public
  String getDateFormatPattern()
  {
    if(sDateFormatPattern == null) {
      return "dd/MM/yyyy";
    }
    
    return sDateFormatPattern;
  }
  
  /**
   * Imposta il flag editabile nel modello della tabella.
   * Tale flag ha effetto solo se sono stati specificati campi editabili.
   * Se si imposta a false tutti campi risultano non editabili.
   *
   * @param boEditable boolean
   */
  public
  void setEditable(boolean boEditable)
  {
    this.boEditable = boEditable;
  }
  
  /**
   * Ritorna il flag editabile del modello della tabella;
   *
   * @return boolean
   */
  public
  boolean isEditable()
  {
    return boEditable;
  }
  
  /**
   * Imposta le classi delle colonne.
   *
   * @param acCOLUMNS_CLASSES Class[]
   */
  public
  void setColumnsClasses(Class[] acCOLUMNS_CLASSES)
  {
    this.acCOLUMNS_CLASSES = acCOLUMNS_CLASSES;
  }
  
  /**
   * Imposta le colonne editabili attraverso un array di interi.
   *
   * @param aiEDITABLE_COLUMNS int[]
   */
  public
  void setEditableColumns(int[] aiEDITABLE_COLUMNS)
  {
    this.aiEDITABLE_COLUMNS = aiEDITABLE_COLUMNS;
  }
  
  /**
   * Imposta le righe editabili attraverso un array di interi.
   *
   * @param aiEDITABLE_ROWS int[]
   */
  public
  void setEditableRows(int[] aiEDITABLE_ROWS)
  {
    this.aiEDITABLE_ROWS = aiEDITABLE_ROWS;
  }
  
  /**
   * Imposta le colonne editabili attraverso un array di simbolici.
   * Utilizzabile quando i dati sono liste di mappe.
   *
   * @param aoEDITABLE_SYMBOLICS Object[]
   */
  public
  void setEditableColumns(Object[] aoEDITABLE_SYMBOLICS)
  {
    if(aoEDITABLE_SYMBOLICS == null) {
      this.aiEDITABLE_COLUMNS = null;
    }
    
    if(aoSYMBOLICS == null) {
      return;
    }
    
    aiEDITABLE_COLUMNS = new int[aoEDITABLE_SYMBOLICS.length];
    for(int i = 0; i < aoEDITABLE_SYMBOLICS.length; i++) {
      aiEDITABLE_COLUMNS[i] = -1;
      Object oEditableSymbolic = aoEDITABLE_SYMBOLICS[i];
      if(oEditableSymbolic == null) continue;
      for(int j = 0; j < aoSYMBOLICS.length; j++) {
        Object oSymbolic = aoSYMBOLICS[j];
        if(oSymbolic == null) continue;
        if(oEditableSymbolic.equals(oSymbolic)) {
          aiEDITABLE_COLUMNS[i] = j;
        }
      }
    }
  }
  
  /**
   * Aggiunge la colonna o il simbolico il cui valore deve essere mostrato
   * come un orario.
   *
   * @param oSymbolic_Or_Index Object
   */
  public
  void addTimeField(Object oSymbolic_Or_Index)
  {
    oListTimeFields.add(oSymbolic_Or_Index);
  }
  
  public
  void removeTimeField(Object oSymbolic_Or_Index)
  {
    oListTimeFields.remove(oSymbolic_Or_Index);
  }
  
  /**
   * Aggiunge la colonna o il simbolico il cui valore deve essere mostrato
   * come una valuta.
   *
   * @param oSymbolic_Or_Index Object
   */
  public
  void addCurrencyField(Object oSymbolic_Or_Index)
  {
    oListCurrencyFields.add(oSymbolic_Or_Index);
    if(dfCurrency == null) dfCurrency = ResourcesMgr.getDefaultDecimalFormat();
  }
  
  public
  void removeCurrencyField(Object oSymbolic_Or_Index)
  {
    oListCurrencyFields.remove(oSymbolic_Or_Index);
  }
  
  public
  int getColumnCount()
  {
    if(asCOLUMNS == null) return 0;
    return asCOLUMNS.length;
  }
  
  public
  String getColumnName(int iCol)
  {
    if(asCOLUMNS == null) return null;
    return asCOLUMNS[iCol];
  }
  
  public
  boolean isCellEditable(int iRow, int iCol)
  {
    if(aiEDITABLE_COLUMNS == null || aiEDITABLE_COLUMNS.length == 0) {
      return false;
    }
    
    if(!boEditable) return false;
    
    for(int i = 0; i < aiEDITABLE_COLUMNS.length; i++) {
      int iEditableCol = aiEDITABLE_COLUMNS[i];
      if(iEditableCol == iCol) {
        if(aiEDITABLE_ROWS == null || aiEDITABLE_ROWS.length == 0) {
          return true;
        }
        for(int j = 0; j < aiEDITABLE_ROWS.length; j++) {
          int iEditableRow = aiEDITABLE_ROWS[j];
          if(iEditableRow == iRow) {
            return true;
          }
        }
      }
    }
    
    return false;
  }
  
  public
  Class getColumnClass(int iCol)
  {
    if(acCOLUMNS_CLASSES == null ||
      acCOLUMNS_CLASSES.length <= iCol) {
      return String.class;
    }
    
    Class oColumnClass = acCOLUMNS_CLASSES[iCol];
    
    if(oColumnClass != null) {
      return oColumnClass;
    }
    
    return String.class;
  }
  
  public
  Object getObjectAt(int iRow, int iCol)
  {
    if(aoSYMBOLICS == null) {
      List oRecord = (List) oData.get(iRow);
      if(oRecord == null || iCol >= oRecord.size()) {
        return null;
      }
      
      return oRecord.get(iCol);
    }
    else {
      Map oRecord = (Map) oData.get(iRow);
      if(oRecord == null) {
        return null;
      }
      Object oSymbolic = aoSYMBOLICS[iCol];
      return oRecord.get(oSymbolic);
    }
  }
  
  public
  Object getValueAt(int iRow, int iCol)
  {
    Object oResult = null;
    
    if(aoSYMBOLICS == null) {
      List oRecord = (List) oData.get(iRow);
      if(oRecord == null || iCol >= oRecord.size()) {
        return null;
      }
      oResult = oRecord.get(iCol);
      
      if(oResult instanceof Date) {
        if(getColumnClass(iCol).equals(Date.class)) return oResult;
        
        if(oListTimeFields.contains(new Integer(iCol))) {
          return formatTime((Date) oResult);
        }
        else {
          return df.format((Date) oResult);
        }
      }
      else
      if(oResult instanceof Calendar) {
        if(getColumnClass(iCol).equals(Calendar.class)) return oResult;
        if(getColumnClass(iCol).equals(Date.class)) return ((Calendar) oResult).getTime();
        
        if(oListTimeFields.contains(new Integer(iCol))) {
          return formatTime((Calendar) oResult);
        }
        else {
          return df.format(((Calendar) oResult).getTime());
        }
      }
      else
      if(oResult instanceof Integer) {
        if(oListTimeFields.contains(new Integer(iCol))) {
          return formatTime((Integer) oResult);
        }
      }
      else
      if(oResult instanceof Double) {
        if(oListCurrencyFields.contains(new Integer(iCol))) {
          String sCurrency = dfCurrency.format(((Double) oResult).doubleValue());
          if(sCurrency.equals("-0,00")) sCurrency = "0,00";
          return sCurrency;
        }
      }
      
      return oResult;
    }
    else {
      Map oRecord = (Map) oData.get(iRow);
      if(oRecord == null) {
        return null;
      }
      Object oSymbolic = aoSYMBOLICS[iCol];
      oResult = oRecord.get(oSymbolic);
      
      if(oResult instanceof Date) {
        if(getColumnClass(iCol).equals(Date.class)) return oResult;
        
        if(oListTimeFields.contains(oSymbolic)) {
          return formatTime((Date) oResult);
        }
        else {
          return df.format((Date) oResult);
        }
      }
      else
      if(oResult instanceof Calendar) {
        if(getColumnClass(iCol).equals(Calendar.class)) return oResult;
        if(getColumnClass(iCol).equals(Date.class)) return ((Calendar) oResult).getTime();
        
        if(oListTimeFields.contains(oSymbolic)) {
          return formatTime((Calendar) oResult);
        }
        else {
          return df.format(((Calendar) oResult).getTime());
        }
      }
      else
      if(oResult instanceof Integer) {
        if(oListTimeFields.contains(oSymbolic)) {
          return formatTime((Integer) oResult);
        }
      }
      else
      if(oResult instanceof Double) {
        if(oListCurrencyFields.contains(oSymbolic)) {
          String sCurrency = dfCurrency.format(((Double) oResult).doubleValue());
          if(sCurrency.equals("-0,00")) sCurrency = "0,00";
          return sCurrency;
        }
      }
      
      return oResult;
    }
  }
  
  public
  void setValueAt(Object oVal, int iRow, int iCol)
  {
    if(aoSYMBOLICS == null) {
      List oRecord = (List) oData.get(iRow);
      
      if(oRecord == null || iCol >= oRecord.size()) {
        return;
      }
      
      // Se la classe della colonna e' diversa da String
      // si riporta il valore senza effettuare parsing.
      if(!getColumnClass(iCol).equals(String.class)) {
        oRecord.set(iCol, oVal);
        fireTableCellUpdated(iRow, iCol);
        return;
      }
      
      // Se la classe della colonna e' String, ma l'oggetto corrente
      // e' di un'altra classe si tenta il parsing.
      Object oCurrVal = oRecord.get(iCol);
      try{
        if(oCurrVal instanceof Date) {
          if(oVal instanceof Date) {
            oRecord.set(iCol, oVal);
          }
          else {
            oRecord.set(iCol, df.parse(oVal.toString()));
          }
        }
        else
        if(oCurrVal instanceof Calendar) {
          if(oVal instanceof Calendar) {
            oRecord.set(iCol, oVal);
          }
          else
          if(oVal instanceof Date) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime((Date) oVal);
            oRecord.set(iCol, gc);
          }
          else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(df.parse(oVal.toString()));
            oRecord.set(iCol, gc);
          }
        }
        else
        if(oCurrVal instanceof Integer) {
          oRecord.set(iCol, new Integer(oVal.toString()));
        }
        else
        if(oCurrVal instanceof Double) {
          oRecord.set(iCol, new Double(oVal.toString()));
        }
        else {
          oRecord.set(iCol, oVal);
        }
        fireTableCellUpdated(iRow, iCol);
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    else {
      Map oRecord = (Map) oData.get(iRow);
      if(oRecord == null) {
        return;
      }
      Object oSymbolic = aoSYMBOLICS[iCol];
      
      if(oVal == null || oVal.toString().trim().length() == 0) {
        oRecord.remove(oSymbolic);
        return;
      }
      
      // Se la classe della colonna e' diversa da String
      // si riporta il valore senza effettuare parsing.
      if(!getColumnClass(iCol).equals(String.class)) {
        oRecord.put(oSymbolic, oVal);
        fireTableCellUpdated(iRow, iCol);
        return;
      }
      
      // Se la classe della colonna e' String, ma l'oggetto corrente
      // e' di un'altra classe si tenta il parsing.
      Object oCurrVal = oRecord.get(oSymbolic);
      try{
        if(oCurrVal instanceof Date) {
          if(oVal instanceof Date) {
            oRecord.put(oSymbolic, oVal);
          }
          else {
            oRecord.put(oSymbolic, df.parse(oVal.toString()));
          }
        }
        else
        if(oCurrVal instanceof Calendar) {
          if(oVal instanceof Calendar) {
            oRecord.put(oSymbolic, oVal);
          }
          else
          if(oVal instanceof Date) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime((Date) oVal);
            oRecord.put(oSymbolic, gc);
          }
          else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(df.parse(oVal.toString()));
            oRecord.put(oSymbolic, gc);
          }
        }
        else
        if(oCurrVal instanceof Integer) {
          oRecord.put(oSymbolic, new Integer(oVal.toString()));
        }
        else
        if(oCurrVal instanceof Double) {
          oRecord.put(oSymbolic, new Double(oVal.toString()));
        }
        else {
          oRecord.put(oSymbolic, oVal);
        }
        fireTableCellUpdated(iRow, iCol);
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  
  protected static
  String formatTime(Calendar oValue)
  {
    if(oValue == null) {
      return "";
    }
    int iHour  = oValue.get(Calendar.HOUR_OF_DAY);
    int iMinute = oValue.get(Calendar.MINUTE);
    if(iMinute < 10) {
      return iHour + ":0" + iMinute;
    }
    return iHour + ":" + iMinute;
  }
  
  protected static
  String formatTime(Date oValue)
  {
    if(oValue == null) {
      return "";
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(oValue);
    int iHour   = cal.get(Calendar.HOUR_OF_DAY);
    int iMinute = cal.get(Calendar.MINUTE);
    if(iMinute < 10) {
      return iHour + ":0" + iMinute;
    }
    return iHour + ":" + iMinute;
  }
  
  protected static
  String formatTime(Integer oValue)
  {
    if(oValue == null) {
      return "";
    }
    int iValue  = oValue.intValue();
    int iHour   = iValue / 100;
    int iMinute = iValue % 100;
    if(iMinute < 10) {
      return iHour + ":0" + iMinute;
    }
    return iHour + ":" + iMinute;
  }
}
