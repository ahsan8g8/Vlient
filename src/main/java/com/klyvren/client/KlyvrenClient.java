package com.klyvren.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class KlyvrenClient implements ClientModInitializer {
    public static final String MOD_ID = "klyvren";
    private static KeyMapping openFallback;
    private static boolean lastCombo;
    private static boolean zooming;

    @Override
    public void onInitializeClient() {
        KlyvrenFeatures.load();
        KlyvrenHud.load();
        openFallback = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.klyvren.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KlyvrenHud.tick(client);
            while (openFallback.consumeClick()) {
                if (!isComboDown(client)) openMenu(client);
            }
            boolean combo = isComboDown(client);
            if (combo && !lastCombo) openMenu(client);
            lastCombo = combo;
        });
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                KlyvrenHud.ID,
                KlyvrenHud::render
        );
    }

    private static boolean isComboDown(Minecraft client) {
        long window = client.getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
                && GLFW.glfwGetKey(window, GLFW.GLFW_KEY_1) == GLFW.GLFW_PRESS;
    }

    public static boolean isZooming() { return zooming; }

    public static void openMenu(Minecraft client) {
        if (!(client.screen instanceof KlyvrenScreen)) {
            client.setScreen(new KlyvrenScreen(client.screen));
        }
    }
}
