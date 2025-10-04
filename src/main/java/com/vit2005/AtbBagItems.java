package com.vit2005;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AtbBagItems {
    private static final Logger LOGGER = LoggerFactory.getLogger("atbbag");

    public static Item ATB_BAG;

    private static Item registerItem(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("atbbag", name));
        Item item = factory.apply(settings.registryKey(key));

        LOGGER.info("Registered item: " + name + " (" + item.getClass().getSimpleName() + ")");

        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerItems() {
        LOGGER.info("Registering ATB Bag items...");

        ATB_BAG = registerItem("atb_bag", AtbBagItem::new, new Item.Settings().maxCount(1));

        // Створюємо ItemGroup для atbbag
        ItemGroup.Builder builder = PolymerItemGroupUtils.builder();
        builder.icon(() -> new ItemStack(ATB_BAG));
        builder.displayName(Text.translatable("item-group.atbbag.items"));
        builder.entries((displayContext, entries) -> {
            entries.add(ATB_BAG);
            entries.add(AtbBagBlocks.ATB_BAG_BLOCK);
        });

        ItemGroup polymerGroup = builder.build();
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of("atbbag", "items"), polymerGroup);

        LOGGER.info("ATB Bag items registered successfully!");
    }
}