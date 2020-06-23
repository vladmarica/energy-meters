package com.vladmarica.energymeters.client;

import com.google.common.collect.Iterators;
import com.vladmarica.energymeters.CommonProxy;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.block.Blocks;
import com.vladmarica.energymeters.client.gui.GuiEnergyMeter;
import com.vladmarica.energymeters.client.model.EnergyMeterBakedModel;
import com.vladmarica.energymeters.client.model.TexturedQuadCache;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

  @Override
  public void preInit(FMLPreInitializationEvent event) {
    super.preInit(event);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    ClientRegistry.bindTileEntitySpecialRenderer(
        TileEntityEnergyMeterBase.class, new EnergyMeterScreenRenderer());
  }

  @Override
  public void postInit(FMLPostInitializationEvent event) {
    super.postInit(event);
  }

  @Override
  public boolean handleEnergyBlockActivation(World world, BlockPos pos, EntityPlayer player) {
    if (!world.isRemote) {
      return true;
    }

    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileEntityEnergyMeterBase) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiEnergyMeter((TileEntityEnergyMeterBase) tile));
      return true;
    }

    return false;
  }

  /**
   * Listener for the {@link ModelRegistryEvent} client-side event. All block item models are
   * registered here.
   */
  @SubscribeEvent
  public void onRegisterModels(ModelRegistryEvent event) {
    Blocks.ENERGY_METER.registerItemModel(Item.getItemFromBlock(Blocks.ENERGY_METER));
  }

  @SubscribeEvent
  public void onModelBaking(ModelBakeEvent event) {
    Map<IBlockState, ModelResourceLocation> variantToModelMap = event.getModelManager()
        .getBlockModelShapes()
        .getBlockStateMapper()
        .getVariants(Blocks.ENERGY_METER);

    // Find one blockstate for each meter type (doesn't matter which one)
    Map<MeterType, IBlockState> meterTypeToStateMap = new HashMap<>();
    for (IBlockState state : variantToModelMap.keySet()) {
      MeterType type = state.getValue(BlockEnergyMeter.PROP_TYPE);
      if (!meterTypeToStateMap.containsKey(type)) {
        meterTypeToStateMap.put(type, state);
      }
    }


    Map<EnumFacing, BakedQuad> cubeQuadMap = new HashMap<>();
    Map<ResourceLocation, TextureAtlasSprite> spriteMap = new HashMap<>();
    IBakedModel originalModel = null;

    // Extract textured quads from default model
    for (Map.Entry<MeterType, IBlockState> meterTypeStatePair : meterTypeToStateMap.entrySet()) {
      IBlockState state = meterTypeStatePair.getValue();
      ModelResourceLocation modelLocation = variantToModelMap.get(state);
      IBakedModel model = event.getModelRegistry().getObject(modelLocation);

      if (originalModel == null) {
        originalModel = model;
      }

      EnergyMetersMod.LOGGER.debug("Extracting textures for meter type {}", meterTypeStatePair.getKey());

      for (EnumFacing side : EnumFacing.values()) {
        List<BakedQuad> quads = model.getQuads(state, side, 0);
        BakedQuad quad = Iterators.getOnlyElement(quads.iterator());
        EnergyMetersMod.LOGGER.debug("Side {} has texture {}", side.getName(), quad.getSprite());

        ResourceLocation spriteLocation = new ResourceLocation(quad.getSprite().getIconName());
        if (!spriteMap.containsKey(spriteLocation)) {
          spriteMap.put(spriteLocation, quad.getSprite());
        }

        cubeQuadMap.put(side, quad);
      }
    }

    // Store cube quads and textures in the cache
    TexturedQuadCache.INSTANCE.setCubeQuadMap(cubeQuadMap);
    TexturedQuadCache.INSTANCE.setTextureMap(spriteMap);

    // Override the energy meter models with the custom baked model
    for (Map.Entry<IBlockState, ModelResourceLocation> entry : variantToModelMap.entrySet()) {
      event.getModelRegistry().putObject(entry.getValue(), new EnergyMeterBakedModel(originalModel));
    }
  }
}
