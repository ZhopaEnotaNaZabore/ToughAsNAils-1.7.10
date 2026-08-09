package toughasnails.api.stat;

import net.minecraft.nbt.NBTTagCompound;

/** Serializes a player stat for the 1.7.10 extended-properties system. */
public interface IStatStorage<T extends IPlayerStat>
{
    NBTTagCompound writeNBT(T instance);
    void readNBT(T instance, NBTTagCompound data);
}
