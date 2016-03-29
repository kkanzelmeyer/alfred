package com.github.kkanzelmeyer.alfred.plugins;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;

public abstract class DevicePlugin
{
  String myDeviceId = null;
  
  DevicePlugin(StateDevice device)
  {
    if(myDeviceId == null)
    {
      myDeviceId = device.getId();
    }
  }

  public abstract void activate();

  public abstract void deactivate();

}
