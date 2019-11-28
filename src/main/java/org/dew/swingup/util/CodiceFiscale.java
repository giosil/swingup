package org.dew.swingup.util;

import java.util.*;

/**
 * Classe di utilita' per il calcolo del codice fiscale.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class CodiceFiscale
{
  static final String mesi       = "ABCDEHLMPRST";
  static final String vocali     = "AEIOU";
  static final String consonanti = "BCDFGHJKLMNPQRSTVWXYZ";
  static final String alfabeto   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private String nome,cognome,comune;
  private Date oDataNascita;
  private Calendar dataNascita;
  private char sesso;
  
  static int[][] matricecod = new int[91][2];
  static {
    matricecod [0][1] = 1;
    matricecod [0][0] = 0;
    matricecod [1][1] = 0;
    matricecod [1][0] = 1;
    matricecod [2][1] = 5;
    matricecod [2][0] = 2;
    matricecod [3][1] = 7;
    matricecod [3][0] = 3;
    matricecod [4][1] = 9;
    matricecod [4][0] = 4;
    matricecod [5][1] = 13;
    matricecod [5][0] = 5;
    matricecod [6][1] = 15;
    matricecod [6][0] = 6;
    matricecod [7][1] = 17;
    matricecod [7][0] = 7;
    matricecod [8][1] = 19;
    matricecod [8][0] = 8;
    matricecod [9][1] = 21;
    matricecod [9][0] = 9;
    matricecod [10][1] = 1;
    matricecod [10][0] = 0;
    matricecod [11][1] = 0;
    matricecod [11][0] = 1;
    matricecod [12][1] = 5;
    matricecod [12][0] = 2;
    matricecod [13][1] = 7;
    matricecod [13][0] = 3;
    matricecod [14][1] = 9;
    matricecod [14][0] = 4;
    matricecod [15][1] = 13;
    matricecod [15][0] = 5;
    matricecod [16][1] = 15;
    matricecod [16][0] = 6;
    matricecod [17][1] = 17;
    matricecod [17][0] = 7;
    matricecod [18][1] = 19;
    matricecod [18][0] = 8;
    matricecod [19][1] = 21;
    matricecod [19][0] = 9;
    matricecod [20][1] = 2;
    matricecod [20][0] = 10;
    matricecod [21][1] = 4;
    matricecod [21][0] = 11;
    matricecod [22][1] = 18;
    matricecod [22][0] = 12;
    matricecod [23][1] = 20;
    matricecod [23][0] = 13;
    matricecod [24][1] = 11;
    matricecod [24][0] = 14;
    matricecod [25][1] = 3;
    matricecod [25][0] = 15;
    matricecod [26][1] = 6;
    matricecod [26][0] = 16;
    matricecod [27][1] = 8;
    matricecod [27][0] = 17;
    matricecod [28][1] = 12;
    matricecod [28][0] = 18;
    matricecod [29][1] = 14;
    matricecod [29][0] = 19;
    matricecod [30][1] = 16;
    matricecod [30][0] = 20;
    matricecod [31][1] = 10;
    matricecod [31][0] = 21;
    matricecod [32][1] = 22;
    matricecod [32][0] = 22;
    matricecod [33][1] = 25;
    matricecod [33][0] = 23;
    matricecod [34][1] = 24;
    matricecod [34][0] = 24;
    matricecod [35][1] = 23;
    matricecod [35][0] = 25;
  }
  
  public
  CodiceFiscale()
  {
  }
  
  public void setCognome(String s) {
    this.cognome = s.toUpperCase();
  }
  
  public void setNome(String s) {
    this.nome = s.toUpperCase();
  }
  
  public void setDataNascita(Date oDataNascita) {
    this.oDataNascita = oDataNascita;
  }
  
  public void setSesso(String s) {
    this.sesso = s.toUpperCase().charAt(0);
  }
  
  public void setComune(String s) {
    this.comune = s;
  }
  
  private
  String calcolaCognome(String cogn)
  {
    int i=0;
    String stringa="";
    //trova consonanti
    while((stringa.length() < 3) &&(i+1 <= cogn.length())) {
      if(consonanti.indexOf(cogn.charAt(i)) > -1) {
        stringa += cogn.charAt(i);
      }
      i++;
    }
    i = 0;
    //se non bastano prende vocali
    while((stringa.length() < 3) &&(i+1 <= cogn.length())) {
      if(vocali.indexOf(cogn.charAt(i)) > -1) {
        stringa += cogn.charAt(i);
      }
      i++;
    }
    //se non bastano aggiungo le x
    if(stringa.length() < 3) {
      for(i = stringa.length();i<3;i++) {
        stringa += "X";
      }
    }
    return stringa;
  }
  
  private
  String calcolaNome(String nom)
  {
    int i=0;
    String stringa="", cons="";
    //trova consonanti
    while((cons.length() < 4) &&(i+1 <= nom.length())) {
      if(consonanti.indexOf(nom.charAt(i)) > -1) {
        cons += nom.charAt(i);
      }
      i++;
    }
    //se sono + di 3 prende 1o 3o 4o
    if(cons.length()>3) {
      stringa=cons.substring(0,1)+cons.substring(2,4);
      return stringa;
    }
    else {
      stringa=cons;
    }
    i = 0;
    //se non bastano prende vocali
    while((stringa.length() < 3) &&(i+1 <= nom.length())) {
      if(vocali.indexOf(nom.charAt(i)) > -1) {
        stringa += nom.charAt(i);
      }
      i++;
    }
    //se non bastano aggiungo le x
    if(stringa.length() < 3) {
      for(i = stringa.length();i<3;i++) {
        stringa += "X";
      }
    }
    return stringa;
  }
  
  String noAccentate(String s)
  {
    final String ACCENTATE="\300\310\311\314\322\331\340\350\351\354\362\371";
    final String NOACCENTO="AEEIOUAEEIOU";
    int i=0;
    //scorre la stringa originale
    while(i<s.length()) {
      int p= ACCENTATE.indexOf(s.charAt(i));
      //se ha trovato una lettera accentata
      if(p>-1) {
        //sostituisce con la relativa non accentata
        s=s.substring(0,i)+NOACCENTO.charAt(p)+s.substring(i+1);
      }
      i++;
    }
    return s;
  }
  
  public
  String getCodiceFiscale() {
    String codice;
    
    try {
      int anno = 0, mese = 0, giorno = 0, codcontrollo = 0;
      
      dataNascita = new GregorianCalendar();
      dataNascita.setTime(oDataNascita);
      String a = Integer.toString(dataNascita.get(Calendar.YEAR));
      a = a.substring(a.length() - 2, a.length());
      anno = Integer.parseInt(a);
      mese = dataNascita.get(Calendar.MONTH);
      giorno = dataNascita.get(Calendar.DATE);
      
      codice = calcolaCognome(noAccentate(cognome.trim())) +
        calcolaNome(noAccentate(nome.trim()));
      if(sesso == 'F') {
        giorno = giorno + 40;
      }
      codice += ((anno < 10) ? "0" : "") + Integer.toString(anno) +
        mesi.charAt(mese) +((giorno < 10) ? "0" : "") +
        Integer.toString(giorno);
      codice += comune;
      for(int i = 0; i < 15; i++) {
        codcontrollo +=
          matricecod[Character.getNumericValue(codice.charAt(i))][(i + 1) %
        2];
      }
      codice += alfabeto.charAt(codcontrollo % 26);
    }
    catch(Exception ex) {
      codice = "XXXXXX00A00A000X";
    }
    
    return codice;
  }
  
  /**
   * Restituisce il sesso dal codice fiscale.
   *
   * @param sCodiceFiscale String
   * @return String
   */
  public static
  String getSesso(String sCodiceFiscale)
  {
    if(sCodiceFiscale == null ||
      sCodiceFiscale.length() < 15) {
      return null;
    }
    
    int iGiorno = Integer.parseInt(sCodiceFiscale.substring(9, 11));
    
    if(iGiorno > 40) {
      return "F";
    }
    
    return "M";
  }
  
  /**
   * Calcola la data di nascita dal codice fiscale.
   * Ad esempio getDataNascita("xxxxxx87A03xxxx", 1900, 1800, 90)
   * ritorna 03 / 01 / 1987
   * invece getDataNascita("xxxxxx91A03xxxx", 1900, 1800, 90)
   * ritorna 03 / 01 / 1891.
   *
   * @param sCodiceFiscale Codice fiscale
   * @param iSecoloRiferimento Secolo di riferimento
   * @param iSecoloSoglia  Secolo considerato se l'anno supera la soglia
   * @param iSoglia Soglia
   * @return Date
   */
  public static
  Date getDataNascita(String sCodiceFiscale,
    int iSecoloRiferimento,
    int iSecoloSoglia, int iSoglia)
  {
    if(sCodiceFiscale == null ||
      sCodiceFiscale.length() < 15) {
      return null;
    }
    
    int iAnno = Integer.parseInt(sCodiceFiscale.substring(6, 8));
    if(iAnno >= iSoglia) {
      iAnno = iSecoloSoglia + iAnno;
    }
    else {
      iAnno = iSecoloRiferimento + iAnno;
    }
    
    String sMese = sCodiceFiscale.substring(8, 9);
    int iMese = mesi.indexOf(sMese);
    if(iMese < 0) return null;
    
    int iGiorno = Integer.parseInt(sCodiceFiscale.substring(9, 11));
    if(iGiorno > 40) {
      iGiorno = iGiorno - 40;
    }
    if(iGiorno > 31) return null;
    
    Calendar cal = new GregorianCalendar(iAnno, iMese, iGiorno);
    return cal.getTime();
  }
  
  /**
   * Restituisce il comune dal codice fiscale.
   *
   * @param sCodiceFiscale Codice fiscale
   * @return String
   */
  public static
  String getComune(String sCodiceFiscale)
  {
    if(sCodiceFiscale == null ||
      sCodiceFiscale.length() < 15) {
      return null;
    }
    
    return sCodiceFiscale.substring(11, 15);
  }
  
  public static
  char getCheckDigit(String sCodiceFiscale)
  {
    if(sCodiceFiscale == null || sCodiceFiscale.length() < 15) {
      return 'A';
    }
    
    int codcontrollo = 0;
    for(int i = 0; i < 15; i++) {
      codcontrollo +=
        matricecod[Character.getNumericValue(sCodiceFiscale.charAt(i))][(i+1)%2];
    }
    
    return alfabeto.charAt(codcontrollo % 26);
  }
  
  public static
  boolean checkCheckDigit(String sCodiceFiscale)
  {
    if(sCodiceFiscale == null || sCodiceFiscale.length() < 16) {
      return false;
    }
    
    int codcontrollo = 0;
    for(int i = 0; i < 15; i++) {
      codcontrollo +=
        matricecod[Character.getNumericValue(sCodiceFiscale.charAt(i))][(i+1)%2];
    }
    
    return sCodiceFiscale.charAt(15) == alfabeto.charAt(codcontrollo % 26);
  }
  
  public static
  boolean check(String sCodiceFiscale, String sCognome, String sNome)
  {
    if(sCodiceFiscale == null ||
      sCodiceFiscale.length() < 16) {
      return false;
    }
    
    try {
      Date dataNascita = getDataNascita(sCodiceFiscale, 1900, 1900, 0);
      if(dataNascita == null) return false;
    }
    catch(Exception ex) {
      return false;
    }
    
    String sCogNom = sCodiceFiscale.substring(0, 6);
    
    if(sCognome != null && sNome != null &&
      sCognome.length() > 0 && sNome.length() > 0) {
      CodiceFiscale cf = new CodiceFiscale();
      cf.setCognome(sCognome);
      cf.setNome(sNome);
      cf.setDataNascita(new Date());
      cf.setSesso("M");
      cf.setComune("A000");
      String sCodFisc_Calc = cf.getCodiceFiscale();
      String sCogNom_Calc = sCodFisc_Calc.substring(0, 6);
      
      if(!sCogNom.equals(sCogNom_Calc)) {
        return false;
      }
    }
    
    return true;
  }
}
