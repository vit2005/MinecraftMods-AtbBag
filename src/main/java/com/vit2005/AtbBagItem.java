package com.vit2005;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AtbBagItem extends Item implements PolymerItem {
    private static final Logger LOGGER = LoggerFactory.getLogger("atbbag");

    public AtbBagItem(Settings settings) {
        super(settings.maxCount(1));
        LOGGER.info("AtbBagItem created");
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        // BUNDLE або PAPER - обидва підходять
        return Items.BUNDLE;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        // Додаємо кастомну модель через item_model компонент (1.21.4+)
        out.set(DataComponentTypes.ITEM_MODEL, Identifier.of("atbbag", "item/atb_bag"));

        LOGGER.info("Creating polymer item stack with model: atbbag:item/atb_bag");

        return out;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide());
        PlayerEntity player = context.getPlayer();
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