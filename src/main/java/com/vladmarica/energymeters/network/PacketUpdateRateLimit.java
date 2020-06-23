package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateRateLimit implements IMessage {
  private BlockPos pos;
  private int rateLimit;

  public PacketUpdateRateLimit() {}

  public PacketUpdateRateLimit(BlockPos meterPos, int rateLimit) {
    this.pos = meterPos;
    this.rateLimit = rateLimit;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.pos = BufferUtil.readBlockPos(buf);
    this.rateLimit = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    BufferUtil.writeBlockPos(buf, this.pos);
    buf.writeInt(this.rateLimit);
  }

  public static class Handler implements IMessageHandler<PacketUpdateRateLimit, IMessage> {

    @Override
    public IMessage onMessage(PacketUpdateRateLimit message, MessageContext ctx) {
      BlockPos pos = message.pos;
      WorldServer world =  ctx.getServerHandler().player.getServerWorld();

      world.addScheduledTask(() -> {
        if (!world.isBlockLoaded(pos)) {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateRateLimit for unloaded position {}", pos);
          return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          ((TileEntityEnergyMeterBase) tile).handleRateLimitChangeRequest(message.rateLimit);
          EnergyMetersMod.LOGGER.info("Recieved PacketUpdateRateLimit for {}", pos);
        } else {
          EnergyMetersMod.LOGGER.error(
              "Recieved PacketUpdateRateLimit for position with no TE: {}", pos);
        }
      });

      return null;
    }
  }
}
