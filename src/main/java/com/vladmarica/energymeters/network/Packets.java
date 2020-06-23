package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import net.minecraftforge.fml.relauncher.Side;

public class Packets {
  private static int packetId = 0;

  public static void register() {
    EnergyMetersMod.NETWORK.registerMessage(
        PacketEnergyTransferRate.Handler.class,
        PacketEnergyTransferRate.class,
        packetId++,
        Side.CLIENT);

    EnergyMetersMod.NETWORK.registerMessage(
        PacketUpdateMeterSides.Handler.class,
        PacketUpdateMeterSides.class,
        packetId++,
        Side.SERVER);

    EnergyMetersMod.NETWORK.registerMessage(
        PacketUpdateMeterConfig.Handler.class,
        PacketUpdateMeterConfig.class,
        packetId++,
        Side.SERVER);

    EnergyMetersMod.NETWORK.registerMessage(
        PacketUpdateRateLimit.Handler.class,
        PacketUpdateRateLimit.class,
        packetId++,
        Side.SERVER);
  }

  private Packets() {}
}
