package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

import org.dew.swingup.*;

/**
 * Classe utilizzata per mostrare documenti html.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public
class HtmlBrowser extends AJInternalFrame
{
  protected JEditorPane jEditorPane;
  protected URL urlHomePage;
  protected JButton btnHome;
  protected HyperlinkListener oDefaultHyperlinkListener;
  
  /**
   * Costruttore senza pagina di inizio.
   * Per impostare la pagina di inizio richiamare setHomePage.
   *
   * @param sTitle Titolo del frame
   * @param sIcon Icona del frame
   */
  public
  HtmlBrowser(String sTitle, String sIcon)
  {
    try {
      init(sTitle, sIcon);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di HtmlBrowser.", ex);
    }
  }
  
  public
  HtmlBrowser(String sTitle, String sIcon, URL urlPage)
  {
    try {
      init(sTitle, sIcon);
      setHomePage(urlPage);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di HtmlBrowser.", ex);
    }
  }
  
  public
  HtmlBrowser(String sTitle, String sIcon, String sText)
  {
    try {
      init(sTitle, sIcon);
      setText(sText);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di HtmlBrowser.", ex);
    }
  }
  
  public
  void setHomePage(URL urlPage)
    throws Exception
  {
    jEditorPane.setPage(urlPage);
    jEditorPane.setCaretPosition(0);
    urlHomePage = urlPage;
  }
  
  public
  void setText(String sText)
    throws Exception
  {
    jEditorPane.setText(sText);
    jEditorPane.setCaretPosition(0);
    urlHomePage = null;
    btnHome.setEnabled(false);
  }
  
  public
  void addHyperlinkListener(HyperlinkListener oHyperlinkListener)
  {
    jEditorPane.addHyperlinkListener(oHyperlinkListener);
  }
  
  public
  void removeHyperlinkListener(HyperlinkListener oHyperlinkListener)
  {
    jEditorPane.removeHyperlinkListener(oHyperlinkListener);
  }
  
  public
  void removeDefaultHyperlinkListener()
  {
    jEditorPane.removeHyperlinkListener(oDefaultHyperlinkListener);
  }
  
  public
  Container buildGUI()
    throws Exception
  {
    jEditorPane = new JEditorPane();
    jEditorPane.setEditable(false);
    jEditorPane.setContentType("text/html");
    oDefaultHyperlinkListener = createHyperLinkListener();
    jEditorPane.addHyperlinkListener(oDefaultHyperlinkListener);
    JScrollPane oScrollPane = new JScrollPane(jEditorPane);
    
    JPanel oButtonsPanel = new JPanel();
    btnHome = GUIUtil.buildActionButton(IConstants.sGUIDATA_HOME, "home");
    JButton btnPrint = GUIUtil.buildActionButton(IConstants.sGUIDATA_PRINT, "print");
    JButton btnExit = GUIUtil.buildActionButton(IConstants.sGUIDATA_EXIT,  "exit");
    oButtonsPanel.add(btnHome);
    oButtonsPanel.add(btnPrint);
    oButtonsPanel.add(btnExit);
    
    btnHome.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(urlHomePage == null) return;
        try {
          jEditorPane.setPage(urlHomePage);
          jEditorPane.setCaretPosition(0);
          jEditorPane.updateUI();
        }
        catch(IOException ex) {
          GUIMessage.showException("Errore nell'apertura della pagina.", ex);
        }
      }
    });
    
    btnPrint.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DocumentRenderer dr = new DocumentRenderer();
        dr.print(jEditorPane);
      }
    });
    
    btnExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        close();
      }
    });
    
    JPanel oMainPanel = new JPanel(new BorderLayout());
    oMainPanel.add(oScrollPane, BorderLayout.CENTER);
    oMainPanel.add(oButtonsPanel, BorderLayout.SOUTH);
    
    return oMainPanel;
  }
  
  public
  boolean onClosing()
  {
    return true;
  }
  
  public
  void onActivated()
  {
  }
  
  public
  void onOpened()
  {
  }
  
  public HyperlinkListener createHyperLinkListener() {
    return new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          if(e instanceof HTMLFrameHyperlinkEvent) {
            ((HTMLDocument) jEditorPane.getDocument()).processHTMLFrameHyperlinkEvent(
              (HTMLFrameHyperlinkEvent)e);
          } else {
            try {
              URL url = e.getURL();
              if(url == null) return;
              String sURL = url.toString();
              if(sURL.indexOf(' ') >= 0) {
                StringBuffer sbURLEncoded = new StringBuffer();
                for(int i = 0; i < sURL.length(); i++) {
                  char c = sURL.charAt(i);
                  if(c == ' ') {
                    sbURLEncoded.append("%20");
                  }
                  else {
                    sbURLEncoded.append(c);
                  }
                }
                url = new URL(sbURLEncoded.toString());
              }
              jEditorPane.setPage(url);
              jEditorPane.setCaretPosition(0);
            }
            catch(Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    };
  }
}
