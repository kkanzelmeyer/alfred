package com.github.kkanzelmeyer.alfred.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.enums.State;

/**
 * Used to manage the state devices connected to Alfred
 * 
 * @author Kevin Kanzelmeyer
 *
 */
public enum StateDeviceManager
{
  INSTANCE;

  // List of devices to manage
  private HashMap<String, StateDevice> deviceList = new HashMap<String, StateDevice>();

  // List of handlers to manage
  private ArrayList<StateDeviceHandler> deviceHandlers = new ArrayList<StateDeviceHandler>();

  // Logger
  final private static Logger LOG = LoggerFactory.getLogger(StateDeviceManager.class);

  /**
   * Method to retrieve a clone of a device. Returns null if the device doesn't
   * exist
   * 
   * @param id
   *          The ID of the desired device
   * @return an instance of the desired state device. Note that this method
   *         returns "null" if the desired object doesn't exist
   */
  public StateDevice getDevice(String id)
  {
    if (deviceList.containsKey(id))
    {
      StateDevice device = new StateDevice(deviceList.get(id));
      return device;
    } else
      return null;
  }

  /**
   * Method to see if the device manager contains a specified device
   * 
   * @param id
   *          The ID of the desired device
   * @return True if the device exists in the Device Manager, False otherwise
   */
  public boolean contains(String id)
  {
    return deviceList.containsKey(id);
  }

  /**
   * Method to retrieve all devices
   * 
   * @return A Hashmap of all registered devices in the Device Manager
   */
  public HashMap<String, StateDevice> getAllDevices()
  {
    return deviceList;
  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - -
  // Add, Remove, Update methods for DeviceList
  // - - - - - - - - - - - - - - - - - - - - - - - - - -

  /**
   * This method adds the input device to be managed by the Device Manager. It
   * will notify all registered StateDeviceHandlers that a new device has been
   * added
   * 
   * @param device
   *          A StateDevice
   * 
   */
  public void addStateDevice(StateDevice device)
  {
    LOG.trace("Adding device " + device.getName());
    deviceList.put(device.getId(), device);
    for (StateDeviceHandler handler : deviceHandlers)
    {
      handler.onAddDevice(device);
    }
  }

  /**
   * This method removes a device from the device manager
   * 
   * @param device
   *          A StateDevice
   */
  public void removeStateDevice(StateDevice device)
  {
    LOG.trace("Removing device " + device.getName());
    deviceList.remove(device.getId());
    for (StateDeviceHandler handler : deviceHandlers)
    {
      handler.onRemoveDevice(device);
    }
  }

  /**
   * Method to update a given state device. Note that if the device doesn't
   * currently exist it will be added. Also note that the state device will only
   * be updated if the input device state is different
   * 
   * @param id
   *          The ID of the device to update
   * @param state
   *          The new state
   */
  public void updateStateDevice(String id, State state)
  {
    LOG.debug("Update state device called for " + id + " with state " + state);
    if (deviceList.containsKey(id))
    {
      StateDevice updateDevice = getDevice(id);
      if (updateDevice.getState() != state)
      {
        updateDevice.setState(state);
        deviceList.put(id, updateDevice);
        // notify handlers
        for (StateDeviceHandler handler : deviceHandlers)
        {
          handler.onUpdateDevice(updateDevice);
        }
      } else
      {
        LOG.debug("No state change, ignoring update");
      }
    } else
    {
      LOG.error("Device not recognized");
    }
  }

  // - - - - - - - - - - - - - - - - - - - - - - - - - -
  // Add, Remove methods for DeviceHandlers
  // - - - - - - - - - - - - - - - - - - - - - - - - - -
  /**
   * Method to add a StateDeviceHandler to the device manager
   * 
   * @param handler
   *          An instance of the handler
   */
  public void addDeviceHandler(StateDeviceHandler handler)
  {
    if (!deviceHandlers.contains(handler))
    {
      LOG.trace("Adding device handler " + handler.getClass().getSimpleName());
      deviceHandlers.add(handler);
    }
  }

  /**
   * Method to remove a StateDeviceHandler to the device manager
   * 
   * @param handler
   *          An instance of the handler
   */
  public void removeDeviceHandler(StateDeviceHandler handler)
  {
    if (deviceHandlers.contains(handler))
    {
      LOG.trace("Removing device handler " + handler.getClass().getSimpleName());
      deviceHandlers.remove(handler);
    }
  }
}
