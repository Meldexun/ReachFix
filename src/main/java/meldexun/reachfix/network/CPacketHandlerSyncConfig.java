package meldexun.reachfix.network;

import meldexun.reachfix.util.ReachAttributeUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketHandlerSyncConfig implements IMessageHandler<SPacketSyncConfig, IMessage> {

	@Override
	public IMessage onMessage(SPacketSyncConfig message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			ReachAttributeUtil.setEnabled(message.isEnabled());
			ReachAttributeUtil.setReach(message.getReach());
			ReachAttributeUtil.setReachCreative(message.getReachCreative());
		});
		return null;
	}

}
