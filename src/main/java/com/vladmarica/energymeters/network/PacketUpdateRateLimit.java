package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketUpdateRateLimit implements IPacket {
  private BlockPos pos;
  private int rateLimit;

  public PacketUpdateRateLimit(PacketBuffer buffer) {
    this.pos = BufferUtil.readBlockPos(buffer);
    this.rateLimit = buffer.readInt();
  }

  public PacketUpdateRateLimit(BlockPos meterPos, int rateLimit) {
    this.pos = meterPos;
    this.rateLimit = rateLimit;
  }

  @Override
  public void encode(PacketBuffer buffer) {
    BufferUtil.writeBlockPos(buffer, this.pos);
    buffer.writeInt(this.rateLimit);
  }

  @Override
  public void handle(Supplier<Context> ctx) {
    final ServerPlayerEntity sender = ctx.get().getSender();
    if (sender != null) {
      ctx.get().enqueueWork(() -> {
        if (!sender.world.isBlockLoaded(pos)) {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateRateLimit for unloaded position {}", pos);
          return;
        }

        TileEntity tile = sender.world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          ((TileEntityEnergyMeterBase) tile).handleRateLimitChangeRequest(rateLimit);
          EnergyMetersMod.LOGGER.info("Recieved PacketUpdateRateLimit for {}", pos);
        } else {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateRateLimit for position with no TE: {}", pos);
        }
      });
    }

    ctx.get().setPacketHandled(true);
  }
}
