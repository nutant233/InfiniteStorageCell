package org.infinitestoragecell.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InfinityCellSavaedData extends SavedData {

    public static InfinityCellSavaedData INSTANCE = new InfinityCellSavaedData();

    private final Map<UUID, InfinityCellDataStorage> cells = new HashMap<>();

    public InfinityCellSavaedData() {
        setDirty();
    }

    public InfinityCellSavaedData(CompoundTag nbt) {
        ListTag cellList = nbt.getList("list", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < cellList.size(); i++) {
            CompoundTag cell = cellList.getCompound(i);
            cells.put(cell.getUUID("uuid"), InfinityCellDataStorage.fromNbt(cell.getCompound("data")));
        }
        setDirty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag cellList = new ListTag();
        for (Map.Entry<UUID, InfinityCellDataStorage> entry : cells.entrySet()) {
            CompoundTag cell = new CompoundTag();
            cell.putUUID("uuid", entry.getKey());
            cell.put("data", entry.getValue().toNbt());
            cellList.add(cell);
        }
        nbt.put("list", cellList);
        return nbt;
    }

    public void updateCell(UUID uuid, InfinityCellDataStorage infinityCellDataStorage) {
        cells.put(uuid, infinityCellDataStorage);
        setDirty();
    }

    public InfinityCellDataStorage getOrCreateCell(UUID uuid) {
        if (!cells.containsKey(uuid)) {
            updateCell(uuid, new InfinityCellDataStorage());
        }
        return cells.get(uuid);
    }

    public void modifyCell(UUID cellID, ListTag keys, long[] amounts) {
        InfinityCellDataStorage cellToModify = getOrCreateCell(cellID);
        if (keys != null && amounts != null) {
            cellToModify.keys = keys;
            cellToModify.amounts = amounts;
        }
        updateCell(cellID, cellToModify);
    }

    public void removeCell(UUID uuid) {
        cells.remove(uuid);
        setDirty();
    }
}
