package kl1nge5.create_build_gun.render.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class DebugWidget extends AbstractWidget {
    public DebugWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width,this.getY() + this.height, 0xFF00FF00);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}


}
