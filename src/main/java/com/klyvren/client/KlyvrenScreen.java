package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;

public final class KlyvrenScreen extends Screen {
    private final Screen parent;
    private int tab = 0;
    private long openedAt;
    private static final String[] TABS = {"HUD", "PvP", "Visual", "Performance", "Recording", "Mods", "Settings"};
    private List<KlyvrenFeatures.Feature> visible = List.of();

    public KlyvrenScreen(Screen parent) {
        super(Component.literal("Klyvren Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        openedAt = System.currentTimeMillis();
        rebuildVisible();
    }

    private void rebuildVisible() {
        visible = new ArrayList<>();
        String category = TABS[tab];
        for (var feature : KlyvrenFeatures.ALL.values()) {
            if (feature.category().equals(category)) visible.add(feature);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        long age = System.currentTimeMillis() - openedAt;
        float t = Math.min(1f, age / 220f);
        float ease = 1f - (1f - t) * (1f - t);

        // Lightweight black overlay. No blur, shaders, textures, or extra rendering.
        g.fill(0, 0, width, height, 0xE9000000);

        int panelW = Math.min(940, width - 24);
        int panelH = Math.min(620, height - 24);
        int targetX = (width - panelW) / 2;
        int targetY = (height - panelH) / 2;
        int y = height + 20 - (int)((panelH + 20) * ease);

        g.fill(targetX, y, targetX + panelW, y + panelH, 0xFF090A0D);
        g.fill(targetX, y, targetX + 3, y + panelH, 0xFF8B5CF6);

        int sidebar = 170;
        g.fill(targetX + 3, y, targetX + sidebar, y + panelH, 0xFF0D0F13);
        g.drawString(font, "KLYVREN", targetX + 22, y + 22, 0xFFFFFFFF, false);
        g.drawString(font, "CLIENT", targetX + 22, y + 38, 0xFF8F96A3, false);
        g.drawString(font, "Klyvren Studios", targetX + 22, y + panelH - 24, 0xFF606774, false);

        for (int i = 0; i < TABS.length; i++) {
            int ty = y + 72 + i * 34;
            boolean selected = i == tab;
            g.fill(targetX + 12, ty - 4, targetX + sidebar - 12, ty + 22,
                    selected ? 0xFF211735 : 0x00000000);
            g.drawString(font, TABS[i], targetX + 26, ty + 3,
                    selected ? 0xFFFFFFFF : 0xFF8D939E, false);
        }

        int contentX = targetX + sidebar + 28;
        g.drawString(font, TABS[tab], contentX, y + 25, 0xFFFFFFFF, false);
        g.drawString(font, "Lightweight settings • no shader effects", contentX, y + 42, 0xFF737A86, false);

        if (tab == 0) {
            drawButton(g, contentX, y + 66, 116, 28, "HUD EDITOR", mouseX, mouseY);
        }

        int startY = y + 108;
        int rowH = 42;
        int colW = Math.min(285, (panelW - sidebar - 68) / 2);

        for (int i = 0; i < visible.size(); i++) {
            var feature = visible.get(i);
            int col = i % 2;
            int row = i / 2;
            int bx = contentX + col * (colW + 14);
            int by = startY + row * rowH;
            if (by + 34 > y + panelH - 18) continue;

            boolean enabled = KlyvrenFeatures.isEnabled(feature.id());
            boolean hover = mouseX >= bx && mouseX <= bx + colW
                    && mouseY >= by && mouseY <= by + 32;

            g.fill(bx, by, bx + colW, by + 32, hover ? 0xFF171A20 : 0xFF111318);
            g.drawString(font, feature.name(), bx + 12, by + 6, 0xFFE6E8EC, false);

            int sx = bx + colW - 54;
            int sy = by + 7;
            g.fill(sx, sy, sx + 40, sy + 18, enabled ? 0xFF5F3FA8 : 0xFF30343B);
            g.fill(enabled ? sx + 24 : sx + 4, sy + 3,
                    enabled ? sx + 36 : sx + 16, sy + 15, 0xFFFFFFFF);
        }

        if (age < 240 && minecraft != null) minecraft.setScreen(this);
    }

    private void drawButton(GuiGraphics g, int x, int y, int w, int h, String text, int mx, int my) {
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        g.fill(x, y, x + w, y + h, hover ? 0xFF2A2040 : 0xFF1A1722);
        g.drawString(font, text, x + 12, y + 9, 0xFFD8C7FF, false);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubled) {
        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.mouseClicked(event, doubled);

        int panelW = Math.min(940, width - 24);
        int panelH = Math.min(620, height - 24);
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;
        if (System.currentTimeMillis() - openedAt < 230) return true;

        int sidebar = 170;
        for (int i = 0; i < TABS.length; i++) {
            int ty = py + 72 + i * 34;
            if (event.x() >= px + 12 && event.x() <= px + sidebar - 12
                    && event.y() >= ty - 4 && event.y() <= ty + 22) {
                tab = i;
                rebuildVisible();
                return true;
            }
        }

        int contentX = px + sidebar + 28;
        if (tab == 0 && event.x() >= contentX && event.x() <= contentX + 116
                && event.y() >= py + 66 && event.y() <= py + 94) {
            minecraft.setScreen(new HudEditorScreen(this));
            return true;
        }

        int startY = py + 108;
        int colW = Math.min(285, (panelW - sidebar - 68) / 2);
        for (int i = 0; i < visible.size(); i++) {
            var feature = visible.get(i);
            int col = i % 2;
            int row = i / 2;
            int bx = contentX + col * (colW + 14);
            int by = startY + row * 42;
            if (event.x() >= bx && event.x() <= bx + colW
                    && event.y() >= by && event.y() <= by + 32) {
                KlyvrenFeatures.toggle(feature.id());
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        KlyvrenHud.save();
        Minecraft.getInstance().setScreen(parent);
    }
}
