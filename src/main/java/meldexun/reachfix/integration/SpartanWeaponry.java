package meldexun.reachfix.integration;

import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import lombok.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.jetbrains.annotations.NotNull;

@Data
public class SpartanWeaponry {

    public static double getReachBonus(@NotNull EntityPlayer player, @NotNull EnumHand hand) {
        @NotNull ItemStack stack = player.getHeldItem(hand);
        @NotNull Item item = stack.getItem();

        if (!(item instanceof IWeaponPropertyContainer<?>)) {
            return 0.0D;
        }

        @NotNull IWeaponPropertyContainer<?> propertyContainer = (IWeaponPropertyContainer<?>) item;
        WeaponProperty reachProperty = propertyContainer.getFirstWeaponPropertyWithType("reach");

        if (reachProperty == null) {
            return 0.0D;
        }
        return reachProperty.getMagnitude() - 5.0D;
    }
}
