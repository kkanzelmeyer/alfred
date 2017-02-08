package com.github.kkanzelmeyer.alfred.plugins;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.alert.AlertService;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceHandler;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.server.Config;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionDetectorDefaultWithDNE;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class WebcamMotionPlugin extends DevicePlugin {

  private DoorbellStateHandler stateHandler = null;
  private final String CLASSNAME = this.getClass().getSimpleName();
  private Timer timer = null;
  private Webcam webcam = null;
  private WebcamMotionDetector detector = null;
  private BufferedImage detectionImage = null;
  // private BufferedImage baselineImage = null;
  private String detectionImagePath = null;
  private static final Logger LOG = LoggerFactory.getLogger(WebcamMotionPlugin.class);

  public WebcamMotionPlugin(StateDevice device) {
    super(device);
    // TODO make webcam device a SAP
    LOG.debug("Creating webcam instance");
    Webcam.setDriver(new V4l4jDriver()); // this is important
    webcam = Webcam.getDefault();
    Dimension[] myResolution = new Dimension[] { new Dimension(640, 360), new Dimension(1280, 720) };
    webcam.setCustomViewSizes(myResolution);
    webcam.setViewSize(myResolution[0]);
    // take a throwaway picture to allow the camera to adjust itself
    webcam.open();
    webcam.getImage();
    webcam.close();
  }

  @Override
  public void activate() {
    LOG.debug("Activating plugin: {}", CLASSNAME);

    // motion detection
    if (detector == null) {
      detector = new WebcamMotionDetector(webcam,
          new WebcamMotionDetectorDefaultWithDNE(Config.INSTANCE.getPixelThreshold(),
              Config.INSTANCE.getAreaThreshold()),
          Config.INSTANCE.getMotionInterval());
      detector.setMaxAreaThreshold(Config.INSTANCE.getMaxAreaThreshold());
      detector.setInertia(Config.INSTANCE.getInertia());
      detector.addMotionListener(new MotionListener());
      // create do-not-engage zone
      detector.setDne(Config.INSTANCE.getDne());
      detector.start();
    }

    // State handler
    if (stateHandler == null) {
      stateHandler = new DoorbellStateHandler();
      StateDeviceManager.INSTANCE.addDeviceHandler(stateHandler);
    }

    // create new timer object for reset task
    timer = new Timer();
  }

  @Override
  public void deactivate() {
    timer.cancel();
    LOG.trace("Deactivating plugin {}", CLASSNAME);
    StateDeviceManager.INSTANCE.removeDeviceHandler(stateHandler);
    stateHandler = null;
    detector.stop();
  }

  private class MotionListener implements WebcamMotionListener {

    @Override
    public void motionDetected(WebcamMotionEvent wme) {
      LOG.info("Motion detected");
      LOG.debug("Area affected by motion: {}%", wme.getArea());
      LOG.debug("Motion COG: {}%", wme.getCog());
      int sum = 0;
      for (int t : wme.getSource().getThresholds()) {
        LOG.trace("Threshold: {}", t);
        sum += t;
      }
      double avgThreshold = sum / wme.getSource().getThresholds().size();
      LOG.debug("Average Threshold: {}", avgThreshold);
      String area = "Area affected by motion: " + wme.getArea();
      String threshold = "Average threshold: " + avgThreshold;
      String cog = "Motion COG: " + wme.getCog();
      StateDevice device = StateDeviceManager.INSTANCE.getDevice(myDeviceId);
      State newState;
      detectionImage = wme.getCurrentImage();
      detectionImagePath = saveImage(detectionImage);
      if (device.getState() == State.INACTIVE) {
        AlertService.INSTANCE.sendAlert(detectionImagePath, area + "<br/>" + threshold + "<br/>" + cog);
        newState = State.ACTIVE;
        StateDeviceManager.INSTANCE.updateStateDevice(myDeviceId, newState);
      }
      // baselineImage = wme.getPreviousImage();
      // saveImage(baselineImage);
    }

    public String saveImage(BufferedImage image) {
      // Save the image to a file
      try {
        // Create directory
        Calendar today = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dayFormat = df.format(today.getTime());
        File directory = new File(Config.INSTANCE.getImageDir() + "/" + dayFormat);
        if (!directory.exists()) {
          directory.mkdirs();
        }

        // Create file
        df = new SimpleDateFormat("yyyyMMdd-kkmmss");
        String date = df.format(today.getTime());
        String filename = "visitor" + date + ".jpg";
        File outputfile = new File(directory, filename);
        LOG.debug("Saving image: {}", outputfile.getAbsolutePath());
        ImageIO.write(image, "jpg", outputfile);
        LOG.debug("Image saved: {}", outputfile.getAbsolutePath());
        return outputfile.getAbsolutePath();
      } catch (Exception e) {
        LOG.error("Trouble saving image", e);
      }
      return null;
    }
  }

  private class DoorbellStateHandler implements StateDeviceHandler {
    @Override
    public void onAddDevice(StateDevice device) {
    }

    @Override
    public void onUpdateDevice(StateDevice device) {
      LOG.debug("State update received by {}", CLASSNAME);
      if (device.getState() == State.ACTIVE) {
        LOG.debug("State set to {}", device.getState());
        startResetTimer(device);
      } else {
        LOG.trace("Resetting status");
      }
    }

    @Override
    public void onRemoveDevice(StateDevice device) {
    }

    public void startResetTimer(StateDevice device) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.SECOND, Config.INSTANCE.getDoorbellReset());
      Date endTime = calendar.getTime();
      LOG.info("Scheduling reset timer");
      // timer.schedule(new DoorbellResetTask(device), endTime);
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          LOG.info("Resetting {}", device.getName());
          StateDeviceManager.INSTANCE.updateStateDevice(device.getId(), State.INACTIVE);
        }

      }, endTime);
    }
  }
}
