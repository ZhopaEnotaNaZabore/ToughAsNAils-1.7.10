/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.api.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.TANPotions;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.IDrink;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.api.config.GameplayOption;
import toughasnails.thirst.ThirstHandler;

public abstract class ItemDrink<T extends Enum<T> & IDrink> extends Item
{
    public ItemDrink()
    {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        ThirstHandler thirstHandler = (ThirstHandler)ThirstHelper.getThirstData(player);
        
        if (thirstHandler.isThirsty())
        {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        
        return stack;
    }
    
    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer entity)
    {
        if (!world.isRemote && entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            IThirst thirst = ThirstHelper.getThirstData(player);
            T type = getTypeFromMeta(stack.getItemDamage());
            
            if (!player.capabilities.isCreativeMode)
            {
                --stack.stackSize;
            }
            
            thirst.addStats(type.getThirst(), type.getHydration());
            addEffects(player, type);
            
            if (!player.capabilities.isCreativeMode)
            {
                if (stack.stackSize <= 0)
                {
                    return new ItemStack(Items.glass_bottle);
                }
                
                player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
        }

        return stack;
    }
    
    public void addEffects(EntityPlayer player, T type)
    {
        if (player.worldObj.rand.nextFloat() < type.getPoisonChance() && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
        {
            player.addPotionEffect(new PotionEffect(TANPotions.thirst.id, 600));
        }
    }
    
    public abstract T getTypeFromMeta(int meta);
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.drink;
    }
    
    // default behavior in Item is to return 0, but the meta value is important here because it determines which dart type to use
    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }
}
