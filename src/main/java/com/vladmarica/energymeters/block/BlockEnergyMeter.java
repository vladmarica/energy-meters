package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.energy.EnergyType;
import com.vladmarica.energymeters.energy.EnergyTypes;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.ToolType;

public class BlockEnergyMeter extends Block {

  private MeterType meterType;

  public BlockEnergyMeter(MeterType meterType) {
    super(
        Block.Properties.create(Material.IRON)
            .harvestTool(ToolType.PICKAXE)
            .sound(SoundType.STONE)
            .hardnessAndResistance(3.5F));

    this.meterType = meterType;
  }

  public enum MeterType implements IStringSerializable {
    FE_METER(0, EnergyTypes.FE);

    private int index;
    private EnergyType type;

    MeterType(int index, EnergyType type) {
      this.index = index;
      this.type = type;
    }

    public int getIndex() {
      return this.index;
    }

    public EnergyType getEnergyType() {
      return this.type;
    }

    @Override
    public String getName() {
      return this.type.getName().toLowerCase();
    }
  }
}
