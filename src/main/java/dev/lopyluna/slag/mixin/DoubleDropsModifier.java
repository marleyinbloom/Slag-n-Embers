package dev.lopyluna.slag.mixin;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.abilities.weapon.SkyrootWeapon;
import dev.lopyluna.slag.SlagEmbers;
import dev.lopyluna.slag.register.AllDataComponents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(com.aetherteam.aether.loot.modifiers.DoubleDropsModifier.class)
public class DoubleDropsModifier {

    @Inject(method = "doApply", at = @At(value = "HEAD"), cancellable = true)
    private void doApply(ObjectArrayList<ItemStack> lootStacks, LootContext context, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        Entity entity = context.getParamOrNull(LootContextParams.DIRECT_ATTACKING_ENTITY);
        Entity target = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        ObjectArrayList<ItemStack> newStacks = new ObjectArrayList<>(lootStacks);
        if (entity instanceof LivingEntity livingEntity && target != null) {
            ItemStack weaponStack = livingEntity.getMainHandItem();
            SlagEmbers.LOGGER.info("" + weaponStack);
            if (EquipmentUtil.isFullStrength(livingEntity) && weaponStack.has(AllDataComponents.SKYROOT_WEAPON) &&
                    !target.getType().is(AetherTags.Entities.NO_SKYROOT_DOUBLE_DROPS)) {
                for (ItemStack stack : lootStacks) {
                    if (!stack.is(AetherTags.Items.NO_SKYROOT_DOUBLE_DROPS)) {
                        newStacks.add(stack);
                        cir.setReturnValue(newStacks);
                        cir.cancel();
                    }
                }
            }
        }
    }

}
