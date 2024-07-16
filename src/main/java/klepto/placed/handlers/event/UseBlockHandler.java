package klepto.placed.handlers.event;

import klepto.placed.Placed;
import klepto.placed.registry.block.ModBlocks;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!player.isSpectator()){
                ItemStack stack = player.getStackInHand(hand);

                
                if (stack.isOf(Items.GLOWSTONE_DUST)){
                    if(!world.getBlockState(hitResult.getBlockPos()).isOf(ModBlocks.GLOWSTONE_WIRE)){
                        if(!player.isCreative()){
                            stack.decrement(1);
                        }
                        world.playSoundAtBlockCenter(hitResult.getBlockPos().up(1), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1f, 1f, true);
                        world.setBlockState(hitResult.getBlockPos().up(1), ModBlocks.GLOWSTONE_WIRE.getDefaultState());
                        return ActionResult.SUCCESS;
                    }
                } else if (stack.isOf(Items.SUGAR)) {  
                    if(!world.getBlockState(hitResult.getBlockPos()).isOf(ModBlocks.SUGAR_WIRE)){
                        if(!player.isCreative()){
                            stack.decrement(1);
                        }
                        world.playSoundAtBlockCenter(hitResult.getBlockPos().up(1), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1f, 1f, true);
                        world.setBlockState(hitResult.getBlockPos().up(1), ModBlocks.SUGAR_WIRE.getDefaultState());
                        return ActionResult.SUCCESS;
                    }
                }
            
        }
        return ActionResult.PASS;
    }

    public static String registerHandler(){
        return "Registering HANDLERS for " + Placed.MOD_ID;
    }

}