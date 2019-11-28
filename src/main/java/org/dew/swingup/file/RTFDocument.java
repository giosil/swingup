package org.dew.swingup.file;

/**
 * Implementazione di ContentObject che consente la creazione di un documento rtf.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class RTFDocument extends ContentObject
{
  public
  RTFDocument()
  {
  }
  
  public
  RTFDocument(String sText)
  {
    this.data = sText;
  }
  
  public
  void build()
  {
    content = new StringBuffer();
    content.append("{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1040{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}}\n");
    content.append("\\viewkind4\\uc1\\pard\\f0\\fs20\\par\n");
    if(data == null) {
      content.append("|[content]|\\par\n");
    }
    else {
      content.append(data.toString());
    }
    content.append("}\n");
  }
}
