package com.vladmarica.energymeters.integration;

import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

/**
 * Peripheral provider for ComputerCraft/CC:Tweaked. Provides a {@link ComputerComponent} when
 * ComputerCraft requests a peripheral for a {@link TileEntityEnergyMeterBase}.
 */
@Optional.Interface(modid = ModIDs.COMPUTERCRAFT, iface = "dan200.computercraft.api.peripheral.IPeripheralProvider")
public class ComputerCraftPeripheralProvider implements IPeripheralProvider {

  @Nullable
  @Override
  public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileEntityEnergyMeterBase) {
      TileEntityEnergyMeterBase meter = (TileEntityEnergyMeterBase) tile;
      if (facing != meter.getScreenSide()) {
        return meter.getComputerComponent();
      }
    }

    return null;
  }
}
