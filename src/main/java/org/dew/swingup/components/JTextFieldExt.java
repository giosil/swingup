package org.dew.swingup.components;

import java.awt.event.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.*;

import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.util.IValuable;

/**
 * Estensione di JTextField che permette l'impostazioni di un limite  di
 * caratteri e la digitazione esclusiva di lettere e/o numeri.
 * E' stata aggiunta la possibilita' di riportare un numero decimale (setCharsAllowed = iCHARS_DECIMAL)
 * con una determinata (setDecimal) precisione (2 predefinito).
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.1
 */
@SuppressWarnings({"serial"})
public
class JTextFieldExt extends JTextField implements IValuable
{
  public final static int iCASE_NOTMODIFIED = 0;
  public final static int iCASE_UPPER       = 1;
  public final static int iCASE_LOWER       = 2;
  
  public final static int iCHARS_ALL        = 0;
  public final static int iCHARS_LETTERS    = 1;
  public final static int iCHARS_NUMERIC    = 2;
  public final static int iCHARS_DECIMAL    = 3;
  public final static int iCHARS_QUANTITY     = 4;
  
  protected int iMaxLength = 1000;
  protected int iCase = 0;
  protected int iCharsAllowed = 0;
  protected boolean boSelectionOnFocus = false;
  protected int decimal;
  
  protected DecimalFormat decimalFormat = ResourcesMgr.getDefaultDecimalFormat();
  
  public JTextFieldExt()
  {
    super();
    this.addKeyListener(new KLJTextFieldExt());
    this.addFocusListener(new FLJTextFieldExt());
    this.getDocument().addDocumentListener(new DLJTextFieldExt());
  }
  
  public JTextFieldExt(int iMaxLength)
  {
    super();
    this.iMaxLength = iMaxLength;
    this.addKeyListener(new KLJTextFieldExt());
    this.addFocusListener(new FLJTextFieldExt());
    this.getDocument().addDocumentListener(new DLJTextFieldExt());
  }
  
  public JTextFieldExt(String sText, int iMaxLength)
  {
    super(sText);
    this.iMaxLength = iMaxLength;
    this.addKeyListener(new KLJTextFieldExt());
    this.addFocusListener(new FLJTextFieldExt());
    this.getDocument().addDocumentListener(new DLJTextFieldExt());
  }
  
  public 
  Object getValue() 
  {
    if(iCharsAllowed == iCHARS_DECIMAL) {
      String sText = getText();
      if(sText == null || sText.length() == 0) {
        return null;
      }
      return new Double(toDouble(sText));
    }
    if(iCharsAllowed == iCHARS_QUANTITY) {
      String sText = getText();
      if(sText == null || sText.length() == 0) {
        return null;
      }
      return new Integer(toInt(sText));
    }
    return getText();
  }
  
  public 
  void setValue(Object oValue)
  {
    if(oValue == null) {
      setText("");
    }
    else
    if(oValue instanceof Double || oValue instanceof BigDecimal || oValue instanceof Float) {
      setText(decimalFormat.format(((Double) oValue).doubleValue()));
    }
    else
    if(oValue instanceof Integer || oValue instanceof BigInteger) {
      setText(oValue.toString());
    }
    else
    if(oValue instanceof Date) {
      setText(ResourcesMgr.getDefaultDateFormat().format((Date) oValue));
    }
    else
    if(oValue instanceof Calendar) {
      setText(ResourcesMgr.getDefaultDateFormat().format(((Calendar) oValue).getTime()));
    }
    else {
      setText(oValue.toString());
    }
  }
  
  public
  void setMaxLength(int iMaxLength)
  {
    this.iMaxLength = iMaxLength;
  }
  
  public
  void setCase(int iCase)
  {
    this.iCase = iCase;
  }
  
  public
  void setSelectionOnFocus(boolean boSelectionOnFocus)
  {
    this.boSelectionOnFocus = boSelectionOnFocus;
  }
  
  public
  boolean isSelectionOnFocus()
  {
    return boSelectionOnFocus;
  }
  
  public
  void setCharsAllowed(int iCharsAllowed)
  {
    this.iCharsAllowed = iCharsAllowed;
  }
  
  public
  int getCharsAllowed()
  {
    return iCharsAllowed;
  }
  
  public 
  int getDecimal() 
  {
    return decimal;
  }
  
  public 
  void setDecimal(int decimal) 
  {
    this.decimal = decimal;
    if(decimal == 0 || decimal == 2) {
      decimalFormat = ResourcesMgr.getDefaultDecimalFormat();
    }
    else 
    if(decimal > 0) {
      StringBuffer sbPattern = new StringBuffer("#,##0.");
      for(int i = 0; i < decimal; i++) sbPattern.append("0");
      decimalFormat = new DecimalFormat(sbPattern.toString());
    }
  }
  
