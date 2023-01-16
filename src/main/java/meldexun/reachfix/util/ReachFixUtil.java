package meldexun.reachfix.util;

import java.util.UUID;

import meldexun.reachfix.ReachFix;
import meldexun.reachfix.integration.BetterSurvival;
import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.integration.SpartanWeaponry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ReachFixUtil {

	private static final UUID REACH_UUID = UUID.fromString("00d4860f-b487-4402-b424-373a52566330");
	private static final String REACH_STRING = ReachFix.MODID + ":base_reach";

	public static void updateBaseReachModifier(EntityPlayer player) {
		updateBaseReachModifier(player, player.isCreative());
	}

	public static void updateBaseReachModifier(EntityPlayer player, boolean creative) {
		ReachFixConfig config = ReachFixConfig.getInstance();
		IAttributeInstance attribute = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
		attribute.removeModifier(REACH_UUID);
		attribute.applyModifier(new AttributeModifier(REACH_UUID, REACH_STRING, (creative ? config.reachCreative : config.reach) - 5.0D, 0).setSaved(false));
	}

	public static double getBlockReach(EntityPlayer player, EnumHand hand) {
		double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		if (ReachFix.isSpartanWeaponryInstalled) {
			reach += SpartanWeaponry.getReachBonus(player, hand);
		}
		if (ReachFix.isBetterSurvivalInstalled) {
			reach += BetterSurvival.getReachBonus(player, hand);
		}
		return reach;
	}

	public static double getEntityReach(EntityPlayer player, EnumHand hand) {
		ReachFixConfig config = ReachFixConfig.getInstance();
		return Math.max(getBlockReach(player, hand) + (player.isCreative() ? (config.entityReachCreative - config.reachCreative) : (config.entityReach - config.reach)), 0.0D);
	}

}
