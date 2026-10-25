/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.temperature;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.IStatStorage;
import toughasnails.api.temperature.Temperature;
import toughasnails.temperature.modifier.TemperatureModifier.ExternalModifier;

public class TemperatureStorage implements IStatStorage<ITemperature>
{
    @Override
    public NBTTagCompound writeNBT(ITemperature instance) 
    {
        NBTTagCompound compound = new NBTTagCompound();
        
        compound.setInteger("temperatureLevel", instance.getTemperature().getRawValue());
        compound.setInteger("temperatureTimer", instance.getChangeTime());
        if (instance instanceof TemperatureHandler)
            compound.setInteger("ModifierClock", ((TemperatureHandler)instance).getModifierClock());
        
        NBTTagList externalModifierList = new NBTTagList();
        for (ExternalModifier modifier : instance.getExternalModifiers().values())
        {
            externalModifierList.appendTag(modifier.serializeNBT());
        }  
        compound.setTag("ExternalModifiers", externalModifierList);
        
        return compound;
    }

    @Override
    public void readNBT(ITemperature instance, NBTTagCompound compound) 
    {
        if (compound.hasKey("temperatureLevel"))
        {
            instance.setTemperature(new Temperature(compound.getInteger("temperatureLevel")));
            instance.setChangeTime(compound.getInteger("temperatureTimer"));
            if (instance instanceof TemperatureHandler)
                ((TemperatureHandler)instance).setModifierClock(compound.hasKey("ModifierClock")
                        ? compound.getInteger("ModifierClock") : compound.getInteger("temperatureTimer"));
            
            NBTTagList externalModifierTagList = compound.getTagList("ExternalModifiers", Constants.NBT.TAG_COMPOUND);
            Map<String, ExternalModifier> externalModifierList = Maps.newHashMap();
            for (int i = 0; i < externalModifierTagList.tagCount(); i++)
            {
                NBTTagCompound externalModifierCompound = externalModifierTagList.getCompoundTagAt(i);
                ExternalModifier modifier = new ExternalModifier();
                modifier.deserializeNBT(externalModifierCompound);
                externalModifierList.put(modifier.getName(), modifier);
            }
            instance.setExternalModifiers(externalModifierList);
        }
    }
}
