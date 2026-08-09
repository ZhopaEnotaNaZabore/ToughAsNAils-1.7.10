package toughasnails.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import toughasnails.init.ModEnchantments;

public class EnchantmentWarming extends Enchantment
{
    public EnchantmentWarming(int id)
    {
        super(id, 10, EnumEnchantmentType.armor);
        this.setName("warming");
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment)
    {
        return super.canApplyTogether(enchantment) && enchantment != ModEnchantments.COOLING;
    }
}
