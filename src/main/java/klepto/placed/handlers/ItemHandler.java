package klepto.placed.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ItemHandler{

    /**
     * Checks if the stack passed is to be fully broken or not. If so, decrements the durability of the stack by 1. If not, completely 
     * removes the stack.
     * @param stack  The stack to be affected.
     * @param player The PlayerEntity.
     * @param breakFully If the item is to be fully removed or just damaged.
     */
    static void destroyItem(ItemStack stack, PlayerEntity player, Boolean breakFully){
        if (!player.isCreative()){
            if(!breakFully){
                if (stack.getDamage() < stack.getMaxDamage()){
                    stack.setDamage(stack.getDamage() + 1);
                } else if (stack.getDamage() == stack.getMaxDamage()){
                    stack.decrement(1);
                }
            } else {
                stack.decrement(1);
            }
        }
    }
}
