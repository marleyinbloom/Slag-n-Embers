package dev.lopyluna.slag.mixin;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import dev.lopyluna.slag.SlagEmbers;
import dev.lopyluna.slag.content.items.modular_tool.BakedModularToolItem;
import dev.lopyluna.slag.register.AllDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
