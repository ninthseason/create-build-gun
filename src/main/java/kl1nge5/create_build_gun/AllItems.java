package kl1nge5.create_build_gun;

import kl1nge5.create_build_gun.items.BuildGunItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class AllItems {
    public static final DeferredItem<Item> BUILD_GUN = BuildGun.ITEMS.registerItem("build_gun", BuildGunItem::new);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_GROUP = BuildGun.CREATIVE_MODE_TABS.register("create_build_gun", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_build_gun"))
            .icon(() -> BUILD_GUN.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(BUILD_GUN.get());
            }).build());
    public static void init() {}  // for loading the class
}
