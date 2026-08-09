package toughasnails.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import toughasnails.handler.thirst.DrinkHandler;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
public class MessageDrinkWaterInWorld implements IMessage, IMessageHandler<MessageDrinkWaterInWorld, IMessage> {
	private boolean isRain;

	public MessageDrinkWaterInWorld() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public IMessage onMessage(MessageDrinkWaterInWorld message, MessageContext ctx)
	{
		if (Side.SERVER == ctx.side)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			DrinkHandler.tryDrinkWaterInWorld(player, false);
		}
		return null;
	}
}
