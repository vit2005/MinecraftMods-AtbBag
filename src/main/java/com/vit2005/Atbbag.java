package com.vit2005;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Atbbag implements ModInitializer {
    public static final String MOD_ID = "atbbag";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        // ВАЖЛИВО: Спочатку додаємо ресурси
        PolymerResourcePackUtils.addModAssets("atbbag");

        // Примусово вмикаємо autohost (якщо не спрацював конфіг)
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register((builder) -> {
            LOGGER.info("Resource pack is being created...");
        });

        // Будуємо ресурспак
        boolean built = PolymerResourcePackUtils.buildMain();
        LOGGER.info("Resource pack built: " + built);

        // Додаємо лог про шлях до ресурспаку
        LOGGER.info("Resource pack location: " + PolymerResourcePackUtils.getMainPath());

        // ПОТІМ реєструємо блоки та айтеми
        AtbBagBlocks.registerBlocks();
        AtbBagItems.registerItems();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            AtbBagCommand.register(dispatcher);
        });

        LOGGER.info("ATB Bag mod initialized!");
    }
}