package com.yucky.client.freecam;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Client-only freecam state and movement controller. */
public final class FreecamController {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static FreecamEntity camera;
    private static Vec3d position;
    private static float yaw;
    private static float pitch;
    private static boolean enabled;

    /** Blocks per client tick. Sprint multiplies this by 2.5. */
    private static double speed = 1.0D;

    private FreecamController() {}

    public static boolean isEnabled() {
        return enabled && camera != null && CLIENT.player != null && CLIENT.world != null;
    }

    public static void enable() {
        ClientPlayerEntity player = CLIENT.player;
        if (player == null || CLIENT.world == null) return;

        enabled = true;
        position = player.getPos().add(0.0D, 0.0D, 0.0D);
        yaw = player.getYaw();
        pitch = player.getPitch();

        camera = new FreecamEntity(CLIENT.world);
        camera.setPosition(position);
        camera.setYaw(yaw);
        camera.setPitch(pitch);
        camera.prevYaw = yaw;
        camera.prevPitch = pitch;

        // The camera entity is deliberately NOT added to ClientWorld.
        // It exists only as a local render/camera target.
        CLIENT.setCameraEntity(camera);

        // Keep the real player completely stationary while freecam is active.
        player.setVelocity(Vec3d.ZERO);
    }

    public static void disable() {
        enabled = false;
        if (CLIENT.player != null) {
            CLIENT.player.setVelocity(Vec3d.ZERO);
            CLIENT.setCameraEntity(CLIENT.player);
        } else {
            CLIENT.setCameraEntity(null);
        }
        camera = null;
        position = null;
    }

    public static void tick() {
        if (!isEnabled()) return;

        ClientPlayerEntity player = CLIENT.player;
        if (player == null) {
            disable();
            return;
        }

        // Freeze the real player's local movement. Because the player never
        // changes position or rotation, no freecam movement is sent to a server.
        player.setVelocity(Vec3d.ZERO);

        double currentSpeed = speed * (CLIENT.options.sprintKey.isPressed() ? 2.5D : 1.0D);
        double vertical = 0.0D;
        if (CLIENT.options.jumpKey.isPressed()) vertical += currentSpeed;
        if (CLIENT.options.sneakKey.isPressed()) vertical -= currentSpeed;

        double forward = 0.0D;
        double strafe = 0.0D;
        if (CLIENT.options.forwardKey.isPressed()) forward += 1.0D;
        if (CLIENT.options.backKey.isPressed()) forward -= 1.0D;
        if (CLIENT.options.leftKey.isPressed()) strafe += 1.0D;
        if (CLIENT.options.rightKey.isPressed()) strafe -= 1.0D;

        Vec3d movement = Vec3d.ZERO;
        if (forward != 0.0D || strafe != 0.0D) {
            double length = Math.sqrt(forward * forward + strafe * strafe);
            forward /= length;
            strafe /= length;

            // Forward/back follows the camera's pitch, like spectator mode.
            float yawRad = yaw * MathHelper.RADIANS_PER_DEGREE;
            float pitchRad = pitch * MathHelper.RADIANS_PER_DEGREE;
            double cosPitch = Math.cos(pitchRad);
            Vec3d forwardVec = new Vec3d(
                    -Math.sin(yawRad) * cosPitch,
                    -Math.sin(pitchRad),
                    Math.cos(yawRad) * cosPitch
            );
            Vec3d rightVec = new Vec3d(Math.cos(yawRad), 0.0D, Math.sin(yawRad));
            movement = forwardVec.multiply(forward).add(rightVec.multiply(strafe)).multiply(currentSpeed);
        }

        movement = movement.add(0.0D, vertical, 0.0D);
        position = position.add(movement);

        camera.setPosition(position);
        camera.setYaw(yaw);
        camera.setPitch(pitch);
        camera.prevYaw = yaw;
        camera.prevPitch = pitch;
        CLIENT.setCameraEntity(camera);
    }

    public static void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        if (!isEnabled()) return;

        // Match Minecraft's normal sensitivity handling closely enough for the
        // camera to feel like vanilla mouse-look, without changing the player.
        double sensitivity = CLIENT.options.getMouseSensitivity().getValue();
        double multiplier = sensitivity * 0.6D + 0.2D;
        double scale = multiplier * multiplier * multiplier * 8.0D;

        yaw += (float) (cursorDeltaX * scale * 0.15D);
        pitch += (float) (cursorDeltaY * scale * 0.15D);
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);

        if (camera != null) {
            camera.setYaw(yaw);
            camera.setPitch(pitch);
            camera.prevYaw = yaw;
            camera.prevPitch = pitch;
        }
    }

    public static double speed() { return speed; }
    public static void setSpeed(double value) { speed = MathHelper.clamp(value, 0.1D, 10.0D); }
}
