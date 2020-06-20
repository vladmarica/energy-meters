package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.config.EnumRedstoneControlState;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateMeterConfig implements IMessage {
  private BlockPos pos;
  private EnumRedstoneControlState redstoneControlState;
  private int energyAliasIndex;

  public PacketUpdateMeterConfig() {}

  public PacketUpdateMeterConfig(BlockPos pos, EnumRedstoneControlState redstoneControlState, int energyAliasIndex) {
    this.pos = pos;
    this.redstoneControlState = redstoneControlState;
    this.energyAliasIndex = energyAliasIndex;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    this.pos = BufferUtil.readBlockPos(buf);
    this.redstoneControlState = EnumRedstoneControlState.values()[buf.readInt()];
    this.energyAliasIndex = buf.readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    BufferUtil.writeBlockPos(buf, this.pos);
    buf.writeInt(this.redstoneControlState.ordinal());
    buf.writeInt(this.energyAliasIndex);
  }

  public static class Handler implements IMessageHandler<PacketUpdateMeterConfig, IMessage> {

    @Override
    public IMessage onMessage(PacketUpdateMeterConfig message, MessageContext ctx) {
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
          ((TileEntityEnergyMeterBase) tile).handleConfigUpdateRequest(
              message.redstoneControlState, message.energyAliasIndex);
          EnergyMetersMod.LOGGER.info(
              "Recieved PacketUpdateMeterConfig for {}", pos);
        } else {
          EnergyMetersMod.LOGGER.error(
              "Recieved PacketUpdateMeterConfig for position with no TE: {}", pos);
        }
      });

      return null;
    }
  }
}
