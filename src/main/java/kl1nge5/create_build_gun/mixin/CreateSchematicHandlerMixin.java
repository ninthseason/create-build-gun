package kl1nge5.create_build_gun.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import kl1nge5.create_build_gun.AllItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SchematicHandler.class)
abstract class CreateSchematicHandlerMixin {
    @Shadow
    private ItemStack activeSchematicItem;
    @Shadow
    private int activeHotbarSlot;


    @Inject(method = "findBlueprintInHand", at = @At("HEAD"), cancellable = true)
    private void AddCheckIfIsBuildGun(Player player, CallbackInfoReturnable<ItemStack> cir) {
        // make Create think that the Build Gun is a schematic item
        ItemStack stack = player.getMainHandItem();
        if (stack.is(AllItems.BUILD_GUN.asItem()) && stack.has(AllDataComponents.SCHEMATIC_FILE)) {
            activeSchematicItem = stack;
            activeHotbarSlot = player.getInventory().selected;
            cir.setReturnValue(stack);
        }
    }
}
