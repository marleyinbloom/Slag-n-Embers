package dev.lopyluna.slag.client;

import dev.lopyluna.slag.content.items.modular_tool.BakedModularToolItem;
import dev.lopyluna.slag.content.items.modular_tool.IToolPart;
import dev.lopyluna.slag.content.items.modular_tool.ModularToolItem;
import dev.lopyluna.slag.register.AllLangs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

@SuppressWarnings("unused")
public class ClientTooltips {

    public static void appendHoverTextModularTool(ModularToolItem item, ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (context.level() instanceof ClientLevel level) {
            var parts = item.getParts(stack);
            if (parts != null && !parts.isEmpty()) {
                var toolParts = parts.itemsCopy();
                if (toolParts.isEmpty()) {
                    tooltip.add(AllLangs.tr("modular_tool_waiting").withStyle(ChatFormatting.GRAY));
                    return;
                }
                if (!(item instanceof BakedModularToolItem)) {
                    var tier = item.averageMod(stack, IToolPart::getTough);

                    var rodCount = ModularToolItem.testRodCount(parts);
                    var ready = true;
                    var stickCount = parts.getItem(Items.STICK).getCount();
                    if (!parts.contains(Items.STICK)) {
                        if (rodCount<=0) tooltip.add(AllLangs.tr("modular_tool_invalid").withStyle(ChatFormatting.RED));
                        else tooltip.add(AllLangs.trArgs("modular_tool_insert_stick", rodCount, rodCount > 1 ? "s" : "").withStyle(ChatFormatting.RED));
                        ready = false;
                    } else if (rodCount != stickCount) {
                        tooltip.add((rodCount < stickCount ? AllLangs.tr("modular_tool_too_many_stick") : AllLangs.tr("modular_tool_too_few_stick")).withStyle(ChatFormatting.RED));
                        ready = false;
                    }

                    if (ready) {
                        tooltip.add(AllLangs.trArgs("modular_tool_ready", String.valueOf(tier)).withStyle(ChatFormatting.GREEN));
                    }

                    tooltip.add(AllLangs.tr("modular_tool_remove").withStyle(ChatFormatting.GRAY));
                }

                AllLangs.modularToolStats(tooltip, parts, stack, item);
                AllLangs.modularToolParts(tooltip, toolParts);
            } else tooltip.add(AllLangs.tr("modular_tool_waiting").withStyle(ChatFormatting.GRAY));
        }
    }
}
