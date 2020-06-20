package com.vladmarica.energymeters.tile;

import com.vladmarica.energymeters.energy.EnergyTypes;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityEnergyMeterFE extends TileEntityEnergyMeterBase {
  private ForgeEnergyStorage inputEnergyStorage;
  private ForgeEnergyStorage outputEnergyStorage;

  public TileEntityEnergyMeterFE() {
    super(EnergyTypes.FE);
  }

  @Override
  public void onLoad() {
    super.onLoad();

    if (!this.world.isRemote) {
      this.inputEnergyStorage = new ForgeEnergyStorage(this, inputSide);
      this.outputEnergyStorage = new ForgeEnergyStorage(this, outputSide);
    }
  }

  @Override
  public void handleSideUpdateRequest(@Nullable EnumFacing inputSide, @Nullable EnumFacing outputSide) {
    this.inputEnergyStorage = new ForgeEnergyStorage(this, inputSide);
    this.outputEnergyStorage = new ForgeEnergyStorage(this, outputSide);
    super.handleSideUpdateRequest(inputSide, outputSide);
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY && facing != null && doesSideAcceptConnection(facing)) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY && facing != null && doesSideAcceptConnection(facing)) {
      if (facing == inputSide) {
        return CapabilityEnergy.ENERGY.cast(this.inputEnergyStorage);
      } else if (facing == outputSide) {
        return CapabilityEnergy.ENERGY.cast(this.outputEnergyStorage);
      }
      throw new RuntimeException("Attempted to get energy capability for invalid side: " + facing);
    }
    return super.getCapability(capability, facing);
  }
}
