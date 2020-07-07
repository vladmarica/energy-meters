package com.vladmarica.energymeters;

import com.vladmarica.energymeters.client.ClientProxy;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(EnergyMetersMod.MODID)
public class EnergyMetersMod {
  public static final String MODID = "energymeters";
  public static final String NAME = "Forge Energy Meters";
  public static final String VERSION = "1.0.2";

  public static final Logger LOGGER = LogManager.getLogger(MODID);
  //public static final SimpleNetworkWrapper NETWORK =
      ///NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

  public static CommonProxy proxy = DistExecutor.runForDist(
      () -> getClientProxy(),
      () -> () -> new CommonProxy());

  @OnlyIn(Dist.CLIENT)
  private static Supplier<CommonProxy> getClientProxy() {
    return ClientProxy::new;
  }

  public EnergyMetersMod() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitEvent);
  }

  public void onInitEvent(final FMLCommonSetupEvent event) {
    proxy.init(event);
  }
}
