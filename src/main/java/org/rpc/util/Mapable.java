package org.rpc.util;

import java.util.Map;

@SuppressWarnings({"rawtypes"})
public
interface Mapable
{
  public void fromMap(Map map);
  
  public Map toMap();
}
