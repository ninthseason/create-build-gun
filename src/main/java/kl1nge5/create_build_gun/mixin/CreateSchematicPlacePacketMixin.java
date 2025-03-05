package kl1nge5.create_build_gun.mixin;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import kl1nge5.create_build_gun.AllItems;
import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.ConfigSpec;
import kl1nge5.create_build_gun.data.DataManager;
import kl1nge5.create_build_gun.data.StageData;
import kl1nge5.create_build_gun.entities.BuildingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SchematicPlacePacket.class)
public class CreateSchematicPlacePacketMixin {
    @Final
    @Shadow
    private ItemStack stack;

    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z"))
    private boolean isCreativeRedirect(ServerPlayer player) {
        return player.getMainHandItem().is(AllItems.BUILD_GUN) || player.isCreative();
    }

    @Inject(method = "handle", at = @At("HEAD"), remap = false, cancellable = true)
    private void takeCostAndCheckStage(ServerPlayer player, CallbackInfo ci) {
        // 只截获使用建筑枪放置方块的情况
        if (player == null || !stack.is(AllItems.BUILD_GUN.asItem())) return;

        Logger LOGGER = LogUtils.getLogger();
        // 如果缺少配置
        String sid = stack.get(kl1nge5.create_build_gun.AllDataComponents.SCHEMATIC_ID);
        ConfigSpec.SchematicEntry.SchematicConfig config = DataManager.getConfigById(sid);
        if (config == null) {
            LOGGER.warn("schematic config is absent: {}", sid);
            ci.cancel();
            return;
        };

        // 检查当前阶段建筑是否解锁，若否则取消放置
        int world_stage = ((StageData) player.server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<SavedData>(StageData::new, StageData::load), BuildGun.MODID)).stage;
        if (world_stage < config.stage) {
            player.sendSystemMessage(Component.translatable("create_build_gun.tips.stage_not_reached"));
            ci.cancel();
            return;
        }

        // 检查玩家背包是否有足够的材料，若否则取消放置，反之消耗材料并放行
        ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry[] cost = config.cost;
        boolean affordable = (cost == null || player.isCreative());
        if (!affordable) {
            boolean all = true;
            for (ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry costEntry : cost) {
                ResourceLocation item_identifier = ResourceLocation.parse(costEntry.id);
                int item_count = ContainerHelper.clearOrCountMatchingItems(player.getInventory(), (stack) -> stack.is(BuiltInRegistries.ITEM.get(item_identifier)), 0, true);
                if (item_count < costEntry.count) {
                    all = false;
                    break;
                }
            }
            if (all) {
                affordable = true;
            }
        }
        if (affordable) {
            for (ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry costEntry : cost) {
                ResourceLocation item_identifier = ResourceLocation.parse(costEntry.id);
                ContainerHelper.clearOrCountMatchingItems(player.getInventory(), (stack) -> stack.is(BuiltInRegistries.ITEM.get(item_identifier)), costEntry.count, false);
            }

            // 放置建筑实体
            if (config.removable) {
                Vec3i vec3i = stack.get(AllDataComponents.SCHEMATIC_BOUNDS);
                BlockPos anchorPos = stack.get(AllDataComponents.SCHEMATIC_ANCHOR);
                Rotation rotation = stack.get(AllDataComponents.SCHEMATIC_ROTATION);
                BlockPos anotherPos;
                if (vec3i != null && anchorPos != null) {
                    switch (rotation) {
                        case CLOCKWISE_90:
                            anotherPos = anchorPos.offset(-vec3i.getZ() + 1, vec3i.getY(), vec3i.getX());
                            anchorPos = anchorPos.offset(1, 0, 0);
                            break;
                        case CLOCKWISE_180:
                            anotherPos = anchorPos.offset(-vec3i.getX() + 1, vec3i.getY(), -vec3i.getZ() + 1);
                            anchorPos = anchorPos.offset(1, 0, 1);
                            break;
                        case COUNTERCLOCKWISE_90:
                            anotherPos = anchorPos.offset(vec3i.getZ(), vec3i.getY(), -vec3i.getX() + 1);
                            anchorPos = anchorPos.offset(0, 0, 1);
                            break;
                        case NONE:
                            anotherPos = anchorPos.offset(vec3i);
                            break;
                        case null:  // wont happen
                            anotherPos = anchorPos;
                            break;
                    }
                    BuildingEntity buildingEntity = new BuildingEntity(player.level(), new AABB(anchorPos.getX(), anchorPos.getY(), anchorPos.getZ(), anotherPos.getX(), anotherPos.getY(), anotherPos.getZ()), stack);
                    player.level().addFreshEntity(buildingEntity);
                }
            }

        } else {
            player.sendSystemMessage(Component.translatable("create_build_gun.tips.cantafford"));
            ci.cancel();
        }
    }
}
