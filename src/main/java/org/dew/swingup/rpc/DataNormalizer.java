package org.dew.swingup.rpc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import org.dew.swingup.*;
import org.dew.swingup.util.*;

/**
 * Classe di utilita' per la normalizzazione dei dati trasferiti tramite chiamate RPC.
 *
 * @author <a href="mailto:giorgio.giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class DataNormalizer
{
  public static
  Object normalize(Object o)
  {
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    if(oRPCClient == null) return o;
    String sClientName   = oRPCClient.toString();
    String sProtocolName = oRPCClient.getProtocolName();
    if(sProtocolName.equals(IRPCClient.sPROTOCOL_XMLRPC)) {
      if(sClientName != null && sClientName.equals("org.xml.rpc.client.XmlRpcInvoker")) {
        return o;
      }
      else {
        return normalizeForXMLRPC(o);
      }
    }
    return o;
  }
  
  private static
  Object normalizeForXMLRPC(Object o)
  {
    if(o instanceof Map) {
      return mapToXMLRPCHashtable((Map) o);
    }
    else
    if(o instanceof Collection) {
      return collectionToXMLRPCVector((Collection) o);
    }
    else
    if(o instanceof String) {
      return o;
    }
    else
    if(o instanceof Integer) {
      return o;
    }
    else
    if(o instanceof Short) {
      return o;
    }
    else
    if(o instanceof Double) {
      return o;
    }
    else
    if(o instanceof BigDecimal) {
      return new Double(((BigDecimal) o).doubleValue());
    }
    else
    if(o instanceof BigInteger) {
      return new Integer(((BigInteger) o).intValue());
    }
    else
    if(o instanceof Float) {
      return o;
    }
    else
    if(o instanceof Boolean) {
      return o;
    }
    else
    if(o instanceof Date) {
      return o;
    }
    else
    if(o instanceof Calendar) {
      return ((Calendar) o).getTime();
    }
    else
    if(o instanceof byte[]) {
      return (byte[]) o;
    }
    else
    if(o instanceof NamedParameter) {
      return ((NamedParameter) o).getValue();
    }
    else
    if(o instanceof CodeAndDescription) {
      return ((CodeAndDescription) o).getCode();
    }
    else
    if(o == null) {
      return "";
    }
    return o.toString();
  }
  
  private static
  Hashtable mapToXMLRPCHashtable(Map oMap)
  {
    if(oMap == null) return null;
    Hashtable oResult = new Hashtable();
    Iterator oItEntry = oMap.entrySet().iterator();
    while(oItEntry.hasNext()) {
      Map.Entry entry = (Map.Entry) oItEntry.next();
      Object oKey   = entry.getKey();
      Object oValue = entry.getValue();
      if(oValue != null) {
        Object oNormValue = normalizeForXMLRPC(oValue);
        if(oNormValue != null) {
          oResult.put(oKey.toString(), oNormValue);
        }
      }
    }
    return oResult;
  }
  
  private static
  Vector collectionToXMLRPCVector(Collection collection)
  {
    if(collection == null) return null;
    Vector oResult = new Vector();
    Iterator iterator = collection.iterator();
    while(iterator.hasNext()) {
      Object oValue = iterator.next();
      if(oValue != null) {
        Object oNormValue = normalizeForXMLRPC(oValue);
        if(oNormValue != null) {
          oResult.add(oNormValue);
        }
      }
    }
    return oResult;
  }
}
