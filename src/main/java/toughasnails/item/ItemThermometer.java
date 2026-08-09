package toughasnails.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureScale;

/** The 1.7.10 client renders thermometer variants from its metadata. */
public class ItemThermometer extends Item
{
    private IIcon[] icons = new IIcon[21];
    public ItemThermometer()
    {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        if (world.isRemote && held)
        {
            int temperature = TemperatureHelper.getTargetAtPos(world, (int)entity.posX, (int)entity.posY, (int)entity.posZ, null).getRawValue();
            int variant = Math.max(0, Math.min(20, Math.round(temperature * 20.0F / TemperatureScale.getScaleTotal())));
            stack.setItemDamage(variant);
        }
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

    @Override
    public void registerIcons(IIconRegister register)
    {
        for (int i = 0; i < icons.length; i++)
            icons[i] = register.registerIcon("toughasnails:thermometer_" + (i < 10 ? "0" : "") + i);
        itemIcon = icons[0];
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return icons[Math.max(0, Math.min(icons.length - 1, damage))];
    }
}
