package com.vladmarica.energymeters.energy;

import com.vladmarica.energymeters.integration.ModIDs;
import net.minecraftforge.fml.ModList;

public class EnergyTypeFE extends EnergyType {
  EnergyTypeFE() {
    super("FE", "Forge Energy");
    this.addAlias("RF", "Redstone Flux");
    this.addAlias("µI", "Micro Infinity", () -> ModList.get().isLoaded(ModIDs.ENDERIO));
    this.addAlias("IF", "Immersive Flux", () -> ModList.get().isLoaded(ModIDs.IMMERSIVE_ENGINEERING));
  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public boolean isLimitable() {
    return true;
  }
}
