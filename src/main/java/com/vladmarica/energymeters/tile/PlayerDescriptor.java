package com.vladmarica.energymeters.tile;

import java.util.UUID;

public class PlayerDescriptor {
  private UUID uuid;
  private String username;

  public PlayerDescriptor(UUID uuid, String username) {
    if (uuid == null) {
      throw new IllegalArgumentException("UUID cannot be null");
    }

    if (username == null) {
      throw new IllegalArgumentException("Username cannot be null");
    }

    this.uuid = uuid;
    this.username = username;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getUsername() {
    return this.username;
  }

  @Override
  public String toString() {
    return String.format("[username=%s, uuid=%s]", this.username, this.uuid);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof PlayerDescriptor) {
      PlayerDescriptor otherDescriptor = (PlayerDescriptor) other;
      return this.username.equals(otherDescriptor.username)
          && this.uuid.equals(otherDescriptor.uuid);
    }

    return false;
  }
}
