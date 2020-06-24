package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.energy.EnergyType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemBlockEnergyMeter extends ItemBlock {
  public ItemBlockEnergyMeter(BlockEnergyMeter block) {
    super(block);
    this.setHasSubtypes(true);
    this.setMaxDamage(0);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
      ITooltipFlag flagIn) {
    EnergyType type = MeterType.values()[stack.getMetadata()].getEnergyType();
    tooltip.add("Supports " + type.getAliasesDisplayString());

    if (!type.isAvailable()) {
      tooltip.add(TextFormatting.RED + "Cannot be placed because a required mod is missing");
    }
  }

  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
      EnumFacing facing, float hitX, float hitY, float hitZ) {

    // Check if the meter type for this block is available. If not, do not allow placement.
    ItemStack itemstack = player.getHeldItem(hand);
    if (!itemstack.isEmpty()) {
      MeterType type = MeterType.values()[this.getMetadata(itemstack.getMetadata())];
      if (!type.getEnergyType().isAvailable()) {
        return EnumActionResult.FAIL;
      }
    }

    return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
  }

  @Override
  public int getMetadata(int meta) {
    return meta;
  }
}
