package kl1nge5.create_build_gun;

import kl1nge5.create_build_gun.render.BuildGunScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AllScreens {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(AllScreens::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(AllMenus.DUMMY_MENU.get(), BuildGunScreen::new);
    }
}
