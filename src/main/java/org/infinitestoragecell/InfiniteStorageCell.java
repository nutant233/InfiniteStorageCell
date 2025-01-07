package org.infinitestoragecell;

import appeng.api.ids.AECreativeTabIds;
import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellGuiHandler;
import appeng.api.storage.cells.ICellHandler;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.common.MEStorageMenu;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infinitestoragecell.storage.InfinityCellHandler;
import org.infinitestoragecell.storage.InfinityCellSavaedData;

@Mod(InfiniteStorageCell.MODID)
public class InfiniteStorageCell {

    public static final String MODID = "infinitestoragecell";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> INFINITY_ITEM_CELL = ITEMS.register("infinity_item_cell", () -> new InfinityCellItem(AEKeyType.items()));
    public static final RegistryObject<Item> INFINITY_FLUID_CELL = ITEMS.register("infinity_fluid_cell", () -> new InfinityCellItem(AEKeyType.fluids()));
    public static final RegistryObject<Item> INFINITY_CHEMICAL_CELL;

    static {
       if (ModList.get().isLoaded("appmek")) {
           INFINITY_CHEMICAL_CELL = ITEMS.register("infinity_chemical_cell", () -> new InfinityCellItem(MekanismKeyType.TYPE));
       } else {
           INFINITY_CHEMICAL_CELL = null;
       }
    }

    public InfiniteStorageCell() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(InfiniteStorageCell::onLevelLoad);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        StorageCells.addCellHandler(InfinityCellHandler.INSTANCE);
        StorageCells.addCellGuiHandler(new InfinityCellGuiHandler());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == AECreativeTabIds.MAIN) {
            event.accept(INFINITY_ITEM_CELL);
            event.accept(INFINITY_FLUID_CELL);
            if (INFINITY_CHEMICAL_CELL!=null) {
                event.accept(INFINITY_CHEMICAL_CELL);
            }
        }
    }

    private static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            InfinityCellSavaedData.INSTANCE = serverLevel.getDataStorage().computeIfAbsent(InfinityCellSavaedData::new, InfinityCellSavaedData::new, "infinite_storage_cell_data");
        }
    }

    private static class InfinityCellGuiHandler implements ICellGuiHandler {

        @Override
        public boolean isSpecializedFor(ItemStack cell) {
            return cell.getItem() instanceof InfinityCellItem;
        }

        @Override
        public void openChestGui(Player player, IChestOrDrive chest, ICellHandler cellHandler, ItemStack cell) {
            MenuOpener.open(MEStorageMenu.TYPE, player, MenuLocators.forBlockEntity(((BlockEntity) chest)));
        }
    }
}
