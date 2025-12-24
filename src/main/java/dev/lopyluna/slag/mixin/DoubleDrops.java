package dev.lopyluna.slag.mixin;

import com.aetherteam.aether.item.tools.abilities.SkyrootTool;
import dev.lopyluna.slag.SlagEmbers;
import dev.lopyluna.slag.content.items.modular_tool.BakedModularToolItem;
import dev.lopyluna.slag.register.AllDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(com.aetherteam.aether.loot.functions.DoubleDrops.class)
public class DoubleDrops implements SkyrootTool {
    @Inject(method = "run", at = @At(value = "HEAD"), cancellable = true)
    private void run(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cin) {
        Level level = context.getLevel();
        ItemStack toolStack = context.getParamOrNull(LootContextParams.TOOL);
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (toolStack != null && toolStack.getItem() instanceof BakedModularToolItem toolItem &&
                toolStack.has(AllDataComponents.SKYROOT_TOOL)) {
            cin.setReturnValue(doubleDrops(level, stack, toolStack, blockState));
            cin.cancel();
        }
    }
}
