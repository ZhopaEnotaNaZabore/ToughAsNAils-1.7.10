package toughasnails.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.RenderingRegistry;
import toughasnails.client.render.TANBlockRenderer;
import toughasnails.particle.EntitySnowflakeFX;
import toughasnails.particle.TANParticleTypes;

/** Legacy client proxy. 1.7.10 uses icon registration rather than ModelLoader. */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers()
    {
        TANRenderIds.BLOCK = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new TANBlockRenderer());
    }

    @Override
    public void spawnParticle(TANParticleTypes type, World world, double x, double y, double z, Object... info)
    {
        if (type == TANParticleTypes.SNOWFLAKE)
        {
            EntityFX particle = new EntitySnowflakeFX(world, x, y, z,
                    (world.rand.nextDouble() - 0.5D) * 0.06D, -0.02D,
                    (world.rand.nextDouble() - 0.5D) * 0.06D);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }
}
