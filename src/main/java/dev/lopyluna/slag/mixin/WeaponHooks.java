package dev.lopyluna.slag.mixin;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.google.common.util.concurrent.AtomicDouble;
import dev.lopyluna.slag.register.AllDataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(com.aetherteam.aether.event.hooks.AbilityHooks.WeaponHooks.class)
public class WeaponHooks {
    @Inject(method = "reduceWeaponEffectiveness", at = @At(value = "HEAD"), cancellable = true)
    private static void reduceWeaponEffectiveness(LivingEntity target, Entity source, float damage, CallbackInfoReturnable<Float> cir) {
        if (AetherConfig.SERVER.tools_debuff.get() && !target.level().isClientSide()) { // Checks if tool debuffs are enabled and if the level is on the server side.
            double pow = Math.max(Math.pow(damage, damage > 1.0 ? 0.6 : 1.6), 1.0);
            if (source instanceof LivingEntity livingEntity) {
                ItemStack stack = livingEntity.getMainHandItem();
                if ((target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)) && !target.getType().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)) { // Checks if the target is an Aether entity.
                    if (!stack.isEmpty()) {
                        if (stack.has(AllDataComponents.AETHER_EFFICIENT)) {
                            cir.setReturnValue(damage);
                            cir.cancel();
                        }
                    }
                }
            }
        }
    }
}
