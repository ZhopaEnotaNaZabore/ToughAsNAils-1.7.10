package toughasnails.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import toughasnails.block.BlockRainCollector;
import toughasnails.block.BlockTANCampfire;
import toughasnails.block.BlockTANTemperatureCoil;
import toughasnails.core.TANRenderIds;

/** Legacy 1.7.10 replacement for the 1.12 JSON block models. */
public class TANBlockRenderer implements ISimpleBlockRenderingHandler
{
    @Override
    public int getRenderId() { return TANRenderIds.BLOCK; }

    @Override
    public boolean shouldRender3DInInventory(int modelId) { return true; }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        setBounds(block, metadata, renderer);
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon(block, 0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon(block, 1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon(block, 5, metadata));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        renderer.setRenderBoundsFromBlock(block);
    }

    private static IIcon icon(Block block, int side, int metadata) { return block.getIcon(side, metadata); }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        if (block instanceof BlockRainCollector)
        {
            // Bottom basin plus four thin walls: the legacy equivalent of the JSON collector model.
            renderBox(world, x, y, z, block, renderer, 0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
            renderBox(world, x, y, z, block, renderer, 0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
            renderBox(world, x, y, z, block, renderer, 0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            renderBox(world, x, y, z, block, renderer, 0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 0.125D);
            renderBox(world, x, y, z, block, renderer, 0.125D, 0.0D, 0.875D, 0.875D, 1.0D, 1.0D);
        }
        else if (block instanceof BlockTANCampfire)
        {
            // Low crossed-log approximation; preserves the non-cubic footprint in 1.7.10.
            renderBox(world, x, y, z, block, renderer, 0.0D, 0.0D, 0.30D, 1.0D, 0.25D, 0.70D);
            renderBox(world, x, y, z, block, renderer, 0.30D, 0.0D, 0.0D, 0.70D, 0.25D, 1.0D);
        }
        else
        {
            renderBox(world, x, y, z, block, renderer, 0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
        }
        renderer.setRenderBoundsFromBlock(block);
        return true;
    }

    private static void setBounds(Block block, int metadata, RenderBlocks renderer)
    {
        if (block instanceof BlockTANTemperatureCoil) renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
        else if (block instanceof BlockTANCampfire) renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
        else renderer.setRenderBoundsFromBlock(block);
    }

    private static void renderBox(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer,
            double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
        renderer.renderStandardBlock(block, x, y, z);
    }
}
