package meldexun.reachfix.asm.hook.client;

import meldexun.reachfix.util.ReachAttributeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.world.GameType;

public class NetworkPlayerInfoHook {

	public static void onUpdateGameMode(NetworkPlayerInfo playerInfo, GameType newGameMode) {
		ReachAttributeUtil.onGameModeChanged(Minecraft.getMinecraft().player, playerInfo.getGameType(), newGameMode);
	}

}
