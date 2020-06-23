package com.vladmarica.energymeters.client.gui;

import com.vladmarica.energymeters.client.Sprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiIconButton extends GuiButton {
  public static final int SIZE = 20;

  private Sprite icon;

  public GuiIconButton(int index, int x, int y, Sprite icon) {
    super(index, x, y, SIZE, SIZE, "");
    this.icon = icon;
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    super.drawButton(mc, mouseX, mouseY, partialTicks);
    if (icon != null) {
      icon.render(mc.currentScreen, this.x + 2, this.y + 2);
    }
  }

  public void setIcon(Sprite icon) {
    this.icon = icon;
  }
}
