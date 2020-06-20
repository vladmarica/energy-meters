package com.vladmarica.energymeters.properties;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyFacing implements IUnlistedProperty<EnumFacing> {
  private String name;

  public static UnlistedPropertyFacing create(String name) {
    return new UnlistedPropertyFacing(name);
  }

  private UnlistedPropertyFacing(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public boolean isValid(EnumFacing value) {
    return true;
  }

  @Override
  public Class<EnumFacing> getType() {
    return EnumFacing.class;
  }

  @Override
  public String valueToString(EnumFacing value) {
    return value.getName();
  }
}
