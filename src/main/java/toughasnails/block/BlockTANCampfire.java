package toughasnails.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import toughasnails.api.ITANBlock;
import toughasnails.item.ItemTANBlock;
import toughasnails.core.TANRenderIds;

/** Campfire state packed into metadata: bits 1-3 are age, bit 0 is burning. */
public class BlockTANCampfire extends Block implements ITANBlock
{
    private static final AxisAlignedBB SELECTION_BOX = AxisAlignedBB.getBoundingBox(0.1D, 0.0D, 0.1D, 0.9D, 0.5D, 0.9D);

    public BlockTANCampfire()
    {
        super(Material.rock);
        setTickRandomly(true);
        setHardness(0.7F);
        setStepSound(soundTypeStone);
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

    private static int getAge(int metadata)
    {
        return metadata >> 1;
    }

    private static boolean isBurning(int metadata)
    {
        return (metadata & 1) != 0;
    }

    private static int state(int age, boolean burning)
    {
        return (Math.max(0, Math.min(7, age)) << 1) | (burning ? 1 : 0);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        int age = getAge(metadata);
        if (!isBurning(metadata))
            return;

        if (world.isRaining() && world.canBlockSeeTheSky(x, y + 1, z))
        {
            world.setBlockMetadataWithNotify(x, y, z, state(7, false), 2);
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
            for (int i = 0; i < 8; ++i)
                world.spawnParticle("largesmoke", x + 0.75F - random.nextFloat() / 2.0F, y + 0.9F, z + 0.75F - random.nextFloat() / 2.0F, 0.0D, 0.0D, 0.0D);
            return;
        }

        int nextAge = age + 1;
        world.setBlockMetadataWithNotify(x, y, z, state(nextAge, nextAge < 7), 2);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return isBurning(world.getBlockMetadata(x, y, z)) ? 15 : 0;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return SELECTION_BOX.getOffsetBoundingBox(x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        if (isBurning(world.getBlockMetadata(x, y, z)) && entity instanceof EntityLivingBase)
            entity.setFire(1);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null)
            return false;

        int metadata = world.getBlockMetadata(x, y, z);
        int age = getAge(metadata);
        Item item = held.getItem();

        if (age == 0 && !isBurning(metadata) && !(world.isRaining() && world.canBlockSeeTheSky(x, y + 1, z)))
        {
            if (item == Items.stick)
            {
                if (world.rand.nextInt(10) == 0)
                    world.setBlockMetadataWithNotify(x, y, z, state(0, true), 2);
                consumeHeldItem(player, held, false);
                return true;
            }
            if (item == Items.flint_and_steel)
            {
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                world.setBlockMetadataWithNotify(x, y, z, state(0, true), 2);
                held.damageItem(1, player);
                return true;
            }
        }
        else if (age > 0 && isLog(held))
        {
            consumeHeldItem(player, held, false);
            world.setBlockMetadataWithNotify(x, y, z, state(Math.max(age - 2, 0), isBurning(metadata)), 2);
            return true;
        }

        return false;
    }

    private static boolean isLog(ItemStack stack)
    {
        List<ItemStack> logs = OreDictionary.getOres("logWood");
        for (ItemStack log : logs)
            if (log.getItem() == stack.getItem())
                return true;
        return false;
    }

    private static void consumeHeldItem(EntityPlayer player, ItemStack stack, boolean ignoreCreative)
    {
        if (!ignoreCreative && player.capabilities.isCreativeMode)
            return;
        if (--stack.stackSize <= 0)
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        if (!isBurning(world.getBlockMetadata(x, y, z)))
            return;

        if (random.nextInt(24) == 0)
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for (int i = 0; i < 3; ++i)
            world.spawnParticle("flame", x + 0.75F - random.nextFloat() / 2.0F, y + 0.25F + random.nextFloat() / 2.0F, z + 0.75F - random.nextFloat() / 2.0F, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", x + 0.75F - random.nextFloat() / 2.0F, y + 0.9F, z + 0.75F - random.nextFloat() / 2.0F, 0.0D, 0.0D, 0.0D);
        if (random.nextBoolean())
            world.spawnParticle("largesmoke", x + 0.75F - random.nextFloat() / 2.0F, y + 0.9F, z + 0.75F - random.nextFloat() / 2.0F, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
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
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z) && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }
}
