package com.klyvren.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.lwjgl.glfw.GLFW;

public final class KlyvrenClient implements ClientModInitializer {
    public static final String MOD_ID="klyvren"; private static boolean lastCombo;
    @Override public void onInitializeClient(){
        KlyvrenFeatures.load();KlyvrenHud.load();
        ClientTickEvents.END_CLIENT_TICK.register(client->{KlyvrenHud.tick(client);
            if(client.screen instanceof TitleScreen) client.setScreen(new KlyvrenTitleScreen());
            boolean combo=isComboDown(client);if(combo&&!lastCombo&&client.screen==null)openMenu(client);lastCombo=combo;});
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT,KlyvrenHud.ID,KlyvrenHud::render);
    }
    private static boolean isComboDown(Minecraft c){if(c.getWindow()==null)return false;long w=c.getWindow().handle();return GLFW.glfwGetKey(w,GLFW.GLFW_KEY_RIGHT_SHIFT)==GLFW.GLFW_PRESS&&GLFW.glfwGetKey(w,GLFW.GLFW_KEY_1)==GLFW.GLFW_PRESS;}
    public static void openMenu(Minecraft c){if(c.screen==null)c.setScreen(new KlyvrenScreen(null));}
}