package com.jd.bluedragon.distribution.testCore.base;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.jd.security.configsec.JDSecurityPropertyInterface;
import com.jd.security.configsec.codec.binary.Base64;
import com.jd.security.configsec.spring.config.JDSecurityLoader;
import com.jd.security.configsec.spring.config.JMQMonitor;
import com.jd.security.configsec.spring.config.JMQMonitorOffline;
import com.jd.security.configsec.spring.config.JMQMonitorStrategy;
public class TestSecrect {
	 private static final Logger logger = Logger.getLogger(TestSecrect.class.getName());
	  public static final String INTERNAL_VALIDATION_ERROR_STRING = "The validation of encrypted file failed. For quick fixing, please go to J-ONE system or your own deployment system and re-deploy (发布） the application. For emergency contact, please contact support Timline(DongDong) 5765119. Detail information can be found at http://cf.jd.com/pages/viewpage.action?pageId=98743077";
	  public static final String INTERNAL_DECRYPTION_ERROR_STRING = "The process of decryption has error. For quick fixing, please go to J-ONE system or your own deployment system and re-deploy (发布） the application. For emergency contact, please contact support Timline(DongDong) 5765119. Detail information can be found at http://cf.jd.com/pages/viewpage.action?pageId=98743077.";
	  public static final String INTERNAL_ERROR_STRING = "The process of encryption has error. For quick fixing, please go to J-ONE system or your own deployment system and re-deploy (发布） the application. For emergency contact, please contact support Timline(DongDong) 5765119. Detail information can be found at http://cf.jd.com/pages/viewpage.action?pageId=98743077.";
	  private static final String ENCRYPTED_COMMENT = "# The file has been encrypted , please don't modify this file.";
	  private static final String MODE = "CONFIG_SEC_MODE";
	  private static volatile boolean debugMode = false;
	  private static final String DEBUG_MODE = "DEBUG_MODE";
	  
	  public static void main(String[] args) throws Exception{
		  validateEncFile(new File(""));
	  }
	  public static boolean checkIsEnabled(File originFile)
	  {
	    String enableFlagFileName = originFile.getParent() + File.separatorChar + "ok." + originFile.getName();
	    File enableFlagFile = new File(enableFlagFileName);
	    boolean isEncryptionEnabled = !enableFlagFile.exists();

	    return isEncryptionEnabled;
	  }

	  public static boolean checkIsNewDeployment(File originFile)
	  {
	    File flagFile = new File(originFile.getPath() + ".fin");
	    try
	    {
	      InputStream input = new FileInputStream(originFile);
	      try { BufferedReader br = new BufferedReader(new InputStreamReader(input));
	        boolean isNewDeployment;
	        try { String lineValue = br.readLine();
	          isNewDeployment = (!flagFile.exists()) || (null == lineValue) || (!lineValue.contains("# The file has been encrypted , please don't modify this file."));
	        }
	        finally
	        {
	        }
	      }
	      finally
	      {
	        boolean isNewDeployment;
	        input.close();
	      }
	    }
	    catch (IOException e)
	    {
	      throw new IllegalStateException(e);
	    }
	    boolean isNewDeployment = false;
	    return isNewDeployment;
	  }

	  public static void touchFile(File origFile) throws IOException
	  {
	    File flagFile = new File(origFile.getPath() + ".fin");
	    if (!flagFile.exists())
	      new FileOutputStream(flagFile).close();
	  }

	  public static void cleanProperties(File file)
	    throws IOException
	  {
	    FileWriter fw = null;
	    try {
	      fw = new FileWriter(file);
	      fw.write("# The file has been encrypted , please don't modify this file.");

	      touchFile(file);

	      if (fw != null) {
	        fw.flush();
	        fw.close();
	      }
	    }
	    finally
	    {
	      if (fw != null) {
	        fw.flush();
	        fw.close();
	      }
	    }
	  }

	  public static boolean validateEncFile(File origFile) throws Exception
	  {
	    JMQMonitor monitor = new JMQMonitor();
	    JMQMonitorOffline monitorOffline = new JMQMonitorOffline();
	    JMQMonitorStrategy.initMonitor(monitor, monitorOffline);

	    boolean r = true;
	    try
	    {
	      File encodeFile = new File(origFile.getParentFile() + File.separator + "." + origFile.getName() + ".enc");
	      JDSecurityPropertyInterface JDProperty = JDSecurityLoader.getInstance().getValidationUtil();
	      JDProperty.handleInit(origFile, false, isDebugMode());

	      String decProperties = JDProperty.decryptProperties(encodeFile);
	      InputStream encStream = new ByteArrayInputStream(decProperties.getBytes());
	      Properties encProp = new Properties();
	      encProp.load(encStream);

	      Properties origProp = new Properties();
	      InputStream origis = new FileInputStream(origFile);
	      try
	      {
	        origProp.load(origis);
	      } finally {
	        if (origis != null) {
	          origis.close();
	        }

	      }

	      Set<String> origKeys = origProp.stringPropertyNames();
	      Set<String> encKeys = encProp.stringPropertyNames();


	      boolean isSentJdbc = true;
	      String jdbcUrl = "";
	      for (String key : origKeys) {
	        if ((!key.contains("password")) && (!key.contains("pwd")) && (!key.contains("pass")))
	        {
	          if (key.contains("jdbc.url")) {
	            jdbcUrl = origProp.getProperty(key);
	          }
	        }
	        String origValue = origProp.getProperty(key);
	        String encValue = encProp.getProperty(key);
	        if (!origValue.equals(encValue)) {
	          r = false;


	          logger.severe("Validation error:key [" + key + "] validation failed.");
	        }
	      }
	      String KEYSPECALGO;
	      if ((isSentJdbc) && (!jdbcUrl.equals(""))) {
	        KEYSPECALGO = "AES";
	        String ALGO = "AES/CBC/PKCS5Padding";
	        String AESCodeKey = JDSecurityLoader.getAESCodeKey();
	        byte[] key = Base64.decodeBase64(AESCodeKey);

	        SecretKey JCEkey = new SecretKeySpec(key, KEYSPECALGO);
	        IvParameterSpec ivParameterSpec = new IvParameterSpec(key);

	        Cipher cipherDec = Cipher.getInstance(ALGO);
	        cipherDec.init(1, JCEkey, ivParameterSpec);

	        byte[] origClass = cipherDec.doFinal(jdbcUrl.getBytes());
	        String urlValue = Base64.encodeBase64String(origClass);

	      }
	      return r;
	    }
	    catch (Exception ex) {
	      String stack = JMQMonitor.extractStackTrace(ex);


	      throw ex;
	    } finally {
	      JMQMonitorStrategy.shutdownMonitor(monitor, monitorOffline);
	    }
	  }

	  public static boolean isProdEnv()
	  {
	    String mode = System.getenv("CONFIG_SEC_MODE");
	    boolean r;
	    if ((mode != null) && (mode.equals("TEST")))
	      r = false;
	    else {
	      r = true;
	    }
	    return r;
	  }

	  public static synchronized void setDebugMode(boolean isDebug)
	  {
	    debugMode = isDebug;
	  }

	  public static synchronized boolean isDebugMode()
	  {
	    boolean isDebug = false;
	    String debugModeEnv = System.getenv("DEBUG_MODE");
	    if ((debugModeEnv != null) && (debugModeEnv.equals("TRUE"))) {
	      isDebug = true;
	    }
	    if (debugMode) {
	      isDebug = true;
	    }

	    return isDebug;
	  }
}
