package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.tile.TileEntityEnergyMeterBase;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketEnergyTransferRate implements IPacket {
  private final BlockPos pos;
  private final float rate;
  private final long totalEnergyTransferred;

  public PacketEnergyTransferRate(PacketBuffer buffer) {
    this.pos = BufferUtil.readBlockPos(buffer);
    this.rate = buffer.readFloat();
    this.totalEnergyTransferred = buffer.readLong();
  }

  public PacketEnergyTransferRate(BlockPos meterPos, float rate, long totalEnergyTransferred) {
    this.pos = meterPos;
    this.rate = rate;
    this.totalEnergyTransferred = totalEnergyTransferred;
  }

  @Override
  public void encode(PacketBuffer buffer) {
    BufferUtil.writeBlockPos(buffer, this.pos);
    buffer.writeFloat(this.rate);
    buffer.writeLong(this.totalEnergyTransferred);
  }

  @Override
  public void handle(Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
        World world = Minecraft.getInstance().world;
        if (!world.isBlockLoaded(pos)) {
          return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyMeterBase) {
          TileEntityEnergyMeterBase energyMeterTile = (TileEntityEnergyMeterBase) tile;
          energyMeterTile.setTransferRate(rate);
          energyMeterTile.setTotalEnergyTransferred(totalEnergyTransferred);
        } else {
          EnergyMetersMod.LOGGER.error("Recieved PacketEnergyTransferRate for position with no TE: {}", pos);
        }
      });
    });
    ctx.get().setPacketHandled(true);
  }
}
