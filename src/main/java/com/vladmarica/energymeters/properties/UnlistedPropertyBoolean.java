package com.vladmarica.energymeters.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyBoolean implements IUnlistedProperty<Boolean> {
  private String name;

  public static UnlistedPropertyBoolean create(String name) {
    return new UnlistedPropertyBoolean(name);
  }

  private UnlistedPropertyBoolean(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public boolean isValid(Boolean value) {
    return true;
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

  @Override
  public String valueToString(Boolean value) {
    return Boolean.toString(value);
  }
}
