/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.config.SyncedConfig.SyncedConfigEntry;
import toughasnails.core.ToughAsNails;

public class MessageSyncConfigs implements IMessage, IMessageHandler<MessageSyncConfigs, IMessage>
{
    public NBTTagCompound nbtOptions;
    
    public MessageSyncConfigs() {}
    
    public MessageSyncConfigs(NBTTagCompound nbtOptions)
    {
        this.nbtOptions = nbtOptions;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) 
    {
        this.nbtOptions = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
        ByteBufUtils.writeTag(buf, nbtOptions);
    }

    @Override
    public IMessage onMessage(MessageSyncConfigs message, MessageContext ctx)
    {
        if (ctx.side == Side.CLIENT)
        {
            for (Object rawKey : message.nbtOptions.func_150296_c()) {
                    String key = (String)rawKey;
                    SyncedConfigEntry entry = SyncedConfig.optionsToSync.get(key);

                    if (entry == null) {
                        ToughAsNails.logger.error("Option " + key + " does not exist locally!");
                        continue; // Don't try access non-exist option
                    }

                    entry.value = message.nbtOptions.getString(key);
                    ToughAsNails.logger.info("TAN configuration synchronized with the server");
                }
        }
        
        return null;
    }
}
