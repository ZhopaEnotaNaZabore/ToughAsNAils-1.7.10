package toughasnails.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

/** Lightweight legacy snowflake particle using the vanilla particle atlas. */
public class EntitySnowflakeFX extends EntityFX
{
    public EntitySnowflakeFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        super(world, x, y, z, motionX, motionY, motionZ);
        particleScale *= 0.75F;
        particleMaxAge = 32 + rand.nextInt(24);
        particleGravity = 0.02F;
        noClip = true;
        setParticleTextureIndex(7);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        motionX *= 0.96D;
        motionY *= 0.96D;
        motionZ *= 0.96D;
    }
}
