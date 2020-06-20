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

public class PacketUpdateMeterSides implements IMessage {
  private BlockPos pos;
  @Nullable private EnumFacing inputSide;
  @Nullable private EnumFacing outputSide;

  public PacketUpdateMeterSides() {}

  public PacketUpdateMeterSides(BlockPos meterPos, @Nullable EnumFacing inputSide, @Nullable EnumFacing outputSide) {
    this.pos = meterPos;
    this.inputSide = inputSide;
    this.outputSide = outputSide;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.pos = BufferUtil.readBlockPos(buf);
    this.inputSide = BufferUtil.readNullableFace(buf);
    this.outputSide = BufferUtil.readNullableFace(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    BufferUtil.writeBlockPos(buf, this.pos);
    BufferUtil.writeNullableFace(buf, this.inputSide);
    BufferUtil.writeNullableFace(buf, this.outputSide);
  }

  public static class Handler implements IMessageHandler<PacketUpdateMeterSides, IMessage> {

    @Override
    public IMessage onMessage(PacketUpdateMeterSides message, MessageContext ctx) {
      BlockPos pos = message.pos;
      WorldServer world =  ctx.getServerHandler().player.getServerWorld();

      world.addScheduledTask(() -> {
        if (!world.isBlockLoaded(pos)) {
          EnergyMetersMod.LOGGER.error(
              "Recieved PacketUpdateMeterSides for unloaded position {}", pos);
          return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          ((TileEntityEnergyMeterBase) tile).handleSideUpdateRequest(message.inputSide, message.outputSide);
          EnergyMetersMod.LOGGER.info(
              "Recieved PacketUpdateMeterSides for {}", pos);
        } else {
          EnergyMetersMod.LOGGER.error(
              "Recieved PacketUpdateMeterSides for position with no TE: {}", pos);
        }
      });

      return null;
    }
  }
}
