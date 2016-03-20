package com.github.kkanzelmeyer.alfred.plugins;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceHandler;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.server.Config;
import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.utils.PinConverter;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * Raspberry Pi Doorbell Plugin with Webcam option
 * 
 * This class handles the server behavior for a doorbell device. The general
 * behavior is that the sensor near the door (button, motion, etc) can set the
 * device state to "active". When the device is set to active it requests a
 * picture from the connected webcam and sends a message to all connected
 * clients. It will also start a reset timer that resets the device in
 * "inactive" after two minutes.
 * 
 * The plugin has three primary components:
 * 
 * <ul>
 * <li><b>Sensor:</b> This is the hardware that notifies the system when a
 * visitor is present. It can be a button, motion sensor, proximity sensor, etc.
 * </li>
 * <li><b>State Handler:</b> The state handler is responsible for getting a
 * picture from the webcam when the device is set to Active, and for sending
 * state changes to all connected clients</li>
 * </ul>
 * 
 * @author kevin
 *
 */
public class RPDoorbellPluginWebcam implements DevicePlugin
{

  private int                  pin;
  private String               myDeviceId;
  private GpioPinDigitalInput  sensor       = null;
  private DoorbellStateHandler stateHandler = null;
  private final String         CLASSNAME    = this.getClass().getSimpleName();
  private Timer                timer        = null;

  private static final boolean DEPLOYED = Config.INSTANCE.getEnvironment().equals("production");
  final private static Logger  LOG      = LoggerFactory.getLogger(RPDoorbellPluginWebcam.class);

  public RPDoorbellPluginWebcam(int pin, StateDevice device)
  {
    this.pin = pin;
    this.myDeviceId = device.getId();
  }

  /**
   * @param pin
   * @param deviceId
   * @deprecated
   */
  public RPDoorbellPluginWebcam(int pin, String deviceId)
  {
    this.pin = pin;
    this.myDeviceId = deviceId;
  }

  /**
   * Call this method to activate the plugin. Note - the environment variable
   * JAVA_ENV must be set to 'production' in order to activate the Raspberry pi
   * controls
   */
  public void activate()
  {
    // Raspberry pi handler
    if (Config.INSTANCE.getEnvironment().equals("production"))
    {
      LOG.debug("Adding plugin for pin {}", pin);
      GpioController gpio = GpioFactory.getInstance();
      sensor = gpio.provisionDigitalInputPin(PinConverter.ModelB.fromInt(pin), "Input", PinPullResistance.PULL_DOWN);
      sensor.addListener(new DoorbellSensorHandler());
    }

    // State handler
    if (stateHandler == null)
    {
      stateHandler = new DoorbellStateHandler();
      StateDeviceManager.INSTANCE.addDeviceHandler(stateHandler);
    }

    // create new timer object for reset task
    timer = new Timer();
  }

  public void deactivate()
  {
    timer.cancel();
    LOG.trace("Deactivating plugin {}", CLASSNAME);
    StateDeviceManager.INSTANCE.removeDeviceHandler(stateHandler);
    stateHandler = null;
  }

  /**
   * This class handles input changes from the Raspberry Pi GPIO pins
   * 
   * @author kevin
   *
   */
  private class DoorbellSensorHandler implements GpioPinListenerDigital
  {

    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
    {
      if (event.getState() == PinState.HIGH)
      {
        LOG.info("{} event detected : {}", myDeviceId, event.getState());
        StateDevice device = StateDeviceManager.INSTANCE.getDevice(myDeviceId);
        State newState;
        if (device.getState() != State.ACTIVE)
        {
          newState = State.ACTIVE;
        }
        else
        {
          // TODO for testing only - remove setting to inactive for
          // product installation
          newState = State.INACTIVE;
        }

        // update the state
        StateDeviceManager.INSTANCE.updateStateDevice(myDeviceId, newState);
      }
    }
  }

