package com.vladmarica.energymeters.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public class Sprite {
  private static final int DEFAULT_WIDTH = 16;
  private static final int DEFAULT_HEIGHT = 16;
  private static final int SPRITEMAP_SIZE = 16;

  private ResourceLocation texture;
  private int index;
  private int textureX;
  private int textureY;

  public Sprite(ResourceLocation texture, int index) {
    this.texture = texture;
    this.index = index;
    this.textureX = this.index % SPRITEMAP_SIZE * DEFAULT_WIDTH;
    this.textureY = this.index / SPRITEMAP_SIZE * DEFAULT_HEIGHT;
  }

  public void render(Screen gui, int x, int y) {
    gui.getMinecraft().getTextureManager().bindTexture(this.texture);
    gui.blit(x, y, textureX, textureY, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }
}
