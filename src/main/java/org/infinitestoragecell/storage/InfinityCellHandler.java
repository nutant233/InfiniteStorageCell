package org.infinitestoragecell.storage;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import net.minecraft.world.item.ItemStack;
import org.infinitestoragecell.InfinityCellItem;

public class InfinityCellHandler implements ICellHandler {

    public static final InfinityCellHandler INSTANCE = new InfinityCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return is.getItem() instanceof InfinityCellItem;
    }

    @Override
    public InfinityCellInventory getCellInventory(ItemStack is, ISaveProvider container) {
        return InfinityCellInventory.createInventory(is, container);
    }
}
