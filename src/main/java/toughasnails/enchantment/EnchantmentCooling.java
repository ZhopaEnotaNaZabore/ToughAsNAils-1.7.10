package toughasnails.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import toughasnails.init.ModEnchantments;

public class EnchantmentCooling extends Enchantment
{
    public EnchantmentCooling(int id)
    {
        super(id, 10, EnumEnchantmentType.armor);
        this.setName("cooling");
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment)
    {
        return super.canApplyTogether(enchantment) && enchantment != ModEnchantments.WARMING;
    }
}
