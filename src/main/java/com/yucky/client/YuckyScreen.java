package com.yucky.client.gui;

import com.yucky.client.YuckyClient;
import com.yucky.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class YuckyScreen extends Screen {
    private static final Identifier BACKGROUND_1 = Identifier.of("yucky", "textures/gui/background1.jpeg");
    private static final Identifier BACKGROUND_2 = Identifier.of("yucky", "textures/gui/background2.jpeg");
    private int selected = 0;
    private boolean changingMenuKey = false;

    public YuckyScreen() {
        super(Text.literal("yucky"));
    }

    private int panelX() { return Math.max(18, width / 2 - 430); }
    private int panelY() { return Math.max(18, height / 2 - 250); }

    @Override
    public void render(DrawContext g, int mouseX, int mouseY, float delta) {
        int x = panelX(), y = panelY(), w = Math.min(860, width - 36), h = Math.min(500, height - 36);
        drawMenuBackground(g);
        rounded(g, x, y, x + w, y + h, 0xE00C0C10, 16);
        g.drawText(font, Text.literal("yucky"), x + 24, y + 22, 0xFFFFFFFF, false);
        g.drawText(font, Text.literal("client dashboard"), x + 24, y + 40, 0xFF85858F, false);

        int bgButtonX = x + w - 132;
        rounded(g, bgButtonX, y + 18, x + w - 24, y + 48, 0xFF22222A, 10);
        g.drawText(font, Text.literal("Background " + (YuckyClient.CONFIG.menuBackground + 1) + "/2"), bgButtonX + 12, y + 28, 0xFFFFFFFF, false);

        int keyButtonX = x + w - 260;
        rounded(g, keyButtonX, y + 18, bgButtonX - 8, y + 48, changingMenuKey ? 0xFF8A5CF6 : 0xFF22222A, 10);
        g.drawText(font, Text.literal(changingMenuKey ? "Press a key..." : "Menu Key: " + keyName(YuckyClient.CONFIG.menuKeyCode)), keyButtonX + 12, y + 28, 0xFFFFFFFF, false);

        int sidebarW = 190;
        rounded(g, x + 16, y + 68, x + sidebarW, y + h - 16, 0xE0111116, 12);
        String[] cats = {"Render", "World", "Player", "Misc"};
        for (int i = 0; i < cats.length; i++) {
            int cy = y + 92 + i * 42;
            if ((selected < 2 && i == 0) || (selected == 2 && i == 2)) {
                rounded(g, x + 26, cy - 8, x + sidebarW - 10, cy + 24, 0xFF24242D, 9);
            }
            g.drawText(font, Text.literal(cats[i]), x + 42, cy, i == 0 ? 0xFFFFFFFF : 0xFF92929C, false);
        }

        int cx = x + sidebarW + 18, cy = y + 68, cw = w - sidebarW - 34;
        rounded(g, cx, cy, cx + cw, cy + 70, 0xE015151B, 12);
        g.drawText(font, Text.literal("Modules"), cx + 18, cy + 16, 0xFFFFFFFF, false);
        g.drawText(font, Text.literal("Visual and movement modules"), cx + 18, cy + 36, 0xFF777780, false);

        int cardY = cy + 82;
        drawModule(g, cx, cardY, cw, YuckyClient.MODULES.storage(), selected == 0, 0xFF153D2A);
        drawModule(g, cx, cardY + 72, cw, YuckyClient.MODULES.spawner(), selected == 1, 0xFF401C24);
        drawModule(g, cx, cardY + 144, cw, YuckyClient.MODULES.freecam(), selected == 2, 0xFF25204A);
        drawModule(g, cx, cardY + 216, cw, YuckyClient.MODULES.itemView(), selected == 3, 0xFF3D2D18);

        int settingsY = cardY + 280;
        if (selected == 0) drawStorageSettings(g, cx, settingsY, cw);
        else if (selected == 1) drawSpawnerSettings(g, cx, settingsY, cw);
        else if (selected == 2) drawFreecamSettings(g, cx, settingsY, cw);
        else drawItemViewSettings(g, cx, settingsY, cw);

        super.render(g, mouseX, mouseY, delta);
    }

    private void drawModule(DrawContext g, int x, int y, int w, Module m, boolean selected, int enabledBg) {
        rounded(g, x, y, x + w, y + 62, m.enabled() ? enabledBg : 0xE015151B, 12);
        g.drawText(font, Text.literal(m.name()), x + 16, y + 13, 0xFFFFFFFF, false);
        g.drawText(font, Text.literal(m.description()), x + 16, y + 33, 0xFF85858F, false);
        rounded(g, x + w - 72, y + 18, x + w - 22, y + 44, m.enabled() ? 0xFF53D68C : 0xFF303039, 13);
        g.drawText(font, Text.literal(m.enabled() ? "ON" : "OFF"), x + w - 61, y + 26, m.enabled() ? 0xFF06140B : 0xFFB0B0B8, false);
        if (selected) rounded(g, x + 3, y + 3, x + 6, y + 59, 0xFF67D89A, 2);
    }

    private void drawStorageSettings(DrawContext g, int x, int y, int w) {
        settingsHeader(g, x, y, w, "Storage Finder settings");
        int yy = y + 34;
        slider(g, x, yy, w / 2 - 8, "Outline opacity", YuckyClient.CONFIG.outlineOpacity, "90%");
        slider(g, x + w / 2 + 8, yy, w / 2 - 8, "Fill opacity", YuckyClient.CONFIG.fillOpacity, "20%");
    }

    private void drawSpawnerSettings(DrawContext g, int x, int y, int w) {
        settingsHeader(g, x, y, w, "Spawner Finder settings");
        int yy = y + 34;
        slider(g, x, yy, w / 2 - 8, "Outline opacity", YuckyClient.CONFIG.outlineOpacity, "90%");
        slider(g, x + w / 2 + 8, yy, w / 2 - 8, "Fill opacity", YuckyClient.CONFIG.fillOpacity, "20%");
    }

    private void drawFreecamSettings(DrawContext g, int x, int y, int w) {
        settingsHeader(g, x, y, w, "Freecam settings");
        int yy = y + 34;
        float normalized = (float) ((com.yucky.client.freecam.FreecamController.speed() - 0.1) / 9.9);
        slider(g, x, yy, w / 2 - 8, "Speed", normalized, String.format("%.1f", com.yucky.client.freecam.FreecamController.speed()));
        g.drawText(font, Text.literal("WASD: move   Space/Shift: vertical   Ctrl: boost   V: toggle"), x + w / 2 + 8, yy + 2, 0xFFB8B8C2, false);
    }

    private void drawItemViewSettings(DrawContext g, int x, int y, int w) {
        settingsHeader(g, x, y, w, "Item View settings");
        var m = YuckyClient.MODULES.itemView();
        int yy = y + 30;
        g.drawText(font, Text.literal("Main hand"), x + 14, yy, 0xFFFFFFFF, false);
        g.drawText(font, Text.literal(String.format("X %.2f   Y %.2f   Z %.2f   Scale %.2f", m.mainX, m.mainY, m.mainZ, m.mainScale)), x + 90, yy, 0xFFB8B8C2, false);
        g.drawText(font, Text.literal("Off hand"), x + 14, yy + 22, 0xFFFFFFFF, false);
        g.drawText(font, Text.literal(String.format("X %.2f   Y %.2f   Z %.2f   Scale %.2f", m.offX, m.offY, m.offZ, m.offScale)), x + 90, yy + 22, 0xFFB8B8C2, false);
        g.drawText(font, Text.literal("Edit values in ItemViewModule.java for now"), x + 14, yy + 44, 0xFF777780, false);
    }

    private void settingsHeader(DrawContext g, int x, int y, int w, String s) {
        rounded(g, x, y, x + w, y + 88, 0xFF111116, 12);
        g.drawText(font, Text.literal(s), x + 14, y + 12, 0xFFFFFFFF, false);
    }

    private void slider(DrawContext g, int x, int y, int w, String label, float value, String val) {
        g.drawText(font, Text.literal(label), x, y, 0xFFB8B8C2, false);
        g.drawText(font, Text.literal(val), x + w - 28, y, 0xFFFFFFFF, false);
        g.fill(x, y + 16, x + w, y + 18, 0xFF303039);
        g.fill(x, y + 16, x + (int) (w * value), y + 18, 0xFF67D89A);
        int knob = x + (int) (w * value);
        g.fill(knob - 3, y + 13, knob + 3, y + 21, 0xFFFFFFFF);
    }

    private void drawMenuBackground(DrawContext g) {
        Identifier texture = YuckyClient.CONFIG.menuBackground == 0 ? BACKGROUND_1 : BACKGROUND_2;
        g.drawTexture(texture, 0, 0, 0.0f, 0.0f, width, height, width, height);
        g.fill(0, 0, width, height, 0x8A000000);
    }

    private static void rounded(DrawContext g, int x1, int y1, int x2, int y2, int color, int radius) {
        g.fill(x1 + radius, y1, x2 - radius, y2, color);
        g.fill(x1, y1 + radius, x2, y2 - radius, color);
        g.fill(x1 + 2, y1 + 2, x1 + radius, y1 + radius, color);
        g.fill(x2 - radius, y1 + 2, x2 - 2, y1 + radius, color);
        g.fill(x1 + 2, y2 - radius, x1 + radius, y2 - 2, color);
        g.fill(x2 - radius, y2 - radius, x2 - 2, y2 - 2, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = panelX(), y = panelY(), w = Math.min(860, width - 36);
        if (inside(mouseX, mouseY, x + w - 132, y + 18, x + w - 24, y + 48)) {
            YuckyClient.CONFIG.menuBackground = (YuckyClient.CONFIG.menuBackground + 1) % 2;
            return true;
        }
        if (inside(mouseX, mouseY, x + w - 260, y + 18, x + w - 140, y + 48)) {
            changingMenuKey = true;
            return true;
        }

        int start = y + 68 + 82;
        if (inside(mouseX, mouseY, x + 208, start, x + w - 16, start + 62)) {
            YuckyClient.MODULES.storage().toggle(); selected = 0; return true;
        }
        if (inside(mouseX, mouseY, x + 208, start + 72, x + w - 16, start + 134)) {
            YuckyClient.MODULES.spawner().toggle(); selected = 1; return true;
        }
        if (inside(mouseX, mouseY, x + 208, start + 144, x + w - 16, start + 206)) {
            YuckyClient.MODULES.freecam().toggle(); selected = 2; return true;
        }
        if (inside(mouseX, mouseY, x + 208, start + 216, x + w - 16, start + 278)) {
            YuckyClient.MODULES.itemView().toggle(); selected = 3; return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean inside(double mx, double my, int x1, int y1, int x2, int y2) {
        return mx >= x1 && mx <= x2 && my >= y1 && my <= y2;
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (changingMenuKey) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                YuckyClient.CONFIG.menuKeyCode = keyCode;
                YuckyClient.CONFIG.save();
                changingMenuKey = false;
            } else {
                changingMenuKey = false;
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { onClose(); return true; }
        if (keyCode == GLFW.GLFW_KEY_B) {
            YuckyClient.CONFIG.menuBackground = (YuckyClient.CONFIG.menuBackground + 1) % 2;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static String keyName(int keyCode) {
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null && !name.isBlank()) return name.toUpperCase();
        return switch (keyCode) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            default -> "KEY " + keyCode;
        };
    }

    @Override public void onClose() { YuckyClient.CONFIG.save(); minecraft.setScreen(null); }
    @Override public boolean isPauseScreen() { return false; }
}
