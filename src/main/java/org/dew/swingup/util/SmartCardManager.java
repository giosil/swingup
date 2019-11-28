package org.dew.swingup.util;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.ResourcesMgr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Classe di utilita' per la gestione della smart card. Essa si basa su una libreria proprietaria, tuttavia e' possibile fruire del
 * servizio di firma tramite il provider sun.security.pkcs11.SunPKCS11: <br />
 * <br />
 * <pre>
 * import java.security.Key;
 * import java.security.KeyStore;
 * import java.security.PrivateKey;
 * import java.security.Provider;
 * import java.security.SecureRandom;
 * import java.security.Security;
 * import java.security.Signature;
 *
 * import java.security.cert.Certificate;
 * import java.security.cert.CertStore;
 * import java.security.cert.CollectionCertStoreParameters;
 * import java.security.cert.X509Certificate
 *
 * // Bouncycastle PKCS7
 * import org.bouncycastle.cms.CMSProcessable;
 * import org.bouncycastle.cms.CMSProcessableByteArray;
 * import org.bouncycastle.cms.CMSSignedData;
 * import org.bouncycastle.cms.CMSSignedDataGenerator;
 *
 * // Sun PKCS7
 * import java.math.BigInteger;
 * import sun.security.pkcs.ContentInfo;
 * import sun.security.pkcs.PKCS7;
 * import sun.security.pkcs.SignerInfo;
 * import sun.security.util.DerOutputStream;
 * import sun.security.util.DerValue;
 * import sun.security.x509.AlgorithmId;
 * import sun.security.x509.X500Name;
 *
 * import javax.net.ssl.HttpsURLConnection;
 * import javax.net.ssl.KeyManagerFactory;
 * import javax.net.ssl.SSLContext;
 * import javax.net.ssl.SSLSocketFactory;
 * import javax.net.ssl.TrustManagerFactory;
 *
 * public void examples() {
 *
 * SmartCardManager smartCardManager = new SmartCardManager();
 *
 * Provider provider = new sun.security.pkcs11.SunPKCS11(smartCardManager.getProviderConfig(false));
 * Security.addProvider(provider); // add a Provider with name "SunPKCS11-" + SmartCardManager.sPROVIDER_NAME
 *
 * KeyStore keystore = KeyStore.getInstance("PKCS11");
 * keystore.load(null, smartCardManager.getLastPinTypedCharArray(true));
 *
 * String sFirstAlias  = null; // Typically an authentication certificate.
 * String sSecondAlias = null; // Typically is a digital signature certificate.
 * Enumeration aliases = keystore.aliases();
 * if(aliases.hasMoreElements()) {
 *     sFirstAlias  = (String) aliases.nextElement();
 * }
 * if(aliases.hasMoreElements()) {
 *     sSecondAlias = (String) aliases.nextElement();
 * }
 * if(sSecondAlias == null) sSecondAlias = sFirstAlias;
 *
 * Certificate certificate = keystore.getCertificate(sFirstAlias);
 *
 * Key privateKey = keystore.getKey(sFirstAlias, smartCardManager.getLastPinTypedCharArray(true));
 *
 * byte[] unsignedData = "test".getBytes();
 *
 * // Simple Signature
 * Signature signature = Signature.getInstance("SHA256withRSA");
 * signature.initSign((PrivateKey) privateKey);
 * signature.update(unsignedData);
 * byte[] abSignature = signature.sign();
 *
 * // PKCS#7 (p7m) Signature with BouncyCastle (recommended)
 * CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Collections.singletonList((X509Certificate) certificate)));
 *
 * CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
 * generator.addSigner((PrivateKey) privateKey, (X509Certificate) certificate, CMSSignedDataGenerator.DIGEST_SHA256);
 * generator.addCertificatesAndCRLs(certStore);
 * CMSProcessable content = new CMSProcessableByteArray(unsignedData);
 * CMSSignedData signedData = generator.generate(content, true, "SunPKCS11-" + SmartCardManager.sPROVIDER_NAME);
 * byte[] abPKCSSignature = signedData.getEncoded();
 *
 * // PKCS#7 Signature with Sun (NOT recommended)
 * X500Name   subjectX500Name    = X500Name.asX500Name(((X509Certificate) certificate).getSubjectX500Principal());
 * BigInteger serialNumber       = x509Certificate.getSerialNumber();
 * AlgorithmId digestAlgorithmId = new AlgorithmId(AlgorithmId.SHA256_oid);
 * AlgorithmId signAlgorithmId   = new AlgorithmId(AlgorithmId.RSAEncryption_oid);
 *
 * SignerInfo  signerInfo  = new SignerInfo(subjectX500Name, serialNumber, digestAlgorithmId, signAlgorithmId, abSignature);
 * ContentInfo contentInfo = new ContentInfo(ContentInfo.DATA_OID, new DerValue(DerValue.tag_OctetString, unsignedData));
 *
 * PKCS7 pkcs7 = new PKCS7(new AlgorithmId[] {digestAlgorithmId, signAlgorithmId}, contentInfo, new X509Certificate[] {x509Certificate}, new SignerInfo[] {signerInfo});
 * ByteArrayOutputStream baos = new DerOutputStream();
 * pkcs7.encodeSignedData(baos);
 * byte[] abPKCS7Signature = baos.toByteArray();
 *
 * // SSL mutual authentication
 * KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
 * keyManagerFactory.init(keystore, smartCardManager.getLastPinTypedCharArray(true));
 *
 * // The second parameter of sslContext.init is TrustManager[].
 * // For example: TrustManagerFactory.getInstance("SunX509").getTrustManagers()
 * SSLContext sslContext = SSLContext.getInstance("TLS");
 * sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
 *
 * SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
 *
 * HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
 *
 * // Configuration BindingProvider of Web Services Port
 * BindingProvider bindingProvider = ((BindingProvider) service_port);
 * Map<String, Object> requestContext = bindingProvider.getRequestContext();
 * requestContext.put(JAXWSProperties.SSL_SOCKET_FACTORY, sslSocketFactory);
 *
 * }
 * </pre>
 *
 * @version 1.0
 */
