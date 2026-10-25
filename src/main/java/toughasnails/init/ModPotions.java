package toughasnails.init;

import static toughasnails.api.TANPotions.cold_resistance;
import static toughasnails.api.TANPotions.heat_resistance;
import static toughasnails.api.TANPotions.hydration;
import static toughasnails.api.TANPotions.hyperthermia;
import static toughasnails.api.TANPotions.hypothermia;
import static toughasnails.api.TANPotions.thirst;

import net.minecraft.potion.Potion;
import toughasnails.potion.PotionColdResistance;
import toughasnails.potion.PotionHeatResistance;
import toughasnails.potion.PotionHydration;
import toughasnails.potion.PotionHyperthermia;
import toughasnails.potion.PotionHypothermia;
import toughasnails.potion.PotionThirst;

public class ModPotions
{
    public static void init()
    {
        hypothermia = registerPotion(new PotionHypothermia(24).setPotionName("potion.hypothermia"));
        hyperthermia = registerPotion(new PotionHyperthermia(25).setPotionName("potion.hyperthermia"));
        thirst = registerPotion(new PotionThirst(26).setPotionName("potion.thirst"));
        cold_resistance = registerPotion(new PotionColdResistance(27).setPotionName("potion.cold_resistance"));
        heat_resistance = registerPotion(new PotionHeatResistance(28).setPotionName("potion.heat_resistance"));
        hydration = registerPotion(new PotionHydration(29).setPotionName("potion.hydration"));
        toughasnails.util.PotionBrewingRecipe.register();
    }

    private static Potion registerPotion(Potion potion)
    {
        return potion;
    }
}
