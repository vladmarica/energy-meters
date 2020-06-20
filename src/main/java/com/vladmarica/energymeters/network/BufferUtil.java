package com.vladmarica.energymeters.network;

import com.google.common.base.Charsets;
import com.vladmarica.energymeters.tile.PlayerDescriptor;
import ibxm.Player;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
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

  public static byte encodeNullableFace(@Nullable EnumFacing face) {
    return face != null ? (byte) face.ordinal() : NULL_FACE_ORDINAL;
  }

  @Nullable
  public static EnumFacing decodeNullableFace(byte b) {
    if (b == NULL_FACE_ORDINAL) {
      return null;
    }
    return EnumFacing.values()[b];
  }

  public static void writeNullableFace(ByteBuf buf, @Nullable EnumFacing face) {
    buf.writeByte(encodeNullableFace(face));
  }

  @Nullable
  public static EnumFacing readNullableFace(ByteBuf buf) {
    return decodeNullableFace(buf.readByte());
  }

  public static void writePlayerDescriptor(ByteBuf buf, PlayerDescriptor descriptor) {
    byte[] uuidBytes = descriptor.getUUID().toString().getBytes(Charsets.UTF_8);
    byte[] usernameBytes = descriptor.getUsername().getBytes(Charsets.UTF_8);

    buf.writeByte(uuidBytes.length);
    buf.writeBytes(uuidBytes);
    buf.writeByte(usernameBytes.length);
    buf.writeBytes(usernameBytes);
  }

  public static PlayerDescriptor readPlayerDescriptor(ByteBuf buf) {
    byte uuidLength = buf.readByte();
    byte[] uuidBytes = new byte[uuidLength];
    buf.readBytes(uuidBytes);

    byte usernameLength = buf.readByte();
    byte[] usernameBytes = new byte[usernameLength];
    buf.readBytes(usernameBytes);

    return new PlayerDescriptor(
        UUID.fromString(new String(uuidBytes, Charsets.UTF_8)),
        new String(usernameBytes, Charsets.UTF_8));
  }

  private BufferUtil() {}
}
