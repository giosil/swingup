package org.dew.swingup.fm;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.dew.swingup.*;
import org.dew.swingup.util.GUIUtil;

@SuppressWarnings({"rawtypes","unchecked"})
public
class GUITextViewer extends JPanel
{
  private static final long serialVersionUID = 119229557260784132L;
  
  protected JLabel jlTitle;
  protected JLabel jlFilePath;
  protected JLabel jlLastModified;
  protected JLabel jlLength;
  protected JTextArea jTextArea;
  
  private
  GUITextViewer(boolean boFMInfo)
  {
    super(new BorderLayout(8, 8));
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    
    JPanel jpNorth = null;
    if(boFMInfo) {
      jpNorth = new JPanel(new GridLayout(3, 1, 4, 4));
      
      JPanel jp0 = new JPanel(new BorderLayout());
      JLabel jl0 = new JLabel("File Path:");
      jl0.setFont(GUIUtil.modifyFont(jl0.getFont(), 3));
      jl0.setPreferredSize(new Dimension(130, 0));
      jlFilePath = new JLabel();
      jlFilePath.setForeground(new Color(0, 0, 128));
      jlFilePath.setFont(GUIUtil.modifyFont(jlFilePath.getFont(), 3));
      jp0.add(jl0, BorderLayout.WEST);
      jp0.add(jlFilePath, BorderLayout.CENTER);
      jpNorth.add(jp0);
      
      JPanel jp1 = new JPanel(new BorderLayout());
      JLabel jl1 = new JLabel("Last Modified:");
      jl1.setFont(GUIUtil.modifyFont(jl1.getFont(), 3));
      jl1.setPreferredSize(new Dimension(130, 0));
      jlLastModified = new JLabel();
      jlLastModified.setForeground(new Color(0, 0, 128));
      jlLastModified.setFont(GUIUtil.modifyFont(jlLastModified.getFont(), 3));
      jp1.add(jl1, BorderLayout.WEST);
      jp1.add(jlLastModified, BorderLayout.CENTER);
      jpNorth.add(jp1);
      
      JPanel jp2 = new JPanel(new BorderLayout());
      JLabel jl2 = new JLabel("Length:");
      jl2.setFont(GUIUtil.modifyFont(jl2.getFont(), 3));
      jl2.setPreferredSize(new Dimension(130, 0));
      jlLength = new JLabel();
      jlLength.setForeground(new Color(0, 0, 128));
      jlLength.setFont(GUIUtil.modifyFont(jlLength.getFont(), 3));
      jp2.add(jl2, BorderLayout.WEST);
      jp2.add(jlLength, BorderLayout.CENTER);
      jpNorth.add(jp2);
    }
    else {
      jpNorth = new JPanel(new BorderLayout());
      jlTitle = new JLabel();
      jlTitle.setForeground(new Color(0, 0, 128));
      jlTitle.setFont(GUIUtil.modifyFont(jlTitle.getFont(), 3));
      jpNorth.add(jlTitle, BorderLayout.CENTER);
    }
    
    jTextArea = new JTextArea();
    jTextArea.setFont(new Font("Monospaced", Font.BOLD, 16));
    JScrollPane jsCenter = new JScrollPane(jTextArea);
    
    add(jpNorth,  BorderLayout.NORTH);
    add(jsCenter, BorderLayout.CENTER);
  }
  
  public
  GUITextViewer(FMEntry fmEntry, String sText)
  {
    this(true);
    if(fmEntry != null) {
      jlFilePath.setText(fmEntry.getPath());
      jlLastModified.setText(fmEntry.getStringLastModified());
      jlLength.setText(fmEntry.getStringLength());
    }
    jTextArea.setText(sText);
    jTextArea.setCaretPosition(0);
  }
  
  public
  GUITextViewer(String sTitle, String sText)
  {
    this(false);
    jlTitle.setText(sTitle);
    jTextArea.setText(sText);
    jTextArea.setCaretPosition(0);
  }
  
