package com.vladmarica.energymeters.tile.config;

import com.vladmarica.energymeters.client.Sprite;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IConfigEnum {

  @Nullable
  String getDisplayName();

  @Nullable
  String getDescription();

  Enum<? extends IConfigEnum> getDefault();

  @SideOnly(Side.CLIENT)
  @Nullable
  default Sprite getIcon() {
    return null;
  }

  default boolean isAvailable() {
    return true;
  }
}
