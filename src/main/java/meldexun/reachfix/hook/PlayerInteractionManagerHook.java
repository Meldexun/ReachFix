package meldexun.reachfix.hook;

import lombok.Data;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;
import org.jetbrains.annotations.NotNull;

@Data
public class PlayerInteractionManagerHook {

    public static void onUpdateGameMode(@NotNull PlayerInteractionManager playerManager, GameType newGameMode) {
        ReachFixUtil.updateBaseReachModifier(playerManager.player, newGameMode == GameType.CREATIVE);
    }
}
