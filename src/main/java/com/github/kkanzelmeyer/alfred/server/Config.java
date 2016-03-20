package com.github.kkanzelmeyer.alfred.server;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.utils.ParseJsonFile;

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
  private ArrayList<String> mNumbers = null;
  private String mEnvironment = null;
  private int mDoorbellReset = 5;
  private final Logger LOG = LoggerFactory.getLogger(Config.class);

  private Config()
  {
    mNumbers = new ArrayList<String>();
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

      JSONArray jsonNums = (JSONArray) json.get("numbers");
      Iterator<?> iterator = jsonNums.iterator();
      while (iterator.hasNext())
      {
        String number = (String) iterator.next();
        LOG.debug("Adding number: {}", number);
        mNumbers.add(number);
      }
      LOG.debug("Finshed with json saps");

      mEnvironment = System.getenv("JAVA_ENV");

    }
    catch (Exception e)
    {
      LOG.error("Unable to load SAPS file", e);
    }
    LOG.debug("Finished loading saps");
  }

  public String getImageDir()
  {
    return mImageDir;
  }

  public ArrayList<String> getNumbers()
  {
    return mNumbers;
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
}
