package toughasnails.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import toughasnails.api.ITANBlock;
import toughasnails.core.ToughAsNails;
import toughasnails.item.ItemTANBlock;
import toughasnails.particle.TANParticleTypes;
import toughasnails.tileentity.TileEntityTemperatureSpread;
import toughasnails.core.TANRenderIds;

/** Metadata: bit 0 is coil type (0 cooling, 1 heating), bit 3 is redstone power. */
public class BlockTANTemperatureCoil extends BlockContainer implements ITANBlock
{
    private static final AxisAlignedBB BOUNDS = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockTANTemperatureCoil()
    {
        super(Material.iron);
        setHardness(1.0F);
    }

    @Override
    public Class<? extends ItemBlock> getItemClass()
    {
        return ItemTANBlock.class;
    }

    @Override
    public int[] getPresetMetadatas()
    {
        return new int[] {0, 1};
    }

    @Override
    public String getStateName(int metadata)
    {
        return (metadata & 1) == 0 ? "cooling_coil" : "heating_coil";
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return BOUNDS.getOffsetBoundingBox(x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        if ((metadata & 8) == 0)
            return;

        double particleX = x + 0.4F + random.nextFloat() * 0.2F;
        double particleY = y + 0.7F + random.nextFloat() * 0.3F;
        double particleZ = z + 0.4F + random.nextFloat() * 0.2F;
        if ((metadata & 1) == 1)
            world.spawnParticle("smoke", particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
        else
            ToughAsNails.proxy.spawnParticle(TANParticleTypes.SNOWFLAKE, world, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata & 1;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        return (world.getBlockMetadata(x, y, z) & 8) != 0 ? 7 : 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return (metadata & 1) == 0 ? new TileEntityTemperatureSpread(-10) : new TileEntityTemperatureSpread(10);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityTemperatureSpread)
            ((TileEntityTemperatureSpread)tile).reset();
        super.breakBlock(world, x, y, z, block, metadata);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        updatePowered(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour)
    {
        updatePowered(world, x, y, z);
    }

    private void updatePowered(World world, int x, int y, int z)
    {
        boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        int metadata = world.getBlockMetadata(x, y, z);
        if (world.isRemote || powered == ((metadata & 8) != 0))
            return;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityTemperatureSpread))
            return;

        TileEntityTemperatureSpread spread = (TileEntityTemperatureSpread)tile;
        if (powered)
            spread.fill();
        else
            spread.reset();
        world.setBlockMetadataWithNotify(x, y, z, (metadata & 1) | (powered ? 8 : 0), 3);
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
}
