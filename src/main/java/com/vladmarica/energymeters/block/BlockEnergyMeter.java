package com.vladmarica.energymeters.block;

import com.google.common.collect.ImmutableList;
import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.energy.EnergyTypes;
import com.vladmarica.energymeters.properties.UnlistedPropertyBoolean;
import com.vladmarica.energymeters.properties.UnlistedPropertyFacing;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterEU;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterFE;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterMJ;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnergyMeter extends BlockBase {

  public static final String NAME = "meter";
  public static final PropertyDirection PROP_FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static final PropertyEnum<MeterType> PROP_TYPE = PropertyEnum.create("type", MeterType.class);

  public static final UnlistedPropertyFacing PROP_INPUT = UnlistedPropertyFacing.create("input");
  public static final UnlistedPropertyFacing PROP_OUTPUT = UnlistedPropertyFacing.create("output");
  public static final UnlistedPropertyBoolean PROP_CONNECTED = UnlistedPropertyBoolean.create("connected");

  public BlockEnergyMeter() {
    super(Material.IRON, NAME);

    this.setSoundType(SoundType.METAL);

    this.setDefaultState(blockState.getBaseState()
        .withProperty(PROP_FACING, EnumFacing.NORTH)
        .withProperty(PROP_TYPE, MeterType.FE_METER));

    this.setHarvestLevel("pickaxe", 0);
    this.setCreativeTab(CreativeTabs.SEARCH);
  }

  @Override
  public TileEntity createTileEntity(World world, IBlockState state) {
    MeterType type = state.getValue(PROP_TYPE);

    switch (type) {
      case FE_METER:
        return new TileEntityEnergyMeterFE();
      case MJ_METER:
        return new TileEntityEnergyMeterMJ();
      case EU_METER:
        return new TileEntityEnergyMeterEU();
      default:
        EnergyMetersMod.LOGGER.error("Attempted to create tile entity for invalid type {}", type.getName());
    }

    return null;
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn,
      BlockPos fromPos) {
    super.neighborChanged(state, world, pos, blockIn, fromPos);

    TileEntityEnergyMeterBase tile = (TileEntityEnergyMeterBase) world.getTileEntity(pos);
    if (tile != null) {
      tile.onNeighborChanged(fromPos, state);
    }
  }

  @Override
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
      float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    MeterType meterType = MeterType.values()[meta];
    return this.getDefaultState().withProperty(PROP_TYPE, meterType);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    int facingIndex = meta & 0b11;
    int typeIndex = (meta & 0b1100) >> 2;
    return getDefaultState()
        .withProperty(PROP_FACING, EnumFacing.getHorizontal(facingIndex))
        .withProperty(PROP_TYPE, MeterType.values()[typeIndex]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    int facingIndex = state.getValue(PROP_FACING).getHorizontalIndex();
    int typeIndex = state.getValue(PROP_TYPE).getIndex();
    return facingIndex | (typeIndex << 2);
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
      EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    return EnergyMetersMod.PROXY.handleEnergyBlockActivation(world, pos, player);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new ExtendedBlockState(
        this,
        new IProperty[] { PROP_FACING, PROP_TYPE },
        new IUnlistedProperty[] { PROP_INPUT, PROP_OUTPUT, PROP_CONNECTED });
  }

  @Override
  public IExtendedBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    IExtendedBlockState ext = (IExtendedBlockState) state;
    TileEntityEnergyMeterBase tile = (TileEntityEnergyMeterBase) world.getTileEntity(pos);

    if (tile != null) {
      ext = ext.withProperty(PROP_INPUT, tile.getInputSide())
          .withProperty(PROP_OUTPUT, tile.getOutputSide())
          .withProperty(PROP_CONNECTED, tile.isFullyConnected());
    }

    return ext;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    world.setBlockState(pos, state.withProperty(PROP_FACING, getFacingFromEntity(pos, placer)), 2);
  }

  public static EnumFacing getFacingFromEntity(BlockPos clickedBlock, EntityLivingBase entity) {
    EnumFacing facing =  EnumFacing.getFacingFromVector(
        (float) (entity.posX - clickedBlock.getX()),
        (float) (entity.posY - clickedBlock.getY()),
        (float) (entity.posZ - clickedBlock.getZ()));

    if (facing.getAxis() == EnumFacing.Axis.Y) {
      facing = EnumFacing.NORTH;
    }

    return facing;
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX,
      float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
  }

  @Override
  public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos,
      @Nullable EnumFacing side) {
    return side != null && side != state.getValue(PROP_FACING).getOpposite();
  }

  @Override
  public void getSubBlocks(CreativeTabs tabs, NonNullList<ItemStack> items) {
    for (MeterType type : MeterType.values()) {
      if (type.getEnergyType().isAvailable()) {
        items.add(new ItemStack(this, 1, type.getIndex()));
      }
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    MeterType type = state.getValue(PROP_TYPE);
    return type.getIndex();
  }

  @SideOnly(Side.CLIENT)
  public void registerItemModel(Item item) {
    for (MeterType type : MeterType.values()) {
      ModelLoader.setCustomModelResourceLocation(item, type.getIndex(),
          new ModelResourceLocation(
              getRegistryName(), "inventory_" + type.getName()));
    }
  }

  public enum MeterType implements IStringSerializable  {
    FE_METER(0, EnergyTypes.FE),
    MJ_METER(1, EnergyTypes.MJ),
    EU_METER(2, EnergyTypes.EU);

    private int index;
    private EnergyType type;

    MeterType(int index, EnergyType type) {
      this.index = index;
      this.type = type;
    }

    public int getIndex() {
      return this.index;
    }

    public EnergyType getEnergyType() {
      return this.type;
    }

    @Override
    public String getName() {
      return this.type.getName().toLowerCase();
    }
  }
}
