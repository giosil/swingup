package org.dew.rpc.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.dew.json.rpc.commons.RpcRemoteException;
import org.dew.util.Arrays;
import org.dew.util.WUtil;
import org.dew.xml.rpc.XmlRpcException;

@SuppressWarnings({"rawtypes","unchecked"})
public
class RpcUtil
{
  public static
  String completeMethodName(String methodName, String[] requestData)
  {
    if(requestData.length > 2) {
      String sPathInfo = requestData[2];
      if(sPathInfo != null) {
        if(sPathInfo.startsWith("/")) sPathInfo = sPathInfo.substring(1);
        if(sPathInfo.length() > 0) return sPathInfo + "." + methodName;
      }
    }
    return methodName;
  }
  
  public static
  Object executeMethod(Map handlers, RpcAudit audit, String methodName, List params)
    throws Throwable
  {
    String handlerName  = null;
    Object handler      = null;
    Object[] parameters = null;
    long lBefore        = 0;
    try {
      if(methodName == null || methodName.length() == 0) {
        throw new RpcRemoteException(-32600, "invalid method name", null);
      }
      int iSep = methodName.indexOf('.');
      if(iSep <= 0) {
        throw new RpcRemoteException(-32600, "invalid method name", null);
      }
      handlerName = methodName.substring(0,  iSep);
      methodName  = methodName.substring(iSep + 1);
      
      // Find handler...
      handler = handlers.get(handlerName);
      if(handler == null) {
        // [Security]
        throw new RpcRemoteException(-32601, "handler " + WUtil.toHTMLText(handlerName, "") + " not found", null);
      }
      if(handler instanceof String) {
        try {
          Context context = new InitialContext();
          handler = context.lookup((String) handler);
        }
        catch(Throwable th) {
          th.printStackTrace();
          // [Security]
          throw new RpcRemoteException(-32601, "handler " + WUtil.toHTMLText(handlerName, "") + " not available", null);
        }
      }
      
      // Find method...
      Method method = null;
      Method lastMethodSameName = null;
      Method[] methods = handler.getClass().getMethods();
      for(int i = 0; i < methods.length; i++) {
        Method m = methods[i];
        if(!m.getName().equals(methodName)) continue;
        
        lastMethodSameName = m;
        
        parameters = getParameters(m, params);
        if(parameters == null) continue;
        
        method = m;
        break;
      }
      if(method == null) {
        if(lastMethodSameName != null) {
          parameters = getParametersExt(lastMethodSameName, params);
        }
        if(parameters == null) {
          // [Security]
          throw new RpcRemoteException(-32601, "method " + WUtil.toHTMLText(methodName, "") + "(" + getStringParams(params) + ") not found", null);
        }
        method = lastMethodSameName;
      }
      
      // Invoke method...
      if(audit != null) {
        lBefore = System.currentTimeMillis();
        Object oBefore = audit.beforeInvoke(handlerName, methodName, handler, parameters);
        if(oBefore != null) return oBefore;
      }
      Object oResult = method.invoke(handler, parameters);
      if(audit != null) {
        Object oAfter = audit.afterInvoke(handlerName, methodName, handler, parameters, lBefore, oResult, null);
        if(oAfter != null) return oAfter;
      }
      return oResult;
    }
    catch(Throwable t) {
      if(audit != null) {
        try {
          Object oAfter = audit.afterInvoke(handlerName, methodName, handler, parameters, lBefore, null, t);
          if(oAfter != null) return oAfter;
        }
        catch(Throwable th) {
          if(th instanceof InvocationTargetException) {
            th = ((InvocationTargetException) th).getTargetException();
          }
          throw th;
        }
      }
      if(t instanceof InvocationTargetException) {
        t = ((InvocationTargetException) t).getTargetException();
      }
      if(t instanceof RpcRemoteException) throw t;
      if(t instanceof XmlRpcException) throw t;
      String sMessage = t.getMessage();
      if(sMessage == null || sMessage.length() == 0) sMessage = t.toString();
      throw new RpcRemoteException(0, sMessage, RpcUtil.getStackTrace(t));
    }
  }
  
