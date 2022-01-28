package meldexun.reachfix.network;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CPacketHandlerSyncConfig implements IMessageHandler<SPacketSyncConfig, IMessage> {

	@Override
	public IMessage onMessage(SPacketSyncConfig message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
			ReachFixUtil.setEnabled(message.isEnabled());
			ReachFixUtil.setReach(message.getReach());
			ReachFixUtil.setReachCreative(message.getReachCreative());
			ReachFixUtil.updateBaseReachModifier(getPlayer());
		});
		return null;
	}

	@SideOnly(Side.CLIENT)
	private static EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

}
