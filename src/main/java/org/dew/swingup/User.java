package org.dew.swingup;

import java.text.DateFormat;
import java.util.*;

/**
 * Bean che raccoglie le informazioni dell'utente.
 *
 * @version 1.0
 */
public
class User
{
  private int iIdSession;
  private String userName;
  private String password;
  private int passwordHashCode;
  private String role;
  private List groups;
  private List structures;
  private List subStructures;
  private List grants;
  private String lastName;
  private String firstName;
  private String email;
  private Date datePassword;
  private Date dateLastAccess;
  private String currentIdClient;
  private String userClass;
  private Integer personId;
  private String taxCode;
  private String title;
  private String reference;
  private String sex;
  private String token;
  private boolean nativeAuthentication = true;
  private Map resources;
  private Map info;
  private List listCDS;
  private String certSubjectDN;
  private String certIssuerDN;
  
  /**
   * Costruttore.
   */
  public User()
  {
  }
  
  public void setIdSession(int iIdSession) {
    this.iIdSession = iIdSession;
  }
  public int getIdSession() {
    return iIdSession;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getUserName() {
    return userName;
  }
  public void setPassword(String password) {
    this.password = password;
    if(password != null && password.length() > 0) {
      this.passwordHashCode = password.hashCode();
    }
  }
  public String getPassword() {
    return password;
  }
  public void setPasswordHashCode(int passwordHashCode) {
    this.passwordHashCode = passwordHashCode;
  }
  public int getPasswordHashCode() {
    return passwordHashCode;
  }
  public void setRole(String role) {
    this.role = role;
  }
  public String getRole() {
    return role;
  }
  public void setGroups(List groups) {
    this.groups = groups;
  }
  public List getGroups() {
    return groups;
  }
  public void setGrants(List grants) {
    this.grants = grants;
  }
  public List getGrants() {
    return grants;
  }
  public void setStructures(List structures) {
    this.structures = structures;
  }
  public List getStructures() {
    return structures;
  }
  public void setSubStructures(List substructures) {
    this.subStructures = substructures;
  }
  public List getSubStructures() {
    return subStructures;
  }
  public void setResources(Map resources) {
    this.resources = resources;
  }
  public Map getResources() {
    return resources;
  }
  public void setInfo(Map info) {
    this.info = info;
  }
  public Map getInfo() {
    return info;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getEmail() {
    return email;
  }
  public void setDatePassword(Date datePassword) {
    this.datePassword = datePassword;
  }
  public Date getDatePassword() {
    return datePassword;
  }
  public void setDateLastAccess(Date dateLastAccess) {
    this.dateLastAccess = dateLastAccess;
  }
  public Date getDateLastAccess() {
    return dateLastAccess;
  }
  public void setCurrentIdClient(String currentIdClient) {
    this.currentIdClient = currentIdClient;
  }
  public String getCurrentIdClient() {
    return currentIdClient;
  }
  public void setUserClass(String userClass) {
    this.userClass = userClass;
  }
  public String getUserClass() {
    return userClass;
  }
  public void setPersonId(Integer personId) {
    this.personId = personId;
  }
  public Integer getPersonId() {
    return personId;
  }
  public void setReference(String reference) {
    this.reference = reference;
  }
  public String getReference() {
    return reference;
  }
  public void setSex(String sex) {
    this.sex = sex;
  }
  public String getSex() {
    return sex;
  }
  public void setTaxCode(String taxCode) {
    this.taxCode = taxCode;
  }
  public String getTaxCode() {
    return taxCode;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getTitle() {
    return title;
  }
  public void setToken(String token) {
    this.token = token;
  }
  public String getToken() {
    return token;
  }
  public void setNativeAuthentication(boolean nativeAuthentication) {
    this.nativeAuthentication = nativeAuthentication;
  }
  public boolean isNativeAuthentication() {
    return nativeAuthentication;
  }
  public void setListCDS(List listCDS) {
    this.listCDS = listCDS;
  }
  public List getListCDS() {
    return listCDS;
  }
  public void setCertSubjectDN(String certSubjectDN) {
    this.certSubjectDN = certSubjectDN;
  }
  public String getCertSubjectDN() {
    return certSubjectDN;
  }
  public void setCertIssuerDN(String certIssuerDN) {
    this.certIssuerDN = certIssuerDN;
  }
  public String getCertIssuerDN() {
    return certIssuerDN;
  }
  
  /**
   * Ottiene una mappa di risorse le cui chiavi sono gerarchicamente
   * appartenenti ad una risorsa individuata dal parametro specificato.
   * In altre parole vengono selezionate tutte le chiavi che iniziano per:
   * sFather + "."
   * Si noti che le chiavi della mappa restituita sono ottenute escludendo
   * il prefisso.
   *
   * @param sFather String
   * @return Map
   */
  public
  Map getResourcesByFather(String sFather)
  {
    Map mapResult = new HashMap();
    
    if(resources == null) return mapResult;
    
    String sPrefix = sFather + ".";
    
    Iterator oItKeys = resources.keySet().iterator();
    while(oItKeys.hasNext()) {
      String sKey = oItKeys.next().toString();
      if(sKey.startsWith(sPrefix)) {
        Object oValue = resources.get(sKey);
        if(sPrefix.length() < sKey.length()) {
          String sNewKey = sKey.substring(sPrefix.length());
          mapResult.put(sNewKey, oValue);
        }
      }
    }
    
    return mapResult;
  }
  
  /**
   * Ottiene una lista di valori dalle risorse considerando le chiavi
   * che hanno il prefisso specificato.
   *
   * @param sPrefix String
   * @return List
   */
  public
  List getListResourceValuesByPrefix(String sPrefix)
  {
    List listResult = new ArrayList();
    
    if(resources == null) return listResult;
    
    Iterator oItKeys = resources.keySet().iterator();
    while(oItKeys.hasNext()) {
      String sKey = oItKeys.next().toString();
      if(sKey.startsWith(sPrefix)) {
        Object oValue = resources.get(sKey);
        listResult.add(oValue);
      }
    }
    
    return listResult;
  }
  
  public
  String toString()
  {
    return userName;
  }
  
  public
  int hashCode()
  {
    if(userName == null) return 0;
    return userName.hashCode();
  }
  
  public
  boolean equals(Object anObject)
  {
    if(this == anObject) {
      return true;
    }
    else
    if(anObject instanceof User) {
      String sUserName = ((User) anObject).getUserName();
      if(sUserName != null && sUserName.equals(userName)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Restituisce la rappresentazione HTML dell'utente.
   *
   * @return String
   */
  public
  String toHTML()
  {
    String sResult = "<b>UserName:</b> " + userName + "<br>";
    // Informazioni credenziale
    if(certSubjectDN != null) {
      sResult += "<b>Subject DN:</b> " + certSubjectDN + "<br>";
    }
    if(certIssuerDN != null) {
      sResult += "<b>Issuer DN:</b> " + certIssuerDN + "<br>";
    }
    String sUserClass = userClass;
    if(sUserClass != null && sUserClass.trim().length() > 0) {
      sResult += "<b>Classe:</b> " + sUserClass + "<br>";
    }
    if(role != null) {
      sResult += "<b>Ruolo:</b> " + role + "<br>";
    }
    if(groups != null) {
      sResult += "<b>Gruppi:</b> " + groups + "<br>";
    }
    if(grants != null) {
      sResult += "<b>Abilitazioni:</b> " + grants + "<br>";
    }
    if(structures != null) {
      sResult += "<b>Strutture:</b> " + structures + "<br>";
    }
    if(subStructures != null) {
      sResult += "<b>Sottostrutture:</b> " + subStructures + "<br>";
    }
    // Informazioni utente
    if(personId != null) {
      sResult += "<b>Id Utente:</b> " + personId + "<br>";
    }
    if(lastName != null) {
      sResult += "<b>Cognome:</b> " + lastName + "<br>";
    }
    if(firstName != null) {
      sResult += "<b>Nome:</b> " + firstName + "<br>";
    }
    if(sex != null) {
      sResult += "<b>Sesso:</b> " + sex + "<br>";
    }
    if(taxCode != null) {
      sResult += "<b>Codice Fiscale:</b> " + taxCode + "<br>";
    }
    if(title != null) {
      sResult += "<b>Titolo:</b> " + title + "<br>";
    }
    if(reference != null) {
      sResult += "<b>Riferimento:</b> " + reference + "<br>";
    }
    if(email != null) {
      sResult += "<b>Email:</b> " + email + "<br>";
    }
    sResult += "<b>Autenticazione nativa:</b> " + nativeAuthentication + "<br>";
    
    if(datePassword != null) {
      DateFormat dfDate = ResourcesMgr.getDefaultDateFormat();
      sResult += "<b>Data password:</b> " + dfDate.format(datePassword) + "<br>";
    }
    if(dateLastAccess != null) {
      DateFormat dfDateTime = DateFormat.getInstance();
      sResult += "<b>Ultimo accesso:</b> " + dfDateTime.format(dateLastAccess) + "<br>";
    }
    // Risorse
    if(resources != null) {
      sResult += "<b>Risorse:</b><br>";
      Object[] aoKeys = resources.keySet().toArray();
      Arrays.sort(aoKeys);
      for(int i = 0; i < aoKeys.length; i++) {
        Object oKey   = aoKeys[i];
        Object oValue = resources.get(oKey);
        sResult += "&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"-1\">" + oKey + " = ";
        sResult += oValue + "</font><br>";
      }
    }
    // Info
    if(info != null) {
      sResult += "<b>Informazioni:</b><br>";
      Object[] aoKeys = info.keySet().toArray();
      Arrays.sort(aoKeys);
      for(int i = 0; i < aoKeys.length; i++) {
        Object oKey   = aoKeys[i];
        Object oValue = info.get(oKey);
        sResult += "&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"-1\">" + oKey + " = ";
        sResult += oValue + "</font><br>";
      }
    }
    // CDS
    if(listCDS != null && listCDS.size() > 0) {
      sResult += "<b>Comunicazioni di servizio:</b> " + listCDS.size() + "<br>";
    }
    return sResult;
  }
}
