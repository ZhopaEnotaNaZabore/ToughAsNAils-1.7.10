package toughasnails.init;

import net.minecraft.enchantment.Enchantment;
import toughasnails.enchantment.EnchantmentCooling;
import toughasnails.enchantment.EnchantmentWarming;

/** 1.7.10 registrations use numeric enchantment identifiers rather than registries. */
public final class ModEnchantments
{
    public static final Enchantment WARMING = new EnchantmentWarming(96);
    public static final Enchantment COOLING = new EnchantmentCooling(97);

    private ModEnchantments() {}
}
