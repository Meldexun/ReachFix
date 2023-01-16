package meldexun.reachfix.network;

import meldexun.configutil.ConfigUtil;
import meldexun.reachfix.ReachFix;
import meldexun.reachfix.config.ReachFixConfig;
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
			try {
				ConfigUtil.readServerSettings(ReachFixConfig.SLAVE_CONFIG, message.getBuffer());
			} catch (ReflectiveOperationException e) {
				ReachFix.LOGGER.error("Failed to read server config", e);
			}
			ReachFixUtil.updateBaseReachModifier(getPlayer());
		});
		return null;
	}

	@SideOnly(Side.CLIENT)
	private static EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

}
