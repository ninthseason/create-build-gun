package kl1nge5.create_build_gun.render;

import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.DataManager;
import kl1nge5.create_build_gun.network.ToServer.SelectSchematicPackage;
import kl1nge5.create_build_gun.render.widgets.TextButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;


public class BuildGunScreen extends AbstractContainerScreen<DummyMenu> {
    // 潦草实现，将来大修
    boolean dirty = true;
    public String current_tab = null;
    TabInsertHelper tabInsertHelper;
    TabDisplayHelper tabDisplayHelper;

    public BuildGunScreen(DummyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // prevent rendering of title
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // just implement it but do nothing
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // the size limit of a blit is 256x256, so we need to split the background into two parts
        int start_left_x = (this.width - 384) >> 1;
        int start_right_x = start_left_x + 256;
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg_left.png"), start_left_x, 20, 0, 0, 256, 200);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg_right.png"), start_right_x, 20, 0, 0, 128, 200);
    }

    @Override
    protected void init() {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.dirty) {
            this.dirty = false;
            this.tabInsertHelper = new TabInsertHelper(30, 30, 10, 60);
            this.tabDisplayHelper = new TabDisplayHelper(30, 60, 10, 30, 60, 384);

            this.renderables.clear();
            for (String t : DataManager.schematicMap.keySet()) {
                if (this.current_tab == null) {
                    this.current_tab = t;
                }
                this.addRenderableWidget(tabInsertHelper.nextTab(this, t));
            }
            if (this.current_tab != null) {
                for (String s : DataManager.schematicMap.get(this.current_tab)) {
                    this.addRenderableWidget(tabDisplayHelper.nextEntry(this, s));
                }
            }
        }
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}

class TabInsertHelper {
    public int x;
    public int y;
    public int stride;
    public int offset = 0;
    public int width;

    public TabInsertHelper(int x, int y, int stride, int width) {
        this.x = x;
        this.y = y;
        this.stride = stride;
        this.width = width;
    }

    public TextButton nextTab(Screen parent, String title) {
        Component t = Component.translatable("create_build_gun.gui.tab." + title);
        TextButton textButton = new TextButton(parent, x + offset, y, width, 20, t, (e) -> {
            if (e instanceof TextButton && ((TextButton) e).parent instanceof BuildGunScreen) {
                ((BuildGunScreen) ((TextButton) e).parent).current_tab = title;
                ((BuildGunScreen) ((TextButton) e).parent).dirty = true;
            }
        });
        this.offset += stride + width;
        return textButton;
    }
}

class TabDisplayHelper {
    public int x;
    public int y;
    public int stride_x;
    public int stride_y;
    public int offset_x;
    public int offset_y;
    public int width;
    public int total_width;

    public TabDisplayHelper(int x, int y, int stride_x, int stride_y, int width, int total_width) {
        this.x = x;
        this.y = y;
        this.stride_x = stride_x;
        this.stride_y = stride_y;
        this.width = width;
        this.total_width = total_width;
    }

    public TextButton nextEntry(Screen parent, String title) {
        Component t = Component.translatable("create_build_gun.gui.entry." + title);
        TextButton textButton = new TextButton(parent, x + offset_x, y + offset_y, width, 20, t, (e) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            PacketDistributor.sendToServer(new SelectSchematicPackage(title, player.getMainHandItem().copy()));
            player.closeContainer();
        });
        this.offset_x += stride_x + width;
        if (this.offset_x + this.width > this.total_width) {
            this.offset_x = 0;
            this.offset_y += stride_y;
        }
        return textButton;
    }
}

