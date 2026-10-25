package toughasnails;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.config.TemperatureOption;
import toughasnails.api.item.TANItems;
import toughasnails.init.ModCrafting;
import toughasnails.init.ModPotions;
import toughasnails.temperature.TemperatureHandler;
import toughasnails.temperature.TemperatureStorage;
import toughasnails.thirst.ThirstHandler;

/** Executable regression checks; run with gradlew portRegression. */
public final class PortRegression {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(cpw.mods.fml.relauncher.FMLRelaunchLog.class,
                null, cpw.mods.fml.relauncher.Side.SERVER, "side");
        cpw.mods.fml.common.Loader.injectData("7", "10", "99", "99", "1.7.10", "9.05",
                new java.io.File("build"), java.util.Collections.emptyList());
        cpw.mods.fml.common.Loader loader = cpw.mods.fml.common.Loader.instance();
        cpw.mods.fml.common.LoadController controller = new cpw.mods.fml.common.LoadController(loader);
        cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(cpw.mods.fml.common.Loader.class,
                loader, controller, "modController");
        Bootstrap.func_151354_b();
        cpw.mods.fml.common.ModMetadata metadata = new cpw.mods.fml.common.ModMetadata();
        metadata.modId = "toughasnails";
        cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(cpw.mods.fml.common.LoadController.class,
                controller, new cpw.mods.fml.common.DummyModContainer(metadata), "activeContainer");
        int id = 5000;
        for (Field field : TANItems.class.getFields()) {
            if (field.getType() == Item.class) {
                Item item = new Item();
                Item.itemRegistry.addObject(id++, "toughasnails:" + field.getName(), item);
                field.set(null, item);
            }
        }
        for (String name : new String[]{"campfire", "temperature_coil", "rain_collector"})
            Item.itemRegistry.addObject(id++, "toughasnails:" + name, new Item());

        ModPotions.init();
        checkBrew(TANItems.ice_cube, TANPotions.heat_resistance.id, false);
        checkBrew(TANItems.magma_shard, TANPotions.cold_resistance.id, false);
        checkBrew(TANItems.jelled_slime, TANPotions.hydration.id, true);
        check(PotionHelper.getPotionEffects(16, false) == null, "Awkward potion gained an effect");
        List vanilla = PotionHelper.getPotionEffects(8195, false);
        check(vanilla.size() == 1 && ((PotionEffect)vanilla.get(0)).getPotionID() == 12,
                "Vanilla fire resistance changed");

        int before = CraftingManager.getInstance().getRecipeList().size();
        ModCrafting.preInit();
        ModCrafting.init();
        List recipes = CraftingManager.getInstance().getRecipeList();
        check(recipes.size() - before == 31, "Missing crafting recipes");
        for (int i = before; i < recipes.size(); i++)
            check(((IRecipe)recipes.get(i)).getRecipeOutput().getItem() != null, "Invalid recipe output");
        checkCraft(TANItems.canteen, 0, 1,
                null, Items.leather, null, Items.leather, null, Items.leather,
                Items.leather, Items.leather, Items.leather);
        checkCraft(TANItems.charcoal_filter, 0, 3,
                Items.paper, null, null, new ItemStack(Items.coal, 1, 1), null, null,
                Items.paper, null, null);
        checkCraft(TANItems.canteen, 2, 1,
                new ItemStack(TANItems.canteen, 1, 1), TANItems.charcoal_filter);
        checkCraft(TANItems.fruit_juice, 0, 1, TANItems.purified_water_bottle, Items.sugar, Items.apple);
        checkCraft(TANItems.magma_shard, 0, 4, Items.magma_cream);

        SyncedConfig.addOption(GameplayOption.ENABLE_THIRST, "true");
        ThirstHandler thirst = new ThirstHandler();
        thirst.onSendClientUpdate();
        check(!thirst.hasChanged(), "Fresh thirst snapshot is dirty");
        thirst.setHydration(0);
        check(thirst.hasChanged(), "Hydration-only change was lost");
        thirst.onSendClientUpdate();
        check(!thirst.hasChanged(), "Hydration snapshot never settles");

