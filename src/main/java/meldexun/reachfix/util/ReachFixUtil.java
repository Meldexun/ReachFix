package meldexun.reachfix.util;

import lombok.Data;
import meldexun.reachfix.ReachFix;
import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.integration.SpartanWeaponry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class ReachFixUtil {

    private static final UUID REACH_UUID = UUID.fromString("00d4860f-b487-4402-b424-373a52566330");
    private static final String REACH_STRING = ReachFix.MODID + ":base_reach";

    public static void updateBaseReachModifier(@NotNull EntityPlayer player) {
        boolean creative = player.isCreative();
        updateBaseReachModifier(player, creative);
    }

    public static void updateBaseReachModifier(@NotNull EntityPlayer player, boolean creative) {
        ReachFixConfig config = ReachFixConfig.getInstance();
        @NotNull IAttributeInstance attribute = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);

        attribute.removeModifier(REACH_UUID);
        AttributeModifier modifier = new AttributeModifier(REACH_UUID, REACH_STRING,
                (creative ? config.reachCreative : config.reach) - 5.0D, 0).setSaved(false);
        attribute.applyModifier(modifier);
    }

    public static double getBlockReach(@NotNull EntityPlayer player, EnumHand hand) {
        return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    }

    public static double getEntityReach(@NotNull EntityPlayer player, @NotNull EnumHand hand) {
        ReachFixConfig config = ReachFixConfig.getInstance();
        double reach = getBlockReach(player, hand);

        if (player.isCreative()) {
            reach += config.entityReachCreative - config.reachCreative;
        } else {
            reach += config.entityReach - config.reach;
        }

        if (ReachFix.isSpartanWeaponryInstalled) {
            reach += SpartanWeaponry.getReachBonus(player, hand);
        }
        return MathHelper.clamp(reach, 0.0D, 1024.0D);
    }
}