  /**
   * The purpose of this class is to handle state changes to a doorbell device
   * 
   * @author Kevin Kanzelmeyer
   *
   */
  public class DoorbellStateHandler implements StateDeviceHandler
  {

    private DoorbellResetTask resetTask = null;

    public void onAddDevice(StateDevice device)
    {
      // filter message based on this plugin's device id
      if (device.getId().equals(myDeviceId))
      {
        LOG.trace("Device added");
        LOG.trace(device.toString());
      }
    }

    /**
     * This method starts a thread to capture an image with the webcam. It also
     * starts building the message to send clients. The message is completed and
     * sent in the TakePictureCallback
     */
    public void onUpdateDevice(StateDevice device)
    {

      if (device.getId().equals(myDeviceId))
      {

        LOG.info("Device updated {}", device.toString());

        // if the state is being set to Active, take a picture
        // and let the callback finish sending the message
        if (device.getState() == State.ACTIVE)
        {

          WebCameraThread webCamThread = new WebCameraThread(new TakePictureCallback());
          new Thread(webCamThread).start();

          // start a reset timer
          startResetTimer(device);

        }
        else
        {
          LOG.trace("Resetting status");
        }
      }
    }

    public void onRemoveDevice(StateDevice device)
    {
      // filter message based on this plugin's device id
      if (device.getId().equals(myDeviceId))
      {
        LOG.trace("Device removed");
        LOG.trace(device.toString());
      }
    }

    /**
     * This helper method schedules the reset doorbell task
     * 
     * @param seconds
     *          delay in minutes for the reset timer
     * @param device
     *          reference to the state device
     */
    public void startResetTimer(StateDevice device)
    {
      resetTask = new DoorbellResetTask(device);
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.SECOND, Config.INSTANCE.getDoorbellReset());
      Date endTime = calendar.getTime();
      LOG.info("Scheduling reset timer");
      timer.schedule(resetTask, endTime);
    }

    /**
     * This class is a callback to the webcam thread. The onComplete method is
     * called by the webcam thread after the picture has been taken.
     * 
     * @author Kevin Kanzelmeyer
     *
     */
    private class TakePictureCallback implements WebCamCallback
    {

      /**
       * This method adds the image to the message that was started in the
       * parent class onDeviceUpdate method. After the message is built it is
       * sent to each client, then the image is saved as a file, then an email
       * is sent to email clients
       */
      public void onComplete(RenderedImage image)
      {
        LOG.debug("Finished taking picture. Adding to message");

        // Save the image to a file
        LOG.debug("Saving image file on server");
        String date = String.valueOf(System.currentTimeMillis());
        String filename = "visitor" + date + ".jpg";
        try
        {
          File directory = new File(Config.INSTANCE.getImageDir());
          File outputfile = new File(directory, filename);
          ImageIO.write(image, "jpg", outputfile);
          LOG.debug("Image saved: {}", outputfile.getAbsolutePath());
        }
        catch (IOException e)
        {
          // TODO Auto-generated catch block
          LOG.error("Trouble saving image", e);
        }

        // send texts
        if (DEPLOYED)
        {
          // Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
          Server.INSTANCE.sendTextAlert();
        }
      }
    }

    /**
     * Class to reset the doorbell state to inactive. This timer task is
     * scheduled by the parent class
     * 
     * @author Kevin Kanzelmeyer
     *
     */
    private class DoorbellResetTask extends TimerTask
    {

      private StateDevice mDevice = null;

      /**
       * @param device
       *          The device to reset to INACTIVE
       */
      public DoorbellResetTask(StateDevice device)
      {
        mDevice = device;
      }

      /**
       * Task to be executed when the timer scheduler calls it
       */
      @Override
      public void run()
      {
        LOG.info("Resetting {}", mDevice.getName());
        StateDeviceManager.INSTANCE.updateStateDevice(mDevice.getId(), State.INACTIVE);
      }
    }
  }

}
