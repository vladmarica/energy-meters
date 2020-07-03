package com.vladmarica.energymeters.energy;

import com.vladmarica.energymeters.integration.ModIDs;
import net.minecraftforge.fml.common.Loader;

public class EnergyTypeFE extends EnergyType {
  EnergyTypeFE() {
    super("FE", "Forge Energy");
    this.addAlias("RF", "Redstone Flux");
    this.addAlias("µI", "Micro Infinity", () -> Loader.isModLoaded(ModIDs.ENDERIO));
    this.addAlias("IF", "Immersive Flux", () -> Loader.isModLoaded(ModIDs.IMMERSIVE_ENGINEERING));
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
