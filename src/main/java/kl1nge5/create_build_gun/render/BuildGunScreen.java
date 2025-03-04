package kl1nge5.create_build_gun.render;

import kl1nge5.create_build_gun.BuildGun;
import kl1nge5.create_build_gun.data.CachedClientStage;
import kl1nge5.create_build_gun.data.ConfigSpec;
import kl1nge5.create_build_gun.data.DataManager;
import kl1nge5.create_build_gun.network.ToServer.RequestStagePackage;
import kl1nge5.create_build_gun.network.ToServer.SelectSchematicPackage;
import kl1nge5.create_build_gun.render.widgets.SchematicButton;
import kl1nge5.create_build_gun.render.widgets.TextButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Vector;


public class BuildGunScreen extends AbstractContainerScreen<DummyMenu> {
    public String current_tab = null;
    int ideal_width;
    int ideal_height;
    // 实际可用的窗口边界
    int top;
    int bottom;
    int left;
    int right;
    // 扩大一圈的窗口边界(因为窗口有边框)
    int ttop;
    int bbottom;
    int lleft;
    int rright;
    // 理想的切分tab和schematic的位置
    int ideal_split;
    // 理想的按钮宽高
    int ideal_button_width;
    int ideal_button_height;
    // 当前鼠标悬停的schematic, 该值将由SchematicButton在被调用渲染时更新
    public ConfigSpec.SchematicEntry current_hover_schematic;
    // 理想的切分schematic和details的位置
    int ideal_split2;
    // 同步stage数据的剩余时间
    int time_to_sync_stage = 10;

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
        // 绘制窗口四角
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), lleft, ttop, 0, 0, 64, 64);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), lleft, bbottom - 64, 0, 128, 64, 64);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), rright - 64, ttop, 128, 0, 64, 64);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), rright - 64, bbottom - 64, 128, 128, 64, 64);
        // 绘制窗口边框
        // 上下
        int x_offset = 64;
        while (lleft + x_offset < rright - 64) {
            guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), lleft + x_offset, ttop, 64, 0, 64, 64);
            guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), lleft + x_offset, bbottom - 64, 64, 128, 64, 64);
            x_offset += 64;
        }
        // 左右
        int y_offset = 64;
        while (ttop + y_offset < bbottom - 64) {
            guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), lleft, ttop + y_offset, 0, 64, 64, 64);
            guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), rright - 64, ttop + y_offset, 128, 64, 64, 64);
            y_offset += 64;
        }
        // 绘制窗口中间部分
        x_offset = 64;
        y_offset = 64;
        while (ttop + y_offset < bbottom - 64) {
            while (lleft + x_offset < rright - 64) {
                guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(BuildGun.MODID, "textures/gui/simple_bg.png"), lleft + x_offset, ttop + y_offset, 64, 64, 64, 64);
                x_offset += 64;
            }
            x_offset = 64;
            y_offset += 64;
        }
        // 绘制切分线
        guiGraphics.fill(ideal_split, ttop, ideal_split + 1, bbottom, 0xFF000000);
        guiGraphics.fill(ideal_split2, ttop, ideal_split2 + 1, bbottom, 0xFF000000);
    }

    @Override
    protected void init() {
        // 背景相关
        // 理想宽高是屏幕宽高的80%
        this.ideal_width = (this.width / 10) << 3;
        this.ideal_height = (this.height / 10) << 3;
        // 故我们可以得出窗口的上/下/左/右边界坐标
        this.top = (this.height - ideal_height) >> 1;
        this.bottom = (this.height + ideal_height) >> 1;
        this.left = (this.width - ideal_width) >> 1;
        this.right = (this.width + ideal_width) >> 1;
        this.ttop = this.top - 4;
        this.bbottom = this.bottom + 4;
        this.lleft = this.left - 4;
        this.rright = this.right + 4;
        // 理想切分位置在靠左1/5处
        this.ideal_split = this.left + (this.right - this.left) / 5;
        // 理想tab按钮的高为窗口高度的1/8, 宽为左边框到切分线的距离(需要留出4像素的边框)
        this.ideal_button_height = ideal_height >> 3;
        this.ideal_button_width = ideal_split - left - 4;
        // 理想切分schematic和details的位置在靠左3/5处
        this.ideal_split2 = this.left + (this.right - this.left) * 3 / 5;

        // 需要在此添加所有组件，因为窗口大小在变动时会自动清除所有组件并调用该方法
        // 添加tab按钮
        if (DataManager.config != null && DataManager.config.tabs != null) {
            TabInsertHelper tabInsertHelper = new TabInsertHelper(left + 1, top + 1, ideal_button_height, ideal_button_width);
            for (ConfigSpec.TabEntry tab : DataManager.config.tabs) {
                if (current_tab == null) {
                    current_tab = tab.id;
                }
                this.addRenderableWidget(tabInsertHelper.nextTab(this, tab));
            }
        }
        // 添加schematic按钮
        if (DataManager.config != null && DataManager.config.schematics != null) {
            TabDisplayHelper tabDisplayHelper = new TabDisplayHelper(ideal_split + 4, top + 1, ideal_button_width, ideal_button_height, ideal_split2 - ideal_split);
            PacketDistributor.sendToServer(new RequestStagePackage());
            for (ConfigSpec.SchematicEntry schematic : DataManager.config.schematics) {
                if (schematic.tab.equals(current_tab) && schematic.config.stage <= CachedClientStage.stage) {
                    this.addRenderableWidget(tabDisplayHelper.nextEntry(this, schematic));
                }
            }
        }
    }

    public void renderSchematicTips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (current_hover_schematic == null || current_hover_schematic.config == null) return;
        int x = this.ideal_split2 + 4;
        int y = this.top + 4;
        int offset = 0;
        if (current_hover_schematic.config.description != null && !current_hover_schematic.config.description.isEmpty()) {
            for (String s : multilineStringSplit(guiGraphics, current_hover_schematic.config.description, right - ideal_split2 - 12)) {
                guiGraphics.drawString(this.font, s, x, y + offset, 0xFFFFFF);
                offset += 10;
            }
        }
        offset += 10;
        guiGraphics.drawString(this.font, Component.translatable("create_build_gun.gui.description.materials_cost"), x, y + offset, 0xFFFFFF);
        offset += 20;
        if (current_hover_schematic.config.cost != null && current_hover_schematic.config.cost.length > 0) {
            for (ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry costEntry : current_hover_schematic.config.cost) {
                ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(costEntry.id)));
                guiGraphics.renderItem(itemStack, x, y + offset);
                guiGraphics.drawString(this.font, "x" + costEntry.count, this.ideal_split2 + 20, this.top + 10 + offset, 0xFFFFFF);
                offset += 20; // 物品图标的大小是16x16，我们稍微大一点
            }
        } else {
            guiGraphics.drawString(this.font, Component.translatable("create_build_gun.gui.description.no_materials_need"), x, y + offset, 0xFFFFFF);
        }
    }

    public Vector<String> multilineStringSplit(GuiGraphics guiGraphics, String input, int width) {
        // 由于直接渲染文字长度可能超出窗口，我们需要不断试探，将文字分割成多行
        int a = 0;
        int b = 1;
        Vector<String> result = new Vector<>();
        String subString = input.substring(a, b);
        while (!subString.isEmpty()) {
            while (guiGraphics.drawString(this.font, subString, 0, -100, 0) < width && b < input.length()) {
                b++;
                subString = input.substring(a, b);
            }
            if (b == input.length()) {
                result.add(input.substring(a, b));
                break;
            }
            result.add(input.substring(a, b));
            a = b;
            b = a + 1;
            subString = input.substring(a, b);
        }
        return result;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 每10个时间单位向服务器请求同步一次stage数据
        if (time_to_sync_stage > 0) {
            time_to_sync_stage--;
            if (time_to_sync_stage <= 0) {
                PacketDistributor.sendToServer(new RequestStagePackage());
                this.rebuildWidgets();
                time_to_sync_stage = 10;
            }
        }
        this.current_hover_schematic = null;
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        this.renderSchematicTips(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void rebuildWidgets() {
        super.rebuildWidgets();
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

    public TextButton nextTab(Screen parent, ConfigSpec.TabEntry tab) {
        Component t = Component.translatable(tab.name);
        TextButton textButton = new TextButton(parent, x, y + offset, width, stride * 95 / 100, t, (e) -> {
            if (e instanceof TextButton && ((TextButton) e).parent instanceof BuildGunScreen) {
                ((BuildGunScreen) ((TextButton) e).parent).current_tab = tab.id;
                ((BuildGunScreen) ((TextButton) e).parent).rebuildWidgets();
            }
        });
        this.offset += stride;
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
    public int total_width;
    public int width;
    public int height;

    public TabDisplayHelper(int x, int y, int stride_x, int stride_y, int total_width) {
        this.x = x;
        this.y = y;
        this.stride_x = stride_x;
        this.stride_y = stride_y;
        this.total_width = total_width;
        this.width = stride_x * 95 / 100;
        this.height = stride_y * 95 / 100;
    }

    public TextButton nextEntry(Screen parent, ConfigSpec.SchematicEntry schematic) {
        TextButton textButton = new SchematicButton(parent, schematic, x + offset_x, y + offset_y, width, height, Component.translatable(schematic.name), (e) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            PacketDistributor.sendToServer(new SelectSchematicPackage(schematic.id, player.getMainHandItem().copy()));
            player.closeContainer();
        });

        this.offset_x += stride_x;
        if (this.offset_x + this.width > this.total_width) {
            this.offset_x = 0;
            this.offset_y += stride_y;
        }
        return textButton;
    }
}

