package org.infinitestoragecell.storage;

import appeng.api.stacks.GenericStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfinityCellDataStorage {

    public List<GenericStack> keys;

    public InfinityCellDataStorage() {
        this(new ArrayList<>());
    }

    private InfinityCellDataStorage(List<GenericStack> keys) {
        this.keys = keys;
    }

    public Tag toNbt(HolderLookup.@NotNull Provider provider) {
        var nbt = new ListTag();
        keys.forEach(k -> {
            nbt.add(GenericStack.writeTag(provider, k));
        });
        return nbt;
    }

    public static InfinityCellDataStorage fromNbt(HolderLookup.@NotNull Provider provider, ListTag nbt) {
        var list = new ArrayList<GenericStack>();
        nbt.forEach(k -> {
            list.add(GenericStack.readTag(provider, (CompoundTag) k));
        });
        return new InfinityCellDataStorage(list);
    }
}
