package toughasnails.temperature.modifier;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import toughasnails.api.temperature.IModifierMonitor;
import toughasnails.api.temperature.Temperature;
import toughasnails.config.json.ArmorTemperatureData;
import toughasnails.init.ModConfig;
import toughasnails.init.ModEnchantments;
import toughasnails.util.config.NBTUtilExt;

import java.util.Map;

public class ArmorModifier extends TemperatureModifier {
    public ArmorModifier(String id) {
        super(id);
    }

    @Override
    public Temperature applyPlayerModifiers(EntityPlayer player, Temperature initialTemperature, IModifierMonitor monitor) {
        int newTemperatureLevel = initialTemperature.getRawValue();

        InventoryPlayer inventory = player.inventory;

        armorcycle:
        for(int i = 0; i < 4; i++) {
            ItemStack armor = inventory.armorInventory[i];
            if (armor == null) continue;
            //Checking armor from config file
            String name = (String)net.minecraft.item.Item.itemRegistry.getNameForObject(armor.getItem());
            for(int j = 0; j < ModConfig.armorTemperatureData.size(); j++) {
                ArmorTemperatureData atd = ModConfig.armorTemperatureData.get(j);
                int index = atd.names.indexOf(name);
                if(index >= 0) {
                    NBTTagCompound compare = atd.nbts == null ? null : atd.getNBTTagCompounds().get(index);
                    if(NBTUtilExt.areNBTsEqualOrNull(armor.getTagCompound(), compare)) {
                        newTemperatureLevel += atd.modifier;
                        continue armorcycle;
                    }
                }
            }
            //Checking for enchantments
            if(EnchantmentHelper.getEnchantmentLevel(ModEnchantments.COOLING.effectId, armor) > 0) {
                newTemperatureLevel -= 1;
            } else if(EnchantmentHelper.getEnchantmentLevel(ModEnchantments.WARMING.effectId, armor) > 0) {
                newTemperatureLevel += 1;
            }
        }

        monitor.addEntry(new IModifierMonitor.Context(this.getId(), "Armor", initialTemperature, new Temperature(newTemperatureLevel)));
        return new Temperature(newTemperatureLevel);
    }

    @Override
    public boolean isPlayerSpecific() {
        return true;
    }
}
