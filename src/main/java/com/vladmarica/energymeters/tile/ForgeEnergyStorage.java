package com.vladmarica.energymeters.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyStorage implements IEnergyStorage {
  private TileEntityEnergyMeter tile;
  private EnumFacing side;

  public ForgeEnergyStorage(TileEntityEnergyMeter tile, EnumFacing side) {
    this.tile = tile;
    this.side = side;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return this.tile.receiveEnergy(maxReceive, simulate, this.side);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return this.tile.extractEnergy(maxExtract, simulate, this.side);
  }

  @Override
  public int getEnergyStored() {
    return this.tile.getEnergyStored(this.side);
  }

  @Override
  public int getMaxEnergyStored() {
    return this.tile.getMaxEnergyStored(this.side);
  }

  @Override
  public boolean canExtract() {
    return this.tile.canExtract(this.side);
  }

  @Override
  public boolean canReceive() {
    return this.tile.canReceive(this.side);
  }
}
