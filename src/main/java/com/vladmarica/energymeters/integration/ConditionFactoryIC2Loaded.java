package com.vladmarica.energymeters.integration;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

public class ConditionFactoryIC2Loaded implements IConditionFactory {

  @Override
  public BooleanSupplier parse(JsonContext context, JsonObject json) {
    return () -> Loader.isModLoaded(ModIDs.IC2);
  }
}
