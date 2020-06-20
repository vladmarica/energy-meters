package com.vladmarica.energymeters.network;

import com.vladmarica.energymeters.tile.PlayerDescriptor;
import ibxm.Player;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BufferUtilTest {
  @Test
  public void testBlockPos() {
    ByteBuf buf = Unpooled.buffer();
    BlockPos pos = new BlockPos(10, -4, 32);

    BufferUtil.writeBlockPos(buf, pos);
    BlockPos newPos = BufferUtil.readBlockPos(buf);

    assertEquals(pos, newPos);
  }

  @Test
  public void testNullFace() {
    ByteBuf buf = Unpooled.buffer();
    EnumFacing face = null;

    BufferUtil.writeNullableFace(buf, face);
    EnumFacing newFace = BufferUtil.readNullableFace(buf);
    assertNull(newFace);
  }

  @Test
  public void testNonNullFace() {

    for (EnumFacing face : EnumFacing.values()) {
      ByteBuf buf = Unpooled.buffer();
      BufferUtil.writeNullableFace(buf, face);

      EnumFacing newFace = BufferUtil.readNullableFace(buf);
      assertEquals(face, newFace);
    }
  }

  @Test
  public void testPlayerDescriptors() {
    UUID uuid = UUID.randomUUID();
    String username = "Quintinity";

    PlayerDescriptor descriptor = new PlayerDescriptor(uuid, username);
    ByteBuf buf = Unpooled.buffer();

    BufferUtil.writePlayerDescriptor(buf, descriptor);

    PlayerDescriptor newDescriptor = BufferUtil.readPlayerDescriptor(buf);
    assertEquals(descriptor, newDescriptor);
    assertEquals(descriptor.getUsername(), newDescriptor.getUsername());
    assertEquals(descriptor.getUUID(), newDescriptor.getUUID());
  }
}
