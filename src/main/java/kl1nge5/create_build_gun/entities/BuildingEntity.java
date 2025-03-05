package kl1nge5.create_build_gun.entities;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllDataComponents;
import kl1nge5.create_build_gun.AllEntityTypes;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.slf4j.Logger;

public class BuildingEntity extends Entity implements IEntityWithComplexSpawn {
    private static final Logger LOGGER = LogUtils.getLogger();
    public String sid;
    public BlockPos anchor;
    public Rotation rotation;
    public Mirror mirror;

    public BuildingEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public BuildingEntity(Level level, AABB bb, ItemStack gun) {
        this(AllEntityTypes.BUILDING_ENTITY_TYPE.get(), level);

        this.sid = gun.get(kl1nge5.create_build_gun.AllDataComponents.SCHEMATIC_ID);
        this.anchor = gun.get(AllDataComponents.SCHEMATIC_ANCHOR);
        this.rotation = gun.get(AllDataComponents.SCHEMATIC_ROTATION);
        this.mirror = gun.get(AllDataComponents.SCHEMATIC_MIRROR);

        setBoundingBox(bb);
        resetPositionToBB();
    }

    public void resetPositionToBB() {
        AABB bb = getBoundingBox();
        setPosRaw(bb.getCenter().x, bb.minY, bb.getCenter().z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        Vec3 position = position();
        writeBoundingBox(compound, getBoundingBox().move(position.scale(-1)));
        compound.putString("sid", this.sid);
        compound.putIntArray("anchor", new int[]{this.anchor.getX(), this.anchor.getY(), this.anchor.getZ()});
        compound.putString("rotation", this.rotation.toString());
        compound.putString("mirror", this.mirror.toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        Vec3 position = position();
        setBoundingBox(readBoundingBox(compound).move(position));
        this.sid = compound.getString("sid");
        int[] anchors = compound.getIntArray("anchor");
        this.anchor = new BlockPos(anchors[0], anchors[1], anchors[2]);
        this.rotation = Rotation.valueOf(compound.getString("rotation"));
        this.mirror = Mirror.valueOf(compound.getString("mirror"));
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        CompoundTag compound = new CompoundTag();
        addAdditionalSaveData(compound);
        buffer.writeNbt(compound);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        readAdditionalSaveData(additionalData.readNbt());
    }

    @Override
    public void setPos(double x, double y, double z) {
        // 覆写掉默认的方法
        // 因为该方法会在实体生成和收到更新时被调用，而默认的方法会重置实体的包围盒大小
        // 所以我们让它什么都不做，以保持包围盒的大小不变
    }

    public static void writeBoundingBox(CompoundTag compound, AABB bb) {
        compound.put("From", VecHelper.writeNBT(new Vec3(bb.minX, bb.minY, bb.minZ)));
        compound.put("To", VecHelper.writeNBT(new Vec3(bb.maxX, bb.maxY, bb.maxZ)));
    }

    public static AABB readBoundingBox(CompoundTag compound) {
        Vec3 from = VecHelper.readNBT(compound.getList("From", Tag.TAG_DOUBLE));
        Vec3 to = VecHelper.readNBT(compound.getList("To", Tag.TAG_DOUBLE));
        return new AABB(from, to);
    }
}