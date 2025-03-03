package kl1nge5.create_build_gun.mixin;

import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import kl1nge5.create_build_gun.AllItems;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SchematicPlacePacket.class)
public class CreateSchematicPlacePacketMixin {
    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z"))
    private boolean isCreativeRedirect(ServerPlayer player) {
        return player.getMainHandItem().is(AllItems.BUILD_GUN) || player.isCreative();
    }
}
