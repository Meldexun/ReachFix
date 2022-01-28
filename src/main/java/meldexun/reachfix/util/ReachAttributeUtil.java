package meldexun.reachfix.util;

import java.util.UUID;

import meldexun.reachfix.ReachFix;
import meldexun.reachfix.config.ReachFixConfig;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class ReachAttributeUtil {

	private static final UUID REACH_UUID = UUID.fromString("00d4860f-b487-4402-b424-373a52566330");
	private static final String REACH_STRING = ReachFix.MOD_ID + ":base_reach";

	public static void updateBaseReachModifier(EntityPlayer player) {
		updateBaseReachModifier(player, player.isCreative());
	}

	public static void updateBaseReachModifier(EntityPlayer player, boolean creative) {
		IAttributeInstance attribute = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
		attribute.removeModifier(REACH_UUID);
		attribute.applyModifier(new AttributeModifier(REACH_UUID, REACH_STRING, (creative ? ReachFixConfig.reachCreative : ReachFixConfig.reach) - 5.0D, 0).setSaved(false));
	}

	public static double getBlockReach(EntityPlayer player) {
		return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
	}

	public static double getEntityReach(EntityPlayer player) {
		return Math.max(getBlockReach(player) - 1.5D, 0.0D);
	}

}
