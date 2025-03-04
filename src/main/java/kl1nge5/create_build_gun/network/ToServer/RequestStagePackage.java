package kl1nge5.create_build_gun.network.ToServer;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.StageData;
import kl1nge5.create_build_gun.network.ToClient.ResponseStagePackage;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public record RequestStagePackage() implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CustomPacketPayload.Type<RequestStagePackage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "request_stage"));
    public static final StreamCodec<ByteBuf, RequestStagePackage> STREAM_CODEC = StreamCodec.unit(new RequestStagePackage());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void serverHandler(RequestStagePackage payload, IPayloadContext context) {
        int world_stage = ((StageData) context.player().getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<SavedData>(StageData::new, StageData::load), BuildGun.MODID)).stage;
        PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new ResponseStagePackage(world_stage));
    }
}
