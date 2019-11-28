package org.dew.swingup.file;

/**
 * Implementazione di ContentObject che consente l'inserimento di un testo in
 * un documento rtf.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class RTFText extends ContentObject
{
  public
  RTFText()
  {
  }
  
  public
  RTFText(String sText)
  {
    this.data = sText;
  }
  
  public
  void build()
  {
    content = new StringBuffer();
    if(data == null) {
      return;
    }
    String sData = data.toString();
    for(int i = 0; i < sData.length(); i++) {
      char c = sData.charAt(i);
      if(c == '\\') {
        content.append("\\\\");
      }
      else if(c == '{') {
        content.append("\\{");
      }
      else if(c == '}') {
        content.append("\\}");
      }
      else if(c == '\n') {
        content.append(" \\par ");
      }
      else if(c == '\t') {
        content.append(" \\tab ");
      }
      else {
        content.append(c);
      }
    }
  }
}
