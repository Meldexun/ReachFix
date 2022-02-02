package meldexun.reachfix.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;

@Mixin(PlayerInteractionManager.class)
public class MixinPlayerInteractionManager {

	@Shadow
	private PlayerEntity player;

	@ModifyConstant(method = "handleBlockBreakAction", constant = @Constant(doubleValue = 1.5D))
	public double getEyeHeight(double d) {
		return this.player.getEyeHeight();
	}

	@Inject(method = "setGameModeForPlayer(Lnet/minecraft/world/GameType;Lnet/minecraft/world/GameType;)V", at = @At("HEAD"))
	public void onSetGameModeForPlayer(GameType gameMode, GameType prevGameMode, CallbackInfo info) {
		ReachFixUtil.updateBaseReachModifier(this.player, gameMode == GameType.CREATIVE);
	}

}
