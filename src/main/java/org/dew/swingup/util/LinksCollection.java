package org.dew.swingup.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

@SuppressWarnings({"rawtypes","unchecked"})
public 
class LinksCollection extends JPanel 
{
  private static final long serialVersionUID = 4429248551485906191L;
  
  protected List listOfLinks;
  protected JLabel[] arrayOfJLabel;
  protected List listActionListener;
  protected Map mapLinks = new HashMap();
  
  public LinksCollection()
  {
    super(new FlowLayout(FlowLayout.LEFT));
  }
  
  public LinksCollection(int hgap)
  {
    super(new FlowLayout(FlowLayout.LEFT, hgap, 5));
  }
  
  public 
  void addActionListener(ActionListener al) 
  {
    if(al == null) return;
    if(listActionListener == null) {
      listActionListener = new ArrayList();
    }
    listActionListener.add(al);
  }
  
  public 
  void removeActionListener(ActionListener al) 
  {
    if(al == null) return;
    if(listActionListener == null) return;
    listActionListener.remove(al);
  }
  
  public 
  void setLinks(List listOfLinks) 
  {
    this.removeAll();
    this.updateUI();
    this.mapLinks.clear();
    if(listOfLinks != null && listOfLinks.size() > 0) {
      arrayOfJLabel = new JLabel[listOfLinks.size() * 2 - 1];
      int iCountLinks = listOfLinks.size();
      for(int i = 0; i < iCountLinks; i++) {
        Object oLink  = listOfLinks.get(i);
        if(oLink == null) oLink = "";
        String sLinkName = oLink.toString();
        if(oLink instanceof CodeAndDescription) {
          mapLinks.put(sLinkName, ((CodeAndDescription) oLink).getCode());
        }
        else {
          mapLinks.put(sLinkName, oLink);
        }
        JLabel jlLink = new JLabel(sLinkName);
        jlLink.setName(sLinkName);
        arrayOfJLabel[i * 2]  = jlLink;
        this.add(jlLink);
        if(i < iCountLinks - 1) {
          JLabel jlSeparator = new JLabel("|");
          arrayOfJLabel[i * 2 + 1] = jlSeparator;
          this.add(jlSeparator);
        }
        jlLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jlLink.setForeground(new Color(0, 0, 128));
        jlLink.addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            JLabel jlLabel = (JLabel) e.getSource();
            jlLabel.setForeground(new Color(128, 128, 255));
          }
          public void mouseExited(MouseEvent e) {
            JLabel jlLabel = (JLabel) e.getSource();
            jlLabel.setForeground(new Color(0, 0, 128));
          }
          public void mouseClicked(MouseEvent e) {
            if(listActionListener == null || listActionListener.size() == 0) return;
            JLabel jlLabel = (JLabel) e.getSource();
            String sName = jlLabel.getName();
            Object oLink = mapLinks.get(sName);
            String sLink = oLink != null ? oLink.toString() : "";
            ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sLink);
            for(int i = 0; i < listActionListener.size(); i++) {
              ActionListener al = (ActionListener) listActionListener.get(i);
              al.actionPerformed(ae);
            }
          }
        });
      }
    }
    this.updateUI();
  }
}
