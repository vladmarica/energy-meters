package com.vladmarica.energymeters.tile;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.MovingAverage;
import com.vladmarica.energymeters.Util;
import com.vladmarica.energymeters.block.BlockEnergyMeter;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.block.Blocks;
import com.vladmarica.energymeters.client.model.EnergyMeterBakedModel;
import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.energy.EnergyType.EnergyAlias;
import com.vladmarica.energymeters.energy.IEnergyMeter;
import com.vladmarica.energymeters.network.BufferUtil;
import com.vladmarica.energymeters.network.PacketEnergyTransferRate;
import com.vladmarica.energymeters.tile.config.EnumRedstoneControlState;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class TileEntityEnergyMeterBase extends TileEntity implements ITickableTileEntity,
    IEnergyMeter {

  /**
   * The maximum distance in blocks from this tile entity that a player has to be to receive
   * an update packet.
   */
  protected static final double PACKET_RANGE = 32;

  /**
   * The maximum interval in ticks between {@link PacketEnergyTransferRate} packets that tile entity
   * sends out.
   */
  protected static final double UPDATE_PACKET_MAX_TICK_INTERVAL = 120;

  /**
   * The minimum interval in ticks between {@link PacketEnergyTransferRate} packets that tile entity
   * sends out. For example, if this value is {@code 4}, this tile entity will wait at least
   * {@code 4} ticks between packets.
   */
  protected static final double UPDATE_PACKET_MIN_TICK_INTERVAL = 4;

  /** The name of this component as it appears to mods like OpenComputers and ComputerCraft. */
  protected static final String COMPUTER_COMPONENT_NAME = "energy-meter";

  public static final int UNLIMITED_RATE = -1;

  protected static final String NBT_CONNECTED_KEY = "connected";
  protected static final String NBT_POWERED_KEY = "powered";
  protected static final String NBT_INPUT_SIDE_KEY = "input-side";
  protected static final String NBT_OUTPUT_SIDE_KEY = "output-side";
  protected static final String NBT_TOTAL_ENERGY_TRANSFERRED_KEY = "total-energy-transferred";
  protected static final String NBT_REDSTONE_CONTROL_STATE = "redstone-control-state";
  protected static final String NBT_ENERGY_ALIAS = "energy-alias";
  protected static final String NBT_RATE_LIMIT_KEY = "rate-limit";

  protected static final String NBT_OWNER_UUID = "owner-uuid";
  protected static final String NBT_OWNER_USERNAME = "owner-username";

  protected int ticks = 0;
  protected long totalEnergyTransferred = 0;
  protected long totalEnergyTransferredLastTick = 0;
  protected float transferRate = 0;
  protected float lastTransferRateSent = 0;
  protected int ticksSinceLastTransferRatePacket = 0;

  protected EnergyType energyType;
  protected boolean loadedFromDisk = false;

  private boolean hadFirstTick = false;

  /**
   * True if this meter is fully connected, meaning that it has a valid {@link IEnergyStorage}
   * connected to the input and output sides.
   */
  protected boolean fullyConnected = false;

  /** True if this tile entity's block is currently receiving redstone power */
  protected boolean powered = false;

  protected MovingAverage transferRateMovingAverage = new MovingAverage(10);

  protected Direction screenSide = Direction.NORTH;

  @Nullable
  protected Direction inputSide;

  @Nullable
  protected Direction outputSide;

  protected EnumRedstoneControlState redstoneControlState = EnumRedstoneControlState.ACTIVE;
  protected EnergyAlias energyAlias;

  protected int rateLimit = UNLIMITED_RATE;

  public TileEntityEnergyMeterBase(MeterType type) {
    super(TileEntityTypes.get(type));
    this.energyType = type.getEnergyType();
    this.energyAlias = this.energyType.getDefaultAlias();
  }

  @Override
  public void read(CompoundNBT tag) {
    super.read(tag);
    this.inputSide = BufferUtil.decodeNullableFace(tag.getByte(NBT_INPUT_SIDE_KEY));
    this.outputSide = BufferUtil.decodeNullableFace(tag.getByte(NBT_OUTPUT_SIDE_KEY));
    this.totalEnergyTransferred = tag.getLong(NBT_TOTAL_ENERGY_TRANSFERRED_KEY);
    this.redstoneControlState = EnumRedstoneControlState.values()[tag.getInt(NBT_REDSTONE_CONTROL_STATE)];
    this.energyAlias = this.energyType.getAlias(tag.getInt(NBT_ENERGY_ALIAS));
    this.rateLimit = tag.getInt(NBT_RATE_LIMIT_KEY);

    this.totalEnergyTransferredLastTick = this.totalEnergyTransferred;
    this.loadedFromDisk = true;
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    CompoundNBT tag = super.write(compound);
    tag.putByte(NBT_INPUT_SIDE_KEY, BufferUtil.encodeNullableFace(this.inputSide));
    tag.putByte(NBT_OUTPUT_SIDE_KEY, BufferUtil.encodeNullableFace(this.outputSide));
    tag.putLong(NBT_TOTAL_ENERGY_TRANSFERRED_KEY, this.totalEnergyTransferred);
    tag.putInt(NBT_REDSTONE_CONTROL_STATE, this.redstoneControlState.ordinal());
    tag.putInt(NBT_ENERGY_ALIAS, this.energyAlias.getIndex());
    tag.putInt(NBT_RATE_LIMIT_KEY, this.rateLimit);
    return tag;
  }

  @Override
  public CompoundNBT getUpdateTag() {
    CompoundNBT tag = super.getUpdateTag();
    tag.putBoolean(NBT_CONNECTED_KEY, this.fullyConnected);
    tag.putBoolean(NBT_POWERED_KEY, this.powered);
    tag.putByte(NBT_INPUT_SIDE_KEY, BufferUtil.encodeNullableFace(this.inputSide));
    tag.putByte(NBT_OUTPUT_SIDE_KEY, BufferUtil.encodeNullableFace(this.outputSide));
    tag.putLong(NBT_TOTAL_ENERGY_TRANSFERRED_KEY, this.totalEnergyTransferred);
    tag.putInt(NBT_REDSTONE_CONTROL_STATE, this.redstoneControlState.ordinal());
    tag.putInt(NBT_ENERGY_ALIAS, this.energyAlias.getIndex());
    tag.putInt(NBT_RATE_LIMIT_KEY, this.rateLimit);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundNBT tag) {
    this.fullyConnected = tag.getBoolean(NBT_CONNECTED_KEY);
    this.powered = tag.getBoolean(NBT_POWERED_KEY);
    this.inputSide = BufferUtil.decodeNullableFace(tag.getByte(NBT_INPUT_SIDE_KEY));
    this.outputSide = BufferUtil.decodeNullableFace(tag.getByte(NBT_OUTPUT_SIDE_KEY));
    this.totalEnergyTransferred = tag.getLong(NBT_TOTAL_ENERGY_TRANSFERRED_KEY);
    this.redstoneControlState = EnumRedstoneControlState.values()[tag.getInt(NBT_REDSTONE_CONTROL_STATE)];
    this.energyAlias = energyType.getAlias(tag.getInt(NBT_ENERGY_ALIAS));
    this.rateLimit = tag.getInt(NBT_RATE_LIMIT_KEY);
  }

  /**
   * Returns the {@link SUpdateTileEntityPacket} for this tile to send to clients. This is called
   * automatically on the server whenever a client loads the chunk or when a block update is
   * triggered on the server. The packet contains <i>state</i> update info for clients, such as if
   * the meter is fully connected or powered by redstone.
   * <br>
   * This is not to be confused with the {@link PacketEnergyTransferRate} packets, which are sent
   * out every few ticks if the transfer rate changes.
   */
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
  }

  /**
   * Called on the client when it receives a {@link SUpdateTileEntityPacket} from the server.
   */
  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet){
    this.handleUpdateTag(packet.getNbtCompound());
    // this.getWorld().markBlockRangeForRenderUpdate(this.pos, this.pos);
  }

  /**
   * Called during the very first tick after this tile entity is loaded into the world. The internal
   * fields like {@link #pos} and {@link #world} are populated by this point. Use this method as an
   * alternative to a constructor and {@link #onLoad()} for initializing a tile entity.
   */
  public void onFirstTick() {
    BlockState state = this.world.getBlockState(pos);
    this.screenSide = state.get(BlockEnergyMeter.PROP_FACING);

    if (!loadedFromDisk) {
      this.inputSide = Util.getLeftFace(this.screenSide);
      this.outputSide = Util.getRightFace(this.screenSide);
    }

    if (!this.world.isRemote) {
      this.checkConnections();
      this.checkRedstone();
    } else {
      ModelDataManager.requestModelDataRefresh(this);
    }

    this.markDirty();
  }

  @Override
  public void tick() {
    if (!this.hadFirstTick) {
      this.hadFirstTick = true;
      this.onFirstTick();
    }

    if (this.world.isRemote) {
      return;
    }

    // Compute the FE transfer in this tick by taking the difference between total transfer this
    // tick and the total transfer last tick
    long transferredThisTick = Math.abs(this.totalEnergyTransferred - this.totalEnergyTransferredLastTick);

    // Add FE transfer this tick to moving average
    this.transferRateMovingAverage.add(transferredThisTick);
    this.transferRate = this.transferRateMovingAverage.getAverage(); // compute average FE/t
    this.totalEnergyTransferredLastTick = this.totalEnergyTransferred;

    if (transferredThisTick > 0) {
      this.markDirty();
    }

    // Send update packet to all nearby players if required (if the transfer rate changed or enough
    // ticks have passed since the last packet)
    if (((this.transferRate != this.lastTransferRateSent || this.transferRate > 0)
        && this.ticksSinceLastTransferRatePacket > UPDATE_PACKET_MIN_TICK_INTERVAL)
        || this.ticksSinceLastTransferRatePacket >= UPDATE_PACKET_MAX_TICK_INTERVAL) {

      this.lastTransferRateSent = this.transferRate;
      this.ticksSinceLastTransferRatePacket = 0;

      EnergyMetersMod.NETWORK.send(
          PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(this.pos)),
          new PacketEnergyTransferRate(this.pos, this.transferRate, this.totalEnergyTransferred));
    } else {
      this.ticksSinceLastTransferRatePacket++;
    }

    if (this.ticks % 10 == 0) {
      this.checkConnections();
    }

    this.ticks = ++this.ticks % 20;
  }


  public EnumRedstoneControlState getRedstoneControlState() {
    return this.redstoneControlState;
  }

  public void setRedstoneControlState(EnumRedstoneControlState redstoneControlState) {
    this.redstoneControlState = redstoneControlState;
  }

  public EnergyType getEnergyType() {
    return this.energyType;
  }

  public EnergyAlias getEnergyAlias() {
    return this.energyAlias;
  }

  public void setEnergyAlias(EnergyAlias alias) {
    if (alias.getEnergyType() != this.energyType) {
      EnergyMetersMod.LOGGER.error("Tried to set alias of type {} to meter of type {}",
          alias.getEnergyType().getName(),
          this.energyType.getName());
      return;
    }

    this.energyAlias = alias;
  }

  /**
   * Returns true is this meter is currently disabled. A disabled meter acts like a breaker and
   * will not transmit power. Whether or not this meter is disable depends on if it is powered and
   * it's redstone control state as defined in {@link #redstoneControlState}.
   */
  public boolean isDisabled() {
    return !this.redstoneControlState.isMachineEnabled(this.powered);
  }

  protected void notifyUpdate() {
    BlockState currentBlockState = this.world.getBlockState(this.pos);
    this.world.notifyBlockUpdate(this.pos, currentBlockState, currentBlockState, 3);
    this.world.notifyNeighborsOfStateChange(this.pos, Blocks.ENERGY_METER_FE);
  }

  public abstract int getEnergyScale();

  protected abstract void checkConnections();

  /**
   * Checks if this tile entity's block is powered. If the powered state has changed, then a block
   * update is triggered.
   */
  protected void checkRedstone() {
    boolean newPowered = this.world.isBlockPowered(this.pos);
    if (newPowered != this.powered) {
      this.powered = newPowered;
      this.notifyUpdate();
    }
  }

  public void onNeighborChanged(BlockPos neighborPos, BlockState newState) {
    if (this.world.isRemote) {
      return;
    }

    this.checkConnections();
    this.checkRedstone();
  }

  /** Sets the current FE transfer rate for this meter. This method should only be used client-side
   * to set the transfer rate for rendering when an update packet
   * {@link PacketEnergyTransferRate} is received. On the server, the transfer rate is calculated in
   * the {@link #tick} method.
   */
  public void setTransferRate(float rate) {
    this.transferRate = rate;
  }

  public float getTransferRate() {
    return this.transferRate;
  }

  public void setTotalEnergyTransferred(long totalEnergyTransferred) {
    this.totalEnergyTransferred = totalEnergyTransferred;
  }

  public long getTotalEnergyTransferred() {
    return this.totalEnergyTransferred;
  }

  public Direction getScreenSide() {
    return this.screenSide;
  }

  @Nullable
  public Direction getInputSide() {
    return this.inputSide;
  }

  @Nullable
  public Direction getOutputSide() {
    return this.outputSide;
  }

  public void setInputSide(@Nullable Direction side) {
    if (side == this.screenSide) {
      throw new IllegalArgumentException("Cannot set input side to screen side");
    }

    this.inputSide = side;
  }

  public void setOutputSide(@Nullable Direction side) {
    if (side == this.screenSide) {
      throw new IllegalArgumentException("Cannot set output side to screen side");
    }

    this.outputSide = side;
  }

  public int getRateLimit() {
    return this.rateLimit;
  }

  public void setRateLimit(int limit) {
    this.rateLimit = limit;
  }

  public void handleSideUpdateRequest(@Nullable Direction inputSide, @Nullable Direction outputSide) {
    if (this.world.isRemote) {
      throw new IllegalStateException("Should not have received side update packet on the client");
    }

    this.setInputSide(inputSide);
    this.setOutputSide(outputSide);

    this.checkConnections();
    this.markDirty();

    ModelDataManager.requestModelDataRefresh(this);

    BlockState state = this.world.getBlockState(this.pos);
    this.world.notifyBlockUpdate(pos, state, state, 3);
    this.world.notifyNeighborsOfStateChange(this.pos, Blocks.ENERGY_METER_FE);
  }

  public void handleConfigUpdateRequest(EnumRedstoneControlState redstoneControlState, int energyAliasIndex) {
    if (this.world.isRemote) {
      throw new IllegalStateException("Should not have received config update packet on the client");
    }

    this.redstoneControlState = redstoneControlState;
    this.setEnergyAlias(this.energyType.getAlias(energyAliasIndex));

    if (!this.energyAlias.isAvailable()) {
      this.energyAlias = this.energyType.getDefaultAlias();
    }

    this.markDirty();

    BlockState state = this.world.getBlockState(this.pos);
    this.world.notifyBlockUpdate(pos, state, state, 3);
    this.world.notifyNeighborsOfStateChange(this.pos, Blocks.ENERGY_METER_FE);
  }

  public void handleRateLimitChangeRequest(int newRateLimit) {
    if (this.world.isRemote) {
      throw new IllegalStateException("Should not have received rate limit update packet on the client");
    }

    if (newRateLimit < 0 && newRateLimit != UNLIMITED_RATE) {
      newRateLimit = UNLIMITED_RATE;
    }

    this.rateLimit = newRateLimit;

    this.checkConnections();
    this.markDirty();

    BlockState state = this.world.getBlockState(this.pos);
    this.world.notifyBlockUpdate(pos, state, state, 3);
    this.world.notifyNeighborsOfStateChange(this.pos, Blocks.ENERGY_METER_FE);
  }

  public boolean isFullyConnected() {
    return this.fullyConnected;
  }

  @OnlyIn(Dist.CLIENT)
  @Nonnull
  @Override
  public IModelData getModelData() {
    return new ModelDataMap.Builder()
        .withInitial(EnergyMeterBakedModel.MODEL_PROP_INPUT_SIDE, this.inputSide)
        .withInitial(EnergyMeterBakedModel.MODEL_PROP_OUTPUT_SIDE, this.outputSide).build();
  }

  // IEnergyMeter implementation

  @Override
  public BlockPos getPosition() {
    return this.pos;
  }

  @Override
  public World getWorldObj() {
    return this.world;
  }

  // End IEnergyMeter implementation
}
