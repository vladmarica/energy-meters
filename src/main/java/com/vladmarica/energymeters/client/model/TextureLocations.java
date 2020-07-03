package com.vladmarica.energymeters.client.model;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public class TextureLocations {
  private static final Map<MeterType, ResourceLocation> SIDE_TEXTURES = new HashMap<>();
  private static final Map<MeterType, ResourceLocation> INPUT_TEXTURES = new HashMap<>();
  private static final Map<MeterType, ResourceLocation> OUTPUT_TEXTURES = new HashMap<>();
  private static final Map<MeterType, ResourceLocation> SCREEN_TEXTURES = new HashMap<>();

  static {
    for (MeterType meterType : MeterType.values()) {
      String suffix = meterType != MeterType.FE_METER ? "_" + meterType.getName() : "";
      SIDE_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "blocks/meter" + suffix));
      INPUT_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "blocks/meter_input" + suffix));
      OUTPUT_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "blocks/meter_output" + suffix));
      SCREEN_TEXTURES.put(meterType, new ResourceLocation(EnergyMetersMod.MODID, "blocks/meter_screen" + suffix));
    }
  }

  public static ResourceLocation getSideTexture(MeterType type) {
    return SIDE_TEXTURES.get(type);
  }

  public static ResourceLocation getInputTexture(MeterType type) {
    return INPUT_TEXTURES.get(type);
  }

  public static ResourceLocation getOutputTexture(MeterType type) {
    return OUTPUT_TEXTURES.get(type);
  }

  public static ResourceLocation getScreenTexture(MeterType type) {
    return SCREEN_TEXTURES.get(type);
  }

  public static ResourceLocation getGuiResource(ResourceLocation location) {
    String path = location.getPath();
    return new ResourceLocation(location.getNamespace(), String.format("textures/%s.png", path));
  }
}
