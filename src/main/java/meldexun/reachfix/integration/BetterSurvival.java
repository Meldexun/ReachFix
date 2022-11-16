package meldexun.reachfix.integration;

import com.mujmajnkraft.bettersurvival.ICustomWeapon;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class BetterSurvival {

	public static double getReachBonus(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		Item item = stack.getItem();
		if (!(item instanceof ICustomWeapon)) {
			return 0.0D;
		}
		ICustomWeapon customWeapon = (ICustomWeapon) item;
		float reach = customWeapon.getReach();
		if (reach <= 5.0F) {
			return 0.0D;
		}
		return reach - 5.0D;
	}

}
