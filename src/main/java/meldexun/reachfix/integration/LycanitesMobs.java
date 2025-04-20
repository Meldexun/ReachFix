package meldexun.reachfix.integration;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class LycanitesMobs {

    public static double getReachBonus(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        if(!(item instanceof ItemEquipment)){
            return 0.0D;
        }
        else{
            return ((ItemEquipment) item).getDamageRange(stack);
        }
    }
}
