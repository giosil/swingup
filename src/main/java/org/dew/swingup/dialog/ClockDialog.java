package org.dew.swingup.dialog;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.*;

import org.dew.swingup.*;

/**
 * Dialogo che mostra un orologio a sfere.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class ClockDialog extends JDialog implements Runnable
{
  private volatile Thread timer;       // The thread that displays clock
  private int xcenter = 80, ycenter = 85; // Center position
  private int xh, yh, xm, ym, xs, ys;
  private int lastxs, lastys, lastxm, lastym, lastxh, lastyh;  // Dimensions used to draw hands
  private SimpleDateFormat formatter;  // Formats the date displayed
  private String lastdate;             // String to hold date displayed
  private Date currentDate;            // Used to get date to display
  private Color handColor;             // Color of main hands and dial
  private Color numberColor;           // Color of second hand and numbers
  private final static String sPATTERN = "EEE dd/MM/yyyy HH:mm:ss";
  
  public
  ClockDialog(Frame frame)
  {
    super(frame, "Data e ora", true);
    
    try {
      init();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public static
  void showMe(Frame frame)
  {
    ClockDialog oDialog = new ClockDialog(frame);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    oDialog.setLocation(screenSize.width/2 - oDialog.getSize().width/2, screenSize.height/2 - oDialog.getSize().height/2);
    oDialog.setVisible(true);
  }
  
  private
  void init()
    throws Exception
  {
    this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    this.setSize(160, 165);
    this.setResizable(false);
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        doClose();
      }
      public void windowOpened(WindowEvent we) {
        start();
      }
      public void windowActivated(WindowEvent we) {
      }
    });
    
    this.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          doClose();
        }
      }
    });
    
    lastxs = lastys = lastxm = lastym = lastxh = lastyh = 0;
    formatter = new SimpleDateFormat(sPATTERN);
    currentDate = new Date();
    lastdate = formatter.format(currentDate);
    handColor = Color.blue;
    numberColor = Color.darkGray;
  }
  
  protected
  void doClose()
  {
    User user = ResourcesMgr.getSessionManager().getUser();
    
    if(user != null) {
      String sPassword = user.getPassword();
      String sInput = GUIMessage.getPasswordInput("Digitare la password di " + user.getUserName());
      if(sInput == null) return;
      if(sPassword.length() == 0) {
        int passwordHashCode = user.getPasswordHashCode();
        if(passwordHashCode != 0 && passwordHashCode != sInput.hashCode()) {
          GUIMessage.showWarning("La password immessa non \350 esatta.");
          return;
        }
      }
      else {
        if(!sInput.equals("\n") && sPassword != null && !sInput.equals(sPassword)) {
          GUIMessage.showWarning("La password immessa non \350 corretta.");
          return;
        }
      }
    }
    
    dispose();
  }
  
  public
  void start()
  {
    currentDate = new Date();
    calculate();
    saveLast();
    
    timer = new Thread(this);
    timer.start();
  }
  
  public
  void stop()
  {
    if(timer == null) return;
    timer.interrupt();
    timer = null;
  }
  
  public
  void update(Graphics g)
  {
    if(g == null) return;
    
    super.update(g);
    
    currentDate = new Date();
    
    calculate();
    
    formatter.applyPattern(sPATTERN);
    String today = formatter.format(currentDate);
    
    // Erase if necessary
    g.setColor(getBackground());
    if(xs != lastxs || ys != lastys) {
      g.drawLine(xcenter, ycenter, lastxs, lastys);
      g.drawString(lastdate, 10, 155);
    }
    if(xm != lastxm || ym != lastym) {
      g.drawLine(xcenter, ycenter-1, lastxm, lastym);
      g.drawLine(xcenter-1, ycenter, lastxm, lastym);
    }
    if(xh != lastxh || yh != lastyh) {
      g.drawLine(xcenter, ycenter-1, lastxh, lastyh);
      g.drawLine(xcenter-1, ycenter, lastxh, lastyh);
    }
    
    // Draw date and hands
    g.setColor(numberColor);
    g.drawString(today, 10, 155);
    g.drawLine(xcenter, ycenter, xs, ys);
    g.setColor(handColor);
    g.drawLine(xcenter, ycenter-1, xm, ym);
    g.drawLine(xcenter-1, ycenter, xm, ym);
    g.drawLine(xcenter, ycenter-1, xh, yh);
    g.drawLine(xcenter-1, ycenter, xh, yh);
    
    lastdate = today;
    currentDate = null;
    
    saveLast();
  }
  
  public
  void paint(Graphics g)
  {
    if(g == null) return;
    
    super.paint(g);
    
    // Draw the circle and numbers
    g.setColor(handColor);
    g.drawArc(xcenter-50, ycenter-50, 100, 100, 0, 360);
    g.setColor(numberColor);
    g.drawString("9", xcenter-45, ycenter+3);
    g.drawString("3", xcenter+40, ycenter+3);
    g.drawString("12", xcenter-5, ycenter-37);
    g.drawString("6", xcenter-3, ycenter+45);
    
    // Draw date and hands
    g.setColor(numberColor);
    g.drawString(lastdate, 10, 155);
    g.drawLine(xcenter, ycenter, lastxs, lastys);
    g.setColor(handColor);
    g.drawLine(xcenter, ycenter-1, lastxm, lastym);
    g.drawLine(xcenter-1, ycenter, lastxm, lastym);
    g.drawLine(xcenter, ycenter-1, lastxh, lastyh);
    g.drawLine(xcenter-1, ycenter, lastxh, lastyh);
  }
  
  public
  void run()
  {
    Thread me = Thread.currentThread();
    while(timer == me) {
      update(getGraphics());
      try { Thread.sleep(1000); } catch(Exception e) {}
    }
  }
  
  public
  void dispose()
  {
    super.dispose();
    stop();
  }
  
  protected
  void calculate()
  {
    int s = 0, m = 10, h = 10;
    
    formatter.applyPattern("s");
    try {
      s = Integer.parseInt(formatter.format(currentDate));
    } catch(NumberFormatException n) {
      s = 0;
    }
    formatter.applyPattern("m");
    try {
      m = Integer.parseInt(formatter.format(currentDate));
    } catch(NumberFormatException n) {
      m = 10;
    }
    formatter.applyPattern("h");
    try {
      h = Integer.parseInt(formatter.format(currentDate));
    } catch(NumberFormatException n) {
      h = 10;
    }
    
    // Set position of the ends of the hands
     xs = (int)(Math.cos(s * Math.PI / 30 - Math.PI / 2) * 45 + xcenter);
     ys = (int)(Math.sin(s * Math.PI / 30 - Math.PI / 2) * 45 + ycenter);
     xm = (int)(Math.cos(m * Math.PI / 30 - Math.PI / 2) * 40 + xcenter);
     ym = (int)(Math.sin(m * Math.PI / 30 - Math.PI / 2) * 40 + ycenter);
     xh = (int)(Math.cos((h*30 + m / 2) * Math.PI / 180 - Math.PI / 2) * 30
    + xcenter);
     yh = (int)(Math.sin((h*30 + m / 2) * Math.PI / 180 - Math.PI / 2) * 30
    + ycenter);
  }
  
  protected
  void saveLast()
  {
    lastxs = xs; lastys = ys;
    lastxm = xm; lastym = ym;
    lastxh = xh; lastyh = yh;
  }
}
