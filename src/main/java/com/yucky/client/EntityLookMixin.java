package com.yucky.client.mixin;

import com.yucky.client.freecam.FreecamController;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityLookMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void yucky$freecamLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Object) this instanceof net.minecraft.client.network.ClientPlayerEntity && FreecamController.isEnabled()) {
            FreecamController.changeLookDirection(cursorDeltaX, cursorDeltaY);
            ci.cancel();
        }
    }
}
