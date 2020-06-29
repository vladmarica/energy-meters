package com.vladmarica.energymeters.energy;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEnergyMeter {

  /**
   * Returns whether the given side can receive energy.
   */
  boolean canReceiveEnergy(EnumFacing side);

  /**
   * Returns whether the given side can send energy.
   */
  boolean canEmitEnergy(EnumFacing side);

  /**
   * Handles the receiving of energy on the given side.
   * @return The amount of energy that was accepted. Must be at most the amount provided and
   * non-negative.
   */
  long receiveEnergy(long amount, boolean simulate, EnumFacing side);

  /**
   * Returns the amount of energy requested on the given side. Not all implementations will need
   * need to override this, since not all energy systems have the concept of "requesting power".
   */
  default long getRequestedEnergy(EnumFacing side) {
    return 0;
  }

  BlockPos getPosition();

  World getWorldObj();
}
