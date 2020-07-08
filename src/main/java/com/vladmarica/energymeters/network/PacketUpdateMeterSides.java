package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketUpdateMeterSides implements IPacket {
  private BlockPos pos;
  @Nullable
  private Direction inputSide;
  @Nullable
  private Direction outputSide;

  public PacketUpdateMeterSides(PacketBuffer buffer) {
    this.pos = BufferUtil.readBlockPos(buffer);
    this.inputSide = BufferUtil.readNullableFace(buffer);
    this.outputSide = BufferUtil.readNullableFace(buffer);
  }

  public PacketUpdateMeterSides(
      BlockPos meterPos, @Nullable Direction inputSide, @Nullable Direction outputSide) {
    this.pos = meterPos;
    this.inputSide = inputSide;
    this.outputSide = outputSide;
  }


  @Override
  public void encode(PacketBuffer buffer) {
    BufferUtil.writeBlockPos(buffer, this.pos);
    BufferUtil.writeNullableFace(buffer, this.inputSide);
    BufferUtil.writeNullableFace(buffer, this.outputSide);
  }

  @Override
  public void handle(Supplier<Context> ctx) {
    final ServerPlayerEntity sender = ctx.get().getSender();
    if (sender != null) {
      ctx.get().enqueueWork(() -> {
        if (!sender.world.isBlockLoaded(pos)) {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateMeterSides for unloaded position {}", pos);
          return;
        }

        TileEntity tile = sender.world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          ((TileEntityEnergyMeterBase) tile).handleSideUpdateRequest(inputSide, outputSide);
          EnergyMetersMod.LOGGER.info("Recieved PacketUpdateMeterSides for {}", pos);
        } else {
          EnergyMetersMod.LOGGER.error("Recieved PacketUpdateMeterSides for position with no TE: {}", pos);
        }
      });
    }

    ctx.get().setPacketHandled(true);
  }
}
