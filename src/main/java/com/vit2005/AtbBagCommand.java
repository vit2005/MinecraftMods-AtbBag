package com.vit2005;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AtbBagCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("GetAtbBag")
                        .executes(AtbBagCommand::execute)
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity player) {
            // Створюємо ItemStack з BlockItem блоку (це можна поставити!)
            ItemStack atbBag = new ItemStack(AtbBagBlocks.ATB_BAG_BLOCK);

            // Додаємо айтем в інвентар гравця
            boolean added = player.giveItemStack(atbBag);

            if (added) {
                player.sendMessage(Text.literal("§aТи отримав Пакет АТБ!"), false);
            } else {
                player.sendMessage(Text.literal("§cІнвентар переповнений!"), false);
            }

            return 1;
        }

        source.sendError(Text.literal("Ця команда може бути виконана тільки гравцем!"));
        return 0;
    }
}