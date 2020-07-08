package com.vladmarica.energymeters.client.model;

import com.vladmarica.energymeters.EnergyMetersMod;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuadRetextured;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedQuadCache {
  public static final TexturedQuadCache INSTANCE = new TexturedQuadCache();

  private Map<ResourceLocation, TextureAtlasSprite> spriteMap;
  private Map<Direction, BakedQuad> cubeQuadMap;
  private Map<Direction, Map<ResourceLocation, BakedQuadRetextured>> texturedQuadCache = new HashMap<>();

  public BakedQuadRetextured getBakedQuad(Direction side, ResourceLocation texture) {
    // Check cache first
    if (this.texturedQuadCache.get(side).containsKey(texture)) {
      EnergyMetersMod.LOGGER.debug("TexturedQuadCache hit for {}-{}", side.getName(), texture);
      return this.texturedQuadCache.get(side).get(texture);
    }

    // In the cache of cache miss, build and store the requested retextured quad
    EnergyMetersMod.LOGGER.debug("TexturedQuadCache miss for {}-{}", side.getName(), texture);

    BakedQuadRetextured retexturedQuad = buildRetexturedQuad(side, texture);
    this.texturedQuadCache.get(side).put(texture, retexturedQuad);
    return retexturedQuad;
  }

  private BakedQuadRetextured buildRetexturedQuad(Direction side, ResourceLocation texture) {
    BakedQuad originalQuad = cubeQuadMap.get(side);
    TextureAtlasSprite sprite = spriteMap.get(texture);
    return new BakedQuadRetextured(originalQuad, sprite);
  }

  public void setTextureMap(Map<ResourceLocation, TextureAtlasSprite> spriteMap) {
    this.spriteMap = spriteMap;
  }

  public void setCubeQuadMap(Map<Direction, BakedQuad> cubeQuadMap) {
    if (cubeQuadMap.size() != 6) {
      throw new RuntimeException(
          String.format("Cube quad map has %d quads, expected 6", cubeQuadMap.size()));
    }

    this.cubeQuadMap = cubeQuadMap;
    for (Direction side : this.cubeQuadMap.keySet()) {
      this.texturedQuadCache.put(side, new HashMap<>());
    }
  }

  private TexturedQuadCache() {}
}
