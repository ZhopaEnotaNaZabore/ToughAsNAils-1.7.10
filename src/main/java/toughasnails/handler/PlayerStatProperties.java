package toughasnails.handler;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import toughasnails.api.stat.IPlayerStat;
import toughasnails.api.stat.PlayerStatRegistry;

/** Forge 1.7.10 container for an individual player stat. */
public class PlayerStatProperties implements IExtendedEntityProperties
{
    private final String identifier;
    private final IPlayerStat stat;

    public PlayerStatProperties(String identifier)
    {
        this.identifier = identifier;
        this.stat = PlayerStatRegistry.createStat(identifier);
    }

    public IPlayerStat getStat()
    {
        return stat;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        compound.setTag(identifier, PlayerStatRegistry.writeNBT(identifier, stat));
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        if (compound.hasKey(identifier))
            PlayerStatRegistry.readNBT(identifier, stat, compound.getCompoundTag(identifier));
    }

    @Override
    public void init(Entity entity, World world) {}
}
