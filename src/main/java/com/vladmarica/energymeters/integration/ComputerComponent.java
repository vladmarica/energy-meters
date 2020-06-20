package com.vladmarica.energymeters.integration;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid = ModIDs.COMPUTERCRAFT, iface = "dan200.computercraft.api.peripheral.IPeripheral")
public class ComputerComponent implements IPeripheral {
  public static final String COMPONENT_NAME = "energy_meter";

  private TileEntityEnergyMeterBase meter;

  public ComputerComponent(TileEntityEnergyMeterBase meter) {
    this.meter = meter;
  }

  public Object[] getTransferRate()  {
    return new Object[] { this.meter.getTransferRate() };
  }

  public Object[] getTotalEnergyTransferred() {
    return new Object[] { this.meter.getTotalEnergyTransferred() };
  }

  public Object[] getStatus() {
    String statusString = "active";
    if (!this.meter.isFullyConnected()) {
      statusString = "not connected";
    } else if (this.meter.isDisabled()) {
      statusString = "disabled";
    }
    return new Object[] { statusString };
  }

  public Object[] getRedstoneControlState() {
    return new Object[] { this.meter.getRedstoneControlState().getDescription().toLowerCase()};
  }

  public Object[] getEnergyType() {
    return new Object[] { this.meter.getEnergyType().getName() };
  }

  public Object[] getEnergyTypeAlias() {
    return new Object[] { this.meter.getEnergyAlias().getDisplayName() };
  }

  @Optional.Method(modid = ModIDs.COMPUTERCRAFT)
  @Nonnull
  @Override
  public String getType() {
    return COMPONENT_NAME;
  }

  @Optional.Method(modid = ModIDs.COMPUTERCRAFT)
  @Nonnull
  @Override
  public String[] getMethodNames() {
    return new String[] {
        "getTransferRate",
        "getTotalEnergyTransferred",
        "getStatus",
        "getRedstoneControlState",
        "getEnergyType",
        "getEnergyTypeAlias"
    };
  }

  @Optional.Method(modid = ModIDs.COMPUTERCRAFT)
  @Nullable
  @Override
  public Object[] callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull Object[] agrs) throws LuaException, InterruptedException {
    switch (method) {
      case 0:
        return this.getTransferRate();
      case 1:
        return this.getTotalEnergyTransferred();
      case 2:
        return this.getStatus();
      case 3:
        return this.getRedstoneControlState();
      case 4:
        return this.getEnergyType();
      case 5:
        return this.getEnergyTypeAlias();
      default:
        EnergyMetersMod.LOGGER.error("Attempted to call unknownComputerCraft method {}", method);
        return null;
    }
  }

  @Optional.Method(modid = ModIDs.COMPUTERCRAFT)
  @Override
  public boolean equals(@Nullable IPeripheral other) {
    if (other == this) {
      return true;
    }

    if (other instanceof ComputerComponent) {
      ComputerComponent otherComponent = (ComputerComponent) other;
      return this.meter.getPos().equals(otherComponent.meter.getPos())
          && this.meter.getWorld().provider.getDimension() == otherComponent.meter.getWorld().provider.getDimension();
    }

    return false;
  }
}
