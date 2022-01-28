package meldexun.reachfix.asm.hook.client;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

public class NetworkPlayerInfoHook {

	public static void onUpdateGameMode(NetworkPlayerInfo playerInfo, GameType newGameMode) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.world == null) {
			return;
		}
		EntityPlayer player = mc.world.getPlayerEntityByUUID(playerInfo.getGameProfile().getId());
		if (player == null) {
			return;
		}
		ReachFixUtil.updateBaseReachModifier(player, newGameMode == GameType.CREATIVE);
	}

}
