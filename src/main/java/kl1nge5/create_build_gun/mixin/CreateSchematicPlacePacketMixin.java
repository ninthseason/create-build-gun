package kl1nge5.create_build_gun.mixin;

import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import kl1nge5.create_build_gun.AllDataComponents;
import kl1nge5.create_build_gun.AllItems;
import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.ConfigSpec;
import kl1nge5.create_build_gun.data.DataManager;
import kl1nge5.create_build_gun.data.StageData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import org.checkerframework.checker.units.qual.A;
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
        // 排除不必消耗材料的情况
        if (player == null || player.isCreative()) return;
        if (!stack.is(AllItems.BUILD_GUN.asItem())) return;

        // 如果没有配置耗材，则直接放行
        String sid = stack.get(AllDataComponents.SCHEMATIC_ID);
        ConfigSpec.SchematicEntry.SchematicConfig config = DataManager.getConfigById(sid);
        if (config == null) return;

        // 检查当前阶段建筑是否解锁，若否则取消放置
        int world_stage = ((StageData) player.server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<SavedData>(StageData::new, StageData::load), BuildGun.MODID)).stage;
        if (world_stage < config.stage) {
            player.sendSystemMessage(Component.translatable("create_build_gun.tips.stage_not_reached"));
            ci.cancel();
            return;
        }

        // 检查玩家背包是否有足够的材料，若否则取消放置，反之消耗材料并放行
        ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry[] cost = config.cost;
        if (cost == null) return;
        boolean affordable = true;
        for (ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry costEntry : cost) {
            ResourceLocation item_identifier = ResourceLocation.parse(costEntry.id);
            int item_count = ContainerHelper.clearOrCountMatchingItems(player.getInventory(), (stack) -> stack.is(BuiltInRegistries.ITEM.get(item_identifier)), 0, true);
            if (item_count < costEntry.count) {
                affordable = false;
                break;
            }
        }
        if (affordable) {
            for (ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry costEntry : cost) {
                ResourceLocation item_identifier = ResourceLocation.parse(costEntry.id);
                ContainerHelper.clearOrCountMatchingItems(player.getInventory(), (stack) -> stack.is(BuiltInRegistries.ITEM.get(item_identifier)), costEntry.count, false);
            }
        } else {
            player.sendSystemMessage(Component.translatable("create_build_gun.tips.cantafford"));
            ci.cancel();
        }
    }
}
