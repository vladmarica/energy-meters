package com.vladmarica.energymeters.integration;

import com.vladmarica.energymeters.EnergyMetersMod;
import dan200.computercraft.api.ComputerCraftAPI;

public class ComputerCraftIntegration {
  public static void apply() {
    ComputerCraftAPI.registerPeripheralProvider(new ComputerCraftPeripheralProvider());
    EnergyMetersMod.LOGGER.info("Applied ComputerCraft integration");
  }
}
