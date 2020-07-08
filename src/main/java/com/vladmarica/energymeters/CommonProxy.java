package com.vladmarica.energymeters;

import com.vladmarica.energymeters.network.NetworkPackets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy {

  public void init(FMLCommonSetupEvent event) {
    NetworkPackets.register();
    EnergyMetersMod.LOGGER.info("CommonProxy init");
  }

  public boolean handleEnergyBlockActivation(World world, BlockPos pos, PlayerEntity player) {
    return true;
  }
}
