package org.infinitestoragecell;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue IDLE_DRAIN = BUILDER.comment("Infinity Cell idle drain of the cell: how many AE/t it uses passively.").defineInRange("idle_drain", 512, 1, Integer.MAX_VALUE);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int idleDrain;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        idleDrain = IDLE_DRAIN.get();
    }
}
