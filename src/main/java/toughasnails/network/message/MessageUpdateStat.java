package toughasnails.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import toughasnails.api.stat.IPlayerStat;
import toughasnails.api.stat.PlayerStatRegistry;
import toughasnails.api.stat.StatHandlerBase;

public class MessageUpdateStat implements IMessage, IMessageHandler<MessageUpdateStat, IMessage>
{
    public String identifier;
    public NBTTagCompound data;
    
    public MessageUpdateStat() {}
    
    public MessageUpdateStat(String identifier, NBTTagCompound data)
    {
        if (data == null) throw new IllegalArgumentException("Data cannot be null!");
        
        this.identifier = identifier;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.identifier = ByteBufUtils.readUTF8String(buf);
        this.data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.identifier);
        ByteBufUtils.writeTag(buf, this.data);
    }
    
    @Override
    public IMessage onMessage(MessageUpdateStat message, MessageContext ctx)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        
        if (player != null)
        {
            IPlayerStat stat = PlayerStatRegistry.getStat(player, message.identifier);
            if (stat != null)
                PlayerStatRegistry.readNBT(message.identifier, stat, message.data);
        }
        
        return null;
    }
}
