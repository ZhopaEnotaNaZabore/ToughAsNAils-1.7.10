package toughasnails.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import toughasnails.api.ITANBlock;
import toughasnails.api.item.TANItems;
import toughasnails.item.ItemTANBlock;
import toughasnails.core.TANRenderIds;

/** A four-level rain collector. In 1.7.10 its level is stored in block metadata. */
public class BlockRainCollector extends Block implements ITANBlock
{
    public BlockRainCollector()
    {
        super(Material.iron);
        setHardness(2.0F);
    }

    @Override
    public Class<? extends ItemBlock> getItemClass()
    {
        return ItemTANBlock.class;
    }

    @Override
    public int[] getPresetMetadatas()
    {
        return new int[] {0};
    }

    @Override
    public String getStateName(int metadata)
    {
        return "";
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType() { return TANRenderIds.BLOCK; }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null)
            return true;

        int level = world.getBlockMetadata(x, y, z);
        Item item = held.getItem();

        if (item == Items.bucket)
        {
            if (level == 3 && !world.isRemote)
            {
                if (!player.capabilities.isCreativeMode)
                    replaceHeldItem(player, held, new ItemStack(TANItems.purified_water_bucket));
                setWaterLevel(world, x, y, z, 0);
            }
            return true;
        }

        if (item == Items.glass_bottle)
        {
            if (level > 0 && !world.isRemote)
            {
                if (!player.capabilities.isCreativeMode)
                    replaceHeldItem(player, held, new ItemStack(TANItems.purified_water_bottle));
                setWaterLevel(world, x, y, z, level - 1);
            }
            return true;
        }

        if (item == TANItems.canteen)
        {
            if (level > 0 && !world.isRemote)
            {
                if (!player.capabilities.isCreativeMode)
                    held.setItemDamage(2);
                setWaterLevel(world, x, y, z, level - 1);
            }
            return true;
        }

        return false;
    }

    private void replaceHeldItem(EntityPlayer player, ItemStack held, ItemStack replacement)
    {
        --held.stackSize;
        if (held.stackSize <= 0)
            player.inventory.setInventorySlotContents(player.inventory.currentItem, replacement);
        else if (!player.inventory.addItemStackToInventory(replacement))
            player.dropPlayerItemWithRandomChoice(replacement, false);
    }

    public void setWaterLevel(World world, int x, int y, int z, int level)
    {
        world.setBlockMetadataWithNotify(x, y, z, Math.max(0, Math.min(3, level)), 2);
        world.func_147453_f(x, y, z, this);
    }

    @Override
    public void fillWithRain(World world, int x, int y, int z)
    {
        if (world.rand.nextInt(2) == 0 && world.getBlockMetadata(x, y, z) < 3)
        {
            float temperature = world.getBiomeGenForCoords(x, z).getFloatTemperature(x, y, z);
            if (temperature >= 0.15F)
                setWaterLevel(world, x, y, z, world.getBlockMetadata(x, y, z) + 1);
        }
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
        return world.getBlockMetadata(x, y, z);
    }
}
