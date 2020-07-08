package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.tile.config.IConfigEnum;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonConfigEnum<T extends Enum<T> & IConfigEnum> extends GuiIconButton implements
    IHasTooltip {
  private T value;
  private List<T> possibleValues;
  private String title;

  public GuiButtonConfigEnum(String title, int x, int y, Class<T> enumClass, T value, IPressable onPress) {
    super(x, y, null, onPress);
    this.value = value;
    this.possibleValues = ImmutableList.copyOf(enumClass.getEnumConstants());
    this.title = title;

    if (this.value.getIcon() == null) {
      this.setMessage(this.value.getDisplayName());
    }

    this.setIcon(this.value.getIcon());
  }

  public T cycle() {
    int newOrdinal = (value.ordinal() + 1) % possibleValues.size();
    this.value = possibleValues.get(newOrdinal);
    this.setMessage(this.value.getIcon() == null ? this.value.getDisplayName() : "");
    this.setIcon(this.value.getIcon());
    return this.value;
  }

  @Override
  public List<String> getTooltipLines() {
    return ImmutableList.of(this.title, TextFormatting.GRAY + this.value.getDescription());
  }
}
