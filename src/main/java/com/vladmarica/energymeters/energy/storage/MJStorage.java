package com.vladmarica.energymeters.energy.storage;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.vladmarica.energymeters.energy.IEnergyMeter;
import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;

public class MJStorage implements IMjReceiver {

  private IEnergyMeter meter;
  private EnumFacing side;
  private boolean connectToReceiversOnly;

  public MJStorage(IEnergyMeter meter, EnumFacing side, boolean connectToReceiversOnly) {
    this.meter = meter;
    this.side = side;
    this.connectToReceiversOnly = connectToReceiversOnly;
  }

  @Override
  public long getPowerRequested() {
    return this.meter.getRequestedEnergy(this.side);
  }

  @Override
  public long receivePower(long microJoules, boolean simulate) {
    return microJoules - this.meter.receiveEnergy(microJoules, simulate, this.side);
  }

  @Override
  public boolean canConnect(@Nonnull IMjConnector other) {
    if (other instanceof MJStorage) {
      return false; // Don't connect to adjacent meters
    }

    if (connectToReceiversOnly) {
      return other instanceof IMjReceiver && ((IMjReceiver) other).canReceive();
    }

    return true;
  }
}
