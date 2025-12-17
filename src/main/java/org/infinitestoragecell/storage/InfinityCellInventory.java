package org.infinitestoragecell.storage;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.infinitestoragecell.InfiniteStorageCell;
import org.infinitestoragecell.InfinityCellItem;

import java.util.ArrayList;
import java.util.UUID;

public class InfinityCellInventory implements StorageCell {

    private final ItemStack stack;
    private final ISaveProvider container;
    private final AEKeyType keyType;
    private Object2LongOpenHashMap<AEKey> storedMap;
    private boolean isPersisted = true;

    private InfinityCellInventory(AEKeyType keyType, ItemStack stack, ISaveProvider saveProvider) {
        this.stack = stack;
        container = saveProvider;
        this.keyType = keyType;
    }

    @Override
    public CellState getStatus() {
        if (getCellStoredMap().isEmpty()) {
            return CellState.EMPTY;
        }
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 512;
    }

    @Override
    public Component getDescription() {
        return null;
    }

    public boolean hasUUID() {
        return stack.get(InfiniteStorageCell.UUID) != null;
    }

    public UUID getUUID() {
        return stack.get(InfiniteStorageCell.UUID);
    }

    static InfinityCellInventory createInventory(ItemStack stack, ISaveProvider saveProvider) {
        if (stack.getItem() instanceof InfinityCellItem cellType) {
            return new InfinityCellInventory(cellType.getKeyType(), stack, saveProvider);
        }
        return null;
    }

    private static InfinityCellSavaedData getStorageInstance() {
        return InfinityCellSavaedData.INSTANCE;
    }

    private Object2LongOpenHashMap<AEKey> getCellStoredMap() {
        if (storedMap == null) {
            storedMap = new Object2LongOpenHashMap<>();
            if (hasUUID()) loadCellStoredMap();
        }
        return storedMap;
    }

    private void loadCellStoredMap() {
        var storage = getStorageInstance().getOrCreateCell(getUUID());
        for (int i = 0; i < storage.keys.size(); i++) {
            var stack = storage.keys.get(i);
            getCellStoredMap().put(stack.what(), stack.amount());
        }
    }

    private void saveChanges() {
        isPersisted = false;
        if (container != null) {
            container.saveChanges();
        } else {
            persist();
        }
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        for (Object2LongMap.Entry<AEKey> entry : Object2LongMaps.fastIterable(getCellStoredMap())) {
            out.add(entry.getKey(), entry.getLongValue());
        }
    }

    @Override
    public void persist() {
        if (isPersisted) return;
        if (hasUUID()) {
            if (getCellStoredMap().isEmpty()) {
                getStorageInstance().removeCell(getUUID());
                stack.remove(InfiniteStorageCell.UUID);
                return;
            }
            var totalByte = 0D;
            var keys = new ArrayList<GenericStack>();
            for (Object2LongMap.Entry<AEKey> entry : Object2LongMaps.fastIterable(getCellStoredMap())) {
                long amount = entry.getLongValue();
                if (amount > 0) {
                    keys.add(new GenericStack(entry.getKey(), amount));
                    totalByte += (double) amount / entry.getKey().getAmountPerByte();
                }
            }
            getStorageInstance().modifyCell(getUUID(), keys);
            stack.set(InfiniteStorageCell.BYTE, totalByte);
        }
        isPersisted = true;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount == 0 || (keyType != null && !keyType.contains(what))) return 0;
        if (!hasUUID()) {
            stack.set(InfiniteStorageCell.UUID, UUID.randomUUID());
            loadCellStoredMap();
        }
        if (mode == Actionable.MODULATE) {
            getCellStoredMap().addTo(what, amount);
            saveChanges();
        }
        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        long currentAmount = getCellStoredMap().getLong(what);
        if (currentAmount > 0) {
            if (amount >= currentAmount) {
                if (mode == Actionable.MODULATE) {
                    storedMap.remove(what, currentAmount);
                    saveChanges();
                }
                return currentAmount;
            } else {
                if (mode == Actionable.MODULATE) {
                    storedMap.put(what, currentAmount - amount);
                    saveChanges();
                }
                return amount;
            }
        }
        return 0;
    }

}
