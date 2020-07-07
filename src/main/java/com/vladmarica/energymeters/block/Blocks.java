package com.vladmarica.energymeters.block;

import com.vladmarica.energymeters.EnergyMetersMod;
import com.vladmarica.energymeters.block.BlockEnergyMeter.MeterType;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = EnergyMetersMod.MODID, bus = Bus.MOD)
@ObjectHolder(EnergyMetersMod.MODID)
public class Blocks {

  @ObjectHolder("meter")
  public static BlockEnergyMeter ENERGY_METER_FE;

  @SubscribeEvent
  public static void onBlockRegistration(RegistryEvent.Register<Block> event) {
    event.getRegistry().register(new BlockEnergyMeter(MeterType.FE_METER).setRegistryName("meter"));
  }

  @SubscribeEvent
  public static void onItemRegistration(RegistryEvent.Register<Item> event) {
    Item.Properties props = new Item.Properties().group(ItemGroup.BUILDING_BLOCKS);

    BlockItem item = new BlockItem(ENERGY_METER_FE, props);
    item.setRegistryName(ENERGY_METER_FE.getRegistryName());
    event.getRegistry().register(item);
  }
}
