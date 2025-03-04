package kl1nge5.create_build_gun.network.ToServer;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllDataComponents;
import io.netty.buffer.ByteBuf;
import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.DataManager;
import net.minecraft.core.Vec3i;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.GZIPInputStream;

public record SelectSchematicPackage(String id, ItemStack item) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CustomPacketPayload.Type<SelectSchematicPackage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "select_schematic"));

    public static final StreamCodec<ByteBuf, SelectSchematicPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SelectSchematicPackage::id,
            ByteBufCodecs.fromCodec(ItemStack.CODEC),
            SelectSchematicPackage::item,
            SelectSchematicPackage::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void serverHandler(SelectSchematicPackage payload, IPayloadContext context) {
        // 找到玩家使用的建造枪
        ServerPlayer player = (ServerPlayer) context.player();
        int build_gun_slot = player.getInventory().findSlotMatchingItem(payload.item());
        if (build_gun_slot == -1) return;
        ItemStack build_gun = player.getInventory().getItem(build_gun_slot);
        // 找到原理图路径
        String schematic = DataManager.findSchematicById(payload.id);
        if (schematic == null) {
            LOGGER.warn("Player {} requested for non-existent schematic: {}", player.getScoreboardName(), payload.id);
            return;
        }
        Path schematicPath = Paths.get("schematics", "uploaded", "__BuildGun__", schematic).toAbsolutePath();
        if (!schematicPath.toFile().exists()) {
            LOGGER.warn("schematic {} doesn't in {}", schematic, schematicPath);
            return;
        }


        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(schematicPath ,StandardOpenOption.READ))))) {
            CompoundTag nbt = NbtIo.read(stream, NbtAccounter.create(0x20000000L));
            ListTag listtag = nbt.getList("size", 3);
            Vec3i size = new Vec3i(listtag.getInt(0), listtag.getInt(1), listtag.getInt(2));
            build_gun.set(AllDataComponents.SCHEMATIC_FILE, schematic);
            build_gun.set(AllDataComponents.SCHEMATIC_BOUNDS, size);
            build_gun.set(AllDataComponents.SCHEMATIC_OWNER, "__BuildGun__");
        } catch (IOException e) {
            LOGGER.warn("Failed to read schematic", e);
        }
    }
}
