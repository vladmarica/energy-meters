package com.vladmarica.energymeters.tile;

import com.vladmarica.energymeters.Util;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.energy.storage.ForgeEnergyStorage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityEnergyMeterFE extends TileEntityEnergyMeterBase {

  private ForgeEnergyStorage inputEnergyStorage;
  private ForgeEnergyStorage outputEnergyStorage;
  private LazyOptional<IEnergyStorage> inputStorageOptional = LazyOptional.empty();
  private LazyOptional<IEnergyStorage> outputStorageOptional = LazyOptional.empty();

  public TileEntityEnergyMeterFE() {
    super(MeterType.FE_METER);
  }

  private void setupCapabilities(Direction inputSide, Direction outputSide) {
    this.inputEnergyStorage = new ForgeEnergyStorage(this, inputSide);
    this.outputEnergyStorage = new ForgeEnergyStorage(this, outputSide);

    if (this.inputStorageOptional.isPresent()) {
      this.inputStorageOptional.invalidate();
    }

    if (this.outputStorageOptional.isPresent()) {
      this.outputStorageOptional.invalidate();
    }

    this.inputStorageOptional = LazyOptional.of(() -> this.inputEnergyStorage);
    this.outputStorageOptional = LazyOptional.of(() -> this.outputEnergyStorage);
  }

  @Override
  public void onFirstTick() {
    super.onFirstTick();
    this.setupCapabilities(this.inputSide, this.outputSide);
  }

  @Override
  public int getEnergyScale() {
    return 1;
  }


  @Override
  public long receiveEnergy(long amount, boolean simulate, Direction side) {
    if (!isFullyConnected() || side != this.inputSide || this.isDisabled()) {
      return 0;
    }

    if (this.world.isRemote) {
      return 0;
    }

    int amountReceived = 0;
    BlockPos outputBlockPos = this.pos.add(this.outputSide.getDirectionVec());
    LazyOptional<IEnergyStorage> adjacentStorageOptional = Util.getEnergyStorage(
        this.world, outputBlockPos, outputSide.getOpposite());

    if (adjacentStorageOptional.isPresent()) {
      IEnergyStorage adjacentStorage = adjacentStorageOptional.orElseThrow(
          () -> new RuntimeException("Failed to get present adjacent storage for pos " + this.pos));
      long amountToSend = this.rateLimit == UNLIMITED_RATE ? amount : Math.min(amount, this.rateLimit);
      amountReceived = adjacentStorage.receiveEnergy((int) amountToSend, simulate); // TODO: is this cast unsafe?
    }

    if (!simulate) {
      this.totalEnergyTransferred += amountReceived;
    }

    return amountReceived;
  }

  @Override
  public boolean canReceiveEnergy(Direction side) {
    return side == this.inputSide;
  }

  @Override
  public boolean canEmitEnergy(Direction side) {
    return side == this.outputSide;
  }

  @Override
  protected void checkConnections() {
    boolean connected = false;

    if (this.inputSide != null && this.outputSide != null) {
      BlockPos inputNeighbor = this.pos.offset(this.inputSide);
      BlockPos outputNeightbor = this.pos.offset(this.outputSide);

      IEnergyStorage adjacentInputStorage =
          Util.getEnergyStorage(this.world, inputNeighbor, this.inputSide.getOpposite())
              .orElse(null);
      IEnergyStorage adjacentOutputStorage =
          Util.getEnergyStorage( this.world, outputNeightbor, this.outputSide.getOpposite())
              .orElse(null);

      connected = adjacentInputStorage != null
          && adjacentOutputStorage != null
          && adjacentOutputStorage.canReceive();
    }

    if (connected != this.fullyConnected) {
      this.fullyConnected = connected;
      this.notifyUpdate();
    }
  }

  @Override
  public void handleSideUpdateRequest(@Nullable Direction inputSide, @Nullable Direction outputSide) {
    this.setupCapabilities(inputSide, outputSide);
    super.handleSideUpdateRequest(inputSide, outputSide);
  }

  /**
   * Called on the client when it receives a {@link SUpdateTileEntityPacket} from the server.
   */
  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet){
    super.onDataPacket(net, packet);
    this.setupCapabilities(this.inputSide, this.outputSide);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityEnergy.ENERGY && side != null) {
      if (side == this.inputSide) {
        return CapabilityEnergy.ENERGY.orEmpty(cap, this.inputStorageOptional);
      } else if (side == this.outputSide) {
        return CapabilityEnergy.ENERGY.orEmpty(cap, this.outputStorageOptional);
      }
    }

    return super.getCapability(cap, side);
  }
}
