package kl1nge5.create_build_gun;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public class AllDataComponents {
    public static final Supplier<DataComponentType<String>> SCHEMATIC_ID = BuildGun.DATA_COMPONENTS.registerComponentType(
            "sid",
            builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );

    public static void init() {}  // for loading the class
}
