package meldexun.reachfix.asm.hook;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;

public class PlayerInteractionManagerHook {

	public static void onUpdateGameMode(PlayerInteractionManager playerManager, GameType newGameMode) {
		ReachFixUtil.updateBaseReachModifier(playerManager.player, newGameMode == GameType.CREATIVE);
	}

}
