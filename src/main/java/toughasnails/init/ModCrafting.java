package toughasnails.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import toughasnails.api.item.TANItems;

public final class ModCrafting
{
    private ModCrafting() {}

    private static ItemStack stack(String name, int count, int meta)
    {
        Item item = (Item)Item.itemRegistry.getObject(name);
        if (item == null) throw new IllegalArgumentException("Missing recipe item: " + name);
        return new ItemStack(item, count, meta);
    }

    public static void preInit()
    {
        OreDictionary.registerOre("sugar", new ItemStack(Items.sugar));
        OreDictionary.registerOre("blockCactus", new ItemStack(Blocks.cactus));
        OreDictionary.registerOre("cropCarrot", new ItemStack(Items.carrot));
        OreDictionary.registerOre("paper", new ItemStack(Items.paper));
        OreDictionary.registerOre("leather", new ItemStack(Items.leather));
        OreDictionary.registerOre("string", new ItemStack(Items.string));
        OreDictionary.registerOre("blockGlassColorless", new ItemStack(Blocks.glass));
    }

    public static void init()
    {
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 0), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", stack("minecraft:apple", 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 1), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", "cropBeetroot"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 2), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", "blockCactus"));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:campfire", 1, 0), " L ", "LLL", "###", '#', "cobblestone", 'L', "logWood"));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:canteen", 1, 0), " # ", "# #", "###", '#', "leather"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 3), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", "cropCarrot"));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:charcoal_filter", 3, 0), "#", "C", "#", '#', "paper", 'C', stack("minecraft:coal", 1, 1)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 4), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", "cropChorusFruit"));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:temperature_coil", 1, 0), "# #", "#C#", "CRC", '#', stack("toughasnails:ice_cube", 1, 0), 'C', "cobblestone", 'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 5), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", stack("minecraft:speckled_melon", 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 6), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", stack("minecraft:golden_apple", 1, 32767)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 7), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", stack("minecraft:golden_carrot", 1, 0)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:temperature_coil", 1, 1), "# #", "#C#", "CRC", '#', stack("toughasnails:magma_shard", 1, 0), 'C', "cobblestone", 'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("minecraft:ice", 1, 0), "##", "##", '#', stack("toughasnails:ice_cube", 1, 0)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:jelled_slime", 3, 0), "III", "I#I", "III", '#', "slimeball", 'I', stack("toughasnails:ice_cube", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:jelled_slime_boots", 1, 0), "# #", "# #", '#', stack("toughasnails:jelled_slime", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:jelled_slime_chestplate", 1, 0), "# #", "###", "###", '#', stack("toughasnails:jelled_slime", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:jelled_slime_helmet", 1, 0), "###", "# #", '#', stack("toughasnails:jelled_slime", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:jelled_slime_leggings", 1, 0), "###", "# #", "# #", '#', stack("toughasnails:jelled_slime", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.magma_cream), "##", "##", '#', stack("toughasnails:magma_shard", 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 8), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", stack("minecraft:melon", 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:fruit_juice", 1, 9), stack("toughasnails:purified_water_bottle", 1, 0), "sugar", stack("minecraft:pumpkin", 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:purified_water_bottle", 1, 0), stack("minecraft:potion", 1, 0), stack("toughasnails:charcoal_filter", 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(stack("toughasnails:canteen", 1, 2), stack("toughasnails:charcoal_filter", 1, 0), stack("toughasnails:canteen", 1, 1)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:rain_collector", 1, 0), "I#I", "CBC", "CCC", '#', "string", 'I', "ingotIron", 'C', "cobblestone", 'B', stack("minecraft:bucket", 1, 0)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:thermometer", 1, 0), " # ", "#R#", " # ", '#', "blockGlassColorless", 'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:wool_boots", 1, 0), "# #", "# #", '#', stack("minecraft:wool", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:wool_helmet", 1, 0), "###", "# #", '#', stack("minecraft:wool", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:wool_chestplate", 1, 0), "# #", "###", "###", '#', stack("minecraft:wool", 1, 32767)));
        GameRegistry.addRecipe(new ShapedOreRecipe(stack("toughasnails:wool_leggings", 1, 0), "###", "# #", "# #", '#', stack("minecraft:wool", 1, 32767)));
        // Magma blocks do not exist in 1.7.10: use reversible magma cream conversion.
        GameRegistry.addShapelessRecipe(new ItemStack(TANItems.magma_shard, 4), Items.magma_cream);
        GameRegistry.addSmelting(Items.water_bucket, new ItemStack(TANItems.purified_water_bucket), 0.0F);
        GameRegistry.addSmelting(new ItemStack(Items.potionitem, 1, 0), new ItemStack(TANItems.purified_water_bottle), 0.0F);
    }
}
