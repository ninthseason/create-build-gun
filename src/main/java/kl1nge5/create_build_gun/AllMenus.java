package kl1nge5.create_build_gun;

import kl1nge5.create_build_gun.render.DummyMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class AllMenus {
    public static final Supplier<MenuType<DummyMenu>> DUMMY_MENU = BuildGun.MENU_TYPES.register("dummy_menu", () -> new MenuType<>(DummyMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void init() {}  // for loading the class
}
