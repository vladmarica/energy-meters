package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.client.model.EnergyMeterBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBase extends Block {
  public BlockBase(Material material, String name) {
    super(material);

    setRegistryName(name);
    setUnlocalizedName(String.format("%s.%s", EnergyMetersMod.MODID, name));

    setHardness(1F);
    setCreativeTab(CreativeTabs.TOOLS);
  }

  /*
  @SideOnly(Side.CLIENT)
  public void registerModel() {
    ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
      @Override
      protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return EnergyMeterBakedModel.BAKED_MODEL_LOCATION;
      }
    });
  }
  */

  @Override
  public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
    return super.getWeakChanges(world, pos);
  }

  @SideOnly(Side.CLIENT)
  public void registerItemModel(Item item) {
    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
}
