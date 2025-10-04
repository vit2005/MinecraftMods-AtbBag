package com.vit2005;

import com.mojang.serialization.MapCodec;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import eu.pb4.factorytools.api.block.FactoryBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import eu.pb4.factorytools.api.block.FactoryBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class AtbBagBlock extends BlockWithEntity implements PolymerBlock, FactoryBlock {

    public AtbBagBlock(Settings settings, Block block) {
        super(settings.dropsNothing());
        // Забороняємо звичайний дроп — ми самі контролюємо, що випаде
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
        // Використовується для серіалізації (Fabric поки ігнорує, можна лишити null)
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AtbBagBlockEntity(pos, state);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        // Використовуємо BARRIER для невидимого блоку, бо модель малюється через FactoryTools
        return Blocks.BARRIER.getDefaultState();
    }

    // Викликається при руйнуванні блоку
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof AtbBagBlockEntity bag) {
                // Створюємо ItemStack пакету з даними з BlockEntity

                ItemStack stack = new ItemStack(AtbBagItems.ATB_BAG);
                stack.applyComponentsFrom(bag.createComponentMap());

                // Спавнимо його у світі, якщо дроп дозволений
                if (!player.shouldSkipBlockDrops()) {
                    ItemEntity entity = new ItemEntity(
                            world,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            stack
                    );
                    entity.setToDefaultPickupDelay();
                    world.spawnEntity(entity);
                }

                // Важливо: очищаємо інвентар, щоб не дропнути дубль речей
                bag.clear();
            }
        }

        // Партикли, звуки, івенти
        this.spawnBreakParticles(world, player, pos, state);

        // Обробка Piglin і GameEvent
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS) && world instanceof ServerWorld serverWorld) {
            PiglinBrain.onGuardedBlockInteracted(serverWorld, player, false);
        }
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));

        // Не викликаємо super.onBreak(), щоб повністю контролювати логіку
        return state;
    }

    // Викликається при взаємодії ПКМ з блоком
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
            if (factory != null) {
                player.openHandledScreen(factory); // Відкриваємо GUI інвентаря
            }
        }

        // --- нове: переносимо дані з ItemStack у BlockEntity ---
        if (!world.isClient) {
            ItemStack stack = player.getStackInHand(player.getActiveHand());
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof AtbBagBlockEntity bag && stack.getComponents().isEmpty()) {
                // Оновлюємо дані блоку, навіть якщо айтем був пустий
                bag.markDirty();
            }
        }

        return ActionResult.SUCCESS;
    }

// === FACTORY BLOCK METHODS ===

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true; // false якщо не потрібно тікати
    }

    // === МОДЕЛЬ ===

    public static class Model extends BlockModel {
        private final ItemDisplayElement main;

        public Model(BlockState state) {
            // Створюємо ItemStack з модельки блоку
            ItemStack modelStack = ItemDisplayElementUtil.getModel(
                    Identifier.of("atbbag", "block/atb_bag_block")
            );

            // Створюємо віртуальний елемент
            this.main = ItemDisplayElementUtil.createSimple(modelStack);
            this.main.setScale(new org.joml.Vector3f(1f, 1f, 1f)); // Масштаб (за потреби)

            // Додаємо до холдера
            this.addElement(this.main);
        }
    }
}

