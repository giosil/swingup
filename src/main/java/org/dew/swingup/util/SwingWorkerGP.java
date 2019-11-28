package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.RootPaneContainer;

/**
 * Variazione di SwingWorker in cui e' prevista l'attivazione del GlassPane.
 *
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 */
public abstract class SwingWorkerGP {
  private Object value; // see getValue(), setValue()
  private RootPaneContainer component;
  
  /**
   * Class to maintain reference to current worker thread
   * under separate synchronization control.
   */
  private static class ThreadVar {
    private Thread thread;
    ThreadVar(Thread t) {
      thread = t;
    }
    
    synchronized Thread get() {
      return thread;
    }
    
    synchronized void clear() {
      thread = null;
    }
  }
  
  private ThreadVar threadVar;
  
  /**
   * Get the value produced by the worker thread, or null if it hasn't been
   * constructed yet.
   *
   * @return Object
   */
  protected synchronized Object getValue() {
    return value;
  }
  
  /**
   * Set the value produced by worker thread
   *
   * @param x Object
   */
  private synchronized void setValue(Object x) {
    value = x;
  }
  
  /**
   * Compute the value to be returned by the <code>get</code> method.
   *
   * @return Object
   */
  public abstract Object construct();
  
  /**
   * Called on the event dispatching thread (not on the worker thread)
   * after the <code>construct</code> method has returned.
   */
  public void finished() {
    disableGlassPane();
  }
  
  /**
   * A new method that interrupts the worker thread.  Call this method
   * to force the worker to stop what it's doing.
   */
  public void interrupt() {
    
    Thread t = threadVar.get();
    if(t != null) {
      t.interrupt();
    }
    threadVar.clear();
    disableGlassPane();
  }
  
  /**
   * Return the value created by the <code>construct</code> method.
   * Returns null if either the constructing thread or the current
   * thread was interrupted before a value was produced.
   *
   * @return the value created by the <code>construct</code> method
   */
  public Object get() {
    while(true) {
      Thread t = threadVar.get();
      if(t == null) {
        return getValue();
      }
      try {
        t.join();
      }
      catch(InterruptedException e) {
        Thread.currentThread().interrupt(); // propagate
        return null;
      }
    }
  }
  
  public SwingWorkerGP(RootPaneContainer component) {
    this.component = component;
    if((component!=null)&&
      (component instanceof Component)&&
      (!(component.getGlassPane() instanceof GlassPane)))
    component.setGlassPane(new GlassPane((Component)component));
    
    final Runnable doFinished = new Runnable() {
      public void run() {
        finished();
      }
    };
    
    Runnable doConstruct = new Runnable() {
      public void run() {
        try {
          setValue(construct());
        }
        finally {
          threadVar.clear();
          disableGlassPane();
        }
        
        SwingUtilities.invokeLater(doFinished);
      }
    };
    
    Thread t = new Thread(doConstruct);
    threadVar = new ThreadVar(t);
    
  }
  
  private void disableGlassPane() {
    if(this.component!=null)
    this.component.getGlassPane().setVisible(false);
  }
  
  /**
   * Start a thread that will call the <code>construct</code> method
   * and then exit.
   */
  public SwingWorkerGP() {
    this(null);
  }
  
  /**
   * Start the worker thread.
   */
  public void start() {
    if(component!=null)
    component.getGlassPane().setVisible(true);
    
    Thread t = threadVar.get();
    if(t != null) {
      t.start();
    }
  }
  
  public static void createGlassPane(RootPaneContainer component) {
    component.setGlassPane(new GlassPane((Component)component));
  }
  
}

class GlassPane extends JComponent implements AWTEventListener {
  
  private static final long serialVersionUID = 1883010215107325461L;
  
  private Component component;
  private MouseListener mouseListener;
  private KeyListener keyListener;
  private boolean visible;
  
  public GlassPane(Component component) {
    this.component = component;
    mouseListener = new MouseAdapter() {};
    keyListener = new KeyAdapter() {};
  }
  
  public void eventDispatched(AWTEvent event) {
    Object source = event.getSource();
    if((source instanceof Component)&&(SwingUtilities.getRoot(component)==component))
    if((event instanceof KeyEvent)&&visible)
    ;((KeyEvent) event).consume();
  }
  
  public void setVisible(boolean visible) {
    this.visible = visible;
    super.setVisible(visible);
    component.setCursor(visible?Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR):Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    if(visible) {
      Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
      addMouseListener(mouseListener);
      addKeyListener(keyListener);
    } else {
      Toolkit.getDefaultToolkit().removeAWTEventListener(this);
      removeMouseListener(mouseListener);
      removeKeyListener(keyListener);
    }
  }
}
