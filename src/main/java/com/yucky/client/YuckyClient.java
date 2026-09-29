package com.yucky.client;

import com.yucky.client.config.YuckyConfig;
import com.yucky.client.gui.YuckyScreen;
import com.yucky.client.render.HighlightRenderer;
import com.yucky.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class YuckyClient implements ClientModInitializer {
    public static final String MOD_ID = "yucky";
    public static final YuckyConfig CONFIG = new YuckyConfig();
    public static final ModuleManager MODULES = new ModuleManager();
    private static KeyBinding freecamKey;
    private static boolean menuKeyWasDown;

    @Override
    public void onInitializeClient() {
        CONFIG.load();
        MODULES.registerDefaults();
        HighlightRenderer.register();
        freecamKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.yucky.freecam", GLFW.GLFW_KEY_V, "category.yucky"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long handle = client.getWindow().getHandle();
            boolean down = GLFW.glfwGetKey(handle, CONFIG.menuKeyCode) == GLFW.GLFW_PRESS;
            if (down && !menuKeyWasDown && client.currentScreen == null) client.setScreen(new YuckyScreen());
            menuKeyWasDown = down;
            while (freecamKey.wasPressed()) MODULES.freecam().toggle();
            MODULES.tick();
        });
    }

    public static MinecraftClient client() { return MinecraftClient.getInstance(); }
}
