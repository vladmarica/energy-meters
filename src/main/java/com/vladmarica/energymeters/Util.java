package com.vladmarica.energymeters;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class Util {
  private static final byte[] SIDE_LEFT = { 4, 5, 5, 4, 2, 3 };
  private static final byte[] SIDE_RIGHT = { 5, 4, 4, 5, 3, 2 };
  public static final byte[] SIDE_BACK = { 1, 0, 3, 2, 5, 4 };

  public static Direction getLeftFace(Direction face) {
    return Direction.values()[SIDE_LEFT[face.getIndex()]];
  }

  public static Direction getRightFace(Direction face) {
    return Direction.values()[SIDE_RIGHT[face.getIndex()]];
  }

  public static Direction getBackFace(Direction face) {
    return Direction.values()[SIDE_BACK[face.getIndex()]];
  }

  public static LazyOptional<IEnergyStorage> getEnergyStorage(World world, BlockPos pos, Direction side) {
    TileEntity tile = world.getTileEntity(pos);

    if (tile != null) {
      return tile.getCapability(CapabilityEnergy.ENERGY, side);
    }

    return LazyOptional.empty();
  }

  public static boolean isValidRateLimitString(String s) {
    if (s.isEmpty()) {
      return true;
    }

    try {
      int limit = Integer.parseInt(s);
      return limit >= 0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }
}
