package com.vladmarica.energymeters.tile;

import com.vladmarica.energymeters.energy.IEnergyMeter;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.info.ILocatable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EUEnergyStorage implements IEnergyConductor, ILocatable {

  private IEnergyMeter meter;

  public EUEnergyStorage(IEnergyMeter meter) {
    this.meter = meter;
  }

  @Override
  public double getConductionLoss() {
    return 0;
  }

  @Override
  public double getInsulationEnergyAbsorption() {
    return Double.MAX_VALUE;
  }

  @Override
  public double getInsulationBreakdownEnergy() {
    return Double.MAX_VALUE;
  }

  @Override
  public double getConductorBreakdownEnergy() {
    return Double.MAX_VALUE;
  }

  @Override
  public void removeInsulation() {}

  @Override
  public void removeConductor() {}

  @Override
  public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
    return this.meter.canReceiveEnergy(side);
  }

  @Override
  public boolean emitsEnergyTo(IEnergyAcceptor acceptor, EnumFacing side) {
    return this.meter.canEmitEnergy(side);
  }

  @Override
  public BlockPos getPosition() {
    return this.meter.getPosition();
  }

  @Override
  public World getWorldObj() {
    return this.meter.getWorldObj();
  }
}
