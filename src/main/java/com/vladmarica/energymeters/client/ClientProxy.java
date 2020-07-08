package com.vladmarica.energymeters.client;

import com.google.common.collect.Iterators;
import com.vladmarica.energymeters.CommonProxy;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.Blocks;
import com.vladmarica.energymeters.client.gui.EnergyMeterScreen;
import com.vladmarica.energymeters.client.model.EnergyMeterBakedModel;
import com.vladmarica.energymeters.client.model.TexturedQuadCache;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends CommonProxy {

  private static Random tempRandom = new Random();

  @Override
  public void init(FMLCommonSetupEvent event) {
    super.init(event);
    FMLJavaModLoadingContext.get().getModEventBus().register(this);
    ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyMeterBase.class, new EnergyMeterScreenRenderer());
  }

  @Override
  public boolean handleEnergyBlockActivation(World world, BlockPos pos, PlayerEntity player) {
    if (!world.isRemote) {
      return true;
    }

    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileEntityEnergyMeterBase) {
      Minecraft.getInstance().displayGuiScreen(new EnergyMeterScreen((TileEntityEnergyMeterBase) tile));
      return true;
    }

    return false;
  }

  @SubscribeEvent
  public void onModelBaking(ModelBakeEvent event) {
    Map<Direction, BakedQuad> cubeQuadMap = new HashMap<>();
    Map<ResourceLocation, TextureAtlasSprite> spriteMap = new HashMap<>();

    overrideModel(event, Blocks.ENERGY_METER_FE, cubeQuadMap, spriteMap);

    // Store cube quads and textures in the cache
    TexturedQuadCache.INSTANCE.setCubeQuadMap(cubeQuadMap);
    TexturedQuadCache.INSTANCE.setTextureMap(spriteMap);
  }

  private static void overrideModel(ModelBakeEvent event, BlockEnergyMeter block, Map<Direction, BakedQuad> cubeQuadMap, Map<ResourceLocation, TextureAtlasSprite> spriteMap) {
    for (BlockState state : block.getStateContainer().getValidStates()) {
      ModelResourceLocation modelLocation = BlockModelShapes.getModelLocation(state);
      IBakedModel originalModel = event.getModelRegistry().get(modelLocation);

      // Extract textured quads from default model
      for (Direction side : Direction.values()) {
        List<BakedQuad> quads = originalModel.getQuads(state, side, tempRandom, EmptyModelData.INSTANCE);
        BakedQuad quad = Iterators.getOnlyElement(quads.iterator());

        ResourceLocation spriteLocation = quad.getSprite().getName();
        if (!spriteLocation.getPath().equals("missingno") && !spriteMap.containsKey(spriteLocation)) {
          spriteMap.put(spriteLocation, quad.getSprite());
          EnergyMetersMod.LOGGER.debug("Side {} has texture {}", side.getName(), quad.getSprite());
        }

        cubeQuadMap.put(side, quad);
      }

      event.getModelRegistry().put(modelLocation, new EnergyMeterBakedModel(originalModel));
    }
  }
}
