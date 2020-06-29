package com.vladmarica.energymeters.energy.storage;

import com.vladmarica.energymeters.energy.IEnergyMeter;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyStorage implements IEnergyStorage {
  private IEnergyMeter meter;
  private EnumFacing side;

  public ForgeEnergyStorage(IEnergyMeter meter, EnumFacing side) {
    this.meter = meter;
    this.side = side;
  }

  @Override
  public int receiveEnergy(int amount, boolean simulate) {
    return (int) this.meter.receiveEnergy(amount, simulate, this.side);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored() {
    return 0;
  }

  @Override
  public int getMaxEnergyStored() {
    return 0;
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return this.meter.canReceiveEnergy(this.side);
  }
}
