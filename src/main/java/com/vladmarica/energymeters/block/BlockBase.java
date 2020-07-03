package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.EnergyMetersMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockBase extends Block {
  public BlockBase(Material material, String name) {
    super(material);

    this.setRegistryName(name);
    this.setTranslationKey(String.format("%s.%s", EnergyMetersMod.MODID, name));
    this.setHardness(1F);
    this.setCreativeTab(EnergyMetersMod.CREATIVE_TAB);
  }
}
