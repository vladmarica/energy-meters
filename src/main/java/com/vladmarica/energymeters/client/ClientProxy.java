package com.vladmarica.energymeters.client;

import com.vladmarica.energymeters.CommonProxy;
import com.vladmarica.energymeters.EnergyMetersMod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends CommonProxy {

  @Override
  public void init(FMLCommonSetupEvent event) {
    super.init(event);
    EnergyMetersMod.LOGGER.info("ClientProxy init");
  }
}
