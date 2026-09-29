package com.yucky.client.config;

public final class YuckyConfig {
    public boolean storageFinder = true;
    public boolean spawnerFinder = false;
    public int chestColor = 0xFF55FF88;
    public int dropperColor = 0xFF55AAFF;
    public int dispenserColor = 0xFFAA55FF;
    public int barrelColor = 0xFFFFAA55;
    public int shulkerColor = 0xFFFF55DD;
    public int spawnerColor = 0xFFFF5555;
    public float outlineOpacity = 0.90f;
    public float fillOpacity = 0.20f;
    public float outlineWidth = 2.0f;
    public int renderDistance = 96;
    public boolean throughWalls = true;
    public Mode mode = Mode.OUTLINE_FILL;
    public int menuBackground = 0;
    public int menuKeyCode = 344; // Right Shift

    public void save() {
        try {
            java.nio.file.Path path = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("yucky.cfg");
            java.nio.file.Files.writeString(path, "menuKeyCode=" + menuKeyCode + "\nmenuBackground=" + menuBackground + "\n");
        } catch (Exception ignored) {}
    }

    public void load() {
        try {
            java.nio.file.Path path = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("yucky.cfg");
            if (!java.nio.file.Files.exists(path)) return;
            for (String line : java.nio.file.Files.readAllLines(path)) {
                if (line.startsWith("menuKeyCode=")) menuKeyCode = Integer.parseInt(line.substring(12).trim());
                if (line.startsWith("menuBackground=")) menuBackground = Integer.parseInt(line.substring(15).trim());
            }
        } catch (Exception ignored) {}
    }

    public enum Mode { OUTLINE, FILL, OUTLINE_FILL }
}
