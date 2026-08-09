/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import toughasnails.api.item.ItemDrink;
import toughasnails.api.thirst.IDrink;
import toughasnails.item.ItemFruitJuice.JuiceType;

public class ItemFruitJuice extends ItemDrink<JuiceType>
{
    private IIcon[] icons = new IIcon[JuiceType.values().length];
    @Override
    public JuiceType getTypeFromMeta(int meta) 
    {
        return JuiceType.values()[meta % JuiceType.values().length];
    }
    
    // get the correct name for this item by looking up the meta value in the DartType enum
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "item.juice_" + getTypeFromMeta(stack.getItemDamage()).toString();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item item, CreativeTabs tab, List subItems)
    {
        if (this.getCreativeTab() == tab)
        {
            for (JuiceType juiceType : JuiceType.values())
            {
                subItems.add(new ItemStack(this, 1, juiceType.ordinal()));
            }
        }
    }
    
    public static enum JuiceType implements IDrink
    {
        APPLE(8, 0.8F), 
        BEETROOT(10, 0.8F), 
        CACTUS(9, 0.2F), 
        CARROT(8, 0.6F), 
        CHORUS_FRUIT(12, 0.6F),
        GLISTERING_MELON(16, 1.0F),
        GOLDEN_APPLE(20, 1.2F), 
        GOLDEN_CARROT(14, 1.0F), 
        MELON(8, 0.5F), 
        PUMPKIN(7, 0.7F);
        
        private int thirst;
        private float hydration;
        
        private JuiceType(int thirst, float hydration)
        {
            this.thirst = thirst;
            this.hydration = hydration;
        }
        
        @Override
        public int getThirst()
        {
            return this.thirst;
        }
        
        @Override
        public float getHydration()
        {
            return this.hydration;
        }
        
        @Override
        public float getPoisonChance() 
        {
            return 0.0F;
        }
        
        public String getName()
        {
            return this.name().toLowerCase();
        }
        
        @Override
        public String toString()
        {
            return this.getName();
        }
    }

    @Override
    public void registerIcons(IIconRegister register)
    {
        for (JuiceType type : JuiceType.values())
            icons[type.ordinal()] = register.registerIcon("toughasnails:juice_" + type.getName());
        itemIcon = icons[0];
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return icons[getTypeFromMeta(damage).ordinal()];
    }
    
    @Override
    public boolean hasEffect(ItemStack stack)
    {
        switch (getTypeFromMeta(stack.getItemDamage()))
        {
            case GLISTERING_MELON: case GOLDEN_APPLE: case GOLDEN_CARROT:
                return true;
            default:
                return super.hasEffect(stack); 
        }
    }
   
}
