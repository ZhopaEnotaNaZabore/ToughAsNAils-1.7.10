package toughasnails.init;

import toughasnails.api.stat.PlayerStatRegistry;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.temperature.TemperatureHandler;
import toughasnails.temperature.TemperatureStorage;
import toughasnails.thirst.ThirstHandler;
import toughasnails.thirst.ThirstStorage;

public class ModStats
{
    public static void init()
    {
        PlayerStatRegistry.addStat(ITemperature.class, new TemperatureStorage(), TemperatureHandler.class);
        PlayerStatRegistry.addStat(IThirst.class, new ThirstStorage(), ThirstHandler.class);
    }
}
