package meldexun.reachfix.asm.hook.client;

import meldexun.reachfix.util.ReachAttributeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;

public class NetworkPlayerInfoHook {

	public static void onUpdateGameMode(GameType oldGameMode, GameType newGameMode) {
		Minecraft mc = Minecraft.getMinecraft();
		ReachAttributeUtil.onGameModeChanged(mc.player, oldGameMode, newGameMode);
	}

}
