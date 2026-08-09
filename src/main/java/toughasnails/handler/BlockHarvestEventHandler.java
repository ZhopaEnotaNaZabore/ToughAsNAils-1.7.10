package toughasnails.handler;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import toughasnails.api.item.TANItems;
import toughasnails.init.ModConfig;

public class BlockHarvestEventHandler
{
	@SubscribeEvent
    public void onBlockBreak(HarvestDropsEvent event)
    {	
		int fortune = event.fortuneLevel;
		
		if (event.harvester == null || event.isSilkTouching)
		    return;
		
        if (event.block == Blocks.ice)
        {
        	if (ModConfig.gameplay.iceCubeDrops)
        	{
	        	event.drops.clear();
            event.drops.add(new ItemStack(TANItems.ice_cube, new Random().nextInt(fortune + 1) + new Random().nextInt(2)));
        	}
        }
        
        // Minecraft 1.7.10 has no vanilla magma block. The corresponding
        // magma-shard drop is therefore intentionally unavailable until a
        // replacement block is supplied by a compatibility module.
    }
}