        TemperatureHandler temperature = new TemperatureHandler();
        SyncedConfig.addOption(TemperatureOption.BASE_TEMPERATURE_CHANGE_TICKS, "600");
        SyncedConfig.addOption(TemperatureOption.MAX_RATE_MODIFIER, "0");
        Method interval = TemperatureHandler.class.getDeclaredMethod("getChangeTicks", int.class);
        interval.setAccessible(true);
        int target = temperature.getTemperature().getRawValue();
        check((Integer)interval.invoke(temperature, target) == 600, "Wrong base interval");
        Method tick = TemperatureHandler.class.getDeclaredMethod("tickModifiers");
        tick.setAccessible(true);
        temperature.applyModifier("test", 3, -500, 60);
        check((Integer)interval.invoke(temperature, target) == 100, "Coil rate ignored");
        for (int i = 0; i < 30; i++) {
            temperature.setChangeTime(0);
            tick.invoke(temperature);
        }
        TemperatureStorage storage = new TemperatureStorage();
        NBTTagCompound saved = storage.writeNBT(temperature);
        TemperatureHandler restored = new TemperatureHandler();
        storage.readNBT(restored, saved);
        check(restored.getModifierClock() == 30, "Modifier clock not persisted");
        for (int i = 0; i < 30; i++) tick.invoke(restored);
        check(restored.hasModifier("test"), "Modifier expired before 60 ticks");
        tick.invoke(restored);
        check(!restored.hasModifier("test"), "Modifier survived its expiry after timer resets/reload");
        check((Integer)interval.invoke(restored, target) == 600, "Expired coil rate remains active");
        System.out.println("Port regression checks passed: recipes, brewing, synchronization, modifier expiry/save.");
    }

    private static void checkCraft(Item output, int meta, int count, Object... slots) {
        net.minecraft.inventory.InventoryCrafting grid = new net.minecraft.inventory.InventoryCrafting(
                new net.minecraft.inventory.Container() {
                    public boolean canInteractWith(net.minecraft.entity.player.EntityPlayer player) { return true; }
                }, 3, 3);
        for (int i = 0; i < slots.length; i++) {
            Object value = slots[i];
            if (value != null) grid.setInventorySlotContents(i,
                    value instanceof ItemStack ? (ItemStack)value : new ItemStack((Item)value));
        }
        ItemStack result = CraftingManager.getInstance().findMatchingRecipe(grid, null);
        check(result != null && result.getItem() == output && result.getItemDamage() == meta
                && result.stackSize == count, "Crafting grid produced the wrong item");
    }

    private static void checkBrew(Item ingredient, int effectId, boolean instant) {
        String formula = ingredient.getPotionEffect(new ItemStack(ingredient));
        int meta = PotionHelper.applyIngredient(16, formula);
        List effects = PotionHelper.getPotionEffects(meta, false);
        check(effects != null && effects.size() == 1, "Missing or overlapping potion effect");
        PotionEffect effect = (PotionEffect)effects.get(0);
        check(effect.getPotionID() == effectId, "Wrong brewed effect");
        check(PotionHelper.getPotionEffects(PotionHelper.applyIngredient(0, formula), false) == null,
                "Water bottle bypasses awkward potion requirement");
        int splash = PotionHelper.applyIngredient(meta, PotionHelper.gunpowderEffect);
        check((splash & 16384) != 0 && ((PotionEffect)PotionHelper.getPotionEffects(splash, false).get(0))
                .getPotionID() == effectId, "Splash conversion failed");
        if (!instant) {
            int extended = PotionHelper.applyIngredient(meta, PotionHelper.redstoneEffect);
            check(((PotionEffect)PotionHelper.getPotionEffects(extended, false).get(0)).getDuration()
                    > effect.getDuration(), "Redstone extension failed");
        } else {
            int strong = PotionHelper.applyIngredient(meta, PotionHelper.glowstoneEffect);
            check(((PotionEffect)PotionHelper.getPotionEffects(strong, false).get(0)).getAmplifier() == 1,
                    "Glowstone amplification failed");
        }
    }
}
