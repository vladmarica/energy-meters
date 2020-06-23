package com.vladmarica.energymeters.tile;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.vladmarica.energymeters.energy.EnergyTypes;
import com.vladmarica.energymeters.integration.BuildcraftIntegration;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityEnergyMeterMJ extends TileEntityEnergyMeterBase {
  private MJStorage inputMJStorage;
  private MJStorage outputMJStorage;

  public TileEntityEnergyMeterMJ() {
    super(EnergyTypes.MJ);
  }

  @Override
  public void onLoad() {
    super.onLoad();

    if (!this.world.isRemote) {
      this.inputMJStorage = new MJStorage(this, this.inputSide, false);
      this.outputMJStorage = new MJStorage(this, this.outputSide, true);
    }
  }

  @Override
  public int getEnergyScale() {
    return 10;
  }

  @Override
  public boolean canReceiveEnergy(EnumFacing side) {
    return true;
  }

  @Override
  public boolean canEmitEnergy(EnumFacing side) {
    return true;
  }

  @Override
  public long receiveEnergy(long amount, boolean simulate, EnumFacing side) {
    if (!isFullyConnected() || side != this.inputSide || this.isDisabled()) {
      return 0;
    }

    long amountTransferred = 0;
    BlockPos outputNeightbor = this.pos.offset(this.outputSide);
    IMjReceiver outputReciever = BuildcraftIntegration.getMJReciever(this.world, outputNeightbor, this.outputSide.getOpposite());
    if (outputReciever != null && outputReciever.canReceive()) {
      long amountToSend = this.rateLimit == UNLIMITED_RATE ? amount : Math.min(amount, this.rateLimit * MjAPI.MJ);
      amountTransferred = amountToSend - outputReciever.receivePower(amountToSend, simulate);
    }

    if (!simulate) {
      long amountTransferredMJ = (long) (((double) amountTransferred) / MjAPI.MJ * this.getEnergyScale());
      this.totalEnergyTransferred += amountTransferredMJ;
    }

    return amountTransferred;
  }

  @Override
  protected void checkConnections() {
    boolean connected = false;

    if (this.inputSide != null && this.outputSide != null && this.inputMJStorage != null && this.outputMJStorage != null) {
      BlockPos inputNeighbor = this.pos.offset(this.inputSide);
      BlockPos outputNeightbor = this.pos.offset(this.outputSide);

      IMjConnector inputConnector = BuildcraftIntegration.getMJConnector(this.world, inputNeighbor, this.inputSide.getOpposite());
      IMjReceiver outputReciever = BuildcraftIntegration.getMJReciever(this.world, outputNeightbor, this.outputSide.getOpposite());

      connected = inputConnector != null
          && outputReciever != null
          && outputReciever.canReceive()
          && this.inputMJStorage.canConnect(inputConnector)
          && outputReciever.canConnect(this.outputMJStorage);
    }

    if (connected != this.fullyConnected) {
      this.fullyConnected = connected;
      this.notifyUpdate();
    }
  }

  @Override
  public void handleSideUpdateRequest(@Nullable EnumFacing inputSide, @Nullable EnumFacing outputSide) {
    super.handleSideUpdateRequest(inputSide, outputSide);
    this.inputMJStorage = new MJStorage(this, this.inputSide, false);
    this.outputMJStorage = new MJStorage(this, this.outputSide, true);
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    if (capability == MjAPI.CAP_RECEIVER && facing == this.inputSide) {
      return true;
    }
    if (capability == MjAPI.CAP_CONNECTOR && doesSideAcceptConnection(facing)) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (capability == MjAPI.CAP_RECEIVER && facing == this.inputSide) {
      return MjAPI.CAP_RECEIVER.cast(this.inputMJStorage);
    }
    if (capability == MjAPI.CAP_CONNECTOR) {
      if (facing == this.inputSide) {
        return MjAPI.CAP_CONNECTOR.cast(this.inputMJStorage);
      }
      if (facing == this.outputSide) {
        return MjAPI.CAP_CONNECTOR.cast(this.outputMJStorage);
      }
    }
    return super.getCapability(capability, facing);
  }
}
