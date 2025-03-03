package kl1nge5.create_build_gun.mixin;

import com.simibubi.create.content.schematics.client.tools.ToolType;
import kl1nge5.create_build_gun.AllItems;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ToolType.class)
public class CreateToolTypeMixin {
    @ModifyVariable(method = "getTools", at = @At("HEAD"), remap = false, argsOnly = true)
    private static boolean keepAlwaysCreateMode(boolean value) {
        return Minecraft.getInstance().player.getMainHandItem().is(AllItems.BUILD_GUN) || value;
    }
}
