package meldexun.reachfix.asm.hook;

import meldexun.reachfix.util.ReachAttributeUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;

public class PlayerInteractionManagerHook {

	public static void onUpdateGameMode(EntityPlayerMP player, GameType oldGameMode, GameType newGameMode) {
		ReachAttributeUtil.onGameModeChanged(player, oldGameMode, newGameMode);
	}

}
