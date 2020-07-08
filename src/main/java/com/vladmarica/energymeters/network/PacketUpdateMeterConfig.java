package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import com.vladmarica.energymeters.tile.config.EnumRedstoneControlState;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketUpdateMeterConfig implements IPacket {
  private BlockPos pos;
  private EnumRedstoneControlState redstoneControlState;
  private int energyAliasIndex;

  public PacketUpdateMeterConfig(PacketBuffer buffer) {
    this.pos = BufferUtil.readBlockPos(buffer);
    this.redstoneControlState = EnumRedstoneControlState.values()[buffer.readInt()];
    this.energyAliasIndex = buffer.readInt();
  }

  public PacketUpdateMeterConfig(BlockPos pos, EnumRedstoneControlState redstoneControlState, int energyAliasIndex) {
    this.pos = pos;
    this.redstoneControlState = redstoneControlState;
    this.energyAliasIndex = energyAliasIndex;
  }

  @Override
  public void encode(PacketBuffer buffer) {
    BufferUtil.writeBlockPos(buffer, this.pos);
    buffer.writeInt(this.redstoneControlState.ordinal());
    buffer.writeInt(this.energyAliasIndex);
  }

  @Override
  public void handle(Supplier<Context> ctx) {
    final ServerPlayerEntity sender = ctx.get().getSender();
    if (sender != null) {
      ctx.get().enqueueWork(() -> {
        if (!sender.world.isBlockLoaded(pos)) {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateMeterConfig for unloaded position {}", pos);
          return;
        }

        TileEntity tile = sender.world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          ((TileEntityEnergyMeterBase) tile).handleConfigUpdateRequest(redstoneControlState, energyAliasIndex);
          EnergyMetersMod.LOGGER.info("Recieved PacketUpdateMeterConfig for {}", pos);
        } else {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateMeterConfig for position with no TE: {}", pos);
        }
      });
    }

    ctx.get().setPacketHandled(true);
  }
}
