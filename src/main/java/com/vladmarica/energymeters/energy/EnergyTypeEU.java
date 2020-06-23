package com.vladmarica.energymeters.energy;

import com.vladmarica.energymeters.integration.ModIDs;
import net.minecraftforge.fml.common.Loader;

public class EnergyTypeEU extends EnergyType {
  EnergyTypeEU() {
    super("EU", "Energy Unit");
  }

  @Override
  public boolean isAvailable() {
    return Loader.isModLoaded(ModIDs.IC2);
  }
}
