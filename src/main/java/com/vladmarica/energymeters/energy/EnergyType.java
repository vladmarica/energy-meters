package com.vladmarica.energymeters.energy;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class EnergyType {

  protected String name;
  protected String description;
  protected List<EnergyAlias> aliases;
  private String aliasesDisplayString = null;

  EnergyType(String name, String description) {
    this.name = name;
    this.description = description;

    this.aliases = new ArrayList<>();
    this.addAlias(name, description);
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public List<EnergyAlias> getAliases() {
    return ImmutableList.copyOf(this.aliases);
  }

  public EnergyAlias getAlias(int index) {
    return this.aliases.get(index);
  }

  protected void addAlias(String displayName, String description, Supplier<Boolean> isAvailableSupplier) {
    int nextId = this.aliases.size();
    this.aliases.add(new EnergyAlias(this, nextId, displayName, description, isAvailableSupplier));
  }

  protected void addAlias(String displayName, String description) {
    this.addAlias(displayName, description, () -> true);
  }

  public EnergyAlias getDefaultAlias() {
    return this.aliases.get(0);
  }

  /**
   * Returns a String representing all aliases for this energy type. Only aliases that are available
   * are included. For example, if this energy type is "FE", then this method can return
   * "FE/RF/µI".
   */
  public String getAliasesDisplayString() {
    if (this.aliasesDisplayString != null) {
      return this.aliasesDisplayString;
    }

    this.aliasesDisplayString = this.getAliases().stream()
        .filter(EnergyAlias::isAvailable)
        .map(EnergyAlias::getDisplayName)
        .collect(Collectors.joining("/"));

    return this.aliasesDisplayString;
  }

  public abstract boolean isAvailable();

  public abstract boolean isLimitable();

  public static class EnergyAlias {
    private EnergyType type;
    private int index;
    private String displayName;
    private String description;
    private Supplier<Boolean> isAvailableSupplier;

    EnergyAlias(EnergyType type, int index, String displayName, String description, Supplier<Boolean> isAvailableSupplier) {
      this.type = type;
      this.displayName = displayName;
      this.description = description;
      this.index = index;
      this.isAvailableSupplier = isAvailableSupplier;
    }

    public EnergyType getEnergyType() {
      return this.type;
    }

    public boolean isAvailable() {
      return this.isAvailableSupplier.get();
    }

    public String getDisplayName() {
      return this.displayName;
    }

    public String getDescription() {
      return this.description;
    }

    public int getIndex() {
      return this.index;
    }
  }
}
