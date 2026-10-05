package com.klyvren.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class KlyvrenClient implements ClientModInitializer {
    public static final String MOD_ID = "klyvren";
    private static boolean lastCombo;
    private static boolean zooming;

    @Override
    public void onInitializeClient() {
        KlyvrenFeatures.load();
        KlyvrenHud.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KlyvrenHud.tick(client);

            boolean combo = isComboDown(client);
            if (combo && !lastCombo && client.screen == null) {
                openMenu(client);
            }
            lastCombo = combo;
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                KlyvrenHud.ID,
                KlyvrenHud::render
        );
    }

    private static boolean isComboDown(Minecraft client) {
        if (client.getWindow() == null) return false;
        long window = client.getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
                && GLFW.glfwGetKey(window, GLFW.GLFW_KEY_1) == GLFW.GLFW_PRESS;
    }

    public static boolean isZooming() { return zooming; }

    public static void openMenu(Minecraft client) {
        if (client.screen == null) {
            client.setScreen(new KlyvrenScreen(null));
        }
    }
}
