package com.vladmarica.energymeters.tile;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import com.vladmarica.energymeters.block.Blocks;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = EnergyMetersMod.MODID, bus = Bus.MOD)
@ObjectHolder(EnergyMetersMod.MODID)
public class TileEntityTypes {
  private static Map<MeterType, TileEntityType<? extends TileEntityEnergyMeterBase>> tileEntityTypeMap = new HashMap<>();

  public static TileEntityType<? extends TileEntityEnergyMeterBase> get(MeterType meterType) {
    return tileEntityTypeMap.get(meterType);
  }

  @SubscribeEvent
  public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
    // FE meter tile registration
    TileEntityType<TileEntityEnergyMeterFE> tileTypeFE = TileEntityType.Builder.create(
        TileEntityEnergyMeterFE::new, Blocks.ENERGY_METER_FE).build(null);
    tileTypeFE.setRegistryName(EnergyMetersMod.MODID, "te_energy_meter_fe");
    event.getRegistry().register(tileTypeFE);
    tileEntityTypeMap.put(MeterType.FE_METER, tileTypeFE);
  }
}
