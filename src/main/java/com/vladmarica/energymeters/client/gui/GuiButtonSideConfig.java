package com.vladmarica.energymeters.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.util.ResourceLocation;

public class GuiButtonSideConfig extends AbstractGui implements IRenderable {
  private static final int WIDTH = 16;
  private static final int HEIGHT = 16;

  private ResourceLocation texture;
  private RelativeBlockSide side;
  private int x;
  private int y;
  private boolean hover = false;
  private boolean disabled = false;
  private Minecraft mc = Minecraft.getInstance();

  public GuiButtonSideConfig(int x, int y, RelativeBlockSide side, ResourceLocation texture) {
    this(x, y, side, texture, false);
  }

  public GuiButtonSideConfig(int x, int y, RelativeBlockSide side, ResourceLocation texture, boolean disabled) {
    this.texture = texture;
    this.side = side;
    this.x = x;
    this.y = y;
    this.disabled = disabled;
  }

  public void render(int mouseX, int mouseY, float partialTicks) {
    this.hover = mouseX >= this.x && mouseX <= this.x + WIDTH
        && mouseY >= this.y && mouseY <= this.y + HEIGHT;

    if (this.hover && !this.disabled) {
      fill(this.x - 1, this.y - 1, this.x + WIDTH + 1, this.y + HEIGHT + 1, 0xFFFFFFFF);
    }

    this.mc.getTextureManager().bindTexture(this.texture);
    blit(this.x, this.y, 0, 0, WIDTH, HEIGHT, 16, 16);
  }

  public boolean isMouseHovered() {
    return this.hover;
  }

  public RelativeBlockSide getSide() {
    return this.side;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  public boolean isDisabled() {
    return this.disabled;
  }

  public void setTexture(ResourceLocation texture) {
    this.texture = texture;
  }
}
