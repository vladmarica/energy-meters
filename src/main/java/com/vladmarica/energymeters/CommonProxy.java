package com.vladmarica.energymeters;

import com.vladmarica.energymeters.integration.ComputerCraftIntegration;
import com.vladmarica.energymeters.integration.ModIDs;
import com.vladmarica.energymeters.network.Packets;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterEU;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterFE;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterMJ;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
  public void preInit(FMLPreInitializationEvent event) { }

  public void init(FMLInitializationEvent event) {
    // Register tile entities
    GameRegistry.registerTileEntity(
        TileEntityEnergyMeterFE.class,
        new ResourceLocation(EnergyMetersMod.MODID, "te_energy_meter_fe"));
    GameRegistry.registerTileEntity(
        TileEntityEnergyMeterMJ.class,
        new ResourceLocation(EnergyMetersMod.MODID, "te_energy_meter_mj"));
    GameRegistry.registerTileEntity(
        TileEntityEnergyMeterEU.class,
        new ResourceLocation(EnergyMetersMod.MODID, "te_energy_meter_eu"));

    // Register network packets
    Packets.register();
  }

  public void postInit(FMLPostInitializationEvent event) {
    if (Loader.isModLoaded(ModIDs.COMPUTERCRAFT)) {
      ComputerCraftIntegration.apply();
    }
  }

  public boolean handleEnergyBlockActivation(World world, BlockPos pos, EntityPlayer player) {
    return true;
  }
}
