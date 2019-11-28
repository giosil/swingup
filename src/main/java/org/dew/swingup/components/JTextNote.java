package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Componente utile per l'inserimento di testo con molti caratteri.
 * Al suo interno utilizza un JTextArea.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class JTextNote extends JScrollPane
{
  public final static int iCASE_NOTMODIFIED = 0;
  public final static int iCASE_UPPER       = 1;
  public final static int iCASE_LOWER       = 2;
  
  protected JTextArea txtNote;
  protected int iMaxLength = 0;
  protected int iCase = 0;
  protected boolean boDocumentListenerAdded = false;
  
  public
  JTextNote(int iRows)
  {
    this(iRows, 0, iCASE_NOTMODIFIED);
  }
  
  public
  JTextNote(int iRows, int iTheMaxLength)
  {
    this(iRows, iTheMaxLength, iCASE_NOTMODIFIED);
  }
  
  public
  JTextNote(int iRows, int iTheMaxLength, int iTheCase)
  {
    super();
    this.iMaxLength = iTheMaxLength;
    this.iCase      = iTheCase;
    txtNote = new JTextArea();
    Font oFontMonospaced = new Font("Monospaced",
      txtNote.getFont().getStyle(),
      txtNote.getFont().getSize());
    txtNote.setLineWrap(true);
    txtNote.setFont(oFontMonospaced);
    txtNote.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent ke) {
        if(ke.getKeyChar() == '\t') {
          ke.consume();
        }
      }
      public void keyTyped(KeyEvent ke) {
        if(ke.getKeyChar() == '\t') {
          txtNote.transferFocus();
        }
      }
    });
    
    if(iCase != iCASE_NOTMODIFIED) {
      txtNote.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          
          // Controllo della lunghezza massima
          String sText = txtNote.getText();
          int iLength = sText.length();
          if(iMaxLength > 0 && iLength > iMaxLength) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int iCaretPosition = txtNote.getCaretPosition();
                txtNote.setText(txtNote.getText().substring(0, iMaxLength));
                try{ txtNote.setCaretPosition(iCaretPosition); } catch(Throwable th) {};
              }
            });
          }
          
          switch(iCase) {
            case iCASE_UPPER:
            if(!sText.equals(sText.toUpperCase())) {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  int iCaretPosition = txtNote.getCaretPosition();
                  txtNote.setText(txtNote.getText().toUpperCase());
                  try{ txtNote.setCaretPosition(iCaretPosition); } catch(Throwable th) {};
                }
              });
            }
            break;
            case iCASE_LOWER:
            if(!sText.equals(sText.toLowerCase())) {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  int iCaretPosition = txtNote.getCaretPosition();
                  txtNote.setText(txtNote.getText().toLowerCase());
                  try{ txtNote.setCaretPosition(iCaretPosition); } catch(Throwable th) {};
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
      });
      boDocumentListenerAdded = false;
    }
    
    txtNote.setRows(iRows);
    txtNote.setLineWrap(true);
    txtNote.setWrapStyleWord(true);
    
    getViewport().add(txtNote);
  }
  
  /**
   * Imposta il case del testo.
   * 0 = nessuna modifica
   * 1 = upper
   * 2 = lower
   *
   * @param iTheCase int
   */
  public
  void setCase(int iTheCase)
  {
    this.iCase = iTheCase;
    if(iCase != iCASE_NOTMODIFIED && !boDocumentListenerAdded) {
      txtNote.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
          // Controllo della lunghezza massima
          String sText = txtNote.getText();
          int iLength = sText.length();
          if(iMaxLength > 0 && iLength > iMaxLength) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int iCaretPosition = txtNote.getCaretPosition();
                txtNote.setText(txtNote.getText().substring(0, iMaxLength));
                try{ txtNote.setCaretPosition(iCaretPosition); } catch(Throwable th) {};
              }
            });
          }
          switch(iCase) {
            case iCASE_UPPER:
            if(!sText.equals(sText.toUpperCase())) {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  int iCaretPosition = txtNote.getCaretPosition();
                  txtNote.setText(txtNote.getText().toUpperCase());
                  try{ txtNote.setCaretPosition(iCaretPosition); } catch(Throwable th) {};
                }
              });
            }
            break;
            case iCASE_LOWER:
            if(!sText.equals(sText.toLowerCase())) {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  int iCaretPosition = txtNote.getCaretPosition();
                  txtNote.setText(txtNote.getText().toLowerCase());
                  try{ txtNote.setCaretPosition(iCaretPosition); } catch(Throwable th) {};
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
      });
      boDocumentListenerAdded = false;
    }
  }
  
  /**
   * Ottiene il case del testo.
   *
   * @return int
   */
  public
  int getCase()
  {
    return iCase;
  }
  
  public
  JTextArea getJTextArea()
  {
    return txtNote;
  }
  
  public
  void setMaxLength(int iMaxLength)
  {
    this.iMaxLength = iMaxLength;
  }
  
  public
  int getMaxLength()
  {
    return iMaxLength;
  }
  
  public
  void addFocusListener(FocusListener fl)
  {
    super.addFocusListener(fl);
    
    if(txtNote != null) {
      txtNote.addFocusListener(fl);
    }
  }
  
  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    
    if(txtNote != null) {
      txtNote.removeFocusListener(fl);
    }
  }
  
  public
  void requestFocus()
  {
    if(txtNote != null) {
      txtNote.requestFocus();
    }
    else {
      super.requestFocus();
    }
  }
  
  public
  void transferFocus()
  {
    if(txtNote != null) {
      txtNote.transferFocus();
    }
    else {
      super.transferFocus();
    }
  }
  
  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    
    if(txtNote != null) {
      txtNote.addKeyListener(kl);
    }
  }
  
  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    
    if(txtNote != null) {
      txtNote.removeKeyListener(kl);
    }
  }
  
  public
  void setBackground(Color color)
  {
    if(txtNote != null) {
      super.setBackground(color);
      txtNote.setBackground(color);
    }
    else {
      super.setBackground(color);
    }
  }
  
  public
  Color getBackground()
  {
    if(txtNote != null) {
      return txtNote.getBackground();
    }
    else {
      return super.getBackground();
    }
  }
  
  public
  void setEnabled(boolean boEnabled)
  {
    txtNote.setEnabled(boEnabled);
  }
  
  public
  void setName(String sName)
  {
    super.setName(sName);
    txtNote.setName(sName);
  }
  
  public
  void setEditable(boolean boEditable)
  {
    txtNote.setEditable(boEditable);
  }
  
  public
  void setText(String sText)
  {
    txtNote.setText(normalize(sText));
    txtNote.setCaretPosition(0);
  }
  
  public
  String getText()
  {
    return normalize(txtNote.getText());
  }
  
  protected
  String normalize(String sText)
  {
    if(sText == null) return null;
    
    StringBuffer sb = new StringBuffer(sText.length());
    for(int i = 0; i < sText.length(); i++) {
      int iChar = sText.charAt(i);
      if(iChar == 8216 || iChar == 8217) {
        sb.append('\'');
      }
      else
      if(iChar == 8220 || iChar == 8221) {
        sb.append('"');
      }
      else
      if(iChar == 8211) {
        sb.append('-');
      }
      else
      if(iChar == 8226) {
        sb.append('*');
      }
      else
      if(iChar > 255) {
        sb.append(' ');
      }
      else
      if(iChar == '\t') {
        sb.append("    ");
      }
      else {
        sb.append((char) iChar);
      }
    }
    
    return sb.toString();
  }
}
