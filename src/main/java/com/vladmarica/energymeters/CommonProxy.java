package com.vladmarica.energymeters;

import com.vladmarica.energymeters.integration.ModIntegration;
import com.vladmarica.energymeters.network.Packets;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
  public void preInit(FMLPreInitializationEvent event) { }

  public void init(FMLInitializationEvent event) {
    // Register tile entities
    GameRegistry.registerTileEntity(
        TileEntityEnergyMeter.class,
        new ResourceLocation(EnergyMetersMod.MODID, "tile_entity_meter"));

    // Register network packets
    Packets.register();
  }

  public void postInit(FMLPostInitializationEvent event) {
    ModIntegration.checkInstalledMods();
  }

  public boolean handleEnergyBlockActivation(World world, BlockPos pos, EntityPlayer player) {
    return true;
  }
}
