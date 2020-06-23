package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.tile.config.IConfigEnum;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonConfigEnum<T extends Enum<T> & IConfigEnum> extends GuiIconButton implements IHasTooltip {
  private T value;
  private List<T> possibleValues;
  private String title;

  public GuiButtonConfigEnum(int index, String title, int x, int y, Class<T> enumClass, T value) {
    super(index, x, y, null);
    this.value = value;
    this.possibleValues = ImmutableList.copyOf(enumClass.getEnumConstants());
    this.title = title;

    if (this.value.getIcon() == null) {
      this.displayString = this.value.getDisplayName();
    }

    this.setIcon(this.value.getIcon());
  }

  public T cycle() {
    int newOrdinal = (value.ordinal() + 1) % possibleValues.size();
    this.value = possibleValues.get(newOrdinal);
    this.displayString = this.value.getIcon() == null ? this.value.getDisplayName() : "";
    this.setIcon(this.value.getIcon());
    return this.value;
  }

  @Override
  public List<String> getTooltipLines() {
    return ImmutableList.of(this.title, TextFormatting.GRAY + this.value.getDescription());
  }
}
