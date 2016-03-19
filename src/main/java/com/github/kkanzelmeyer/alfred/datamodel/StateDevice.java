package com.github.kkanzelmeyer.alfred.datamodel;

import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;

/**
 * 
 * This object is used to represent a device connected to Alfred who's state is
 * of interest to the user; i.e. a light, ceiling fan, electrical outlet
 * (on/off), a garage door (open/closed), a doorbell (active, inactive), a
 * window sensor, etc.
 * 
 * @author Kevin Kanzelmeyer
 *
 */
public class StateDevice
{

  private String _id;
  private String _name;
  private State _state;
  private Type _type;

  /**
   * Builder constructor
   * 
   * @param builder
   *          The builder constructor
   */
  public StateDevice(Builder builder)
  {
    _id = builder.getId();
    _name = builder.getName();
    _state = builder.getState();
    _type = builder.getType();
  }

  /**
   * Copy constructor
   * 
   * Creates a copy from a reference
   * 
   */
  public StateDevice(StateDevice device)
  {
    _id = device.getId();
    _name = device.getName();
    _state = device.getState();
    _type = device.getType();
  }

  public String getId()
  {
    return _id;
  }

  public String getName()
  {
    return _name;
  }

  public State getState()
  {
    return _state;
  }

  public void setState(State state)
  {
    this._state = state;
  }

  public Type getType()
  {
    return this._type;
  }

  @Override
  public String toString()
  {
    return "\nDevice ID: " + _id + "\nDevice Name: " + _name + "\nDevice Type: " + _type + "\nDevice State: " + _state;

  }

  /**
   * Builder for the StateDevice. This is the only way to create a StateDevice
   * instance
   * 
   * @author kevin
   *
   */
  public static class Builder
  {

    private String id;
    private String name;
    private State state;
    private Type type;

    public String getId()
    {
      return id;
    }

    public Builder setId(String id)
    {
      this.id = id;
      return this;
    }

    public String getName()
    {
      return name;
    }

    public Builder setName(String name)
    {
      this.name = name;
      return this;
    }

    public State getState()
    {
      return state;
    }

    public Builder setState(State state)
    {
      this.state = state;
      return this;
    }

    public Type getType()
    {
      return type;
    }

    public Builder setType(Type type)
    {
      this.type = type;
      return this;
    }

    public StateDevice build()
    {
      return new StateDevice(this);
    }
  }

  @Override
  public boolean equals(Object other)
  {
    if (!(other instanceof StateDevice))
    {
      return false;
    }
    StateDevice that = (StateDevice) other;

    return this._id.equals(that.getId()) 
        && this._name.equals(that.getName()) 
        && this._state.equals(that.getState())
        && this._type.equals(that._type);
  }
}
