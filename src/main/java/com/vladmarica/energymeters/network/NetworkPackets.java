package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;

public class NetworkPackets {
  private static int packetId = 0;

  public static void register() {
    EnergyMetersMod.NETWORK.registerMessage(
        packetId++,
        PacketEnergyTransferRate.class,
        PacketEnergyTransferRate::encode,
        PacketEnergyTransferRate::new,
        PacketEnergyTransferRate::handle);

    EnergyMetersMod.NETWORK.registerMessage(
        packetId++,
        PacketUpdateMeterConfig.class,
        PacketUpdateMeterConfig::encode,
        PacketUpdateMeterConfig::new,
        PacketUpdateMeterConfig::handle);

    EnergyMetersMod.NETWORK.registerMessage(
        packetId++,
        PacketUpdateMeterSides.class,
        PacketUpdateMeterSides::encode,
        PacketUpdateMeterSides::new,
        PacketUpdateMeterSides::handle);

    EnergyMetersMod.NETWORK.registerMessage(
        packetId++,
        PacketUpdateRateLimit.class,
        PacketUpdateRateLimit::encode,
        PacketUpdateRateLimit::new,
        PacketUpdateRateLimit::handle);
    }

  private NetworkPackets() {}
}