  protected 
  double toDouble(String sText)
  {
    if(sText == null || sText.length() == 0) {
      return 0.0d;
    }
    if(sText.indexOf('.') >= 0 && sText.indexOf(',') >= 0) {
      StringBuffer sb = new StringBuffer(sText.length());
      for(int i = 0; i < sText.length(); i++) {
        char c = sText.charAt(i);
        if(c != '.') sb.append(c);
      }
      sText = sb.toString();
    }
    try { return Double.parseDouble(sText.replace(',', '.')); } catch(Exception ex) {}
    return 0.0d;
  }
  
  protected 
  int toInt(String sText)
  {
    if(sText == null || sText.length() == 0) {
      return 0;
    }
    try { return Integer.parseInt(sText.trim()); } catch(Exception ex) {}
    return 0;
  }
  
  class KLJTextFieldExt extends KeyAdapter
  {
    public void keyTyped(KeyEvent e)
    {
      switch(iCharsAllowed) {
        case iCHARS_ALL:
          return;
        case iCHARS_LETTERS:
          char c = e.getKeyChar();
          if(c >= KeyEvent.VK_A && c <= KeyEvent.VK_Z) {
            return;
          }
          else
          if(c >= KeyEvent.VK_A + 32 && c <= KeyEvent.VK_Z + 32) {
            return;
          }
          else
          if(c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_SPACE ||
            c == '\'' ||
            c == ','  ||
            c == '.'  ||
            c == '#'  ||
            c == '-') {
            return;
          }
          e.consume();
          break;
        case iCHARS_NUMERIC:
          char cn = e.getKeyChar();
          if((cn < KeyEvent.VK_0 || cn > KeyEvent.VK_9) && cn != KeyEvent.VK_BACK_SPACE) {
            e.consume();
          }
          break;
        case iCHARS_DECIMAL:
          char cd = e.getKeyChar();
          if((cd < KeyEvent.VK_0 || cd > KeyEvent.VK_9) && cd != KeyEvent.VK_BACK_SPACE && cd != '.' && cd != ',' && cd != '-') {
            e.consume();
          }
          break;
        case iCHARS_QUANTITY:
          char ca = e.getKeyChar();
          if((ca < KeyEvent.VK_0 || ca > KeyEvent.VK_9) && ca != KeyEvent.VK_BACK_SPACE && ca != '-') {
            e.consume();
          }
          break;
        default:
          return;
      }
    }
  }
  
  class FLJTextFieldExt extends FocusAdapter
  {
    public void focusGained(FocusEvent e) {
      if(iCharsAllowed == iCHARS_DECIMAL) {
        String sText = getText();
        if(sText != null && sText.length() > 0) {
          if(sText.indexOf('.') >= 0 || sText.indexOf(',') >= 0) {
            final double dValue = toDouble(sText);
            String sValue = String.valueOf(dValue).replace('.', ',');
            if(sValue.endsWith(",00")) {
              sValue = sValue.substring(0, sValue.length()-3);
            }
            if(sValue.endsWith(",0")) {
              sValue = sValue.substring(0, sValue.length()-2);
            }
            setText(sValue);
          }
        }
      }
      if(boSelectionOnFocus) {
        selectAll();
      }
    }
    public void focusLost(FocusEvent e) {
      if(iCharsAllowed == iCHARS_DECIMAL) {
        String sText = getText();
        if(sText == null || sText.length() == 0) {
          return;
        }
        if(sText.length() == 1) {
          if(sText.equals(".") || sText.equals(",")) {
            setText("");
            return;
          }
        }
        final double dValue = toDouble(sText);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            setText(decimalFormat.format(dValue));
          }
        });
      }
    }
  }
  
  class DLJTextFieldExt implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) {
      // Controllo della lunghezza massima
      String sText = getText();
      int iLength = sText.length();
      if(iMaxLength > 0 && iLength > iMaxLength) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            int iCaretPosition = getCaretPosition();
            setText(getText().substring(0, iMaxLength));
            try{ setCaretPosition(iCaretPosition); } catch(Throwable th) {};
          }
        });
      }
      
      switch(iCase) {
        case iCASE_UPPER:
          if(!sText.equals(sText.toUpperCase())) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int iCaretPosition = getCaretPosition();
                setText(getText().toUpperCase());
                try{ setCaretPosition(iCaretPosition); } catch(Throwable th) {};
              }
            });
          }
          break;
        case iCASE_LOWER:
          if(!sText.equals(sText.toLowerCase())) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int iCaretPosition = getCaretPosition();
                setText(getText().toLowerCase());
                try{ setCaretPosition(iCaretPosition); } catch(Throwable th) {};
              }
            });
          }
          break;
      }
    }
    
    public void changedUpdate(DocumentEvent e) {
    }
    
    public void removeUpdate(DocumentEvent e) {
    }
  }
}
