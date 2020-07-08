package com.vladmarica.energymeters.client.model;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class EnergyMeterBakedModel implements IDynamicBakedModel {
  public static final ModelProperty<Direction> MODEL_PROP_INPUT_SIDE = new ModelProperty<>();
  public static final ModelProperty<Direction> MODEL_PROP_OUTPUT_SIDE = new ModelProperty<>();

  private IBakedModel originalModel;

  public EnergyMeterBakedModel(IBakedModel originalModel) {
    this.originalModel = originalModel;
  }

  @Nonnull
  @Override
  public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos,
      @Nonnull BlockState state, @Nonnull IModelData modelData) {
    //modelData.setData(MODEL_PROP_INPUT_SIDE, Direction.DOWN);
    //modelData.setData(MODEL_PROP_OUTPUT_SIDE, Direction.UP);
    return modelData;
  }

  @Nonnull
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
      @Nonnull Random rand, @Nonnull IModelData extraData) {
    if (side == null) {
      return ImmutableList.of();
    }

    if (state != null) {
      Direction facing = state.get(BlockEnergyMeter.PROP_FACING);
      Direction inputSide = extraData.getData(MODEL_PROP_INPUT_SIDE);
      Direction outputSide = extraData.getData(MODEL_PROP_OUTPUT_SIDE);
      MeterType type = ((BlockEnergyMeter) state.getBlock()).getMeterType();

      if (side == facing) {
        return ImmutableList.of(
            TexturedQuadCache.INSTANCE.getBakedQuad(side, TextureLocations.getScreenTexture(type)));
      }

      if (side == inputSide) {
        return ImmutableList.of(
            TexturedQuadCache.INSTANCE.getBakedQuad(side, TextureLocations.getInputTexture(type)));
      }

      if (side == outputSide) {
        return ImmutableList.of(
            TexturedQuadCache.INSTANCE.getBakedQuad(side, TextureLocations.getOutputTexture(type)));
      }

      return ImmutableList.of(
          TexturedQuadCache.INSTANCE.getBakedQuad(side, TextureLocations.getSideTexture(type)));
    }

    return ImmutableList.of(
        TexturedQuadCache.INSTANCE.getBakedQuad(side, TextureLocations.getSideTexture(
            MeterType.FE_METER))
    );
  }

  @Override
  public boolean isAmbientOcclusion() {
    return true;
  }

  @Override
  public boolean isGui3d() {
    return false;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return this.originalModel.getParticleTexture(EmptyModelData.INSTANCE);
  }

  @Override
  public ItemOverrideList getOverrides() {
    return null;
  }
}
