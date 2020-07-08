package com.vladmarica.energymeters.client.gui;

import com.vladmarica.energymeters.client.Sprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class GuiIconButton extends Button {
  public static final int SIZE = 20;

  private Sprite icon;

  public GuiIconButton(int x, int y, Sprite icon, IPressable onPress) {
    super(x, y, SIZE, SIZE, "", onPress);
    this.icon = icon;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);
    if (icon != null) {
      icon.render(Minecraft.getInstance().currentScreen, this.x + 2, this.y + 2);
    }
  }

  public void setIcon(Sprite icon) {
    this.icon = icon;
  }
}
