package com.vladmarica.energymeters.client;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.client.Sprite;
import net.minecraft.util.ResourceLocation;

public class Sprites {
  public static final ResourceLocation SPRITE_MAP_TEXTURE = new ResourceLocation(
      EnergyMetersMod.MODID, "textures/gui/spritemap.png");

  public static final Sprite REDSTONE_DISABLED = new Sprite(SPRITE_MAP_TEXTURE, 0);
  public static final Sprite REDSTONE_ACTIVE = new Sprite(SPRITE_MAP_TEXTURE, 1);
  public static final Sprite REDSTONE_INVERTED = new Sprite(SPRITE_MAP_TEXTURE, 2);
  public static final Sprite SPECTRAL_ARROW = new Sprite(SPRITE_MAP_TEXTURE, 3);
}
