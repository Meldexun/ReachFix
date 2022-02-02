package meldexun.reachfix.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameType;

@Mixin(NetworkPlayerInfo.class)
public class MixinNetworkPlayerInfo {

	@Shadow
	private GameProfile profile;

	@Inject(method = "setGameMode", at = @At("HEAD"))
	public void onSetGameMode(GameType gameMode, CallbackInfo info) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}
		PlayerEntity player = mc.level.getPlayerByUUID(this.profile.getId());
		if (player == null) {
			return;
		}
		ReachFixUtil.updateBaseReachModifier(player, gameMode == GameType.CREATIVE);
	}

}
