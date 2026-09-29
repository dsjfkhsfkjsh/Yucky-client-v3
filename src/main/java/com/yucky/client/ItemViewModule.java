package com.yucky.client.module;

import net.minecraft.util.Hand;

/** Client-side first-person held-item transform settings. */
public final class ItemViewModule extends Module {
    public float mainX = 0.0f;
    public float mainY = 0.0f;
    public float mainZ = 0.0f;
    public float mainScale = 1.0f;
    public float mainRotX = 0.0f;
    public float mainRotY = 0.0f;
    public float mainRotZ = 0.0f;

    public float offX = 0.0f;
    public float offY = 0.0f;
    public float offZ = 0.0f;
    public float offScale = 1.0f;
    public float offRotX = 0.0f;
    public float offRotY = 0.0f;
    public float offRotZ = 0.0f;

    public ItemViewModule() {
        super("Item View", "Change the position, scale, and rotation of held items", false);
    }

    public float x(Hand hand) { return hand == Hand.MAIN_HAND ? mainX : offX; }
    public float y(Hand hand) { return hand == Hand.MAIN_HAND ? mainY : offY; }
    public float z(Hand hand) { return hand == Hand.MAIN_HAND ? mainZ : offZ; }
    public float scale(Hand hand) { return hand == Hand.MAIN_HAND ? mainScale : offScale; }
    public float rotX(Hand hand) { return hand == Hand.MAIN_HAND ? mainRotX : offRotX; }
    public float rotY(Hand hand) { return hand == Hand.MAIN_HAND ? mainRotY : offRotY; }
    public float rotZ(Hand hand) { return hand == Hand.MAIN_HAND ? mainRotZ : offRotZ; }
}
