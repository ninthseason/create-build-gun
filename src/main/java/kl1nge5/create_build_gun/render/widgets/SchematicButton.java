package kl1nge5.create_build_gun.render.widgets;

import kl1nge5.create_build_gun.data.ConfigSpec;
import kl1nge5.create_build_gun.render.BuildGunScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class SchematicButton extends TextButton {
    ConfigSpec.SchematicEntry schematicEntry;

    public SchematicButton(Screen parent, ConfigSpec.SchematicEntry schematicEntry, int x, int y, int width, int height, Component message, OnPress onPress) {
        super(parent, x, y, width, height, message, onPress);
        this.schematicEntry = schematicEntry;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isHovered()) {
            if (schematicEntry.config == null) return;
            if (parent instanceof BuildGunScreen) {
                ((BuildGunScreen) parent).current_hover_schematic = this.schematicEntry;
            }
        }
    }
}
