package kl1nge5.create_build_gun.items;

import com.simibubi.create.AllDataComponents;
import kl1nge5.create_build_gun.render.DummyMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BuildGunItem extends Item {
    public BuildGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (usedHand == InteractionHand.MAIN_HAND) {
            if (player.isShiftKeyDown()) {
                ItemStack build_gun = player.getItemInHand(usedHand);
                build_gun.remove(AllDataComponents.SCHEMATIC_FILE);
                build_gun.remove(AllDataComponents.SCHEMATIC_DEPLOYED);
                // Lazy:
                // build_gun.remove(AllDataComponents.SCHEMATIC_ANCHOR);
                // build_gun.remove(AllDataComponents.SCHEMATIC_BOUNDS);
                // build_gun.remove(AllDataComponents.SCHEMATIC_OWNER);
            } else if (!level.isClientSide() && player instanceof ServerPlayer) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player_) -> new DummyMenu(containerId, playerInventory),
                        Component.literal("")
                ));
            }
        }
        return super.use(level, player, usedHand);
    }
}
