package com.vladmarica.energymeters;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(modid = EnergyMetersMod.MODID, name = EnergyMetersMod.NAME, version = EnergyMetersMod.VERSION)
public class EnergyMetersMod {
  public static final String MODID = "energymeters";
  public static final String NAME = "Forge Energy Meters";
  public static final String VERSION = "1.0";

  public static final Logger LOGGER = LogManager.getLogger(MODID);
  public static final SimpleNetworkWrapper NETWORK =
      NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

  @Instance
  public static EnergyMetersMod INSTANCE;

  @SidedProxy(
      serverSide = "com.vladmarica.energymeters.CommonProxy",
      clientSide = "com.vladmarica.energymeters.client.ClientProxy")
  public static CommonProxy PROXY;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    PROXY.preInit(event);
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    PROXY.init(event);
  }

  @EventHandler
  public void init(FMLPostInitializationEvent event) {
    PROXY.postInit(event);
  }
}
