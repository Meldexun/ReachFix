package meldexun.reachfix.hook.client;

import com.mojang.authlib.GameProfile;
import lombok.Data;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
public class NetworkPlayerInfoHook {

    public static void onUpdateGameMode(@NotNull NetworkPlayerInfo playerInfo, GameType newGameMode) {
        @NotNull Minecraft mc = Minecraft.getMinecraft();

        if (mc.world == null) {
            return;
        }

        @NotNull GameProfile gameProfile = playerInfo.getGameProfile();
        UUID gameProfileId = gameProfile.getId();
        @Nullable EntityPlayer player = mc.world.getPlayerEntityByUUID(gameProfileId);

        if (player == null) {
            return;
        }

        ReachFixUtil.updateBaseReachModifier(player, newGameMode == GameType.CREATIVE);
    }
}
