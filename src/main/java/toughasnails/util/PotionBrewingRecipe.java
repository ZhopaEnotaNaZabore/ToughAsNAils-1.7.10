package toughasnails.util;

import java.util.Map;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import toughasnails.api.TANPotions;
import toughasnails.api.item.TANItems;

/** Native 1.7.10 brewing, including its timer, automation and splash conversion. */
public final class PotionBrewingRecipe
{
    private PotionBrewingRecipe() {}

    public static void register()
    {
        // Low-bit patterns 7, 11 and 15 are unused by vanilla 1.7.10.
        register(TANItems.ice_cube, TANPotions.heat_resistance, 7, false);
        register(TANItems.magma_shard, TANPotions.cold_resistance, 11, false);
        register(TANItems.jelled_slime, TANPotions.hydration, 15, true);
    }

    private static void register(Item ingredient, Potion potion, int pattern, boolean amplify)
    {
        Map<Integer, String> requirements = ReflectionHelper.getPrivateValue(
                PotionHelper.class, null, "potionRequirements", "field_77927_l");
        StringBuilder condition = new StringBuilder();
        StringBuilder transform = new StringBuilder();
        for (int bit = 0; bit < 4; bit++)
        {
            boolean set = (pattern & (1 << bit)) != 0;
            if (bit != 0) condition.append(" & ");
            condition.append(set ? "" : "!").append(bit);
            transform.append(set ? "+" : "-").append(bit);
        }
        // Require awkward potion, then use vanilla duration and splash bits.
        transform.append("&4-4+13");
        if (!potion.isInstant()) condition.append(" & 0+6");
        requirements.put(potion.id, condition.toString());
        ingredient.setPotionEffect(transform.toString());
        if (amplify)
        {
            Map<Integer, String> amplifiers = ReflectionHelper.getPrivateValue(
                    PotionHelper.class, null, "potionAmplifiers", "field_77928_m");
            amplifiers.put(potion.id, "5");
        }
    }
}
