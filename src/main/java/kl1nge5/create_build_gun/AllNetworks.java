package kl1nge5.create_build_gun;

import kl1nge5.create_build_gun.network.ToClient.ResponseStagePackage;
import kl1nge5.create_build_gun.network.ToServer.BuildingRemovalPackage;
import kl1nge5.create_build_gun.network.ToServer.RequestStagePackage;
import kl1nge5.create_build_gun.network.ToServer.SelectSchematicPackage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AllNetworks {

    public static void init(IEventBus modBusEvent) {
        modBusEvent.addListener(AllNetworks::register);
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                SelectSchematicPackage.TYPE,
                SelectSchematicPackage.STREAM_CODEC,
                SelectSchematicPackage::serverHandler
        );
        registrar.playToServer(
                RequestStagePackage.TYPE,
                RequestStagePackage.STREAM_CODEC,
                RequestStagePackage::serverHandler
        );
        registrar.playToServer(
                BuildingRemovalPackage.TYPE,
                BuildingRemovalPackage.STREAM_CODEC,
                BuildingRemovalPackage::serverHandler
        );
        registrar.playToClient(
                ResponseStagePackage.TYPE,
                ResponseStagePackage.STREAM_CODEC,
                ResponseStagePackage::clientHandler
        );
    }
}
