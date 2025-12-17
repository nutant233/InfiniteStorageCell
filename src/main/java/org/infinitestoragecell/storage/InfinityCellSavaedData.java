package org.infinitestoragecell.storage;

import appeng.api.stacks.GenericStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InfinityCellSavaedData extends SavedData {

    public static InfinityCellSavaedData INSTANCE = new InfinityCellSavaedData();

    private final Map<UUID, InfinityCellDataStorage> cells = new HashMap<>();

    public InfinityCellSavaedData() {
    }

    public InfinityCellSavaedData(CompoundTag nbt, HolderLookup.Provider provider) {
        ListTag cellList = nbt.getList("list", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < cellList.size(); i++) {
            CompoundTag cell = cellList.getCompound(i);
            cells.put(cell.getUUID("uuid"), InfinityCellDataStorage.fromNbt(provider, cell.getList("data", Tag.TAG_COMPOUND)));
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        ListTag cellList = new ListTag();
        for (Map.Entry<UUID, InfinityCellDataStorage> entry : cells.entrySet()) {
            CompoundTag cell = new CompoundTag();
            cell.putUUID("uuid", entry.getKey());
            cell.put("data", entry.getValue().toNbt(provider));
            cellList.add(cell);
        }
        nbt.put("list", cellList);
        return nbt;
    }

    public InfinityCellDataStorage getOrCreateCell(UUID uuid) {
        return cells.computeIfAbsent(uuid, k -> new InfinityCellDataStorage());
    }

    public void modifyCell(UUID cellID, List<GenericStack> keys) {
        InfinityCellDataStorage cellToModify = getOrCreateCell(cellID);
        if (keys != null) {
            cellToModify.keys = keys;
        }
        setDirty();
    }

    public void removeCell(UUID uuid) {
        cells.remove(uuid);
        setDirty();
    }
}
