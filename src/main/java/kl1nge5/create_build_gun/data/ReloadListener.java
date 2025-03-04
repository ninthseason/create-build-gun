package kl1nge5.create_build_gun.data;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ReloadListener {
    public static void init(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(ReloadListener::serverReload);
        modEventBus.addListener(ReloadListener::clientReload);
    }

    @SubscribeEvent
    public static void serverReload(AddReloadListenerEvent event) {
        DataManager.init();
    }

    @SubscribeEvent
    public static void clientReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientReloadListener.INSTANCE);
    }
}

class ClientReloadListener implements ResourceManagerReloadListener {
    public static ClientReloadListener INSTANCE = new ClientReloadListener();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        DataManager.init();
    }
}
