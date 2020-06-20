package com.vladmarica.energymeters.tile.config;

import com.vladmarica.energymeters.client.Sprite;
import com.vladmarica.energymeters.client.Sprites;
import java.util.function.Function;
import javax.annotation.Nullable;

public enum EnumRedstoneControlState implements IConfigEnum {
  ACTIVE("Active", powered -> !powered),
  INVERTED("Inverted", Function.identity()),
  IGNORED("Ignored", powered -> true);

  private String title;
  private Function<Boolean, Boolean> checkEnabledFunction;

  EnumRedstoneControlState(String title, Function<Boolean, Boolean> checkEnabledFunction) {
    this.title = title;
    this.checkEnabledFunction = checkEnabledFunction;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Nullable
  @Override
  public String getDescription() {
    return this.title;
  }

  @Override
  public EnumRedstoneControlState getDefault() {
    return ACTIVE;
  }

  @Override
  public Sprite getIcon() {
    switch(ordinal()) {
      case 0: return Sprites.REDSTONE_ACTIVE;
      case 1: return Sprites.REDSTONE_INVERTED;
      case 2: return Sprites.REDSTONE_DISABLED;
      default:
        throw new RuntimeException(
            "EnumRedstoneControl attempted to get icon for invalid ordinal " + ordinal());
    }
  }

  public boolean isMachineEnabled(boolean powered) {
    return this.checkEnabledFunction.apply(powered);
  }
}
