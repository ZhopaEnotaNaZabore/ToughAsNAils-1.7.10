/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.config;

import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import toughasnails.api.config.SyncedConfig;
import toughasnails.core.ToughAsNails;
import toughasnails.handler.PacketHandler;
import toughasnails.network.message.MessageSyncConfigs;

public class SyncedConfigHandler
{
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.worldObj;
        
        if (!world.isRemote)
        {
            NBTTagCompound nbtOptions = new NBTTagCompound();
            
            for (Entry<String, SyncedConfig.SyncedConfigEntry> entry : SyncedConfig.optionsToSync.entrySet())
            {
                nbtOptions.setString(entry.getKey(), entry.getValue().value);
            }
            
            IMessage message = new MessageSyncConfigs(nbtOptions);
            PacketHandler.instance.sendTo(message, (EntityPlayerMP)player);        
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if (event.world.isRemote && (Minecraft.getMinecraft().getNetHandler() == null
                || !Minecraft.getMinecraft().getNetHandler().getNetworkManager().isChannelOpen()))
        {
            SyncedConfig.restoreDefaults();
            ToughAsNails.logger.info("TAN configuration restored to local values");
        }
    }

}
