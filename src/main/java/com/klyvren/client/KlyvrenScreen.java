package com.klyvren.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public final class KlyvrenScreen extends Screen {
    private final Screen parent;
    private int tab = 0;
    private static final String[] TABS = {"HUD", "PvP", "Visual", "Performance", "Recording", "Mods", "Settings"};
    private List<KlyvrenFeatures.Feature> visible = List.of();

    public KlyvrenScreen(Screen parent) {
        super(Component.literal("Klyvren Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int navX = 18;
        for (int i = 0; i < TABS.length; i++) {
            int idx = i;
            addRenderableWidget(Button.builder(Component.literal(TABS[i]), b -> {
                tab = idx;
                clearAndInit();
            }).bounds(navX, 50, 82, 24).build());
            navX += 86;
        }

        if (tab == 0) {
            addRenderableWidget(Button.builder(Component.literal("HUD Editor"),
                    b -> minecraft.setScreen(new HudEditorScreen(this)))
                    .bounds(18, 92, 110, 24).build());
        }

        visible = new ArrayList<>();
        String category = TABS[tab];
        for (var feature : KlyvrenFeatures.ALL.values()) {
            if (feature.category().equals(category)) visible.add(feature);
        }

        int x = 18, y = 130, col = 0;
        for (var feature : visible) {
            int yy = y;
            addRenderableWidget(Button.builder(label(feature), b -> {
                KlyvrenFeatures.toggle(feature.id());
                clearAndInit();
            }).bounds(x, yy, 220, 24).build());

            col++;
            if (col % 2 == 0) {
                x = 18;
                y += 30;
            } else {
                x = 246;
            }
        }
    }

    private Component label(KlyvrenFeatures.Feature feature) {
        return Component.literal((KlyvrenFeatures.isEnabled(feature.id()) ? "ON  " : "OFF ") + feature.name());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);

        int left = Math.max(10, width / 2 - 390);
        int right = Math.min(width - 10, width / 2 + 390);

        g.fill(left, 14, right, height - 14, 0xF00B0D12);
        g.fill(left, 14, left + 5, height - 14, 0xFF8B5CF6);

        g.drawString(font, "KLYVREN CLIENT", left + 18, 22, 0xFFFFFFFF, false);
        g.drawString(font, "35-feature Fabric client", left + 18, 36, 0xFF9A9EAA, false);
        g.drawString(font, "RIGHT SHIFT + 1", Math.max(left + 250, right - 120), 25, 0xFFB9A5FF, false);

        g.fill(left + 10, 80, right - 10, 82, 0xFF242833);
        g.drawString(font, TABS[tab], left + 18, 94, 0xFFFFFFFF, false);

        if (tab == 0) {
            g.drawString(font,
                    "Open HUD Editor to drag enabled modules and save their positions.",
                    left + 18, height - 32, 0xFFB9A5FF, false);
        }

        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        KlyvrenHud.save();
        Minecraft.getInstance().setScreen(parent);
    }
}
