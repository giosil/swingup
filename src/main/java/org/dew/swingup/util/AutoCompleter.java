package org.dew.swingup.util;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.dew.swingup.*;
import org.dew.swingup.components.*;

/**
 * Classe astratta per implementare l'autocompletamento su una casella di testo.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"rawtypes","serial","unchecked"})
public abstract
class AutoCompleter
{
  public static final String AUTOCOMPLETER = "AUTOCOMPLETER"; //NOI18N
  
  protected JList list = new JList();
  protected JPopupMenu popup = new JPopupMenu();
  protected JTextComponent textComp;
  protected ArrayList listActionListener = new ArrayList();
  protected boolean boEnableAutoCompletion = true;
  protected boolean boDontShowPopup = false;
  protected int iVisibleRowCount = 10;
  
  protected static Map mapFieldsFlagEnabled = new HashMap();
  
  static Action acceptAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent) e.getSource();
      AutoCompleter completer = (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
      completer.popup.setVisible(false);
      try {
        completer.acceptedListItem((String) completer.list.getSelectedValue());
      }
      catch(Throwable th) {
        System.err.println("Exception in AutoCompleter.acceptedListItem: " + th);
      }
      completer.notifySelection(e);
    }
  };
  
  DocumentListener documentListener = new DocumentListener() {
    public void insertUpdate(DocumentEvent e) {
      showPopup();
    }
    
    public void removeUpdate(DocumentEvent e) {
      showPopup();
    }
    
    public void changedUpdate(DocumentEvent e) {
    }
  };
  
  public
  AutoCompleter(JTextComponent comp)
  {
    this(comp, false);
  }
  
  /**
   * Il parametro boElableToggle serve per abilitare la disattivazione
   * del autocompletamento tramite il tasto F6.
   *
   * @param comp JTextComponent
   * @param boEnableToggle boolean
   */
  public
  AutoCompleter(JTextComponent comp, boolean boEnableToggle)
  {
    textComp = comp;
    
    boEnableAutoCompletion =
      ResourcesMgr.getBooleanProperty(ResourcesMgr.sAPP_AUTOCOMPLETION, true);
    
    String sName = textComp.getName();
    Boolean oFlagEnabled = (Boolean) mapFieldsFlagEnabled.get(sName);
    if(oFlagEnabled != null) {
      boEnableAutoCompletion = oFlagEnabled.booleanValue();
    }
    
    textComp.putClientProperty(AUTOCOMPLETER, this);
    
    if(boEnableToggle) {
      String sToolTipText = textComp.getToolTipText();
      if(sToolTipText == null || sToolTipText.length() == 0) {
        textComp.setToolTipText(getToggleToolTip());
      }
    }
    
    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(null);
    list.setFocusable(false);
    list.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          ActionEvent actionEvent = new ActionEvent(textComp, ActionEvent.ACTION_PERFORMED, "doubleClick");
          acceptAction.actionPerformed(actionEvent);
        }
      }
    });
    scroll.getVerticalScrollBar().setFocusable(false);
    scroll.getHorizontalScrollBar().setFocusable(false);
    
    popup.setBorder(BorderFactory.createLineBorder(Color.black));
    popup.add(scroll);
    
    if(textComp instanceof JTextFieldExt) {
      ((JTextFieldExt) textComp).setSelectionOnFocus(false);
    }
    
    if(textComp instanceof JTextField) {
      textComp.registerKeyboardAction(showAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
        KeyEvent.CTRL_MASK),
        JComponent.WHEN_FOCUSED);
      textComp.registerKeyboardAction(showAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
        JComponent.WHEN_FOCUSED);
      if(boEnableToggle) {
        textComp.registerKeyboardAction(toggleAction,
          getToggleKeyStroke(),
          JComponent.WHEN_FOCUSED);
      }
      textComp.getDocument().addDocumentListener(documentListener);
    }
    
    textComp.registerKeyboardAction(upAction,
      KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
      JComponent.WHEN_FOCUSED);
    textComp.registerKeyboardAction(hidePopupAction,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_FOCUSED);
    textComp.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
      }
      public void focusLost(FocusEvent e) {
        if(!e.isTemporary()) {
          if(popup != null && popup.isVisible()) {
            popup.setVisible(false);
            textComp.transferFocus();
          }
        }
      }
    });
    
    popup.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
    list.setRequestFocusEnabled(false);
  }
  
  public static
  AutoCompleter createDataFileAutoCompleter(JTextComponent jtc, String sDataFile)
  {
    return new DataFileAutoCompleter(jtc, sDataFile);
  }
  
  public static
  AutoCompleter createCollectionAutoCompleter(JTextComponent jtc, Collection colData)
  {
    return new CollectionAutoCompleter(jtc, colData);
  }
  
  public
  void setEnabledAutoCompletion(boolean boEnableAutoCompletion)
  {
    this.boEnableAutoCompletion = boEnableAutoCompletion;
    String sName = textComp.getName();
    if(sName == null) return;
    mapFieldsFlagEnabled.put(sName, new Boolean(boEnableAutoCompletion));
  }
  
  public
  boolean isEnabledAutoCompletion()
  {
    return boEnableAutoCompletion;
  }
  
  public
  void setDontShowPopup(boolean boDontShowPopup)
  {
    this.boDontShowPopup = boDontShowPopup;
  }
  
  public
  boolean isDontShowPopup()
  {
    return boDontShowPopup;
  }
  
  /**
   * Memorizza l'abilitazione all'autocompletamento del componente
   * che ha il nome specificato.
   *
   * @param sComponentName String
   * @param boEnableAutoCompletion boolean
   */
  public static
  void setEnabledAutoCompletion(String sComponentName,
    boolean boEnableAutoCompletion)
  {
    mapFieldsFlagEnabled.put(sComponentName,
      new Boolean(boEnableAutoCompletion));
  }
  
  public
  void setVisibleRowCount(int iVisibleRowCount)
  {
    this.iVisibleRowCount = iVisibleRowCount;
  }
  
  public
  int getVisibleRowCount()
  {
    return iVisibleRowCount;
  }
  
  public
  void addActionListener(ActionListener al)
  {
    if(al != null) {
      listActionListener.add(al);
    }
  }
  
  public
  void removeActionListener(ActionListener al)
  {
    if(al != null) {
      listActionListener.remove(al);
    }
  }
  
  protected
  void showPopup()
  {
    if(!boEnableAutoCompletion) {
      return;
    }
    
    if(boDontShowPopup) {
      return;
    }
    
    try {
      popup.setVisible(false);
      if(textComp.isEnabled() &&
        updateListData() &&
        list.getModel().getSize() != 0) {
        if(!(textComp instanceof JTextField)) {
          textComp.getDocument().addDocumentListener(documentListener);
        }
        textComp.registerKeyboardAction(acceptAction,
          KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
          JComponent.WHEN_FOCUSED);
        int size = list.getModel().getSize();
        list.setVisibleRowCount(size < iVisibleRowCount ? size : iVisibleRowCount);
        int pos = Math.min(textComp.getCaret().getDot(),
          textComp.getCaret().getMark());
        Rectangle recLocation = textComp.getUI().modelToView(textComp, pos);
        if(recLocation != null) {
          popup.show(textComp, recLocation.x, textComp.getHeight());
        }
      }
      else {
        popup.setVisible(false);
      }
    }
    catch(Exception ex) {
      System.err.println("Exception in AutoCompleter.showPopup: " + ex);
    }
    
    textComp.requestFocus();
  }
  
  public static
  AutoCompleter getAutoCompleter(JComponent jComponent)
  {
    Object oResult = jComponent.getClientProperty(AUTOCOMPLETER);
    if(oResult instanceof AutoCompleter) {
      return (AutoCompleter) oResult;
    }
    return null;
  }
  
  static Action showAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent)e.getSource();
      AutoCompleter completer = (AutoCompleter)tf.getClientProperty(AUTOCOMPLETER);
      if(tf.isEnabled()) {
        if(completer.popup.isVisible()) {
          completer.selectNextPossibleValue();
        }
        else {
          completer.showPopup();
        }
      }
    }
  };
  
  static Action toggleAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent) e.getSource();
      String sName = tf.getName();
      if(sName == null) return;
      AutoCompleter completer = (AutoCompleter) tf.getClientProperty(AUTOCOMPLETER);
      if(tf.isEnabled()) {
        boolean boEnabled = completer.isEnabledAutoCompletion();
        completer.setEnabledAutoCompletion(!boEnabled);
        mapFieldsFlagEnabled.put(sName, new Boolean(!boEnabled));
        if(boEnabled) {
          completer.popup.setVisible(false);
          ResourcesMgr.getStatusBar().setWarning("Autocompletamento disabilitato per il campo " + sName + ".");
        }
        else {
          ResourcesMgr.getStatusBar().setWarning("Autocompletamento abilitato per il campo " + sName + ".");
        }
      }
    }
  };
  
  static Action upAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent)e.getSource();
      AutoCompleter completer = (AutoCompleter)tf.getClientProperty(AUTOCOMPLETER);
      if(tf.isEnabled()) {
        if(completer.popup.isVisible()) {
          completer.selectPreviousPossibleValue();
        }
      }
    }
  };
  
  static Action hidePopupAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      JComponent tf = (JComponent)e.getSource();
      AutoCompleter completer = (AutoCompleter)tf.getClientProperty(AUTOCOMPLETER);
      if(tf.isEnabled()) {
        completer.popup.setVisible(false);
        completer.notifyHidePopup();
      }
    }
  };
  
  /**
   * Selects the next item in the list.  It won't change the selection if the
   * currently selected item is already the last item.
   */
  protected
  void selectNextPossibleValue()
  {
    int si = list.getSelectedIndex();
    if(si < list.getModel().getSize() - 1) {
      list.setSelectedIndex(si + 1);
      list.ensureIndexIsVisible(si + 1);
    }
  }
  
  /**
   * Selects the previous item in the list.  It won't change the selection if the
   * currently selected item is already the first item.
   */
  protected
  void selectPreviousPossibleValue()
  {
    int si = list.getSelectedIndex();
    if(si > 0) {
      list.setSelectedIndex(si - 1);
      list.ensureIndexIsVisible(si - 1);
    }
  }
  
  protected
  void notifySelection(ActionEvent e)
  {
    for(int i = 0; i < listActionListener.size(); i++) {
      ActionListener al = (ActionListener) listActionListener.get(i);
      al.actionPerformed(e);
    }
  }
  
  /**
   * Metodo opzionalmente da implementare per modificare il testo che
   * che suggerisce all'utente come abilitare/disabilitare l'autocompletamento.
   *
   * @return String
   */
  protected
  String getToggleToolTip()
  {
    return "F7 per abilitare/disabilitare l'autocompletamento";
  }
  
  /**
   * Metodo opzionalmente da implementare per modificare il tasto che
   * abilita/disabilita l'autocompletamento.
   *
   * @return KeyStroke (predefinito = F7)
   */
  protected
  KeyStroke getToggleKeyStroke()
  {
    return KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
  }
  
  /**
   * Metodo opzionalmente da implementare per catturare l'evento che nasconde
   * il popup (tasto ESC)
   */
  protected void notifyHidePopup()
  {
  }
  
  /**
   * Metodo da implementare per gestire la ricerca delle voci da proporre.
   *
   * @return boolean
   */
  protected abstract boolean updateListData();
  
  /**
   * Metodo da implementare per gestire la scelta di una voce dalla lista.
   *
   * @param sSelectedItem String
   */
  protected abstract void acceptedListItem(String sSelectedItem);
}
