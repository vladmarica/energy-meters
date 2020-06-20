package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.tile.config.IConfigEnum;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonConfigEnum<T extends Enum<T> & IConfigEnum> extends GuiButton {
  private static final int SIZE = 20;
  private T value;
  private List<T> possibleValues;
  private String title;

  public GuiButtonConfigEnum(int index, String title, int x, int y, Class<T> enumClass, T value) {
    super(index, x, y, SIZE, SIZE, "");
    this.value = value;
    this.possibleValues = ImmutableList.copyOf(enumClass.getEnumConstants());
    this.title = title;

    if (this.value.getIcon() != null) {
      this.displayString = this.value.getDisplayName();
    }
  }

  public T cycle() {
    int newOrdinal = (value.ordinal() + 1) % possibleValues.size();
    this.value = possibleValues.get(newOrdinal);
    this.displayString = this.value.getIcon() != null ? this.value.getDisplayName() : "";
    return this.value;
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    super.drawButton(mc, mouseX, mouseY, partialTicks);
    if (value.getIcon() != null) {
      value.getIcon().render(mc.currentScreen, this.x + 2, this.y + 2);
    }
  }

  public void drawTooltip(GuiScreen gui, int mouseX, int mouseY) {
    gui.drawHoveringText(
        ImmutableList.of(this.title, TextFormatting.GRAY + this.value.getDescription()),
        mouseX,
        mouseY);
  }
}
