package kl1nge5.create_build_gun.render;

import kl1nge5.create_build_gun.AllMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class DummyMenu extends AbstractContainerMenu {
    // actually we don't use a menu, but it is required as a part of Screen
    public DummyMenu(int containerId, Inventory playerInv) {
        super(AllMenus.DUMMY_MENU.get(), containerId);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
