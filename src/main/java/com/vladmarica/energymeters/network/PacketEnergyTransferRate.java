package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEnergyTransferRate implements IMessage {
  public BlockPos pos;
  private float rate;
  private long totalEnergyTransfered;

  public PacketEnergyTransferRate() {}

  public PacketEnergyTransferRate(BlockPos meterPos, float rate, long totalEnergyTransfered) {
    this.pos = meterPos;
    this.rate = rate;
    this.totalEnergyTransfered = totalEnergyTransfered;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.pos = BufferUtil.readBlockPos(buf);
    this.rate = buf.readFloat();
    this.totalEnergyTransfered = buf.readLong();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    BufferUtil.writeBlockPos(buf, this.pos);
    buf.writeFloat(this.rate);
    buf.writeLong(this.totalEnergyTransfered);
  }

  public static class Handler implements IMessageHandler<PacketEnergyTransferRate, IMessage> {

    @Override
    public IMessage onMessage(PacketEnergyTransferRate message, MessageContext ctx) {
      final float rate = message.rate;
      final long totalEnergyTransferred = message.totalEnergyTransfered;
      final BlockPos pos = message.pos;

      Minecraft.getMinecraft().addScheduledTask(() -> {
        WorldClient world = Minecraft.getMinecraft().world;
        if (!world.isBlockLoaded(pos)) {
          EnergyMetersMod.LOGGER.error(
              "Recieved PacketEnergyTransferRate for unloaded position {}", pos);
          return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          TileEntityEnergyMeterBase energyMeterTile = (TileEntityEnergyMeterBase) tile;
          energyMeterTile.setTransferRate(rate);
          energyMeterTile.setTotalEnergyTransferred(totalEnergyTransferred);
        } else {
          EnergyMetersMod.LOGGER.error(
              "Recieved PacketEnergyTransferRate for position with no TE: {}", pos);
        }
      });

      return null;
    }
  }
}
