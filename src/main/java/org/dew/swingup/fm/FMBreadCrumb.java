package org.dew.swingup.fm;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class FMBreadCrumb extends JPanel {
  
  private static final long serialVersionUID = 8637954262775485200L;
  
  protected String sPath;
  protected List listTokens;
  protected JLabel[] arrayOfJLabel;
  protected String sSeparator;
  
  protected List listActionListener;
  
  protected String sPathToHide;
  protected String sNameOfPathToHide;
  
  public FMBreadCrumb()
  {
    super(new FlowLayout(FlowLayout.LEFT));
  }
  
  public void setPathToHide(String sPathToHide, String sNameOfPathToHide) {
    this.sPathToHide = sPathToHide;
    this.sNameOfPathToHide = sNameOfPathToHide;
  }
  
  public void addActionListener(ActionListener al) {
    if(al == null) return;
    if(listActionListener == null) {
      listActionListener = new ArrayList();
    }
    listActionListener.add(al);
  }
  
  public void removeActionListener(ActionListener al) {
    if(al == null) return;
    if(listActionListener == null) return;
    listActionListener.remove(al);
  }
  
  public void setPath(String sPath) {
    if(sPathToHide != null && sPathToHide.length() > 0 && sNameOfPathToHide != null && sNameOfPathToHide.length() > 0) {
      int iIndexOf = sPath.indexOf(sPathToHide);
      if(iIndexOf < 0) iIndexOf = sPath.indexOf(sPathToHide.replace('/', '\\'));
      if(iIndexOf >= 0) {
        sPath = sNameOfPathToHide + sPath.substring(iIndexOf + sPathToHide.length());
      }
    }
    this.sPath = sPath;
    this.removeAll();
    this.updateUI();
    listTokens = getTokens(sPath);
    if(listTokens != null && listTokens.size() > 0) {
       arrayOfJLabel = new JLabel[listTokens.size() * 2];
      for(int i = 0; i < listTokens.size(); i++) {
        String sToken  = (String) listTokens.get(i);
        JLabel jlToken = new JLabel(sToken);
        jlToken.setName(String.valueOf(i));
        JLabel jlSeparator = new JLabel(sSeparator);
         arrayOfJLabel[i * 2]     = jlToken;
         arrayOfJLabel[i * 2 + 1] = jlSeparator;
        this.add(jlToken);
        this.add(jlSeparator);
        
        jlToken.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jlToken.setForeground(new Color(0, 0, 128));
        jlToken.addMouseListener(new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            JLabel jlLabel = (JLabel) e.getSource();
            jlLabel.setForeground(new Color(128, 128, 255));
          }
          public void mouseExited(MouseEvent e) {
            JLabel jlLabel = (JLabel) e.getSource();
            jlLabel.setForeground(new Color(0, 0, 128));
          }
          public void mouseClicked(MouseEvent e) {
            JLabel jlLabel = (JLabel) e.getSource();
            String sName = jlLabel.getName();
            int iIndex = -1;
            try{ iIndex = Integer.parseInt(sName); } catch(Exception ex) {}
            if(iIndex < 0) return;
            if(listTokens == null || iIndex >= listTokens.size()) return;
            if(listActionListener != null && listActionListener.size() > 0) {
              String sSelectedPath = "";
              for(int i = 0; i <= iIndex; i++) {
                String sToken = (String) listTokens.get(i);
                if(sToken.equalsIgnoreCase("(root)")) {
                  sSelectedPath += sSeparator;
                }
                else {
                  sSelectedPath += sToken + sSeparator;
                }
              }
              if(sPathToHide != null && sNameOfPathToHide != null && sNameOfPathToHide.length() > 0) {
                if(sSelectedPath.startsWith(sNameOfPathToHide)) {
                  sSelectedPath = sPathToHide + sSelectedPath.substring(sNameOfPathToHide.length());
                }
              }
              ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sSelectedPath);
              for(int i = 0; i < listActionListener.size(); i++) {
                ActionListener al = (ActionListener) listActionListener.get(i);
                al.actionPerformed(ae);
              }
            }
          }
        });
      }
    }
    this.updateUI();
  }
  
  protected List getTokens(String sPath) {
    List listResult = new ArrayList();
    if(sPath == null || sPath.length() == 0) {
      sSeparator = File.separator;
      return listResult;
    }
    int iStart = 0;
    for(int i = 0; i < sPath.length(); i++) {
      if(iStart < 0) iStart = i;
      char c = sPath.charAt(i);
      if(c == '\\' || c == '/') {
        if(sSeparator == null) sSeparator = String.valueOf(c);
        if(i > 0) {
          listResult.add(sPath.substring(iStart, i));
        }
        else {
          listResult.add("(root)");
        }
        iStart = -1;
      }
    }
    if(sSeparator == null) sSeparator = "/";
    if(iStart >= 0 && iStart <= sPath.length() - 1) {
      listResult.add(sPath.substring(iStart, sPath.length()));
    }
    return listResult;
  }
}
