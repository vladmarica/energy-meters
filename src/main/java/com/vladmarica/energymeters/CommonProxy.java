package com.vladmarica.energymeters;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy {

  public void init(FMLCommonSetupEvent event) {
    EnergyMetersMod.LOGGER.info("CommonProxy init");
  }

  /*
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
  */
}
