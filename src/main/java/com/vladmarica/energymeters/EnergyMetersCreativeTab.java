package com.vladmarica.energymeters;

import com.vladmarica.energymeters.block.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnergyMetersCreativeTab extends CreativeTabs {
  public EnergyMetersCreativeTab() {
    super(EnergyMetersMod.MODID);
  }

  @Override
  public ItemStack createIcon() {
    return new ItemStack(Item.getItemFromBlock(Blocks.ENERGY_METER));
  }
}
