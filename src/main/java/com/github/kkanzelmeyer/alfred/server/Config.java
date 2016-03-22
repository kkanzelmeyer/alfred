package com.github.kkanzelmeyer.alfred.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.utils.ParseJsonFile;
import com.twilio.sdk.TwilioRestClient;

/**
 * @author kevin
 *
 */
public enum Config
{

  INSTANCE;

  private String mImageDir = null;
  private String twilioSid = null;
  private String twilioToken = null;
  private ArrayList<String> mEmails = null;
  private String mEnvironment = null;
  private int mDoorbellReset = 5;
  private TwilioRestClient mClient = null;
  // Email saps
  private String auth = null;
  private String ttls = null;
  private String host = null;
  private String port = null;
  private String username = null;
  private String token = null;
  private Properties mProperties = null;
  
  private final Logger LOG = LoggerFactory.getLogger(Config.class);

  private Config()
  {
    mEmails = new ArrayList<String>();
    try
    {
      JSONObject json = ParseJsonFile.toObject("config.json");

      LOG.debug("Saving SAPS");
      JSONObject twilio = (JSONObject) json.get("twilio");
      twilioSid = (String) twilio.get("sid");
      LOG.debug("Twilio SID: {}", twilioSid);

      twilioToken = (String) twilio.get("token");
      LOG.debug("Twilio Token: {}", twilioToken);

      mImageDir = (String) json.get("imageDir");
      LOG.debug("Image Directory: {}", mImageDir);

      Long val = ((Long) json.get("doorbellReset"));
      mDoorbellReset = val.intValue();
      LOG.debug("Doorbell reset : {}", mDoorbellReset);

      JSONArray jArr = (JSONArray) json.get("emails");
      Iterator<?> iterator = jArr.iterator();
      while (iterator.hasNext())
      {
        String email = (String) iterator.next();
        LOG.debug("Adding email: {}", email);
        mEmails.add(email);
      }

      // mail settings
      LOG.debug("Loading mail settings");
      JSONObject mail = (JSONObject) json.get("mail");
      JSONObject smtp = (JSONObject) mail.get("smtp");
      auth = (String) smtp.get("auth");
      ttls = (String) smtp.get("ttls");
      host = (String) smtp.get("host");
      port = (String) smtp.get("port");
      username = (String) mail.get("username");
      token = (String) mail.get("token");
      
      LOG.debug("Finshed with json saps");

      // TODO set to environmental variable for deployment
      // mEnvironment = System.getenv("JAVA_ENV");
      mEnvironment = "development";

    }
    catch (Exception e)
    {
      LOG.error("Config Error", e);
    }
    LOG.debug("Finished loading saps");
  }

  public String getImageDir()
  {
    return mImageDir;
  }

  public ArrayList<String> getEmails()
  {
    return mEmails;
  }

  public String getEnvironment()
  {
    return mEnvironment;
  }

  public int getDoorbellReset()
  {
    return mDoorbellReset;
  }

  public void setDoorbellReset(int val)
  {
    mDoorbellReset = val;
  }

  public TwilioRestClient getTwilioClient()
  {
    // Create twilio obj
    if (mClient == null)
    {
      LOG.debug("Creating twilio client");
      mClient = new TwilioRestClient(twilioSid, twilioToken);
    }
    return mClient;
  }

  public Properties getEmailProperties()
  {
    if (mProperties == null)
    {
      LOG.debug("Creating mail properties");
      Properties props = new Properties();
      props.put("mail.smtp.auth", auth);
      props.put("mail.smtp.starttls.enable", ttls);
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", port);
      props.put("mail.username", username);
      props.put("mail.token", token);
      props.put("mail.smtp.connectiontimeout", "3000");
      mProperties = props;
    }
    LOG.debug(mProperties.toString());
    return mProperties;
  }

  public String getUsername()
  {
    return username;
  }


  public String getToken()
  {
    return token;
  }

}
