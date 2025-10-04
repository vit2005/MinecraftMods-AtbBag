package com.vit2005;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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
        return Items.BUNDLE; // Використовуємо BUNDLE як базу (він має модель 3D)
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        LOGGER.info("modifyBasePolymerItemStack called for ATB_BAG");
        LOGGER.info("Input stack: " + stack);
        LOGGER.info("Output stack before: " + out);

        // Встановлюємо кастомну модель
        Identifier modelId = Identifier.of("atbbag", "atb_bag");
        out.set(DataComponentTypes.ITEM_MODEL, modelId);

        LOGGER.info("Set model to: " + modelId);
        LOGGER.info("Output stack after: " + out);
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

        BlockState state = AtbBagBlocks.ATB_BAG_BLOCK.getDefaultState();
        if (world.setBlockState(pos, state)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AtbBagBlockEntity bag) {
                bag.readComponents(stack);
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