public
class SmartCardManager
{
  protected String sLibraryDirectory;
  protected String[] asLibraries = {"pkcs11wrapper.dll"};
  protected int iDATE_LAST_MODIFIED_DLL = 20130617;
  
  protected static String sLastPinTyped = null;
  protected static String sCryptokiPath = null;
  protected static int iLastSlotUsed = 0;
  protected static byte[] dummyToken = "X".getBytes();
  
  public static String sASK_PIN_SMART_CARD = "Digitare il PIN della Smart Card inserita";
  public static String sPROVIDER_NAME      = "smartcard";
  
  public
  SmartCardManager()
    throws Exception
  {
    initUSBReader();
  }
  
  public
  String getCryptokiPath(boolean check)
  {
    return getCryptokiPath(check, null);
  }
  
  public
  String getCryptokiPath(boolean check, String sThePin)
  {
    if(sCryptokiPath == null || sCryptokiPath.length() == 0) {
      String sSmartCardDll = ResourcesMgr.dat.getProperty("smartcard.dll");
      if(sSmartCardDll == null || sSmartCardDll.length() == 0) {
        sSmartCardDll = ResourcesMgr.config.getProperty("swingup.smartcard.dll");
      }
      if(sSmartCardDll != null && sSmartCardDll.length() > 1) {
        sCryptokiPath = sSmartCardDll;
        return sCryptokiPath;
      }
      if(check) {
        try {
          signWithPKCS11(dummyToken, false, sThePin);
        }
        catch(Throwable th) {
          th.printStackTrace();
        }
      }
    }
    if(sCryptokiPath == null || sCryptokiPath.length() < 2) {
      return "C:\\Windows\\System32\\bit4opki.dll";
    }
    return sCryptokiPath;
  }
  
  public
  String getLastPinTyped()
  {
    return sLastPinTyped;
  }
  
  public
  void setLastPinTyped(String sPin)
  {
    setLastPinTyped(sPin, false);
  }
  
  public
  void setLastPinTyped(String sPin, boolean boReplace)
  {
    if(boReplace) {
      sLastPinTyped = sPin;
    }
    else
    if(sLastPinTyped == null || sLastPinTyped.length() == 0) {
      sLastPinTyped = sPin;
    }
  }
  
  public
  String getLastPinTyped(boolean askPinIfNotSetted)
  {
    if(sLastPinTyped == null || sLastPinTyped.length() == 0) {
      if(askPinIfNotSetted) {
        return GUIMessage.getInput(sASK_PIN_SMART_CARD);
      }
    }
    return sLastPinTyped;
  }
  
  public
  char[] getLastPinTypedCharArray()
  {
    if(sLastPinTyped == null) return new char[0];
    return sLastPinTyped.toCharArray();
  }
  
