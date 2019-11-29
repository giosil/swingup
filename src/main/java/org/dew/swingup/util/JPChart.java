package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.dew.swingup.impl.DefaultGUIManager;

@SuppressWarnings("rawtypes")
public 
class JPChart extends JPanel implements MouseListener, MouseMotionListener
{
  private static final long serialVersionUID = -3772075829446398380L;
  
  protected String[] asSeries;
  protected String[] asCategories;
  protected int[]    aiSeries;
  protected int[]    aiCategories;
  protected double[][] adData;
  protected ChartRectangle[] arRectangles;
  protected String sNote;
  
  protected int iMarginTop      = DefaultGUIManager.resizeForHRScreen(5);
  protected int iMarginBottom   = DefaultGUIManager.resizeForHRScreen(10);
  protected int iMarginSx       = DefaultGUIManager.resizeForHRScreen(5);
  protected int iRectHeight     = DefaultGUIManager.resizeForHRScreen(16);
  protected int iVerticalGap    = DefaultGUIManager.resizeForHRScreen(4);
  protected int iHorizontalGap  = DefaultGUIManager.resizeForHRScreen(4);
  protected int iMaxBarWith     = DefaultGUIManager.resizeForHRScreen(450);
  protected int iMinCountBar    = DefaultGUIManager.resizeForHRScreen(4);
  protected int iMaxLabCatWidth = DefaultGUIManager.resizeForHRScreen(75);
  protected int iMaxValue       = DefaultGUIManager.resizeForHRScreen(5);
  protected int iCountBar       = DefaultGUIManager.resizeForHRScreen(0);
  protected int iStepValue      = DefaultGUIManager.resizeForHRScreen(45);
  protected int iY_Of_XAxis     = DefaultGUIManager.resizeForHRScreen(0);
  
  protected Color color09 = new Color(  0,   0, 200);
  protected Color color10 = new Color(200,   0,   0);
  protected Color color11 = new Color(  0, 200,   0);
  protected Color color12 = new Color(200, 200,   0);
  protected Color color13 = new Color(  0, 200, 200);
  protected Color color14 = new Color(200,   0, 200);
  
  protected List<ActionListener> listActionListener = null;
  
