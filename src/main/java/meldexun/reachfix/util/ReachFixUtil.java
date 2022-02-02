package meldexun.reachfix.util;

import java.util.UUID;

import meldexun.reachfix.ReachFix;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;

public class ReachFixUtil {

	private static final UUID REACH_UUID = UUID.fromString("00d4860f-b487-4402-b424-373a52566330");
	private static final String REACH_STRING = ReachFix.MOD_ID + ":base_reach";
	private static boolean enabled = true;
	private static double reach = 4.5D;
	private static double reachCreative = 5.0D;

	public static void updateBaseReachModifier(PlayerEntity player) {
		updateBaseReachModifier(player, player.isCreative());
	}

	public static void updateBaseReachModifier(PlayerEntity player, boolean creative) {
		ModifiableAttributeInstance attribute = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
		attribute.removeModifier(REACH_UUID);
		attribute.addTransientModifier(new AttributeModifier(REACH_UUID, REACH_STRING, (creative ? reachCreative : reach) - 5.0D, Operation.ADDITION));
	}

	public static double getBlockReach(PlayerEntity player) {
		return player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
	}

	public static double getEntityReach(PlayerEntity player) {
		return Math.max(getBlockReach(player) - 1.5D, 0.0D);
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		ReachFixUtil.enabled = enabled;
	}

	public static void setReach(double reach) {
		ReachFixUtil.reach = reach;
	}

	public static void setReachCreative(double reachCreative) {
		ReachFixUtil.reachCreative = reachCreative;
	}

}
