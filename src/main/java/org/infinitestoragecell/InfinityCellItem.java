package org.infinitestoragecell;

import appeng.api.stacks.AEKeyType;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.List;

@Getter
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InfinityCellItem extends Item {

    @Nullable
    private final AEKeyType keyType;

    public InfinityCellItem(@Nullable AEKeyType keyType) {
        super(new Properties().stacksTo(1).fireResistant());
        this.keyType = keyType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext p_339594_, List<Component> tooltip, TooltipFlag p_41424_) {
        if (stack.has(InfiniteStorageCell.UUID)) {
            tooltip.add(Component.literal("UUID: ").withStyle(ChatFormatting.GRAY).append(Component.literal(stack.get(InfiniteStorageCell.UUID).toString()).withStyle(ChatFormatting.AQUA)));
            tooltip.add(Component.literal("Byte: ").withStyle(ChatFormatting.GRAY).append(Component.literal(formatDouble(stack.getOrDefault(InfiniteStorageCell.BYTE, 0D))).withStyle(ChatFormatting.GREEN)));
        }
    }

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    private static String formatDouble(double number) {
        if (number < 1_000) {
            return DF.format(number);
        } else if (number < 1_000_000) {
            return DF.format(number / 1_000) + "K";
        } else if (number < 1_000_000_000) {
            return DF.format(number / 1_000_000) + "M";
        } else if (number < 1_000_000_000_000.0) {
            return DF.format(number / 1_000_000_000) + "G";
        } else if (number < 1_000_000_000_000_000.0) {
            return DF.format(number / 1_000_000_000_000.0) + "T";
        } else if (number < 1_000_000_000_000_000_000.0) {
            return DF.format(number / 1_000_000_000_000_000.0) + "P";
        } else if (number < 1_000_000_000_000_000_000_000.0) {
            return DF.format(number / 1_000_000_000_000_000_000.0) + "E";
        } else if (number < 1_000_000_000_000_000_000_000_000.0) {
            return DF.format(number / 1_000_000_000_000_000_000_000.0) + "Z";
        } else {
            return DF.format(number / 1_000_000_000_000_000_000_000_000.0) + "Y";
        }
    }
}
