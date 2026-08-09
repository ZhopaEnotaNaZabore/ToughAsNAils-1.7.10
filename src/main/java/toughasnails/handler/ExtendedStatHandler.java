package toughasnails.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import toughasnails.api.stat.IPlayerStat;
import toughasnails.api.stat.PlayerStatRegistry;
import toughasnails.api.stat.StatHandlerBase;
import toughasnails.core.ToughAsNails;

public class ExtendedStatHandler
{
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.entity;

            for (String identifier : PlayerStatRegistry.getPlayerStats().keySet()) {
                if (player.getExtendedProperties(identifier) == null)
                    player.registerExtendedProperties(identifier, new PlayerStatProperties(identifier));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(Clone event)
    {
        for (String identifier : PlayerStatRegistry.getPlayerStats().keySet())
        {
            IPlayerStat oldStat = PlayerStatRegistry.getStat(event.original, identifier);
            IPlayerStat newStat = PlayerStatRegistry.getStat(event.entityPlayer, identifier);
            if (oldStat != null && newStat != null)
                PlayerStatRegistry.readNBT(identifier, newStat, PlayerStatRegistry.writeNBT(identifier, oldStat));
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.worldObj;
        
        if (!world.isRemote)
        {
            for (String identifier : PlayerStatRegistry.getPlayerStats().keySet())
            {
                StatHandlerBase stat = (StatHandlerBase)PlayerStatRegistry.getStat(player, identifier);
                stat.onSendClientUpdate();
                PacketHandler.instance.sendTo(stat.createUpdateMessage(), (EntityPlayerMP)player);
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.worldObj;

        if (!world.isRemote)
        {
            for (String identifier : PlayerStatRegistry.getPlayerStats().keySet())
            {
                IPlayerStat stat = PlayerStatRegistry.getStat(player, identifier);

                stat.update(player, world, event.phase);

                if (event.phase == Phase.START)
                {
                    if (stat.hasChanged())
                    {
                        stat.onSendClientUpdate();
                        PacketHandler.instance.sendTo(stat.createUpdateMessage(), (EntityPlayerMP)player);
                    }
                }
            }
        }
    }
}
