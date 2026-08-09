package toughasnails.core;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import toughasnails.command.TANCommand;
import toughasnails.handler.BlockHarvestEventHandler;
import toughasnails.handler.LootTableEventHandler;
import toughasnails.init.ModBlocks;
import toughasnails.init.ModCompat;
import toughasnails.init.ModConfig;
import toughasnails.init.ModCrafting;
import toughasnails.init.ModEntities;
import toughasnails.init.ModHandlers;
import toughasnails.init.ModItems;
import toughasnails.init.ModPotions;
import toughasnails.init.ModStats;
import toughasnails.init.ModVanillaCompat;

@Mod(modid = ToughAsNails.MOD_ID, version = ToughAsNails.MOD_VERSION, name = ToughAsNails.MOD_NAME, dependencies = "required-after:Forge@[10.13.4.1614,)", guiFactory = ToughAsNails.GUI_FACTORY)
public class ToughAsNails
{
    public static final String MOD_NAME = "Tough As Nails";
    public static final String MOD_ID = "toughasnails";
    public static final String MOD_VERSION = "1.0.0-1.7.10";
    public static final String GUI_FACTORY = "toughasnails.client.gui.GuiFactory";
    
    @Instance(MOD_ID)
    public static ToughAsNails instance;
    
    @SidedProxy(clientSide = "toughasnails.core.ClientProxy", serverSide = "toughasnails.core.CommonProxy")
    public static CommonProxy proxy;
    
    public static Logger logger = LogManager.getLogger(MOD_ID);
    public static File configDirectory;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configDirectory = new File(event.getModConfigurationDirectory(), "toughasnails");

        ModConfig.preInit(configDirectory);
    	ModBlocks.init();
    	ModEntities.init();
        ModItems.init();
        ModStats.init();
        ModPotions.init();
        ModVanillaCompat.init();
        ModHandlers.init();
        
        ModCrafting.preInit();
        
        MinecraftForge.EVENT_BUS.register(new LootTableEventHandler());
        MinecraftForge.EVENT_BUS.register(new BlockHarvestEventHandler());
        
        proxy.registerRenderers();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	ModCrafting.init();
        ModConfig.init(configDirectory);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ModCompat.postInit();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new TANCommand());
    }
}
