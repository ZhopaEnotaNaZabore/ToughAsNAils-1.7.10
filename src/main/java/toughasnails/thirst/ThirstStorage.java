/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.thirst;

import net.minecraft.nbt.NBTTagCompound;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.stat.IStatStorage;

public class ThirstStorage implements IStatStorage<IThirst>
{
    @Override
    public NBTTagCompound writeNBT(IThirst instance) 
    {
        NBTTagCompound compound = new NBTTagCompound();
        
        compound.setInteger("thirstLevel", instance.getThirst());
        compound.setInteger("thirstTimer", instance.getChangeTime());
        compound.setFloat("thirstHydrationLevel", instance.getHydration());
        compound.setFloat("thirstExhaustionLevel", instance.getExhaustion());

        return compound;
    }

    @Override
    public void readNBT(IThirst instance, NBTTagCompound compound) 
    {
        if (compound.hasKey("thirstLevel"))
        {
            instance.setThirst(compound.getInteger("thirstLevel"));
            instance.setHydration(compound.getFloat("thirstHydrationLevel"));
            instance.setExhaustion(compound.getFloat("thirstExhaustionLevel"));
            instance.setChangeTime(compound.getInteger("thirstTimer"));
        }

    }
}
