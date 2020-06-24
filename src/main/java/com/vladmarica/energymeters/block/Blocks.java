package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.EnergyMetersMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
@ObjectHolder(EnergyMetersMod.MODID)
public final class Blocks {

  @ObjectHolder(BlockEnergyMeter.NAME)
  public static BlockEnergyMeter ENERGY_METER;

  @SubscribeEvent
  public static void onRegisterBlockEvent(RegistryEvent.Register<Block> event) {
    event.getRegistry().register(new BlockEnergyMeter());
  }

  @SubscribeEvent
  public static void onRegisterItemsEvent(RegistryEvent.Register<Item> event) {
    event.getRegistry().register(
        new ItemBlockEnergyMeter(ENERGY_METER)
            .setRegistryName(ENERGY_METER.getRegistryName()));
  }

  private Blocks() {}
}
