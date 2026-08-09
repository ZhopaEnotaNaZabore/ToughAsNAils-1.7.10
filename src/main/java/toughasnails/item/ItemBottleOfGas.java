package toughasnails.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import toughasnails.api.TANBlocks;

/** Places a gas variant and leaves the player with an empty glass bottle. */
public class ItemBottleOfGas extends Item
{
    private final Block block;

    public static enum BottleContents
    {
        BLACKDAMP, WHITEDAMP, FIREDAMP, STINKDAMP;

        public String getName()
        {
            return this.name().toLowerCase();
        }
    }

    public ItemBottleOfGas()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.block = TANBlocks.gas;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubItems(Item item, CreativeTabs tab, List subItems)
    {
        for (BottleContents contents : BottleContents.values())
        {
            subItems.add(new ItemStack(this, 1, contents.ordinal()));
        }
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

    public BottleContents getContentsType(ItemStack stack)
    {
        int metadata = stack.getItemDamage();
        BottleContents[] values = BottleContents.values();
        return metadata >= 0 && metadata < values.length ? values[metadata] : BottleContents.BLACKDAMP;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "item.bottle_of_" + getContentsType(stack).getName();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ)
    {
        if (!world.getBlock(x, y, z).isReplaceable(world, x, y, z))
        {
            x += Facing.offsetsXForSide[side];
            y += Facing.offsetsYForSide[side];
            z += Facing.offsetsZForSide[side];
        }

        if (!player.canPlayerEdit(x, y, z, side, stack) || !this.block.canPlaceBlockAt(world, x, y, z))
        {
            return false;
        }

        if (world.setBlock(x, y, z, this.block, getMetadata(stack.getItemDamage()), 3))
        {
            if (!player.capabilities.isCreativeMode)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            return true;
        }
        return false;
    }
}
