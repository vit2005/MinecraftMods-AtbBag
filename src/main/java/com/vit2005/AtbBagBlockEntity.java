package com.vit2005;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class AtbBagBlockEntity extends LootableContainerBlockEntity {
    // Наш інвентар з 9 слотів
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public AtbBagBlockEntity(BlockPos pos, BlockState state) {
        super(AtbBagBlocks.ATB_BAG_BLOCK_ENTITY, pos, state);
    }

    // Локалізована назва контейнера
    @Override
    protected Text getContainerName() {
        return Text.translatable("container.atbbag");
    }

    // Створюємо GUI з 1 рядком (9 слотів)
    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        // Варіант 1: Використати конструктор напряму для 1 рядка
        return new GenericContainerScreenHandler(
                ScreenHandlerType.GENERIC_9X1,
                syncId,
                playerInventory,
                this,  // передаємо сам BlockEntity як Inventory
                1      // кількість рядків
        );
    }

    // Кількість слотів у контейнері
    @Override
    public int size() {
        return inventory.size();
    }

    // Повертаємо інвентар
    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        System.out.println("=== getHeldStacks called, size: " + inventory.size() + " ===");
        return inventory;
    }

    // Встановлюємо інвентар при завантаженні
    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    // Серіалізуємо інвентар у NBT
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.writeLootTable(view)) {
            Inventories.writeData(view, this.inventory, false);
        }
    }

    // Десеріалізуємо інвентар з NBT
    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(view)) {
            Inventories.readData(view, this.inventory);
        }
    }


    @Override
    public void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty()) {
                System.out.println("Slot " + i + ": " + stack.getItem() + " x" + stack.getCount());
            }
        }
    }

    // КРИТИЧНО: Перевизначаємо setStack щоб викликати markDirty
    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        markDirty(); // Позначаємо, що дані змінилися
    }

    // Також перевизначаємо removeStack методи
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = super.removeStack(slot, amount);
        markDirty(); // Теж позначаємо зміну
        return result;
    }

    @Override
    public boolean isEmpty() {
        // Перевіряємо, чи всі слоти пусті (без генерації луту)
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}