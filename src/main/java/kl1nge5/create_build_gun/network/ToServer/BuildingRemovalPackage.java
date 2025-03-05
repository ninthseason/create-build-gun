package kl1nge5.create_build_gun.network.ToServer;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.SchematicPrinter;
import io.netty.buffer.ByteBuf;
import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.DataManager;
import kl1nge5.create_build_gun.entities.BuildingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Paths;

public record BuildingRemovalPackage(int entityId) implements CustomPacketPayload {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CustomPacketPayload.Type<BuildingRemovalPackage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "remove_building"));

    public static final StreamCodec<ByteBuf, BuildingRemovalPackage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BuildingRemovalPackage::entityId,
            BuildingRemovalPackage::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void serverHandler(BuildingRemovalPackage payload, IPayloadContext context) {
        int entityId = payload.entityId;
        Level level = context.player().level();
        BuildingEntity entity = (BuildingEntity) level.getEntity(entityId);

        String schematicPath = DataManager.findSchematicById(entity.sid);
        if (schematicPath == null) {
            LOGGER.warn("Schematic not found for entity with sid: {}", entity.sid);
            return;
        }
        File file = Paths.get("schematics", "uploaded", "__BuildGun__", schematicPath).toAbsolutePath().toFile();
        if (!file.exists()) {
            LOGGER.warn("Schematic file {} not found", file);
            return;
        }
        SchematicPrinter printer = new SchematicPrinter();
        ItemStack dummy_blueprint = SchematicItem.create(level, schematicPath, "__BuildGun__");
        dummy_blueprint.set(AllDataComponents.SCHEMATIC_DEPLOYED, true);
        dummy_blueprint.set(AllDataComponents.SCHEMATIC_ANCHOR, entity.anchor);
        dummy_blueprint.set(AllDataComponents.SCHEMATIC_ROTATION, entity.rotation);
        dummy_blueprint.set(AllDataComponents.SCHEMATIC_MIRROR, entity.mirror);

        printer.loadSchematic(dummy_blueprint, level, false);
        if (!printer.isLoaded() || printer.isErrored()) {
            LOGGER.warn("Schematic printer load failed");
            return;
        }

        while (printer.advanceCurrentPos()) {
            printer.handleCurrentTarget((pos, state, blockEntity) -> {
                if (level.getBlockState(printer.getCurrentTarget()) == state) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }, (pos, entity1) -> {});
        }

        entity.remove(Entity.RemovalReason.DISCARDED);

    }
}