  public JPChart()
  {
    super();
    setOpaque(true);
    setBackground(Color.white);
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  public JPChart(Color colBackground)
  {
    super();
    setOpaque(true);
    if(colBackground != null) {
      setBackground(colBackground);
    }
    else {
      setBackground(Color.white);
    }
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  public 
  void addActionListener(ActionListener actionListener) 
  {
    if(actionListener == null) return;
    if(listActionListener == null) listActionListener = new ArrayList<ActionListener>();
    listActionListener.add(actionListener);
  }
  
  public 
  void removeActionListener(ActionListener actionListener) 
  {
    if(actionListener == null) return;
    if(listActionListener == null) return;
    listActionListener.remove(actionListener);
  }
  
  public 
  void removeAllActionListener() 
  {
    if(listActionListener == null) return;
    listActionListener.clear();
  }
  
  public
  void setNote(String sNote)
  {
    this.sNote = sNote;
  }
  
  public
  void setDataSet(List listSeries, List listCategories, Map mapValues)
  {
    this.asSeries     = null;
    this.aiSeries     = null;
    this.asCategories = null;
    this.aiCategories = null;
    this.adData       = null;
    
    if(listSeries     == null || listSeries.size()     == 0) return;
    if(listCategories == null || listCategories.size() == 0) return;
    if(mapValues      == null || mapValues.isEmpty()) return;
    
    int iSeries = listSeries.size();
    asSeries = new String[iSeries];
    aiSeries = new int[iSeries];
    for(int i = 0; i < iSeries; i++) {
      String sValue = (String) listSeries.get(i);
      if(sValue == null) sValue = "";
      int iSep = sValue.indexOf('|');
      if(iSep > 0) {
        try{ aiSeries[i] = Integer.parseInt(sValue.substring(0, iSep)); } catch(Exception ex) {}
        asSeries[i] = sValue.substring(iSep + 1);
      }
      else {
        aiSeries[i] = i;
        asSeries[i] = sValue;
      }
    }
    int iCategories = listCategories.size();
    asCategories = new String[iCategories];
    aiCategories = new int[iCategories];
    for(int i = 0; i < iCategories; i++) {
      String sValue = (String) listCategories.get(i);
      if(sValue == null) sValue = "";
      int iSep = sValue.indexOf('|');
      if(iSep > 0) {
        try{ aiCategories[i] = Integer.parseInt(sValue.substring(0, iSep)); } catch(Exception ex) {}
        asCategories[i] = sValue.substring(iSep + 1);
      }
      else {
        aiCategories[i] = i;
        asCategories[i] = sValue;
      }
    }
    
    iCountBar = 0;
    double dMaxValue  = 0.0;
    int i = 0;
    adData = new double[iSeries][iCategories];
    for(int x = 0; x < iSeries; x++) {
      for(int y = 0; y < iCategories; y++) {
        Number nValue = (Number) mapValues.get(x + "," + y);
        double dValue = nValue != null ? nValue.doubleValue() : 0.0d;
        adData[x][y] = dValue;
        if(dValue < 0.01d) continue;
        i++;
        if(dValue > dMaxValue) dMaxValue = dValue;
      }
    }
    iCountBar = i;
    arRectangles = new ChartRectangle[iCountBar];
    if(iCountBar < iMinCountBar) i = iMinCountBar;
    iY_Of_XAxis = iMarginTop + i * (iRectHeight + iVerticalGap);
    iStepValue = 50;
    iMaxValue = (int) dMaxValue;
    if(iMaxValue > 5) iStepValue = iMaxBarWith / iMaxValue;
    if(iMaxValue < 5) iMaxValue  = 5;
    
    int iHeight = iMarginTop + (i + 2) * (iRectHeight + iVerticalGap) + iMarginBottom;
    int iWidth  = iMarginSx + iMaxLabCatWidth + iHorizontalGap + iMaxBarWith + 100;
    this.setPreferredSize(new Dimension(iWidth, iHeight));
    
    repaint();
  }
  
  protected 
  void paintComponent(Graphics g) 
  {
    super.paintComponent(g);
    
    Graphics2D g2d = (Graphics2D)g;
    
    int w = getWidth();
    int h = getHeight();
    GradientPaint gp = new GradientPaint(0, 0, Color.white, w, 0, getBackground());
    g2d.setPaint(gp);
    g2d.fillRect(0,0,w,h);
    
    if(asSeries     == null || asSeries.length     == 0) return;
    if(asCategories == null || asCategories.length == 0) return;
    if(adData       == null || adData.length       == 0) return;
    
    int x = iMarginSx + iMaxLabCatWidth;
    int y = 0;
    int iMaxRectWidth = iMaxValue * iStepValue;
    // Paint axis
    Color colorGrid = new Color(230, 230, 230);
    g.setColor(Color.black);
    g.drawLine(iMarginSx, iY_Of_XAxis, x + iMaxRectWidth, iY_Of_XAxis);
    int iMinX = 0;
    if(iMaxValue >= 90) {
      iMinX = 10;
    }
    else
      if(iMaxValue >= 30) {
        iMinX = 5;
      }
    for(int u = 0; u <= iMaxValue; u++) {
      g.setColor(Color.black);
      g.drawLine(x + u * iStepValue, iY_Of_XAxis, x + u * iStepValue, iY_Of_XAxis + 2);
      if(iMinX > 1) {
        if(u % iMinX == 0 || u == iMaxValue) {
          g.drawString(String.valueOf(u), x + u * iStepValue - 3, iY_Of_XAxis + iRectHeight);
        }
      }
      else {
        g.drawString(String.valueOf(u), x + u * iStepValue - 3, iY_Of_XAxis + iRectHeight);
      }
      g.setColor(colorGrid);
      g.drawLine(x + u * iStepValue, iMarginTop, x + u * iStepValue, iY_Of_XAxis - 1);
    }
    if(sNote != null && sNote.length() > 0) {
      g.setColor(Color.black);
      g.drawString(sNote, iMarginSx, iY_Of_XAxis + iRectHeight + iRectHeight);
    }
    
    // Paint histograms
    int iRectIndex = 0;
    int i = iCountBar < iMinCountBar ? iMinCountBar - iCountBar : 0;
    String sLastCategory = null;
    String sLastSerie    = null;
    for(int c = 0; c < asCategories.length; c++) {
      for(int s = 0; s < asSeries.length; s++) {
        y = iMarginTop + i * (iRectHeight + iVerticalGap);
        double dValue = adData[s][c];
        if(dValue < 0.01d) continue;
        i++;
        int iRectWidth = (int) dValue * iStepValue;
        Color color = null;
        switch (s) {
        case  0: color = Color.blue;       break;
        case  1: color = Color.red;        break;
        case  2: color = Color.green;      break;
        case  3: color = Color.yellow;     break;
        case  4: color = Color.cyan;       break;
        case  5: color = Color.magenta;    break;
        case  6: color = Color.orange;     break;
        case  7: color = Color.lightGray;  break;
        case  8: color = Color.pink;       break;
        case  9: color = color09;          break;
        case 10: color = color10;          break;
        case 11: color = color11;          break;
        case 12: color = color12;          break;
        case 13: color = color13;          break;
        case 14: color = color14;          break;
        case 15: color = Color.blue;       break;
        case 16: color = Color.red;        break;
        case 17: color = Color.green;      break;
        case 18: color = Color.yellow;     break;
        case 19: color = Color.cyan;       break;
        case 20: color = Color.magenta;    break;
        case 21: color = Color.orange;     break;
        case 22: color = Color.lightGray;  break;
        case 23: color = Color.pink;       break;
        case 24: color = color09;          break;
        case 25: color = color10;          break;
        case 26: color = color11;          break;
        case 27: color = color12;          break;
        case 28: color = color13;          break;
        case 29: color = color14;          break;
        default: color = Color.darkGray;   break;
        }
        // Rettangolo
        g2d.setPaint(new GradientPaint(0, 0, Color.white, iMaxBarWith, 0, color));
        g.fillRect(x, y, iRectWidth, iRectHeight);
        // Etichette
        g.setColor(Color.black);
        String sCategory = asCategories[c];
        if(!sCategory.equals(sLastCategory)) {
          g.drawString(sCategory, x - iMaxLabCatWidth, y + iRectHeight - 2);
        }
        sLastCategory = sCategory;
        String sSerie    = asSeries[s];
        if(!sSerie.equals(sLastSerie)) {
          g.drawString(sSerie, x + iRectWidth + iHorizontalGap, y + iRectHeight - 2);
        }
        sLastSerie = sSerie;
        
        arRectangles[iRectIndex++] = new ChartRectangle(x, y, iRectWidth, iRectHeight, c, s);
      }
    }
  }
  
  // java.awt.event.MouseListener -----------------------------------------------
  public void mouseClicked(MouseEvent e) {
    if(arRectangles == null || listActionListener == null || listActionListener.size() == 0) return;
    for(int i = 0; i < arRectangles.length; i++) {
      ChartRectangle rect = arRectangles[i];
      if(rect != null && rect.contains(e.getPoint())) {
        int c = rect.getIndexCategories();
        int s = rect.getIndexSeries();
        if(aiCategories == null || aiCategories.length <= c) return;
        if(aiSeries     == null || aiSeries.length     <= s) return;
        
        int iIdCateg    = aiCategories[c];
        String sDescCat = asCategories[c];
        
        int iIdSerie    = aiSeries[s];
        String sDescSer = asSeries[s];
        
        String command = iIdCateg + "|" + sDescCat + "|" + iIdSerie + "|" + sDescSer;
        
        ActionEvent ae = new ActionEvent(JPChart.this, ActionEvent.ACTION_PERFORMED, command);
        if(listActionListener != null) {
          for(int j = 0; j < listActionListener.size(); j++) {
            ActionListener actionListener = listActionListener.get(j);
            actionListener.actionPerformed(ae);
          }
        }
        
      }
    }
  }
  public void mousePressed(MouseEvent e)  {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseEntered(MouseEvent e)  {}
  public void mouseExited(MouseEvent e)   {}
  // -----------------------------------------------------------------------------
  
  // java.awt.event.MouseMotionListener ------------------------------------------
  public void mouseMoved(MouseEvent e) {
    if(arRectangles == null || listActionListener == null || listActionListener.size() == 0) return;
    for(int i = 0; i < arRectangles.length; i++) {
      ChartRectangle rect = arRectangles[i];
      if(rect != null && rect.contains(e.getPoint())) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return;
      }
    }
    setCursor(Cursor.getDefaultCursor());
  }
  public void mouseDragged(MouseEvent e) {}
  // -----------------------------------------------------------------------------
  
  class ChartRectangle extends Rectangle
  {
    private static final long serialVersionUID = -5806296478756968892L;
    
    protected int iIndexCategories;
    protected int iIndexSeries;
    
    public ChartRectangle()
    {
      super();
    }
    
    public ChartRectangle(int x, int y, int width, int height)
    {
      super(x, y, width, height);
    }
    
    public ChartRectangle(int x, int y, int width, int height, int iIndexCategories, int iIndexSeries)
    {
      super(x, y, width, height);
      this.iIndexCategories = iIndexCategories;
      this.iIndexSeries = iIndexSeries;
    }
    
    public int getIndexCategories() {
      return iIndexCategories;
    }
    
    public void setIndexCategories(int iIndexCategories) {
      this.iIndexCategories = iIndexCategories;
    }
    
    public int getIndexSeries() {
      return iIndexSeries;
    }
    
    public void setIndexSeries(int iIndexSeries) {
      this.iIndexSeries = iIndexSeries;
    }
  }
}
