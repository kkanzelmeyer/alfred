package com.github.kkanzelmeyer.alfred.datamodel;

/**
 * 
 * This interface should be implemented by a class which needs to be notified
 * when a device is added, updated, or removed. For example, if you are writing
 * a client application and you need to update a User Interface based on the
 * state of a device you could have a class that implements this interface. Then
 * you can define custom behavior for each method
 * 
 * @author Kevin Kanzelmeyer
 *
 */
public interface StateDeviceHandler
{

  /**
   * Called when a new device is added to the StateDeviceManager
   * 
   * @param device
   *          A reference to the added device
   */
  public void onAddDevice(StateDevice device);

  /**
   * Called when a device in the StateDeviceManager is updated
   * 
   * @param device
   *          A reference to the updated device
   */
  public void onUpdateDevice(StateDevice device);

  /**
   * Called when a device is removed from the StateDeviceManager
   * 
   * @param device
   *          A reference to the removed state device
   */
  public void onRemoveDevice(StateDevice device);

}