  // Restituisce il tipo con generici SOLO in presenza di bean.
  // 1.5+
  public static
  String getBeanGenericType(Method method, int iIndex)
  {
    Type[] genericTypes = method.getGenericParameterTypes();
    if(genericTypes == null || genericTypes.length <= iIndex) {
      return null;
    }
    Type type = genericTypes[iIndex];
    if(type instanceof ParameterizedType) {
      String sResult = type.toString();
      if(sResult == null) return null;
      int iSepG = sResult.indexOf('<');
      if(iSepG > 0) {
        String g1 = "";
        String g2 = "";
        String generics = sResult.substring(iSepG + 1, sResult.length()-1);
        int iSepT = generics.indexOf(',');
        if(iSepT > 0) {
          g1 = generics.substring(0,iSepT).trim();
          g2 = generics.substring(iSepT+1).trim();
        }
        else {
          g1 = generics;
        }
        if(g1 != null && g1.length() > 0 && !g1.startsWith("java.") && !g1.startsWith("[") && !g1.startsWith("?")) {
          return sResult;
        }
        if(g2 != null && g2.length() > 0 && !g2.startsWith("java.") && !g2.startsWith("[") && !g2.startsWith("?")) {
          return sResult;
        }
      }
    }
    return null;
  }
  
  public static
  Object[] getParameters(Method method, List params)
  {
    Class[] types = method.getParameterTypes();
    if(types.length != params.size()) return null;
    Object[] aoResult = new Object[types.length];
    for(int i = 0; i < types.length; i++) {
      String sTypeName = types[i].getName();
      Object param     = params.get(i);
      
      if(sTypeName.equals("java.lang.String")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof String) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("int")) {
        if(param == null) {
          aoResult[i] = new Integer(0);
        }
        else
        if(param instanceof Integer) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.lang.Number")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Number) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.lang.Integer")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Integer) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("long")) {
        if(param == null) {
          aoResult[i] = new Long(0);
        }
        else
        if(param instanceof Number) {
          aoResult[i] = new Long(((Number) param).longValue());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.lang.Long")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Number) {
          aoResult[i] = new Long(((Number) param).longValue());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("double")) {
        if(param == null) {
          aoResult[i] = new Double(0.0d);
        }
        else
        if(param instanceof Double) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.lang.Double")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Double) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("boolean")) {
        if(param == null) {
          aoResult[i] = Boolean.FALSE;
        }
        else
        if(param instanceof Boolean) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.lang.Boolean")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Boolean) {
          aoResult[i] = param;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Date")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.util.Date) {
          aoResult[i] = param;
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = ((Calendar) param).getTime();
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = calDate.getTime();
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Calendar")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.util.Date) {
          Calendar cal = Calendar.getInstance();
          cal.setTimeInMillis(((java.util.Date) param).getTime());
          aoResult[i]  = cal;
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = param;
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = calDate;
        }
        else {
          return null;
        }
      }
      else // 1.8+
      if(sTypeName.equals("java.time.LocalDate")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.time.LocalDate) {
          aoResult[i] = param;
        }
        else
        if(param instanceof java.util.Date) {
          aoResult[i] = WUtil.toLocalDate(param, null);
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = WUtil.toLocalDate(param, null);
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = WUtil.toLocalDate(calDate, null);
        }
        else {
          return null;
        }
      }
      else // 1.8+
      if(sTypeName.equals("java.time.LocalDateTime")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.time.LocalDateTime) {
          aoResult[i] = param;
        }
        else
        if(param instanceof java.util.Date) {
          aoResult[i] = WUtil.toLocalDateTime(param, null);
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = WUtil.toLocalDateTime(param, null);
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = WUtil.toLocalDateTime(calDate, null);
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Vector")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Vector) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (Vector) param;
          }
        }
        else
        if(param instanceof Collection) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new Vector((Collection) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Stack")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Stack) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (Stack) param;
          }
        }
        else
        if(param instanceof Collection) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            Stack stack = new Stack();
            stack.addAll((Collection) param);
            aoResult[i] = stack;
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.List") || sTypeName.equals("java.util.ArrayList") || sTypeName.equals("java.util.Collection")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof ArrayList) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (ArrayList) param;
          }
        }
        else
        if(param instanceof Collection) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new ArrayList((Collection) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.LinkedList")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof LinkedList) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (LinkedList) param;
          }
        }
        else
        if(param instanceof Collection) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new LinkedList((Collection) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Hashtable")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Hashtable) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (Hashtable) param;
          }
        }
        else
        if(param instanceof Map) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new Hashtable((Map) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Map") || sTypeName.equals("java.util.HashMap")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof HashMap) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (HashMap) param;
          }
        }
        else
        if(param instanceof Map) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new HashMap((Map) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Properties")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Properties) {
          aoResult[i] = (Properties) param;
        }
        else
        if(param instanceof Map) {
          Properties properties = new Properties();
          Iterator iterator = ((Map) param).entrySet().iterator();
          while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object oVal = entry.getValue();
            if(oVal == null) continue;
            properties.setProperty(entry.getKey().toString(), oVal.toString());
          }
          aoResult[i] = properties;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.TreeSet")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof TreeSet) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (TreeSet) param;
          }
        }
        else
        if(param instanceof Collection) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new TreeSet((Collection) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.TreeMap")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof TreeMap) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = (TreeMap) param;
          }
        }
        else
        if(param instanceof Map) {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new TreeMap((Map) param);
          }
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[B")) { // byte[]
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof byte[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          byte[] array = Arrays.toArrayOfByte((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[I")) { // int[]
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof int[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          int[] array = Arrays.toArrayOfInt((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[C")) { // char[]
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof char[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          char[] array = Arrays.toArrayOfChar((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else
        if(param instanceof String) {
          String sParam = (String) param;
          aoResult[i] = sParam.toCharArray();
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[Z")) { // boolean[]
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof boolean[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          boolean[] array = Arrays.toArrayOfBoolean((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[D")) { // double[]
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof double[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          double[] array = Arrays.toArrayOfDouble((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.startsWith("[L") && sTypeName.endsWith(";")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else 
        if(param.getClass().isArray()) {
          aoResult[i] = WUtil.toObject(param, sTypeName);
        }
        else
        if(param instanceof Collection) {
          aoResult[i] = WUtil.toObject(param, sTypeName);
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Set") || sTypeName.equals("java.util.HashSet")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Collection) {
          aoResult[i] = new HashSet((Collection) param);
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.sql.Date")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.util.Date) {
          aoResult[i] = new java.sql.Date(((java.util.Date) param).getTime());
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = new java.sql.Date(((Calendar) param).getTimeInMillis());
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = new java.sql.Date(calDate.getTimeInMillis());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.sql.Timestamp")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.util.Date) {
          aoResult[i] = new java.sql.Timestamp(((java.util.Date) param).getTime());
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = new java.sql.Timestamp(((Calendar) param).getTimeInMillis());
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = new java.sql.Timestamp(calDate.getTimeInMillis());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.sql.Time")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof java.util.Date) {
          aoResult[i] = new java.sql.Time(((java.util.Date) param).getTime());
        }
        else
        if(param instanceof java.util.Calendar) {
          aoResult[i] = new java.sql.Time(((Calendar) param).getTimeInMillis());
        }
        else
        if(param instanceof String) {
          Calendar calDate = WUtil.stringToCalendar((String) param);
          if(calDate == null) return null;
          aoResult[i] = new java.sql.Time(calDate.getTimeInMillis());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.math.BigDecimal")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Number) {
          aoResult[i] = new BigDecimal(param.toString());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.math.BigInteger")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Number) {
          aoResult[i] = new BigInteger(param.toString());
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.lang.Object")) {
        if(param == null) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = param;
        }
      }
      else {
        if(param == null) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Map) {
          Class[] acInterfaces = types[i].getInterfaces();
          boolean boMapable = false;
          if(acInterfaces != null && acInterfaces.length > 0) {
            for(int c = 0; c < acInterfaces.length; c++) {
              Class cInterface = acInterfaces[c];
              if(cInterface.equals(Mapable.class)) {
                try {
                  Mapable mapable = (Mapable) types[i].newInstance();
                  mapable.fromMap((Map) param);
                  aoResult[i] = mapable;
                  boMapable = true;
                }
                catch(Exception ex) {
                  System.err.println("RpcUtil.getParameters(" + method + "," + params + "): " + ex);
                }
              }
            }
          }
          if(!boMapable) {
            aoResult[i] = WUtil.populateBean(types[i], (Map) param);
          }
        }
        else 
        if(param instanceof String) {
          if(types[i].isEnum()) {
            try {
              aoResult[i] = Enum.valueOf(types[i], (String) param);
            }
            catch(Exception ex) {
              System.err.println("RpcUtil.getParameters(" + method + "," + params + "): " + ex);
              return null;
            }
          }
          else {
            return null;
          }
        }
        else {
          return null;
        }
      }
    }
    return aoResult;
  }
  
  public static
  Object[] getParametersExt(Method method, List params)
  {
    Class[] types = method.getParameterTypes();
    if(types.length != params.size()) return null;
    Object[] aoResult = new Object[types.length];
    for(int i = 0; i < types.length; i++) {
      String sTypeName = types[i].getName();
      Object param     = params.get(i);
      
      if(sTypeName.equals("java.lang.String")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toString(param, null);
        }
      }
      else
      if(sTypeName.equals("int")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = new Integer(0);
        }
        else {
          aoResult[i] = WUtil.toInteger(param, new Integer(0));
        }
      }
      else
      if(sTypeName.equals("java.lang.Number")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toNumber(param, null);
        }
      }
      else
      if(sTypeName.equals("java.lang.Integer")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toInteger(param, null);
        }
      }
      else
      if(sTypeName.equals("long")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = new Long(0);
        }
        else {
          aoResult[i] = WUtil.toLongObj(param, new Long(0));
        }
      }
      else
      if(sTypeName.equals("java.lang.Long")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toLongObj(param, null);
        }
      }
      else
      if(sTypeName.equals("double")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = new Double(0.0d);
        }
        else {
          aoResult[i] = WUtil.toDoubleObj(param, new Double(0.0d));
        }
      }
      else
      if(sTypeName.equals("java.lang.Double")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toDoubleObj(param, null);
        }
      }
      else
      if(sTypeName.equals("java.math.BigDecimal")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toBigDecimal(param, null);
        }
      }
      else
      if(sTypeName.equals("java.math.BigInteger")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toBigInteger(param, null);
        }
      }
      else
      if(sTypeName.equals("boolean")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = Boolean.FALSE;
        }
        else {
          aoResult[i] = WUtil.toBooleanObj(param, Boolean.FALSE);
        }
      }
      else
      if(sTypeName.equals("java.lang.Boolean")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toBooleanObj(param, null);
        }
      }
      else
      if(sTypeName.equals("java.util.Date")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toDate(param, null);
        }
      }
      else
      if(sTypeName.equals("java.util.Calendar")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toCalendar(param, null);
        }
      }
      else // 1.8+
      if(sTypeName.equals("java.time.LocalDate")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toLocalDate(param, null);
        }
      }
      else // 1.8+
      if(sTypeName.equals("java.time.LocalDateTime")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toLocalDateTime(param, null);
        }
      }
      else
      if(sTypeName.equals("java.util.Vector")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = WUtil.toVector(param, null);
          }
        }
      }
      else
      if(sTypeName.equals("java.util.Stack")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            Stack stack = new Stack();
            stack.addAll(WUtil.toVector(param, true));
            aoResult[i] = stack;
          }
        }
      }
      else
      if(sTypeName.equals("java.util.List") || sTypeName.equals("java.util.ArrayList") || sTypeName.equals("java.util.Collection")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = WUtil.toList(param, null);
          }
        }
      }
      else
      if(sTypeName.equals("java.util.LinkedList")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new LinkedList(WUtil.toList(param, true));
          }
        }
      }
      else
      if(sTypeName.equals("java.util.Hashtable")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = WUtil.toHashtable(param, false);
          }
        }
      }
      else
      if(sTypeName.equals("java.util.Map") || sTypeName.equals("java.util.HashMap")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = WUtil.toMap(param, false);
          }
        }
      }
      else
      if(sTypeName.equals("java.util.Properties")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Map) {
          Properties properties = new Properties();
          Iterator iterator = ((Map) param).entrySet().iterator();
          while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object oVal = entry.getValue();
            if(oVal == null) continue;
            properties.setProperty(entry.getKey().toString(), oVal.toString());
          }
          aoResult[i] = properties;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.TreeSet")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new TreeSet(WUtil.toList(param, true));
          }
        }
      }
      else
      if(sTypeName.equals("java.util.TreeMap")) {
        String sBeanGenericType = getBeanGenericType(method, i);
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          if(sBeanGenericType != null) {
            aoResult[i] = WUtil.toObject(param, sBeanGenericType);
          }
          else {
            aoResult[i] = new TreeMap(WUtil.toMap(param, true));
          }
        }
      }
      else
      if(sTypeName.equals("[B")) { // byte[]
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof byte[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          byte[] array = Arrays.toArrayOfByte((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[I")) { // int[]
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof int[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          int[] array = Arrays.toArrayOfInt((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[C")) { // char[]
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof char[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          char[] array = Arrays.toArrayOfChar((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else
        if(param instanceof String) {
          String sParam = (String) param;
          aoResult[i] = sParam.toCharArray();
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[Z")) { // boolean[]
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof boolean[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          boolean[] array = Arrays.toArrayOfBoolean((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("[D")) { // double[]
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof double[]) {
          aoResult[i] = param;
        }
        else
        if(param instanceof Collection) {
          double[] array = Arrays.toArrayOfDouble((Collection) param);
          if(array == null) return null;
          aoResult[i] = array;
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.startsWith("[L") && sTypeName.endsWith(";")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else 
        if(param.getClass().isArray()) {
          aoResult[i] = WUtil.toObject(param, sTypeName);
        }
        else
        if(param instanceof Collection) {
          aoResult[i] = WUtil.toObject(param, sTypeName);
        }
        else {
          return null;
        }
      }
      else
      if(sTypeName.equals("java.util.Set") || sTypeName.equals("java.util.HashSet")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toSet(param, false);
        }
      }
      else
      if(sTypeName.equals("java.sql.Date")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toSQLDate(param, null);
        }
      }
      else
      if(sTypeName.equals("java.sql.Time")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toSQLTime(param, null);
        }
      }
      else
      if(sTypeName.equals("java.sql.Timestamp")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = WUtil.toSQLTimestamp(param, null);
        }
      }
      else
      if(sTypeName.equals("java.lang.Object")) {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else {
          aoResult[i] = param;
        }
      }
      else {
        if(param == null || param.equals("null")) {
          aoResult[i] = null;
        }
        else
        if(param instanceof Map) {
          Class[] acInterfaces = types[i].getInterfaces();
          boolean boMapable = false;
          if(acInterfaces != null && acInterfaces.length > 0) {
            for(int c = 0; c < acInterfaces.length; c++) {
              Class cInterface = acInterfaces[c];
              if(cInterface.equals(Mapable.class)) {
                try {
                  Mapable mapable = (Mapable) types[i].newInstance();
                  mapable.fromMap((Map) param);
                  aoResult[i] = mapable;
                  boMapable = true;
                }
                catch(Exception ex) {
                  System.err.println("RpcUtil.getParametersExt(" + method + "," + params + "): " + ex);
                }
              }
            }
          }
          if(!boMapable) {
            aoResult[i] = WUtil.populateBean(types[i], (Map) param);
          }
        }
        else 
        if(param instanceof String) {
          if(types[i].isEnum()) {
            try {
              aoResult[i] = Enum.valueOf(types[i], (String) param);
            }
            catch(Exception ex) {
              System.err.println("RpcUtil.getParametersExt(" + method + "," + params + "): " + ex);
              return null;
            }
          }
          else {
            return null;
          }
        }
        else {
          return null;
        }
      }
    }
    return aoResult;
  }
  
  public static
  String getStackTrace(Throwable t)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos);
    t.printStackTrace(pw);
    pw.close();
    return getMethodAndRow(baos.toByteArray(), 1);
  }
  
  public static
  String getMethodAndRow(byte[] aBytes, int iDepth)
  {
    StringBuffer sb = new StringBuffer();
    int iCountCRLF = 0;
    boolean boTraceOn = false;
    for(int i = 0; i < aBytes.length; i++) {
      byte b = aBytes[i];
      if(b == '\n') iCountCRLF++;
      if(iCountCRLF == iDepth) {
        if(b == ' ') {
          boTraceOn = true;
          continue;
        }
        else
        if(b == ':') {
          boTraceOn = true;
          continue;
        }
        else
        if(b == '(') {
          boTraceOn = false;
          sb.append(':');
          continue;
        }
        else
        if(b == ')') break;
        if(boTraceOn) sb.append((char) b);
      }
    }
    return sb.toString();
  }
  
  protected static
  String getStringParams(List list)
  {
    if(list == null || list.size() == 0) return "";
    String sResult = "";
    for(int i = 0; i < list.size(); i++) {
      Object oValue = list.get(i);
      if(oValue == null) {
        sResult += ",null";
      }
      else {
        sResult += "," + oValue.getClass().getName();
      }
    }
    if(sResult.length() > 0) sResult = sResult.substring(1);
    return sResult;
  }
}
