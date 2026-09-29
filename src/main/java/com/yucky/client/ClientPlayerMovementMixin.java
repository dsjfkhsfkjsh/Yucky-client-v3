package com.yucky.client.mixin;

import com.yucky.client.freecam.FreecamController;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMovementMixin {
    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void yucky$freezePlayerDuringFreecam(CallbackInfo ci) {
        if (FreecamController.isEnabled()) {
            ((ClientPlayerEntity) (Object) this).setVelocity(0.0D, 0.0D, 0.0D);
            ci.cancel();
        }
    }
}
