package toughasnails.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import toughasnails.api.TANBlocks;
import toughasnails.api.item.TANItems;
import toughasnails.item.ItemCanteen;
import toughasnails.item.ItemFruitJuice;
import toughasnails.item.ItemPurifiedWaterBottle;
import toughasnails.item.ItemPurifiedWaterBucket;
import toughasnails.item.ItemThermometer;
import toughasnails.util.inventory.CreativeTabTAN;
import toughasnails.core.ToughAsNails;

public class ModItems
{
    public static void init()
    {
        TANItems.tan_icon = registerItem(new Item(), "tan_icon", null);

        TANItems.wool_armor_material = EnumHelper.addArmorMaterial("WOOL", 2, new int[] {1, 1, 1, 1}, 5);
        TANItems.jelled_slime_armor_material = EnumHelper.addArmorMaterial("JELLED_SLIME", 9, new int[] {2, 5, 3, 2}, 11);
        TANItems.respirator_material = EnumHelper.addArmorMaterial("RESPIRATOR", -1, new int[] {0, 0, 0, 0}, 0);

        TANItems.thermometer = registerItem(new ItemThermometer(), "thermometer");
        TANItems.canteen = registerItem(new ItemCanteen(), "canteen");
        TANItems.purified_water_bottle = registerItem(new ItemPurifiedWaterBottle(), "purified_water_bottle");
        TANItems.purified_water_bucket = registerItem(new ItemPurifiedWaterBucket(TANBlocks.purified_water), "purified_water_bucket");
        FluidContainerRegistry.registerFluidContainer(new FluidStack(TANBlocks.purified_water_fluid, FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(TANItems.purified_water_bucket), FluidContainerRegistry.EMPTY_BUCKET);
        TANItems.fruit_juice = registerItem(new ItemFruitJuice(), "fruit_juice");

        TANItems.ice_cube = registerItem(new Item(), "ice_cube");
        TANItems.magma_shard = registerItem(new Item(), "magma_shard");
        TANItems.jelled_slime = registerItem(new Item(), "jelled_slime");
        TANItems.charcoal_filter = registerItem(new Item(), "charcoal_filter");

        TANItems.wool_helmet = registerItem(new ItemArmor(TANItems.wool_armor_material, 0, 0), "wool_helmet");
        TANItems.wool_chestplate = registerItem(new ItemArmor(TANItems.wool_armor_material, 0, 1), "wool_chestplate");
        TANItems.wool_leggings = registerItem(new ItemArmor(TANItems.wool_armor_material, 0, 2), "wool_leggings");
        TANItems.wool_boots = registerItem(new ItemArmor(TANItems.wool_armor_material, 0, 3), "wool_boots");
        TANItems.jelled_slime_helmet = registerItem(new ItemArmor(TANItems.jelled_slime_armor_material, 0, 0), "jelled_slime_helmet");
        TANItems.jelled_slime_chestplate = registerItem(new ItemArmor(TANItems.jelled_slime_armor_material, 0, 1), "jelled_slime_chestplate");
        TANItems.jelled_slime_leggings = registerItem(new ItemArmor(TANItems.jelled_slime_armor_material, 0, 2), "jelled_slime_leggings");
        TANItems.jelled_slime_boots = registerItem(new ItemArmor(TANItems.jelled_slime_armor_material, 0, 3), "jelled_slime_boots");
    }

    private static Item registerItem(Item item, String name)
    {
        return registerItem(item, name, CreativeTabTAN.instance);
    }

    private static Item registerItem(Item item, String name, CreativeTabs tab)
    {
        item.setUnlocalizedName(name);
        item.setTextureName(ToughAsNails.MOD_ID + ":" + iconName(name));
        item.setCreativeTab(tab);
        GameRegistry.registerItem(item, name);
        return item;
    }

    private static String iconName(String name)
    {
        if ("canteen".equals(name)) return "canteen_empty";
        if ("thermometer".equals(name)) return "thermometer_00";
        if ("fruit_juice".equals(name)) return "juice_apple";
        return name;
    }
}
