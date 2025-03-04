package kl1nge5.create_build_gun.network.ToClient;

import io.netty.buffer.ByteBuf;
import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.CachedClientStage;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResponseStagePackage(int stage) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ResponseStagePackage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "response_stage"));
    public static final StreamCodec<ByteBuf, ResponseStagePackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ResponseStagePackage::stage,
            ResponseStagePackage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void clientHandler(ResponseStagePackage payload, IPayloadContext context) {
        CachedClientStage.stage = payload.stage;
    }
}
