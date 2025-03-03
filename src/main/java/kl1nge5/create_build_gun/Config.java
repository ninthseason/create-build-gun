package kl1nge5.create_build_gun;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BuildGun.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ALLOW_PRINT_IN_SURVIVAL_MODE = BUILDER
            .comment("Whether blueprints can be printed directly in survival mode")
            .define("canSurvivalPrint", true);

    private static final ModConfigSpec.BooleanValue SURVIVAL_PRINT_CONSUME = BUILDER
            .comment("Whether blueprints will be consumed when printed in survival mode")
            .define("survivalPrintConsume", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean canSurvivalPrint;
    public static boolean survivalPrintConsume;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        canSurvivalPrint = ALLOW_PRINT_IN_SURVIVAL_MODE.get();
        survivalPrintConsume = SURVIVAL_PRINT_CONSUME.get();
    }
}
