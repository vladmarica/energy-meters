package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.energy.EnergyType.EnergyAlias;
import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonEnergyAlias extends Button implements IHasTooltip {
  private static final int SIZE = 20;

  private EnergyAlias alias;

  public GuiButtonEnergyAlias(int x, int y, EnergyAlias alias, IPressable onPress) {
    super(x, y, SIZE, SIZE, alias.getDisplayName(), onPress);
    this.alias = alias;

    if (alias.getEnergyType().getAliases().size() == 1) {
      this.active = false;
    }
  }

  public EnergyAlias cycle() {
    EnergyType type = this.alias.getEnergyType();
    List<EnergyAlias> aliasList = type.getAliases();

    do {
      int newIndex = (this.alias.getIndex() + 1) % aliasList.size();
      this.alias = aliasList.get(newIndex);
    } while (!this.alias.isAvailable());

    this.setMessage(this.alias.getDisplayName());
    return this.alias;
  }

  public EnergyAlias getAlias() {
    return this.alias;
  }

  @Override
  public List<String> getTooltipLines() {
    return ImmutableList.of(
        "Display Units",
        TextFormatting.GRAY + this.alias.getDisplayName() + " (" + this.alias.getDescription() + ")");
  }
}
