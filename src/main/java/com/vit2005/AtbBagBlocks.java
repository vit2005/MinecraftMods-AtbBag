package com.vit2005;

import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtbBagBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger("atbbag");

    public static Block ATB_BAG_BLOCK;
    public static BlockEntityType<AtbBagBlockEntity> ATB_BAG_BLOCK_ENTITY;

    private static Block registerBlock(String name, BiFunction<Block.Settings, Block, Block> factory,
                                       Block.Settings settings, Block polymerBlock) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("atbbag", name));
        Block block = factory.apply(settings.registryKey(key), polymerBlock);
        return Registry.register(Registries.BLOCK, key, block);
    }

    private static void registerBlockItem(String name, Block block) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("atbbag", name));

        Item item = new PolymerBlockItem(block, new Item.Settings().registryKey(key), Items.BARRIER) {
            @Override
            public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
                return Items.BARRIER; // Використовуємо BARRIER замість PAPER
            }

            @Override
            public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
                Identifier modelId = Identifier.of("atbbag", "item/atb_bag_block_display");
                out.set(DataComponentTypes.ITEM_MODEL, modelId);

                LOGGER.info("Setting block item model to: " + modelId);
            }
        };

        Registry.register(Registries.ITEM, key, item);
        LOGGER.info("Registered block item: " + name);
    }

    public static void registerBlocks() {
        LOGGER.info("Registering ATB Bag block...");

        // сам блок
        ATB_BAG_BLOCK = registerBlock("atb_bag_block",
                AtbBagBlock::new,
                Block.Settings.copy(Blocks.BARREL),
                Blocks.BARREL
        );

        // айтем блоку
        registerBlockItem("atb_bag_block", ATB_BAG_BLOCK);

        // block entity
        ATB_BAG_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of("atbbag", "atb_bag"),
                FabricBlockEntityTypeBuilder.create(AtbBagBlockEntity::new, ATB_BAG_BLOCK).build()
        );

        LOGGER.info("ATB Bag block registered successfully!");
    }
}
