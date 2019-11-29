package org.dew.swingup.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.IWorkObject;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.components.JBigCalendar;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.JLabelGradient;
import org.dew.util.WUtil;

public class DemoCalendar extends JPanel implements IWorkObject{

  private static final long serialVersionUID = -4748597332358995175L;
  
  protected JBigCalendar jbigCalendar;
  
  protected static final Color cCOLOR_FROM = new Color(210, 230, 255);
  protected static final Color cCOLOR_TO   = new Color(134, 175, 225);
  
  protected static final Color cCOLOR_0    = new Color(200, 255, 200);
  protected static final Color cCOLOR_1    = new Color(255, 255, 200);
  protected static final Color cCOLOR_2    = new Color(255, 200, 200);
  protected static final Color cCOLOR_3    = new Color(200, 220, 245);
  
  protected static final Icon ICON_MARK   = ResourcesMgr.getImageIcon("RedCircleSmall.gif");
  
  public DemoCalendar()
  {
    try {
      init();
    }
    catch (Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di DemoCalendar", ex);
    }
  }

  @Override
  public boolean onClosing() {
    System.out.println("DemoCalendar.onClosing");
    return true;
  }

  @Override
  public void onActivated() {
    System.out.println("DemoCalendar.onActivated");
    
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        loadDemoData();
      }
    });
  }

  @Override
  public void onOpened() {
    System.out.println("DemoCalendar.onOpened");
  }
  
  protected 
  Container buildCalendarContainer() 
  {
    JLabelGradient jlTitle = new JLabelGradient("JBigCalendar Example", JLabel.LEFT, cCOLOR_FROM, cCOLOR_TO);
    jlTitle.setFont(GUIUtil.modifyFont(jlTitle.getFont(), 4));
    jlTitle.setPreferredSize(new Dimension(0, 30));
    jlTitle.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
    
    jbigCalendar = new JBigCalendar(2);
    jbigCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jbigCalendar.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        Date[] selection = jbigCalendar.getSelectedDays_Date();
        if(selection == null || selection.length == 0) {
          return;
        }
        System.out.println("valueChanged (" + WUtil.formatDate(selection[0], "-") + ")");
      }
    });
    jbigCalendar.setEnabled(true);
    
    JPanel jpResult = new JPanel(new BorderLayout(4, 4));
    jpResult.setBorder(BorderFactory.createTitledBorder("Calendar"));
    
    jpResult.add(jlTitle,      BorderLayout.NORTH);
    jpResult.add(jbigCalendar, BorderLayout.CENTER);
    
    return jpResult;
  }
  
  protected
  void loadDemoData()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DATE, 1);
    
    for(int i = 0; i < 60; i++) {
      int iRnd = (int) (Math.random() * 5);
      switch (iRnd) {
        case 0:
          jbigCalendar.setBGColor(cal, cCOLOR_0);
          break;
        case 1:
          jbigCalendar.setBGColor(cal, cCOLOR_1);
          break;
        case 2:
          jbigCalendar.setBGColor(cal, cCOLOR_2);
          break;
        case 3:
          jbigCalendar.setBGColor(cal, cCOLOR_3);
          break;
      }
      if((i + 1) % 10 == 0) {
        jbigCalendar.setIcon(cal, ICON_MARK);
      }
      cal.add(Calendar.DATE, 1);
    }
    
    jbigCalendar.markDay(Calendar.getInstance());
  }
  
  protected 
  void init()
    throws Exception
  {
    this.setLayout(new BorderLayout());
    this.add(buildCalendarContainer(), BorderLayout.CENTER);
  }
}
