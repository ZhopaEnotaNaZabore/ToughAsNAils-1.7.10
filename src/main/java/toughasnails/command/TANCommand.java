package toughasnails.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import toughasnails.api.TANCapabilities;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.config.TemperatureOption;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureScale;
import toughasnails.temperature.TemperatureDebugger;
import toughasnails.temperature.TemperatureHandler;

public class TANCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "toughasnails";
    }
    
    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList("tan");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.toughasnails.usage";
    }
    
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.toughasnails.usage");
        }
        else if ("tempinfo".equals(args[0]))
        {
            displayTemperatureInfo(sender, args);
        }
        else if ("settemp".equals(args[0]))
        {
            setTemperature(sender, args);
        }
    }
    
    private void displayTemperatureInfo(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        TemperatureHandler temperatureStats = (TemperatureHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.TEMPERATURE);
        TemperatureDebugger debugger = temperatureStats.debugger;

        if (SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE))
    	{
        	debugger.setGuiVisible(!debugger.isGuiVisible(), player);
    	}
        else
        {
            sender.addChatMessage(new ChatComponentTranslation("commands.toughasnails.settemp.disabled"));
        }
    }
    
    private void setTemperature(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        TemperatureHandler temperatureStats = (TemperatureHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.TEMPERATURE);
        if (args.length < 2) throw new WrongUsageException("commands.toughasnails.usage");
        int newTemp = parseIntBounded(sender, args[1], 0, TemperatureScale.getScaleTotal());
        Temperature playerTemp = temperatureStats.getTemperature();

        if (SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE))
    	{
	        //Remove any existing potion effects for hypo/hyperthermia
        player.removePotionEffect(TANPotions.hypothermia.id);
        player.removePotionEffect(TANPotions.hyperthermia.id);
	
	        //Reset the change timer to 0
	        temperatureStats.setChangeTime(0);
	        //Set to the new temperature
	        temperatureStats.setTemperature(new Temperature(newTemp));
	
            sender.addChatMessage(new ChatComponentTranslation("commands.toughasnails.settemp.success", newTemp));
    	}
        else
        {
            sender.addChatMessage(new ChatComponentTranslation("commands.toughasnails.settemp.disabled"));
        }
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "settemp", "tempinfo");
        }
        
        return null;
    }
}
