package toughasnails.handler;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import toughasnails.core.ToughAsNails;
import toughasnails.network.message.*;

public class PacketHandler
{
    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(ToughAsNails.MOD_ID);

    public static void init()
    {
        instance.registerMessage(MessageUpdateStat.class, MessageUpdateStat.class, 0, Side.CLIENT);
        instance.registerMessage(MessageTemperatureClient.class, MessageTemperatureClient.class, 1, Side.CLIENT);
        instance.registerMessage(MessageToggleUI.class, MessageToggleUI.class, 2, Side.CLIENT);
        instance.registerMessage(MessageSyncConfigs.class, MessageSyncConfigs.class, 4, Side.CLIENT);
        instance.registerMessage(MessageDrinkWaterInWorld.class, MessageDrinkWaterInWorld.class, 5, Side.SERVER);
    }
}