  public
  char[] getLastPinTypedCharArray(boolean askPinIfNotSetted)
  {
    if(sLastPinTyped == null || sLastPinTyped.length() == 0) {
      if(askPinIfNotSetted) {
        String sPin = GUIMessage.getInput(sASK_PIN_SMART_CARD);
        if(sPin == null || sPin.length() == 0) {
          return new char[0];
        }
        return sPin.toCharArray();
      }
      else {
        return new char[0];
      }
    }
    return sLastPinTyped.toCharArray();
  }
  
  public
  InputStream getProviderConfig(boolean check)
    throws Exception
  {
    return getProviderConfig(check, null);
  }
  
  public
  InputStream getProviderConfig(boolean check, String sThePin)
    throws Exception
  {
    if(sCryptokiPath == null || sCryptokiPath.length() == 0) {
      String sSmartCardDll = ResourcesMgr.dat.getProperty("smartcard.dll");
      if(sSmartCardDll == null || sSmartCardDll.length() == 0) {
        sSmartCardDll = ResourcesMgr.config.getProperty("swingup.smartcard.dll");
      }
      if(sSmartCardDll != null && sSmartCardDll.length() > 1) {
        sCryptokiPath = sSmartCardDll;
      }
      else
      if(check) {
        try {
          signWithPKCS11(dummyToken, false, sThePin);
        }
        catch(Throwable th) {
          th.printStackTrace();
        }
      }
    }
    String sLibrary = sCryptokiPath;
    if(sCryptokiPath == null || sCryptokiPath.length() < 2) {
      sLibrary = "C:\\Windows\\System32\\bit4opki.dll";
    }
    // Non va fatta alcuna normalizzazione (ad es. raddoppio \ ecc.)
    return new ByteArrayInputStream(("name=" + sPROVIDER_NAME + "\nlibrary=" + sLibrary + "\n").getBytes());
  }
  
