package kl1nge5.create_build_gun.network.ToServer;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllDataComponents;
import io.netty.buffer.ByteBuf;
import kl1nge5.create_build_gun.BuildGun;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

public record SelectSchematicPackage(String name, ItemStack item) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CustomPacketPayload.Type<SelectSchematicPackage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "select_schematic"));

    public static final StreamCodec<ByteBuf, SelectSchematicPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SelectSchematicPackage::name,
            ByteBufCodecs.fromCodec(ItemStack.CODEC),
            SelectSchematicPackage::item,
            SelectSchematicPackage::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void serverHandler(SelectSchematicPackage payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        int build_gun_slot = player.getInventory().findSlotMatchingItem(payload.item());
        if (build_gun_slot == -1) return;
        ItemStack build_gun = player.getInventory().getItem(build_gun_slot);
        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(Paths.get("schematics", "uploaded", "__BuildGun__", payload.name()).toAbsolutePath(),StandardOpenOption.READ))))) {
            CompoundTag nbt = NbtIo.read(stream, NbtAccounter.create(0x20000000L));
            ListTag listtag = nbt.getList("size", 3);
            Vec3i size = new Vec3i(listtag.getInt(0), listtag.getInt(1), listtag.getInt(2));
            build_gun.set(AllDataComponents.SCHEMATIC_FILE, payload.name());
            build_gun.set(AllDataComponents.SCHEMATIC_BOUNDS, size);
            build_gun.set(AllDataComponents.SCHEMATIC_OWNER, "__BuildGun__");
        } catch (IOException e) {
            LOGGER.warn("Failed to read schematic", e);
        }
    }
}
