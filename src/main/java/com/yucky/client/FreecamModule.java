package com.yucky.client.module;

import com.yucky.client.freecam.FreecamController;

public final class FreecamModule extends Module {
    public FreecamModule() {
        super("Freecam", "Move the camera freely without moving your server-side player", false);
    }

    @Override
    public void onEnable() {
        FreecamController.enable();
    }

    @Override
    public void onDisable() {
        FreecamController.disable();
    }

    @Override
    public void onClientTick() {
        FreecamController.tick();
    }
}
