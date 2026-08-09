package toughasnails.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import toughasnails.api.item.TANItems;

/** 1.7.10 has no JSON loot tables; ChestGenHooks is its equivalent for bonus chests. */
public class LootTableEventHandler
{
    public LootTableEventHandler()
    {
        ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST,
                new WeightedRandomChestContent(TANItems.canteen, 0, 1, 1, 10));
    }
}
