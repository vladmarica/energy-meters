package com.vladmarica.energymeters.tile.config;

import com.vladmarica.energymeters.client.Sprite;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IConfigEnum {

  @Nullable
  String getDisplayName();

  @Nullable
  String getDescription();

  Enum<? extends IConfigEnum> getDefault();

  @OnlyIn(Dist.CLIENT)
  @Nullable
  default Sprite getIcon() {
    return null;
  }

  default boolean isAvailable() {
    return true;
  }
}
