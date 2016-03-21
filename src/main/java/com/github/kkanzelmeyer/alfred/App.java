package com.github.kkanzelmeyer.alfred;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.twilio.sdk.TwilioRestException;

/**
 * Hello world!
 *
 */
public class App
{
  
  public static void main(String[] args)
  {
    try
    {
      Server.INSTANCE.sendTextAlert("Testing", "C:\\Users\\Kanzelmeyer\\Desktop\\alfred_launcher.png");
    }
    catch (TwilioRestException e)
    {
      e.printStackTrace();
    }
  }
}
