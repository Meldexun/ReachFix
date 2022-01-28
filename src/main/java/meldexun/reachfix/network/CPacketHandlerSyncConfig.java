package meldexun.reachfix.network;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketHandlerSyncConfig implements IMessageHandler<SPacketSyncConfig, IMessage> {

	@Override
	public IMessage onMessage(SPacketSyncConfig message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			ReachFixUtil.setEnabled(message.isEnabled());
			ReachFixUtil.setReach(message.getReach());
			ReachFixUtil.setReachCreative(message.getReachCreative());
			ReachFixUtil.updateBaseReachModifier(Minecraft.getMinecraft().player);
		});
		return null;
	}

}
