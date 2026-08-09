package toughasnails.fluids.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

/** Purified water with the same liquid-mixing rule as the 1.12 implementation. */
public class BlockPurifiedWaterFluid extends BlockFluidClassic
{
    public BlockPurifiedWaterFluid(Fluid fluid)
    {
        super(fluid, Material.water);
        setLightOpacity(3);
        setHardness(100.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        checkForMixing(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour)
    {
        super.onNeighborBlockChange(world, x, y, z, neighbour);
        checkForMixing(world, x, y, z);
    }

    private boolean checkForMixing(World world, int x, int y, int z)
    {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            if (direction == ForgeDirection.DOWN)
                continue;

            Block neighbour = world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
            if (neighbour != this && neighbour.getMaterial().isLiquid())
            {
                world.setBlock(x, y, z, Blocks.stone);
                return true;
            }
        }
        return false;
    }
}
