package org.dew.swingup.components;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

/**
 * Componente per la gestione del calendario.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public
class JBigCalendar extends JPanel
{
  protected int iMonths     = 1;
  protected int iStartMonth = 1;
  protected int iStartYear  = 2000;
  protected Locale oLocale  = Locale.getDefault();
  
  protected Map mapComponents = new HashMap();
  protected Map mapBGColors   = new HashMap();
  protected Map mapFGColors   = new HashMap();
  protected Map mapIcons      = new HashMap();
  protected Map mapMarked     = new HashMap();
  protected List listPrevSelectedDays = new ArrayList();
  protected List listSelectedDays     = new ArrayList();
  protected List listListener         = new ArrayList();
  
  protected boolean boEnabled = true;
  protected int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  
  protected Color colorBGDay;
  protected Color colorFGDay;
  protected Color colorFGDayDisabled;
  protected Color colorDesktop;
  protected Color colorBorderDay;
  protected Color colorBGWeekDay;
  protected Color colorBGWeekDayFestivity;
  protected Color colorBGMonth;
  protected Color colorFGMonth;
  protected Font fontDay;
  protected Font fontDayMarked;
  protected Font fontMonth;
  protected Border borderDay;
  protected Border borderSelected;
  
  public
  JBigCalendar(int iMonths, int iStartMonth, int iStartYear)
  {
    if(iMonths < 1 || iMonths > 6) {
      throw new Error("Parametro iMonths non valido. [1-6]");
    }
    if(iStartMonth < 1 || iStartMonth > 12) {
      throw new Error("Parametro iStartMonth non valido. [1-12]");
    }
    if(iStartYear < 1900 || iStartYear > 9999) {
      throw new Error("Parametro iStartYear non valido. [1900-9999]");
    }
    try{
      this.iMonths     = iMonths;
      this.iStartMonth = iStartMonth;
      this.iStartYear  = iStartYear;
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public
  JBigCalendar(int iMonths)
  {
    try{
      this.iMonths     = iMonths;
      this.iStartMonth = getCurrentMonth();
      this.iStartYear  = getCurrentYear();
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public
  JBigCalendar()
  {
    this(1);
  }
  
  public
  void addListSelectionListener(ListSelectionListener lsl)
  {
    listListener.add(lsl);
  }
  
  public
  void removeListSelectionListener(ListSelectionListener lsl)
  {
    listListener.remove(lsl);
  }
  
  public
  void removeAllListSelectionListeners()
  {
    listListener.clear();
  }
  
  public
  void setEnabled(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
    this.boEnabled = boEnabled;
    Iterator oItKeys = mapComponents.keySet().iterator();
    
    if(boEnabled) {
      while(oItKeys.hasNext()) {
        Integer oKey = (Integer) oItKeys.next();
        JLabel jlDay = (JLabel) mapComponents.get(oKey);
        jlDay.setForeground(colorFGDay);
      }
      refreshFGColors();
    }
    else {
      while(oItKeys.hasNext()) {
        Integer oKey = (Integer) oItKeys.next();
        JLabel jlDay = (JLabel) mapComponents.get(oKey);
        jlDay.setForeground(colorFGDayDisabled);
      }
    }
  }
  
  public
  boolean isEnabled()
  {
    return boEnabled;
  }
  
  /**
   * Pulisce i colori, le icone e le date selezionate.
   */
  public
  void clear()
  {
    clearBGColors();
    clearFGColors();
    clearIcons();
    clearMarked();
    clearSelection();
  }
  
  /**
   * Riaggiorna i colori, le icone e le date selezionate.
   */
  public
  void refresh()
  {
    refreshBGColors();
    refreshFGColors();
    refreshIcons();
    refreshMarkedDays();
    markSelectedDays();
  }
  
  public
  void clearBGColors()
  {
    mapBGColors.clear();
    Iterator oItKeys = mapComponents.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      jlDay.setBackground(colorBGDay);
    }
  }
  
  public
  void clearFGColors()
  {
    mapFGColors.clear();
    Iterator oItKeys = mapComponents.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      jlDay.setForeground(colorFGDay);
    }
  }
  
  public
  void clearIcons()
  {
    mapIcons.clear();
    Iterator oItKeys = mapComponents.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      jlDay.setIcon(null);
    }
  }
  
  public
  void clearMarked()
  {
    mapMarked.clear();
    Iterator oItKeys = mapComponents.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      jlDay.setFont(fontDay);
    }
  }
  
  public
  void setSelectionMode(int selectionMode)
  {
    clearSelection();
    this.selectionMode = selectionMode;
  }
  
  public
  void clearSelection()
  {
    unmarkSelectedDays();
    listSelectedDays = new ArrayList();
    listPrevSelectedDays = new ArrayList();
    ListSelectionEvent lse = new ListSelectionEvent(this, 0, listSelectedDays.size(), false);
    for(int i = 0; i < listListener.size(); i++) {
      ListSelectionListener lsl = (ListSelectionListener) listListener.get(i);
      lsl.valueChanged(lse);
    }
  }
  
  public
  void setBGColor(Integer oDay, Color color)
  {
    mapBGColors.put(oDay, color);
    JLabel jlDay = (JLabel) mapComponents.get(oDay);
    if(jlDay == null) return;
    jlDay.setBackground(color);
  }
  
  public
  void setBGColor(Calendar calDay, Color color)
  {
    setBGColor(getKey(calDay), color);
  }
  
  public
  void setBGColor(Date dateDay, Color color)
  {
    setBGColor(getKey(dateDay), color);
  }
  
  public
  void setFGColor(Integer oDay, Color color)
  {
    mapFGColors.put(oDay, color);
    JLabel jlDay = (JLabel) mapComponents.get(oDay);
    if(jlDay == null) return;
    jlDay.setForeground(color);
  }
  
  public
  void setFGColor(Calendar calDay, Color color)
  {
    setFGColor(getKey(calDay), color);
  }
  
  public
  void setFGColor(Date dateDay, Color color)
  {
    setFGColor(getKey(dateDay), color);
  }
  
  public
  void markDay(Integer oDay)
  {
    mapMarked.put(oDay, Boolean.valueOf(true));
    JLabel jlDay = (JLabel) mapComponents.get(oDay);
    if(jlDay == null) return;
    jlDay.setFont(fontDayMarked);
  }
  
  public
  void markDay(Calendar calDay)
  {
    markDay(getKey(calDay));
  }
  
  public
  void markDay(Date dateDay)
  {
    markDay(getKey(dateDay));
  }
  
  public
  void unmarkDay(Integer oDay)
  {
    mapMarked.put(oDay, Boolean.valueOf(false));
    JLabel jlDay = (JLabel) mapComponents.get(oDay);
    if(jlDay == null) return;
    jlDay.setFont(fontDay);
  }
  
  public
  void unmarkDay(Calendar calDay)
  {
    unmarkDay(getKey(calDay));
  }
  
  public
  void unmarkDay(Date dateDay)
  {
    unmarkDay(getKey(dateDay));
  }
  
  public
  void setIcon(Integer oDay, Icon icon)
  {
    mapIcons.put(oDay, icon);
    JLabel jlDay = (JLabel) mapComponents.get(oDay);
    if(jlDay == null) return;
    jlDay.setIcon(icon);
  }
  
  public
  void setIcon(Calendar calDay, Icon icon)
  {
    setIcon(getKey(calDay), icon);
  }
  
  public
  void setIcon(Date dateDay, Icon icon)
  {
    setIcon(getKey(dateDay), icon);
  }
  
  public
  void nextMonth()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    calendar.add(Calendar.MONTH, 1);
    this.iStartMonth = calendar.get(Calendar.MONTH) + 1;
    this.iStartYear  = calendar.get(Calendar.YEAR);
    buildCalendar();
    refresh();
  }
  
  public
  void prevMonth()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    calendar.add(Calendar.MONTH, -1);
    this.iStartMonth = calendar.get(Calendar.MONTH) + 1;
    this.iStartYear  = calendar.get(Calendar.YEAR);
    buildCalendar();
    refresh();
  }
  
  public
  void setStartMonth(int iMonth, int iYear)
  {
    if(iMonth < 1 || iMonth > 12) {
      throw new Error("Parametro iStartMonth non valido. [1-12]");
    }
    if(iYear < 1900 || iYear > 9999) {
      throw new Error("Parametro iStartYear non valido. [1900-9999]");
    }
    this.iStartMonth = iMonth;
    this.iStartYear = iYear;
    
    buildCalendar();
    
    refresh();
  }
  
  public
  void setCurrentMonth()
  {
    setStartMonth(getCurrentMonth(), getCurrentYear());
  }
  
  public
  Component getComponentDay(Integer oDay)
  {
    return (Component) mapComponents.get(oDay);
  }
  
  public
  Component getComponentDay(Calendar calDay)
  {
    return (Component) mapComponents.get(getKey(calDay));
  }
  
  public
  Component getComponentDay(Date dateDay)
  {
    return (Component) mapComponents.get(getKey(dateDay));
  }
  
  public
  Integer getBegin_Integer()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    return getKey(calendar);
  }
  
  public
  Integer getEnd_Integer()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    calendar.add(Calendar.MONTH, iMonths);
    calendar.add(Calendar.DATE, -1);
    return getKey(calendar);
  }
  
  public
  Calendar getBegin_Calendar()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    return calendar;
  }
  
  public
  Calendar getEnd_Calendar()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    calendar.add(Calendar.MONTH, iMonths);
    calendar.add(Calendar.DATE, -1);
    return calendar;
  }
  
  public
  Date getBegin_Date()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    return calendar.getTime();
  }
  
  public
  Date getEnd_Date()
  {
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    calendar.add(Calendar.MONTH, iMonths);
    calendar.add(Calendar.DATE, -1);
    return calendar.getTime();
  }
  
  public
  void setSelectionInterval(Integer oKeyBegin, Integer oKeyEnd)
  {
    if(oKeyBegin == null || oKeyEnd == null) {
      clearSelection();
      return;
    }
    
    Integer oLowerLimit = getBegin_Integer();
    Integer oUpperLimit = getEnd_Integer();
    
    if(oKeyBegin.compareTo(oUpperLimit) > 0 || oKeyEnd.compareTo(oLowerLimit) < 0) {
      return;
    }
    if(oKeyBegin.compareTo(oLowerLimit) < 0) {
      oKeyBegin = oLowerLimit;
    }
    if(oKeyEnd.compareTo(oUpperLimit) > 0) {
      oKeyEnd = oUpperLimit;
    }
    
    unmarkSelectedDays();
    listSelectedDays = new ArrayList();
    listSelectedDays.add(oKeyBegin);
    selectUpTo(oKeyEnd);
    notifySelection();
  }
  
  public
  void setSelectionInterval(Calendar calBegin, Calendar calEnd)
  {
    Integer oKeyBegin = getKey(calBegin);
    Integer oKeyEnd = getKey(calEnd);
    setSelectionInterval(oKeyBegin, oKeyEnd);
  }
  
  public
  void setSelectionInterval(Date dateBegin, Date dateEnd)
  {
    Integer oKeyBegin = getKey(dateBegin);
    Integer oKeyEnd = getKey(dateEnd);
    setSelectionInterval(oKeyBegin, oKeyEnd);
  }
  
  public
  Integer[] getSelectedDays_Integer()
  {
    Integer[] oResult = new Integer[listSelectedDays.size()];
    for(int i = 0; i < listSelectedDays.size(); i++) {
      Integer oKey = (Integer) listSelectedDays.get(i);
      oResult[i] = oKey;
    }
    return oResult;
  }
  
  public
  Calendar[] getSelectedDays_Calendar()
  {
    Calendar[] oResult = new Calendar[listSelectedDays.size()];
    for(int i = 0; i < listSelectedDays.size(); i++) {
      Integer oKey = (Integer) listSelectedDays.get(i);
      oResult[i] = getCalendar(oKey);
    }
    return oResult;
  }
  
  public
  Date[] getSelectedDays_Date()
  {
    Date[] oResult = new Date[listSelectedDays.size()];
    for(int i = 0; i < listSelectedDays.size(); i++) {
      Integer oKey = (Integer) listSelectedDays.get(i);
      oResult[i] = getDate(oKey);
    }
    return oResult;
  }
  
  protected
  int getCurrentMonth()
  {
    Calendar cal = new GregorianCalendar();
    return cal.get(Calendar.MONTH) + 1;
  }
  
  protected
  int getCurrentYear()
  {
    Calendar cal = new GregorianCalendar();
    return cal.get(Calendar.YEAR);
  }
  
  protected
  void init()
    throws Exception
  {
    setLayout(new GridLayout(1, iMonths));
    setDoubleBuffered(true);
    setOpaque(true);
    initColorsAndFonts();
    buildCalendar();
  }
  
  protected
  void buildCalendar()
  {
    removeAll();
    
    mapComponents = new HashMap();
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iStartYear, iStartMonth - 1, 1);
    int iMonth = iStartMonth;
    int iYear = iStartYear;
    for(int i = 0; i < iMonths; i++) {
      JPanel oMonthPanel = buildMonthPanel(iMonth, iYear);
      calendar.add(Calendar.MONTH, 1);
      iMonth = calendar.get(Calendar.MONTH) + 1;
      iYear = calendar.get(Calendar.YEAR);
      this.add(oMonthPanel);
    }
    
    updateUI();
  }
  
  protected
  JPanel buildMonthPanel(int iMonth, int iYear)
  {
    JPanel oResult = new JPanel(new BorderLayout());
    
    Calendar calendar = Calendar.getInstance(oLocale);
    calendar.set(iYear, iMonth - 1, 1);
    int iStartIndex = getCalendarIndex(calendar);
    JPanel oCalendarPanel = new JPanel(new GridLayout(6, 7));
     for(int j = 0; j < 6 * 7; j++) {
      int iDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      JComponent oDayComponent = null;
      if(j < iStartIndex ||(j != iStartIndex && iDayOfMonth == 1)) {
        oDayComponent = new JPanel();
        oDayComponent.setBackground(colorDesktop);
      }
      else {
        Integer oKey = getKey(calendar);
        oDayComponent = new JLabel(String.valueOf(iDayOfMonth),JLabel.CENTER);
        oDayComponent.setForeground(boEnabled ? colorFGDay : colorFGDayDisabled);
        oDayComponent.setBorder(borderDay);
        oDayComponent.setOpaque(true);
        oDayComponent.setBackground(colorBGDay);
        oDayComponent.addMouseListener(new DayMouseListener(oKey));
        oDayComponent.setFont(fontDay);
        
        mapComponents.put(oKey, oDayComponent);
        
        calendar.add(Calendar.DATE, 1);
      }
      oCalendarPanel.add(oDayComponent);
    }
    
    oResult.add(getHeaderMonth(iMonth, iYear), BorderLayout.NORTH);
    oResult.add(oCalendarPanel, BorderLayout.CENTER);
    
    return oResult;
  }
  
  protected
  Container getHeaderMonth(int iMonth, int iYear)
  {
    JPanel oResult = new JPanel(new BorderLayout());
    
    JPanel oWeekPanel = new JPanel(new GridLayout(1, 7));
    for(int i = 0; i < 7; i++) {
      JLabel oWeekDayLabel = new JLabel(getWeekDayName(i));
      oWeekDayLabel.setOpaque(true);
      oWeekDayLabel.setBackground(colorBGWeekDay);
      if(i > 4) {
        oWeekDayLabel.setForeground(colorBGWeekDayFestivity);
      }
      oWeekPanel.add(oWeekDayLabel);
    }
    
    JLabel oMonthLabel = new JLabel(getMonthName(iMonth) + " " + iYear);
    oMonthLabel.setOpaque(true);
    oMonthLabel.setBackground(colorBGMonth);
    oMonthLabel.setForeground(colorFGMonth);
    oMonthLabel.setFont(fontMonth);
    
    oResult.add(oMonthLabel, BorderLayout.NORTH);
    oResult.add(oWeekPanel, BorderLayout.CENTER);
    
    return oResult;
  }
  
  protected
  String getMonthName(int iMonth)
  {
    Calendar cal = Calendar.getInstance(oLocale);
    cal.set(iStartYear, iMonth - 1, 1);
    DateFormat df = new SimpleDateFormat("MMMMM", oLocale);
    return df.format(cal.getTime());
  }
  
  protected
  String getWeekDayName(int iIndexWeekDay)
  {
    DateFormat df = new SimpleDateFormat("EEE", oLocale);
    Calendar gc = new GregorianCalendar();
    int i = gc.get(Calendar.DAY_OF_WEEK);
    int diff = iIndexWeekDay - i + 2;
    gc.add(Calendar.DATE, diff);
    return df.format(gc.getTime());
  }
  
  protected
  int getCalendarIndex(Calendar calDay)
  {
    int iResult = 0;
    
    int iFirstWeekDay = calDay.get(Calendar.DAY_OF_WEEK);
    if(iFirstWeekDay == Calendar.MONDAY) {
      iResult = 7;
    }
    else {
      if(iFirstWeekDay == Calendar.SUNDAY) {
        iResult = 6;
      }
      else {
        iResult = iFirstWeekDay - 2;
      }
    }
    return iResult;
  }
  
  protected static
  Integer getKey(Date date)
  {
    if(date == null) return null;
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return new Integer(cal.get(Calendar.YEAR)*10000+(cal.get(Calendar.MONTH)+1)*100+cal.get(Calendar.DAY_OF_MONTH));
  }
  
  protected static
  Integer getKey(Calendar cal)
  {
    if(cal == null) return null;
    return new Integer(cal.get(Calendar.YEAR)*10000+(cal.get(Calendar.MONTH)+1)*100+cal.get(Calendar.DAY_OF_MONTH));
  }
  
  protected
  Calendar getCalendar(Integer oKey)
  {
    if(oKey == null) return null;
    int iKey = oKey.intValue();
    int iYear = iKey / 10000;
    int iMonth = (iKey % 10000) / 100;
    int iDay = (iKey % 10000) % 100;
    Calendar cal = Calendar.getInstance(oLocale);
    cal.set(iYear, iMonth - 1, iDay, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }
  
  protected
  Date getDate(Integer oKey)
  {
    if(oKey == null) return null;
    int iKey = oKey.intValue();
    int iYear = iKey / 10000;
    int iMonth = (iKey % 10000) / 100;
    int iDay = (iKey % 10000) % 100;
    Calendar cal = Calendar.getInstance(oLocale);
    cal.set(iYear, iMonth - 1, iDay, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }
  
  protected
  void markSelect(Integer oKey)
  {
    JLabel jlDay = (JLabel) mapComponents.get(oKey);
    if(jlDay == null) return;
    jlDay.setBorder(borderSelected);
  }
  
  protected
  void markSelectedDays()
  {
    for(int i = 0; i < listSelectedDays.size(); i++) {
      Integer oKey = (Integer) listSelectedDays.get(i);
      markSelect(oKey);
    }
  }
  
  protected
  void refreshBGColors()
  {
    Iterator oItKeys = mapBGColors.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      if(jlDay == null) continue;
      Color color = (Color) mapBGColors.get(oKey);
      if(color == null) continue;
      jlDay.setBackground(color);
    }
  }
  
  protected
  void refreshFGColors()
  {
    Iterator oItKeys = mapFGColors.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      if(jlDay == null) continue;
      Color color = (Color) mapFGColors.get(oKey);
      if(color == null) continue;
      jlDay.setForeground(color);
    }
  }
  
  protected
  void refreshIcons()
  {
    Iterator oItKeys = mapIcons.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      if(jlDay == null) continue;
      jlDay.setIcon((Icon) mapIcons.get(oKey));
    }
  }
  
  protected
  void refreshMarkedDays()
  {
    Iterator oItKeys = mapMarked.keySet().iterator();
    while(oItKeys.hasNext()) {
      Integer oKey = (Integer) oItKeys.next();
      JLabel jlDay = (JLabel) mapComponents.get(oKey);
      if(jlDay == null) continue;
      Boolean oMarked = (Boolean) mapMarked.get(oKey);
      if(oMarked == null) continue;
      jlDay.setFont(oMarked.booleanValue() ? fontDay : fontDayMarked);
    }
  }
  
  protected
  void unmarkSelect(Integer oKey)
  {
    JLabel jlDay = (JLabel) mapComponents.get(oKey);
    if(jlDay == null) return;
    jlDay.setBorder(borderDay);
  }
  
  protected
  void unmarkSelectedDays()
  {
    for(int i = 0; i < listSelectedDays.size(); i++) {
      Integer oKey = (Integer) listSelectedDays.get(i);
      unmarkSelect(oKey);
    }
  }
  
  protected
  void select(Integer oKey, boolean boAdd)
  {
    if(!boAdd) {
      unmarkSelectedDays();
      listSelectedDays = new ArrayList();
    }
    listSelectedDays.add(oKey);
    if(!boAdd) {
      markSelectedDays();
    }
    else {
      markSelect(oKey);
    }
  }
  
  protected
  void selectUpTo(Integer oEndKey)
  {
    if(listSelectedDays.size() == 0) {
      select(oEndKey, false);
      return;
    }
    
    if(oEndKey == null) return;
    int iEndKey = oEndKey.intValue();
    
    Integer oFirst = (Integer) listSelectedDays.get(0);
    
    unmarkSelectedDays();
    listSelectedDays = new ArrayList();
    
    Integer oKey = oFirst;
    Calendar cal = getCalendar(oFirst);
    if(oFirst.intValue() < iEndKey) {
      while(oKey.intValue() <= iEndKey) {
        listSelectedDays.add(oKey);
        cal.add(Calendar.DATE, 1);
        oKey = getKey(cal);
      }
    }
    else {
      while(oKey.intValue() >= iEndKey) {
        listSelectedDays.add(oKey);
        cal.add(Calendar.DATE, -1);
        oKey = getKey(cal);
      }
    }
    
    markSelectedDays();
  }
  
  protected
  void unselect(Integer oKey)
  {
    listSelectedDays.remove(oKey);
    unmarkSelect(oKey);
  }
  
  protected
  boolean isSelected(Integer oKey)
  {
    return listSelectedDays.contains(oKey);
  }
  
  protected
  void mousePressedOnDay(Integer oKey, boolean boIsControlDown, boolean boIsShiftDown)
  {
    if(!boEnabled) return;
    
    if(boIsShiftDown && selectionMode != ListSelectionModel.SINGLE_SELECTION) {
      selectUpTo(oKey);
    }
    else {
      if(boIsControlDown &&
      selectionMode != ListSelectionModel.SINGLE_SELECTION &&
      selectionMode != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
        if(isSelected(oKey)) {
          unselect(oKey);
        }
        else {
          select(oKey, true);
        }
      }
      else {
        select(oKey, false);
      }
    }
  }
  
  protected
  void mouseEnteredOnDay(Integer oKey)
  {
    if(!boEnabled) return;
    if(!isSelected(oKey)) select(oKey, selectionMode != ListSelectionModel.SINGLE_SELECTION);
  }
  
  protected
  void mouseReleasedOnDay(Integer oKey)
  {
    if(!boEnabled) return;
    notifySelection();
  }
  
  protected
  void notifySelection()
  {
    boolean boNotify = true;
    if(listPrevSelectedDays.equals(listSelectedDays)) boNotify = false;
    if(boNotify) {
      ListSelectionEvent lse = new ListSelectionEvent(this, 0, listSelectedDays.size(), false);
      for(int i = 0; i < listListener.size(); i++) {
        ListSelectionListener lsl = (ListSelectionListener) listListener.get(i);
        lsl.valueChanged(lse);
      }
    }
    listPrevSelectedDays = new ArrayList(listSelectedDays);
  }
  
  protected
  void initColorsAndFonts()
  {
    colorBGDay      = UIManager.getColor("TextField.background");
    colorFGDay      = UIManager.getColor("TextField.foreground");
    colorFGDayDisabled = UIManager.getColor("TextField.inactiveForeground");
    colorDesktop    = UIManager.getColor("Desktop.background");
    colorBorderDay  = UIManager.getColor("Table.gridColor");
    colorBGWeekDay  = new Color(255, 255, 200);
    colorBGWeekDayFestivity = Color.red;
    colorBGMonth    = UIManager.getColor("Button.darkShadow");
    colorFGMonth    = UIManager.getColor("Button.light");
    fontDay         = UIManager.getFont("TextField.font");
    fontDayMarked   = new Font(fontDay.getName(), Font.BOLD, fontDay.getSize() + 2);
    Font fontLabel  = UIManager.getFont("Label.font");
    fontMonth       = new Font(fontLabel.getName(), fontLabel.getStyle(), fontLabel.getSize() + 2);
    borderDay       = BorderFactory.createLineBorder(colorDesktop, 1);
    borderSelected  = BorderFactory.createLineBorder(Color.blue, 2);
  }
  
  class DayMouseListener extends MouseAdapter
  {
    Integer oKey = null;
    
    public DayMouseListener(Integer oKey) {
      this.oKey = oKey;
    }
    
    public void mousePressed(MouseEvent e) {
      mousePressedOnDay(oKey, e.isControlDown(), e.isShiftDown());
    }
    
    public void mouseReleased(MouseEvent e) {
      mouseReleasedOnDay(oKey);
    }
    
    public void mouseEntered(MouseEvent e) {
      if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
        mouseEnteredOnDay(oKey);
      }
    }
  }
}
