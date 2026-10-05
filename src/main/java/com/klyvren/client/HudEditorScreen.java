package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private KlyvrenHud.Element dragging;
    private double dragOffX;
    private double dragOffY;
    private EditBox clanBox;

    public HudEditorScreen(Screen parent) { super(Component.literal("Klyvren HUD Editor")); this.parent = parent; }

    @Override protected void init() {
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(width - 94, 12, 78, 24).build());
        clanBox = new EditBox(font, 16, height - 36, 180, 20, Component.literal("Clan name"));
        clanBox.setValue(KlyvrenHud.getClanName());
        clanBox.setMaxLength(24);
        addRenderableWidget(clanBox);
        addRenderableWidget(Button.builder(Component.literal("Save clan"), b -> KlyvrenHud.setClanName(clanBox.getValue())).bounds(202, height - 36, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Reset positions"), b -> reset()).bounds(310, height - 36, 130, 20).build());
    }

    private void reset() {
        int x = 10, y = 10;
        for (KlyvrenHud.Element e : KlyvrenHud.elements()) { e.x = x; e.y = y; x += 14; y += e.height + 8; }
        KlyvrenHud.save();
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, width, height, 0x88000000);
        g.drawString(font, "KLYVREN HUD EDITOR", 16, 16, 0xFFFFFFFF, false);
        g.drawString(font, "Drag modules. They stay where you put them.", 16, 31, 0xFFB9A5FF, false);
        KlyvrenHud.renderEditor(g);
        super.render(g, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        if (event.button() == 0) for (KlyvrenHud.Element e : KlyvrenHud.elements()) {
            if (e.visible && e.contains(event.x(), event.y())) {
                dragging = e; dragOffX = event.x() - e.x; dragOffY = event.y() - e.y; return true;
            }
        }
        return super.mouseClicked(event, doubled);
    }

    @Override public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (event.button() == 0 && dragging != null) {
            dragging.x = clamp((int)(event.x()-dragOffX),0,width-dragging.width);
            dragging.y = clamp((int)(event.y()-dragOffY),0,height-70-dragging.height);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && dragging != null) { dragging=null; KlyvrenHud.save(); return true; }
        return super.mouseReleased(event);
    }

    private static int clamp(int value,int min,int max){return Math.max(min,Math.min(max,value));}
    @Override public void onClose(){KlyvrenHud.save();Minecraft.getInstance().setScreen(parent);}
}
