package com.klyvren.client;

import net.minecraft.client.Minecraft;
import java.util.LinkedHashMap;
import java.util.Map;

/** Central settings for the Klyvren client. All entries are local/client-side. */
public final class KlyvrenFeatures {
    public record Feature(String id, String name, String category, boolean defaultEnabled) {}
    public static final Map<String, Feature> ALL = new LinkedHashMap<>();
    private static final Map<String, Boolean> enabled = new LinkedHashMap<>();

    static {
        add("fps", "FPS Counter", "HUD", true);
        add("cps", "CPS Counter", "HUD", true);
        add("ping", "Ping Counter", "HUD", true);
        add("xyz", "Coordinates", "HUD", true);
        add("facing", "Facing Direction", "HUD", false);
        add("biome", "Biome", "HUD", false);
        add("keystrokes", "Keystrokes", "HUD", true);
        add("armor", "Armor Status", "HUD", false);
        add("effects", "Potion Effects", "HUD", false);
        add("target", "Target Info", "HUD", false);
        add("clan", "Clan Name", "HUD", true);
        add("clock", "Clock", "HUD", false);
        add("server", "Server Info", "HUD", false);
        add("speed", "Movement Speed", "HUD", false);
        add("tps", "Server TPS", "HUD", false);
        add("crosshair", "Custom Crosshair", "Visual", true);
        add("shield", "Shield Status", "PvP", true);
        add("attack", "Attack Indicator", "PvP", false);
        add("fullbright", "Fullbright", "Visual", false);
        add("zoom", "Zoom", "Visual", false);
        add("hitbox", "Hitbox Visualizer", "PvP", false);
        add("targetcolor", "Target Color", "PvP", true);
        add("particles", "All Particles", "Visual", true);
        add("clouds", "Clouds", "Visual", false);
        add("shadows", "Entity Shadows", "Visual", true);
        add("smoothlight", "Smooth Lighting", "Visual", true);
        add("render7", "7 Chunk Render Preset", "Performance", true);
        add("sim5", "5 Chunk Simulation Preset", "Performance", true);
        add("fps60", "60 FPS Cap", "Performance", true);
        add("recording", "Recording Profile", "Recording", false);
        add("flashback", "Flashback Integration", "Recording", true);
        add("modlist", "Installed Mod List", "Mods", true);
        add("hudcolors", "HUD Color Controls", "Settings", true);
        add("autosave", "HUD Auto-Save", "Settings", true);
        add("session", "Session Timer", "HUD", false);
    }

    private KlyvrenFeatures() {}
    private static void add(String id, String name, String category, boolean def) {
        ALL.put(id, new Feature(id, name, category, def));
        enabled.put(id, def);
    }
    public static boolean isEnabled(String id) { return enabled.getOrDefault(id, false); }
    public static void setEnabled(String id, boolean value) {
        if (!enabled.containsKey(id)) return;
        enabled.put(id, value);
        applyVanillaSetting(id, value);
        if (isEnabled("autosave")) KlyvrenHud.save();
    }
    public static void toggle(String id) { setEnabled(id, !isEnabled(id)); }
    public static void load() {
        for (Feature f : ALL.values()) enabled.put(f.id(), f.defaultEnabled());
    }
    private static void applyVanillaSetting(String id, boolean on) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options == null) return;
        if (id.equals("clouds")) mc.options.cloudStatus().set(on ? net.minecraft.client.CloudStatus.FANCY : net.minecraft.client.CloudStatus.OFF);
        if (id.equals("shadows")) mc.options.entityShadows().set(on);
        if (id.equals("smoothlight")) mc.options.ambientOcclusion().set(on);
        if (id.equals("render7") && on) mc.options.renderDistance().set(7);
        if (id.equals("sim5") && on) mc.options.simulationDistance().set(5);
        if (id.equals("fps60") && on) mc.options.framerateLimit().set(60);
    }
}
