package org.infinitestoragecell;

import appeng.api.ids.AECreativeTabIds;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.infinitestoragecell.storage.InfinityCellHandler;
import org.infinitestoragecell.storage.InfinityCellSavaedData;

import java.util.UUID;
import java.util.function.Consumer;

@Mod(InfiniteStorageCell.MODID)
public class InfiniteStorageCell {

    public static final String MODID = "infinitestoragecell";

    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

    public static final DeferredHolder<Item, Item> INFINITE_CELL_COMPONENT = ITEMS.register("infinite_cell_component", () -> new Item(new Item.Properties().fireResistant()));
    public static final DeferredHolder<Item, Item> INFINITY_ITEM_CELL = ITEMS.register("infinity_item_cell", () -> new InfinityCellItem(AEKeyType.items()));
    public static final DeferredHolder<Item, Item> INFINITY_FLUID_CELL = ITEMS.register("infinity_fluid_cell", () -> new InfinityCellItem(AEKeyType.fluids()));
    public static final DeferredHolder<Item, Item> INFINITY_GENERAL_CELL = ITEMS.register("infinity_general_cell", () -> new InfinityCellItem(null));

    public InfiniteStorageCell(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        ITEMS.register(modEventBus);
        DR.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.addListener(InfiniteStorageCell::onLevelLoad);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        StorageCells.addCellHandler(InfinityCellHandler.INSTANCE);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == AECreativeTabIds.MAIN) {
            event.accept(INFINITE_CELL_COMPONENT.get());
            event.accept(INFINITY_ITEM_CELL.get());
            event.accept(INFINITY_FLUID_CELL.get());
            event.accept(INFINITY_GENERAL_CELL.get());
        }
    }

    private static final SavedData.Factory<InfinityCellSavaedData> FACTORY = new SavedData.Factory<>(InfinityCellSavaedData::new, InfinityCellSavaedData::new, null);

    private static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            InfinityCellSavaedData.INSTANCE = serverLevel.getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(FACTORY, "infinite_storage_cell_data");
        }
    }

    public static final DataComponentType<UUID> UUID = registerDataComponentType("uuid", builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));
    public static final DataComponentType<Double> BYTE = registerDataComponentType("byte", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    private static <T> DataComponentType<T> registerDataComponentType(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
        return componentType;
    }
}
