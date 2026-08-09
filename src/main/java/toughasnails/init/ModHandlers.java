package toughasnails.init;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import toughasnails.config.SyncedConfigHandler;
import toughasnails.handler.BucketEventHandler;
import toughasnails.handler.ExtendedStatHandler;
import toughasnails.handler.PacketHandler;
import toughasnails.handler.temperature.TemperatureOverlayHandler;
import toughasnails.handler.temperature.TemperatureStatTableHandler;
import toughasnails.handler.thirst.DrinkHandler;
import toughasnails.handler.thirst.ThirstOverlayHandler;
import toughasnails.handler.thirst.ThirstStatHandler;

public class ModHandlers
{
    public static void init()
    {
        PacketHandler.init();

        ExtendedStatHandler stats = new ExtendedStatHandler();
        MinecraftForge.EVENT_BUS.register(stats); // EntityConstructing / Clone
        FMLCommonHandler.instance().bus().register(stats); // PlayerTick / PlayerLoggedIn

        SyncedConfigHandler configs = new SyncedConfigHandler();
        MinecraftForge.EVENT_BUS.register(configs); // World unload
        FMLCommonHandler.instance().bus().register(configs); // Player login
        MinecraftForge.EVENT_BUS.register(new BucketEventHandler());
        MinecraftForge.EVENT_BUS.register(new ThirstStatHandler());
        MinecraftForge.EVENT_BUS.register(new DrinkHandler());
        
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            TemperatureOverlayHandler temperatureOverlay = new TemperatureOverlayHandler();
            MinecraftForge.EVENT_BUS.register(temperatureOverlay); // RenderGameOverlay
            FMLCommonHandler.instance().bus().register(temperatureOverlay); // ClientTick
            MinecraftForge.EVENT_BUS.register(new TemperatureStatTableHandler());
            ThirstOverlayHandler thirstOverlay = new ThirstOverlayHandler();
            MinecraftForge.EVENT_BUS.register(thirstOverlay); // RenderGameOverlay
            FMLCommonHandler.instance().bus().register(thirstOverlay); // ClientTick
        }
    }
}
