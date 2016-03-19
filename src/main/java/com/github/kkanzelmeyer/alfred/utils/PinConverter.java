package com.github.kkanzelmeyer.alfred.utils;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class PinConverter
{
  public static class ModelB
  {
    public static Pin fromInt(Integer pin)
    {
      switch (pin)
      {
      case 3:
        return (Pin) RaspiPin.GPIO_08;
      case 12:
        return (Pin) RaspiPin.GPIO_01;
      case 13:
        return (Pin) RaspiPin.GPIO_02;
      default:
        return null;
      }
    }
  }
}
