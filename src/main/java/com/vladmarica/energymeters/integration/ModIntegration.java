package com.vladmarica.energymeters.integration;

import com.vladmarica.energymeters.EnergyMetersMod;
import net.minecraftforge.fml.common.Loader;

public class ModIntegration {
  public static void checkInstalledMods() {
    debugPrintModStatus(ModIDs.OPENCOMPUTERS);
    debugPrintModStatus(ModIDs.COMPUTERCRAFT);
    debugPrintModStatus(ModIDs.BUILDCRAFT);
    debugPrintModStatus(ModIDs.IC2);
  }

  private static void debugPrintModStatus(String modid) {
    boolean loaded = Loader.isModLoaded(modid);
    EnergyMetersMod.LOGGER.debug("Mod {} is {} loaded", modid, loaded ? "" : "NOT");
  }
}
