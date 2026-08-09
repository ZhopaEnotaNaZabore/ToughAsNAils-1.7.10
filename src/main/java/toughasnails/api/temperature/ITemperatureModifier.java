package toughasnails.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface ITemperatureModifier
{
    Temperature applyEnvironmentModifiers(@Nonnull World world, int x, int y, int z, @Nonnull Temperature initialTemperature, @Nonnull IModifierMonitor monitor);
    Temperature applyPlayerModifiers(@Nonnull EntityPlayer player, @Nonnull Temperature initialTemperature, @Nonnull IModifierMonitor monitor);

    boolean isPlayerSpecific();
    @Nonnull String getId();
}