  public
  GUITextViewer(String sTitle, Map map)
  {
    this(false);
    jlTitle.setText(sTitle);
    String sText = "";
    if(map != null && !map.isEmpty()) {
      List listOfKey = new ArrayList();
      Iterator iterator = map.keySet().iterator();
      while(iterator.hasNext()) {
        listOfKey.add(iterator.next());
      }
      Collections.sort(listOfKey);
      for(int i = 0; i < listOfKey.size(); i++) {
        Object oKey = listOfKey.get(i);
        sText += oKey + " = " + map.get(oKey) + "\n";
      }
    }
    jTextArea.setText(sText);
    jTextArea.setCaretPosition(0);
  }
  
  public
  GUITextViewer(FMEntry fmEntry, List listRows)
  {
    this(true);
    if(fmEntry != null) {
      jlFilePath.setText(fmEntry.getPath());
      jlLastModified.setText(fmEntry.getStringLastModified());
      jlLength.setText(fmEntry.getStringLength());
    }
    String sText = "";
    if(listRows != null && listRows.size() > 0) {
      int iLastRowNumber = 0;
      for(int i = 0; i < listRows.size(); i++) {
        Object oRow = listRows.get(i);
        if(oRow instanceof Map) {
          Map mapRow = (Map) oRow;
          Number oRowNumber = (Number) mapRow.get("r");
          int iRowNumber = oRowNumber != null ? oRowNumber.intValue() : 0;
          Object oRowText   = mapRow.get("t");
          Object oPrev = mapRow.get("p");
          Object oNext = mapRow.get("n");
          if(oPrev != null || oNext != null) {
            if(oPrev != null) {
              sText += lpad("", ' ', 6) + "  " + oPrev + "\n";
            }
            sText += lpad(oRowNumber, ' ', 6) + ": " + oRowText + "\n";
            if(oNext != null) {
              sText += lpad("", ' ', 6) + "  " + oNext + "\n";
              sText += lpad("", ' ', 6) + "  ...\n";
            }
          }
          else {
            if(iRowNumber - iLastRowNumber > 1) {
              sText += lpad("", ' ', 6) + "  ...\n";
            }
            sText += lpad(oRowNumber, ' ', 6) + ": " + oRowText + "\n";
          }
          iLastRowNumber = iRowNumber;
        }
      }
    }
    jTextArea.setText(sText);
    jTextArea.setCaretPosition(0);
  }
  
  public
  void setText(String sText)
  {
    jTextArea.setText(sText);
    jTextArea.setCaretPosition(0);
  }
  
  public
  String getText()
  {
    return jTextArea.getText();
  }
  
  public static void showMe(FMEntry fmEntry, String sText) {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    workPanel.show(new GUITextViewer(fmEntry, sText), fmEntry.getName(), "DocumentLarge.gif");
  }
  
  public static void showMe(FMEntry fmEntry, String sTitle, String sText) {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    workPanel.show(new GUITextViewer(fmEntry, sText), sTitle, "DocumentLarge.gif");
  }
  
  public static void showMe(String sTitle, String sText) {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    workPanel.show(new GUITextViewer(sTitle, sText), sTitle, "DocumentLarge.gif");
  }
  
  public static void showMe(String sTitle, Map map) {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    workPanel.show(new GUITextViewer(sTitle, map), sTitle, "DocumentLarge.gif");
  }
  
  public static void showMe(FMEntry fmEntry, List listRows) {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    workPanel.show(new GUITextViewer(fmEntry, listRows), fmEntry.getName(), "DocumentLarge.gif");
  }
  
  public static
  String lpad(Object oText, char c, int length)
  {
    if(oText == null) oText = new String();
    String text = oText.toString();
    int iTextLength = text.length();
    if(iTextLength >= length) return text;
    int diff = length - iTextLength;
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < diff; i++) sb.append(c);
    sb.append(text);
    return sb.toString();
  }
}