  public
  boolean check()
  {
    try {
      byte[] abSignature = signWithPKCS11(dummyToken, false);
      if(abSignature != null && abSignature.length > 0) {
        return true;
      }
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
    return false;
  }
  
  public
  boolean isSmartCardPresent()
  {
    return true;
  }
  
  public
  byte[] getIdCard()
    throws Exception
  {
    return "0".getBytes();
  }
  
  public
  String[] getPersonalData()
    throws Exception
  {
    return new String[]{};
  }
  
  public
  X509Certificate getX509CertificateObject()
    throws Exception
  {
    byte[] abSignature = null;
    try {
      abSignature = signWithPKCS11(dummyToken, false, null, 0);
      if(abSignature != null && abSignature.length > 0) {
        return getX509CertificateFromSignature(abSignature);
      }
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
    return null;
  }
  
  public
  X509Certificate getX509CertificateObject(int iSlot)
    throws Exception
  {
    byte[] abSignature = null;
    try {
      abSignature = signWithPKCS11(dummyToken, false, null, iSlot);
      if(abSignature != null && abSignature.length > 0) {
        return getX509CertificateFromSignature(abSignature);
      }
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
    return null;
  }
  
  public
  X509Certificate getX509CertificateObject(boolean boAskAlwaysPin)
    throws Exception
  {
    byte[] abSignature = null;
    try {
      abSignature = signWithPKCS11(dummyToken, boAskAlwaysPin, null, 0);
      if(abSignature != null && abSignature.length > 0) {
        return getX509CertificateFromSignature(abSignature);
      }
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
    return null;
  }
  
  public
  X509Certificate getX509CertificateObject(boolean boAskAlwaysPin, int iSlot)
    throws Exception
  {
    byte[] abSignature = null;
    try {
      abSignature = signWithPKCS11(dummyToken, boAskAlwaysPin, null, iSlot);
      if(abSignature != null && abSignature.length > 0) {
        return getX509CertificateFromSignature(abSignature);
      }
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
    return null;
  }
  
  public
  byte[] getX509Certificate()
    throws Exception
  {
    X509Certificate X509Certificate = getX509CertificateObject();
    if(X509Certificate != null) return X509Certificate.getEncoded();
    return null;
  }
  
  public
  byte[] getX509Certificate(int iSlot)
    throws Exception
  {
    X509Certificate X509Certificate = getX509CertificateObject(iSlot);
    if(X509Certificate != null) return X509Certificate.getEncoded();
    return null;
  }
  
  public
  byte[] getX509Certificate(boolean boAskAlwaysPin, int iSlot)
    throws Exception
  {
    X509Certificate X509Certificate = getX509CertificateObject(boAskAlwaysPin, iSlot);
    if(X509Certificate != null) return X509Certificate.getEncoded();
    return null;
  }
  
  public
  byte[] getX509Certificate(boolean boAskAlwaysPin)
    throws Exception
  {
    X509Certificate X509Certificate = getX509CertificateObject(boAskAlwaysPin, 0);
    if(X509Certificate != null) return X509Certificate.getEncoded();
    return null;
  }
  
  public
  byte[] signWithPKCS11(byte[] abDataToSign)
    throws Exception
  {
    int iSlot = 0;
    String sSlot = ResourcesMgr.dat.getProperty("smartcard.slot");
    if(sSlot == null || sSlot.length() == 0) {
      sSlot = ResourcesMgr.config.getProperty("swingup.smartcard.slot");
    }
    if(sSlot != null && sSlot.length() > 0) {
      try { iSlot = Integer.parseInt(sSlot.trim()); } catch(Throwable th) {}
    }
    return signWithPKCS11(abDataToSign, true, null, iSlot);
  }
  
  public
  byte[] signWithPKCS11(byte[] abDataToSign, int iSlot)
    throws Exception
  {
    return signWithPKCS11(abDataToSign, true, null, iSlot);
  }
  
  public
  byte[] signWithPKCS11(byte[] abDataToSign, boolean boAskAlwaysPin)
    throws Exception
  {
    int iSlot = 0;
    String sSlot = ResourcesMgr.dat.getProperty("smartcard.slot");
    if(sSlot == null || sSlot.length() == 0) {
      sSlot = ResourcesMgr.config.getProperty("swingup.smartcard.slot");
    }
    if(sSlot != null && sSlot.length() > 0) {
      try { iSlot = Integer.parseInt(sSlot.trim()); } catch(Throwable th) {}
    }
    return signWithPKCS11(abDataToSign, boAskAlwaysPin, null, iSlot);
  }
  
  public
  byte[] signWithPKCS11(byte[] abDataToSign, boolean boAskAlwaysPin, String sThePin)
    throws Exception
  {
    int iSlot = 0;
    String sSlot = ResourcesMgr.dat.getProperty("smartcard.slot");
    if(sSlot == null || sSlot.length() == 0) {
      sSlot = ResourcesMgr.config.getProperty("swingup.smartcard.slot");
    }
    if(sSlot != null && sSlot.length() > 0) {
      try { iSlot = Integer.parseInt(sSlot.trim()); } catch(Throwable th) {}
    }
    return signWithPKCS11(abDataToSign, boAskAlwaysPin, sThePin, iSlot);
  }
  
  public
  byte[] signWithPKCS11(byte[] abDataToSign, boolean boAskAlwaysPin, String sThePin, int iSlot)
    throws Exception
  {
    return null;
  }
  
  public static
  X509Certificate getX509Certificate(byte[] abX509Certificate)
  {
    if(abX509Certificate == null || abX509Certificate.length == 0) return null;
    X509Certificate x509certificate = null;
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(abX509Certificate);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      x509certificate = (X509Certificate) cf.generateCertificate(bais);
    }
    catch(Throwable th) {
      System.err.println("getX509Certificate 1: " + th);
    }
    if(x509certificate != null) return x509certificate;
    return x509certificate;
  }
  
  public static
  X509Certificate getX509CertificateFromSignature(byte[] abSignature)
  {
    return null;
  }
  
  public static
  byte[] verify(byte[] abSignature)
  {
    return null;
  }
  
  protected
  void initUSBReader()
    throws Exception
  {
    String sOSName = System.getProperty("os.name", "WIN");
    if(sOSName.toUpperCase().indexOf("WIN") < 0) {
      throw new Exception("SmartCardManager is not supported in " + sOSName);
    }
    sLibraryDirectory = System.getProperty("user.home") + File.separator + ".swingup";
    File file = new File(sLibraryDirectory);
    if(!file.exists()) {
      boolean boResult = file.mkdirs();
      if(!boResult) {
        throw new Exception("Can't create " + sLibraryDirectory + " folder.");
      }
    }
    loadLibraries();
  }
  
  protected
  void loadLibraries()
    throws Exception
  {
    System.out.println("[SWINGUP] SmartCardManager.loadLibraries()");
  }
}
