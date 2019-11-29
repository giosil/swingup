package org.dew.swingup.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public
class OutputStreamTextArea extends OutputStream
{
  protected JTextArea jTextArea;

  public
  OutputStreamTextArea(JTextArea jTextArea)
  {
    this.jTextArea = jTextArea;
  }

  public void write(int b) throws IOException {
    if(jTextArea != null) {
      jTextArea.append(String.valueOf((char) b));
    }
  }
}
