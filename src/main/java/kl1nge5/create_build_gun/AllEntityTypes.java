package kl1nge5.create_build_gun;

import com.google.common.collect.ImmutableSet;
import kl1nge5.create_build_gun.entities.BuildingEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AllEntityTypes {
    public static DeferredHolder<EntityType<?>, BuildingEntityType> BUILDING_ENTITY_TYPE =
            BuildGun.ENTITY_TYPE.register("building_entity",
                    () -> new BuildingEntityType(BuildingEntity::new,
                            MobCategory.MISC,
                            true,
                            true,
                            true,
                            true,
                            ImmutableSet.of(),
                            EntityDimensions.scalable(2, 2),
                            1,
                            10,
                            20,
//                            Integer.MAX_VALUE,  // 相当于永远不更新，换言之在客户端看来，这个实体将保持生成时的状态直至被移除
                            FeatureFlagSet.of()
                    )
            );

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(AllEntityTypes::register);
    }  // for loading the class

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BUILDING_ENTITY_TYPE.get(), BuildingEntityRenderer::new);
    }
}

class BuildingEntityRenderer extends EntityRenderer<BuildingEntity> {
    public BuildingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BuildingEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(BuildingEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}

class BuildingEntityType extends EntityType<BuildingEntity> {
    public BuildingEntityType(EntityFactory factory, MobCategory category, boolean serialize, boolean summon, boolean fireImmune, boolean canSpawnFarFromPlayer, ImmutableSet immuneTo, EntityDimensions dimensions, float spawnDimensionsScale, int clientTrackingRange, int updateInterval, FeatureFlagSet requiredFeatures) {
        super(factory, category, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, dimensions, spawnDimensionsScale, clientTrackingRange, updateInterval, requiredFeatures);
    }
}


