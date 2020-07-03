package com.vladmarica.energymeters.client.gui;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.energy.EnergyType.EnergyAlias;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonEnergyAlias extends GuiButton implements IHasTooltip {
  private static final int SIZE = 20;

  private EnergyAlias alias;

  public GuiButtonEnergyAlias(int buttonId, int x, int y, EnergyAlias alias) {
    super(buttonId, x, y, SIZE, SIZE, alias.getDisplayName());
    this.alias = alias;

    if (alias.getEnergyType().getAliases().size() == 1) {
      this.enabled = false;
    }
  }

  public EnergyAlias cycle() {
    EnergyType type = this.alias.getEnergyType();
    List<EnergyAlias> aliasList = type.getAliases();

    do {
      int newIndex = (this.alias.getIndex() + 1) % aliasList.size();
      this.alias = aliasList.get(newIndex);
    } while (!this.alias.isAvailable());

    this.displayString = this.alias.getDisplayName();
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
