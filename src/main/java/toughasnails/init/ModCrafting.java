package toughasnails.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import toughasnails.api.item.TANItems;

public final class ModCrafting
{
    private ModCrafting() {}

    public static void preInit()
    {
        OreDictionary.registerOre("sugar", new ItemStack(Items.sugar));
    }

    public static void init()
    {
        GameRegistry.addSmelting(Items.water_bucket, new ItemStack(TANItems.purified_water_bucket), 0.0F);
        GameRegistry.addSmelting(new ItemStack(Items.potionitem, 1, 0), new ItemStack(TANItems.purified_water_bottle), 0.0F);
    }
}
