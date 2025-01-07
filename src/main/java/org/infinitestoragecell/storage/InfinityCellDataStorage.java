package org.infinitestoragecell.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class InfinityCellDataStorage {

    public static final InfinityCellDataStorage EMPTY = new InfinityCellDataStorage();

    public ListTag keys;
    public long[] amounts;

    public InfinityCellDataStorage() {
        this(new ListTag(), new long[0]);
    }

    private InfinityCellDataStorage(ListTag keys, long[] amounts) {
        this.keys = keys;
        this.amounts = amounts;
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("keys", keys);
        nbt.putLongArray("amounts", amounts);
        return nbt;
    }

    public static InfinityCellDataStorage fromNbt(CompoundTag nbt) {
        ListTag stackKeys = nbt.getList("keys", Tag.TAG_COMPOUND);
        long[] stackAmounts = nbt.getLongArray("amounts");
        return new InfinityCellDataStorage(stackKeys, stackAmounts);
    }
}
