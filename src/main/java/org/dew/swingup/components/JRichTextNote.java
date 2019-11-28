package org.dew.swingup.components;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyledEditorKit;

import org.dew.swingup.IConstants;
import org.dew.swingup.util.GUIUtil;

/**
 * Componente utile per l'inserimento di testo con modifica degli stili.
 * Al suo interno utilizza un JEditorPane.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class JRichTextNote extends JPanel
{
  protected JEditorPane jEditorPane;
  protected int iRows;
  
  protected JButton btnBold;
  protected JButton btnItalic;
  protected JButton btnUnderline;
  protected JButton btnCopy;
  protected JButton btnPaste;
  
  protected String sBEG_BODY  = "<html>\n<head>\n</head>\n<body>\n";
  protected String sEND_BODY  = "</body>\n</html>\n";
  
  protected Map mapLinkAttributes;
  
  /**
   * Costruttore. Non si puo' scendere al di sotto di 4 righe per i pulsanti degli stili.
   *
   * @param iRows int
   */
  public
  JRichTextNote(int iRows)
  {
    this(iRows, null, 0);
  }
  
  /**
   * Costruttore. Non si puo' scendere al di sotto di 4 righe per i pulsanti degli stili.
   *
   * @param iRows int
   * @param urlBackground URL dell'immagine di sfondo
   * @param iMarginLeft Margine sinistro (espresso in px)
   */
  public
  JRichTextNote(int iRows, URL urlBackground, int iMarginLeft)
  {
    if(iRows < 4) iRows = 4;
    this.iRows = iRows;
    
    if(urlBackground != null) {
      sBEG_BODY  = "<html>\n<head>\n</head>\n<body background=\"" + urlBackground + "\" style=\"margin-left: " + iMarginLeft + "px;\">\n";
    }
    jEditorPane = new JEditorPane();
    jEditorPane.setContentType("text/html");
    jEditorPane.setText(sBEG_BODY + sEND_BODY);
    
    // Il tab viene intercettato per il passaggio del focus
    jEditorPane.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent ke) {
        if(ke.getKeyChar() == '\t') {
          ke.consume();
        }
      }
      public void keyTyped(KeyEvent ke) {
        if(ke.getKeyChar() == '\t') {
          jEditorPane.transferFocus();
        }
      }
    });
    
    // Impostazione dell'altezza in funzione del numero di righe
    FontMetrics metrics = getFontMetrics(getFont());
    int rowHeight = metrics.getHeight() + 1;
    Insets insets = getInsets();
     int iHeight = (iRows + 1) * rowHeight + insets.top + insets.bottom;
    Dimension dim = new Dimension(0, iHeight);
    jEditorPane.setPreferredSize(dim);
    // C'e' uno strano problema di resizing per testi lunghi...
    this.setPreferredSize(dim);
    
    JPanel jpButtons = buildButtons();
    JPanel jpEast = new JPanel(new BorderLayout());
    jpEast.add(jpButtons, BorderLayout.NORTH);
    
    setLayout(new BorderLayout());
    add(new JScrollPane(jEditorPane), BorderLayout.CENTER);
    add(jpEast, BorderLayout.EAST);
    setFocusable(false);
  }
  
  public
  JEditorPane getJEditorPane()
  {
    return jEditorPane;
  }
  
  public
  void addFocusListener(FocusListener fl)
  {
    super.addFocusListener(fl);
    
    if(jEditorPane != null) {
      jEditorPane.addFocusListener(fl);
    }
  }
  
  public
  void removeFocusListener(FocusListener fl)
  {
    super.removeFocusListener(fl);
    
    if(jEditorPane != null) {
      jEditorPane.removeFocusListener(fl);
    }
  }
  
  public
  void requestFocus()
  {
    if(jEditorPane != null) {
      jEditorPane.requestFocus();
      jEditorPane.setSelectionStart(0);
      jEditorPane.setSelectionEnd(jEditorPane.getDocument().getLength());
    }
    else {
      super.requestFocus();
    }
  }
  
  public
  void transferFocus()
  {
    if(jEditorPane != null) {
      jEditorPane.transferFocus();
    }
    else {
      super.transferFocus();
    }
  }
  
  public
  void addKeyListener(KeyListener kl)
  {
    super.addKeyListener(kl);
    
    if(jEditorPane != null) {
      jEditorPane.addKeyListener(kl);
    }
  }
  
  public
  void removeKeyListener(KeyListener kl)
  {
    super.removeKeyListener(kl);
    
    if(jEditorPane != null) {
      jEditorPane.removeKeyListener(kl);
    }
  }
  
  public
  void setBackground(Color color)
  {
    if(jEditorPane != null) {
      super.setBackground(color);
      jEditorPane.setBackground(color);
    }
    else {
      super.setBackground(color);
    }
  }
  
  public
  Color getBackground()
  {
    if(jEditorPane != null) {
      return jEditorPane.getBackground();
    }
    else {
      return super.getBackground();
    }
  }
  
  public
  void setEnabled(boolean boEnabled)
  {
    // jEditorPane.setEnabled(boEnabled);
    jEditorPane.setEditable(boEnabled);
    jEditorPane.setFocusable(boEnabled);
    
    btnBold.setEnabled(boEnabled);
    btnItalic.setEnabled(boEnabled);
    btnUnderline.setEnabled(boEnabled);
    if(btnCopy  != null) btnCopy.setEnabled(boEnabled);
    if(btnPaste != null) btnPaste.setEnabled(boEnabled);
  }
  
  public
  Font getFont()
  {
    if(jEditorPane != null) return jEditorPane.getFont();
    return super.getFont();
  }
  
  public
  void setFont(Font font)
  {
    if(jEditorPane != null) {
      jEditorPane.setFont(font);
      return;
    }
    super.setFont(font);
  }
  
  public
  void setName(String sName)
  {
    super.setName(sName);
    jEditorPane.setName(sName);
  }
  
  public
  void setEditable(boolean boEditable)
  {
    jEditorPane.setEditable(boEditable);
  }
  
  public
  void setText(String sText)
  {
    jEditorPane.setText(toHTML(sText));
    
    jEditorPane.setCaretPosition(0);
  }
  
  public
  String getText()
  {
    Document doc = jEditorPane.getDocument();
    
    return getHTMLSource(doc, false);
  }
  
  public
  String getHTMLText()
  {
    Document doc = jEditorPane.getDocument();
    
    return getHTMLSource(doc, true);
  }
  
  public
  void setMapLinkAttributes(Map mapLinkAttributes)
  {
    this.mapLinkAttributes = mapLinkAttributes;
  }
  
  public
  void doCopy()
  {
    String sText = jEditorPane.getSelectedText();
    if(sText == null || sText.length() == 0) {
      sText = getHTMLSource(jEditorPane.getDocument(), false);
    }
    if(sText != null) {
      sText = replaceString(sText, "<br>",   "\n");
      sText = replaceString(sText, "&nbsp;", " ");
      StringSelection oStringSelection = new StringSelection(sText);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(oStringSelection, null);
    }
  }
  
  public
  void doPaste()
  {
    try {
      String sText = null;
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable transferable = clipboard.getContents(null);
      if(transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        sText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
      }
      if(sText != null && sText.length() > 0) {
        Document document   = jEditorPane.getDocument();
        Caret caret         = jEditorPane.getCaret();
        EditorKit editorKit = jEditorPane.getEditorKit();
        document.insertString(caret.getDot(), sText,((StyledEditorKit) editorKit).getInputAttributes());
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  protected
  String getHTMLSource(Document doc, boolean boBody)
  {
    // Apertura della pagina html
    String sResult = "";
    
    if(boBody) sResult += "<html>\n<head>\n</head>\n<body>\n";
    
    // Recupero del body
    Element[] rootElements = doc.getRootElements();
    Element html = rootElements[0];
    Element body = html.getElement(1);
    if(body == null) {
      body = html;
    }
    int iParCount = body.getElementCount();
    
    // Ciclo dei paragrafi
    for(int p = 0; p < iParCount; p++) {
      Element paragraph = body.getElement(p);
      int iCount = paragraph.getElementCount();
      // Recupero allineamento del paragrafo
      String sTagParagraph = null;
      AttributeSet as_p = paragraph.getAttributes().copyAttributes();
      Enumeration enumAs_p = as_p.getAttributeNames();
      while(enumAs_p.hasMoreElements()) {
        Object oName = enumAs_p.nextElement();
        Object oAttr = as_p.getAttribute(oName);
        String sPair = oName + "=" + oAttr;
        if(sPair.equals("align=left")) {
          sTagParagraph = "<p align=\"left\">";
        }
        else
        if(sPair.equals("align=center")) {
          sTagParagraph = "<p align=\"center\">";
        }
        else
        if(sPair.equals("align=right")) {
          sTagParagraph = "<p align=\"right\">";
        }
      }
      if(sTagParagraph != null) {
        sResult += sTagParagraph;
      }
      // Ciclo degli elementi del paragrafo
      for(int i = 0; i < iCount; i++) {
        Element element = paragraph.getElement(i);
        String sElementName = element.getName();
        if(sElementName.equals("br")) {
          sResult += "<br>";
          continue;
        }
        else
        if(sElementName.equals("hr")) {
          sResult += "<hr>";
          continue;
        }
        // Recupero del testo
        int iStart = element.getStartOffset();
        int iEnd   = element.getEndOffset();
        int iLength  = iEnd - iStart;
        String sText = "";
        try { sText = doc.getText(iStart, iLength); } catch(Exception ex) {}
        // L'ultimo paragrafo tende a presentare un break in piu'...
        if(p == iParCount - 1 && i == iCount - 1 && sText.trim().length() == 0) continue;
        // Gli elenchi si presentano in element.getName()
        HashSet hsTags = new HashSet();
        if(sElementName.equals("li")) {
          hsTags.add("li");
          // In presenza di una voce di elenco non si riporta il break
          sText = normalize(sText, false);
        }
        else {
          sText = normalize(sText, true);
        }
        // Recupero tag di stile dagli attributi
        AttributeSet as = element.getAttributes().copyAttributes();
        Enumeration enumAs = as.getAttributeNames();
        while(enumAs.hasMoreElements()) {
          Object oName = enumAs.nextElement();
          Object oAttr = as.getAttribute(oName);
          String sPair = oName + "=" + oAttr;
          if(sPair.equals("font-weight=bold")) {
            hsTags.add("b");
          }
          else
          if(sPair.equals("font-style=italic")) {
            hsTags.add("i");
          }
          else
          if(sPair.equals("text-decoration=underline")) {
            hsTags.add("u");
          }
          else
          if(sPair.startsWith("a=")) {
            String sTag = "a";
            int iHRef = sPair.indexOf("href=");
            if(iHRef > 0) {
              int iEndValue = sPair.indexOf(' ', iHRef);
              if(iEndValue < 0) iEndValue = sPair.length();
              if(iEndValue > iHRef + 5) {
                sTag += " href=\"" + sPair.substring(iHRef + 5, iEndValue) + "\"";
              }
            }
            int iOnClick = sPair.indexOf("onclick=");
            if(iOnClick > 0) {
              int iEndValue = sPair.indexOf(' ', iOnClick);
              if(iEndValue < 0) iEndValue = sPair.length();
              if(iEndValue > iOnClick + 8) {
                sTag += " onclick=\"" + sPair.substring(iOnClick + 8, iEndValue) + "\"";
              }
            }
            int iTarget = sPair.indexOf("target=");
            if(iTarget > 0) {
              int iEndValue = sPair.indexOf(' ', iTarget);
              if(iEndValue < 0) iEndValue = sPair.length();
              if(iEndValue > iTarget + 7) {
                sTag += " target=\"" + sPair.substring(iOnClick + 7, iEndValue) + "\"";
              }
            }
            int iRel = sPair.indexOf("rel=");
            if(iRel > 0) {
              int iEndValue = sPair.indexOf(' ', iRel);
              if(iEndValue < 0) iEndValue = sPair.length();
              if(iEndValue > iRel + 4) {
                sTag += " rel=\"" + sPair.substring(iRel + 4, iEndValue) + "\"";
              }
            }
            hsTags.add(sTag);
          }
          else
          if(sPair.startsWith("color=") && mapLinkAttributes != null) {
            String sBlue = sPair.substring(sPair.length()-2);
            Integer oKey = new Integer(255-Integer.parseInt(sBlue, 16));
            String sLinkAttributes = (String) mapLinkAttributes.get(oKey);
            if(sLinkAttributes != null && sLinkAttributes.length() > 0) {
              hsTags.add("a " + sLinkAttributes);
            }
          }
          String sName = oName.toString();
          if(sName.length() <= 3 && !sName.equalsIgnoreCase("CR") && !sName.equalsIgnoreCase("a")) {
            hsTags.add(sName);
          }
        }
        Object[] tags = hsTags.toArray();
        // Apertura tag di stile
        for(int j = 0; j < tags.length; j++) {
          sResult += "<" + tags[j] + ">";
        }
        // Testo normalizzato
        sResult += sText;
        // Chiusura tag di stile
        for(int j = tags.length - 1; j >= 0; j--) {
          String sTag = (String) tags[j];
          int iSpace = sTag.indexOf(' ');
          if(iSpace > 0) {
            sResult += "</" + sTag.substring(0, iSpace) + ">";
          }
          else {
            sResult += "</" + sTag + ">";
          }
        }
      }
      // Chiusura dell'eventuale paragrafo
      if(sTagParagraph != null) {
        sResult += "</p>";
      }
    }
    // Chiusura della pagina html
    if(boBody) sResult += "\n</body>\n</html>\n";
    return sResult;
  }
  
  protected
  String normalize(String sText, boolean boBR)
  {
    StringBuffer sb = new StringBuffer();
    int iTextLenght = sText.length();
    char cLast = '\0';
    for(int i = 0; i < iTextLenght; i++) {
      char c = sText.charAt(i);
      if(c == '\n') {
        if(boBR) {
          sb.append("<br>");
        }
      }
      else
      if(c == ' ') {
        if(Character.isLetterOrDigit(cLast)) {
          sb.append(c);
        }
        else {
          sb.append("&nbsp;");
        }
      }
      else
      if(c == '<') {
        sb.append("&lt;");
      }
      else
      if(c == '>') {
        sb.append("&gt;");
      }
      else
      if(c == 8216 || c == 8217) {
        sb.append('\'');
      }
      else
      if(c == 8220 || c == 8221) {
        sb.append('"');
      }
      else
      if(c == 8211) {
        sb.append('-');
      }
      else
      if(c == 8226) {
        sb.append('*');
      }
      else {
        sb.append(c);
      }
      cLast = c;
    }
    return sb.toString();
  }
  
  protected
  String toHTML(String sText)
  {
    if(sText == null || sText.trim().length() == 0) {
      return sBEG_BODY + sEND_BODY;
    }
    
    boolean boHasBody = sText.indexOf("<body") > 0 || sText.indexOf("<BODY") > 0;
    if(boHasBody) {
      return sText;
    }
    
    boolean boBR = sText.indexOf("<br>") < 0 && sText.indexOf('\n') >= 0;
    StringBuffer sb = new StringBuffer();
    sb.append(sBEG_BODY);
    int iTextLenght = sText.length();
    boolean boTagOpened = false;
    char cLast = '\0';
    for(int i = 0; i < iTextLenght; i++) {
      char c = sText.charAt(i);
      if(c == '<') {
        if(i < iTextLenght - 1) {
          char c1 = sText.charAt(i + 1);
          if(Character.isLetter(c1) || c1 == '/') {
            sb.append("<");
            boTagOpened = true;
          }
          else {
            sb.append("&lt;");
          }
        }
        else {
          sb.append("&lt;");
        }
      }
      else
      if(c == '>') {
        if(boTagOpened) {
          sb.append(">");
          boTagOpened = false;
        }
        else {
          sb.append("&gt;");
        }
      }
      else
      if(c == '\n') {
        if(boBR) {
          sb.append("<br>");
        }
      }
      else
      if(c == ' ') {
        if(Character.isLetterOrDigit(cLast)) {
          sb.append(c);
        }
        else {
          sb.append("&nbsp;");
        }
      }
      else
      if(c == 8216 || c == 8217) {
        sb.append('\'');
      }
      else
      if(c == 8220 || c == 8221) {
        sb.append('"');
      }
      else
      if(c == 8211) {
        sb.append('-');
      }
      else
      if(c == 8226) {
        sb.append('*');
      }
      else
      if(c > 255) {
        sb.append(' ');
      }
      else {
        sb.append(c);
      }
      cLast = c;
    }
    sb.append(sEND_BODY);
    
    return sb.toString();
  }
  
  protected
  JPanel buildButtons()
  {
    // Costruzione dei pulsanti
    btnBold      = GUIUtil.buildActionButton(IConstants.sGUIDATA_BOLD,      "font-bold");
    btnItalic    = GUIUtil.buildActionButton(IConstants.sGUIDATA_ITALIC,    "font-italic");
    btnUnderline = GUIUtil.buildActionButton(IConstants.sGUIDATA_UNDERLINE, "font-underline");
    btnCopy      = GUIUtil.buildActionButton("|Copia|" + IConstants.sICON_COPY,    "copy");
    btnPaste     = GUIUtil.buildActionButton("|Incolla|" + IConstants.sICON_PASTE, "paste");
    
    // I pulsanti non defono ottenere il focus altrimenti quando viene
    // eseguita l'azione il componente JEditorPane perde il focus e la
    // selezione del testo.
    btnBold.setFocusable(false);
    btnItalic.setFocusable(false);
    btnUnderline.setFocusable(false);
    btnCopy.setFocusable(false);
    btnPaste.setFocusable(false);
    
    // Vengono recuperate le azioni standard dell'editor kit
    Action[] actions = jEditorPane.getEditorKit().getActions();
    for(int i = 0; i < actions.length; i++) {
      Action action = actions[i];
      String sActionName = (String) action.getValue(Action.NAME);
      if(sActionName.equals("font-bold")) {
        btnBold.addActionListener(action);
      }
      else
      if(sActionName.equals("font-italic")) {
        btnItalic.addActionListener(action);
      }
      else
      if(sActionName.equals("font-underline")) {
        btnUnderline.addActionListener(action);
      }
    }
    
    btnCopy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doCopy();
      }
    });
    btnPaste.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doPaste();
      }
    });
    
    JPanel jpResult = null;
    if(iRows > 5) {
      jpResult = new JPanel(new GridLayout(5, 1));
    }
    else {
      jpResult = new JPanel(new GridLayout(3, 1));
    }
    jpResult.add(btnBold);
    jpResult.add(btnItalic);
    jpResult.add(btnUnderline);
    if(iRows > 5) {
      jpResult.add(btnCopy);
      jpResult.add(btnPaste);
    }
    
    return jpResult;
  }
  
  protected static
  String replaceString(String sText, String sPar, String sValue)
  {
    if(sValue == null) sValue = "";
    int iParLen  = sPar.length();
    int iTextLen = sText.length();
    int iIndexOf = sText.indexOf(sPar);
    while(iIndexOf >= 0) {
      String sLeft = sText.substring(0, iIndexOf);
      String sParValue = sValue;
      String sRight = null;
      if(iIndexOf + iParLen >= iTextLen) {
        sRight = "";
      }
      else {
        sRight = sText.substring(iIndexOf + iParLen);
      }
      sText = sLeft + sParValue + sRight;
      iIndexOf = sText.indexOf(sPar);
    }
    return sText;
  }
}
