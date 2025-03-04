package kl1nge5.create_build_gun.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.schematics.SchematicInstances;
import com.simibubi.create.content.schematics.packet.SchematicSyncPacket;
import kl1nge5.create_build_gun.AllItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SchematicSyncPacket.class)
public class CreateSchematicSyncPacketMixin {
    @Final
    @Shadow
    private boolean deployed;
    @Final
    @Shadow
    private BlockPos anchor;
    @Final
    @Shadow
    private Rotation rotation;
    @Final
    @Shadow
    private Mirror mirror;
    @Final
    @Shadow
    private int slot;

    @Inject(method = "handle", at = @At(value = "HEAD"))
    private void allowBuildGunSync(ServerPlayer player, CallbackInfo ci) {
        ItemStack stack;
        if (this.slot == -1) {
            stack = player.getMainHandItem();
        } else {
            stack = player.getInventory().getItem(this.slot);
        }
        if (stack.is(AllItems.BUILD_GUN.asItem())) {
            stack.set(AllDataComponents.SCHEMATIC_DEPLOYED, this.deployed);
            stack.set(AllDataComponents.SCHEMATIC_ANCHOR, this.anchor);
            stack.set(AllDataComponents.SCHEMATIC_ROTATION, this.rotation);
            stack.set(AllDataComponents.SCHEMATIC_MIRROR, this.mirror);
            SchematicInstances.clearHash(stack);
        }
    }
}
