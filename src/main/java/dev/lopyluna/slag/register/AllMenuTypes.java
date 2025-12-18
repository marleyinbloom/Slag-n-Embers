package dev.lopyluna.slag.register;

import dev.lopyluna.slag.content.blocks.crucible_interface.client.InterfaceMenu;
import dev.lopyluna.slag.content.blocks.melter.client.MelterMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

import static dev.lopyluna.slag.SlagEmbers.REGISTER;

public class AllMenuTypes {
    public static final DeferredHolder<MenuType<?>, MenuType<InterfaceMenu>> INTERFACE = REGISTER.menus()
            .register("interface", () -> IMenuTypeExtension.create(InterfaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MelterMenu>> MELTER = REGISTER.menus()
            .register("melter", () -> IMenuTypeExtension.create(MelterMenu::new));

    public static void register() {}
}
