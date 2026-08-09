package toughasnails.api.stat;

import java.util.HashMap;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import toughasnails.handler.PlayerStatProperties;

public class PlayerStatRegistry
{
    private static HashMap<String, Class<? extends StatHandlerBase>> playerStats = Maps.newHashMap();
    private static HashMap<String, IStatStorage<? extends IPlayerStat>> statStorages = Maps.newHashMap();
    
    public static <T extends IPlayerStat> void addStat(Class<T> capabilityClass, IStatStorage<T> storage, Class<? extends StatHandlerBase> implementationClass)
    {
        String identifier = capabilityClass.getName().intern();
        
        if (identifier == null)
        {
            throw new RuntimeException("Stat identifier cannot be null!");
        }
        else if (playerStats.containsKey(identifier))
        {
            throw new RuntimeException("Stat with identifier " + identifier + " already exists!");
        } 

        playerStats.put(identifier, implementationClass);
        statStorages.put(identifier, storage);
    }
    
    public static IStatStorage<IPlayerStat> getStorage(String identifier)
    {
        return (IStatStorage<IPlayerStat>) statStorages.get(identifier);
    }
    
    public static HashMap<String, Class<? extends StatHandlerBase>> getPlayerStats()
    {
        return playerStats;
    }

    public static IPlayerStat createStat(String identifier)
    {
        try
        {
            return playerStats.get(identifier).newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Could not create player stat " + identifier, e);
        }
    }

    public static IPlayerStat getStat(net.minecraft.entity.player.EntityPlayer player, String identifier)
    {
        PlayerStatProperties properties = (PlayerStatProperties) player.getExtendedProperties(identifier);
        return properties == null ? null : properties.getStat();
    }

    public static NBTTagCompound writeNBT(String identifier, IPlayerStat stat)
    {
        return getStorage(identifier).writeNBT(stat);
    }

    public static void readNBT(String identifier, IPlayerStat stat, NBTTagCompound data)
    {
        getStorage(identifier).readNBT(stat, data);
    }
}
