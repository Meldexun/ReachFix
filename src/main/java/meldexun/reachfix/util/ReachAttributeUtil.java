package meldexun.reachfix.util;

import java.util.UUID;

import meldexun.reachfix.ReachFix;
import meldexun.reachfix.config.ReachFixConfig;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

public class ReachAttributeUtil {

	private static final UUID REACH_UUID = UUID.fromString("00d4860f-b487-4402-b424-373a52566330");
	private static final UUID REACH_UUID_CREATIVE = UUID.fromString("d63e6fe7-57ed-4154-b1a3-eec4136a53e5");

	public static void onGameModeChanged(EntityPlayer player, GameType oldGameMode, GameType newGameMode) {
		if (oldGameMode == newGameMode) {
			return;
		}
		IAttributeInstance attribute = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
		if (newGameMode == GameType.CREATIVE) {
			attribute.removeModifier(REACH_UUID);
			attribute.applyModifier(createReachModifierCreative());
		} else if (oldGameMode == GameType.CREATIVE) {
			attribute.removeModifier(REACH_UUID_CREATIVE);
			attribute.applyModifier(createReachModifier());
		}
	}

	private static AttributeModifier createReachModifier() {
		return new AttributeModifier(REACH_UUID, ReachFix.MOD_ID + ":base_reach", ReachFixConfig.reach - 5.0D, 0).setSaved(false);
	}

	private static AttributeModifier createReachModifierCreative() {
		return new AttributeModifier(REACH_UUID_CREATIVE, ReachFix.MOD_ID + ":base_reach_creative", ReachFixConfig.reachCreative - 5.0D, 0).setSaved(false);
	}

	public static double getBlockReach(EntityPlayer player) {
		return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
	}

	public static double getEntityReach(EntityPlayer player) {
		double d = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		return Math.max(d - 1.5D, 0.0D);
	}

}
