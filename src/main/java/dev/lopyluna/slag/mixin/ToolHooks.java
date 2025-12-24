package dev.lopyluna.slag.mixin;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import dev.lopyluna.slag.SlagEmbers;
import dev.lopyluna.slag.content.items.modular_tool.BakedModularToolItem;
import dev.lopyluna.slag.register.AllDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(com.aetherteam.aether.event.hooks.AbilityHooks.ToolHooks.class)
public class ToolHooks {
    @Inject(method = "reduceToolEffectiveness", at = @At(value = "HEAD"), cancellable = true)
    private static void reduceToolEffectiveness(Player player, BlockState state, ItemStack stack, float speed, CallbackInfoReturnable<Float> cir) {
        if (AbilityHooks.ToolHooks.debuffTools) {
            if ((state.getBlock().getDescriptionId().startsWith("block.aether.") || state.is(AetherTags.Blocks.TREATED_AS_AETHER_BLOCK)) && !state.is(AetherTags.Blocks.TREATED_AS_VANILLA_BLOCK)) {
                if (stack.has(AllDataComponents.AETHER_EFFICIENT)) {
                    cir.setReturnValue(speed);
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "handleHolystoneToolAbility", at = @At(value = "HEAD"), cancellable = true)
    private static void handleHolystoneToolAbility(Player player, Level level, BlockPos pos, ItemStack stack, BlockState blockState, CallbackInfo ci) {
        if (stack.getItem() instanceof BakedModularToolItem toolItem && stack.has(AllDataComponents.HOLYSTONE_TOOL)) {
            dropAmbrosium(player, level, pos, stack, blockState);
            ci.cancel();
        }
    }

    @Inject(method = "handleZaniteToolAbility", at = @At(value = "HEAD"), cancellable = true)
    private static void handleZaniteToolAbility(ItemStack stack, float speed, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof BakedModularToolItem toolItem && stack.has(AllDataComponents.ZANITE_TOOL)) {
            cir.setReturnValue(increaseSpeed(stack, speed));
            cir.cancel();
        }
    }

    @Unique
    private static float increaseSpeed(ItemStack stack, float speed) {
        return (float) EquipmentUtil.calculateZaniteBuff(stack, speed);
    }

    @Unique
    private static void dropAmbrosium(Player player, Level level, BlockPos pos, ItemStack stack, BlockState state) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) > 0 && stack.isCorrectToolForDrops(state) && player.getRandom().nextInt(50) == 0) {
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(AetherItems.AMBROSIUM_SHARD.get()));
            level.addFreshEntity(itemEntity);
        }
    }
}
