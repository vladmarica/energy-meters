package com.vladmarica.energymeters.network;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class BufferUtil {
  private static final byte NULL_FACE_ORDINAL = -1;

  public static void writeBlockPos(ByteBuf buf, BlockPos pos) {
    buf.writeInt(pos.getX());
    buf.writeInt(pos.getY());
    buf.writeInt(pos.getZ());
  }

  public static BlockPos readBlockPos(ByteBuf buf) {
    return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
  }

  public static byte encodeNullableFace(@Nullable Direction face) {
    return face != null ? (byte) face.ordinal() : NULL_FACE_ORDINAL;
  }

  @Nullable
  public static Direction decodeNullableFace(byte b) {
    if (b == NULL_FACE_ORDINAL) {
      return null;
    }
    return Direction.values()[b];
  }

  public static void writeNullableFace(ByteBuf buf, @Nullable Direction face) {
    buf.writeByte(encodeNullableFace(face));
  }

  @Nullable
  public static Direction readNullableFace(ByteBuf buf) {
    return decodeNullableFace(buf.readByte());
  }

  private BufferUtil() {}
}
