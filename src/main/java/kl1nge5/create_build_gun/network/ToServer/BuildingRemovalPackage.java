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
import java.util.Vector;

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
        if (!(level.getEntity(entityId) instanceof BuildingEntity)) {
            LOGGER.warn("Entity {} requested by Player {} is not a BuildingEntity", entityId, context.player().getScoreboardName());
            return;
        };
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

        printer.loadSchematic(dummy_blueprint, level, !context.player().canUseGameMasterBlocks());
        if (!printer.isLoaded() || printer.isErrored()) {
            LOGGER.warn("Schematic printer load failed");
            return;
        }
        entity.remove(Entity.RemovalReason.DISCARDED);
        Vector<BlockPos> blocksToRemove = new Vector<>();
        while (printer.advanceCurrentPos()) {
            printer.handleCurrentTarget((pos, state, blockEntity) -> {
                if (level.getBlockState(printer.getCurrentTarget()).getBlock() == state.getBlock()) {
                    blocksToRemove.add(printer.getCurrentTarget());
                }
            }, (pos, entity1) -> {});
        }
        //
        for (BlockPos blockPos : blocksToRemove) {
            // 这几个 flags 可以防止方块拆除时有额外的掉落物（比如机械动力的伪装半砖），也能防止依附的方块因失去依附而掉落
            // 因此会导致不属于原理图的(后来放上的)依附方块悬浮在空中
            level.setBlock(blockPos, Blocks.BARRIER.defaultBlockState(), 2 | 16 | 32 | 64);
        }
        for (BlockPos blockPos : blocksToRemove) {
            // 为了解决上面的问题，可以重新触发一次更新，但这也带来了额外的性能开销，尤其是原理图过大时
            // 如果这真的引发了很大的问题，之后考虑给原理图设置一个配置项，可以由整合包开发者决定该原理图需不需要这样善后
            // 如果不需要善后，则不执行这一步，并且上一步设置为直接替换为空气
            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
        }
    }
}
