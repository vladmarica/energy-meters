package com.vladmarica.energymeters.integration;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuildcraftIntegration {

  public static IMjReceiver getMJReciever(World world, BlockPos pos, EnumFacing side) {
    TileEntity tile = world.getTileEntity(pos);

    if (tile != null && tile.hasCapability(MjAPI.CAP_RECEIVER, side)) {
      return tile.getCapability(MjAPI.CAP_RECEIVER, side);
    }

    return null;
  }

  public static IMjConnector getMJConnector(World world, BlockPos pos, EnumFacing side) {
    TileEntity tile = world.getTileEntity(pos);

    if (tile != null && tile.hasCapability(MjAPI.CAP_CONNECTOR, side)) {
      return tile.getCapability(MjAPI.CAP_CONNECTOR, side);
    }

    return null;
  }
}
