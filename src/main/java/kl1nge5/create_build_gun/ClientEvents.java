package kl1nge5.create_build_gun;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.track.CurvedTrackInteraction;
import com.simibubi.create.foundation.utility.RaycastHelper;
import kl1nge5.create_build_gun.entities.BuildingEntity;
import kl1nge5.create_build_gun.network.ToServer.BuildingRemovalPackage;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static BuildingEntity selected;

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        Minecraft MinecraftClient = Minecraft.getInstance();
        if (MinecraftClient.level == null || MinecraftClient.player == null) return;
        LocalPlayer player = MinecraftClient.player;
        ItemStack offhandItem = player.getOffhandItem();
        if (offhandItem.getItem() == AllItems.BUILD_GUN.get()) {
            // 扫描玩家周围的区域
            AABB scanArea = player.getBoundingBox().inflate(32, 16, 32);
            List<BuildingEntity> BuildingsNearby = MinecraftClient.level.getEntitiesOfClass(BuildingEntity.class, scanArea);

            double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1;
            Vec3 traceOrigin = RaycastHelper.getTraceOrigin(player);
            Vec3 traceTarget = RaycastHelper.getTraceTarget(player, range, traceOrigin);

            double bestDistance = Double.MAX_VALUE;
            selected = null;
            // 寻找与玩家视线在距离内相交的最近的建筑
            for (BuildingEntity buildingEntity : BuildingsNearby) {
                Optional<Vec3> clip = buildingEntity.getBoundingBox().clip(traceOrigin, traceTarget);
                if (clip.isEmpty()) continue;
                Vec3 vec3 = clip.get();
                double distanceToSqr = vec3.distanceToSqr(traceOrigin);
                if (distanceToSqr > bestDistance) continue;
                selected = buildingEntity;
                bestDistance = distanceToSqr;
            }

            for (BuildingEntity buildingEntity : BuildingsNearby) {
                // 对于每个建筑，如果它是选中的建筑，则高亮显示，否则显示为被动颜色
                boolean highlight = buildingEntity == selected;
                AllSpecialTextures faceTex = highlight ? AllSpecialTextures.GLUE : null;
                Outliner.getInstance().showAABB(buildingEntity, buildingEntity.getBoundingBox())
                        .colored(highlight ? 0xe62100 : 0x991600)
                        .withFaceTextures(faceTex, faceTex)
                        .disableLineNormals()
                        .lineWidth(highlight ? 1 / 16f : 1 / 64f);
            }
        }
    }
    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.screen != null || player == null) return;

        KeyMapping key = event.getKeyMapping();
        if (key == mc.options.keyUse) {
            if (!player.getOffhandItem().is(AllItems.BUILD_GUN.get())) return;
            if (selected == null) return;
            PacketDistributor.sendToServer(new BuildingRemovalPackage(selected.getId()));
            event.setCanceled(true);
        }
    }
}
