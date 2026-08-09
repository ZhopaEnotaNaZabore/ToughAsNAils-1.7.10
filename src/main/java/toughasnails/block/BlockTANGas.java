package toughasnails.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import toughasnails.api.ITANBlock;
import toughasnails.item.ItemTANBlock;

/** Invisible, replaceable gas block. Its four variants are stored in metadata. */
public class BlockTANGas extends Block implements ITANBlock
{
    public static enum GasType
    {
        BLACKDAMP, WHITEDAMP, FIREDAMP, STINKDAMP;

        public String getName()
        {
            return this.name().toLowerCase();
        }
    }

    public BlockTANGas()
    {
        super(Material.air);
        this.setHardness(0.0F);
    }

    @Override
    public Class<? extends ItemBlock> getItemClass()
    {
        return ItemTANBlock.class;
    }

    @Override
    public int[] getPresetMetadatas()
    {
        return new int[] { 0, 1, 2, 3 };
    }

    @Override
    public String getStateName(int metadata)
    {
        return getGasType(metadata).getName() + "_block";
    }

    public GasType getGasType(int metadata)
    {
        GasType[] values = GasType.values();
        return metadata >= 0 && metadata < values.length ? values[metadata] : GasType.BLACKDAMP;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        if (random.nextInt(12) == 0)
        {
            world.spawnParticle("spell", x + 0.75F - random.nextFloat() / 2.0F, y + 0.9F,
                    z + 0.75F - random.nextFloat() / 2.0F, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getRenderType()
    {
        return -1;
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
    public boolean canDropFromExplosion(Explosion explosion)
    {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {}

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z)
    {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        GasType type = getGasType(world.getBlockMetadata(x, y, z));
        return type == GasType.FIREDAMP || type == GasType.STINKDAMP ? 2000 : 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return getFlammability(world, x, y, z, face);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
    {
        if (!world.isRemote && getGasType(world.getBlockMetadata(x, y, z)) == GasType.STINKDAMP)
        {
            explode(world, x, y, z, world.getBlockMetadata(x, y, z));
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        GasType type = getGasType(world.getBlockMetadata(x, y, z));
        if (type == GasType.WHITEDAMP && entity instanceof EntityLivingBase)
        {
            EntityLivingBase living = (EntityLivingBase) entity;
            living.addPotionEffect(new PotionEffect(Potion.weakness.id, 500));
            living.addPotionEffect(new PotionEffect(Potion.hunger.id, 500));
            living.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 500));
            living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 500));
        }
        else if (type == GasType.STINKDAMP && !world.isRemote && entity instanceof EntityArrow && entity.isBurning())
        {
            explode(world, x, y, z, world.getBlockMetadata(x, y, z));
        }
    }

    public void explode(World world, int x, int y, int z, int metadata)
    {
        if (!world.isRemote)
        {
            EntityFallingBlock source = new EntityFallingBlock(world, x, y, z, this, metadata);
            world.spawnEntityInWorld(source);
            world.createExplosion(source, x, y, z, 2.0F, true);
            world.setBlockToAir(x, y, z);
        }
    }
}
