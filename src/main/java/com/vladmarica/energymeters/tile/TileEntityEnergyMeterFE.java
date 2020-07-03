package com.vladmarica.energymeters.tile;

import com.vladmarica.energymeters.Util;
import com.vladmarica.energymeters.energy.EnergyTypes;
import com.vladmarica.energymeters.energy.storage.ForgeEnergyStorage;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityEnergyMeterFE extends TileEntityEnergyMeterBase {
  private ForgeEnergyStorage inputEnergyStorage;
  private ForgeEnergyStorage outputEnergyStorage;

  public TileEntityEnergyMeterFE() {
    super(EnergyTypes.FE);
  }

  @Override
  public void onLoad() {
    super.onLoad();

    if (!this.world.isRemote) {
      this.inputEnergyStorage = new ForgeEnergyStorage(this, inputSide);
      this.outputEnergyStorage = new ForgeEnergyStorage(this, outputSide);
    }
  }

  @Override
  public int getEnergyScale() {
    return 1;
  }

  @Override
  public long receiveEnergy(long amount, boolean simulate, EnumFacing side) {
    if (!isFullyConnected() || side != this.inputSide || this.isDisabled()) {
      return 0;
    }

    int amountReceived;
    BlockPos outputBlockPos = this.pos.add(this.outputSide.getDirectionVec());
    IEnergyStorage adjacentEnergyStorage = Util.getEnergyStorage(this.world, outputBlockPos, outputSide.getOpposite());
    if (adjacentEnergyStorage != null) {
      long amountToSend = this.rateLimit == UNLIMITED_RATE ? amount : Math.min(amount, this.rateLimit);
      amountReceived = adjacentEnergyStorage.receiveEnergy((int) amountToSend, simulate); // TODO: is this cast unsafe?
    } else {
      amountReceived = 0;
    }

    if (!simulate) {
      this.totalEnergyTransferred += amountReceived;
    }

    return amountReceived;
  }

  @Override
  public boolean canReceiveEnergy(EnumFacing side) {
    return side == this.inputSide;
  }

  @Override
  public boolean canEmitEnergy(EnumFacing side) {
    return side == this.outputSide;
  }

  @Override
  protected void checkConnections() {
    boolean connected = false;

    if (this.inputSide != null && this.outputSide != null) {
      BlockPos inputNeighbor = this.pos.offset(this.inputSide);
      BlockPos outputNeightbor = this.pos.offset(this.outputSide);

      IEnergyStorage inputEnergyStorage = Util.getEnergyStorage(world, inputNeighbor, this.inputSide.getOpposite());
      IEnergyStorage outputEnergyStorage = Util.getEnergyStorage(world, outputNeightbor, this.outputSide.getOpposite());

      connected = inputEnergyStorage != null
          && outputEnergyStorage != null
          && outputEnergyStorage.canReceive();
    }

    if (connected != this.fullyConnected) {
      this.fullyConnected = connected;
      this.notifyUpdate();
    }
  }

  @Override
  public void handleSideUpdateRequest(@Nullable EnumFacing inputSide, @Nullable EnumFacing outputSide) {
    this.inputEnergyStorage = new ForgeEnergyStorage(this, inputSide);
    this.outputEnergyStorage = new ForgeEnergyStorage(this, outputSide);
    super.handleSideUpdateRequest(inputSide, outputSide);
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY && facing != null && doesSideAcceptConnection(facing)) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY && facing != null && doesSideAcceptConnection(facing)) {
      if (facing == inputSide) {
        return CapabilityEnergy.ENERGY.cast(this.inputEnergyStorage);
      } else if (facing == outputSide) {
        return CapabilityEnergy.ENERGY.cast(this.outputEnergyStorage);
      }
      throw new RuntimeException("Attempted to get energy capability for invalid side: " + facing);
    }
    return super.getCapability(capability, facing);
  }
}
