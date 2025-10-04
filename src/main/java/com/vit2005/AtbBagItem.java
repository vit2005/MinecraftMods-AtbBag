package com.vit2005;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtbBagItem extends Item implements PolymerItem {
    private static final Logger LOGGER = LoggerFactory.getLogger("atbbag");

    public AtbBagItem(Settings settings) {
        super(settings.maxCount(1));
        LOGGER.info("AtbBagItem created");
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PAPER; // Використовуємо PAPER замість BUNDLE для кращої сумісності
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        // ВАЖЛИВО: У 1.21.4+ item_model приймає Identifier, який вказує на JSON файл моделі
        // Цей JSON файл повинен мати структуру з "model": { "type": "minecraft:model", "model": "..." }
        Identifier modelId = Identifier.of("atbbag", "item/atb_bag");
        out.set(DataComponentTypes.ITEM_MODEL, modelId);

        // Копіюємо всі компоненти з оригінального стеку
        out.applyComponentsFrom(itemStack.getComponents());

        LOGGER.info("Creating polymer item stack with model: " + modelId);

        return out;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        var player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (player == null || world.isClient) {
            return ActionResult.PASS;
        }

        LOGGER.info("Placing ATB Bag block at: " + pos);

        if (world.setBlockState(pos, AtbBagBlocks.ATB_BAG_BLOCK.getDefaultState())) {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AtbBagBlockEntity bag) {
                // Переносимо дані з айтему в блок
                bag.readComponents(stack.getComponents());
                bag.markDirty();
                LOGGER.info("Block placed and data transferred");
            }

            if (!player.isCreative()) {
                stack.decrement(1);
            }

            return ActionResult.SUCCESS;
        }

        LOGGER.warn("Failed to place block at: " + pos);
        return ActionResult.FAIL;
    }
}