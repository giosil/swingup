package org.dew.swingup.layout;

import java.io.Serializable;

/**
 * Classe utilizzata da XYlayout per l'assegnazione dei vincoli.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class XYConstraints implements Cloneable, Serializable
{
  int x;
  int y;
  int width;
  int height;
  
  public
  XYConstraints()
  {
    this(0, 0, 0, 0);
  }
  
  public
  XYConstraints(int x, int y, int width, int height)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  
  public
  int getX()
  {
    return x;
  }
  
  public
  void setX(int x)
  {
    this.x = x;
  }
  
  public
  int getY()
  {
    return y;
  }
  
  public
  void setY(int y)
  {
    this.y = y;
  }
  
  public
  int getWidth()
  {
    return width;
  }
  
  public
  void setWidth(int width)
  {
    this.width = width;
  }
  
  public
  int getHeight()
  {
    return height;
  }
  
  public
  void setHeight(int height)
  {
    this.height = height;
  }
  
  public
  int hashCode()
  {
     return x ^ y * 37 ^ width * 43 ^ height * 47;
  }
  
  public
  boolean equals(Object that)
  {
    if(that instanceof XYConstraints) {
      XYConstraints other = (XYConstraints)that;
      return other.x == x && other.y == y &&
        other.width == width && other.height == height;
    } else {
      return false;
    }
  }
  
  public
  Object clone()
  {
    return new XYConstraints(x, y, width, height);
  }
  
  public
  String toString()
  {
    return "XYConstraints[" + x + "," + y + "," + width + "," + height + "]";
  }
}
